$(document).ready(function() {
	updateInvalidDivClass();
	const receiptType = $('input[name=receiptType]:checked').val();

	if (!receiptType) {
		$('input[name=receiptType][value=fee]').prop('checked', true);
		hiddenFields();
	} else {
		const selectedValue = receiptType;
		$("#cardOption").removeAttr('disabled');
		$("#description").parent('div').addClass(displayNone);

		if (selectedValue === 'mess' || selectedValue === 'rebate') {
			$(".bank").parent('div').addClass(displayNone);
			$(".accountHead").parent('div').removeClass(displayNone);
			$("#fromDate").parent('div').removeClass(displayNone);
			$("#toDate").parent('div').removeClass(displayNone);
			$("#description").parent('div').removeClass(displayNone);
		} else {
			hiddenFields();
		}

		if (selectedValue === 'dayScholar') {
			$("#cardOption").attr('disabled', true);
		}

		if (selectedValue === 'scholarship') {
			$("#description").parent('div').removeClass(displayNone);
		}

		if (selectedValue === 'fee') {
			$(".feeTypeSection").removeClass(displayNone);
		} else {
			$(".feeTypeSection").addClass(displayNone);
		}
	}

	$('input[name=receiptType]').on('change', function() {
		$(".is-invalid").removeClass(errorClass)
		const selectedValue = $(this).val();

		$("#cardOption").removeAttr('disabled')
		$("#description").parent('div').addClass(displayNone);

		if (selectedValue === 'mess' || selectedValue === 'rebate') {
			$(".bank").parent('div').addClass(displayNone);
			$(".accountHead").parent('div').removeClass(displayNone);
			$("#fromDate").parent('div').removeClass(displayNone);
			$("#toDate").parent('div').removeClass(displayNone);
			$("#description").parent('div').removeClass(displayNone);
		} else {
			hiddenFields();
		}

		if (selectedValue == 'dayScholar') $("#cardOption").attr('disabled', true)
		if (selectedValue == 'scholarship') $("#description").parent('div').removeClass(displayNone);
		if (selectedValue === 'fee') $(".feeTypeSection").removeClass(displayNone);
		else $(".feeTypeSection").addClass(displayNone);
	});
});

function hiddenFields() {
	$(".bank").parent('div').removeClass(displayNone);
	$(".accountHead").parent('div').addClass(displayNone);
	$("#fromDate").parent('div').addClass(displayNone);
	$("#toDate").parent('div').addClass(displayNone);
	$("#description").parent('div').addClass(displayNone);
}

function validateSearchField() {
	updateInvalidDivClass();
	const requiredFields = $('#listDetailsId').find('input[required]');
	const isValid = validateRadioAndText(requiredFields);
	if (isValid) {
		const filter = $('#filter').val();
		const receiptTypeList = $('#receiptTypeList').val();
		const selectedRadio = $('input[name="bookType"]:checked').val();
		$('input[name="additionalParam.fromDate"]').val($('#csvFrmDate').val());
		$('input[name="additionalParam.toDate"]').val($('#csvToDate').val());
		$('input[name="additionalParam.bookType"]').val(selectedRadio);
		$('input[name="additionalParam.receiptTypeList"]').val(receiptTypeList);
		$('input[name="additionalParam.filter"]').val(filter);
		let page = $('#pageVal').val();
		let size = $('#sizeVal').val();
		let search = $('#search').val();
		createUrlWithParams(page, size, search);
	}
	return isValid;
}

function downloadExcel() {
    console.log("Download triggered");
    const fromDate = $('#csvFrmDate').val() || '';
    const toDate = $('#csvToDate').val() || '';
    const receiptTypeList = $('#receiptTypeList').val() || '';
    const bookType = $('input[name="bookType"]:checked').val() || '';
    const filter = $('#filter').val() || '';

    let url = `${contextPath}${baseURL}${downloadExcelReportURL}?`;

    url += `additionalParam.fromDate=${fromDate}&`;
    url += `additionalParam.toDate=${toDate}&`;
    url += `additionalParam.bookType=${bookType}&`;
    url += `additionalParam.receiptTypeList=${receiptTypeList}&`;
    url += `additionalParam.filter=${filter}`;

    window.location.href = url;
    return false;
}

function downloadExcelTemplate() {
	const receiptType = $('input[name=receiptType]:checked').val();
	window.location.href = `${contextPath}${baseURL}${downloadURL}?receiptType=${receiptType}`;
}

function validateBackend() {
	$('#uploadReceiptBulkUpload').submit();
}

function validateFile() {
	const saveButton = $('#uploadButton');
	saveButton.attr('disabled', true);
	updateInvalidDivClass();

	let isValid = true;
	let textValid = true;
	let isSelValid = true;
	let requiredFields = $('#uploadReceiptBulkUpload').find('input[required]:visible');

	isValid = validateRadioAndText(requiredFields);
	const receiptType = $('input[name=receiptType]:checked').val();

	if (receiptType !== null) {
		if (receiptType === 'mess' || receiptType === 'rebate') {
			const ah = $('#accountHead');
			isSelValid = valRequiredSelection(ah);
			ah.parent('div').removeClass(errorClass);
			if (ah.val() === "") {
				ah.parent('div').addClass(errorClass);
			}
			let textField = $('#uploadReceiptBulkUpload').find('textarea[required]');
			textValid = validateRadioAndText(textField)
		} else {
			const bank = $('#bank');
			isSelValid = valRequiredSelection(bank);
			bank.parent('div').removeClass(errorClass);
			if (bank.val() === "") {
				bank.parent('div').addClass(errorClass);
			}
		}

		if (receiptType === 'scholarship') {
			let textField = $('#uploadReceiptBulkUpload').find('textarea[required]');
			textValid = validateRadioAndText(textField)
		}
	}
	isValid = isValid && textValid;

	var fileInput = document.getElementById('uploadFile_fileUpload');

	if (!fileInput) {
		saveButton.attr('disabled', false);
		return false;
	}

	var filePath = fileInput.value;
	var allowedExtensions = /(\.xls|\.xlsx)$/i;

	if (filePath.trim() === '') {
		$('.selectFile2').removeClass(displayNone);
		$('.validFile').addClass(displayNone);
		saveButton.attr('disabled', false);
		return false;
	}

	if (!allowedExtensions.exec(filePath)) {
		fileInput.value = '';
		$('.selectFile2').addClass(displayNone);
		$('.validFile').removeClass(displayNone);
		saveButton.attr('disabled', false);
		return false;
	}

	$('.selectFile2').addClass(displayNone);
	$('.validFile').addClass(displayNone);

	if (isValid && isSelValid) {
		$('#uploadReceiptBulkUpload').submit();
		return true;
	} else {
		saveButton.attr('disabled', false);
		return false;
	}
}