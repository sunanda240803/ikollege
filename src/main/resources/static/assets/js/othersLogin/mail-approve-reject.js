$(document).ready(function () {
    approveRejectRequest($('#status').val());
});

function approveRejectRequest(element) {
    $('#infoModal').modal('show');
    const infoModalPositive = $("#infoModalPositive");

    $('#deleteModalText').html(`
            <div class="mb-3 themeForm">
                <textarea type="text" class="form-control" id="rejectReason" placeholder="Enter notes" required></textarea>
                <div class="invalid-feedback">Notes Required</div>
            </div>
    `);

    if (element === "Rejected") {
        $('modalheader').text("Are you sure you want to Reject this request ?");
    } else {
        $('modalheader').text("Are you sure you want to Approve this request ?");
    }

    infoModalPositive.off("click").on("click", function (e) {
        const requiredFields = $('textarea[required]');
        let status = element === 'Approve' ? true : valRequiredMultiTextRadio(requiredFields);

        if (status) {
            $.ajax({
                url: contextPath + baseURL + otherCandidateUrl  + '/' + $('#key').val(),
                type: 'POST',
                data: {
                    reason: $('#rejectReason').val(),
                },
                success: function (response) {
                    showToast(response.status, response.message);
                    setTimeout(function() {
                        window.location.href = contextPath;
                    }, 5000);
                },
                error: function (error) {

                }
            });
        }
    });
}