$(document).ready(function() {

	//Blood Group Select
	if ($('#bloodGroupValue').val() != null && $('#bloodGroupValue').val() != '') {
		$('#bloodGroup').val($('#bloodGroupValue').val());
	}
	//PWD Toggle
	let pwdYes = $('#yes').prop('checked');
	if ($('#yes').length || $('#no').length) {
		if (pwdYes) {
			togglePwd(true);
		} else {
			togglePwd(false);
		}
	}
	let phNumMaskElementArray = ['#mobileNumber', '#facultyContactNo', '#relationContactOfGuardian', '#telephoneOrMobileNumber1', '#relationContact']
	inputMask(phNumMaskElementArray, mobileNumberMask);
	inputMask(['#aadhaarNumber'], aadhaarNumberMask);
});

function getStudentdetails() {
	const requiredField = validateRadioAndText($('#studentId'));
	const isvalid = validateStudentId();

	if (isvalid && requiredField) {
		const studentId = $('#studentId').val();
		let category = $('input[name="category"]:checked').val();

		let url = contextPath + getStudentRegistrationURL;
		if (category) {
			url += "/" + category;
		}
		url += "/" + studentId;
//		window.location.href = url;
		const form = document.getElementById('studentBioData');
		form.action = url;
		form.submit();
	} else {
		return false;
	}
}

function wellnessURL(element) {
	let category = element !== null ? element.getAttribute("data-category") : null;
	const studentId = element !== null ? element.getAttribute("data-student-id") : null;

	if (category !== null && studentId !== null) {
		let categoryVal = (category === "Students" ? "students" : "others");
		window.location = contextPath + wellnessArchiveURL + "/" + categoryVal + "?studentId=" + studentId;
	}
}

function validateStudentId() {
	const studentId = $('#studentId');
	studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
	let errorDiv = studentId.siblings('.invalid-feedback');
	errorDiv.html(studentId.siblings('.invalid-value-feedback').html());
	if (!!!studentId.val()) {
		studentId.addClass("is-invalid")
		errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
		return false;
	}
	if (studentId.val().length < 6) {
		studentId.addClass("is-invalid")
		errorDiv.html(studentId.siblings('.invalid-min-feedback').html());
		return false;
	}
	if (!(numeric.test(studentId.val()) && alphabetic.test(studentId.val()))) {
		studentId.addClass("is-invalid")
		errorDiv.html(studentId.siblings('.invalid-format-feedback').html());
		return false;
	}
	return true;
}

function updateStudentBioData(element) {
	const studentId = element.getAttribute("data-student-id");
	window.location = contextPath + getStudentRegistrationURL + updateURL + "?studentId=" + studentId;
}

function updateStudentBioData(element) {
	let errorCount = 0;
	let relationErrorCount = 0;
	let result = true;
	const requiredFields = $('input[required], select[required]');
	let status = validateRadioAndText(requiredFields);
	$('.relationEmail').each(function() {
		validateEmailMsg(this, ($(this).attr('required') === 'required'));
	});
	let reqMobileField = ['#mobileNumber', '.relationContact'];
	let reqAgeField = ['.relationAge'];
	reqMobileField.forEach(function(selector) {
		let elements = $(selector);
		elements.each(function() {
			let element = $(this);
			let errorDiv = element.siblings('.invalid-feedback');

			if (element.attr('required') && !element.val()) {
				element.addClass("is-invalid");
				errorDiv.html(element.siblings('.invalid-req-feedback').html());

				if (selector === '.relationContact') {
					relationErrorCount++;
				} else {
					errorCount++;
				}
			} else if (element.inputmask("unmaskedvalue").length !== 10 && element.inputmask("unmaskedvalue").length > 0) {
				element.addClass("is-invalid").removeClass("is-valid");
				errorDiv.html(element.siblings('.invalid-val-feedback').html());

				if (selector === '.relationContact') {
					relationErrorCount++;
				} else {
					errorCount++;
				}
			} else {
				if (element.attr('required') || element.inputmask("unmaskedvalue").length > 0) {
					element.addClass("is-valid");
				}
				element.removeClass("is-invalid")
			}
		});
	});
	reqAgeField.forEach(function(selector) {
		let elements = $(selector);
		elements.each(function() {
			let element = $(this);
			let errorDiv = element.siblings('.invalid-feedback');

			if (element.attr('required') && !element.val()) {
				element.addClass("is-invalid");
				errorDiv.html(element.siblings('.invalid-req-feedback').html());
				errorCount++;
			} else {
				if (element.attr('required')) {
					element.addClass("is-valid");
				}
				element.removeClass("is-invalid")
			}
		});
	});
	if (relationErrorCount === 0) {
		result = validateFamilyInfoDetails();
		if (!result) errorCount++;
	} else {
		errorCount++;
	}
	result = validateAadhaarNumberPattern($('#aadhaarNumber'), false);
	if (+result !== 0) errorCount++;
	result = validatePANPattern($('#panNumber'), false);
	if (+result !== 0) errorCount++;

	if ($('#facultyEmail').val() !== null && $('#facultyEmail').val() !== '') {
		result = validateEmailDomain($('#facultyEmail'));
		if (!result) errorCount++;
	}
	const selectedValue = document.querySelector('input[name="allStudentsDetailsViewDto.pwd"]:checked')?.value;
	if (selectedValue === 'Y') {
		if ($('#pwdDesc').val() === null || $('#pwdDesc').val() === '') {
			$('#pwdDesc').addClass(errorClass);
			result = false;
			errorCount++;
		}
		else {
			$('#pwdDesc').removeClass(errorClass).addClass(validClass);
		}
	}
	else {
		$('#pwdDesc').addClass(validClass);
	}
	//
	//		if($('#facultyEmail').val()!==null && $('#facultyEmail').val()!==''){
	//			result = validateEmailDomain($('#facultyEmail'));
	//			if (!result) errorCount++;
	//		}

	if (status && errorCount === 0) {
		$('.masked').inputmask('');
		$('#studentBioDataFormUpdate').submit();
	} else { return false; }
}

function togglePwd(isYesSelected) {
	if (isYesSelected) {
		$('.pwdField').removeClass('d-none');
		$('#pwdPercentage').attr('required', true);
		$('#pwdDesc').attr('required', true);
	} else {
		$('.pwdField').addClass('d-none');
		$('#pwdPercentage').removeAttr('required').val('');
		$('#pwdDesc').removeAttr('required').val('');
		$('#otherInfo').val('');
	}
}