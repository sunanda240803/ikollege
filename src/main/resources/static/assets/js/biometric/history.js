function getData() {
    const messSelect = $('#messId');
    const date = $('#date');
    const status = $('#status');
    let url = contextPath + "/history?messId=" + messSelect.val();
    if (date.val()) {
        url += "&date=" + date.val();
    }
    if (status.val()) {
        url += "&status=" + status.val();
    }
    window.location.href = url;
}