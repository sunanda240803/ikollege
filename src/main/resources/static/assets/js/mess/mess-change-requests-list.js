function getMessChangeRequest() {
	$('input[name="additionalParam.messPeriod"]').val($('#messPeriod').val());
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	let search = $('#search').val();
	createUrlWithParams(page, size, search);
}