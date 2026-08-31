const validateRowsInputs = () => {
    const requiredFields = $('input[required]:visible,select[required]:visible,textarea[required]:visible');
    return validateRadioAndText(requiredFields);
}

const addRowToJournalTable = async () => {
    if (!validateRowsInputs()) return;

    const $table = $('#studentLoginTable tbody');
    const existingIndices = new Set();

    // Collect all existing indices from input/select name attributes
    $table.find('tr').each(function () {
        $(this).find('input[name], select[name]').each(function () {
            const name = $(this).attr('name');
            const match = name.match(/\[(\d+)\]/);
            if (match) {
                existingIndices.add(parseInt(match[1], 10));
            }
        });
    });

    // Find the first missing index (i.e., gap)
    let rowIndex = 0;
    while (existingIndices.has(rowIndex)) {
        rowIndex++;
    }

    const $tr = $table.find('tr:first').clone();
    // $tr.find('input').val('');
    $tr.find('select').val(function () {
        return $(this).find('option:first').val();
    });
    $tr.find('input, select').removeClass('is-invalid is-valid');

    // Rename input/select fields with updated index
    $tr.find('input, select').each(function () {
        const $el = $(this);
        const oldName = $el.attr('name');
        if (oldName) {
            const newName = oldName.replace(/\[\d+\]/, `[${rowIndex}]`);
            $el.attr('name', newName);
        }
    });

    $tr.find('select[id^="accHead_"]').each(function () {
        $(this).attr('id', `accHead_${rowIndex}`);
        $(this).attr('oninput', `showHideStudentInput(${rowIndex},this)`);
    });

    $tr.find('input[id^="studentId_"]').each(function () {
        $(this).attr('id', `studentId_${rowIndex}`);
    });

    $tr.find('div[id^="studentIdDiv_"]').each(function () {
        $(this).attr('id', `studentIdDiv_${rowIndex}`);
    });

    $tr.find('div[id^="subAccDiv_"]').each(function () {
        $(this).attr('id', `subAccDiv_${rowIndex}`);
    });

    $tr.find('select[id^="subAccHead_"]').each(function () {
        $(this).attr('id', `subAccHead_${rowIndex}`);
    });

    $table.append($tr);
};


function removeCurrentRow(e) {
    var row = $(e).closest('tr');

    if (!confirm('Are you sure you want to delete this row?')) {
        return;
    }

    if ($('#studentLoginTable tbody tr').length > 1) {
        row.remove();
        calculateTotal();
    } else {
        row.find('input').val('');
        row.find('select').val(function () {
            return $(this).find('option:first').val();
        });
        row.find('input, select').removeClass('is-invalid is-valid');
    }
}

$(document).on('input change', 'input[name^="transferAmtList1"][name$=".amount"], select[name^="transferAmtList1"][name$=".debitOrCredit"]', function () {
    calculateTotal();
});


function calculateTotal() {
    var totalCredit = 0;
    var totalDebit = 0;

    $('#studentLoginTable tbody tr').each(function () {
        var amountInput = $(this).find('input[name$=".amount"]');
        var amount = parseFloat(amountInput.val());
        var drCr = $(this).find('select[name$=".debitOrCredit"]').val();

        if (isNaN(amount) || amountInput.val().trim() === "") {
            amount = 0;
            amountInput.val("0.00");
        }

        if (drCr === 'c' || drCr === 'C') {
            totalCredit += amount;
        } else if (drCr === 'd' || drCr === 'D') {
            totalDebit += amount;
        }
    });

    $('#totalCredit').val(totalCredit.toFixed(2));
    $('#totalCredit').siblings('.totalCredit').text(totalCredit.toFixed(2));

    $('#totalDebit').val(totalDebit.toFixed(2));
    $('#totalDebit').siblings('.totalDebit').text(totalDebit.toFixed(2));
}

$(document).on('click', '.addNewStyle', function () {
    calculateTotal();
});

$(document).on('change', 'select[name="debit_credit"]', function () {
    $(this).closest('td').next().find('input[name="amount"]').focus();
});

function validateJournalVoucherForm() {
    let isValid = validateRowsInputs();
    const debit = parseFloat($('#totalDebit').val());
    const credit = parseFloat($('#totalCredit').val());

    if (isValid) {
        let checkDebitOrCredit1 = checkDebitOrCredit();
        if (checkDebitOrCredit1) {
            if (debit !== credit) {
                showToast('error', 'Amount is not tallied');
                return false;
            } else {
                return true;
            }
        } else {
            showToast('error', 'Please add both Debit & Credit entry!');
            return false;
        }
    } else {
        return false;
    }
}

function checkDebitOrCredit() {
    let hasD = false;
    let hasC = false;

    $('#studentLoginTable tbody select[name$=".debitOrCredit"]').each(function () {
        const val = $(this).val();
        if (val === 'd') hasD = true;
        if (val === 'c') hasC = true;

        // Early exit if both found
        if (hasD && hasC) {
            return false;
        }
    });

    // If only one type is selected, return false
    return hasD && hasC;
}

function showHideStudentInput(index, element) {
    let v = element.value;
    if (v === 'STUD') {
        $('#studentId_' + index).addClass('required-input');
        $('#studentId_' + index).attr('required', true)

        $('#subAccHead_' + index).removeClass('required-input');
        $('#subAccHead_' + index).removeAttr('required');

        $('#studentIdDiv_' + index).removeClass(dNone);
        $('#subAccDiv_' + index).addClass(dNone);
    } else {
        $('#subAccHead_' + index).addClass('required-input');
        $('#subAccHead_' + index).attr('required', true);

        $('#studentId_' + index).removeClass('required-input');
        $('#studentId_' + index).removeAttr('required')

        $('#studentIdDiv_' + index).addClass(dNone);
        $('#subAccDiv_' + index).removeClass(dNone);
    }
}

function cancelJournalVoucher(voucherNo) {
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');
        fetch(contextPath + baseURL + cancelURL + "/" + voucherNo
            , {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            showToast(response.status, response.message);
            setTimeout(function() {
                window.location.href = contextPath + baseURL;
            }, 5000);
        });
        $('#deleteModalPositive').off('click');
    })
}