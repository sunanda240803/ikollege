$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editMessSession(null);
});

function editMessSession(element) {
	const messId = element !== null ? element.getAttribute("data-mess-id") : 0;
	const sessionName = element !== null ? element.getAttribute("data-session-name") : null;
	$.ajax({
		url: contextPath + baseURL + "/" + messId + "/" + sessionName,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#mess-session-modal').modal('show');
			$('.selectpicker').selectpicker('refresh');
			updateSaveButtonStyle(messId,$('#mess-session-modalSave'));
			
			$('#mess-session-modalSave span').html(messId > 0 ? 'Update' : 'Save');

			// Get the values for startTime and endTime
			const startTime = $('#startTime').val();
			const endTime = $('#endTime').val();

			// Split and set values for start time
			if (startTime) {
				const [startHour, startMinute] = startTime.split(':');
				$('#startHour').val(startHour).change(); // Select the hour
				console.log(startTime);
				$('#startMinute').val(startMinute).change(); // Select the minute
			}

			// Split and set values for end time
			if (endTime) {
				const [endHour, endMinute] = endTime.split(':');
				$('#endHour').val(endHour).change(); // Select the hour
				console.log(endTime);
				$('#endMinute').val(endMinute).change(); // Select the minute
			}
			$("#mess-session-modalSave").on("click", function(e) {
				e.preventDefault();
				let errorCount = 0;
				const elementArray = ['#startTime', '#endTime'];
				var isValid = valRequiredTextArray(elementArray);
				var isValidSelect = valRequiredSelect('#messId');
				var isValidSelect1 = valRequiredSelect('#sessionName');
				var messSession = $('#messId');
				if (!valRequiredSelect(messSession)) {
					// messSession.selectpicker('setStyle', errorClass, 'add');
					messSession.parent().find('.dropdown-toggle').addClass(errorClass);
					return false;
				} else {
					messSession.selectpicker('setStyle', errorClass, 'remove');
					messSession.parent().find('.dropdown-toggle').removeClass(errorClass);
				}
				if (isValid && isValidSelect && isValidSelect1) {
					var isTimeValid = validateStartAndEndTime();
					if (!isTimeValid) {
						return false;
					}
					if (messId == 0 || messId == null) {
						var isExist = checkMessAndSessionExist();
						if (isExist) {
							$('#messSessionForm').submit();
						} else {
							return false;
						}
					} else {
						$('#messSessionForm').submit();
					}
				}
			});
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	})
}

function validateStartAndEndTime() {
	const startTime = $('#startTime').val();
	const endTime = $('#endTime').val();

	if (startTime && endTime) {
		// Convert the times to Date objects to compare them
		const start = new Date('1970-01-01T' + startTime + 'Z');
		const end = new Date('1970-01-01T' + endTime + 'Z');

		if (end <= start) {
			// Add error class and show invalid feedback
			$('#endTime').addClass(errorClass);
			$('#endHour').addClass(errorClass);
			$('#endMinute').addClass(errorClass);
			$('#endTime').next('.invalid-feedback').text('End time must be greater than start time.');
			return false;
		} else {
			// Remove error class if validation passes
			$('#endTime').removeClass(errorClass);
			$('#endHour').removeClass(errorClass);
			$('#endMinute').removeClass(errorClass);
			return true;
		}
	}
	return false; // Validation fails if times are not filled
}



function deleteMessSession(element) {
	const messId = element !== null ? element.getAttribute("data-mess-id") : 0;
	const sessionName = element !== null ? element.getAttribute("data-session-name") : null;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + messId + "/" + sessionName,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + messId + sessionName).parents('tr').remove();
					//$('#deleteReq_' + messId + sessionName).parents('tr').remove();
				}
				showToast(response.status, response.message);
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

function checkMessAndSessionExist() {
	var sessionName = $('#sessionName');
	var messId = $('#messId');
	var sessionNameVal = sessionName.val().trim();
	var messIdVal = messId.val().trim();
	let isValid = false;
	if (sessionNameVal != '' && messIdVal != '') {
		var validateUrl = contextPath + baseURL + checkMessAndSessionExistURL + "/" + messIdVal + "/" + sessionNameVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,  // This is required for synchronous validation
			success: function(response) {
				if (response == false || response == 'false') {
					$('#messId').removeClass(errorClass);
					$('#sessionName').removeClass(errorClass);
					isValid = true;
				} else {
					showToast("Error", "Mess Name and Session already exist");
					$('#messId').val('');
					$('#sessionName').val('');
					$('#messId').addClass(errorClass);
					$('#sessionName').addClass(errorClass);
			
					isValid = false;
				}
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
	}
	return isValid;
}

function clearInput() {
	var messId = $('#messId').val();
	if (messId != '') {
		$('#messInputVal').addClass('d-none');
	}
}

function updateHiddenStartTime() {
	const startHours = $('#startHour').val();
	const startMinutes = $('#startMinute').val();
	console.log(`Updating hidden input: startTime with hours: ${startHours}, minutes: ${startMinutes}`);
	if (startHours !== '' && startMinutes !== '') {
		$('#startTime').val(startHours + ':' + startMinutes);
		console.log($('#startTime').val());
		console.log($('#startTime').attr('type'));

	} else {
		$('#startTime').val('');  // Clear if both are not selected
	}
}

function updateHiddenEndTime() {
	const endHours = $('#endHour').val();
	const endMinutes = $('#endMinute').val();
	console.log(`Updating hidden input: endTime with hours: ${endHours}, minutes: ${endMinutes}`);
	if (endHours !== '' && endMinutes !== '') {
		$('#endTime').val(endHours + ':' + endMinutes);
		console.log($('#endTime').val());
		console.log($('#endTime').attr('type'));
	} else {
		$('#endTime').val('');  // Clear if both are not selected
	}
}