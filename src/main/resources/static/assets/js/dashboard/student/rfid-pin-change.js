function rfidPinValidation(element, compareElement, isNewPin) {
	const field = typeof element === 'string' ? $('#' + element) : $('#' + element.id);
	const comparePin = compareElement ? (typeof compareElement === 'string' ? $('#' + compareElement) : $('#' + compareElement.id)) : null;
	let errorDiv = field.siblings('.invalid-feedback');

	if (field.val()) {
		if (field.val().length !== 4) {
			field.removeClass(validClass).addClass(errorClass);
			errorDiv.html(errorDiv.siblings('.invalid-pin-length-feedback').html());
			return false;
		}
		if (comparePin && comparePin.val()) {
			if ((isNewPin && field.val() === comparePin.val()) || (!isNewPin && field.val() !== comparePin.val())) {
				field.removeClass(validClass).addClass(errorClass);
				errorDiv.html(errorDiv.siblings('.invalid-mismatch-feedback').html());
				return false;
			}
		}
		field.removeClass(errorClass).addClass(validClass);
		errorDiv.html('');
		return true;
	} else {
		field.removeClass(validClass).addClass(errorClass);
		errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
		return false;
	}
}

function validateCurrentPin() {
	return new Promise((resolve) => {
		fetch(contextPath + rfidURL + currentRfidPinURL)
			.then((response) => response.text())
			.then((serverPin) => {
				const currentPin = $('#currentPin');
				if (currentPin.val().trim()) {
					if (serverPin && rfidPinValidation('currentPin')) {
						if (serverPin === currentPin.val()) {
							currentPin.removeClass(errorClass).addClass(validClass);
							resolve(true);
						} else {
							const errorDiv = currentPin.siblings('.invalid-feedback');
							currentPin.removeClass(validClass).addClass(errorClass);
							errorDiv.html(errorDiv.siblings('.invalid-mismatch-feedback').html());
							resolve(false);
						}
					} else {
						resolve(false);
					}
				} else {
					resolve(true);
				}
			})
			.catch((error) => {
				console.error("Fetch error:", error);
				resolve(false);
			});
	});
}

function validateRFIDPinChange() {
	$.ajax({
		url: contextPath + rfidURL + rfidByIdURL,
		type: "GET",
		success: function (response) {
			$('#modalDiv').html(response);
			$('#rfid-pin-change-modal').modal('show');
			const accessPinNo = $('#accessPinNo');
			updateSaveButtonStyle(accessPinNo.val(), $('#rfid-pin-change-modalSave'));

			$('#rfid-pin-change-modalSave').off("click").on("click", function (e) {
				e.preventDefault();
				const saveButton = $(this);
				saveButton.attr('disabled', true);

				validateCurrentPin().then((isValid) => {
					let isNewPinValid = true;
					let isRetypePinValid = true;
					const currentPin = $('#currentPin').val();
					const cardStatus = $('#cardStatus').is(":checked");
					if (currentPin) {
						isNewPinValid = rfidPinValidation('newPin', 'currentPin', true);
						isRetypePinValid = rfidPinValidation('retypePin', 'newPin', false);

						if (isValid && isNewPinValid && isRetypePinValid) {
							saveRfidPin(currentPin, $('#newPin').val(), $('#retypePin').val(), cardStatus, saveButton);
						} else {
							saveButton.attr('disabled', false);
						}
					} else {
						saveRfidPin(null, null, null, cardStatus, saveButton);
					}

				});
			});

			$("#rfid-pin-change-modalValidateBackend").off("click").on("click", function (e) {
				e.preventDefault();
				const saveButton = $(this);
				saveButton.attr('disabled', true);

				const currentPin = $('#currentPin');
				const newPin = $('#newPin');
				const retypePin = $('#retypePin');
				const cardStatus = $('#cardStatus').is(':checked');

				saveRfidPin(currentPin.val(), newPin.val(), retypePin.val(), cardStatus, saveButton);
			});
		}
	});
}


function saveRfidPin(currentPin, pinNo, retypePinNo, cardActiveStatus, saveButton) {
	const saveUrl = contextPath + rfidURL + rfidSaveURL;

	$.ajax({
		url: saveUrl,
		type: "POST",
		data: {
			currentPin: currentPin !== null ? currentPin : null,
			pinNo: pinNo !== null ? pinNo : null,
			retypePinNo: retypePinNo !== null ? retypePinNo : null,
			cardActiveStatus: cardActiveStatus ? 'N' : 'Y'
		},
		success: function(response) {
			$('#rfid-pin-change-modal').modal('hide');
			const parentDiv = $('#rfidPinChangeId').closest('.widget-child');
			const url = parentDiv.attr('data-url');
			const widgetId = parentDiv.attr('data-id');
			loadWidget(url, widgetId);

			if (response && response.status === "Success") {
				showToast('Success', 'RFID Data updated successfully');
			} else {
				showToast('Error', 'Error occurred');
			}
		},
		error: function(xhr) {
			saveButton.attr('disabled', false);
			if (xhr.responseJSON && xhr.responseJSON.errors && xhr.responseJSON.code === 400) {
				showToast(xhr.responseJSON.status, xhr.responseJSON.errors);
			} else {
				showToast('Error', 'Error occurred');
			}
		}
	});
}