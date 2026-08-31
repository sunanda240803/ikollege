$(document).ready(function () {

    $('.modal').modal({
        backdrop: 'static',
        keyboard: false
    });

    $(document).on('click', '#addNewId', function () {
        setModalMode('add');
        $('#wardenDetailsForm').modal('show');
    });

    // #wardenDetailsFormSave
    $(document).on('click', '#wardenDetailsFormSave', function () {
        const mode = $('#temporaryAccommodationConfigModal').data('mode');
        const form = $('#wardenDetailsForm');
        const requiredFields = form.find('input[required]:visible,select[required]:visible,textarea[required]:visible');
        let result = validateRadioAndText(requiredFields);
        if (result) {
            if (mode === 'add') {
                addWardenDetails();
            } else {
                updateWardenDetails();
            }
        } else {
            return;
        }
    });

    function setModalMode(mode) {
        const saveButton = $('#wardenDetailsModalSave');
        const modalTitle = $('#modalHeaderText');

        if (mode === 'add') {
            saveButton.find('span').text('Add');
            modalTitle.text('Add New Details');
        } else {
            saveButton.find('span').text('Update');
            modalTitle.text('Update Details');
        }

        $('#temporaryAccommodationConfigModal').data('mode', mode);
    }
});