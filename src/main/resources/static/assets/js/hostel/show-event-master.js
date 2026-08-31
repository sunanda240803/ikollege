$(document).ready(function () {
    $('#addNewId').removeAttr('disabled');

    if (modalError === true) {
        editShowEventMaster(null);
    }

    $('#addNewId, #editButton').click(function () {
        editShowEventMaster(null);
    });
    $('#deleteButton').click(function () {
        deleteShowEventMaster();
    });
});

function editShowEventMaster(element) {
    const eventId = element !== null ? element.getAttribute("data-event-id") : 0;
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
        $('#show-event-master-modal').modal('show');
        $('.selectpicker').selectpicker('refresh');
        updateSaveButtonStyle(eventId, $('#show-event-master-modalSave'));
        $("#show-event-master-modalSave").off("click").on("click", function (e) {
            e.preventDefault();
            updateInvalidDivClass();
            const saveButton = $(this);
            const requiredFields = $('input[required], select[required], textarea[required]');
            let status = valRequiredMultiTextRadio(requiredFields);
            const dateRanges = [
                {fromElement: "eventStartingDate", toElement: "eventEndingDate"},
                {fromElement: "actualEventStartingDate", toElement: "actualEventEndingDate"},
                {fromElement: "eventAccRegistrationStartingDate", toElement: "eventAccRegistrationEndingDate"}
            ];
            const dateValidationStatus = validateBetweenDateRanges(dateRanges);
            if (dateValidationStatus) {
                const salesEndDateElement = $('#eventEndingDate');
                const salesEndDateValue = salesEndDateElement.val();
                const eventEndDateValue = $('#actualEventEndingDate').val();

                const salesEndDate = salesEndDateValue ? new Date(salesEndDateValue) : null;
                const eventEndDate = eventEndDateValue ? new Date(eventEndDateValue) : null;

                const errorDiv = salesEndDateElement.siblings('.invalid-feedback');

                if (salesEndDate && eventEndDate) {
                    if (salesEndDate > eventEndDate) {
                        errorDiv.html(errorDiv.siblings('.invalid-feedback-sales-greater-event').html());
                        salesEndDateElement.addClass(errorClass).removeClass(validClass);
                        status = false;
                    } else {
                        salesEndDateElement.removeClass(errorClass).addClass(validClass);
                    }
                }
            }

            const accHead = $('#accountHead');
            if (!valRequiredSelect(accHead)) {
                accHead.parent().find('.dropdown-toggle').addClass(errorClass).removeClass(validClass);
                return false;
            } else {
                accHead.selectpicker('setStyle', errorClass, 'remove');
                accHead.parent().find('.dropdown-toggle').removeClass(errorClass).addClass(validClass);
            }

            if (status && dateValidationStatus) {
                saveButton.attr('disabled', true);
                $('#show-event-master-modal-form').submit();
            } else {
                return false;
            }
        });

        $("#show-event-master-modalValidateBackend").off("click").on("click", function (e) {
            $('#show-event-master-modal-form').submit();
        })

    });
}

function deleteShowEventMaster(element) {
    const eventId = element !== null ? element.getAttribute("data-event-id") : 0;
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
                showToast('Success', 'Event Master deleted successfully');
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    })
}