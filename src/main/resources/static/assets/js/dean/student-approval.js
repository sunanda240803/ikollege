$(document).ready(function() {
    const cols = 'col-lg-12 col-lg-10 col-lg-6 col-lg-4';
    $("#updateId i").attr("class", "fa-solid fa-file-excel me-1");
    $('#updateId').removeClass('btn-success').addClass('btn-pink');

    $("#downloadButtonId").click(function () {
        downloadExcel();
    });

    $("#toggleFilter").click(function() {
        const isExpanded = $(this).attr("aria-expanded") === "true";
        const toggleFilterDiv = $(".toggleFilterSection");
        const keywordSearchDiv = $(".keywordSearch");
        const toggleApproveSection = $(".toggleApproveSection");
        const squareCheck = $(".square-checkbox:checked");
        const isKeywordVisible = !keywordSearchDiv.hasClass(displayNone);

        if (isExpanded) {
            $("#toggleFilterText").text("Hide Advanced Filter");
            $('#showIcon').removeClass('fa-eye').addClass('fa-eye-slash');
            keywordSearchDiv.addClass(displayNone);
            toggleFilterDiv.removeClass("col-lg-6").addClass("col-lg-12");
        } else {
            $("#toggleFilterText").text("Show Advanced Filter");
            $('#showIcon').removeClass('fa-eye-slash').addClass('fa-eye');
            keywordSearchDiv.removeClass(displayNone);
            toggleFilterDiv.removeClass("col-lg-12").addClass("col-lg-6");
        }

        if (!isKeywordVisible && squareCheck.length > 0) {
            toggleApproveSection.removeClass(cols).addClass("col-lg-4");
            toggleFilterDiv.removeClass(cols).addClass("col-lg-2");
        } else if (squareCheck.length > 0 && isExpanded) {
            toggleApproveSection.removeClass(cols).addClass("col-lg-10");
            toggleFilterDiv.removeClass(cols).addClass("col-lg-2");
        }
    });

    function updateLayout() {
        const toggleApproveSection = $(".toggleApproveSection");
        const toggleFilterDiv = $(".toggleFilterSection");
        const keywordSearchDiv = $(".keywordSearch");
        const isKeywordVisible = !keywordSearchDiv.hasClass(displayNone);
        const hasChecked = $(".square-checkbox:checked").length > 0;

        if (hasChecked) {
            toggleApproveSection.removeClass(displayNone);
            if (!isKeywordVisible) {
                toggleApproveSection.removeClass(cols).addClass("col-lg-10");
                toggleFilterDiv.removeClass(cols).addClass("col-lg-2");
            } else {
                keywordSearchDiv.removeClass(cols).addClass("col-lg-6");
                toggleApproveSection.removeClass(cols).addClass("col-lg-4");
                toggleFilterDiv.removeClass(cols).addClass("col-lg-2");
            }
        } else {
            toggleApproveSection.addClass(displayNone);
            if (!isKeywordVisible) {
                toggleFilterDiv.removeClass("col-lg-4").addClass("col-lg-12");
            } else {
                toggleFilterDiv.removeClass("col-lg-4").addClass("col-lg-6");
                keywordSearchDiv.removeClass("col-lg-4").addClass("col-lg-6");
            }
        }
    }

    $(".square-checkbox").on("change", function () {
        updateLayout();
    });

    $("#clearButton").click(function() {
        $(".square-checkbox").prop("checked", false);
        updateLayout();
    });

    $('.deleteButton').click(function () {
        deleteMessRebateRequest();
    });

    $('.resendButton').click(function () {
        resendMessRebateRequest();
    });

    $('.viewButton').click(function () {
        messRebateRequestViewModel();
    });
});

function downloadExcel(){
    window.location.href = contextPath + "/studentWithRemarks/downloadStudentRemarkTemplate";
}

function deleteMessRebateRequest() {
    $('#deleteModal').modal('show');
}

function resendMessRebateRequest() {
    $('#resendEmailModal').modal('show');
}

function messRebateRequestViewModel() {
    window.location.href = contextPath + '/mess/student-approve-view.html';
}