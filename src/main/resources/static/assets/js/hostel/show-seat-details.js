$(document).ready(function() {
	$('#addNewId').removeAttr('disabled');

	if (modalError === true) {
		editShowSeatDetails(null);
	}

	$('#addNewId, #editButton').click(function() {
		editShowSeatDetails(null);
	});
});

function editShowSeatDetails(element) {
	const seatId = element !== null ? element.getAttribute("data-seat-id") : 0;
	const eventId = !!$('#eventId').val() ? $('#eventId').val() : 0;
	$.ajax({
		url: contextPath + baseURL + getURL + "/" + seatId + "/" + eventId,
		type: "Get",
		success: function(response) {
			$('#showSeatModalDiv').html(response);
			$('#show-seat-details-modal').modal('show');
			$('.selectpicker').selectpicker('refresh');
			updateSaveButtonStyle(seatId, $('#show-seat-details-modalSave'));
			$("#show-seat-details-modalSave").off("click").on("click", function(e) {
				e.preventDefault();
				updateInvalidDivClass();
				const saveButton = $(this);
				const requiredFields = $('input[required], select[required]');
				const status = valRequiredMultiTextRadio(requiredFields);
				if(seatId == 0){
				var showNameInput = $('#showNameInput');
				if (!valRequiredSelect(showNameInput)) {
					showNameInput.parent().find('.dropdown-toggle').addClass(errorClass);
					return false;
				} else {
					showNameInput.selectpicker('setStyle', errorClass, 'remove');
					showNameInput.parent().find('.dropdown-toggle').removeClass(errorClass);
				}
				}
				if (status) {
					var checkCredit = getMaxCreditAmount();
					if(checkCredit){
					$('#showSeatDetailsForm').submit();
					}else{
						return false;
					}
				} else {
					$("#show-seat-details-modalSave").attr('disabled', false);
					return false;
				}
			});
			$("#show-seat-details-modalValidateBackend").off("click").on("click", function(e) {
				$('#showSeatDetailsForm').submit();
			})
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function getShowNameList(eventInputId, id) {
	var eventId = $('#' + eventInputId).val();
	fetch(contextPath + baseURL + getShowNameURL + "/" + eventId, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}).then(response => {
		if (!response.ok) {
			return response.json().then(err => {
				throw err;
			}); // Handle errors
		}
		return response.json();
	}).then(responseJson => {
		if (Array.isArray(responseJson)) {
			var $select = $('#' + id);
			$select.empty();
			$select.append('<option value="">' + selectShowNameMessage + '</option>');

			$.each(responseJson, function(index, show) {
				$select.append('<option value="' + show.id + '">' + show.showName + '</option>');
			});
			 $select.selectpicker('refresh');
		}

	});
}

function validateSearchFeild() {
	var eventMaster = $('#eventId');
	var isValid = true;


	if (!valRequiredSelect(eventMaster)) {
		eventMaster.selectpicker('setStyle', errorClass, 'add');
		eventMaster.parent().find('.dropdown-toggle').addClass(errorClass);
		isValid = false;
	} else {
		eventMaster.selectpicker('setStyle', errorClass, 'remove');
		eventMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
		$('#eventVal').addClass('d-none');
		$('#eventVal').removeClass('d-block');
	}
	// Validate floorName
	if (!valRequiredSelect('#showName')) {
		isValid = false;
	}
	if (isValid) {
		let eventId = eventMaster.val();
		let showId = $('#showName').val();
		// Set these values in the additional param inputs
		$('input[name="additionalParam.eventId"]').val(eventId);
		$('input[name="additionalParam.showId"]').val(showId);
		// Call createUrlWithParams with the current page and size
		let page = $('#pageVal').val();
		let size = $('#sizeVal').val();
		let search = $('#search').val();
		createUrlWithParams(page, size, search);
	}

	return isValid;
}

function getMaxCreditAmount() {
	let isValid = true;
	var eventId = $('#eventIdInput').val();
	var showSeatId = $('#showSeatId').val();
	if (showSeatId > 0) {
		var eventId = $('#editEventId').val();
	}
	var discountedAmount = parseFloat($('#discountedAmount').val());
	if (eventId != '' && eventId != '') {
		var validateUrl = contextPath + eventURL + "/" + eventId;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,  // This is required for synchronous validation
			success: function(response) {
				var creditLimit = parseFloat(response.creditLimit);
				if (discountedAmount > creditLimit) {
					showToast('Error', discountMessage + ' ' + creditLimit);
					$('#discountedAmount').val('');
					$('#discountedAmount').addClass(errorClass);
					isValid = false;
				}else{
				$('#discountedAmount').removeClass(errorClass);
				}
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
	}
	return isValid;
}
