$(document).ready(function () {
    $("#additionalButton").on("click", function () {
        let page = 1;
        let size = 0;
        let search = $('#hostelType').val();    
        createAndReturnUrlWithParams(page, size, search, excelUrl);
    });
    
});

function getStudentBalanceReport() {
    let hostelId = $('#hostelId').val();
    let studentBalance = $('#studentBalance').val();

    // Set these values in the additional param inputs
    $('input[name="additionalParam.hostelId"]').val(hostelId);
    $('input[name="additionalParam.studentBalance"]').val(studentBalance);

    let page = $('#pageVal').val() ?? 1;
    let size = $('#sizeVal').val() ?? 25;
    let search = $('#search').val() ?? '';
    console.log("Page: " + page + ", Size: " + size + ", Search: " + search);
    createUrlWithParams(page, size, search);
}