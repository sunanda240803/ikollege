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

	$('#bulkUploadForm').submit();
	return true;
}