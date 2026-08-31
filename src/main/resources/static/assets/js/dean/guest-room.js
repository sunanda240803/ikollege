$(document).ready(function() {
	$("#addNewId i").attr("class", "fa-solid fa-file-excel me-1");
	$('#addNewId').removeClass('btn-success').addClass('btn-pink');
	$('#updateId').addClass('d-none');
	$('#cancelButton').addClass('d-none');

	$("#toggleFilter").click(function() {
		const isExpanded = $(this).attr("aria-expanded") === "true";
		const toggleFilterDiv = $(".toggleFilterSection");

		if (isExpanded) {
			$("#toggleFilterText").text(hideAdvanceFilter);
			$(".keywordSearch").addClass('d-none');
			toggleFilterDiv.removeClass("col-lg-6").addClass("col-lg-12");
		} else {
			$("#toggleFilterText").text(showAdvanceFilter);
			$(".keywordSearch").removeClass('d-none');
			toggleFilterDiv.removeClass("col-lg-12").addClass("col-lg-6");
		}
	});
	
});

function editGuestRoomModal() {
	$('#guest-room-modal').modal('show');
}


$("#guest-room-modalSave").click(function() {
	const requiredFields = $('input[required]');
	validateRadioAndText(requiredFields);
});

function rejectGuestAccom(frmName, reversal){
    var form = document.getElementById(frmName);
    $('#deleteModal').modal('show');
    $('#deleteModalCancel').removeClass(displayNone);
    $('#deleteBodyText').html(`
            <div class="mb-3 themeForm">
                <textarea type="text" class="form-control" id="rejectReason" maxlength="360" oninput="trimSpaces(this)" placeholder="Enter Reject Reason"></textarea>
                <div class="invalid-feedback">Rejection Reason Required</div>
            </div>
        `);
    const rejectReason = $('#rejectReason');
    const deleteModalPositive = $("#deleteModalPositive");

    $('modalheader').text(reversal === 'reverse' ? 'This request is in Approved state. Do you want to Reject the request?' :
        'Are you sure you want to Reject this accommodation request ?');
    deleteModalPositive.prop("disabled", true);

    if (rejectReason.val().trim() === "") {
        rejectReason.addClass(errorClass).removeClass(validClass);
    } else {
        rejectReason.addClass(validClass).removeClass(errorClass);
    }
    rejectReason.on("input", function () {
        if (rejectReason.val().trim() === "") {
            deleteModalPositive.prop("disabled", true);
        } else {
            deleteModalPositive.prop("disabled", false);
        }
    });
    deleteModalPositive.html(`
            <i class="fa fa-check-circle"></i> Confirm
            `);
    deleteModalPositive.off("click").on("click", function (e) {
        $('#infoModal').modal('hide');
         if (!rejectReason) {
            rejectReason.addClass(errorClass).removeClass(validClass);
            return false;
        }
         // Add hidden input for reject reason
             let reasonInput = form.querySelector('input[name="rejectionDescription"]');
             if (!reasonInput) {
                 reasonInput = document.createElement('input');
                 reasonInput.type = 'hidden';
                 reasonInput.name = 'rejectionDescription';
                 reasonInput.value = rejectReason.val().trim();
                 form.appendChild(reasonInput);
             }


             // Now call your common function
              OverrideApproveGuestAccomReq(frmName, 'Rejected');
    });
}

// Function to handle Override and Approve/Reject actions
async function OverrideApproveGuestAccomReq(frmName, status) {
    resetErrors();
    // Validate date inputs
    const reqFromDate = document.getElementById("checkInDate").value;
    const reqToDate = document.getElementById("checkOutDate").value;
    // Check if required fields are empty
    if (!reqFromDate || !reqToDate) {
        if (!reqFromDate) {
            showError('checkInDateError', checkInDateRequired, 'checkInDate');
        }
        if (!reqToDate) {
            showError('checkOutDateError', checkOutDateRequired, 'checkOutDate');
        }
        return false; // Stop further execution if required fields are empty
    }

    const fromDate = new Date(reqFromDate);
    const toDate = new Date(reqToDate);

    // Check if "To Date" is greater than "From Date"
    if (toDate <= fromDate) {
        showError('checkOutDateError', toDateGreaterThanFromDate, 'checkOutDate');
        return false;
    }

    // Check if "From Date" is a future or current date
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (fromDate < today) {
        showError('checkInDateError', fromDateFutureOrCurrent, 'checkInDate');
        return false;
    }

    // Check if "To Date" is within the allowed range
    const minToDate = maxDateDiff;
    const maxToDate = new Date(fromDate);
    maxToDate.setDate(maxToDate.getDate() + minToDate - 1);

    if (toDate > maxToDate) {
        showError('checkOutDateError', `"Check-out Date" must be within ${minToDate} days from "Check-in Date"`, 'checkOutDate');
        return false;
    }
    resetErrors();
    // Prepare data for API call
    const data = {
        status: status,
        //givenStr: document.getElementById('givenStr').value,
        reqFromDate: reqFromDate,
        reqToDate: reqToDate,
    };

    var form = document.getElementById(frmName);

    const inputName = 'status';
    let pageInput = form.querySelector(`input[name="${inputName}"]`);

    if (pageInput) {
        pageInput.value = status;
    } else {
        pageInput = document.createElement('input');
        pageInput.type = 'hidden';
        pageInput.name = inputName;
        pageInput.value = status;
        form.appendChild(pageInput);
    }

    $('#overrideAndApproveBtn').attr('disabled', true);
    $('#approveBtn').attr('disabled', true);
    $('#rejectBtn').attr('disabled', true);
    $('#paidRejectBtn').attr('disabled', true);
    $.ajax({
        url: form.action,  // Use the form's action URL
        type: 'POST',
        data: $(form).serialize(), // Serialize the form data
        beforeSend: function () {
                // Show the loader just before the request is sent
                showLoader();
            },
        success: function(response) {
            if (response.status === 'Success') {
               // Close the modal on successful submission
               window.scrollTo({
                     top: 0,
                     behavior: 'smooth'
                 });
               showToast(response.status, response.message);

                setTimeout(function() {
                    window.location.href = contextPath + baseUrl;
                }, 5000);

            } else {
                showToast(response.error, response.message);
                $('#overrideAndApproveBtn').attr('disabled', false);
                $('#approveBtn').attr('disabled', false);
                $('#rejectBtn').attr('disabled', false);
                $('#paidRejectBtn').attr('disabled', false);
            }


        },
        error: function(xhr, status, error) {
            // Handle errors if any
            console.error('Error:', error);
            $('#overrideAndApproveBtn').attr('disabled', false);
            $('#approveBtn').attr('disabled', false);
            $('#rejectBtn').attr('disabled', false);
            $('#paidRejectBtn').attr('disabled', false);
        },
        complete: function () {
                hideLoader();
            }
    });
}

// Function to handle re-approval
function reApprove() {
    if (confirm(requestRejectedConfirmation)) {
        OverrideApproveGuestAccomReq('viewGuestRoomDetailForm', 'OverrideAndApproved');
    }
}

function resetErrors() {
    document.getElementById('checkInDateError').style.display = 'none';
    document.getElementById('checkOutDateError').style.display = 'none';
    document.getElementById('checkInDate').classList.remove('errorBdr');
    document.getElementById('checkOutDate').classList.remove('errorBdr');
}

// Helper function to show errors
function showError(errorElementId, errorMessage, inputElementId) {
    document.getElementById(errorElementId).innerHTML = errorMessage;
    document.getElementById(errorElementId).style.display = '';
    document.getElementById(inputElementId).classList.add('errorBdr');
    document.getElementById(errorElementId).scrollIntoView({
        behavior: 'smooth', // Smooth scrolling
        block: 'center'    // Center the field in the viewport
    });
}

function showLoader() {
    $('#loader').removeClass('d-none');
    $('html, body').addClass('stop-scrolling');
}

function hideLoader() {
    $('#loader').addClass('d-none');
    $('html, body').removeClass('stop-scrolling');
}

function downloadFile(element){
    const fileName = element.getAttribute('file-name');
    const downloadUrl = contextPath + file + downloadURL + '/BIO_DATA_PARENT_PROOF/' + fileName;

    fetch(downloadUrl, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw errorData;
                });
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            // Show toast for errors
            if (error.errors && (error.code === 500 || error.code === 400)) {
                showToast(error.status, error.errors);
            } else {
                showToast('Error', 'File Not Found');
            }
        });
}