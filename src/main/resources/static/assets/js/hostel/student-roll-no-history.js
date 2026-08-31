function validateSearchField() {
    const requiredFields = $('input[required], select[required], textarea[required]');
    const isValid = validateRadioAndText(requiredFields);
    if (isValid) {
        let page = $('#pageVal').val();
        let size = $('#sizeVal').val();
        let search = $('#studentId').val();
        createUrlWithParams(page, size, search);
    } else {
        showToast('Error', 'Please fill all required fields.');
        return false;
    }
    return isValid;
}