$(document).ready(function () {
    

});

function activateLogin(element){
    const id = element.getAttribute("data-id");
    $('#enableModal').modal('show');

    $('#enableModalPositive').click(function () {
        const parentRow = $('#'+element.id).closest('tr');
        const statusCell = parentRow.find('td:nth-child(4)');

        $('#enableModal').modal('hide');
        $.ajax({
            url:contextPath+baseURL+'/'+id,
            type:'POST',
            success: function (response) {
                if(response){
                    statusCell.text('Not Blocked');
                    statusCell.removeClass('text-danger').addClass('text-success');
                }
                else{
                    showToast('Failure', "Failed to enable User");
                }
            },
            error: function (error) {
                showToast('Failure', error.message);
            }
        });
        $('#enableButton').hide();
        $('#enableModalPositive').off('click');
    });
}

