$(document).ready(function () {

    $('#addNewId').on('click', function () {
        window.location.href = contextPath+baseURL+'/0';
    })

    $(document).on('click', '#saveId', function (e) {
        e.preventDefault();

        if (!validateRowsInputs()) {
            showToast('Error', 'Please fill all mandatory fields correctly.');
            return;
        }

        const formData = $('#journalVoucherListTable').serialize();

        $.ajax({
            type: 'POST',
            url: '/hostel/journal-voucher',
            data: formData,
            success: function (response) {
                showToast('Success', 'Journal voucher added successfully.');
            },
            error: function (xhr, status, error) {
                showToast('Error', 'An error occurred while adding the journal voucher.');
            }
        });
        return false;
    });

    const validateRowsInputs = () => {
        let isValid = true;

        const voucherDate = $('#voucher_date');
        if (!voucherDate.val()) {
            voucherDate.addClass('is-invalid');
            voucherDate.next('.invalid-feedback').text('Please select voucher date.');
            return;
        }

        $('.is-invalid').removeClass('is-invalid');

        $('#journalVoucherListTable tbody tr').each(function (index) {
            let rowIsValid = true;

            // // Validate Ledger Account
            // const ledgerAccount = $(this).find('.ledger-account');
            // if (!ledgerAccount.val() || ledgerAccount.val() === '0') {
            //     ledgerAccount.addClass('is-invalid');
            //     ledgerAccount.next('.invalid-feedback').text('Please select a valid ledger account.');
            //     rowIsValid = false;
            // }

            // // Validate Ledger Sub Account
            // const ledgerSubAccount = $(this).find('.ledger-sub-account');
            // if (!ledgerSubAccount.val() || ledgerSubAccount.val() === '0') {
            //     ledgerSubAccount.addClass('is-invalid');
            //     ledgerSubAccount.next('.invalid-feedback').text('Please select a valid ledger sub account.');
            //     rowIsValid = false;
            // }

            // Validate Amount
            const amount = $(this).find('.amount');
            if (!amount.val() || parseFloat(amount.val()) < 0) {
                amount.addClass('is-invalid');
                amount.next('.invalid-feedback').text('Please enter a valid amount.');
                rowIsValid = false;
            }

            if (!rowIsValid) {
                isValid = false;
            }
        });

        return isValid;
    }

    function calculateTotal() {
        var totalCredit = 0;
        var totalDebit = 0;

        $('#journalVoucherListTable tbody tr').each(function (index) {
            var amountInput = $(this).find('input[name="amount"]');
            var amount = parseFloat(amountInput.val());
            var drCr = $(this).find('select[name="debit_credit"]').val();

            if (isNaN(amount) || amountInput.val().trim() === "" || amount <= 0) {
                amount = 0;
                amountInput.val("0.00");
            }

            if (drCr === 'Credit') {
                totalCredit += amount;
            } else if (drCr === 'Debit') {
                totalDebit += amount;
            } else {
                console.warn(`Invalid Dr/Cr value in row ${index + 1}:`, drCr);
            }
        });

        $('#totalCredit').val(totalCredit.toFixed(2));
        $('#totalCredit').siblings('.totalCredit').text(totalCredit.toFixed(2));

        $('#totalDebit').val(totalDebit.toFixed(2));
        $('#totalDebit').siblings('.totalDebit').text(totalDebit.toFixed(2));
    }

    $(document).on('change', 'select[name="debit_credit"]', function () {
        $(this).closest('td').next().find('input[name="amount"]').focus();
    });

    calculateTotal();

    $(document).on('input change', 'input[name="amount"], select[name="debit_credit"]', function () {
        calculateTotal();
    });
});

function getDetailsByVoucherNo(){
    const requiredFields = $('input[required]:visible');
    let result = validateRadioAndText(requiredFields);
    if(result){
        const voucherNo = $('#voucherNo').val();
        const url = contextPath+baseURL+'/'+voucherNo;
        window.location.href = contextPath+baseURL+'/'+voucherNo.trim();
    }
}

