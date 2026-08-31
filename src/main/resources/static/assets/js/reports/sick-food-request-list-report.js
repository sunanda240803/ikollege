jQuery(document).ready(function() {
	$('#additionalButton').click(function() {
		checkAdvanceFilter('downloadExcel');
	});
});

function checkAdvanceFilter(status) {
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
		let url = "/" + $('#excelUrl').val();
		createAndReturnUrlWithParams(page, size, search, url);
	}
}