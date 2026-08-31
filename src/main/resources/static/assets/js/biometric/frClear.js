function clearUsers() {
    const ip = $('#deviceId').val();
    if (confirm('Are you sure you want to clear the user list for IP ' + ip + '?')) {
        fetch(contextPath + "/frClear/clearUsers?ip=" + ip + "")
            .then(response => response.text())
            .then(message => {
                  showToast('Success', message);
            })
            .catch(error => {
                   showToast('Error', error);
            });
    }
}