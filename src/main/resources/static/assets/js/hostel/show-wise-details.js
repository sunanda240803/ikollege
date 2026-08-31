let previewBindings;
$(document).ready(function () {
    $('#addNewId').removeAttr('disabled');

    if (modalError === true) {
        editShowWiseDetails(null);
    }

    $('#addNewId, #editButton').click(function () {
        editShowWiseDetails(null);
    });
   
});


function editShowWiseDetails(element) {
	const showId = element !== null ? element.getAttribute("data-show-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + showId,
		type: "Get",
		success: function(response) {
			$('#showWiseModalDiv').html(response);
			$('#show-wise-details-modal').modal('show');
			updateSaveButtonStyle(showId, $('#show-wise-details-modalSave'));
			$("#show-wise-details-modalSave").off("click").on("click", function(e) {
				 e.preventDefault();
            updateInvalidDivClass();
            const saveButton = $(this);
				const requiredFields = $('input[required], select[required]');
				const status = valRequiredMultiTextRadio(requiredFields);
				if (status) {
					if (validateDateRange('regStartingDate', 'regEndingDate')) {
						var isExist = checkShowNameExist();
						if (isExist) {
							$('#showWiseForm').submit();
						}
						else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					$("#show-wise-details-modalSave").attr('disabled', false);
					return false;
				}
			});
			$("#show-wise-details-modalValidateBackend").off("click").on("click", function (e) {
            $('#showWiseForm').submit();
        })
		},
		error: function(error) {
			showToast('Error', 'Error occured');
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
		//const requiredFields = $('select[required]');
				//const status = valRequiredMultiTextRadio(requiredFields);
    if (isValid) {
        let eventId = eventMaster.val(); 

        // Set these values in the additional param inputs
        $('input[name="additionalParam.eventId"]').val(eventId);
        // Call createUrlWithParams with the current page and size
        let page = $('#pageVal').val(); 
        let size = $('#sizeVal').val();
        let search= $('#search').val();
        createUrlWithParams(page, size , search);
    }

    return isValid;
}

// Check show name exist

function checkShowNameExist() {
	var showName = $('#showName');
	var eventId = $('#showEventMasterId');
	var showNameVal = showName.val().trim();
	var eventIdVal = eventId.val().trim();
	var showId = $('#showId').val();
	let isValid = false;
	if(showId == null || showId == ''){
		showId = 0;
	}
	if (showNameVal != '' && eventIdVal != '' ) {
		var validateUrl = contextPath + baseURL + checkShowNameExistURL + "/" + eventIdVal + "/" + showNameVal +"/"+ showId;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,  // This is required for synchronous validation
			success: function(response) {
				if (response == false || response == 'false') {
					$('#showEventMasterId').removeClass(errorClass);
					$('#showName').removeClass(errorClass);
					isValid = true;
				} else {
					showToast("Error", showNameExistMessage);
					//$('#showEventMasterId').val('');
					$('#showName').val('');
					//$('#showEventMasterId').addClass(errorClass);
					$('#showName').addClass(errorClass);
			
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

function validateDateRange(fromElement, toElement) {
	let isValid = true;
	const fromDate = $('#' + fromElement).val();
	const toDate = $('#' + toElement).val();

	// Check if both dates are provided
	if (!fromDate || !toDate) {
		// Display the "required" feedback if either date is missing
		$('#regEndingDate').addClass(errorClass);
		$('#regEndingDate').siblings('.invalid-feedback').removeClass('d-none');
		$('#regEndingDate').siblings('.invalid-feedback-invalidDate').addClass('d-none');
		isValid = false;
	} else if (toDate <= fromDate) {
		// Display the "invalid date greater" feedback if toDate is less than or equal to fromDate
		$('#regEndingDate').addClass(errorClass);
		$('#regEndingDate').siblings('.invalid-feedback-invalidDate').removeClass('d-none');
		$('#regEndingDate').siblings('.invalid-feedback').addClass('d-none');
		isValid = false;
	} else {
		// Clear all errors when validation passes
		$('#regEndingDate').removeClass(errorClass);
		$('#regEndingDate').siblings('.invalid-feedback').addClass('d-none');
		$('#regEndingDate').siblings('.invalid-feedback-invalidDate').addClass('d-none');
		isValid = true;
	}

	return isValid;
}

