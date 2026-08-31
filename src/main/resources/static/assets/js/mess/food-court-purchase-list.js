$(document).ready(function () {
    $('#getdata').on('click', function (e) {
        e.preventDefault();
        var form = $('#form-search-food-court-purchase-list');
        var url = form.attr('action');
        var data = form.serialize();

        const requiredFIelds = $("#form-search-food-court-purchase-list input[required]");
        const isValid = valRequiredSelect('#messPeriod');
        const isDateValid = validateDateRange('#purchasedFromDate, #purchasedToDate');

        if (isValid && isDateValid) {
            showToast('Error', 'Please fill all required fields correctly.');
            return;
        }
    });
});
function validateForm() {
    const requiredFields = $('input[required], select[required], textarea[required]');

    // First validate required fields
    if(!valRequiredMultiTextRadio(requiredFields)) {
        return false;
    }

    // Then validate date range only if both dates exist
    const fromDate = $('#purchasedFromDate').val();
    const toDate = $('#purchasedToDate').val();

    if (fromDate && toDate) {
        validateDateRange();
    } else {
        // If either date is empty, consider valid and clear any previous errors
        clearDateRangeErrors();
        isDateRangeValid = true;
    }

    // If all validations pass, submit the form
    if(isDateRangeValid) {
        $('#foodCourtPurchaseForm').submit();
    }
    return false; // Prevent default form submission
}

function validateDateRange() {
    const fromDate = $('#purchasedFromDate').val();
    const toDate = $('#purchasedToDate').val();

    // If either date is null, don't validate
    if (!fromDate || !toDate) {
        clearDateRangeErrors();
        isDateRangeValid = true;
        return;
    }

    const fromDateObj = new Date(fromDate);
    const toDateObj = new Date(toDate);
    const errorElement = $('#toDateError');

    if (fromDateObj > toDateObj) {
        isDateRangeValid = false;
        errorElement.show();
        $('#purchasedToDate').addClass('is-invalid');
    } else {
        isDateRangeValid = true;
        clearDateRangeErrors();
    }
}

function clearDateRangeErrors() {
    $('#toDateError').hide();
    $('#purchasedToDate').removeClass('is-invalid');
}
 function downloadRequestReport() {
        // Get form data
        const formData = new FormData(document.getElementById('foodCourtPurchaseForm'));

        // Convert FormData to JSON
        const jsonData = {};
        formData.forEach((value, key) => {
            jsonData[key] = value;
        });

        // Send POST request using fetch
        fetch(contextPath+ excelDownloadUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(jsonData),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
             // Extract the filename from the Content-Disposition header
            const contentDisposition = response.headers.get('Content-Disposition');
            let filename; // Default filename in case the header is not present

            if (contentDisposition && contentDisposition.includes('filename=')) {
                filename = contentDisposition
                    .split('filename=')[1]
                    .split(';')[0]
                    .replace(/['"]/g, '');
            }

            return response.blob().then(blob => ({ blob, filename }));
        })
        .then(({ blob, filename }) => {
            // Create a link element to trigger the download
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Error downloading Excel file:', error);
            showToast('Error', error);

        });
    }


    function createUrlWithParams(page, size, search) {
        let currentUrl = contextPath + $('#currentUrl').val();

        let additionalParamsString = $('.additional-param');

        let queryString = '';
        additionalParamsString.each(function() {
            let paramName = $(this).attr('name');
            let paramValue = $(this).val();

            if (queryString.length > 0) {
                queryString += '&';
            }
            queryString += `${paramName}=${encodeURIComponent(paramValue)}`;
        });

        // Append search param if it's not null or empty
        if (search && search.trim() !== '') {
            if (queryString.length > 0) {
                queryString += '&';
            }
            queryString += `search=${encodeURIComponent(search.trim())}`;
        }

        let fullUrl = `${currentUrl}?page=${page}&size=${size}`;
        if (queryString.length > 0) {
            fullUrl += `&${queryString}`;
        }

        // Update the form's action attribute with the full URL
        let form = $('#foodCourtPurchaseForm');
        form.attr('action', fullUrl);

        form.submit();
    }