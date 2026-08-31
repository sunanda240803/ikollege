// Handle form submission
$(document).ready(function() {
    $('#updateId').on('click', function(e) {
		e.preventDefault();
		submitCheckerApproval();
	});
});
function submitCheckerApproval() {
    var form = document.getElementById('voucherForm');
    // Get all selected checkboxes
    const selectedVouchers = Array.from(document.querySelectorAll('.voucher-checkbox:checked'))
        .map(checkbox => checkbox.value);

    // Create a hidden input to send the data
    const hiddenInput = document.createElement('input');
    hiddenInput.type = 'hidden';
    hiddenInput.name = 'voucherNumbers';
    hiddenInput.value = JSON.stringify(selectedVouchers);

    // Add to form and submit
    form.appendChild(hiddenInput);
    form.submit();
}

function deleteCheckerApproval(element) {
    const voucherNo = element !== null ? element.getAttribute("data-voucher-no") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').off('click').click(function() {
        $('#deleteModal').modal('hide');
        $.ajax({
            url: contextPath + checkerApprovalURL + "/" + voucherNo,
            type: "DELETE",
            success: function(response) {
                if (response.status === 'Success' || response.status === 'success') {
                    // $('#'+'deleteReq_' + voucherNo).parents('tr').remove();
                    showToast(response.status, response.message);
                    setTimeout(function() {
                        location.reload();
                    }, 2000);
                }

            },
            error: function(error) {
                showToast('Error', 'Error occurred');
            }
        });
        $('#deleteModalPositive').off('click');
    })
}

function toggleDetails(button) {
    const voucherNo = button.getAttribute('data-voucher-no');
     const totalRows = button.getAttribute('data-total-rows');
    const rows = document.querySelectorAll(`[id^="debit_${voucherNo}_"], [id^="credit_${voucherNo}_"]`);

    // Toggle button state
    button.classList.toggle('expanded');
    const isExpanded = button.classList.contains('expanded');

    // Update tooltip
    const tooltip = bootstrap.Tooltip.getInstance(button);
    if (tooltip) {
        tooltip.setContent({'.tooltip-inner': isExpanded ? 'Hide Entries' : 'Show All Entries'});
    }

    document.getElementById(`slno_${voucherNo}`).rowSpan = isExpanded ? totalRows : 2;
    document.getElementById(`voucherno_${voucherNo}`).rowSpan = isExpanded ? totalRows : 2;
    document.getElementById(`action_${voucherNo}`).rowSpan = isExpanded ? totalRows : 2;


    // Toggle all related rows
    rows.forEach(row => {
        row.classList.toggle('show');
    });

    // Update icon
    const icon = button.querySelector('i');
    if (isExpanded) {
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    } else {
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
}

function highlightRow() {
    const storedPosition = sessionStorage.getItem('highlightedRowPosition');
    if (storedPosition !== null) {
        // Get all rows in the table body (assuming same table structure)
        const tableRows = Array.from(document.querySelectorAll('table tbody tr'));
        // Highlight the row at the stored position if it exists
        if (tableRows[storedPosition]) {
            tableRows[storedPosition].classList.add('highlight-row');
        }
        // Clean up storage
        sessionStorage.removeItem('highlightedRowPosition');
    }
}

function toggleSelectAll(selectAllCheckbox) {
    const checkboxes = document.querySelectorAll('.voucher-checkbox');
    checkboxes.forEach(cb => {
        cb.checked = selectAllCheckbox.checked;
    });
}
