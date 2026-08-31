$('#additionalButton').click(function () {
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
    let url = baseURL + downloadURL;
    createAndReturnUrlWithParams(page, size, search, url);
});