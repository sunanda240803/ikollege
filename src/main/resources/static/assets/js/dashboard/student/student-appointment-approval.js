$(document).ready(function () {
    const approveRejectId = document.getElementById('approveReject');
    approveRejectRequest(approveRejectId.value, approveRejectId);
});

function approveRejectRequest(element, status){
    const infoModalPositive = $("#infoModalPositive");
    const deleteModalPositive = $("#deleteModalPositive");
    const approveStatus = $("#approveStatus");
    const modalheader = $('modalheader');
    if (approveStatus.val() === 'Approved' || approveStatus.val() === 'Rejected'){
        $('#successModal').modal('show');
        modalheader.text(`This request already ${approveStatus.val().toLowerCase()}. or the request is invalid`);
        $('#successModalPositive').on('click', function () {
            window.location.href = contextPath;
        });
        $('#deleteModalText').html(``);
        $('modalbody').addClass(displayNone);
        $('#successModalCancel').addClass(displayNone);
    } else {
        if (element === "r") {
            $('#deleteModal').modal('show');
            $('#deleteModalCancel').on('click', function () {
                window.location.href = contextPath;
            });
            modalheader.text(`Are you sure you want to Reject this appointment request ?`);
            $('#deleteBodyText').html(`
            <div class="mb-3 themeForm">
                <textarea type="text" class="form-control" id="rejectReason" placeholder="Enter Reject Reason" required></textarea>
                <div class="invalid-feedback">Rejection Reason Required</div>
            </div>
        `);
            const rejectReason = $('#rejectReason');
            deleteModalPositive.prop("disabled", true);

            if (rejectReason.val().trim() === "") {
                rejectReason.addClass(errorClass).removeClass(validClass);
            } else {
                rejectReason.addClass(validClass).removeClass(errorClass);
            }
            rejectReason.on("input", function () {
                if (rejectReason.val().trim() === "") {
                    deleteModalPositive.prop("disabled", true);
                } else {
                    deleteModalPositive.prop("disabled", false);
                }
            });
            deleteModalPositive.off("click").on("click", function (e) {
                $('#infoModal').modal('hide');
                approvalStatusRequest(status, element);
            });

        } else {
            $('#infoModal').modal('show');
            $('#infoModalCancel').on('click', function () {
                window.location.href = contextPath;
            });
            modalheader.text(`Are you sure you want to Approve this appointment request ?`);
            $('#deleteModalText').html(``);
            $('#infoModalPositive').html(`
            <i class="fa fa-check-circle"></i> Confirm
            `);
            infoModalPositive.off("click").on("click", function (e) {
                $('#infoModal').modal('hide');
                approvalStatusRequest(status, element);
            });
        }
    }
}