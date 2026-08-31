$(document).ready(function() {
    $('#cancelModalPositive').removeClass('btn-secondary').addClass('btn-primary');
});
$('#addNewId').on('click', function (e) {
    window.location.href = contextPath + baseURL + '/' + requestKey;
})

$("#accommodationRequestSave").off("click").on("click", async function (e) {
    e.preventDefault();
    updateInvalidDivClass();
    // $(this).attr("disabled", true);
    const requiredFields = $('input[required], select[required], textarea[required]')
        .not('#dynamicTemplate input, #dynamicTemplate select, #dynamicTemplate textarea');
    let status = valRequiredMultiTextRadio(requiredFields);
    const dateRanges = [
        {fromElement: "periodOfAppointmentFrom", toElement: "periodOfAppointmentTo"},
        {fromElement: "periodOfStayFrom", toElement: "periodOfStayTo"}
    ];
    const dateValidationStatus = otherCandidateDateRangeValidation(dateRanges);


    if(status){
        status = validateEmailDomain(requiredFields);
    }

    let fileStatus = true;
    $('.file-info').not('#dynamicTemplate .file-info').each(function (index) {
        const fileInfo = $(this);
        const fileError = fileInfo.siblings('.file-error');

        if (fileInfo.text().trim()) {
            fileStatus = true;
            fileError.text('');
        } else {
            fileStatus = false;
            fileError.text(uploadFileMsg);
        }
    });

    const requestFrom = $("#periodOfStayFrom").val();
    const requestToEle = $("#periodOfStayTo");
    const requestTo = requestToEle.val();

    let hasOverlap = false;
    if (status) {
        const imageValid = await validateProfileImage();
        if (!imageValid) {
            status = false;
        }
    }
    if (requestFrom && requestTo && approvedStayRanges.length > 0) {
        hasOverlap = hasOverlapWithApprovedDates(requestFrom, requestTo, approvedStayRanges);
    }
    if (hasOverlap) {
        const errorDiv = requestToEle.siblings('.invalid-feedback');
        errorDiv.removeClass(displayNone);
        errorDiv.html(errorDiv.siblings('.invalid-feedback-approved-dates-compare').html());
        requestToEle.addClass(errorClass).removeClass(validClass);
        showToast('Error', 'Appointment request already approved for the selected dates.');
        status = false;
    }

    if (status && dateValidationStatus && fileStatus) {
        if($('#category').val() !== 'interviews' || $('#category').val() !== 'INTERVIEWS'){
            let conflictingStatus;

            if (!hasOverlap){
                let checkStatus = await checkConflictingAppointmentDates();
                if(checkStatus >=1) {
                    conflictingStatus = await showConflictingAppointmentDatesModal();
                }
                else{
                    conflictingStatus = true;
                }
            } else {
                conflictingStatus = true;
            }

            /*let stipendStatus;
            if(parseInt($('#stipendFellowshipPay').val()) <=0){
                stipendStatus = await showStipendModal();
            }
            else{
                stipendStatus = true;
            }*/

            if (conflictingStatus) {
                $('#accommodationRequestForm').submit();
            } else {
                $(this).attr("disabled", false);
            }
        }
        else{
            $('#accommodationRequestForm').submit();
        }

    } else {
        $(this).attr("disabled", false);
    }
});

async function validateProfileImage() {
    const imageName = $('#imageName').val();
    if (!imageName || imageName.trim() === '') {
        showToast('Error', 'Please upload your profile image before applying the request');
        return false;
    }
    const imageUrl = `${contextPath}${file}${image}/CANDIDATE_PROFILE/${imageName}`;
    try {
        const response = await fetch(imageUrl, { method: 'HEAD' });
        if (!response.ok) {
            showToast('Error', 'Please upload your profile image before applying the request.');
            return false;
        }
        return true;
    } catch (error) {
        showToast('Error', 'Unable to verify profile image.');
        return false;
    }
}

function showConflictingAppointmentDatesModal() {
    return new Promise((resolve) => {
        $('#conflictModal').modal('show');

        $('#conflictModalPositive').off("click").on("click", function () {
            $('#conflictModal').modal('hide');
            resolve(true);
        });

        $('#conflictModalCancel').off("click").on("click", function () {
            $('#conflictModal').modal('hide');
            resolve(false);
        });
    });
}

function showStipendModal(){
    return new Promise((resolve) => {
        $('#stipendModal').modal('show');
        $('#stipendModalPositive').off("click").on("click", function () {
            $('#stipendModal').modal('hide');
            resolve(true);
        });

        $('#stipendModalCancel').off("click").on("click", function () {
            $('#stipendModal').modal('hide');
            resolve(false);
        });
    });
}


function checkConflictingAppointmentDates() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: contextPath + baseURL + check,
            type: 'GET',
            data: {
                appointmentFrom: $('#periodOfAppointmentFrom').val(),
                appointmentTo: $('#periodOfAppointmentTo').val()
            },
            success: function (response) {
                resolve(response); // Properly resolve the Promise
            },
            error: function (error) {
                reject(error); // Reject the Promise on error
            }
        });
    });
}


function showCancelModal(message, key, event) {
    event.preventDefault();
    $('#cancelBodyText').text(message);
    $('#cancelModal').modal('show');
    $("#cancelModalPositive").off("click").on("click", function (e) {
        window.location.href = contextPath + baseURL + cancel + "/" + key;
    });
}

function viewAccommodation(element, event) {
    event.preventDefault();
    const id = element.getAttribute('id');
    $.ajax({
        url: contextPath + baseURL + view + baseURL + "/" + id,
        type: 'GET',
        success: function (response) {
            $('#modalDiv').html(response);
            $('#accommodationRequestViewModal').modal('show');
        },
        error: function (error) {
            showToast('Error', error.message);
        }
    });
}

let index = 0;

function addDocument() {
    const container = $('#fileUploadContainer');
    const i = index + 1;
    let descriptionText = `File Description ${index + 1}`;
    const template = $('#dynamicTemplate').html();
    let populatedTemplate = template
        .replace(/Description {index}/g, descriptionText)
        .replace(/{index}/g, index)
        .replace(/Choose File {index}/g, `Choose File ${index}`);

    const $template = $(populatedTemplate);
    container.append($template);
    index++;
}

function triggerDownload(element) {
    const fileName = element.getAttribute('data-filename');
    const downloadUrl = contextPath + file + download + '/CANDIDATE_PROFILE/' + fileName;

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

function resendAccommodationMail(key) {
    $('#resendEmailModal').modal('show');

    $('#resendEmailModalPositive').off("click").on("click", function (e) {
        fetch(contextPath + baseURL + resend + "/" + key, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.text())
            .then(response => {
                if(response === 'true'){
                    showToast('Success', 'The Mail has been successfully resent.')
                }
                else{
                    showToast('Error', "Failed to resend mail.");
                }
            });
    })
}
//Stay extension request validations
$("#stayRequestSave").off("click").on("click", async function (e) {
    const button = $(this);
    button.attr('disabled', true);
    e.preventDefault();
    updateInvalidDivClass();
    const requiredFields = $('input[required], select[required], textarea[required]');
	let isValid = true;
	requiredFields.each(function() {
        if (!$(this).val().trim()) {
            $(this).addClass('is-invalid');
            isValid = false;
        } else {
            $(this).removeClass('is-invalid');
        }
    });
    const dateRanges = [
        {fromElement: "requestFromDate", toElement: "requestToDate"}
    ];
    const isDateRangeValid = otherCandidateDateRangeValidation(dateRanges);

    const requestFrom = $("#requestFromDate").val();
    const requestToEle = $("#requestToDate");
    const requestTo = requestToEle.val();

    let hasOverlap = false;
    if (requestFrom && requestTo && approvedStayRanges.length > 0) {
        hasOverlap = hasOverlapWithApprovedDates(requestFrom, requestTo, approvedStayRanges);
    }
    if (hasOverlap) {
        const errorDiv = requestToEle.siblings('.invalid-feedback');
        errorDiv.removeClass(displayNone);
        errorDiv.html(errorDiv.siblings('.invalid-feedback-approved-dates-compare').html());
        requestToEle.addClass(errorClass).removeClass(validClass);
        showToast('Error', 'Stay extension already approved for the selected dates.');
        isValid = false;
    }

    if (isValid) {
        const imageValid = await validateProfileImage();
        if (!imageValid) {
            isValid = false;
        }
    }
    if (isValid && isDateRangeValid) {
	       $('#stayExtensionForm').submit(); 
	   } else {
            button.attr('disabled', false);
	   }
});
$("#stayRequestValidate").off("click").on("click", function (e) {
	$('#stayExtensionForm').submit();
});

function hasOverlapWithApprovedDates(requestFrom, requestTo, approvedRanges) {
    const reqFrom = new Date(requestFrom);
    const reqTo = new Date(requestTo);
    for (let i = 0; i < approvedRanges.length; i++) {
        const approvedFrom = new Date(approvedRanges[i].stayFrom);
        const approvedTo = new Date(approvedRanges[i].stayTo);
        if (reqFrom <= approvedTo && reqTo >= approvedFrom) {
            return true;
        }
    }
    return false;
}

function showExtCancelModal(element){
	var id = element.id;
    $('#cancelBodyText').text(stayExtensionReqCancel);
    $('#cancelModal').modal('show');
    $('#cancelModalPositive').removeClass('btn-secondary');
    // $('#cancelModalPositive').addClass('btn px-4 text-white btn-primary');
	$("#cancelModalPositive").off("click").on("click", function (e) {
		window.location.href = contextPath + stayExtensionReqApply + cancelRequest + "/" + id;
	});
}

function applyStayExtensionRequest(element){
	var id = element.id;
	var url = contextPath + stayExtensionReqApply +'/'+id ;
	window.location.href = url;
}

function viewExtension(element,event) {
	event.preventDefault();
	const id = element !== null ? element.id : "";
	$.ajax({
		url: contextPath + stayExtensionReqApply + view + "/" + id,
		type: "GET",
		success: function(response) {
			console.log(response);
			$('#modalDiv').html(response);
			$('#stayExtensionRequestViewModal').modal('show');
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function otherCandidateDateRangeValidation(dateRanges) {
	let allValid = true;

	dateRanges.forEach(({ fromElement, toElement }) => {
		const fromDateElement = $(`#${fromElement}`);
		const toDateElement = $(`#${toElement}`);
		const fromDateValue = fromDateElement.val();
		const toDateValue = toDateElement.val();
		const fromDate = fromDateValue ? new Date(fromDateValue) : null;
		const toDate = toDateValue ? new Date(toDateValue) : null;
		const errorDivFrom = fromDateElement.siblings('.invalid-feedback');
		const errorDivTo = toDateElement.siblings('.invalid-feedback');
		let isValid = true;

		// If both fields are required and empty, mark invalid and skip further validation for this pair
		const bothRequired = fromDateElement.hasClass('required-input') && toDateElement.hasClass('required-input');
		if (bothRequired && (!fromDateValue || !toDateValue)) {
			if (!fromDateValue) {
				fromDateElement.addClass(errorClass).removeClass(validClass);
			}
			if (!toDateValue) {
				errorDivTo.html(errorDivTo.siblings('.invalid-req-feedback').html());
				toDateElement.addClass(errorClass).removeClass(validClass);
			}
			allValid = false;
			return;
		}
		else if (!fromDateValue && !toDateValue) {
			fromDateElement.removeClass(errorClass);
			toDateElement.removeClass(errorClass);
		}

		// Validate required fields
		if (fromDateElement.hasClass('required-input') && !fromDateValue) {
			fromDateElement.addClass(errorClass).removeClass(validClass);
			isValid = false;
		} else {
			fromDateElement.removeClass(errorClass);
		}

		if (toDateElement.hasClass('required-input') && !toDateValue) {
			errorDivTo.html(errorDivTo.siblings('.invalid-req-feedback').html());
			toDateElement.addClass(errorClass).removeClass(validClass);
			isValid = false;
		} else {
			toDateElement.removeClass(errorClass);
		}

		if (!toDate && fromDate) {
			errorDivTo.html(errorDivTo.siblings('.invalid-req-feedback').html());
			toDateElement.addClass(errorClass).removeClass(validClass);
			isValid = false;
		} else {
			toDateElement.removeClass(errorClass);
		}

		if (!fromDate && toDate) {
			fromDateElement.addClass(errorClass).removeClass(validClass);
			isValid = false;
		} else {
			fromDateElement.removeClass(errorClass);
		}
		// Validate date comparison
		if (fromDate && toDate && toDate < fromDate) {
			errorDivTo.html(errorDivTo.siblings('.invalid-feedback-date-compare').html());
			toDateElement.addClass(errorClass).removeClass(validClass);
			isValid = false;
		} else if (fromDate && toDate) {
			fromDateElement.removeClass(errorClass).addClass(validClass);
			toDateElement.removeClass(errorClass).addClass(validClass);
		}
		if (!isValid) {
			allValid = false;
		}
	});
	return allValid;
}
