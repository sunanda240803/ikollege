$(document).ready(function () {
    $(document).on('click', '#addNewId', function () {
        addEdit(null);
    });

    if (modalError === true) {
        addEdit(null);
    }

    $('#additionalButton').click(function() {
        window.open(contextPath+baseURL+downloadURL, '_blank');
    });
});

function addEdit(element){
    const accomId = element !== null ? element.getAttribute("data-id") : 0;
    fetch(contextPath + baseURL + "/" + accomId
        , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalDiv').html(response);
        $('#temporaryAccommodationConfigModal').modal('show');
        updateSaveButtonStyle(accomId, $('#temporaryAccommodationConfigModalSave'));

        $("#temporaryAccommodationConfigModalSave").off("click").on("click", async function (e) {
            e.preventDefault();
            updateInvalidDivClass();
            $(this).attr('disabled', true);

            const requiredFields = $('input[required], select[required], textarea[required]');
            let status = valRequiredMultiTextRadio(requiredFields);

            if (status) {
                $('#tempAccommodationConfigForm').submit();
            }
            else{
                $(this).attr('disabled', false);
            }
        });

        $("#temporaryAccommodationConfigModalValidateBackend").off("click").on("click", function (e) {
            $('#tempAccommodationConfigForm').submit();
        })

    });
}

function deleteTempAccomConfig(element) {
    const accomId = element !== null ? element.getAttribute("data-id") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');
        fetch(contextPath + baseURL + deleteURL + "/" + accomId
            , {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            if (response.status === 'Success') {
                deleteTableRow('deleteAccom_' + accomId);
                showToast('Success', response.message);
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    })
}
