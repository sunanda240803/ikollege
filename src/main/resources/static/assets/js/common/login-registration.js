$(document).ready(function() {
    //Login and Registration onblur validation
    $(document).on('blur', '.email-exist-valid', function () {
        checkEmailIdExistAndValid(this.value);
    });
    $(document).on('blur', '.captcha-validation', function () {
        handleCaptchaValidation(this.value);
    });
    $(document).on('blur', '.login-email-validation', function () {
        validateOtherLoginEmail(true);
    });
    $(document).on('blur', '.login-password-validation', function () {
        validateOtherLoginPassword(6);
    });
    $('#id, #password').on("keypress", function(event) {
        if (event.originalEvent.key === 'Enter') {
            $('#loginBtn').click();
        }
    })
});