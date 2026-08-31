function getHdcComplaintReport() {
	// Set these values in the additional param inputs
	$('input[name="additionalParam.complaintFromDate"]').val($('#complaintFromDate').val());
	$('input[name="additionalParam.complaintToDate"]').val($('#complaintToDate').val());
	$('input[name="additionalParam.hostelId"]').val($('#hostelId').val());

	// Call url with params with the current page and size
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	let search = $('#search').val();

	let url = baseURL + reportURL + downloadExcelURL;
	createAndReturnUrlWithParams(page, size, search, url);
}