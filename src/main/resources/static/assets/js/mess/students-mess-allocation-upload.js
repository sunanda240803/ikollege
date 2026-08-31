$(document).ready(function() {
	$("input[name='messPeriod']").on("change", updateTypes);
});

function updateTypes() {
	const selectedType = $('input[name="messPeriod"]:checked').val();
	if (selectedType !== "Current") {
		$("#change").prop("disabled", true).prop("checked", false);
		$("#remove").prop("disabled", true).prop("checked", false);
		$("#regular").prop("disabled", false).prop("checked", true);
	} else {
		$("#regular").prop("disabled", false);
		$("#change").prop("disabled", false);
		$("#remove").prop("disabled", false);
	}
}

function validateFile() {
	const saveButton = $('#uploadFileButton');
	saveButton.attr('disabled', true);
	updateInvalidDivClass();

	let isValid = true;
	const requiredFields = $('input[required]');
	isValid &= validateRadioAndText(requiredFields);

	var fileInput = document.getElementById('uploadFile_fileUpload');

	if (!fileInput) {
		saveButton.attr('disabled', false);
		return false;
	}

	var filePath = fileInput.value;
	var allowedExtensions = /(\.xls|\.xlsx)$/i;

	if (filePath.trim() === '') {
		$('.selectFile2').removeClass('d-none');
		$('.validFile').addClass('d-none');
		saveButton.attr('disabled', false);
		return false;
	}

	if (!allowedExtensions.exec(filePath)) {
		fileInput.value = '';
		$('.selectFile2').addClass('d-none');
		$('.validFile').removeClass('d-none');
		saveButton.attr('disabled', false);
		return false;
	}

	$('.selectFile2').addClass('d-none');
	$('.validFile').addClass('d-none');

	if (!isValid) {
		saveButton.attr('disabled', false);
		return false;
	}

	$('#studentMessAllocationId').submit();
	return true;
}

function downloadTemplateURL() {
	updateInvalidDivClass();
	let isValid = validateRadioAndText('input[name="allocationType"]');
	if (isValid) {
		const allotmentType = $('input[name="allocationType"]:checked').val();
		if (allotmentType) {
			const url = contextPath + baseURL + templateURL + "/" + allotmentType;
			window.open(url, '_blank');
		}
	}
}