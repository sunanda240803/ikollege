$(document).ready(function() {
	$('#additionalButton').click(function() {
		getGeneralList('downloadExcel');
	});

});

function getGeneralList(status) {
	let isValid = true;
	const requiredFields = $('input[required]');
	const isSelValid = valRequiredSelection('#accHead');
	isValid &= validateRadioAndText(requiredFields);

	var ah = $('#accHead');
	$('#accHead').parent('div').removeClass('is-invalid').removeClass('is-valid');
	if (ah.val() === "") {
		$('#accHead').parent('div').addClass('is-invalid');
	} else {
		$('#accHead').parent('div').addClass('is-valid');
	}
	const dateRanges = [
		{ fromElement: "fromDate", toElement: "toDate" }
	];
	const isDateRangeValid = validateBetweenDateRanges(dateRanges);
	if (isValid && isSelValid && isDateRangeValid) {
		const type = $('input[name="bookType"]:checked').val();
		$('input[name="additionalParam.accHead"]').val($('#accHead').val());
		$('input[name="additionalParam.hostelId"]').val($('#hostelName').val());
		$('input[name="additionalParam.fromDate"]').val($('#fromDate').val());
		$('input[name="additionalParam.toDate"]').val($('#toDate').val());
		$('input[name="additionalParam.bookType"]').val(type);

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
}