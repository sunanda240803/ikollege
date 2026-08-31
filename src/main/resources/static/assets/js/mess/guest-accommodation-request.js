$(document).ready(function() {
    $('#downloadId').click(function() {
        window.location.href = contextPath + baseURL + "/" + $('#newId').val();
    });
    $('#cancelModalPositive').removeClass('btn-secondary').addClass('btn-primary');
    const now = new Date();
    const isoNow = now.toISOString().slice(0, 16); // Get the date part in 'YYYY-MM-DDTHH:MM' format

    // Restrict past dates in check-in and check-out fields
    $('#checkInDate').attr('min', isoNow.slice(0, 10)); // Just the date part for min
    $('#checkOutDate').attr('min', isoNow.slice(0, 10));

    // Set min attribute for checkOutDate based on checkInDate
    $('#checkInDate').on('change', function() {
        const checkInDate = $(this).val();
        if (checkInDate) {
            $('#checkOutDate').attr('min', checkInDate); // Set the min date for check-out based on check-in
        } else {
            $('#checkOutDate').attr('min', isoNow.slice(0, 10)); // Reapply the default min if check-in is cleared
        }
    });

    const checkInDateInput = document.getElementById("checkInDate");

    if (checkInDateInput) {
        let today = new Date();

        // Add 10 days
        let maxDate = new Date();
        maxDate.setDate(today.getDate() + 10);

        // Format date as 'YYYY-MM-DD' for the max date
        const formatDate = (date) => {
            let year = date.getFullYear();
            let month = String(date.getMonth() + 1).padStart(2, "0");
            let day = String(date.getDate()).padStart(2, "0");
            return `${year}-${month}-${day}`;
        };

        // Set max attribute for check-in date
        checkInDateInput.setAttribute("max", formatDate(maxDate));
    }

    // Default checked and disabled status for specific checkboxes
    $('#stayWithStudent').prop('checked', false).prop('disabled', true);
    $('#individualGuestRoom').prop('checked', false).prop('disabled', true);
});

$(document).on('change', 'input[type="checkbox"][name^="guestList"], input[type="date"], select', function() {
	const selectedGuests = $('input[type="checkbox"][name^="guestList"]:checked');
	// const container = $('#fileUploadContainer');
	const requestStartDate = $('#checkInDate').val();
	const requestEndDate = $('#checkOutDate').val();
	const checkInTime = $('#checkInTime').val();
	const checkoutTime = $('#checkOutTime').val();
	let studentHostel = $('#studentHostel').val() === "false";
	// container.empty();
	if(selectedGuests.length > 0) {
		const maxGuests = 2;
		if ($(this).is(':checked') && selectedGuests.length > maxGuests) {
           showToast('Error', guestSelectionLimitMessage.replace('{0}', maxGuests));
           $(this).prop('checked', false);
           return;
        }
	}

	if (selectedGuests.length > 0 && requestStartDate != null && requestStartDate != '' &&
		requestEndDate != null && requestEndDate != '' &&
		checkInTime != null && checkInTime != '' &&
		checkoutTime != null && checkoutTime != '') {
		if (selectedGuests.length > 0) {
			const maxGuests = 2;
			const studentGender = $('#gender').val();
			let stayWithStudent = false;
			let fatherSelected = false;
			let motherSelected = false;

			if (selectedGuests.length > maxGuests) {
				const errorMessage = guestSelectionLimitMessage.replace('{0}', maxGuests);
				showToast('Error', errorMessage);
				$(this).prop('checked', false);
				return;
			}

			selectedGuests.each(function() {
				const relationType = $(this).closest('.RadioCheckToggle')
					.find('input[type="hidden"][name$=".relationOfGuest"]')
					.val();

				// Track if Father or Mother is selected
				if (relationType === father) fatherSelected = true;
				if (relationType === mother) motherSelected = true;

				const guestGender = [father, brother, husband].includes(relationType) ? maleShortForm : femaleShortForm;

				if (guestGender === studentGender && selectedGuests.length === 1) {
					stayWithStudent = true;
				}
			});

			// Check if both Father and Mother are selected
			if ($(this).is('input[type="checkbox"]')) {
				if (fatherSelected && motherSelected) {
					$('#infoModal').modal('show');
				}
			}

			if (studentHostel) {
				$('#stayWithStudent').prop('checked', false).prop('disabled', true);
				$('#individualGuestRoom').prop('checked', true).prop('disabled', false);
			} else if (selectedGuests.length > 1) {
				// More than 1 guest
				$('#stayWithStudent').prop('checked', false).prop('disabled', true);
				$('#individualGuestRoom').prop('checked', true).prop('disabled', false);
			} else if (stayWithStudent) {
				// One guest, genders match
				if ($('#gender').val() === studentGender) {
					// Automatically select Stay With Student
					$('#stayWithStudent').prop('checked', true).prop('disabled', false);
					$('#individualGuestRoom').prop('checked', false).prop('disabled', false);
				} else {
					// Allow manual override for mismatched genders
					$('#stayWithStudent').prop('checked', false).prop('disabled', false);
					$('#individualGuestRoom').prop('checked', true).prop('disabled', false);
				}
			} else {
				// One guest, genders differ
				$('#stayWithStudent').prop('checked', false).prop('disabled', true);
				$('#individualGuestRoom').prop('checked', true).prop('disabled', false);
			}

			let appendedBioIds = new Set();

			selectedGuests.each(function() {
				const index = $(this).attr('data-index');
				let bioId;
				let descriptionText = `Description ${index + 1}`;
				
				bioId = $(this).closest('.RadioCheckToggle')
					.find('input[type="hidden"][name$=".guestId"]')
					.val();
				const name = $(this).closest('.RadioCheckToggle')
					.find('input[type="hidden"][name$=".guestName"]')
					.val();
				const relation = $(this).closest('.RadioCheckToggle')
					.find('input[type="hidden"][name$=".relationOfGuest"]')
					.val();
				const guestId = $(this).closest('.RadioCheckToggle')
					.find('input[type="hidden"][name$=".guestId"]')
					.val();

				descriptionText = `${name} (${relation}) - ${idProof}`;
				$.ajax({
					url: contextPath + baseURL + checkFileNameById,
					method: 'POST',
					data: JSON.stringify({ bioId: bioId }),
					contentType: 'application/json',
					success: function(response) {
						if (!response.exists) {
							$('#fileUploadSection').removeClass(displayNone);
							if (!appendedBioIds.has(bioId)) {
								const descInput = $('#description_' + index);
								if (descInput.length === 0){
									addUploadDocSection(index, descriptionText, null, null, false, guestId);
								}
								appendedBioIds.add(bioId);
							}
						}
					},
					error: function(xhr, status, error) {
						console.error(`Error checking bioId ${bioId}: ${error}`);
					}
				});
			});
			
			/*$.ajax({
				url: contextPath + baseURL + checkHostelRoomOccupancy,
				type: 'GET',
				data: {
					checkInDate: $('#checkInDate').val(),
					checkOutDate: $('#checkOutDate').val()
				},
				success: function(response) {
					if (response != null && response === 'Allotted') {
						$('#stayWithStudent').prop('checked', false).prop('disabled', true);
						$('#individualGuestRoom').prop('checked', true).prop('disabled', false);
					}
				},
				error: function(error) {
					console.error(error);
				}
			});*/

			$('#noOfPersons').val(selectedGuests.length);
			$('#persons').text(selectedGuests.length);
		} else {
			$('#noOfPersons').val('');
			$('#persons').text('');
			$('#stayWithStudent').prop('checked', false);
			$('#individualGuestRoom').prop('checked', false);
			// container.empty();
			$('#fileUploadSection').addClass(displayNone);
			if ($(this).is('input[type="checkbox"]')) {
				showToast('Error', guestSelectionMsg);
			}
		}
	} else {
		$('#stayWithStudent').prop('checked', false).prop('disabled', true);
		$('#individualGuestRoom').prop('checked', false).prop('disabled', true);
	}
	$('input[type="checkbox"][name^="guestList"]:not(:checked)').each(function () {
		const guestIndex = $(this).attr('data-index');
		$('#proof_' + guestIndex).remove();
	});
});
$('#checkOutDate, #checkInDate, #checkInTime, #checkOutTime').on('change', function() {
    validateDates();
});

function validateDates() {
    const checkInDate = $('#checkInDate').val();
    const checkOutDate = $('#checkOutDate').val();
    const checkInTime = $('#checkInTime').val();  // Get check-in time
    const checkOutTime = $('#checkOutTime').val();  // Get check-out time
    const requiredFeedbackCheckIn = $('#checkInDate').siblings('.invalid-feedback');
    const requiredFeedbackCheckOut = $('#checkOutDate').siblings('.invalid-feedback');
    const exceedFeedback = $('#checkOutDate').siblings('.invalid-value-feedback');
	const limitFeedback = $('#checkOutDate').siblings('.invalid-limit-feedback');
	let isValid = true;

    $('#checkInDate, #checkOutDate').removeClass(errorClass);
    requiredFeedbackCheckIn.addClass(displayNone);
    requiredFeedbackCheckOut.addClass(displayNone);
    exceedFeedback.addClass(displayNone);
	limitFeedback.addClass(displayNone);

    if (!checkInDate) {
        requiredFeedbackCheckIn.removeClass(displayNone);
        $('#checkInDate').addClass(errorClass);
        isValid = false;
		return;
    }
    if (!checkOutDate) {
        requiredFeedbackCheckOut.removeClass(displayNone);
        $('#checkOutDate').addClass(errorClass);
        isValid = false;
		return;
    }

    // Combine date and time for check-in and check-out
    const checkInDateTime = combineDateAndTime(checkInDate, checkInTime);
    const checkOutDateTime = combineDateAndTime(checkOutDate, checkOutTime);
    const checkIn = new Date(checkInDateTime);
    const checkOut = new Date(checkOutDateTime);

    const diffInMs = checkOut - checkIn;
    const diffInDays = Math.ceil(diffInMs / (24 * 60 * 60 * 1000));
    // Check maximum allowed days
    const maxAllowedDays = parseInt($('#maximumDays').val(), 10) || 0;
    if (diffInDays > maxAllowedDays) {
        exceedFeedback.html(checkoutDateLimit);
        exceedFeedback.removeClass(displayNone);
		$('#checkOutDate').addClass(errorClass);
        isValid = false;
		return;
    }

	if (diffInDays < 1) {
	    limitFeedback.html(oneDayLimit);
	    limitFeedback.removeClass(displayNone);
		$('#checkOutDate').addClass(errorClass);
	    isValid = false;
		return;
	}

//    calculateDays(diffInDayst);
    $('#noOfDays').val(diffInDays);
    $('#days').text(diffInDays);
	return isValid;

}

// Function to combine date and time
function combineDateAndTime(date, time) {
    return date + 'T' + time + ':00';  // Combine date and time as 'YYYY-MM-DDTHH:MM:00'
}

// Function to calculate the number of days
/*function calculateDays(diffInMs) {
    const checkInDate = new Date(checkIn);
    const checkOutDate = new Date(checkOut);

    // Calculate the difference in milliseconds
    const diffInMs = checkOutDate - checkInDate;

    // Convert milliseconds to days (1 day = 24 * 60 * 60 * 1000 ms)
    const diffInDays = Math.ceil(diffInMs / (24 * 60 * 60 * 1000));

    // Update the noOfDays field
    $('#noOfDays').val(diffInDays);
    $('#days').text(diffInDays);
}*/

$('#guestRequestSave').off("click").on("click", function(e) {
    e.preventDefault();
	const saveButton = $(this);
	saveButton.attr('disabled', true);
    const requiredFields = $('input[required], select[required], textarea[required]');
    const status = valRequiredMultiTextRadio(requiredFields);
    const checkInDate = $('#checkInDate').val();
    const checkOutDate = $('#checkOutDate').val();
    let fileStatus = true;

    const selectedGuests = $('input[type="checkbox"][name^="guestList"]:checked');
    if (selectedGuests.length === 0) {
        showToast('Error', guestSelectionMsg);
		saveButton.attr('disabled', false);
        return false;
    }

    if (checkInDate && checkOutDate && new Date(checkOutDate) < new Date(checkInDate)) {
        showToast('Error', checkindateInvalidMsg);
		saveButton.attr('disabled', false);
        return false;
    }

    if (status && fileStatus && validateDates()) {
        $('#guestRequestForm').submit();
    } else {
        showToast('Error', 'Please fill all the mandatory fields and submit.');
		saveButton.attr('disabled', false);
        return false;
    }
});

function viewGuestAccommodationRequest(element) {
	const requestId = element !== null ? element.getAttribute("data-guest-id") : "";
	showLoader();
	$.ajax({
		url: contextPath + baseURL + "/" + requestId,
		type: "Get",
		success: function(response) {
			hideLoader();
			$('#modalDivView').html(response);
			$('#view-request-modalSave').addClass(dNone);
			$('#view-request-modal').modal('show');
		},
		error: function(error) {
			hideLoader();
			showToast('Error', 'Error occured');
		}
	});
}

function triggerDownload(element) {
	var fileName = element.getAttribute('data-filename');
	const url = contextPath + baseURL + downloadURL + "/" + fileName;
	window.location.href = url;
}

function cancelGuestAccommodationRequest(element) {
	const requestId = element !== null ? element.getAttribute("data-guest-id") : "";
	const cancelModelPositive = $('#cancelModalPositive');
	$('#cancelModal').modal('show');
	cancelModelPositive.removeClass('btn-secondary').addClass('btn-primary');
	cancelModelPositive.off('click').click(function() {
		$('#cancelModal').modal('hide');
		showLoader();
		$.ajax({
			url: contextPath + baseURL + cancelURL +"/" + requestId,
			type: "GET",
			success: function(response) {
				hideLoader();
				if(response == cancel){
					showToast(success, cancelMsg);
					setTimeout(function() {
					window.location.reload();
				}, 3000);
			} else {
					hideLoader();
				showToast('Failure', 'Error occurred');
			}
				
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
		$('#cancelModalPositive').off('click');
	});
	
}