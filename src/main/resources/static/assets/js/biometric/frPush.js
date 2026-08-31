function loadMessDevices() {
    const messSelect = $('#messId');
    if (messSelect.val() !== '') {
        $.ajax({
            url: contextPath + "/frPush/getMessDevices?messId=" + messSelect.val(),
            type: "GET",
            success: function (data) {
                const deviceSelect = $('#messDeviceIp');
                deviceSelect.html('');
                deviceSelect.append($("<option value=\"\">Select Device</option>"));
                deviceSelect.append($("<option value=\"0\">All Devices</option>"));
                if (data.length > 0) {
                    deviceSelect.removeAttr('disabled')
                    $.each(data, function (index, device) {
                        const optionData = $("<option></option>")
                        optionData.attr('value', device.terminalIp);
                        optionData.text(device.terminalIp);
                        deviceSelect.append(optionData);
                    });
                } else {
                    deviceSelect.attr('disabled', 'disabled')
                }
            }
        });
    }
}
function pushDataRequest() {
    const pushData = $('#pushData');
    const messSelect = $('#messId');
    const messDeviceSelect = $('#messDeviceIp');
    if (messSelect.val() !== '' && messDeviceSelect.val() !== '') {
        pushData.attr('disabled', true);
        fetch(contextPath + "/frPush/" + messSelect.val() + "/" + messDeviceSelect.val().replaceAll('.', "-"))
                    .then(response => response.text())
                    .then(message => {
                        showToast('Success', message);
                        pushData.removeAttr('disabled');
                    })
                    .catch(error => {
                        showToast('Error', error);
                        pushData.removeAttr('disabled');
                    });
    }
}