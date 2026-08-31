$(document).ready(function() {
    $("#addNewId i").attr("class", "fa-solid fa-file-excel me-1");
	$('#addNewId').removeClass('btn-success').addClass('btn-pink');
	$("#addNewId span").text("Download Issued Guest Coupons");

    $('#addNewId').on('click', function() {
       downloadExcel();
    });

    $('#returnButton').on('click', function() {
        var checkedRowsIds = getCheckedRowIds();

        if (checkedRowsIds.length === 0) {
            showToast('Error', 'Please select the record(s) to return');
        } else {
            $('#warningModal').modal('show');
        }
    });

    $('#issuedForm').on('submit', function(event) {
        updateMessName();
    });

    $('#warningModalPositive').on('click', function(event) {
        $('#warningModal').modal('hide');
        updateStatus();
    });
});

function getIssuedCoupons() {
    $('input[name="additionalParam.name"]').val($('#name').val());
    $('input[name="additionalParam.requestId"]').val($('#requestID').val());
    $('input[name="additionalParam.diningFrom"]').val($('#diningFrom').val());
    $('input[name="additionalParam.diningTo"]').val($('#diningTo').val());
    $('input[name="additionalParam.submittedFrom"]').val($('#submittedFrom').val());
    $('input[name="additionalParam.submittedTo"]').val($('#submittedTo').val());
    $('input[name="additionalParam.messId"]').val($('#messId').val());
    $('input[name="additionalParam.usedStatus"]').val($('#status').val());

    // Call url with params with the current page and size
    let page = $('#pageVal').val();
    let size = $('#sizeVal').val();
    let search = $('#search').val();
    createUrlWithParams(page, size, search);
}

function downloadExcel() {
    var coupon = {
        name: $('#name').val(),
        requestId: $('#requestID').val(),
        usedStatus: $('#status').val(),
        diningFrom: $('#diningFrom').val(),
        diningTo: $('#diningTo').val(),
        submittedFrom: $('#submittedFrom').val(),
        submittedTo: $('#submittedTo').val(),
        messId: $('#messId').val()
    }
    var queryString = $.param(coupon);

    window.location.href = contextPath + baseURL + downloadPath + "?" + queryString;
}

function updateMessName() {
    var selectedMessName = $('#messId').find(':selected').text();
    $('#messName').value = selectedMessName;
}

function updateStatus() {
    var checkedRowsIds = getCheckedRowIds();

    $.ajax({
        url: contextPath + baseURL,
        type: 'PATCH',
        data: JSON.stringify(checkedRowsIds),
        contentType: 'application/json',
        success: function(response) {
            showToast(response, "Returned Successfully");
            $('#issuedForm').submit();
        },
        error: function(xhr, status, error) {
            showToast('Error', 'Error occurred');
        }
    });
}

function getCheckedRowIds() {
    var checkedRowsIds = [];

    $('.form-check-input:checked').each(function() {
        checkedRowsIds.push($(this).attr('id').split('_')[1]);
    });

    return checkedRowsIds;
}
