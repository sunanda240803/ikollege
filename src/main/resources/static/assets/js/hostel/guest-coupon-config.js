$(document).ready(function () {
    // Trigger Add/Edit Modal
    $('#addNewId').click(function () {
        openGuestCouponModal();
    });
});

// Open Modal for Add/Edit
function openGuestCouponModal(id) {
    const url = id ? `${contextPath}/guestCouponConfig/edit/${id}` : `${contextPath}/guestCouponConfig/add/0`;

    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#guestCouponConfigModal'));
            modal.show();

            // Handle Save button click
            $('#guestCouponConfigModalSave').on('click', function (e) {
                e.preventDefault();
                validateAndSubmitGuestCouponForm();
            });
        })
        .catch(error => {
            showToast('Error', messages.errorLoadingModal);
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

// Validate and submit form
function validateAndSubmitGuestCouponForm() {
    let isFormValid = true;

    $('.required-input').each(function () {
        if (!validateField($(this))) isFormValid = false;
    });

    // Validate Effective From and Valid To dates
    const effectiveFrom = $('#effectiveFrom').val();
    const validTo = $('#validTo').val();
    const today = new Date().setHours(0, 0, 0, 0);

    if (effectiveFrom) {
        const fromDate = new Date(effectiveFrom).setHours(0, 0, 0, 0);
        if (fromDate < today) {
            $('#effectiveFrom').addClass('is-invalid');
            $('#effectiveFrom').siblings('.invalid-feedback').text(messages.fromDateError).show();
            isFormValid = false;
            return;
        } else {
            $('#effectiveFrom').removeClass('is-invalid');
            $('#effectiveFrom').siblings('.invalid-feedback').hide();
        }
    }

    if (effectiveFrom && validTo) {
        const fromDate = new Date(effectiveFrom);
        const toDate = new Date(validTo);
        if (fromDate > toDate) {
            $('#effectiveFrom').addClass('is-invalid');
            $('#effectiveFrom').siblings('.invalid-feedback').text(messages.toDateError).show();
            isFormValid = false;
            return;
        } else {
            $('#effectiveFrom').removeClass('is-invalid');
            $('#effectiveFrom').siblings('.invalid-feedback').hide();
        }
    }

    const amountFields = [
       { id: '#breakfastAmount', name: 'Breakfast Amount' },
       { id: '#lunchAmount', name: 'Lunch Amount' },
       { id: '#dinnerAmount', name: 'Dinner Amount' },
       { id: '#snacksAmount', name: 'Snacks Amount' },
    ];

    amountFields.forEach(field => {
        const rValue = $(field.id).val();
        const isValid = !!rValue.trim();
        if(isValid) {
            const value = parseFloat(rValue);
            if (isNaN(value) || value <= 0) {
                $(field.id).addClass('is-invalid');
                $(field.id).siblings('.invalid-feedback').text(`${field.name} ${messages.amountError}`).show();
                isFormValid = false;
                return;
            } else {
                $(field.id).removeClass('is-invalid');
                $(field.id).siblings('.invalid-feedback').hide();
            }
        }

    });

    if (isFormValid) {
        $("#guestCouponConfigModalSave").attr('disabled', false);
        $('#guestCouponForm').submit();
    } else {
        showToast('Error', messages.invalidForm);
    }
}

// Delete Guest Coupon
function deleteGuestCoupon(id) {
    $('#deleteModal').modal('show');

    $('#deleteModalPositive').off('click').click(function () {
        $('#deleteModal').modal('hide');

        $.ajax({
            url: contextPath + baseURL + "/" + id,
            type: "DELETE",
            success: function (response) {
                showToast(response.status, messages.deleteSuccessMessage);
                setTimeout(function() {
                    location.reload();
                }, 1500);
            },
            error: function () {
                showToast('Error', messages.errorMessage);
            }
        });
        $('#deleteModalPositive').off('click');
    });
}

