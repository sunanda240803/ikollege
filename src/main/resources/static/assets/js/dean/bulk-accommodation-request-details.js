
function approveStudentBulkRequest(status, element) {
    const form = document.getElementById('studentBulkUploadViewForm');
    const isMail = element.getAttribute("data-mail") === 'true';
    showLoader();
    $('#approveBtn').attr('disabled', true);
    $('#approveWithConditionBtn').attr('disabled', true);
    $('#rejectBtn').attr('disabled', true);
    form.action = contextPath + '/public' + updateAndApproveUrl + "/" + status;
    const formData = new FormData(form);

    $.ajax({
        url: form.action,
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            hideLoader();
            if (response.status === 'Success') {
                showToast('Success', response.message);
                setTimeout(function() {
                    if (isMail) {
                        window.location.href = contextPath;
                    } else {
                        window.location.href = contextPath + baseUrl;
                    }
                }, 5000);
            } else {
                showToast('Error', response.message);
                $('#approveBtn').attr('disabled', false);
                $('#approveWithConditionBtn').attr('disabled', false);
                $('#rejectBtn').attr('disabled', false);
            }
        },
        error: function (response) {
            hideLoader();
            $('#approveBtn').attr('disabled', false);
            $('#approveWithConditionBtn').attr('disabled', false);
            $('#rejectBtn').attr('disabled', false);
            showToast("Error", response.message);
        }
    });
}

function handleActionClick(element) {
    const url = element.getAttribute('data-url');
    if (url) {
        window.location.href = contextPath + '/' +url;
    }
}