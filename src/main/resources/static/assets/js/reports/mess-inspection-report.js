$(document).ready(function() {
    $('.row.mb-5.align-items-center').hide();
});


function checkAdvanceFilter(status) {
	if (!isDateRangeValid('#inspectionFromDate', '#inspectionToDate')) {
    	return;
    }
	let filters = $("#filters").val().replace(/[\[\]{}]/g, '').split(',').map(f => f.trim().split('=')[0]);
	filters.forEach(filter => {
		let safeFilter = CSS.escape(filter);
		let inputName = `additionalParam.${filter}`;
		let value = $(`#${filter}`).val() || '';
		$(`input[name="${inputName}"]`).val(value);
	});
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	//Due to search field not available, used a static variable
	$('#search').val("0");
	let search = $('#search').val();
	if (status !== 'downloadExcel') {
		createUrlWithParams(page, size, search);
	} else {
		let url = baseURL + excelURL;
		createAndReturnUrlWithParams(page, size, search, url);
	}
}

function isDateRangeValid(fromSelector, toSelector) {
    const fromDate = $(fromSelector).val();
    const toDate = $(toSelector).val();
	const isInvalidClass = 'is-invalid';
    if (fromDate && toDate && toDate < fromDate) {
        $('#toDateError').show();
        $('#eventToDate').addClass(isInvalidClass);
        return false;
    } else {
        $('#toDateError').hide();
        $('#eventToDate').removeClass(isInvalidClass);
        return true;
    }
}

