const errorClass = 'is-invalid';
const validClass = 'is-valid';
const displayNone  = 'd-none';
const emailPattern = /^\b[A-Z0-9._%-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b$/i;
const alphabetAndNumPattern = /^[a-zA-Z0-9]+$/;
const alphabetAndTwoCharPattern = /^[a-zA-Z]{2,}(?:\s[a-zA-Z]+)*$/;
const alphabetPattern = /^[a-zA-Z]+(?:\s[a-zA-Z]+)*$/;
const numberPattern = /^[0-9.]+$/;
const mobileNumberPattern = /^[0-9]{10}$/;
const maskedMobileNumberPattern = /^[0-9]{3}-[0-9]{3}-[0-9]{4}$/;
const aadhaarNumberPattern = /^[0-9]{4}-[0-9]{4}-[0-9]{4}$/;
const panPattern = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/;
const urlPattern = /^(?:(https?|ftp):\/\/)?(?:[\w-]+\.)+[\w-]{2,}(?:\/[\w/_.:=?+&%#-]{0,})(\?\w=[\w/_.:=?+&%#-]{0,})?(#[^ ]*)?$/i;
const alphaNumMax20Pattern = /^[A-Za-z0-9]{1,20}$/;
const numberWithTwoDecimalsPattern = /^\d+(\.\d{1,2})?$/;
const threeDigitsTwoDecimalsPattern = /^\d{1,3}(\.\d{1,2})?$/;
const fiveDigitsThreeDecimalsPattern = /^\d{1,5}(\.\d{1,3})?$/;
const tenDigitsTwoDecimalsPattern = /^\d{1,10}(\.\d{1,2})?$/;
const numeric = /[0-9]/;
const alphabetsAndNumeric = /^[a-zA-Z0-9]+$/;
const alphabetic = /[a-zA-Z]/;
const invalidFeedback = '.invalid-feedback';
const invalidFeedbackReq = '.invalid-feedback-req';
const dNone = 'd-none';
const digit =/^[1-9][0-9]{0,7}$/;
const alphabetNumCommaPattern = /^[a-zA-Z0-9,]+$/;

$(document).ready(function() {
	// Use the class 'isAlphaAndSpace' to restrict input to alphabetic characters and spaces
	$('input.isAlphaAndSpace').on('keypress', isAlphaAndSpace);

	// Use the class 'isAlphanumeric' to restrict input to alphabetic and numbers
	$('input.isAlphanumeric').on('keypress', isAlphanumeric);

	// Use the class 'isNumberAndDot' to restrict input to number and a dot
	$('input.isNumberAndDot').on('keypress', isNumberAndDot);

	// Use the class 'addressForm' to restrict input to address form
	$('textarea.addressForm').on('keypress', addressForm);

	$(document).on('keypress', '.numeric', function(event) {
		return isNumberKey(event);
	})

	// Get today's date in YYYY-MM-DD format
	var today = new Date().toISOString().split('T')[0];
	// Use the class 'no-future-date' to restrict input to dates up to today
	$('input.no-future-date').each(function() {
		$(this).attr('max', today);
	});
	$('input.future-date').each(function() {
		$(this).attr('min', today);
	});

	//Common onblur validation
	$(document).on('blur', '.required-input', function () {
		validateRadioAndText(this);
	});
	$(document).on('change', '.required-select-input', function () {
		valRequiredSelect(this);
	});
	$(document).on('blur', '.required-mob-num', function () {
		validateMobileNumberPatternDOM(this, true);
		validateMobileNum(this);
	});
	$(document).on('blur', '.required-email', function () {
		validateEmailMsg(this, true);
	});
	$(document).on('blur', '.required-number', function () {
		validateNumberPatternDOM(this, true);
	});
	$(document).on('blur', '.required-aadhaar', function () {
		validateAadhaarNumberPatternDOM(this, true);
	});
	$(document).on('blur', '.required-pan', function () {
		validatePANPatternDOM(this, true);
	});
	$(document).on('blur', '.password-match', function () {
		const validatePwdArray = ['#applicantPassword', '#confirmPassword'];
		validatePasswordMatch(validatePwdArray);
	});
	$(document).on('blur', '.password-length', function () {
		validatePasswordLength(this,6,'passwordError');
	});

	//Mess modal onblur validation
	$(document).on('blur', '.mess-name-validation', function () {
		checkMessNameExist();
	});
	$(document).on('blur', '.mess-head-validation', function () {
		checkMessHeadExist();
	});

	//Hostel modal onblur validation
	$(document).on('blur', '.hostel-name-validation', function () {
		checkHostelNameExist();
	});
	$(document).on('blur', '.hostel-shot-code-validation', function () {
		checkHostelShortCodeExist();
	});
	$(document).on('blur', '.asset-config-validation', function () {
		validateAssetConfigName(this);
	});

	$(document).on('blur', '.asset-shortCode-validation', function () {
		validateAssetShortCode(this);
	});
	$(document).on('blur', '.asset-cost-validation', function () {
		validateDecimalInput(this,2);
	});


	//Floor modal onblur validation
	$(document).on('blur', '.floor-name-validation', function () {
		checkFloorNameExist();
	});
	$(document).on('blur', '.hostel-username-validation', function () {
		validateUser();
	});


	//Course modal onblur validation
	$(document).on('blur', '.course-name-validation', function () {
		checkCourseNameExist();
		valRequiredText(this);
	});
	$(document).on('blur', '.course-code-validation', function () {
		checkCourseCodeExist();
		valRequiredText(this);
	});
	$(document).on('blur', '.date-validation', function () {
		validateDateErrMsg(this, null);
	});

});

function handleCaptchaValidation(inputElement) {
	const captchaCode = inputElement.value;
	validateCaptcha(captchaCode);
}

function validateOtherLoginEmail(req) {
	const username = $('#id'); // Make sure this is a jQuery object
	const loginType = $('#loginType').val();  // Extract the value, not the array
	let errorDiv = username.siblings('.invalid-feedback');

	if (loginType === '1b4a53620b60ae418ff44f3fac3213bc') { // Replace 'other' with the actual value representing Other Candidate Login
		if (!username.val() && req) {
			username.addClass(errorClass).removeClass(validClass);
			errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
			return false;
		} else if (!emailPattern.test(username.val())) {
			username.addClass(errorClass).removeClass(validClass);
			errorDiv.html(errorDiv.siblings('.invalid-valid-feedback').html());
			return false;
		} else {
			username.addClass(validClass).removeClass(errorClass);
			errorDiv.html(errorDiv.siblings('.invalid-feedback').html());
		}
	}
	return true;
}
function validateOtherLoginPassword(minLength) {
	const password = $('#password');
	let errorPasswordDiv = password.siblings('.invalid-feedback');

	// Check if the password field is empty
	if (!password.val()) {
		password.addClass(errorClass).removeClass(validClass);
		errorPasswordDiv.html(errorPasswordDiv.siblings('.invalid-feedback').html());
		return false;
	}
	// Check if the password length is less than minLength
	else if (password.val().length < minLength) {
		password.addClass(errorClass).removeClass(validClass);
		errorPasswordDiv.html(errorPasswordDiv.siblings('.invalid-length-feedback').html());
		return false;
	} else {
		password.addClass(validClass).removeClass(errorClass);
		// errorPasswordDiv.html(errorPasswordDiv.siblings('.invalid-feedback').html());
	}
	return true;
}
function validateMultipleEmailField(emailField) {
	let isEmailValid = true;
	if (emailField.length && emailField.val().trim() !== '') {
		const emails = emailField.val().split(',').map(email => email.trim());
		for (const email of emails) {
			emailField.val(email);
			if (!validateEmailMsg(emailField[0], true)) {
				isEmailValid = false;
			}
		}
		emailField.val(emails.join(', '));
	} else {
		if (emailField.attr('required')) {
			isEmailValid = false;
			validateEmailMsg(emailField[0], true);
		}
	}
	console.log(isEmailValid);
	return isEmailValid;
}

function clearInput(ids) {
	const id = $(this).val();
	if ($(ids) !== ''|| $(ids) !== '0') {
		$('.select-picker-validation').addClass(displayNone);
	}else{
		$('.select-picker-validation').removeClass(displayNone);
	}
}

function fieldEmpty(jQueryField) {
	jQueryField.removeClass(errorClass);
	jQueryField.removeClass(validClass);
	if (jQueryField.val() === '') {
		jQueryField.addClass(errorClass)
		return 1;
	} else {
		jQueryField.addClass(validClass)
	}
	return 0;
}

function fieldFilled(jQueryField) {
	jQueryField.removeClass(errorClass)
	if (jQueryField.val() !== '') {
		jQueryField.addClass(validClass)
	}
}

function fieldSelected(jQueryField) {
	jQueryField.removeClass(errorClass)
	if (jQueryField.val() !== '0') {
		jQueryField.addClass(validClass)
	}
}

function fieldFilledByParent(jQueryParent) {
	$.each(jQueryParent.find('input'), function() {
		fieldFilled($(this));
	})
	$.each(jQueryParent.find('select'), function() {
		fieldSelected($(this));
	})
}

function validateSelfDecCheckboxes(jQueryField, required, declarationClass) {
	const checkbox = $(jQueryField);
	const isChecked = checkbox.is(':checked');
	if (!isChecked && required) {
		checkbox.addClass(errorClass).removeClass(validClass);
		checkbox.next('.invalid-feedback').show();
	} else {
		checkbox.addClass(validClass).removeClass(errorClass);
		checkbox.next('.invalid-feedback').hide();
	}
	const allCheckboxes = $(declarationClass);
	const allChecked = allCheckboxes.length === allCheckboxes.filter(':checked').length;

	if (allChecked) {
		allCheckboxes.closest('.col-lg-12').find('.invalid-feedback').hide();
		allCheckboxes.removeClass(errorClass).addClass(validClass);
	}
}


function hideInvalidFeedback(checkboxId) {
	var checkbox = document.getElementById(checkboxId);
	var feedback = document.getElementById(checkboxId + 'Feedback');

	if (checkbox.checked) {
		feedback.classList.add(displayNone);  // Hide the invalid-feedback if the checkbox is checked
	}
}





function validateRadio(jQueryField, required) {
	jQueryField.removeClass(errorClass)
	let returnStat = 0;
	let valueCheck = false;
	jQueryField.find('input').each(function() {
		if ($(this).is(":checked") === true) {
			valueCheck = true;
		}
	});

	if (valueCheck) {
		jQueryField.addClass(validClass)
	} else if (required) {
		jQueryField.addClass(errorClass)
		returnStat++;
	}
	return returnStat;
}

function validateEmailDOM(element, required) {
	return validateEmail($('#' + element.id), required);
}

function validateEmail(jQueryField, required) {
	jQueryField.removeClass(errorClass).removeClass(validClass)
	let returnStat = 0;
	if (required) {
		returnStat = fieldEmpty(jQueryField)
	}
	if (returnStat === 0) {
		if (jQueryField.val() && jQueryField.val().trim() !== "") {
			const fieldValue = jQueryField.val().trim();

			if (!emailPattern.test(fieldValue)) {
				jQueryField.addClass(errorClass)
				returnStat++;
			} else {
				jQueryField.addClass(validClass)
			}
		}
	}
	return returnStat;
}


function emailValidating(element, req) {
	const emailId = $('#' + element.id);
	const errorDiv = emailId.siblings('.invalid-feedback');

	// Check if the email is required and empty
	if (!!!emailId.val() && req) {
		emailId.addClass(errorClass);  // Assuming 'is-invalid' is your error class
		errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
		return false;  // Return false if the field is required and empty
	}
	// Check if the email format is invalid (assuming validateEmailDOM handles the format validation)
	if (!!emailId.val() && !validateEmailDOM(element)) {
		emailId.addClass(errorClass);  // Add error class for invalid email
		errorDiv.html(errorDiv.siblings('.invalid-valid-feedback').html());
		return false;  // Return false if the email format is invalid
	}
	// If no errors occurred, do not remove the invalid class and return true
	return true;
}


function validateEmailManage(jQueryField, required) {
	jQueryField.removeClass(errorClass);
	let returnStat = 0;

	const fieldValue = jQueryField.val().trim(); // Trim the field to remove extra spaces.

	if (fieldValue.length === 0) {
		// If the field is empty, and it's not required, allow the form to be saved.
		return returnStat;
	} else {
		// If the field is not empty, validate the email format.
		if (!emailPattern.test(fieldValue)) {
			jQueryField.addClass(errorClass); // Add the error class to indicate a problem.
			returnStat++;
		} else {
			jQueryField.addClass(validClass); // Add the valid class to indicate it's correct.
		}
	}

	return returnStat;
}

function validateAge(jQueryField, required, minimumAgeInYears, maximumAgeInYears) {
	jQueryField.removeClass(errorClass)

	let returnStat = 0;
	if (required) {
		returnStat = fieldEmpty(jQueryField)
	}
	if (returnStat === 0) {
		const fieldValue = jQueryField.val();
		const dob = new Date(fieldValue);
		const today = new Date();
		const age = Math.floor((today - dob) / (365.25 * 24 * 60 * 60 * 1000));
		if ((minimumAgeInYears !== undefined && age < minimumAgeInYears) ||
			(maximumAgeInYears !== undefined && age > maximumAgeInYears)) {
			jQueryField.addClass(errorClass)
			returnStat++;
		} else {
			jQueryField.addClass(validClass)
		}
	}
	return returnStat;
}

function validateRadioAndText(ids) {
	var isValid = true;

	$(ids).each(function() {
		var field = $(this);
		const templateField = field.attr('data-template');
		if (templateField === undefined || templateField === false) {
			if (field.is(':radio')) {
				var radioGroup = $('input[name="' + field.attr('name') + '"]');
				var isChecked = radioGroup.is(':checked');

				if (!isChecked) {
					field.closest('.checkRadio-bg').removeClass(validClass);
					field.closest('.checkRadio-bg').addClass(errorClass);
					isValid = false;
				} else {
					field.closest('.checkRadio-bg').removeClass(errorClass);
					field.closest('.checkRadio-bg').addClass(validClass);
				}
			} else if (field.is('select')) {
				// Check if the dropdown has a selected value
				if (field.val() === '0' || field.val() === '') {
					field.removeClass(validClass);
					field.addClass(errorClass);
					isValid = false;
				} else {
					field.removeClass(errorClass);
					field.addClass(validClass);
				}
			} else if (field.is('textarea')) {
				if (field.val() === '') {
					field.removeClass(validClass);
					field.addClass(errorClass);
					isValid = false;
				} else {
					field.removeClass(errorClass);
					field.addClass(validClass);
				}
			} else if (field.is(':checkbox')) {
				if (field.is(':checked')) {
					field.removeClass(errorClass);
					field.addClass(validClass);
				} else {
					field.removeClass(validClass);
					field.addClass(errorClass);
					isValid = false;
				}
			}
			else if (field.attr('type') === 'file') {
				if (field.closest('.d-none').length === 0) {
					if (field.attr('data-file-type') === 'img') {
						let img = field.siblings().find('img');

						$(img).each(function () {
							if ($(this).attr('for') === field.attr('id')) {
								let actualSrc = $(this).attr('src') || '';
								let actualFileName = actualSrc.split('/').pop();
								let defaultImageName = 'id-proof.png';

								if (actualFileName === '' || actualFileName === defaultImageName) {
									field.addClass(errorClass);
									field.removeClass(validClass);
									isValid = false;
								}
							}
						});
					} else {
						// Check if the file input is empty
						if (!field.val() || field[0].files.length === 0) {
							field.addClass(errorClass);
							field.removeClass(validClass);
							isValid = false;
						}
					}
				}
			}
			else {
				if ($.trim(field.val()) === '') {
					field.addClass(errorClass);
					field.removeClass(validClass);
					isValid = false;
				} else {
					field.removeClass(errorClass);
					field.addClass(validClass);

					// For email field, validate email format
					if (field.attr('type') === 'email') {
					    var email = field.val().trim();
					    var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

					    // Find the nearest feedback messages for this particular field
					    var feedbackContainer = field.closest('.form-group');
					    var invalidFeedback = feedbackContainer.find('.invalid-feedback');
					    var invalidEmailFeedback = feedbackContainer.find('.invalid-valid-feedback');

					    if (!emailRegex.test(email)) {
					        field.removeClass(validClass).addClass(errorClass);
							invalidFeedback.html(invalidEmailFeedback.html());
					        isValid = false;
					    } else {
					        field.removeClass(errorClass).addClass(validClass);
					    }
					}
				}
			}
		}
	});

	return isValid;
}

function valRequiredMultiTextRadio(ids) {
	var isValid = validateRadioAndText(ids);
	return isValid;
}


function valRequiredText(element) {
	const input = $('#' + element.id)
	let isValid = true;
	if (!input.attr('readonly') && $.trim($(element).val()) === '') {
		$(element).addClass(errorClass);
		isValid = false;
	} else {
		$(element).removeClass(errorClass);
		$(element).addClass(validClass);
	}
	return isValid;
}

function valRequiredMultiText(ids) {
	let isValid = true;
	$(ids).each(function() {
		if ($.trim($(this).val()) === '') {
			$(this).addClass(errorClass);
			isValid = false;
		} else {
			$(this).removeClass(errorClass);
			$(this).addClass(validClass);
		}
	});
	return isValid;
}

function valRequiredSelect(element) {
	const input = $('#' + element.id)
	let isValid = true;
	let value = $.trim($(element).val());
	if (!input.attr('readonly') && (value === '' || value === '0')) {
		$(element).addClass(errorClass);
		if ($(element).hasClass("selectpicker")) {
			$(element).parent().removeClass(validClass).addClass(errorClass);
		}
		isValid = false;
	} else {
		if ($(element).hasClass("selectpicker")) {
			$(element).parent().removeClass(errorClass).addClass(validClass);
		}
		$(element).removeClass(errorClass).addClass(validClass);
	}
	return isValid;
}

function valRequiredMultiSelect(ids) {
	let isValid = true;
	$(ids).each(function() {
		if ($.trim($(this).val()) === '' || $.trim($(this).val()) === '0') {
			$(this).addClass(errorClass);
			if ($(this).hasClass("selectpicker")) {
				$(this).parent().removeClass(validClass).addClass(errorClass);
			}
			isValid = false;
		} else {
			if ($(this).hasClass("selectpicker")) {
				$(this).parent().removeClass(errorClass).addClass(validClass);
			}
			$(this).removeClass(errorClass).addClass(validClass);
		}
	});
	return isValid;
}

function valRequiredSelection(ids) {
	let isValid = true;
	$(ids).each(function() {
		if ($.trim($(this).val()) === '' || ($(this).val()) === '0') {
			$(this).addClass(errorClass);
			isValid = false;
		} else {
			$(this).removeClass(errorClass);
			$(this).addClass(validClass);
		}
	});
	return isValid;
}

function validateDropdown(jQueryField) {
	jQueryField.removeClass(errorClass)
	let returnStat = 0;
	if (jQueryField.val() !== '0' || jQueryField.val() !== '') {
		jQueryField.addClass(validClass)
	} else {
		jQueryField.addClass(errorClass)
		returnStat++;
	}
	return returnStat;
}

function validateDate(element) {
	const inputDate = new Date($(element).val());
	const today = new Date();

	let isValid = true;
	if (inputDate > today) {
		$(element).addClass(errorClass);
		isValid = false;
	} else {
		$(element).removeClass(errorClass);
		$(element).addClass(validClass);
	}
	return isValid;
}

function validateByPattern(jQueryField, required, pattern) {
	if (jQueryField.length > 0) {
		jQueryField.removeClass(errorClass)
		jQueryField.removeClass(validClass)
		let returnStat = 0;
		if (required) {
			returnStat = fieldEmpty(jQueryField)
		}
		if (returnStat === 0 && jQueryField.val() !== '') {
			const fieldValue = jQueryField.val();
			const cleanedString = fieldValue.trim();
			if (!pattern.test(cleanedString)) {
				jQueryField.addClass(errorClass)
				returnStat++;
			} else {
				jQueryField.addClass(validClass)
			}
		}
		return returnStat;
	}
	return 0;
}

function validateAlphabetAndTwoCharPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, alphabetAndTwoCharPattern);
}

function validateAlphabetPattern(jQueryField) {
	return validateByPattern(jQueryField, false, alphabetPattern);
}

function validateAlphabetAndNumPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, alphabetAndNumPattern);
}

function validateNumberPatternDOM(domElement, required) {
	return validateNumberPattern($('#' + domElement.id), required);
}

function validateNumberPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, numberPattern);
}

function validateMobileNumberPatternDOM(domElement, required) {
	return validateMaskedMobileNumberPattern($('#' + domElement.id), required);
}

function validateMaskedMobileNumberPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, maskedMobileNumberPattern);
}

function validateMobileNumberPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, mobileNumberPattern);
}

function validateAadhaarNumberPatternDOM(domElement, required) {
	return validateAadhaarNumberPattern($('#' + domElement.id), required);
}

function validateAadhaarNumberPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, aadhaarNumberPattern);
}

function validatePANPatternDOM(domElement, required) {
	return validatePANPattern($('#' + domElement.id), required);
}

function validatePANPattern(jQueryField, required) {
	if (jQueryField.length > 0 && jQueryField.val().length > 0) {
		jQueryField.val(jQueryField.val().toUpperCase());
		return validateByPattern(jQueryField, required, panPattern);
	}
	return 0;
}

function validateURLPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, urlPattern);
}

function validateAlphaNumMax20Pattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, alphaNumMax20Pattern);
}

function validateNumberWithTwoDecimalsPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, numberWithTwoDecimalsPattern);
}

function validateNumberWithThreeDecimalsUpToFiveDigitsPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, fiveDigitsThreeDecimalsPattern);
}

function validateNumberUpToOneBillionPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, tenDigitsTwoDecimalsPattern);
}

function validateThreeDigitsTwoDecimalsPattern(jQueryField, required) {
	return validateByPattern(jQueryField, required, threeDigitsTwoDecimalsPattern);
}

function validatePasswordFields(elementArray) {
	let status = true;

	elementArray.forEach(function(element) {
		let input = $('#' + element.id);
		if (!input.attr('readonly') && $.trim($(element).val()) === '') {
			$(element).addClass(errorClass);
			return status = false;
		} else if (!input.attr('readonly') && $.trim($(element).val()).length < 3) {
			document.getElementById('validate' + element.replaceAll('#', ''))
				.textContent = 'Password must be at least 3 characters long';
			$(element).addClass(errorClass);
			return status = false;
		} else {
			$(element).removeClass(errorClass);
		}
	});

	if (status) {
		const currentPassword = $('#currentPassword').val();
		const newPassword = $('#newPassword').val();
		const reTypePassword = $('#reTypePassword').val();

		if (currentPassword && newPassword === currentPassword) {
			$('#newPassword').addClass(errorClass);
			$('#validatenewPassword').text('Current password and New password should not be the same');
			status = false;
		} else if (newPassword !== reTypePassword) {
			$('#reTypePassword').addClass(errorClass);
			$('#validatereTypePassword').text('Passwords do not match new password');
			status = false;
		} else {
			$('#currentPassword, #newPassword, #reTypePassword').removeClass(errorClass);
		}
	}

	return status;
}

function password_show_hide(element) {
	const passwordField = document.getElementById(element);

	const iconTag = document.getElementById(element + 'Icon');
	if (passwordField.type === "password") {
		passwordField.type = "text";
		iconTag.removeAttribute('class');
		iconTag.setAttribute("class", "fa-solid fa-eye");
	} else {
		passwordField.type = "password";
		iconTag.removeAttribute('class');
		iconTag.setAttribute("class", "fa-solid fa-eye-slash");
	}
}

function validateFileSize(element) {
	const files = element.files;
	let totalSize = 0;
	for (i = 0; i < files.length; i++) {
		totalSize += files[i].size;
	}

	const maxSize = parseInt($('#maxFileUploadSize').val()) * 1024 * 1024;
	if (totalSize > maxSize) {
		showToast('Error', 'Uploaded file size exceeded more than ' + $('#maxFileUploadSize').val() + ' MB');
		return false;
	}
	return true;

}


function isNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	// Allow digits (0-9)
	if (charCode >= 48 && charCode <= 57) {
		return true;
	}
	return false;
}

function isNumberKeyWithDecimal(evt) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	// Allow digits (0-9)
	if (charCode >= 48 && charCode <= 57) {
		return true;
	}
	// Allow only one decimal point (.)
	if (charCode == 46) {
		var inputValue = evt.target.value;
		if (inputValue.indexOf('.') === -1) {
			return true;
		}
	}
	return false;
}

function checkInputLength(input) {
	var maxDigits = input.getAttribute('data-maxlength');
	; // Maximum number of digits before the decimal point
	var value = input.value;

	// Split the value into two parts: before and after the decimal point
	var parts = value.split('.');
	if (parts[0].length > maxDigits) {
		// Limit the digits before the decimal point
		input.value = parts[0].substring(0, maxDigits) + (parts[1] ? '.' + parts[1] : '');
	}

	// Limit to two decimal places
	if (parts[1] && parts[1].length > 2) {
		input.value = parts[0] + '.' + parts[1].substring(0, 2);
	}
}

function validateNumberInput(inputElement) {
	let value = inputElement.value;

	// Allow only numbers and one dot
	const validValue = value.replace(/[^0-9.]/g, "");

	// Ensure only one dot and numbers after the dot
	const parts = validValue.split(".");
	if (parts.length > 2) {
		inputElement.value = parts[0] + "." + parts[1];f
	} else {
		inputElement.value = validValue;
	}
}


function validateTextLength(jQueryField, maxLength) {
	var value = $(jQueryField).val();
	if (value.length > maxLength) {
		$(jQueryField).addClass(errorClass);
		return false;
	} else {
		$(jQueryField).removeClass(errorClass);
		return true;
	}
}

function valRequiredTextArray(elementArray) {
	let isValid = true;
	elementArray.forEach(function(element) {
		let input = $('#' + element.id);
		if (!input.attr('readonly') && $.trim($(element).val()) === '') {
			$(element).addClass(errorClass);
			isValid = false;
		} else {
			$(element).removeClass(errorClass);
		}
	});
	return isValid;
}

function validatePasswordMatch(passwordFields) {
	if (passwordFields.length < 2) {
		console.error("At least two password fields are required for validation.");
		return false;
	}
	const password = $(passwordFields[0]).val();
	const confirmPassword = $(passwordFields[1]).val();
	if (password === confirmPassword) {
		$(passwordFields[1]).addClass(validClass).removeClass(errorClass);
		return true;
	} else {
		$('#reTypeId').html('Password and Re-type password mismatch');
		$(passwordFields[1]).addClass(errorClass).removeClass(validClass);
		return false;
	}
}

function clearFormElements(formId) {
	const form = document.getElementById(formId);
	form.reset();
	//clear invalid messages
	const invalidElements = form.querySelectorAll('.is-invalid');
	invalidElements.forEach(function(element) {
		element.classList.remove(errorClass);
	});
}


function validateEmailInput(element) {
	const email = $('#' + element).val().trim();
	const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

	let status = false;

	if (!regex.test(email)) {
		$('#validateEmail').html("Enter a valid email");
		$('#' + element).addClass(errorClass);
		return status;
	} else {
		$('#' + element).removeClass(errorClass);
		status = true;
	}

	return status;
}

function validatePasswordLength(jQueryField, minLength) {
	const value = $(jQueryField).val();
	if(value.length === 0){
		$('#passwordRequired').removeClass(displayNone).html('Password is Required');
		$(jQueryField).addClass(errorClass).removeClass(validClass);
		return false;
	}else if (value.length < minLength) {
		$('#passwordRequired').addClass(displayNone);
		$('#invalidPassword').removeClass(displayNone).html('Password Should be at least '+minLength+' characters');
		$(jQueryField).addClass(errorClass).removeClass(validClass);
		return false;
	}else {
		$('#passwordRequired').removeClass(displayNone);
		$('#invalidPassword').addClass(displayNone);
		$(jQueryField).removeClass(errorClass).addClass(validClass);
		return true;
	}
}

function validateNumbersAndDot(input) {
        // Regular expression to allow only numbers and dots
        const regex = /^[0-9.]*$/;
        if (!regex.test(input.value)) {
            // Remove any invalid characters
            input.value = input.value.replace(/[^0-9.]/g, '');
        }
    }

function isAlpha(event) {
	const charCode = event.which || event.keyCode;
	// Allow letters (A-Z, a-z)
	if ((charCode >= 65 && charCode <= 90) ||  // A-Z
		(charCode >= 97 && charCode <= 122)) { // a-z
		return true;
	}
	return false;
}

function isAlphaAndSpace(event) {
	const charCode = event.which || event.keyCode;
	// Allow letters (A-Z, a-z) and space
	if ((charCode >= 65 && charCode <= 90) ||  // A-Z
		(charCode >= 97 && charCode <= 122) || // a-z
		charCode === 32) {                     // space
		return true;
	}
	event.preventDefault();
}

function isAlphanumeric(event) {
	const charCode = event.which || event.keyCode;
	// Allow letters (A-Z, a-z) and numbers (0-9)
	if ((charCode >= 48 && charCode <= 57) ||  // 0-9
		(charCode >= 65 && charCode <= 90) ||  // A-Z
		(charCode >= 97 && charCode <= 122)) { // a-z
		return true;
	}
	return false;
}

function isNumberAndDot(event) {
	const charCode = event.which || event.keyCode;
	const inputValue = event.target.value;

	// Numbers (0-9) and dot (char code 46)
	if ((charCode >= 48 && charCode <= 57) || charCode === 46) {
		// Only allow one dot (.)
		if (charCode === 46 && inputValue.includes('.')) {
			return false;
		}
		return true;
	}
	return false;
}

function addressForm(event) {
	const charCode = event.which || event.keyCode;
	// Allow letters (A-Z, a-z), numbers (0-9), and specified special characters
	if ((charCode >= 48 && charCode <= 57) ||  // 0-9
		(charCode >= 65 && charCode <= 90) ||  // A-Z
		(charCode >= 97 && charCode <= 122) || // a-z
		charCode === 35 ||   // #
		charCode === 47 ||   // /
		charCode === 45 ||   // -
		charCode === 40 ||   // (
		charCode === 41 ||   // )
		charCode === 44 ||   // ,
		charCode === 46 || charCode == 32) {   // .
		return true;
	}
	return false;
}

// Validate input to allow only the specified number of decimal places
function validateDecimalInput(input, decimalPlaces) {
	const value = input.value.trim(); // Trim whitespace

	// Skip validation if the input is empty
	if (value === '') {
		$(input).removeClass(errorClass).removeClass(validClass);
		return; // Exit the function
	}

	const regex = new RegExp(`^\\d*(\\.\\d{0,${decimalPlaces}})?$`);
	if (!regex.test(value)) {
		$(input).addClass(errorClass).removeClass(validClass);
	} else {
		$(input).removeClass(errorClass).addClass(validClass);
	}

	// Check if there's a decimal and limit decimal places
	if (value.includes('.')) {
		const parts = value.split('.');
		const decimalPart = parts[1];

		if (decimalPart && decimalPart.length > decimalPlaces) {
			// Slice the decimal part to the specified number of digits
			input.value = parts[0] + '.' + decimalPart.slice(0, decimalPlaces);
		}
	}
}

function validateDecimalLength(inputElement, maxDigits) {
	const value = $(inputElement).val();
	// Create a regex pattern dynamically based on maxDigits
	const pattern = new RegExp(`^\\d{1,${maxDigits}}(\\.\\d{0,2})?$`);
	if (pattern.test(value)) {
		$(inputElement).removeClass(errorClass).addClass(validClass);
		return 0;
	} else {
		$(inputElement).removeClass(validClass).addClass(errorClass);
		return 1; 
	}
}

function valRequiredTextArrayWithSiblings(elementArray) {
	let allValid = true;

	elementArray.forEach(function(selector) {
		const field = $(selector);
		const value = field.val().trim();
		const errorField = field.siblings('.invalid-feedback');

		if (value === '') {
			// If the field is empty, show the error message
			errorField.removeClass(displayNone);
			field.addClass(errorClass); // Mark the input as invalid
			allValid = false; // Set the flag to false since this field is invalid
		} else {
			// If the field is not empty, hide the error message
			errorField.addClass(displayNone).html('');
			field.removeClass(errorClass); // Mark the input as valid
		}
	});

	return allValid;
}


function validateAlphaNumericInputWithSpaceAndDot(element) {
    let input = element.value;
    const alphabetAndNumPatternAndDotWithSpace = /^[a-zA-Z0-9 .]*$/;
    if (!alphabetAndNumPatternAndDotWithSpace.test(input)) {
        input = input.replace(/[^a-zA-Z0-9 .]/g, '');
        element.value = input;
    }
    element.value = input.replace(/\s+/g, ' ');
}

function validateAlphabetInputWithSpace(element) {
    let input = element.value;
    const alphabetAndNumPatternAndDotWithSpace = /^[a-zA-Z0-9 ]*$/;
    if (!alphabetAndNumPatternAndDotWithSpace.test(input)) {
        input = input.replace(/[^a-zA-Z0-9 ]/g, '');
        element.value = input;
    }
    element.value = input.replace(/\s+/g, ' ');
}


function validateAlphaNumericInput(element) {
    let input = element.value;
    const alphabetAndNumPatternAndDotWithSpace = /^[a-zA-Z0-9]*$/;
    if (!alphabetAndNumPatternAndDotWithSpace.test(input)) {
        input = input.replace(/[^a-zA-Z0-9]/g, '');
        element.value = input;
    }
    element.value = input.replace(/\s+/g, ' ');
}

function validateMobileNum(element,req) {
	const mobileNum = $('#' + element.id);
	mobileNum.removeClass(validClass).removeClass(errorClass);
	let errorDiv = mobileNum.siblings('.invalid-feedback');
	if (!!!mobileNum.val() && req) {
		mobileNum.addClass(errorClass)
		errorDiv.html(mobileNum.siblings('.invalid-req-feedback').html());
		return false;
	} else if (!!mobileNum.val() && $(mobileNum).inputmask("unmaskedvalue").length !== 10) {
		$(mobileNum).addClass(errorClass);
		$(mobileNum).removeClass(validClass);
		errorDiv.html(mobileNum.siblings('.invalid-val-feedback').html());
		return false;
	} else {
		if (mobileNum.val()) {
			$(mobileNum).addClass(validClass);
		}
		$(mobileNum).removeClass(errorClass);
	}
	
	
}

function validateEmailMsg(element,req){
	const emailId = $('#'+element.id);
	let errorDiv = emailId.siblings('.invalid-feedback');
	if(!!!emailId.val() && req){
		emailId.addClass(errorClass)
		errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
		return false;
	}else if(!!emailId.val() && validateEmailDOM(element,req)){
		emailId.addClass(errorClass)
		errorDiv.html(errorDiv.siblings('.invalid-valid-feedback').html());
		return false;
	}else {
		emailId.removeClass(errorClass)
		errorDiv.html('');
		return true;
	}
}
	

function getUpperCase(element) {
    let input = element.value;    
    element.value = input.toUpperCase();
}

function validateBetweenDateRanges(dateRanges) {
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
		if (fromDate && toDate && toDate <= fromDate) {
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

function validateIntergerNumberInput(element) {
	let inputValue = element.value.trim();
	const numberPatterns = /^[0-9]+$/;
	if (!numberPatterns.test(inputValue)) {
		// Remove non-numeric characters using regex
		inputValue = inputValue.replace(/\D/g, '');
		// Update the input field value
		element.value = inputValue;
	}
}

// From and To date validation
function validateFromAndToDate(fromDate, toDate) {
	var fromDate = $('#' + fromDate);
	var toDate = $('#' + toDate);
	let errorDivFromDate = fromDate.siblings('.invalid-feedback');
	let errorDivToDate = toDate.siblings('.invalid-feedback');

	if ((fromDate.val() == "" && toDate.val() != "")) {
		fromDate.addClass(errorClass);
		//errorDiv.html(errorDivFromDate.siblings('.invalid-req-feedback').html());
		return false;
	} else if (fromDate.val() !== "" && toDate.val() !== "") {
		if (fromDate.val() <= toDate.val()) {
			fromDate.removeClass(errorClass);
			toDate.removeClass(errorClass);
			errorDivToDate.siblings('.invalid-feedback-invalidDate').addClass('d-none');
			return true;
		} else {
			toDate.addClass(errorClass);
			errorDivToDate.siblings('.invalid-feedback-invalidDate').removeClass('d-none');
			errorDivToDate.html('');
			return false;
		}
	} else {
		fromDate.removeClass(errorClass);
		toDate.removeClass(errorClass);
		return true;
	}
}

function validateAndCheckIfscCode(element,suffix) {
	return new Promise((resolve, reject) => {
		updateInvalidDivClass();
		const ifscCodeElement = $('#'+element.id);
		const errorDiv = ifscCodeElement.siblings(invalidFeedback);

        if(ifscCodeElement.val() != null && ifscCodeElement.val() !== ''){
            ifscCodeElement.removeClass(errorClass).removeClass(validClass);
            // Fetch IFSC code details
            fetch(contextPath + ifscURL + "/" + element.value, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 404) {
                            return 404; // Return 404 status for not found
                        }
                        throw new Error(response.statusText);
                    }
                    return response.json(); // Parse JSON for valid response
                })
                .then(status => {
                    if (status !== 404) {
                        // Successful validation, populate bank details
                        const bankName = status.BANK;
                        const branchName = status.BRANCH;
                        $('#bankName'+suffix).val(bankName);
                        $('#branchName'+suffix).val(branchName);
                        errorDiv.html(errorDiv.siblings(invalidFeedbackReq).html());
                        ifscCodeElement.addClass(validClass).removeClass(errorClass);
                        resolve(true); // Validation passed
                    } else {
                        // IFSC code not found
                        $('#bankName'+suffix).val('');
                        $('#bankName'+suffix).val('');
                        errorDiv.html(errorDiv.siblings('.invalid-feedback-invalid-code').html());
                        ifscCodeElement.addClass(errorClass).removeClass(validClass);
                        resolve(false); // Validation failed
                    }
                })
                .catch(error => {
                    // Handle errors during fetch
                    console.error("Error in validateAndCheckIfscCode:", error);
                    errorDiv.html("An error occurred while validating IFSC code.");
                    ifscCodeElement.addClass(errorClass).removeClass(validClass);
                    reject(false); // Validation failed
                });
        }
        else{
            ifscCodeElement.addClass(errorClass);
            // reject(false);
        }


	});
}



function allowOnlyAlphabetsAndNumbers(inputField) {
	inputField.value = inputField.value.split('').filter(char => alphabetAndNumPattern.test(char)).join('');
}

function allowOnlyAlphabetsNumbersComma(inputField) {
	inputField.value = inputField.value.split('').filter(char => alphabetNumCommaPattern.test(char)).join('');
}	

function validateDateErrMsg(element, compareElement) {
	const field = typeof element === 'string' ? $('#' + element) : $('#' + element.id);
	const compareField = compareElement ? (typeof compareElement === 'string'
			? $('#' + compareElement) : $('#' + compareElement.id)) : null;
	let errorDiv = field.siblings('.invalid-feedback');
	if (!field.val()) {
		field.removeClass(validClass).addClass(errorClass);
		errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
		return false;
	} else if (compareField && compareField.val() && field.val() <= compareField.val()) {
		field.removeClass(validClass).addClass(errorClass);
		errorDiv.html(errorDiv.siblings('.invalid-future-feedback').html());
		return false;
	} else {
		field.removeClass(errorClass).addClass(validClass);
		errorDiv.html('');
		return true;
	}
}
// Prevent non-numeric characters and '0' as the first character
function validateAmountInput(event, inputElement) {
    const keyPressed = String.fromCharCode(event.which || event.keyCode);
    const currentValue = inputElement.value;
    if ((currentValue.length === 0 && keyPressed === '0') || !/^[0-9]$/.test(keyPressed)) {
        event.preventDefault(); 
    }
    
    if (currentValue.length >= 8) {
        event.preventDefault(); 
    }
}

function validateNicEditorContent(element){
	// Retrieve content from nicEditor
	const nicE = new nicEditors.findEditor(element.id);
	const blogContent = nicE.getContent();
	const replacedContent = blogContent.replace("<br />", "").trim();
	element.value = replacedContent;

	// Validate content
	let status = true;
	if (replacedContent === '' || replacedContent === '<br>') {
		element.classList.add(errorClass);
		status = false;
	} else {
		element.classList.remove(errorClass);
	}
	return status;
}

function trimSpaces(element) {
	element.value = element.value.replace(/^\s+/, '').replace(/\s{2,}$/, ' ');
}

function formatDate(date, formatPattern) {
    return dateFns.format(new Date(date), formatPattern);
}

function validateEmailDomain(ids){
	var isValid = true;
	let emailDomains = guideEmail.split(',');

	$(ids).each(function() {
		const field = $(this);
		const templateField = field.attr('data-template');
		if (templateField === undefined || templateField === false) {
			if ($.trim(field.val()) === '') {
				field.addClass(errorClass);
				field.removeClass(validClass);
				isValid = false;
			} else {
				field.removeClass(errorClass);
				field.addClass(validClass);

				// For email field, validate email format
				if (field.attr('type') === 'email') {
					const email = field.val().trim();

					let status = emailDomains.some(domain => email.endsWith(domain.trim()));

					const invalidFeedback = field.siblings('.invalid-feedback');
					const invalidDomainEMail = invalidFeedback.siblings('.invalid-domain-email');
					if(status){
						field.removeClass(errorClass).addClass(validClass);
						isValid = true;
					}
					else{
						invalidFeedback.html(invalidDomainEMail.html()+' - ('+guideEmail+')');
						field.removeClass(validClass).addClass(errorClass);
						isValid = false;
					}
				}
			}
		}
	});

	return isValid;
}

function deleteTableRow(id){
	$('#'+id).parents('tr').remove();
}