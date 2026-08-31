function approve(studentId, newRollNo, id, element) {
    $('#infoModal').modal('show');
    $('#infoModalPositive').click(function () {
        $('#infoModal').modal('hide');
        $.ajax({
            url: contextPath + baseURL + approveURL + '/' + studentId + '/' + newRollNo + '/' + id,
            type: 'GET',
            success: function (response) {
                if (response !== null && response !== '') {
                    if(response.status=='Success'){
                         deleteTableRow(element.id);
                    }
                    showToast(response.status, response.message);
                } else {
                    showToast("Error", "Failed to approve!");
                }
            },
            error: function (error) {

            }
        });
        $('#infoModalPositive').off('click');
    })
}