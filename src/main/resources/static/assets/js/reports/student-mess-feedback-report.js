function generateReport(){
    const requiredFields = $('select[required]:visible');
    let result = validateRadioAndText(requiredFields);
    if(result){
        const messPeriod = $('#messPeriod').val();
        const reportType = $('#reportType').val();
        let url = contextPath+baseURL+downloadURL+'/'+messPeriod+'/'+reportType;
        window.open(url, '_blank');
    }
}