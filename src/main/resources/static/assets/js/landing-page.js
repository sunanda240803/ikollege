function authenticate() {
    $('#failureText').html('');
    const elementArray = ['#id', '#password'];
    const isValid = valRequiredTextArray(elementArray);

    if (!isValid) {
        return false;
    }

    $('#userName').val($('#id').val() + "~" + $('#loginType').val());
    const username = $('#id').val();
    const password = $('#password').val();
    const loginType = $('#loginType').val();  // Extract the value, not the array
    const loginTypeArray = ['e000611cee6384acac7ce443e422947d', 'a0f8f3571b4696dd2a59ca33b9e1349a', 'dde72d89154d68e1ca7ffd24a9ca2d5e'];

    validateCredentials(username, password, loginType)
        .then(isValid => {
            if (isValid === 'NOT Blocked') {
                // If credentials are valid, submit the form
                document.getElementById('loginForm').submit();
            }else if (loginType === '1b4a53620b60ae418ff44f3fac3213bc') { //  Other Candidate Login
                if(isValid === 'Blocked')
                {
                    $('#error-message').text('Your account has been blocked due to multiple failed attempts. Please contact the System Admin to unblock your account.');
                }
                if (!emailPattern.test(username)) {
                    $('#id').addClass(errorClass).removeClass(validClass);
                } else {
                    $('#id').removeClass(errorClass).addClass(validClass);
                } if(password.length < 6 || password.length === 0){
                    validateOtherLoginPassword(6);
                }else{
                    $('#id').removeClass(validClass);
                    $('#password').removeClass(validClass);
                    $('#error-message').removeClass(displayNone);
                }
            }else if (loginTypeArray.includes(loginType)) { // Student, Staff and Office Login
                if (username === '') {
                    $('#id').addClass(errorClass).removeClass(validClass);
                }if(password.length < 6 || password.length === 0){
                    validateOtherLoginPassword(6);
                }else{
                    $('#id').removeClass(validClass);
                    $('#password').removeClass(validClass);
                    $('#error-message').removeClass(displayNone);
                }
            } else{
                // If invalid, display an error message
                $('#error-message').removeClass(displayNone);
            }
        })
        .catch(error => {
            console.error('Error during validation:', error);
            $('#error-message').removeClass(displayNone).text('Something went wrong. Please try again later');
        });
}

function validateCredentials(username, password, loginType) {
    var formData = new FormData();
    formData.append('userName', username);
    formData.append('password', password);
    formData.append('loginType', loginType);

    return fetch(contextPath + '/validateCredentials', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            console.log("data "+ data);
            /*if (data === 'true') return true;
            else return false;*/
            return data;
        });
}

const validateCaptcha = captchaCode => new Promise((resolve, reject) => {
    const imgToken = localStorage.getItem("captchatoken");

    $.ajax({
        type: "POST", url: contextPath + CAPTCHA_URL, data: {
            token: imgToken, captchaCode: captchaCode // corrected from captchaVal to captchaCode
        }, success: function (response) {
            if (response.code === 200) {
                resolve(true);  // Resolve the promise with true
            } else {
                $(".captchaError").html('<div class="text-danger"> Invalid Captcha </div>');
                $('.registerBtn').attr('disabled', false);
                resolve(false);  // Resolve the promise with false
            }
        }, error: function () {
            $(".captchaError").html('<div class="text-danger"> Invalid Captcha </div>');
            $('.registerBtn').attr('disabled', false);
            reject(false);  // Reject the promise
        }
    });
});

function register() {
    const elementArray = ['#applicantName', '#applicantEmail', '#applicantPassword', '#confirmPassword', '#captchaCode'];
    const isValid = valRequiredTextArrayWithSiblings(elementArray);
    const captchaCodeResetPass = $('#captchaCode').val();
    const $requiredMessage = $("#captchaRequired");

    if (!isValid) {
        $('#passwordRequired').removeClass(displayNone);
        $('#reTypePasswordRequired').removeClass(displayNone);
        $('#invalidPassword').addClass(displayNone).html('');
        $('#invalidReTypePassword').addClass(displayNone).html('');
        $('#invalidEmail').addClass(displayNone).html('');
        return false;
    }
    const passwordValid = validatePasswordLength('#applicantPassword', 6);
    const confirmPasswordValid = validatePasswordLength('#confirmPassword', 6);
    const confirmPassword = $('#confirmPassword').val();
    const applicantPassword = $('#applicantPassword').val();

    if (!passwordValid || !confirmPasswordValid) {
        $('#passwordRequired').addClass(displayNone);
        $('#reTypePasswordRequired').addClass(displayNone);
        applicantPassword.addClass(errorClass).removeClass(validClass);
        confirmPassword.addClass(errorClass).removeClass(validClass);
        $('#invalidPassword').removeClass(displayNone).html('Password should be at least 6 characters');
        $('#invalidReTypePassword').removeClass(displayNone).html('Re-type Password should be at least 6 characters');
        return false;
    }
    if (confirmPassword !== applicantPassword) {
        $('#reTypePasswordRequired').addClass(displayNone);
        $('#passwordRequired').addClass(displayNone);
        $('#confirmPassword').addClass(errorClass);
        applicantPassword.addClass(errorClass).removeClass(validClass);
        confirmPassword.addClass(errorClass).removeClass(validClass);
        $("#invalidReTypePassword").removeClass("d-none").html("Password and Re-type Password must be same")
        return false;
    }
    const validatePwdArray = ['#applicantPassword', '#confirmPassword'];
    const isValidPwd = validatePasswordMatch(validatePwdArray);
    if (!isValidPwd) {
        return false;
    }
    if (checkEmailIdExistAndValid($('#applicantEmail').val())) {
        return false;
    }


    // Set the username value
    $('#userName').val($('#applicantEmail').val() + "~" + $('#loginType').val());
    if ($('#applicantEmail').val() !== '') {
        if (validateEmail($('#applicantEmail'), true) > 0) {
            $('#applicantEmailError').html('Invalid Email Address');
            return false;
        }
    }
    // Asynchronous CAPTCHA validation
    validateCaptcha(captchaCodeResetPass).then(isCaptchaValid => {
        if (isCaptchaValid) {

            $('#registerForm').submit();
        } else {
            $requiredMessage.removeClass(displayNone);
            $('#captchaCode').addClass(errorClass).removeClass(validClass)
            $(".captchaError").addClass(errorClass).html('<div class="text-danger"> Invalid Captcha </div>');
            $('.registerBtn').attr('disabled', false);
        }
    }).catch(error => {
        console.error('CAPTCHA validation failed:', error);
        $('#errorDiv').html('CAPTCHA validation failed. Please try again.');
    });

    return false;
}

function checkEmailIdExistAndValid(emailId) {
    if (emailId !== '') {
        // Validate Email Format
        if (!emailPattern.test(emailId)) {
            $('#applicantEmail').addClass(errorClass);
            $('#invalidEmail').removeClass(displayNone).html('Invalid email format');
            $('#emailIdRequired').addClass("d-none");
            $("#applicantEmail").val('');
            return false;
        }else{
            $('#applicantEmail').removeClass(errorClass).addClass(validClass);
        }
        // Validate Email exist or not
        $.ajax({
            url: contextPath + checkEmailIdExistURL,
            type: "POST",
            async: false,
            data: {
                "emailId": emailId
            },
            success: function (response) {
                console.log(response);
                if (response === true) {
                    $('#applicantEmail').addClass(errorClass);
                    $('#invalidEmail').removeClass(displayNone).html('Email ID already exists');
                    $('#emailIdRequired').addClass("d-none");
                    $("#applicantEmail").val('');
                    return true;
                } else {
                    $('#emailIdRequired').html('Email ID is required');
                    $('#applicantEmail').removeClass(errorClass);
                    return false;
                }
            },
            error: function () {
                showToast('Error', 'An error occurred');
            }
        });
    } else {
        $('#emailIdRequired').html('Email ID is required');
        $('#applicantEmail').addClass(errorClass);
        return false;
    }
    return false;
}


(function () {
    function getCaptchaCode() {
        var captchaElement = document.getElementById("captcha");

        if (!captchaElement) {
            console.error("Captcha element not found.");
            return;
        }

        // Resetting captcha and related fields
        captchaElement.src = '';
        $('#captchaCode').val('');
        $(".captchaError").html('');
        $('#registerBtn').attr('disabled', false);
        // Making AJAX call to fetch captcha
        $.ajax({
            type: "GET", url: contextPath + CAPTCHA_URL, success: function (response) {
                if (response && response.token && response.image) {
                    // Store captcha token and display image
                    localStorage.setItem("captchatoken", response.token);
                    captchaElement.src = "data:image/png;base64," + response.image;
                } else {
                    console.error("Invalid response received.");
                }
            }, error: function (res) {
                console.error("Error:", res);
                $(".captchaError").html('Failed to load captcha. Please try again.');
            }
        });
    }

    var captchaElement = document.getElementById("captcha");
    var anchorElements = document.querySelectorAll('.captchaBox');
    var captchaCodeCalled = false;

    if (captchaElement) {
        // Add click event listeners to elements in captcha_box
        anchorElements.forEach(function (anchorElement) {
            anchorElement.addEventListener('click', function () {
                if (!captchaCodeCalled) {
                    getCaptchaCode();
                    captchaCodeCalled = true;
                }
            });
        });

        // Fetch captcha code on page load if not already done
        if (!captchaCodeCalled) {
            getCaptchaCode();
            captchaCodeCalled = true;
        }
    }
    window.getCaptchaCode = getCaptchaCode;
})();

$('#forgotPasswordSave').click(function() {
    const emailField = $('#applicantEmail');
    const errorDiv = emailField.siblings('.invalid-feedback');

    if (validateEmail(emailField, true)) {
        return false;
    }

    $.ajax({
        type: "POST",
        url: contextPath + forgotPasswordURL,
        data: {
            "applicantEmail": emailField.val(),
        },
        success: function(response) {
            if (response === 'success') {
                // Optionally show a confirmation message
                $('#forgotPassword').modal('hide');
                $('#successModal').modal('show');
                errorDiv.addClass(displayNone);
                emailField.addClass(validClass).removeClass(errorClass);
            } else if (response === 'Password Mismatch') {
                $('#forgotPassword').modal('show');
                errorDiv.removeClass(displayNone);
                errorDiv.html(errorDiv.siblings('.invalid-valid-feedback').html());
                emailField.addClass(errorClass).removeClass(validClass);
                // $('#invalidEmail').removeClass(displayNone).html('Invalid Registered Email ID');
                return false;
            }
        },
        error: function() {
            $(".invalid-feedback").html("Error in updating the password");
        }
    });
});


window.addEventListener("load", function () {
    const params = new URLSearchParams(window.location.search);
    if (params.has("error")) {
        window.history.replaceState({}, document.title, window.location.pathname);
    }
});
