$(document).ready(function() {
	$('#downloadId').click(function() {
		addStudentMessRebateModal();
	});
	
	if (modalError === true) {
	    addStudentMessRebateModal();
	}
});

function validatePreviousDates() {
	return new Promise((resolve) => {
		const rebateFrom = $('#rebateFrom');
		const rebateTo = $('#rebateTo');
		fetch(contextPath + messRebateURL + checkDetailsExistURL + "?rebateFrom=" + rebateFrom.val() + "&rebateTo=" + rebateTo.val())
			.then(response => response.text())
			.then(response => {
				if (response === '') {
					rebateFrom.removeClass(errorClass).addClass(validClass);
					resolve(true);
				} else {
					let rebateFromErrorDiv = rebateFrom.siblings('.invalid-feedback');
					rebateFrom.removeClass(validClass).addClass(errorClass);
					rebateFromErrorDiv.html(rebateFromErrorDiv.siblings('.invalid-already-requested-feedback').html());
					resolve(false);
				}
			})
			.catch(error => {
				console.error("Fetch error:", error);
				resolve(false);
			});
	});
}

function viewStudentMessRebateDetails(element) {
	const id = element.getAttribute("data-mess-id");
	$.ajax({
		url: contextPath + messRebateURL + '/' + id,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#view-mess-rebate-modal').modal('show');
		},
		error: function() {
			showToast('Error', 'Error occured');
		}
	});
}

function addStudentMessRebateModal() {
	$.ajax({
		url: contextPath + messRebateURL + newURL,
		type: "GET",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#student-mess-rebate-modal').modal('show');
			
			$('#rebateFrom, #rebateTo').on('change', validateRebateDates);

			const rebateFrom = $('#rebateFrom');
			const rebateTo = $('#rebateTo');
			const maxPeriod = parseInt($('#maxPeriod').val() || 0, 10);
			const noOfDays = calculateDaysBetweenDates(rebateFrom.val(), rebateTo.val());
			
			if (noOfDays > maxPeriod) {
				$('.exceedPeriod').removeClass(displayNone);
			} else {
				$('.exceedPeriod').addClass(displayNone);
			}
			
			$('#student-mess-rebate-modalSave').off("click").on("click", function(e) {
				e.preventDefault();
				const saveButton = $(this);
				saveButton.attr('disabled', true);
				updateInvalidDivClass();
				if (validateForm()) {
					// const noOfDaysField = $('#noOfDays');
					// const maxPeriod = parseInt($('#maxPeriod').val() || 0, 10);
					// let fileStatus = true;
					// if (noOfDaysField.val() > maxPeriod) {
					// 	const fileInfo = $('#uploadFile_fileInfo');
					// 	const fileError = $('#uploadFile_fileError');
                    //
					// 	// Validate the file information
					// 	if (fileInfo.length && fileInfo.text().trim()) {
					// 		fileStatus = true;
					// 		fileError.text('');
					// 	} else {
					// 		fileStatus = false;
					// 		fileError.text('Report Document file is required');
					// 	}
					// }
					validatePreviousDates().then(isValid => {
						if (!isValid) {
							saveButton.attr('disabled', false);
							return false;
						}
						// if (isValid && fileStatus) {
						if (isValid) {
							$('form[id="messRebateForm"]').submit();
						} else {
							saveButton.attr('disabled', false);
						}
					});
				} else {
					saveButton.attr('disabled', false);
				}
			});

			$("#student-mess-rebate-modalValidateBackend").on("click", function(e) {
				e.preventDefault();
				$('form[id="messRebateForm"]').submit();
			});
		}
	});
}

// Function to validate rebate dates and update "No. of Days"
function validateRebateDates() {
	const rebateFrom = $('#rebateFrom');
	const rebateTo = $('#rebateTo');
	const noOfDaysField = $('#noOfDays');
	const hiddenNoOfDaysField = $('#hiddenNoOfDays');
	const minFromDate = parseInt($('#minFromDate').val() || 0, 10); // Convert to integer
	const maxFromDate = parseInt($('#maxFromDate').val() || 0, 10); // Convert to integer
	const minPeriod = parseInt($('#minPeriod').val() || 0, 10);
	const maxPeriod = parseInt($('#maxPeriod').val() || 0, 10);

	// Temporary changes for the date 16th, 17th and 18th of Jan 2026.
	const exceptionStartDate = new Date('2026-01-16');
	const exceptionEndDate   = new Date('2026-01-18');

	const today = new Date();
	const minDate = new Date(today);
	const maxDate = new Date(today);

	minDate.setDate(today.getDate() + minFromDate - 1);
	maxDate.setDate(today.getDate() + maxFromDate);

	let isValid = true;

	const rebateFromDate = new Date(rebateFrom.val());
	let rebateFromErrorDiv = rebateFrom.siblings('.invalid-feedback');
	if (!rebateFrom.val() || rebateFromDate < minDate || rebateFromDate > maxDate) {
		rebateFrom.removeClass(validClass).addClass(errorClass);
		rebateFromErrorDiv.html(rebateFromErrorDiv.siblings('.invalid-invalid-feedback').html());
		isValid = false;
	} else {
		rebateFrom.removeClass(errorClass).addClass(validClass);
	}

	// const rebateToDate = new Date(rebateTo.val());
	// const rebateFromDateVal = new Date(rebateFrom.val());
	// let rebateToErrorDiv = rebateTo.siblings('.invalid-feedback');

	// if (rebateFromDateVal > exceptionEndDate || rebateToDate > exceptionEndDate) {
	// 	rebateTo.removeClass(validClass).addClass(errorClass);
	// 	rebateToErrorDiv.html('You can apply for the mess rebate only till January 18. From January 19 onwards, you can apply for future days.');
	// 	isValid = false;
	// 	return isValid;
	// }

	if (rebateFrom.val() && rebateTo.val()) {
		const rebateToDate = new Date(rebateTo.val());
		const noOfDays = calculateDaysBetweenDates(rebateFrom.val(), rebateTo.val());
		let rebateToErrorDiv = rebateTo.siblings('.invalid-feedback');
		// if (rebateToDate < rebateFromDate && !(rebateToDate >= exceptionStartDate && rebateToDate <= exceptionEndDate)) {
		if (rebateToDate < rebateFromDate) {
			rebateTo.removeClass(validClass).addClass(errorClass);
			rebateToErrorDiv.html(rebateToErrorDiv.siblings('.invalid-future-feedback').html());
			isValid = false;
		// } else if (noOfDays < minPeriod && !(rebateToDate >= exceptionStartDate && rebateToDate <= exceptionEndDate)) {
		} else if (noOfDays < minPeriod) {
			rebateTo.removeClass(validClass).addClass(errorClass);
			rebateToErrorDiv.html(rebateToErrorDiv.siblings('.invalid-min-period-feedback').html());
			isValid = false;
		} else {
			rebateTo.removeClass(errorClass).addClass(validClass);
			// Calculate the number of days
			noOfDaysField.val(noOfDays);
			hiddenNoOfDaysField.val(noOfDays);

			// Check if number of days exceeds maxPeriod
			if (noOfDays > maxPeriod) {
				$('.exceedPeriod').removeClass(displayNone);
			} else {
				$('.exceedPeriod').addClass(displayNone);
			}
		}
	} else {
		isValid = false;
	}

	return isValid;
}

// Common function to validate all fields before form submission
function validateForm() {
	let isValid = true;
	$('.form-control').removeClass(errorClass).removeClass(validClass);

	// Validate other fields
	const guideEmail = $('#guideEmail');

	const requiredFields = $('input[required], textarea[required]');

	// Temporary changes for the date 16th, 17th and 18th of Jan 2026.
	const exceptionStartDate = new Date('2026-01-16');
	const exceptionEndDate   = new Date('2026-01-18');
	const rebateToDate = new Date($('#rebateTo').val());
	const exceptionDate = !(rebateToDate >= exceptionStartDate && rebateToDate <= exceptionEndDate);
//	isValid &= validateDateErrMsg('leaveTo', 'leaveFrom');
	if (exceptionDate) {
		isValid &= validateDateErrMsg('rebateFrom', null);
		isValid &= validateDateErrMsg('rebateTo', 'rebateFrom');
	}
	isValid &= validateRadioAndText(requiredFields);
	if (guideEmail.is('input')) {
		isValid &= validateEmailMsg(guideEmail[0], true);
	}

	// Validate rebate dates
	if (isValid !== 0) {
		if (!validateRebateDates()) {
			isValid = false;
		}
	}

	return !!isValid;
}

// Function to calculate the number of days between two dates
function calculateDaysBetweenDates(fromDate, toDate) {
	const from = new Date(fromDate);
	const to = new Date(toDate);

	if (from.getTime() === to.getTime()) {
		return 1;
	}

	const diffTime = Math.abs(to - from);
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
	return diffDays + 1;
}

function triggerDownload() {
	let fileName = $('#fileName').val();
	const url = contextPath + messRebateURL + downloadURL + "/" + fileName;
	window.location.href = url;
}
