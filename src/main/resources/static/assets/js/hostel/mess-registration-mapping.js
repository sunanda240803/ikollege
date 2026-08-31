$(document).ready(function () {
    const saveButtonMapping = {
        '#girlsOptionOneSaveButton': 'girlsOptionOne',
        '#girlsOptionTwoSaveButton': 'girlsOptionTwo',
        '#boysOptionSaveButton': 'boysOption',
    };
    Object.keys(saveButtonMapping).forEach(function (buttonId) {
        $(buttonId).off("click").on("click", function (e) {
            e.preventDefault();
            handleFormSubmit(saveButtonMapping[buttonId]);
        });
    });
});

function handleFormSubmit(fieldPrefix) {
    const fieldSelector = `#${fieldPrefix}`;
    const selectedMessIds = $(`input[type="checkbox"][id^="CheckToggle_"][id$="_${fieldPrefix}"]:checked`)
        .map(function () {
            return $(this).val();
        }).get().join(',');

    $(fieldSelector).val(selectedMessIds);
    $('#mess-registration-mapping-form').submit();
}
