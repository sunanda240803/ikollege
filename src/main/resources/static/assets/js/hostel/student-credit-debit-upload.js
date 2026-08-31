function validateFile() {
	const saveButton = $('#uploadButton');
	saveButton.attr('disabled', true);
	updateInvalidDivClass();

	let isValid = true;
	const requiredFields = $('input[required], textarea[required]');
	const isSelValid = valRequiredSelection('#accountHead');
	isValid &= validateRadioAndText(requiredFields);

	var ah = $('#accountHead');
	$('#accountHead').parent('div').removeClass('is-invalid');
	if (ah.val() === "") {
		$('#accountHead').parent('div').addClass('is-invalid');
	}

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

	if (isValid && isSelValid) {
		$('#studentCreditDebitId').submit();
		return true;
	} else {
		saveButton.attr('disabled', false);
		return false;
	}
}