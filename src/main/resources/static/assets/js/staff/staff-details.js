$(document).ready(function() {
	var facultyId = $('#facultyId').val();
	if (facultyId === '') facultyId = null;
	updateSaveButtonStyleForStringId(facultyId, $('#updateId, #saveButton'));
	
	var today = new Date();
	// Subtract 16 years from the current date
	var sixteenYearsAgo = new Date(today.setFullYear(today.getFullYear() - 16));
	// Format the date to yyyy-mm-dd for the max attribute
	var maxDate = sixteenYearsAgo.toISOString().split('T')[0];
	// Set the max attribute to 16 years ago
	$('#dateOfBirth').attr('max', maxDate);
	$('#dateOfBirth').on('change', function() {
		// Get the selected date from dateOfBirth
		var selectedDate = $(this).val();
		// Set the min attribute of the dateOfJoining input to the selected date
		$('#dateOfJoining').attr('min', selectedDate);
	});
			
	var username = $('#userName1').val();
	$("#userName").text(username);
	let phNumMaskElementArray = ['#mobileNumber', '#contactNumber']
	inputMask(phNumMaskElementArray, mobileNumberMask);
	$('#updateId').on('click', function(e) {
		e.preventDefault();
		$('#saveButton').click();
	});
});

// Add designation
//function showCategoryBox() {
//	$('#designationId').parent().removeClass(validClass).removeClass(errorClass);
//	$('#addDesignation').removeClass("is-invalid");
//	$('#designationSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').addClass(displayNone);
//	$('#designationInput, #saveCatBtn, #designationCancelButton').removeClass(displayNone);
//	$('#designationName').val(0);
//	$('#addDesignation').val('');
//	$('#saveCatBtn').removeClass('btn-blue').addClass('btn-aqua');
//	$('#saveCatBtn').find('i').removeClass('fa-rotate').addClass('fa-floppy-disk');
//	$('#saveCatBtn').tooltip('dispose').attr('title', 'Save Designation').tooltip({ trigger: 'hover' });
//}
//
//// department cancel
//$('#designationCancelButton').click(function() {
//	cancelButton();
//});

//function cancelButton() {
//	$('#designationId').parent().removeClass(validClass).removeClass(errorClass);
//	$('#addDesignation').removeClass("is-invalid");
//	$('#validateCatNameDrop').addClass(displayNone);
//	$('#designationInput, #saveCatBtn, #designationCancelButton').addClass(displayNone);
//	$('#designationSelect, #addCatBtn, #editCatBtn, #deleteCatBtn').removeClass(displayNone);
//}

//department update
//$('#editCatBtn').click(function() {
//	$('#designationId').parent().removeClass(validClass).removeClass(errorClass);
//	var id = $('#designationId').val();
//	if (id === null || id == "" || id == 0) {
//		$('#validateCatNameDrop').removeClass(displayNone);
//		$('#designationSelect').removeClass("d-none");
//
//	} else {
//		$('#saveCatBtn').removeClass('btn-aqua').addClass('btn-blue');
//		$('#saveCatBtn').find('i').removeClass('fa-floppy-disk').addClass('fa-rotate');
//		$('#validateCatNameDrop').addClass(displayNone);
//		const dropdown = document.getElementById("designationId");
//		const selectedOption = dropdown.options[dropdown.selectedIndex];
//		const selectedText = selectedOption.text;
//		$('#masterId').val(id);
//		$('#addDesignation').val(selectedText);
//		$('#designationSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').addClass(displayNone);
//		$('#designationInput, #saveCatBtn, #designationCancelButton').removeClass(displayNone);
//		$('#saveCatBtn').tooltip('dispose').attr('title', 'Update Designation').tooltip({ trigger: 'hover' });
//	}
//});
//
//$('#saveCatBtn').on("click", function(event) {
//	$('#designationId').parent().removeClass(validClass).removeClass(errorClass);
//	event.preventDefault();
//	$(this).prop('disabled', true);
//
//	var designationName = $('#addDesignation').val().trim();
//	var id = $('#masterId').val().trim();
//
//	if (designationName === '') {
//		$('#validateCatName').text("Designation Name is required").addClass("is-invalid");
//		$('#addDesignation').addClass("is-invalid");
//		$("#saveCatBtn").prop('disabled', false);
//		return false;
//	} else {
//		$('#validateCatName').text('').removeClass("is-invalid");
//		$('#addDesignation').removeClass("is-invalid");
//
//		var validateUrl = contextPath + designationMaster + designationNameExist + "/" + designationName + "/" + (id || 0);
//
//		$.ajax({
//			url: validateUrl,
//			type: "GET",
//			async: false,
//			success: function(response) {
//				if (!response) {
//					// Proceed with saving via AJAX
//					var formData = {
//						designationName: designationName,
//						id: id || null
//					};
//
//					$.ajax({
//						url: contextPath + designationMaster,
//						type: "POST",
//						data: formData,
//						success: function(response) {
//							// Show success toast or message
//							showToast('Success', 'Designation saved successfully');
//
//							// Re-enable the save button
//							$("#saveCatBtn").prop('disabled', false);
//
//							cancelButton();
//							refreshDesignationDropdown();
//
//							// Optionally clear the input fields
//							$('#addDesignation').val('');
//							$('#masterId').val(0);
//						},
//						error: function() {
//							showToast('Error', 'Error saving designation');
//							$("#saveCatBtn").prop('disabled', false);
//						}
//					});
//				} else {
//					$('#addDesignation').addClass("is-invalid");
//					$('#validateCatName').text('Designation Name already exists').addClass("is-invalid");
//					$("#saveCatBtn").prop('disabled', false);
//				}
//			},
//			error: function() {
//				showToast('Error', 'Error occurred while validating');
//				$("#saveCatBtn").prop('disabled', false);
//			}
//		});
//	}
//});
//
//// Function to refresh the designation dropdown after saving
//function refreshDesignationDropdown() {
//	$.ajax({
//		url: contextPath + designationMaster, // URL to get updated designation list
//		type: "GET",
//		success: function(response) {
//			var designationDropdown = $('#designationId');
//
//			// Destroy the selectpicker
//			designationDropdown.selectpicker('destroy');
//
//			// Clear the existing options
//			designationDropdown.empty();
//			designationDropdown.append('<option value="">Select Designation</option>');
//
//			// Populate with new options
//			if (response) {
//				response.forEach(function(designation) {
//					designationDropdown.append('<option value="' + designation.id + '">' + designation.designationName + '</option>');
//				});
//			}
//
//			// Reinitialize the selectpicker
//			designationDropdown.selectpicker();
//
//			// Refresh the selectpicker to ensure UI updates
//			$('.selectpicker').selectpicker('refresh');
//		},
//		error: function() {
//			showToast('Error', 'Error refreshing designation dropdown');
//		}
//	});
//}
//
//// Delete designation
//function deleteDesignation() {
//	$('#designationId').parent().removeClass(validClass).removeClass(errorClass);
//	var id = $('#designationId').val();
//	if (!id || id == 0) {
//		$('#validateCatNameDrop').removeClass(displayNone);
//		$('#designationSelect').removeClass("d-none");
//		return false;
//	}
//
//	$('#validateCatNameDrop').addClass(displayNone);
//	$('#deleteModal').modal('show');
//	$('#dynamicId').val(id);
//
//	// Ensure only one click handler is attached
//	$('#deleteModalPositive').off('click').on('click', function() {
//		$('#deleteModal').modal('hide');
//		const designationId = $('#dynamicId').val();
//
//		$.ajax({
//			url: contextPath + designationMaster + "/" + designationId,
//			type: "DELETE",
//			success: function(response) {
//				if (response != "") {
//					showToast('Success', 'Designation deleted successfully');
//					refreshDesignationDropdown();
//					$('.selectpicker').selectpicker('refresh');
//					$('#validateCatNameDrop').addClass(displayNone);
//					$('#designationSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').removeClass(displayNone);
//					$('#designationInput, #saveCatBtn, #designationCancelButton').addClass(displayNone);
//				} else {
//					showToast('Failure', response);
//				}
//			},
//			error: function(xhr, status, error) {
//				let errorMessage = 'Error occurred';
//				if (xhr.responseJSON && xhr.responseJSON.message) {
//					// Use the message from the server's response if available
//					errorMessage = xhr.responseJSON.message;
//				} else if (xhr.responseText) {
//					// Use the responseText if available
//					errorMessage = xhr.responseText;
//				}
//				showToast('Error', errorMessage);
//			}
//		});
//		$('#deleteModalPositive').off('click');
//	});
//}


function validateForm() {
	let ageValid = false;
	let mobileNumberValid = false;
	const requiredFields = $('input[required], select[required], textarea[required]');
	const isValid = validateRadioAndText(requiredFields);
	const activate = document.getElementById('activate').checked;
	const dropdownValid = activate ? valRequiredMultiSelect('#roleId') : true;
	// Validate age based on staff type
	const dobElement = $('#dateOfBirth');
	const dateValid = fieldEmpty(dobElement);
	if (dateValid === 0 && requiredFields) {
		const staffType = $('input[name="teachingOrNonTeaching"]:checked').val();
		const dob = new Date(dobElement.val());
		const age = calculateAge(dob);
		ageValid = validateAgeBasedOnStaffType(age, staffType);
	}
	
	let reqMobileField = ['#mobileNumber'];
	reqMobileField.forEach(function(element1) {
		let element = $(element1);
		let errorDiv = element.siblings('.invalid-feedback');
		if (!!!element.val()) {
			element.addClass("is-invalid");
			$(element).removeClass(validClass);
			errorDiv.html(element.siblings('.invalid-req-feedback').html());
			mobileNumberValid = false;
		} else if (!!element.val() && $(element).inputmask("unmaskedvalue").length !== 10) {
			$(element).addClass(errorClass);
			$(element).removeClass(validClass);
			errorDiv.html(element.siblings('.invalid-val-feedback').html());
			mobileNumberValid = false;
		} else {
			$(element).addClass(validClass).removeClass(errorClass);
			mobileNumberValid = true;
		}
	});
	
	const result = isValid && ageValid && mobileNumberValid && requiredFields && dropdownValid;
	if (result) {
		$('.masked').inputmask('');
	}
	return result;
}

// Calculate age based on date of birth
function calculateAge(birthDate) {
	const today = new Date();
	let age = today.getFullYear() - birthDate.getFullYear();
	const monthDifference = today.getMonth() - birthDate.getMonth();

	if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
		age--;
	}
	return age;
}

// Validate age based on staff type (Teaching or Non-Teaching)
function validateAgeBasedOnStaffType(age, staffType) {
	const dobElement = $('#dateOfBirth');
	let errorDiv = dobElement.siblings('.invalid-feedback');

	// Teaching staff must be above 20 years
	if (staffType === 'Teaching Staff') {
		if (age < 20) {
			dobElement.removeClass(validClass).addClass(errorClass);
			errorDiv.html(dobElement.siblings('.invalid-age-limit-teaching-feedback').html()).removeClass(displayNone);
			return false;
		}
	}
	// Non-Teaching staff must be above 16 years
	else if (staffType === 'Non-Teaching Staff') {
		if (age < 16) {
			dobElement.removeClass(validClass).addClass(errorClass);
			errorDiv.html(dobElement.siblings('.invalid-age-limit-non-teaching-feedback').html()).removeClass(displayNone);
			return false;
		}
	}

	dobElement.removeClass(errorClass);
	errorDiv.addClass(displayNone);
	return true;
}

//searchable dropdown validation
function validateDropdown(selectors, required) {
	if (!Array.isArray(selectors)) {
		selectors = [selectors];
	}

	let isValid = true;

	selectors.forEach(selector => {
		const dropdown = $(selector);
		const value = dropdown.val().trim();
		const errorDiv = dropdown.siblings('.invalid-feedback');

		if (required && value === '') {
			dropdown.addClass("is-invalid");
			errorDiv.removeClass(displayNone);
			isValid = false;
		} else {
			dropdown.removeClass("is-invalid");
			errorDiv.addClass(displayNone);
		}
	});

	return isValid;
}

function createStaffUserName() {
    const firstName = $('#newFacultyId').val();
    let facultyId = $('#facultyId').val();
    facultyId = facultyId !== "" ? facultyId : "0";
    const activate = document.getElementById('activate').checked;
    const isValid = validateRadioAndText('#newFacultyId');
    if (firstName.trim() !== '') {
		if (activate && isValid) {
			const url = contextPath + baseURL + usernameURL + "/" + firstName + "/-/Faculty/" + facultyId;
			console.log(url);
			$.ajax({
				url: url,
				type: "GET",
				success: function(response) {
					console.log(response);
					$('#userName').html(response);
					$('#userName1').val(response);
				},
				error: function(error) {
					console.log("Error");
					console.log(error)
				}
			})
		} else {
			$('#userName').html('');
		}
    } else {
        $("#firstName").focus();
        $("#activate").prop("checked", false);
    }
}