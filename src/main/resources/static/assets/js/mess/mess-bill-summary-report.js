$(document).ready(function() {
	$('#updateId span').text('Change Mess From Date');
	if (modalError === true) {
		messConfig();
	}
	$('#updateId').click(function() {
		messConfig();
	});
	$('#additionalButton').click(function() {
		sendMailToStudents();
	});
});

function getMessBillSummaryReport() {
	const mp = $('#messPeriod');
	const mn = $('#messName');
	const isSelValid = validMandatoryFields(mp, mn);
	if (isSelValid) {
		let url = contextPath + baseURL + downloadReportURL + "/" + mp.val() + "/" + mn.val();
		window.open(url, '_blank');
	}
}

function getMessBillSummaryPdf() {
	const mp = $('#messPeriod');
	const mn = $('#messName');
	const isSelValid = validMandatoryFields(mp, mn);
	if (isSelValid) {
		let url = contextPath + baseURL + downloadPdfURL + "/" + mp.val() + "/" + mn.val();
		window.open(url, '_blank');
	}
}

function sendMailToStudents() {
	const mp = $('#messPeriod');
	const mn = $('#messName');
	const isSelValid = validMandatoryFields(mp, mn);
	if (isSelValid) {
		$.ajax({
			url: contextPath + baseURL + sendMailURL + "/" + mp.val() + "/" + mn.val(),
			type: "Get",
			success: function(response) {
				$('#resendEmailModal').modal('show');
				$('#modalMailDiv').html(response);
				$("#resendEmailModalPositive").on("click", function(e) {
					$('#resendEmailModal').modal('hide');
					e.preventDefault();
					$('#messBillSummaryMailId').submit();
				});
			}
		});
	}
}

function validMandatoryFields(mp, mn) {
	const isSelValid = valRequiredSelection('#messPeriod, #messName');

	mp.parent('div').removeClass('is-invalid is-valid');
	mn.parent('div').removeClass('is-invalid is-valid');

	if (mp.val() === "") {
		mp.parent('div').addClass('is-invalid');
	} else {
		mp.parent('div').addClass('is-valid');
	}
	if (mn.val() === "") {
		mn.parent('div').addClass('is-invalid');
	} else {
		mn.parent('div').addClass('is-valid');
	}
	return isSelValid;
}

function checkStudentInMessPeriod() {
	return new Promise((resolve) => {
		updateInvalidDivClass();
		const studentId = $('#studentIdModal');
		const studentNameModal = $('#studentNameModal');

		studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
		const errorDiv = studentId.siblings('.invalid-feedback');

		const clearFields = () => {
			studentId.val('');
			studentNameModal.text('');
		};

		if (!studentId.val()) {
			studentId.addClass(errorClass);
			errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
			studentNameModal.text('');
			return resolve(false);
		}

		$.ajax({
			url: contextPath + messAllottedURL + studentURL + "/" + studentId.val(),
			method: "GET",
			success: function(responseStudent) {
				if (responseStudent !== '') {
					studentId.removeClass(errorClass).addClass(validClass);
					$.ajax({
						url: contextPath + messAllottedURL + checkStudentInMessPeriodURL + "/" + studentId.val(),
						method: "GET",
						success: function(response) {
							if (!response) {
								clearFields();
								studentId.addClass(errorClass);
								errorDiv.html(studentId.siblings('.invalid-not-exist-mess-feedback').html());
								resolve(false);
							} else {
								studentNameModal.text(responseStudent);
								resolve(true);
							}
						},
						error: function() {
							errorDiv.html("Error occurred");
							clearFields();
							resolve(false);
						}
					});
				} else {
					studentId.addClass(errorClass);
					errorDiv.html(studentId.siblings('.invalid-not-exist-feedback').html());
					clearFields();
					resolve(false);
				}
			},
			error: function() {
				clearFields();
				studentId.addClass(errorClass);
				errorDiv.html("Error occurred");
				resolve(false);
			}
		});
	});
}

function messConfig() {
	$.ajax({
		url: contextPath + baseURL + updateURL,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			updateInvalidDivClass();
			$('#mess-from-date-configuration-modal').modal('show');
			$("#comments").text('');
			setEffectiveDateRange($("#effectiveFromDate"));
			updateSaveButtonStyle(1, $('#mess-from-date-configuration-modalSave'));

			$("#mess-from-date-configuration-modalSave").on("click", function(e) {
				e.preventDefault();
				updateInvalidDivClass();
				let isValid = true;
				isValid &= validateEffectiveDate($('#effectiveFromDate'));
				const requiredFields = $('textarea[required], input[required]');
				isValid &= validateRadioAndText(requiredFields);
				if (isValid) {
					$('#messBillSummaryId').submit();
				}
			});

			$("#mess-from-date-configuration-modalValidateBackend").on("click", function(e) {
				e.preventDefault();
				$('#messBillSummaryId').submit();
			});
		},
		error: function() {
			showToast('Error', 'Error occured');
		}
	});
}


function setEffectiveDateRange($inputField) {
	// Get tomorrow's date
	const today = new Date();
	const tomorrow = new Date(today);
	tomorrow.setDate(today.getDate() + 1);
	const tomorrowStr = tomorrow.toISOString().split('T')[0];
	const maxDate = $("#diningToDate").val();
	$inputField.attr("min", tomorrowStr);
	$inputField.attr("max", maxDate);
	//$inputField.prop("disabled", false);
}

function validateEffectiveDate($inputField) {
	let $inputDiv = $inputField.siblings('.invalid-feedback');
	const selectedDate = new Date($inputField.val());
	const today = new Date();
	const diningToDate = new Date($("#diningToDate").val());

	// Normalize all dates to 00:00:00 to avoid time comparison issues
	selectedDate.setHours(0, 0, 0, 0);
	today.setHours(0, 0, 0, 0);
	diningToDate.setHours(0, 0, 0, 0);

	if (!$inputField.val()) {
		$inputField.removeClass(validClass).addClass(errorClass);
		$inputDiv.html($inputDiv.siblings('.invalid-req-feedback').html());
		return false;
	} else if (selectedDate <= today || selectedDate > diningToDate) {
		$inputField.removeClass(validClass).addClass(errorClass);
		$inputDiv.html($inputDiv.siblings('.invalid-range-feedback').html());
		return false;
	} else {
		$inputField.removeClass(errorClass).addClass(validClass);
		return true;
	}
}
