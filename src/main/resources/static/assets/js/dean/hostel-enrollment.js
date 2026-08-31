$('#addNewId').click(function() {
    window.location.href=contextPath+baseURL+lateFeeEnrollment;
});

function validateFile() {
    $('.errorList').html('');
    const fileInput = document.getElementById('file_fileUpload');
    const filePath = fileInput.value;
    const allowedExtensions = /(\.xlsx)$/i;

    let fileStatus = true;
    const requiredFields = $('input[required]');
    const isValid = validateRadioAndText(requiredFields);

    if (filePath.trim() !== '') {
        if (!allowedExtensions.exec(filePath)) {
            fileInput.value = '';
            $('.selectFile2').addClass('d-none');
            $('.validFile').removeClass('d-none');
            fileStatus = false;
        }
        $('#studentBulkUploadForm').submit();
        fileStatus = true;
    } else {
        $('.selectFile2').removeClass('d-none');
        fileStatus = false;
    }
    return fileStatus && isValid;
}