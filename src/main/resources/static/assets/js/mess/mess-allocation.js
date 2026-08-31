$(document).ready(function () {
    $('#addNewId').click(function () {

        $.ajax({
            url: contextPath + basePath + addPath,
            type: "Get",
            success: function(response) {
                $('#modalDiv').html(response);
                $('#mess-vendor-modal').modal('show');
                $('.selectpicker').selectpicker('refresh');
                $('#mess-vendor-modalSave span').html('Save');
                validateForm();
            },
            error: function(error) {
                showToast('Error', 'Error occurred');
            }
        });
    });
});

function validateForm() {
    $('#mess-vendor-modalSave').on("click", function (e) {
        e.preventDefault();
        let isValidSelect = true; // Initialize as valid
        let isValidInput = true;

        // Array of IDs to validate
        const fieldsToValidate = ['#floorId', '#catererId'];
        const inputsToValidate = ['#rate']
        let status = validateDateChange();

        // Loop through each field and validate select inputs
        fieldsToValidate.forEach(function (fieldId) {
            const field = $(fieldId);
            if (!valRequiredSelect(field[0])) { // Use field[0] to pass the DOM element
                field.parent().find('.dropdown-toggle').addClass(errorClass); // Add error class to selectpicker UI
                isValidSelect = false; // Mark as invalid
            } else {
                field.parent().find('.dropdown-toggle').removeClass(errorClass); // Remove error class
            }
        });

        inputsToValidate.forEach(function (inputFieldId) {
            const inputField = $(inputFieldId);

            if (!valRequiredText(inputField[0])) {
                isValidInput = false;
            }
        });

        // If all validations pass, submit the form
        if (isValidSelect && isValidInput && status) {
            $('#messAllocation').submit(); // Submit the form
        } else {
            $("#mess-vendor-modalSave").attr('disabled', false); // Re-enable the button if validation fails
        }
    });
}

function updateVendorName() {
    var selectedVendor = $('#catererId').find(':selected').text();
    $('#vendorName').value = selectedVendor;
}

function updateFloorName() {
    var selectedFloor = $('#floorId').find(':selected').text();
    $('#floorName').value = selectedFloor;
}

function validateDateChange() {
    let status = true;
    let isDateRangeValid = checkDateRange();
    let toDateRequired = validateDate($('#toDate'));
    let fromDateRequired = validateDate($('#fromDate'));

    if (!isDateRangeValid || !fromDateRequired || !toDateRequired) {
        status = false;
    }

    return status;
}

function validateDate(element) {
    let status = true;
    const date = element.val() ? new Date(element.val()) : null;

    if (!date) {
        element.addClass(errorClass).removeClass(validClass);
        status = false;
    } else {
        element.removeClass(errorClass).addClass(validClass);
    }

    return status;
}

function checkDateRange() {
    const fromDateElement = $('#fromDate');
    const toDateElement = $('#toDate');
    const errorDiv = fromDateElement.siblings('.invalid-feedback');
    const fromDate = fromDateElement.val() ? new Date(fromDateElement.val()) : null;
    const toDate = toDateElement.val() ? new Date(toDateElement.val()) : null;
    let status = true;

    // Handle fromDate and toDate validation
    if (fromDate && toDate) {
        // Check if fromDate > toDate
        if (fromDate >= toDate) {
            errorDiv.html(errorDiv.siblings('.invalid-feedback-date-compare').html()); // Set error message
            fromDateElement.addClass(errorClass).removeClass(validClass); // Apply error class
            toDateElement.addClass(errorClass).removeClass(validClass); // Apply error class to both fields
            $('.invalid-feedback-date-compare').removeClass('d-none'); // Show date comparison error message
            status = false;
        } else {
            fromDateElement.removeClass(errorClass).addClass(validClass); // Remove error class
            toDateElement.removeClass(errorClass).addClass(validClass); // Remove error class
            $('.invalid-feedback-date-compare').addClass('d-none'); // Hide date comparison error message
        }
    }

    return status;
}




