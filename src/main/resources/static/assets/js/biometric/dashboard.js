$(document).ready(function () {
    new DataTable('.table');
    checkExpiryDate();
});

function checkExpiryDate() {
    const expiryDate = $('#expiryDate');
    const messPeriod = $('#messPeriodAsExpiry');
    expiryDate.removeAttr('disabled');
    if (messPeriod.is(':checked')) {
        expiryDate.attr('disabled', true);
    }
}

function showData(data) {
    const resultHtml = $('#result');
    resultHtml.html('');
    resultHtml.append("<ul>")
    data.split("|~|").forEach(function (item) {
        if (item !== '') {
            resultHtml.append("<li>" + item + "</li>");
        }
    })
    resultHtml.append("</ul>")
}

function pushData(element, pullOrPush) {
    startStopLoader(true);
    const pushIp = $('#' + pullOrPush + 'DeviceId');
    const pushIpErr = $('#' + pullOrPush + 'DeviceIdErr');
    let error = false;
    pushIp.removeClass('is-invalid')
    if (pushIp.val() === '') {
        pushIpErr.html('Please enter the IP');
        pushIp.addClass('is-invalid')
        error = true;
    } else if (pushIp.val().split(".").length !== 4) {
        pushIpErr.html('Please enter a valid IP');
        pushIp.addClass('is-invalid')
        error = true;
    }
    pushDataAjax(error, element, pullOrPush);
}

function pushByMess(element) {
    pushDataAjax(false, element, "pushByMess")
}

function pushDataAjax(error, element, pullOrPush) {
    const expiryDate = $('#expiryDate');
    const messPeriod = $('#messPeriodAsExpiry');
    expiryDate.removeClass('is-invalid');
    if (!messPeriod.is(':checked') && expiryDate.val() === '') {
        expiryDate.addClass('is-invalid');
        error = true;
    }
    $('#serialNumberHidden').val($('#' + element.id).attr('data-config-id'))
    if (!error) {
        $.ajax({
            url: contextPath + "/frDashboard/" + pullOrPush,
            type: 'POST',
            data: $('#fr-form').serialize(),
            success: function (data) {
                showToast('Success', data);
                showData(data);
                startStopLoader(false);
            },
            error: function (data) {
                startStopLoader(false);
            }
        });
    } else {
        startStopLoader(false);
    }
}

function startStopLoader(loader) {
    if (loader) showLoader();
        else hideLoader();
}

function togglePin(btn) {
    const userId = btn.getAttribute("data-user-id");
    const input = document.getElementById("pinInput_" + userId);
    const icon = btn.querySelector("i");

    if (!input) {
        console.error("Input not found for user: " + userId);
        return;
    }

    if (input.type === "password") {
        input.type = "text";
        icon.classList.replace("fa-eye", "fa-eye-slash");
        btn.setAttribute("title", "Hide PIN");
    } else {
        input.type = "password";
        icon.classList.replace("fa-eye-slash", "fa-eye");
        btn.setAttribute("title", "View PIN");
    }
    let tooltip = bootstrap.Tooltip.getInstance(btn);
    if (tooltip) {
        tooltip.setContent({ '.tooltip-inner': btn.getAttribute("title") });
    }
}

function activeOrInactiveStudent(element) {
    const studentId = element.getAttribute("data-student-id");
    const activeStatus = element.getAttribute("data-student-status");
    $('#activeInactiveStudentModal').modal('show');
    $('#deleteModalText').text(`Are you sure you want to ${activeStatus === 'Y' ? 'inactive' : 'active'} the student ?.`);
    $('#activeInactiveStudentModalPositive').off("click").on("click", function () {
        showLoader();
        $.ajax({
            url: `${contextPath}/frDashboard${updateURL}/${studentId}/${activeStatus}`,
            method: "POST",
            success: function(response) {
                hideLoader();
                showToast(response.status, response.message);
                setTimeout(function() {
                    location.reload();
                }, 5000);
            },
            error: function(xhr) {
                hideLoader();
                showToast('Error', 'Failed to update student status.');
            }
        });
    });
}
