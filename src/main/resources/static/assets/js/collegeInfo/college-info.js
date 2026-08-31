$(document).ready(function() {
	let phNumMaskElementArray = ['#alternateContactNumber', '#contactNumber'];
	inputMask(phNumMaskElementArray, mobileNumberMask);

    $('#collegeSave, #updateId').off("click").on("click", function (e) {
        e.preventDefault();
        validateAndSubmit();
    });
});

function validateAndSubmit() {
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const emailID = $('#email');
    const isFieldsValid = validateRadioAndText(requiredFields);
    let isValidWebsiteUrl = validateWebsiteURL($('#websiteURL')[0],true);
    const isEmailValid = validateEmailMsg(emailID[0], true);
    let contactNumberValid = validateMobileNum($('#contactNumber')[0], true) !== false;
    let alternativeContactNumberValid = validateMobileNum($('#alternateContactNumber')[0], true) !== false;
    if (isFieldsValid && isEmailValid && contactNumberValid && alternativeContactNumberValid && isValidWebsiteUrl) {
        $('.masked').inputmask('');
        $('#collegeForm').submit();
    } else {
        showToast('Error', 'Please fill all the mandatory fields and submit.');
        return false;
    }
}

function validateWebsiteURL(element, req) {
    const urlField = $('#' + element.id);
    const parentDiv = urlField.closest('.col-lg-6'); 
    const errorDiv = parentDiv.find('.invalid-feedback');
    const urlRegex = /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/; // URL validation regex
    if (!!!urlField.val() && req) {
        urlField.addClass(errorClass);
        errorDiv.html(parentDiv.find('.invalid-req-feedback').html());
        errorDiv.removeClass(displayNone).show();
        return false;
    }
    if (!!urlField.val() && !urlRegex.test(urlField.val())) {
        urlField.addClass(errorClass);
        errorDiv.html(parentDiv.find('.invalid-valid-feedback').html());
        errorDiv.removeClass(displayNone).show();
        return false;
    }
    urlField.removeClass(errorClass);
    errorDiv.addClass(displayNone).hide();
    return true;
}

