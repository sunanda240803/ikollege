function triggerCron(cronName, params) {
    let url = contextPath + "/frScheduler/" + cronName + 'Cron';
    if (params) {
        url += "?" + params;
    }
    //console.log(url);
    fetch(url)
       .then(response => response.text())   // read response body
       .then(message => {
            showToast('Success', message);
            $('#' + cronName + 'Status').html(message);
       })
       .catch(error => {
           showToast('Error', error);
       });
}

function pushRemove() {
    triggerCron('pushRemove')
}

function pushNextSession() {
    triggerCron('pushNextSession')
}

function collectDeviceLogs() {
    const fromDate = $('#dateFrom');
    const toDate = $('#dateTo');
    let params = "";
    if (fromDate.val()) {
        params += "&fromDate=" + fromDate.val();
    }
    if (toDate.val()) {
        params += "&toDate=" + toDate.val();
    }
    triggerCron('collectDeviceLogs', params)
}

function clearDeviceLogs() {
    if (confirm("Are you sure you want to clear the device logs?")) {
        triggerCron('clearDeviceLogs')
    } else {
        console.log('Clear action cancelled');
    }
}

function getSchedulerLogs() {
    fetch(contextPath + "/frScheduler/getSchedulerLogs")
        .then(response => {
                return response.json();
            }
        )
        .then(html => {
            console.log(html);
            const logContainer = $('#schedulerLogs');
            logContainer.html('<ul>');
            html.forEach(log => {
                logContainer.append('<li class="' +
                    (log.log.includes("Error") ? 'text-danger' : 'text-info')+'">'
                    + log.createdAt.replace('T', ' ') + ' -> ' + log.scheduler + ": " + log.log + '</li>');
            });
            logContainer.append('</ul>');
        });
}