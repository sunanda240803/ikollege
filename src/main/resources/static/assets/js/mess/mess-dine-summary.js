function validateForm() {
    const requiredFields = $('input[required], select[required], textarea[required]');
    if(valRequiredMultiTextRadio(requiredFields)){
        $('#messDineSummaryForm').submit();
    }
}

 function downloadRequestReport() {
        // Get form data
        const formData = new FormData(document.getElementById('messDineSummaryForm'));

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