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
	if (!isDateRangeValid('#eventFromDate', '#eventToDate')) {
       return;
   }
    if(valRequiredMultiTextRadio(requiredFields)){
        $('#facultyAccommodationRequestForm').submit();
    }
}

 function downloadRequestReport() {
		if (!isDateRangeValid('#eventFromDate', '#eventToDate')) {
	       return;
	   }
        // Get form data
        const formData = new FormData(document.getElementById('facultyAccommodationRequestForm'));

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
	
	function isDateRangeValid(fromSelector, toSelector) {
	    const fromDate = $(fromSelector).val();
	    const toDate = $(toSelector).val();
		const isInvalidClass = 'is-invalid';
	    if (fromDate && toDate && toDate < fromDate) {
	        $('#toDateError').show();
	        $('#eventToDate').addClass(isInvalidClass);
	        return false;
	    } else {
	        $('#toDateError').hide();
	        $('#eventToDate').removeClass(isInvalidClass);
	        return true;
	    }
	}
	