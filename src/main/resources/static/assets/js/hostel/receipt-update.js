$(document).ready(function () {
    $("#submitUpdateReceiptDetails").on('click', function (e) {
        e.preventDefault();
        $(this).attr('disabled', true);
        $(".is-invalid").removeClass('is-invalid')
        const requiredFields = $('input[required]:visible');
        let result = validateRadioAndText(requiredFields);

        let isValid = true;
        let isChecked = false;
        $("input[name$='.isCredited']").each(function () {
            const creditedChecked = $(this).is(':checked');
            const creditedDateInput = $(this).closest('tr').find("input[name$='.creditedDate']");
            if (creditedChecked) {
                if (creditedDateInput.val() === '') {
                    creditedDateInput.addClass('is-invalid');
                    isValid = false;
                }
                isChecked = true;
            }
        });

        if(result && isValid && isChecked) {
            $('#receiptUpdateForm').submit();
        }
        else if(isValid && !isChecked) {
            showToast("Error", "Please select the credited checkbox.");
            $(this).attr('disabled', false);
        }
        else{
            $(this).attr('disabled', false);
        }
    });

    $("input[name$='.isCredited']").on('change', function () {
        $(".is-invalid").removeClass('is-invalid')
        const creditedChecked = $(this).is(':checked');
        const creditedDateInput = $(this).closest('tr').find("input[name$='.creditedDate']");
        if (creditedChecked) {
            creditedDateInput.prop('required', true);
        } else {
            creditedDateInput.prop('required', false);
        }
    });

    $("#filterData").on('click', function (e) {
        const requiredFields = $('select[required]:visible');
        let result = validateRadioAndText(requiredFields);
    });
});

function deleteReceipt(element){
    const voucherNo = element !== null ? element.getAttribute("data-voucher") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');
        fetch(contextPath + baseURL + deleteURL + "/" + voucherNo
            , {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            if (response.status === 'Success') {
                deleteTableRow('deleteReceipt_' + voucherNo);
                showToast('Success', 'Receipt Entry deleted successfully');
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    })
}