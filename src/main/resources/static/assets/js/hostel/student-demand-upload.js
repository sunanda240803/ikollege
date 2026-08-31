function validateFile() {
	const saveButton = $('#uploadButton');
	saveButton.attr('disabled', true);
	updateInvalidDivClass();

	let isValid = true;
	let hasInput = false;
	let hasInvalidValue = false;

	// Continue existing required field and file validation
	const requiredFields = $('input[required], textarea[required]');
	isValid &= validateRadioAndText(requiredFields);

	const fileInput = document.getElementById('uploadFile_fileUpload');
	if (!fileInput) {
		saveButton.attr('disabled', false);
		return false;
	}

	const filePath = fileInput.value;
	const allowedExtensions = /(\.xls|\.xlsx)$/i;

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

	// Validate accheadInputs
	$('input[id^="accheadInputs_"]').each(function () {
		const value = $(this).val().trim();

		if (value !== '') {
			hasInput = true;
			const num = parseFloat(value);
			if (isNaN(num) || num <= 0) {
				hasInvalidValue = true;
			}
		}
	});

	if (!hasInput) {
		showToast('Error', 'At least one input is required in the Account Heads & Charges fields.');
		saveButton.attr('disabled', false);
		return false;
	}

	if (hasInvalidValue) {
		showToast('Error', 'Values in the Account Heads & Charges fields must be greater than zero.');
		saveButton.attr('disabled', false);
		return false;
	}

	if (isValid) {
		$('#studentDemandId').submit();
		return true;
	} else {
		saveButton.attr('disabled', false);
		return false;
	}
}
