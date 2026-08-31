$(document).ready(function () {
})

function fetchLogs(id, tag) {
    console.log(id + ":" + tag);
    fetch(contextPath + "/api/logs" + tag)
        .then(response => response.json())
        .then(data => {
            const logContainer = $('#log-container_' + id);
            for (const log of data) {
                let color = ''
                if (log.toString().startsWith("INFO:")) color = 'info';
                else if (log.toString().startsWith("ERROR:")) color = 'danger';
                else if (log.toString().startsWith("WARNING:")) color = 'warning';
                logContainer.html('<div><label class="f-small text-' + color + '">' + log + '</label></div>' + logContainer.html());
            }
            const dt = new Date();
            const time =
                dt.getHours().toString().padStart(2, "0") + ":" +
                dt.getMinutes().toString().padStart(2, "0") + ":" +
                dt.getSeconds().toString().padStart(2, "0");
            $('#lastRefresh').html("Last refresh @" + time);
        });
}

function startFetchLogs(id, tag) {
    setTimeout(function () {
        fetchLogs(id, tag)
    }, 1000);
}