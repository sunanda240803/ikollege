function validateRollNumberChange() {
	$.ajax({
		url: contextPath + rollnoURL + rollnoPopupURL,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#roll-number-change-modal').modal('show');

			$('#roll-number-change-modalSave').off("click").on("click", function(e) {
				e.preventDefault();
				const saveButton = $(this);
				saveButton.attr('disabled', true);

				const fileInfo = $('#uploadFile_fileInfo');
				const fileError = $('#uploadFile_fileError');
				const fileElement = $('#uploadFile_fileUpload')[0];

				let fileStatus = false;

				// Validate the file information
				if (fileInfo.length && fileInfo.text().trim()) {
					fileStatus = true;
					fileError.text('');
				} else {
					fileStatus = false;
					fileError.text('Proof of Roll Number change file is required');
				}

				validateStudentId().then(isValid => {
					if (!isValid) {
						saveButton.attr('disabled', false);
						return false;
					}
					if (isValid && fileStatus) {
						const newRollNo = $('#studentId').val();
						saveRollNumberChange(newRollNo, fileElement, saveButton);
					} else {
						saveButton.attr('disabled', false);
					}
				}).catch(() => {
					saveButton.attr('disabled', false);
				});
			});

			$("#roll-number-change-modalValidateBackend").off("click").on("click", function(e) {
				e.preventDefault();
				const saveButton = $(this);
				saveButton.attr('disabled', true);
				const newRollNo = $('#studentId').val();
				const fileElement = $('#uploadFile_fileUpload')[0];
				saveRollNumberChange(newRollNo, fileElement, saveButton);
			})

		}
	});
}

function saveRollNumberChange(newRollNo, fileElement, saveButton) {
	const saveUrl = contextPath + rollnoURL + rollnoSaveURL;
	const formData = new FormData();
	formData.append('newRollNo', newRollNo);

	if (fileElement && fileElement.files.length > 0) {
		formData.append('file', fileElement.files[0]);
	}

	$.ajax({
		url: saveUrl,
		type: "POST",
		data: formData,
		processData: false,
		contentType: false,
		success: function(response) {
			$('#roll-number-change-modal').modal('hide');
			const parentDiv = $('#rollNumberChangeId').closest('.widget-child');
			const url = parentDiv.attr('data-url');
			const widgetId = parentDiv.attr('data-id');
			loadWidget(url, widgetId);

			if (response && response.status === "Success") {
				showToast('Success', 'Roll Number saved successfully');
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

function validateStudentId() {
	return new Promise((resolve) => {
		const studentId = $('#studentId');
		studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
		let errorDiv = studentId.siblings('.invalid-feedback');
		errorDiv.html(studentId.siblings('.invalid-value-feedback').html());

		if (!studentId.val()) {
			studentId.addClass("is-invalid");
			errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
			resolve(false);
			return;
		}

		if (studentId.val().length < 6) {
			studentId.addClass("is-invalid");
			errorDiv.html(studentId.siblings('.invalid-min-feedback').html());
			resolve(false);
			return;
		}
		if (!(numeric.test(studentId.val()) && alphabetic.test(studentId.val()))) {
			studentId.addClass("is-invalid")
			errorDiv.html(studentId.siblings('.invalid-format-feedback').html());
			resolve(false);
			return false;
		}

		fetch(contextPath + rollnoURL + validateRollNoURL + "?rollno=" + studentId.val())
			.then(response => response.text())
			.then(response => {
				if (response === '') {
					studentId.removeClass("is-invalid").addClass("is-valid");
					resolve(true);
				} else {
					studentId.val('');
					studentId.addClass("is-invalid");
					errorDiv.html(studentId.siblings('.invalid-exist-feedback').html());
					resolve(false);
				}
			})
			.catch(error => {
				console.error("Fetch error:", error);
				studentId.addClass("is-invalid");
				errorDiv.html("An error occurred while validating the roll number.");
				resolve(false);
			});
	});
}
