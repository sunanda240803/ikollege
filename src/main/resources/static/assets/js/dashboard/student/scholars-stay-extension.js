$(document).ready(function () {
    if (modalError === true) {
        addScholarsStayExtension();
    }
    $('#addNewId').click(function() {
        addScholarsStayExtension();
    });
    $('#thesis-submitted-modalPositive').click(function (){
        logoutStudentSession();
    });
    $('#cancelModalPositive').removeClass('btn-secondary').addClass('btn-primary');
});

function addScholarsStayExtension() {
    showLoader();
    fetch(contextPath + baseURL + getURL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        return response.text();
    }).then(response => {
        console.log($('#guideNameEmailPresent').val())
        if ($('#guideNameEmailPresent').val() === 'false'){
            $('#modalDiv').html(response);
            $('#scholars-stay-extension-modal').modal('show');
            hideLoader();
            // $('#admissionDate').on('change', updateStayFromRestrictions);
            $('#selectCategory').change(function() {
                const selectedValue = $(this).val();
                if (selectedValue === "ThesisSubmitted") {
                    $('#scholars-stay-extension-modal').modal('hide');
                    $(this).val(""); // Reset the dropdown
                    $('#thesis-submitted-modal').modal('show');
                } else {
                    $('#extensionFields').removeClass('d-none');
                }
            });

            $('#admissionDate').change(function () {
                const admissionDate = $(this).val();
                if (admissionDate) {
                    const msOrPhdPeriod = $('#msOrPhdPeriod').val() === '5 Years MS / PHD' ? 5 : 2;
                    const minDateFromAdmission = new Date(admissionDate);
                    minDateFromAdmission.setFullYear(minDateFromAdmission.getFullYear() + msOrPhdPeriod);
                    const currentDate = new Date();
                    const effectiveMinDate = currentDate < minDateFromAdmission ? currentDate : minDateFromAdmission;
                    const effectiveMinDateFormatted = effectiveMinDate.toISOString().split('T')[0];
                    const stayFrom = $('#stayFrom');
                    stayFrom.removeAttr('disabled');
                    stayFrom.attr('min', effectiveMinDateFormatted);
                    stayFrom.val('');
                    $('#stayTo').removeAttr('disabled');
                }
            });

            $("#scholars-stay-extension-modalSave").off("click").on("click", function (e) {
                e.preventDefault();
                updateInvalidDivClass();
                const requiredFields = $('input[required], select[required], textarea[required]');
                const dateRanges = [
                    { fromElement: "stayFrom", toElement: "stayTo" }
                ];
                const stayFrom = $('#stayFrom');
                const admissionDate = $('#admissionDate');
                const programType = $('#msOrPhdPeriod');
                const isFieldsValid = validateRadioAndText(requiredFields);
                const isDateRangeValid = validateBetweenDateRanges(dateRanges);
                const saveButton = $(this);
                const saveBackendButton = $('#scholars-stay-extension-modalValidateBackend');
                const cancelButton = $('#scholars-stay-extension-modalCancel');
                const isAdmissionDateValid = validateStayFromDate(stayFrom[0], admissionDate[0], programType.val());
                if (isFieldsValid && isDateRangeValid && isAdmissionDateValid) {
                    saveButton.attr('disabled', true);
                    saveBackendButton.attr('disabled', true);
                    cancelButton.attr('disabled', true);
                    $('#scholars-stay-extension-form').submit();
                } else {
                    saveButton.attr('disabled', false);
                    saveBackendButton.attr('disabled', false);
                    cancelButton.attr('disabled', false);
                    return false;
                }
            });
            $("#scholars-stay-extension-modalValidateBackend").off("click").on("click", function () {
                $('#scholars-stay-extension-form').submit();
            })
        }else{
            hideLoader();
            showToast('Error', 'Guide / Faculty Name or Email is empty');
        }
    });
}

function validateStayFromDate(element, admissionDateElement, programType) {
    const stayFromDateField = $('#' + element.id);
    const admissionDateField = $('#' + admissionDateElement.id);
    let errorDiv = stayFromDateField.siblings('.invalid-feedback');
    const stayFromDate = new Date(stayFromDateField.val());
    const admissionDate = new Date(admissionDateField.val());
    let thresholdYears;
    if (programType === '5 Years MS / PHD') {
        thresholdYears = 5;
    } else if (programType === '2 Years MS / PHD') {
        thresholdYears = 2;
    } else {
        stayFromDateField.addClass(errorClass);
        errorDiv.html("Invalid program type.");
        return false;
    }
    if (!stayFromDateField.val()) {
        stayFromDateField.addClass(errorClass);
        errorDiv.html(stayFromDateField.siblings('.invalid-feedback-frontEnd').html());
        return false;
    }
    const thresholdDate = new Date(admissionDate);
    thresholdDate.setFullYear(thresholdDate.getFullYear() + thresholdYears);
    const isValid = stayFromDate >= thresholdDate;
    if (!isValid && programType === '5 Years MS / PHD') {
        stayFromDateField.addClass(errorClass);
        errorDiv.html(stayFromDateField.siblings('.invalid-feedback-phd-yrs-comp').html());
        return false;
    } else if (!isValid && programType === '2 Years MS / PHD') {
        stayFromDateField.addClass(errorClass);
        errorDiv.html(stayFromDateField.siblings('.invalid-feedback-ms-yrs-comp').html());
        return false;
    } else {
        stayFromDateField.removeClass(errorClass);
        errorDiv.html('');
        return true;
    }
}

function scholarsStayExtensionView(element) {
    showLoader();
    const extensionId = element !== null ? element.getAttribute("data-extension-id") : 0;
    fetch(contextPath + baseURL + getViewURL + "/" + extensionId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        return response.text();
    }).then(response => {
        console.log(extensionId);
        $('#modalViewDiv').html(response);
        $('#scholars-stay-extension-view-modal').modal('show');
        hideLoader();
    });
}

function scholarsStayExtensionCancel(element) {
    const extensionId = element !== null ? element.getAttribute("data-extension-id") : 0;
    $('#cancelModal').modal('show');
    fetch(contextPath + baseURL + getCancelURL + "/" + extensionId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalCancelDiv').html(response);
        $('#cancelModalPositive').off('click').one('click', function (e) {
            $('#cancelModal').modal('hide'); // Hide the modal
            e.preventDefault();
            $('#scholars-stay-extension-cancel-form').submit();
        });
    });
}

function scholarsStayExtensionResendMail(element) {
    const extensionId = element !== null ? element.getAttribute("data-extension-id") : 0;
    $('#resendEmailModal').modal('show');
    fetch(contextPath + baseURL + getResendMailURL + "/" + extensionId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalResendEmailDiv').html(response);
        $('#resendEmailModalPositive').off('click').one('click', function (e) {
            $('#resendEmailModal').modal('hide'); // Hide the modal
            e.preventDefault();
            $('#scholars-stay-extension-resend-email-form').submit();
        });
    });
}

function  logoutStudentSession(){
    fetch(contextPath + logoutURL, {
        method: 'POST',
        credentials: 'include'
    }).then(response => {
        if (response.ok) {
            window.location.href = contextPath + otherLoginURL;
        } else {
            showToast('Failure', response.message);
        }
    }).catch(error => {
        showToast('Failure', error.message);
    });
}
