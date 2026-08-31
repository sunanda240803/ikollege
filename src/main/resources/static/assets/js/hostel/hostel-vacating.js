let isSubmitting = false;
$(document).ready(function () {
	let phNumMaskElementArray = ['#mobileNumber']
	inputMask(phNumMaskElementArray, mobileNumberMask);
	$('#reasonSelect').on('change', function () {
		const selectedValue = $(this).val();
		toggleSection('#otherReasonContainer', selectedValue === 'Others');
		toggleSection('#periodOfAbsence', selectedValue === 'Exchange/Internship prog');
		toggleSection('.bankAndDonationContainer', selectedValue !== 'Exchange/Internship prog');
        if (selectedValue === 'Exchange/Internship prog') {
            $('#donationContainer').addClass(displayNone).find('[required]').removeAttr('required');
            $("input[name='donationStatus']").prop("checked", false);
            $('#toWhom').val('');
            $('#donationAmount').val('');
            $('#description').val('');
            $('#hostelName').val('').selectpicker('refresh');
        }
    });

	$("input[name='donationStatus']").on("change", function () {
		const isDonation = $(this).val() === 'true';
        toggleDonationSection('#donationContainer', isDonation);
		if (!isDonation) {
			$('#toWhom').val('');
			$('#donationAmount').val('');
            $('#description').val('');
            $('#hostelName').val('').selectpicker('refresh');
            toggleDonationSection('#descriptionContainer', false);
		}
	});

	$("#toWhom").on("change", function () {
		const value = $(this).val();
        toggleDonationSection("#descriptionContainer", value === "Others");
        toggleDonationSection("#hostelNameContainer", value === "Hostel");
	});

	$('#saveId').off("click").on("click", function (e) {
		e.preventDefault();
		validateAndSubmit('#vacatingForm');
	});

	$('#hostelVacatingSave').off("click").on("click", function (e) {
		e.preventDefault();
		validateAndSubmit('#vacatingForm');
	});

});

async function validateAndSubmit(formSelector) {
	if (isSubmitting) {
		return false;
	}

	isSubmitting = true;
	const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
	const emailID = $('#email');
	const hostelVacatingSave1 = $('#hostelVacatingSave');
	const hostelVacatingSave2 = $('#saveId');
	const backBtn = $('#cancelButton');
	const cancelButton = $('#cancel');
	$('.selectpicker').selectpicker('refresh');
	let isHostelName = true;
	const hostelSectionVisible = $('#hostelName').is(':visible') && $('#hostelName').is(':visible');
	if (hostelSectionVisible) {
		isHostelName = valRequiredSelect('.selectpicker');
	}

	const dateRanges = [
		{fromElement: "exchangeProgFromDateId", toElement: "exchangeProgToDateId"}
	];

	const isFieldsValid = validateRadioAndText(requiredFields);
	let isDateRangeValid = true;
	const dateSectionVisible = $('#exchangeProgFromDateId').is(':visible') && $('#exchangeProgToDateId').is(':visible');
	if (dateSectionVisible) {
		isDateRangeValid = validateBetweenDateRanges(dateRanges);
	}

	const isEmailValid = validateEmailMsg(emailID[0], true);
	let mobileNumberValid = validateMobileNum($('#mobileNumber')[0], true) !== false;
	let ifscStatus = true;
	const ifscCodeVisible = $('#ifscCode').is(':visible');
	if (ifscCodeVisible) {
		ifscStatus = await validateAndCheckIfscCode(document.getElementById('ifscCode'), 'One');
	}

	if (isFieldsValid && isEmailValid && isDateRangeValid && mobileNumberValid && ifscStatus && isHostelName) {
		hostelVacatingSave1.attr('disabled', true);
		hostelVacatingSave2.attr('disabled', true);
		backBtn.attr('disabled', true);
		cancelButton.attr('disabled', true);
		$('.masked').inputmask('');
		$(formSelector).submit();
	} else {
		isSubmitting = false;
		showToast('Error', 'Please fill all the mandatory fields and submit.');
		hostelVacatingSave1.attr('disabled', false);
		hostelVacatingSave2.attr('disabled', false);
		backBtn.attr('disabled', false);
		cancelButton.attr('disabled', false);
		return false;
	}
}
function toggleSection(selector, show) {
	if (show) {
		$(selector).removeClass(displayNone).find('[data-required="true"]').attr('required', true);
	} else {
		$(selector).addClass(displayNone).find('[required]').removeAttr('required');
        $('#otherReason').val('');
        $('#exchangeProgFromDateId').val('');
        $('#exchangeProgToDateId').val('');
        $('#placeId').val('');
        $('#ifscCode').val('');
        $('#bankName').val('');
        $('#branchName').val('');
        $('#bankAccNo').val('');
	}
}

function toggleDonationSection(selector, show) {
    if (show) {
        $(selector).removeClass(displayNone).find('[data-required="true"]').attr('required', true);
    } else {
        $(selector).addClass(displayNone).find('[required]').removeAttr('required');
        $('#otherReason').val('');
        $('#exchangeProgFromDateId').val('');
        $('#exchangeProgToDateId').val('');
        $('#placeId').val('');
    }
}

