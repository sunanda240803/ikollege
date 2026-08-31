
$(document).ready(function() {
    $('#addNewId').click(function() {
        addLateFee();
    });

    if (modalError === true) {
        addLateFee();
    }
});

function addLateFee(){
    fetch(contextPath + baseURL + lateFeeEnrollment + "/0"
        , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalDiv').html(response);
        $('#late-fee-modal').modal('show');
        // $('.selectpicker').selectpicker('refresh');
        $("#late-fee-modalSave").off("click").on("click", function (e) {
            $(this).attr('disabled', true);
            e.preventDefault();
            updateInvalidDivClass();
            const requiredFields = $('input[required], select[required], textarea[required]');
            let status = valRequiredMultiTextRadio(requiredFields);

            if(status){
                $('#lateFeeForm').submit();
            }
            else{
                $(this).attr('disabled', false);
            }
        });

        $("#late-fee-modalValidateBackend").off("click").on("click", function (e) {
            $('#lateFeeForm').submit();
        })

    });
}