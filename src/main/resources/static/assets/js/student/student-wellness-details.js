$(document).ready(function() {
	const addNew = $('#addNew').val();
	const saveButton = $('#saveButton').val();
	if (addNew === 'Disable') {
		$('#addNewId').addClass(displayNone);
	}

	if (saveButton === 'saved') {
		$("#updateId i").attr("class", "fa-solid fa-floppy-disk me-1");
		$("#updateId span").text("Save");
		$('#updateId').removeClass('btn-blue').addClass('btn-aqua');
	}
	$('#updateId').click(function() {
		validateForm();
	});
	$('#additionalButton').click(function() {
		const id = $('#id').val();
		const url = contextPath + baseURL + pdfWellnessDetailsURL + '/' + id;
		window.open(url, '_blank');
	});

	let phNumMaskElementArray = ['#referralPhone, #otherStudPhone'];
	inputMask(phNumMaskElementArray, mobileNumberMask);

	function toggleReferralFields() {
		var referralType = $('#referralType').val();
		$('.others').addClass('d-none');
		$('.othersDesc').addClass('d-none');

		if (referralType === "Faculty" || referralType === "Student Body") {
			$('.others').removeClass('d-none');
			$('#referralOthersDescription').val('');
		} else if (referralType === "Self") {
			$('#referralOthersDescription').val('');
			$('#referralBy').val('');
			$('#referralPhone').val('');
			$('#referralEmail').val('');
			$('#referralLandlineNum').val('');
			$('.others').addClass('d-none');
			$('.othersDesc').addClass('d-none');
		} else if (referralType === "Others") {
			$('.others').removeClass('d-none');
			$('.othersDesc').removeClass('d-none');
		}
	}

	function toggleConcernFields() {
		var concernType = $('#concernType').val();
		$('.othersConcern').addClass('d-none');

		if (concernType === "Others") {
			$('.othersConcern').removeClass('d-none');
		} else {
			$('#concernOthersDescription').val('');
		}
	}

	function toggleSelfHarmFields() {
		var selfHarmValue = $('input[name="selfHarm"]:checked').val();
		if (selfHarmValue === "true") {
			$('.selfHarmType').removeClass('d-none');
		} else {
			$('#selfHarmType').val('');
			$('.selfHarmType').addClass('d-none');
		}
	}

	function togglePsychiatricFields() {
		var psychiatricValue = $('input[name="psychiatricConsultation"]:checked').val();
		if (psychiatricValue === "true") {
			$('.psychiatricName').removeClass('d-none');
		} else {
			$('#psychiatricName').val('');
			$('.psychiatricName').addClass('d-none');
		}
	}

	toggleReferralFields();
	toggleConcernFields();
	toggleSelfHarmFields();
	togglePsychiatricFields();
	$('#concernType').on('change', toggleConcernFields);
	$('#referralType').on('change', toggleReferralFields);
	$('input[name="selfHarm"]').on('change', toggleSelfHarmFields);
	$('input[name="psychiatricConsultation"]').on('change', togglePsychiatricFields);

	$("#saveFormBackend").on("click", function(e) {
		e.preventDefault();
		$('#wellnessFormId').submit();
	});
});

function validateForm() {
	const saveButton = $('#saveForm');
	const saveButtonHeader = $('#updateId');
	saveButton.attr('disabled', true);
	saveButtonHeader.attr('disabled', true);
	updateInvalidDivClass();

	let isValid = true;
	const requiredFields = $('input[required], select[required], textarea[required]');
	isValid &= validateRadioAndText(requiredFields);

	if ($('#category').val() === 'Others' && ($('#id').val() === null || $('#id').val() === '')) {
		isValid &= validateEmailMsg($('#otherStudEmail')[0], true);
	}

	const referralType = $('#referralType').val();
	const concernType = $('#concernType').val();
	const selfHarmValue = $('input[name="selfHarm"]:checked').val();
	const psychiatricValue = $('input[name="psychiatricConsultation"]:checked').val();

	if (["Faculty", "Student Body", "Others"].includes(referralType)) {
		isValid &= validateRadioAndText('#referralBy');
		isValid &= validateEmailMsg($('#referralEmail')[0], false);
	}
	if (referralType === "Others") {
		isValid &= validateRadioAndText('#referralOthersDescription');
	}

	if (concernType === "Others") {
		isValid &= validateRadioAndText('#concernOthersDescription');
	}

	if (selfHarmValue === "true") {
		isValid &= validateRadioAndText('#selfHarmType');
	}
	if (psychiatricValue === "true") {
		isValid &= validateRadioAndText('#psychiatricName');
	}

	isValid &= validateEmailMsg($('#coordinatedEmail')[0], true);

	if (isValid) {
		$('#wellnessFormId').submit();
	} else {
		saveButton.attr('disabled', false);
		saveButtonHeader.attr('disabled', false);
		showToast('Error', 'Please fill all the mandatory fields and submit.');
		return false;
	}
}
