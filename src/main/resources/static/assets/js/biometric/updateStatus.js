$(document).ready(function () {
    new DataTable('.table');
});

function pushWithinMess(element, pushRemove) {
    const jElement = $('#' + element.id);
    jElement.attr('disabled', 'disabled');
    const messId = jElement.attr('data-config-id');
    fetch(contextPath + "/messDetails/updateStatus/" + pushRemove + '/' + messId)
        .then(response => response.json())
        .then(response => {
            $('#result_' + messId).html('Result: ' + response + ' updated to ' + pushRemove);
            jElement.removeAttr('disabled');
        });
}

function pushRemoveToAllMess(element, pushRemove) {
    const jElement = $('#' + element.id);
    jElement.attr('disabled', 'disabled');
    fetch(contextPath + "/messDetails/updateStatus/" + pushRemove + '/' + 0)
        .then(response => response.json())
        .then(response => {
            $('#resultAll').html('Result: ' + response + ' updated to ' + pushRemove);
            jElement.removeAttr('disabled');
        });
}

function showTerminalDetails(element) {
    const terminalElement = $('#' + element.id);
    fetch(contextPath + "/messDetails/updateTerminal/" + terminalElement.attr('data'))
        .then(response => response.text())
        .then(response => {
            const index = element.id.split("_")[1];
            console.log(index);
            $('#terminal_details_' + index).html(response);
            $('#terminalModal').show();
        });
}

function closeDiv(element) {
    const cancelButton = $('#' + element.id);
    // cancelButton.pa
}

function togglePassword(element) {
    const index = element.id.split("_")[1];
    let passwordField = $("#password_" + index);
    let icon = $(this).find("i");

    if (passwordField.attr("type") === "password") {
        passwordField.attr("type", "text");
        icon.removeClass("fa-eye").addClass("fa-eye-slash");
    } else {
        passwordField.attr("type", "password");
        icon.removeClass("fa-eye-slash").addClass("fa-eye");
    }
}