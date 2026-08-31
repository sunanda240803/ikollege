$(document).ready(function() {
	const $imageInput = $('#imageInput');
	const $imageToResize = $('#imageToResize');
	const $resizedImage = $('#resizedImage');
	const $resizeButton = $('#resizeButton');
	const $downloadButton = $('#downloadButton');
	const $resizeWidth = $('#resizeWidth');
	const $resizeHeight = $('#resizeHeight');

	let uploadedImage = null;

	// When an image is selected from the user's device
	$imageInput.on('change', function() {
		const file = this.files[0];

		if (file) {
			const reader = new FileReader();

			// When file is read, display it in the image element
			reader.onload = function(e) {
				uploadedImage = new Image();
				uploadedImage.src = e.target.result;
				uploadedImage.onload = function() {
					$imageToResize.attr('src', uploadedImage.src).removeClass('d-none');
					$resizeButton.removeClass('d-none');
				};
			};

			reader.readAsDataURL(file);
		}
	});

	$resizeButton.on('click', function() {
		const width = parseInt($resizeWidth.val(), 10);
		const height = parseInt($resizeHeight.val(), 10);

		if (!width || !height) {
			alert("Please enter valid width and height values!");
			return;
		}

		// Create a canvas to resize the image
		const canvas = $('<canvas>')[0];
		const ctx = canvas.getContext('2d');

		canvas.width = width;
		canvas.height = height;

		// Draw the image resized to the new dimensions on the canvas
		ctx.drawImage(uploadedImage, 0, 0, width, height);

		// Convert canvas to a data URL and set it to the resizedImage element
		const resizedDataURL = canvas.toDataURL();
		$resizedImage.attr('src', resizedDataURL).removeClass('d-none');

		// Show the download button
		$downloadButton.removeClass('d-none');
	});

	$downloadButton.on('click', function() {
		const resizedDataURL = $resizedImage.attr('src');

		// Create an anchor element to download the resized image
		const link = $('<a>').attr({
			href: resizedDataURL,
			download: 'resized-image.png'
		});

		// Trigger the download
		link[0].click();
	});

	// student-registration.html code

	$('[data-bs-toggle="modal"]').tooltip(); // Initialize tooltips for modal-trigger buttons

	// Hide tooltip when the modal is closed
	$('#resizeModal').on('hidden.bs.modal', function() {
		$('#resizeButton').tooltip('hide'); // Manually hide the tooltip
	});

	let phNumMaskElementArray = ['#mobileNumber', '#facultyContactNo', '#relationContactOfGuardian', '#telephoneOrMobileNumber1', '#relationContact']
	inputMask(phNumMaskElementArray, mobileNumberMask);
	inputMask(['#aadhaarNumber'], aadhaarNumberMask);

	$('.modal').modal({
		backdrop: 'static',
		keyboard: false
	});
	const saveButton = $('#save');
	saveButton.on('click', function() {
		$('#saveForm').click();
	}).find('span', function() {
		console.log(action);
		if (action === 'update')
			$(this).html('Update Form');
	});
	$('#toggleContent').click(function() {
		const moreContent = $('#moreContent');
		moreContent.toggleClass('d-none');
		const isContentVisible = !moreContent.hasClass('d-none');
		const newText = isContentVisible ? "...less" : "...more";
		$(this).text(newText);
	});
	triggerPageLoadFunctions();
	const signedParentName = $('#signedParentName');
	signedParentName.on('blur', function() {
		const parentDataList = $('#parentNameList');
		parentDataList.html('');
		parentDataList.append($('<option>', {
			text: $(this).val().toUpperCase(),
			value: $(this).val().toUpperCase()
		}));
		updateContactLoopByName();
	});

	$(document).on('input', '.relationContact', function() {
	    updateContactLoopByNumber();
	});
	
	$(document).on('change', '.relationName', function() {
		updateContactLoopByName();
	});

	handleAgreementChange('#agreement1', '#underTakingCheck');
	handleAgreementChange('#agreement2', '#hostelRulesCheck');
	handleAgreementChange('#agreement3', '#vehicleRulesCheck');


	const bindings = [
		{ source: '#signedParentName', targets: ['#parentsName1', '#parentsName2', '#studentDeclarationParentOrGuardiansName', '#vehicleDeclarationparentsName'] },
		{ source: '#studentName', targets: ['#parentsDeclarationStudentName1', '#parentsDeclarationStudentName2', '#parentsDeclarationStudentName3', '#parentsDeclarationStudentName4', '#parentsDeclarationStudentName5', '#parentsDeclarationStudentName6', '#studentDeclarationStudentName', '#studentDeclarationStudentName2', '#vehicleDeclarationStudentName'] },
		{ source: '#studentId', targets: ['#parentsDeclarationRollNumber1', '#parentsDeclarationRollNumber2', '#parentsDeclarationRollNumber3', '#parentsDeclarationRollNumber4', '#parentsDeclarationRollNumber5', '#parentsDeclarationRollNumber6'] },
		{ source: '#mobileNumber', targets: ['#telephoneOrMobileNumber2'] }
	];

	bindings.forEach(function(binding) {
		$(binding.source).on('blur', function() {
			const value = $(this).val();
			binding.targets.forEach(function(target) {
				$(target).text(value);
				$(target).val(value);
			});
		});
	});

	//signature section
	const previewBindings = [
		{ input: '#studentPhoto', previews: ['#studentProfileImage'] },
		{ input: '#studentSignature', previews: ['#studentSignaturePreview', '#studentDeclarationSignaturePreview', '#studentVehicleDeclarationSignaturePreview'] },
		{ input: '#parentSignatureFile', previews: ['#parentSignaturePreview', '#parentsSignaturePreview', '#parentsDeclarationSignaturePreview'] }
	];

	previewBindings.forEach(function(binding) {
		$(binding.input).on('change', function() {
			previewImage(this, binding.previews);
		});
	});
	

	// Initialize tooltips on page load
	initializeTooltips();


	const facultyDetails = $('.facultyDetails');
	const facultyName = $('#facultyName');

	// Check if the default value of facultyName is "0" on page load
	if (facultyName.val() === "0" || facultyName.val() === "") {
		resetFacultyDetails(facultyDetails);
	}

	// Monitor the input on the facultyName field
	facultyName.on('input', function() {
		if ($(this).val() === "0" || facultyName.val() === "") {
			resetFacultyDetails(facultyDetails);
		} else {
			// Make the facultyContactNo field writable
			facultyDetails.prop('readonly', false);
			facultyDetails.prop('required', true)
		}
	});

	$('#updateId').removeClass('btn-blue').addClass('btn-tmPrimary');
	$('#updateId i').removeClass('fa-rotate').addClass('fa-arrow-pointer');
	$('#updateId span').text('Update Form');
	$('#updateId').off("click").on("click", function(e) {
		e.preventDefault();
		const isValid = validateForm();
		if (isValid) {
			$('#studentRegistrationFormId').submit();
		}
	});
	togglePwdFields($('#pwdValue').val() === 'Y');
});


function resetFacultyDetails(facultyDetails) {
	// Clear the facultyContactNo value and set it to readonly
	facultyDetails.val('');
	facultyDetails.prop('required', false);
	facultyDetails.prop('readonly', true);
	// Clear validation classes and messages
	facultyDetails.removeClass('is-invalid is-valid');
	facultyDetails.each(function() {
		$(this).siblings('.invalid-feedback').text($(this).siblings('.invalid-req-feedback').text());
	});
}

// Function to initialize tooltips
function initializeTooltips() {
	$('[data-bs-toggle="tooltip"]').tooltip(); // Initialize all tooltips
}


// Function to update the family table rows
function updateTable() {
	addParentDetails(false);
	// Reinitialize tooltips for dynamically added rows
	initializeTooltips();
}

function handleAgreementChange(agreementId, checkId) {
	$(agreementId).change(function() {
		$(checkId).prop('checked', this.checked);
		if (this.checked) {
			$(checkId).removeClass('d-none').removeClass('is-invalid');
		} else {
			$(checkId).addClass('d-none');
		}
	});
}

function updateHiddenInput(selectElement) {
	// Find the data-rowcount attribute of the dropdown
	const rowCount = $(selectElement).data("rowcount");

	// Locate the corresponding hidden input based on the rowCount
	const hiddenInput = $(`#relationTypeHidden${rowCount}`);

	// Update the value of the hidden input to match the dropdown's selected value
	hiddenInput.val(selectElement.value);
}

function validateStudentId() {
	return new Promise((resolve, reject) => {  // Wrap the function in a promise
		if ($('#updateStudentId').val() === undefined) {
			const studentId = $('#studentId');
			studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
			let errorDiv = studentId.siblings('.invalid-feedback');
			errorDiv.html(studentId.siblings('.invalid-value-feedback').html());

			if (!!!studentId.val()) {
				studentId.addClass("is-invalid");
				errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
				return reject(false);  // Reject the promise with false
			}
			if (studentId.val().length < 6) {
				studentId.addClass("is-invalid");
				errorDiv.html(studentId.siblings('.invalid-min-feedback').html());
				return reject(false);  // Reject the promise with false
			}
			if (!(numeric.test(studentId.val()) && alphabetic.test(studentId.val()))) {
				studentId.addClass("is-invalid");
				errorDiv.html(studentId.siblings('.invalid-format-feedback').html());
				return reject(false);  // Reject the promise with false
			}
			if (studentId.val() !== '') {
				fetch(contextPath + baseURL + validateStudentIdURL + "?studentId=" + studentId.val())
					.then(response => {
						return response.text();
					})
					.then(response => {
						console.log(response);
						if (response === '') {
							studentId.removeClass("is-invalid");
							studentId.addClass("is-valid");
							const applicationNo = $('#applicationNumber');
							if (applicationNo.val() === '') {
								applicationNo.val(studentId.val());
								applicationNo.blur();
							}
							return resolve(true);  // Resolve the promise with true
						} else {
							studentId.val('');
							studentId.addClass("is-invalid");
							errorDiv.html(studentId.siblings('.invalid-exist-feedback').html());
							return reject(false);  // Reject the promise with false
						}
					})
					.catch(error => {
						console.error('Error:', error);
						return reject(false);  // Reject the promise in case of an error
					});
			} else {
				studentId.addClass("is-valid");
				return resolve(true);  // Resolve the promise with true
			}
		}
		return resolve(true);  // Resolve the promise with true
	});
}


function validateForm() {
	console.log("validating");
	let result = true;
	const errorMsg = $('#formErrors');
	errorMsg.removeClass(errorClass);
	let errorCount = 0;
	let relationErrorCount = 0;
	const form = document.getElementById('studentRegistrationFormId');

	if (form) {
		const inputs = form.querySelectorAll('.is-valid, .is-invalid');
		inputs.forEach(input => {
			input.classList.remove('is-valid', 'is-invalid');
		});
	}

	const id = $('#formId').val();

	result = validateRadioAndText($('input[required], select[required], textarea[required]'));
	if (!result) errorCount++;

	if (id === '') {
		result = validateDeclarations('parent-declaration', 'parentDeclaration');
		if (!result) errorCount++;
		result = validateDeclarations('student-declaration', 'studentDeclaration');
		if (!result) errorCount++;
		result = validateDeclarations('vehicle-declaration', 'vehicleDeclaration');
		if (!result) errorCount++;
		result = validateSignedParentDetails();
		if (!result) errorCount++;
		result = validateLocalGuardianDetails();
		if (!result) errorCount++;
		result = errorCount === 0;
		$('.relationEmail').each(function () {
			validateEmailMsg(this, ($(this).attr('required') === 'required'));
		});
		let reqMobileField;
		const selectedRadio = document.querySelector('input[name="guardianStatus"]:checked');
		if (selectedRadio && selectedRadio.value === 'Y') {
			reqMobileField = ['#mobileNumber', '.relationContact', '#relationContactOfGuardian'];
		} else {
			reqMobileField = ['#mobileNumber', '.relationContact', '#facultyContactNo'];
		}
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
	}
	result = validateAadhaarNumberPattern($('#aadhaarNumber'), false);
	if (+result !== 0) errorCount++;
	result = validatePANPattern($('#panNumber'), false);
	if (+result !== 0) errorCount++;
	validateStudentId().then(result => {
		return result;
	}).catch(result => {
		showToast('Error', 'Unable to validate student ID');
	});

	const selectedValue = document.querySelector('input[name="pwd"]:checked')?.value;
	if(selectedValue === 'Y') {
		if($('#pwdDesc').val()===null || $('#pwdDesc').val()===''){
			$('#pwdDesc').addClass(errorClass);
			result = false;
			errorCount++;
		}
		else{
			$('#pwdDesc').removeClass(errorClass).addClass(validClass);
		}
	}
	else{
		$('#pwdDesc').addClass(validClass);
	}

	const facultyEmail = $('#facultyEmail');
	if (facultyEmail.val() !== null && facultyEmail.val() !== '') {
		result = validateEmailDomain(facultyEmail);
		if (!result) errorCount++;
	}

	result = validateEmail($('#studentPersonalEmail'), false);
	if (+result !== 0) errorCount++;

	if (errorCount === 0) {
		$('.masked').inputmask('');
		console.log('working fine');
		result = true;
	} else {
		errorMsg.addClass(errorClass);
		result = false;
	}
	if (!result) {
		errorMsg.addClass('is-invalid');
	}
	console.log("validation complete");
	return result;
}

function validateLocalGuardianDetails() {

	const selectedRadio = document.querySelector('input[name="guardianStatus"]:checked');
	if (selectedRadio && selectedRadio.value === 'Y') {
		const elementTextArray = ['#relationNameOfGuardian,#guardianEmail,#relationContactOfGuardian,#guardianAddress'];
		const isValid = valRequiredTextArray(elementTextArray);
		$('#guardianEmail').each(function() {
			validateEmailMsg(this, true);
		});
		valRequiredTextArray(['#guardianAddress']);
		if (isValid) {
			let errorCount = 0;
			errorCount += validateEmail($('#guardianEmail'), true)
			if (errorCount == 0)
				return true;
			else
				return true;
		} else
			return false;

	}
	return true;
}

function validateSignedParentDetails() {
	const signedParentName = $('#signedParentName');
	if (signedParentName.val() === '') {
		const parentNameMismatchError = $('#parentNameMismatch');
		parentNameMismatchError.addClass('is-invalid');
		let result = false;
		$('.relationName').each(function() {
			if ($(this).attr('data-template') !== true) {
				if (signedParentName.val() === $(this).val()) {
					result = true;
				}
			}
		});
		if (result) {
			parentNameMismatchError.removeClass('is-invalid');
		}
		return result;
	}
	return true;
}

function validateDeclarations(declarationCheckboxes, declarationResult) {
	let result = validateRadioAndText($('.' + declarationCheckboxes));
	const declaration = $('#' + declarationResult);
	if (!result) {
		declaration.addClass("is-invalid");
	} else {
		declaration.removeClass("is-invalid");
	}
	return result;
}


function validateAgreement(index, modelId) {
	if (!$('#agreement' + index).is(':checked')) {
		$('.agreementError' + index).addClass('d-block').removeClass('d-none');
		$('#agreement' + index).focus();
		return false;
	} else {
		$('.agreementError' + index).removeClass('d-block').addClass('d-none');
		$('#' + modelId).modal('hide');
	}

}

function toggleFileInput(inputDivId) {
	const fileInputDiv = document.getElementById(inputDivId);
	if (fileInputDiv.style.display === "none") {
		fileInputDiv.style.display = "block";
	} else {
		fileInputDiv.style.display = "none";
	}
}

function togglePwdFields(isYesSelected) {
    if (isYesSelected) {
        $('.pwd').removeClass('d-none');
        $('#pwdPercentage').attr('required', true);
        $('#pwdDesc').attr('required', true);
    } else {
        $('.pwd').addClass('d-none');
        $('#pwdPercentage').removeAttr('required');
        $('#pwdDesc').removeAttr('required');
    }
}

