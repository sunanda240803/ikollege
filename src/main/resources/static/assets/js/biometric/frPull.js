function pullDataRequest() {
    const pullData = $('#pullData');
    const pullIp = $('#deviceId').val();
    if (pullIp !== '') {
        pullData.attr('disabled', true);
        fetch(contextPath + "/frPull/" + pullIp + "/" + $('#deleteUser').is(':checked'))
            .then(response => response.text())
            .then(message => {
                  showToast('Success', message);
                  pullData.removeAttr('disabled');
            })
            .catch(error => {
                   showToast('Error', error);
                   pullData.removeAttr('disabled');
            });
    }
}