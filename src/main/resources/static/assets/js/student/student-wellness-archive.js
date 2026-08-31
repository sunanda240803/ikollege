$(document).ready(function() {
	$("#toggleFilter").click(function() {
		const isExpanded = $(this).attr("aria-expanded") === "true";
		const toggleFilterDiv = $(".toggleFilterSection");

		if (isExpanded) {
			$("#toggleFilterText").text("Hide Advanced Filter");
			$(".keywordSearch").addClass(displayNone);
			toggleFilterDiv.removeClass("col-lg-6").addClass("col-lg-12");
		} else {
			$("#toggleFilterText").text("Show Advanced Filter");
			$(".keywordSearch").removeClass(displayNone);
			toggleFilterDiv.removeClass("col-lg-12").addClass("col-lg-6");
		}
	});

	$('#additionalButton').click(function() {
		getWellnessFilteredList('downloadExcel');
	});

	const saveButton = $('#saveButton').val();
	const addNew = $('#addNew').val();
	if (saveButton === 'Disable') {
		$('#updateId').addClass(displayNone);
	}
	if (addNew === 'Disable') {
		$('#addNewId').addClass(displayNone);
	}

	$("#addNewId span").text("Add Visit Notes");
	$('#addNewId').click(function() {
		editVisitHistoryDetails(null);
	});

	if (modalError === true) {
	    editVisitHistoryDetails(null);
	}

});

function updateHiddenDurationTime() {
	const hour = parseInt($('#hour').val()) || 0;
	const minute = parseInt($('#minute').val()) || 0;
	const totalMinutes = hour * 60 + minute;

	$('#duration').val(totalMinutes);
}

function updateMinuteOptions() {
	const hour = parseInt($('#hour').val()) || 0;
	const $minuteSelect = $('#minute');

	// Clear existing options
	$minuteSelect.empty();

	// Always add the default option
	$minuteSelect.append('<option value="" selected>Minute</option>');

	if (hour === 2) {
		// If hour is 2, only allow 00 minutes
		$minuteSelect.append('<option value="00">00</option>');
	} else {
		// Otherwise, add 00, 15, 30, and 45
		for (let i = 0; i <= 3; i++) {
			let minuteValue = i * 15;
			let formattedMinute = minuteValue < 10 ? '0' + minuteValue : minuteValue;
			$minuteSelect.append(`<option value="${formattedMinute}">${formattedMinute}</option>`);
		}
	}
}

function editWellnessDetails(element) {
	let category = element !== null ? element.getAttribute("data-category") : null;
	const studentId = element !== null ? element.getAttribute("data-student-id") : null;
	if (category !== null && studentId !== null) {
		let categoryVal = (category === "Students" ? "students" : "others");
		window.location.href = contextPath + baseURL + '/' + categoryVal + "?studentId=" + studentId;
	}
}

function viewWellnessDetails(element) {
	const wellnesId = element.getAttribute("data-wellness-id");
	window.location.href = contextPath + baseURL + viewWellnessDetailsURL + "?wellnessId=" + wellnesId;
}

function printPdfWellnessDetails(element) {
	const wellnesId = element.getAttribute("data-wellness-id");
	const url = contextPath + baseURL + pdfWellnessDetailsURL + '/' + wellnesId;
	window.open(url, '_blank');
}

function editVisitHistoryDetails(element) {
	const wellnessId = $('#wellnessId').val();
	const id = element !== null ? element.getAttribute("data-id") : 0;
	$.ajax({
		url: contextPath + baseURL + wellnessVisitDetailsURL + "/" + wellnessId + "/" + id,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#wellness-visit-history-modal').modal('show');

			updateSaveButtonStyle(id, $('#wellness-visit-history-modalSave'));
			$('#wellnessId').val(wellnessId);

			function toggleStatusFields() {
				var visitStatus = $('#visitStatus').val();
				$('.closed').addClass(displayNone);

				if (visitStatus === "closed") {
					$('#followUpDate').val('');
					$('#futureActionPlan').val('');
					$('.closed').addClass(displayNone);
				} else {
					$('.closed').removeClass(displayNone);
				}
			}
			toggleStatusFields();
			$('#visitStatus').on('change', toggleStatusFields);

			if (id !== null && id > 0) {
				// **Extract and Set Hour & Minute based on `duration`**
				const duration = parseInt($('#duration').val()) || 0; // Ensure `duration` is a number
				const hourValue = Math.floor(duration / 60); // Extract hours
				const minuteValue = duration % 60; // Extract remaining minutes

				$('#hour').val(hourValue).trigger('change'); // Set hour dropdown & trigger change
				updateMinuteOptions(); // Ensure minute options are refreshed before setting
				$('#minute').val(minuteValue < 10 ? '0' + minuteValue : minuteValue).trigger('change'); // Set minute dropdown
			}

			$('#hour').change(function() {
				updateMinuteOptions();
				updateHiddenDurationTime();
			});

			$('#minute').change(function() {
				updateHiddenDurationTime();
			});

			$("#wellness-visit-history-modalSave").on("click", function(e) {
				e.preventDefault();
				const saveButton = $('#wellness-visit-history-modalSave');
				saveButton.attr('disabled', true);
				updateInvalidDivClass();

				let isValid = true;
				const requiredFields = $('input[required], select[required]');
				isValid = validateRadioAndText(requiredFields);

				let duration = $('#duration');
				if (duration.val() === null || duration.val() === '0' || duration.val() === '') {
					duration.removeClass(validClass).addClass(errorClass);
					isValid = isValid && false;
				} else {
					duration.removeClass(errorClass).addClass(validClass);
				}

				if (isValid) {
					$('#wellnessVisitId').submit();
				} else {
					saveButton.attr('disabled', false);
					showToast('Error', 'Please fill all the mandatory fields and submit.');
					return false;
				}
			});
			$("#wellness-visit-history-modalValidateBackend").on("click", function(e) {
				e.preventDefault();
				$('#wellnessVisitId').submit();
			});
		},
		error: function() {
			showToast('Error', 'Error occured');
		}
	});
}

function getWellnessFilteredList(status) {
	// Set these values in the additional param inputs
	$('input[name="additionalParam.studentName"]').val($('#studentName').val());
	$('input[name="additionalParam.studentId"]').val($('#studentId').val());
	$('input[name="additionalParam.referralDateFrom"]').val($('#referralDateFrom').val());
	$('input[name="additionalParam.referralDateTo"]').val($('#referralDateTo').val());
	$('input[name="additionalParam.visitDateFrom"]').val($('#visitDateFrom').val());
	$('input[name="additionalParam.visitDateTo"]').val($('#visitDateTo').val());
	$('input[name="additionalParam.referralType"]').val($('#referralType').val());
	$('input[name="additionalParam.concernType"]').val($('#concernType').val());
	$('input[name="additionalParam.coordinatorName"]').val($('#coordinatorName').val());
	$('input[name="additionalParam.department"]').val($('#department').val());
	$('input[name="additionalParam.status"]').val(status);

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

function deleteWellnessDetails(element) {
	const id = element !== null ? element.getAttribute("data-wellness-id") : null;
	const indexId = element !== null ? element.getAttribute("id") : null;

	$('#deleteModal').modal('show');
	$('#dynamicId').val(id);

	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + '/' + id, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				'Cache-Control': 'no-cache'
			},
			cache: 'no-cache'
		}).then(response => response.json())
			.then(response => {
				if (response.status === 'Success') {
					$('#' + indexId).parents('tr').remove();
					showToast('Success', 'Wellness details deleted successfully');
				} else {
					showToast('Failure', response.message);
				}
			});
		$('#deleteModalPositive').off('click');
	});
}