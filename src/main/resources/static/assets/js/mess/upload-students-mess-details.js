$(document).ready(function () {
    $('#studentDemandUploadSubmit').off('click').on('click', function (e) {
        e.preventDefault();

        let hadError = false;

        // Validate radio fields
        const radioFields = $("input[name='messAllotmentType']");
        let radioSelected = false;
        radioFields.each(function () {
            if ($(this).is(':checked')) {
                radioSelected = true;
                return false;
            }
        });
        if (radioSelected) {
            radioFields.closest('.checkRadio-bg').removeClass('is-invalid');
        } else {
            radioFields.closest('.checkRadio-bg').addClass('is-invalid');
            showToast('Error', 'Please select a valid option for Mess Allotment Type');
            hadError = true;
        }

        // Validate required fields
        const requiredFields = ["#studentId", "#studentName", "#amount", "#description"];
        requiredFields.forEach(function (selector) {
            const field = $(selector);
            if (field.length === 0) {
                console.error(`Field with selector '${selector}' not found in the DOM.`);
                return;
            }
            if (!field.val().trim()) {
                field.addClass('is-invalid');
                hadError = true;
            } else {
                field.removeClass('is-invalid');
            }
        });

        // Validate file input
        const fileInput = $("#receiptUpload_fileUpload")[0];
        const file = fileInput ? fileInput.files[0] : null;
        const maxFileSize = 5 * 1024 * 1024;
        const allowedFileTypes = ['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/vnd.ms-excel'];

        const fileErrorElement = $('#receiptUpload_fileError');
        fileErrorElement.text('');

        if (!file) {
            fileErrorElement.text('No file selected. Please upload a file.');
            showToast('Error', 'Please upload a file.');
            hadError = true;
        } else if (!allowedFileTypes.includes(file.type)) {
            fileErrorElement.text('Invalid file type. Accepted types are: .xls, .xlsx');
            showToast('Error', 'Invalid file type. Accepted types are: .xls, .xlsx');
            hadError = true;
        } else if (file.size > maxFileSize) {
            fileErrorElement.text('File size exceeds the 5 MB limit. Please upload a smaller file.');
            showToast('Error', 'File size exceeds the 5 MB limit. Please upload a smaller file.');
            hadError = true;
        }

        if (hadError) {
            fileErrorElement.show();
            return false;
        } else {
            fileErrorElement.hide();
        }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('studentId', $('#studentId').val());
        formData.append('studentName', $('#studentName').val());
        formData.append('amount', $('#amount').val());


        $.ajax({
            url: '/mess/upload-students-mess-details',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                showToast('Success', 'File uploaded successfully.');
                response = JSON.parse(response);
                if (response.status === 'success') {
                    window.location.href = '/mess/view-students-mess-details';
                } else {
                    showToast('Error', response.message);
                }
            },
            error: function (xhr, status, error) {
                showToast('Error', 'An error occurred while uploading the file. Please try again.');
            }
        });
    });
});