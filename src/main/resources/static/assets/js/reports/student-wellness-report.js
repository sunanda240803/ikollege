$(document).ready(function() {
    $('.row.mb-5.align-items-center').hide();
});

function checkAdvanceFilter(status) {
	if (!isDateRangeValid('#visitFromDate', '#visitToDate') || !isDateRangeValid('#followUpFromDate', '#followUpToDate')) {
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
    const toElem = document.querySelector(toSelector);
    if (fromDate && toDate && toDate < fromDate) {
        toElem.classList.add(isInvalidClass);
        return false;
    } else {
        toElem.classList.remove(isInvalidClass);
        return true;
    }
}