$(document).ready(function () {
    $("#getData").on('click', function () {
        const requiredFields = $('input[required]:visible');
        let result = validateRadioAndText(requiredFields);
        if (!result) {
            showToast('Error', 'Please fill all required fields.', 'error');
            return;
        } else {
            const form = $(this).closest('form')[0];
            const formData = new FormData(form);
            $.ajax({
                url: '/hostel/student-balance-ledger',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    if (response.status === 'success') {
                        $('#balanceLedger').html(response.data);
                    } else {
                        showToast('Error', response.message, 'error');
                    }
                },
                error: function (xhr, status, error) {
                    showToast('Error', 'An error occurred while processing your request.', 'error');
                }
            });
        }
    });
});
