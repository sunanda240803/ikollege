$(document).ready(function () {
    $('#addNewId').removeAttr('disabled');

    if (modalError === true) {
        editStudentComplaintConfig(null);
    }

    $('#addNewId, #editButton').click(function() {
        editStudentComplaintConfig(null);
    });

    $('#deleteButton').click(function() {
        deleteStudentComplaintConfig(this);
    });
});

function editStudentComplaintConfig(element) {
    const eventId = element !== null ? element.getAttribute("data-student-complaint-config-id") : 0;
    fetch(contextPath + baseURL + getURL + "/" + eventId
        , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalDiv').html(response);
        $('#student-complaint-config-modal').modal('show');
        $('input[name="complaintType"]').change(toggleShortDescription);
        toggleShortDescription();
        updateSaveButtonStyle(eventId, $('#student-complaint-config-modalSave'));
        console.log('configId:-'+$('#configId').val());
        $("#student-complaint-config-modalSave").off("click").on("click", function (e) {
            e.preventDefault();
            updateInvalidDivClass();
            const saveButton = $(this);
            const requiredFields = $('input[required]:visible, select[required]:visible, input[required]:visible');
            let status = valRequiredMultiTextRadio(requiredFields);
            const emailField = $('#inChargeEmail');
            let isEmailValid = validateMultipleEmailField(emailField);
            console.log(isEmailValid);
            if (status && isEmailValid) {
                saveButton.attr('disabled', true);
                $('#student-complaint-config-form').submit();
            } else {
                return false;
            }
        });
        $("#student-complaint-config-modalValidateBackend").off("click").on("click", function (e) {
            $('#student-complaint-config-form').submit();
        })
    });
}

function deleteStudentComplaintConfig(element) {
    const eventId = element !== null ? element.getAttribute("data-student-complaint-config-id") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');
        fetch(contextPath + baseURL + deleteURL + "/" + eventId
            , {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            if (response.status === 'Success') {
                $('#deleteEvent_' + eventId).parents('tr').remove();
                showToast('Success', 'Student Complaint Configuration deleted successfully');
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    })
}

function toggleShortDescription() {
    const selectedType = $('input[name="complaintType"]:checked').val();
    if (selectedType === 'mess') {
        $('#shortDescriptionContainer').addClass(displayNone);
    } else {
        $('#shortDescriptionContainer').removeClass(displayNone);
    }
}