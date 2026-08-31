$(document).ready(function () {
    $('#addNewId').click(function () {
        window.location.href = contextPath + baseURL + '/0';
    });
});

$('#saveForm').click(function (e) {
    updateInvalidDivClass();
    e.preventDefault();
    const requiredFields = $('input[required], select[required], textarea[required]');
    $(this).attr('disabled', true);
    let result = validateRadioAndText(requiredFields);
    const elementArray = ['#startTime', '#endTime, #pushTime'];
    const isValid = valRequiredTextArray(elementArray);
    if (result && isValid) {
        const dateRanges = [
            {fromElement: "diningFromDate", toElement: "diningToDate"},
            {fromElement: "registrationStartDate", toElement: "registrationEndDate"}
        ];
        result = validateBetweenDateRanges(dateRanges);
    }
    if (result && isValid) {
        $('#messPeriodConfigForm').submit();
    } else {
        $(this).attr('disabled', false);
    }
});


function updateHiddenTime(baseId) {
    const startHours = $(`#${baseId}Hour`).val() || '00';
    const startMinutes = $(`#${baseId}Minute`).val() || '00';
    const time = (startHours === '00' && startMinutes === '00') ? '' : `${startHours}:${startMinutes}`;
    $(`#${baseId}Time`).val(time);
}

/*function showBulkMailModal() {
    fetch(contextPath + baseURL + mailUrl + "/" + $('#messPeriodConfigId').val(), {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.text())
        .then(response => {
            // Inject the modal content
            $('#modalDiv').html(response);
            integrateModalNicEditor($('#contentId')[0]);
            // Show the modal
            $('#student-mess-bulk-mail-modal').modal('show');

            // Save button logic
            $("#student-mess-bulk-mail-modalSave").off("click").on("click", function (e) {
                e.preventDefault();

                const requiredFields = $('input[required],textarea[required]');
                let status = valRequiredMultiTextRadio(requiredFields);

                const mailContent = validateNicEditorContent($('#contentId')[0]);

                // Submit the form if valid
                if (status && mailContent) {
                    $(this).attr('disabled', true);
                    $('#studentMessBulkMailForm').submit();
                } else {
                    $(this).attr('disabled', false);
                }
            });
        })
        .catch(error => {
            console.error("Error fetching modal content:", error);
        });
}*/

$('#send').click(function () {
    let status = valRequiredSelect($('#bulkMailSubject')[0]);
    const mailContent = validateNicEditorContent($('#contentId')[0]);

    // Submit the form if valid
    if (status && mailContent) {
        $(this).attr('disabled', true);
        $('#studentMessBulkMailForm').submit();
    } else {
        $(this).attr('disabled', false);
    }
})

function showMail() {
    $('#studentMessBulkMailForm').removeClass(dNone);
    new nicEditor({
    		fullPanel: true,
    		buttonList: ['fontSize','bold','italic','underline','strikeThrough','hr','image','upload','forecolor','link','indent','outdent',
                        'unlink','left','center','right','justify','ol','ul','xhtml']
    	}).panelInstance('contentId');

    window.scrollTo({
        top: document.body.scrollHeight,
        behavior: 'smooth'
    });
}

