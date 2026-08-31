function validateForm() {
    const requiredFields = $('input[required], select[required], textarea[required]');
    if(valRequiredMultiTextRadio(requiredFields)){
        $('#messAllottedDinedForm').submit();
    }
}

 