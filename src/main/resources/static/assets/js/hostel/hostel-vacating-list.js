$(document).ready(function () {
    $("#getData").click(function (e) {
        e.preventDefault();
        const requiredFields = $('#hostelVacatingListForm input[required]:visible, #hostelVacatingListForm select[required]:visible, #hostelVacatingListForm textarea[required]:visible');

        let result = validateRadioAndText(requiredFields);
        if (!result) {
            showToast('Error', 'Please fill all mandatory fields.');
            return false;
        }
        const formData = $('#hostelVacatingListForm').serialize();
        $.ajax({
            type: 'POST',
            url: '/hostel/vacating-list',
            data: formData,
            success: function (response) {
                showToast('Success', 'Vacating list fetched successfully.');
            },
            error: function (xhr, status, error) {
                showToast('Error', 'An error occurred while fetching the vacating list.');
            }
        });
        return false;
    });
});