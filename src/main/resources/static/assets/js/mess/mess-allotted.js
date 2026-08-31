$(document).ready(function() {
	if (modalError === true) {
		let type = $('#returnType').val();
		let id = $('#returnId').val();
		messConfig(id, type);
	}
	$('#additionalButton').click(function() {
		getMessAllottedList('downloadExcel');
	});
	$('#addNewId').click(function() {
		messConfig(null, 'add');
	});
});

function getMessAllottedList(status) {
	// Set these values in the additional param inputs
	$('input[name="additionalParam.studentName"]').val($('#studentName').val());
	$('input[name="additionalParam.studentId"]').val($('#studentId').val());
	$('input[name="additionalParam.messPeriod"]').val($('#messPeriod').val());
	$('input[name="additionalParam.messName"]').val($('#messName').val());

	// Call url with params with the current page and size
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	let search = $('#search').val();

	if (status !== 'downloadExcel') {
		createUrlWithParams(page, size, search);
	} else {
		let url = baseURL + downloadReportURL;
		createAndReturnUrlWithParams(page, size, search, url);
	}
}

function messConfig(elementOrId, type) {
	let id = 0;
	if (typeof elementOrId === 'object' && elementOrId !== null && 'getAttribute' in elementOrId) {
		id = elementOrId.getAttribute("data-id");
	} else if (typeof elementOrId === 'string' || typeof elementOrId === 'number') {
		id = elementOrId;
	}
	$.ajax({
		url: contextPath + baseURL + messConfigURL + "/" + type + "/" + id,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			updateInvalidDivClass();
			$('#mess-allotment-configuration-modal').modal('show');
			if (type === 'change') {
				$("#modalHeaderText").text('Student Mess Change');
				$("#description").text('');
				$(".toggle-change-mess").show().attr("required", true);
				$(".toggle-common-mess").show();
				$(".toggle-change-mess-input").hide().removeAttr("required");
				$(".toggle-remove-mess").hide().removeAttr("required");
				setEffectiveDateRange($("#effectiveFromDate"), type);
			} else if (type === 'remove') {
				$("#modalHeaderText").text('Remove Student Mess');
				$("#remarks").text('');
				$(".toggle-remove-mess").show().attr("required", true);
				$(".toggle-common-mess").show();
				$(".toggle-change-mess-input").hide().removeAttr("required");
				$(".toggle-change-mess").hide().removeAttr("required");
				setEffectiveDateRange($("#effectiveTill"), type);
			} else {
				$("#modalHeaderText").text('Student Details');
				$(".toggle-change-mess-input").show().attr("required", true);
				$(".toggle-common-mess").hide();
				$(".toggle-remove-mess").hide().removeAttr("required");
				$(".toggle-change-mess").hide().removeAttr("required");
				setEffectiveDateRange($("#fromDate"), type);
				setEffectiveDateRange($("#toDate"), type);
			}
			updateSaveButtonStyle(id, $('#mess-allotment-configuration-modalSave'));
			$('#id').val(id);
			$('#type').val(type);

			const form = $("#messAllotedListForm");

			$("#mess-allotment-configuration-modalSave").on("click", function(e) {
				updateInvalidDivClass();
				e.preventDefault();
				let isValid = true;

				const type = $('#type').val();

				if (type === 'change') {
					const requiredFields = $('textarea[required]:visible, select[required]:visible');
					isValid &= validateRadioAndText(requiredFields);
					isValid &= validateEffectiveDate($('#effectiveFromDate'));
				} else if (type === 'remove') {
					const requiredFields = $('textarea[required]:visible');
					isValid &= validateRadioAndText(requiredFields);
					isValid &= validateEffectiveDate($('#effectiveTill'));
				} else if (type === 'add') {
					const requiredFields = $('select[required]:visible, input[required]:visible');
					isValid &= validateRadioAndText(requiredFields);
					isValid &= validateEffectiveDate($('#fromDate'));
				}
				if (isValid) {
					form.submit();
				}
			});

			$("#mess-allotment-configuration-modalValidateBackend").on("click", function(e) {
				e.preventDefault();
				form.submit();
			});
		},
		error: function() {
			showToast('Error', 'Error occured');
		}
	});
}

function checkStudentInMessPeriod() {
	return new Promise((resolve) => {
		updateInvalidDivClass();
		const studentId = $('#studentIdModal');
		const messSelect = $('#messIdModal');
		const studentNameModal = $('#studentNameModal');

		studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
		const errorDiv = studentId.siblings('.invalid-feedback');

		const clearFields = () => {
			messSelect.empty();
			messSelect.append(`<option value="">Select Mess Name</option>`);
			studentNameModal.text('');
		};

		if (!studentId.val()) {
			studentId.addClass(errorClass);
			errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
			clearFields();
			return resolve(false);
		}

		$.ajax({
			url: contextPath + baseURL + studentURL + "/" + studentId.val(),
			method: "GET",
			success: function(responseStudent) {
				if (responseStudent !== '') {
					studentId.removeClass(errorClass).addClass(validClass);

					$.ajax({
						url: contextPath + baseURL + checkStudentInMessPeriodURL + "/" + studentId.val(),
						method: "GET",
						success: function(response) {
							if (!response) {
								studentId.removeClass(errorClass).addClass(validClass);

								$.ajax({
									url: contextPath + baseURL + studentMessListURL + "/" + studentId.val(),
									method: "GET",
									success: function(data) {
										studentNameModal.text(responseStudent);
										messSelect.empty();
										messSelect.append(`<option value="">Select Mess Name</option>`);

										if (Array.isArray(data)) {
											data.forEach(mess => {
												messSelect.append(`<option value="${mess.id}">${mess.messName}</option>`);
											});
											resolve(true);
										} else if (data === "No mess available") {
											messSelect.append(`<option value="">No mess available</option>`);
											resolve(false);
										}
									},
									error: function() {
										console.error("Error fetching mess list");
										clearFields();
										resolve(false);
									}
								});
							} else {
								studentId.val('');
								studentId.addClass(errorClass);
								errorDiv.html(studentId.siblings('.invalid-exist-feedback').html());
								clearFields();
								resolve(false);
							}
						},
						error: function() {
							console.error("Error validating mess period");
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
				console.error("Error fetching student");
				studentId.addClass(errorClass);
				errorDiv.html("Error occurred");
				clearFields();
				resolve(false);
			}
		});
	});
}

function setEffectiveDateRange($inputField, type) {
	// Get tomorrow's date
	const today = new Date();
	const tomorrow = new Date(today);
	tomorrow.setDate(today.getDate() + 1);

	// Format it as yyyy-MM-dd
	const todayStr = today.toISOString().split('T')[0];

	// Get the diningToDate from the hidden field
	const maxDate = $("#diningToDate").val();
	if (type === 'change') {
		const minDateRaw = $("#currentFromDate").val();
		if (minDateRaw) {
			const minDateObj = new Date(minDateRaw);
			minDateObj.setDate(minDateObj.getDate() + 1);
			const minDateStr = minDateObj.toISOString().split('T')[0];
			$inputField.attr("min", minDateStr > todayStr ? minDateStr : todayStr);
		}
	} else if (type === 'remove') {
		const minDateRaw = $("#currentFromDate").val();
		if (minDateRaw) {
			$inputField.attr("min", minDateRaw >= todayStr ? minDateRaw : todayStr);
		}
	} else {
		$inputField.attr("min", todayStr);
	}
	// Set min and max date for the input
	$inputField.attr("max", maxDate);
	$inputField.prop("disabled", false);
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
	} else if (selectedDate < today || selectedDate > diningToDate) {
		$inputField.removeClass(validClass).addClass(errorClass);
		$inputDiv.html($inputDiv.siblings('.invalid-range-feedback').html());
		return false;
	} else {
		$inputField.removeClass(errorClass).addClass(validClass);
		return true;
	}
}
