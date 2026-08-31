const logContainer = $("#log-container");
let autoRefreshLogs = false;
let autoScroll = false;

$(document).ready(function () {
    $('#autoRefreshLogs').prop('checked', true)
    $('#autoScroll').prop('checked', true)
    toggleAutoRefresh();
    toggleAutoScroll();
    logContainer.on("scroll", function () {
        const isAtBottom = logContainer[0].scrollHeight - logContainer.scrollTop() <= logContainer.outerHeight() + 5;
        autoScroll = isAtBottom;
       $('#autoScroll').prop('checked', isAtBottom);
    });
    fetchLogs();
})

function fetchLogs() {
    fetch(contextPath + "/api/logs/" + $('#log-tag').html())
        .then(response => response.json())
        .then(data => {
            for (const log of data) {
               if (log.includes("__UPLOAD_SUCCESS__")) {
                    showToast("Success", "Upload completed successfully");
                }
                else if (log.includes("__MAIL_SUCCESS__")) {
                     showToast("Success", "Mails added in queue successfully");
                 }
                let color = ''
                if (log.toString().startsWith("INFO:")) color = 'info';
                else if (log.toString().startsWith("ERROR:")) color = 'danger';
                else if (log.toString().startsWith("WARNING:")) color = 'warning';
                logContainer.append('<div><label class="text-' + color + '">' + log + '</label></div>');
                /*if (autoScroll) {
                    var scrollHeight = logContainer[0].scrollHeight;

                    // Animate the scrollTop property to the scrollHeight
                    logContainer.animate({
                        scrollTop: scrollHeight
                    }, 50); // 500ms duration for the animation
                }*/
            }
            if (autoScroll) {
                logContainer.scrollTop(logContainer[0].scrollHeight);
            }
            const dt = new Date();
            const time =
                dt.getHours().toString().padStart(2, "0") + ":" +
                dt.getMinutes().toString().padStart(2, "0") + ":" +
                dt.getSeconds().toString().padStart(2, "0");
            $('#lastRefresh').html("Last refresh @" + time);
            if (autoRefreshLogs) {
                startFetchLogs();
            }
        });
}

function startFetchLogs() {
    setTimeout(fetchLogs, 1000);
}

function toggleAutoRefresh() {
    autoRefreshLogs = $('#autoRefreshLogs').is(":checked");
}

function toggleAutoScroll() {
    autoScroll = $('#autoScroll').is(":checked");
}

function clearUILogs() {
    logContainer.html("");
}

function downloadExcelReport(){
    const fromDate = $('#fromDate').val();
    const toDate = $('#toDate').val();
    let studentId = $('#studentId').val();
    let messId = $('#messId').val();
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const isFieldsValid = validateRadioAndText(requiredFields);
    if(isFieldsValid) {
        showLoader();
        const params = new URLSearchParams({
            messId: messId,
            studentId: studentId,
            fromDate: fromDate,
            toDate: toDate
        });
       fetch(`${contextPath}/frAdmin${downloadExcelURL}?${params.toString()}`)
                       .then(response => {
                           if (!response.ok) {
                               throw new Error("Download failed");
                           }
                           return response.blob();
                       })
                       .then(blob => {
                           const url = window.URL.createObjectURL(blob);
                           const a = document.createElement("a");
                           a.href = url;
                           a.download = "DeviceFrLogReport.xlsx";
                           document.body.appendChild(a);
                           a.click();
                           a.remove();
                           window.URL.revokeObjectURL(url);
                           hideLoader();
                           showToast('Success', 'Excel Downloaded successfully!.');
                       })
                       .catch(error => {
                           console.error(error);
                           hideLoader();
                           showToast('Error', 'Failed to download Excel.');
                       });
    } else {
        showToast('Error', 'Please enter the required details.');
    }
}