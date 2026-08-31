let initialExpandedLedgerKey = null;
$(document).ready(function() {
    const expanded = $('.accordion-collapse.show');
    if (expanded.length > 0) {
        const headerBtn = expanded.closest('.accordion-item').find('.accordion-button');
        initialExpandedLedgerKey = headerBtn.data('ledger-key');
    }
    $('#ledgerAccordion').on('show.bs.collapse', function (e) {
        const expandedItem = $(e.target).closest('.accordion-item');
        const headerBtn = expandedItem.find('.accordion-button');
        initialExpandedLedgerKey = headerBtn.data('ledger-key');
        console.log("Updated ledgerKey: ", initialExpandedLedgerKey); // for debugging
    });

    $('input[name=view]').on('change', function () {
        const selected = $(this).val();
        if (selected === 'expandView') {
            $('#collapseAccordion').addClass(displayNone);
            $('#expandTable').removeClass(displayNone);
        } else {
            $('#expandTable').addClass(displayNone);
            $('#collapseAccordion').removeClass(displayNone);
        }
    });
});

function downloadExcel(){
    const viewVal = $('input[name=view]:checked').val();
    const year = initialExpandedLedgerKey;
    const studentId = $('#studentId').val();
    const fromDate = $('#fromDateId').val();
    const toDate = $('#toDateId').val();
    const messOrCard = $('input[name=selectedCriteria]:checked').val();
    const allVal = `${viewVal},${year},${studentId},${fromDate},${toDate},${messOrCard}`;
    window.location.href = `${contextPath}${messLedgerBaseURL}${downloadURL}?allVal=${allVal}`;
}

function validateForm() {
    const requiredFields = $('input[required], select[required], textarea[required]');
    const dateRanges = [
        {fromElement: "fromDateId", toElement: "toDateId"}
    ];

    let isDateRangeValid = true;
    const dateSectionVisible = $('#fromDateId').is(':visible') && $('#toDateId').is(':visible');
    if (dateSectionVisible) {
        isDateRangeValid = validateBetweenDateRanges(dateRanges);
    }
    if(valRequiredMultiTextRadio(requiredFields) && isDateRangeValid){
        $('#ledger-form').submit();
    }
}
function proceedTransaction(event) {
	var studentID = event.getAttribute('data-bs-student-id');
	var amount = event.getAttribute('data-bs-amount');
	var transactionType = event.getAttribute('data-bs-type');
	var eventId = event.getAttribute('data-bs-event-id');
	// Set different body text based on transaction type
    var bodyText;
    switch(transactionType) {
        case 'penaltyClaim':
            bodyText = claimText;
            break;
        case 'donateClaim':
            bodyText = donateText;
            break;
        case 'hostelDepositRefund':
            bodyText = refundText;
            break;
        case 'cardAmountTransfer':
            bodyText = transferText;
            break;
        case 'purchaseAmountDeduct':
            bodyText = purchaseText;
            break;
        default:
            bodyText = transferText;
    }

    // Update modal body text before showing
    $('#transactionModalText').text(bodyText);
    $('#transactionConfirmationModal').modal('show');
    $('#transactionConfirmationModalPositive').off("click").on("click", function (e) {
            showLoader();
            $('#transactionConfirmationModal').modal('hide');
            fetch(contextPath + baseURL + transactionUrl + "/" + studentID + "?amount=" + amount+ "&transactionType="+transactionType+ "&eventId="+eventId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(response => {
                hideLoader();
				console.log(response);
				console.log(response.status);
                if(response !=null){
			        if(response.status=='Success'){
                        showToast('Success', 'Transaction successful')
                        setTimeout(function() {
                            validateForm();
                        }, 3000);
                    } else {
                        showToast('Error', response.message)
                    }
                }
                else{
                    showToast('Error', "Failed in Transaction request.");
                }
            });
    })
}

function openSettlementModal(event) {

	var studentID = event.getAttribute('data-bs-student-id');
	var amount = event.getAttribute('data-bs-amount');
    const url = contextPath + baseURL + settlementUrl + "/" + studentID + "?amount=" + amount ;
    showLoader();
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#settlementModal'));
            hideLoader();
            modal.show();

            // Handle Save button click
            $('#settlementModalSave').on('click', function (e) {
                e.preventDefault();
                const requiredFields = $('input[required], select[required], textarea[required]');
                const isValid = valRequiredMultiTextRadio(requiredFields);
                if (isValid) {
                    $("#settlementModalSave").attr('disabled', false);
                    //$('#settlementForm').submit();
                    modal.hide();
                    submitForm();
                } else {
                    showToast('Error', 'Please enter required fields');
                }
            });
        })
        .catch(error => {
            showToast('Error', messages.errorLoadingModal);
        });
}
  function submitForm() {
    showLoader();
    const form = $('#settlementForm')[0]; // Get the raw DOM element
      const formData = new FormData(form);
    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
    .then(data => {
        console.log(data);
        hideLoader();
        if(data.status=='Success'){
            showToast('Success', data.message);
            setTimeout(function() {
                validateForm();
            }, 3000);
        } else {
            showToast('Error', data.error)
        }
    })
    .catch(error => {
        hideLoader();
        showToast('Error', savedError);
    });
  }

function validateField($field) {
    const value = $field.val();
    const isValid = !!value.trim();
    const errorDiv = $field.siblings('.invalid-feedback');

    if (isValid) {
        $field.removeClass('is-invalid');
    } else {
        $field.addClass('is-invalid');
    }

    return isValid;
}
function showLoader() {
    $('#loader').removeClass('d-none');
    $('html, body').addClass('stop-scrolling');
}

function hideLoader() {
    $('#loader').addClass('d-none');
    $('html, body').removeClass('stop-scrolling');
}

function deleteLedgerEntry(element) {
    const voucherNo = element !== null ? element.getAttribute("data-voucher") : 0;
    const studentId = element !== null ? element.getAttribute("data-studentId") : 0;
    const slNo = element !== null ? element.getAttribute("data-slNo") : 0;

    $('#deleteModal').modal('show');

    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');

        fetch(contextPath + messLedgerBaseURL + deleteURL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                voucherNo: voucherNo,
                studentId: studentId,
                slNo: slNo
            })
        })
        .then(response => response.json())
        .then(response => {
            if (response.status === 'Success') {
                deleteTableRow('deleteLedgerEntry_' + voucherNo);
                showToast('Success', 'Ledger Entry deleted successfully');
            } else {
                showToast('Failure', response.message);
            }
        });

        $('#deleteModalPositive').off('click');
    });
}


function undoSettlement(element) {
    const studentId = element !== null ? element.getAttribute("data-bs-student-id") : null;

    $('#undoSettlementModal').modal('show');

    $('#undoSettlementModalPositive').click(function () {
        $('#undoSettlementModal').modal('hide');

        fetch(contextPath + baseURL + undoSettlementUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                studentId: studentId
            })
        })
        .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
        .then(data => {
            console.log(data);
            hideLoader();
            if(data.status=='Success'){
                showToast('Success', data.message);
                setTimeout(function() {
                    validateForm();
                }, 3000);
            } else {
                showToast('Error', data.error)
            }
        })
        .catch(error => {
            hideLoader();
            showToast('Error', savedError);
        });

        $('#undoSettlementModalPositive').off('click');
    });
}