function generateReport() {
    const requiredFields = $('input[required]:visible');
    let result = validateRadioAndText(requiredFields);
    if (result) {
        const reportType = $('input[name="reportType"]:checked').val();
        let url = contextPath + baseURL + downloadURL + '/' + reportType;
        window.open(url, '_blank');
    }
}
function generateCouponReport() {
    const requiredFields = $('input[required]:visible');
    const submittedFromDate = $('#submittedFromDate').val();
    const submittedToDate = $('#submittedToDate').val();
    const hostelId = $('#hostelId').val();
    let result = validateRadioAndText(requiredFields);
    if (result) {
        const reportType = $('input[name="reportType"]:checked').val();
        let url = `${contextPath}${baseURL}${downloadURL}?type=${reportType}&submittedFromDate=${submittedFromDate}&submittedToDate=${submittedToDate}&hostelId=${hostelId}`;
        window.open(url, '_blank');
    }
}