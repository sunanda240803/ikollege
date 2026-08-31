$(document).ready(function() {
    const deleteModalPositive = $('#deleteModalPositive');
    deleteModalPositive.off('click');
    deleteModalPositive.click(function () {
        $('#deleteModal').modal('hide');
        const path = $('#urlId').val();
        const prefix = $('#prefixId').val();
        $.ajax({
            url: path,
            type: "Delete",
            success: function (response) {
                if (response.status === 'Success') {
                    showToast('Success', $('#toasterMsg').val());
                    $('#' + prefix).parents('tr').remove();
                } else {
                    showToast('Failure', response.message);
                }
            },
            error: function (error) {
                showToast('Error', 'Error occured');
            }
        })
    })
});

function checkTabsEnabled(id) {
    if (id === 'new' || id === 'add') {
        $('.secondary-tab').addClass('disabled')
    }
}

function loadSubTabs(id) {
    let subUrls = $('#subUrls').val().split(",");
    for (let i = 0; i < subUrls.length; i++) {
        if (SHOW_DEBUG_LOG) console.log(subUrls[i]);
        $.ajax({
            url: DOMAIN_URL + subUrls[i] + "/list/" + id,
            type: "GET",
            success: function (response) {
                if (SHOW_DEBUG_LOG) console.log(subUrls[i].split("/")[2]);
                $('#' + subUrls[i].split("/")[2] + "Div").html(response);
            }
        });
    }
}

function deleteDetails(element, subTabUrl, parentId, id, deleteMessagePrefix) {
    const url = DOMAIN_URL + subTabUrl + "/" + parentId + "/" + id;
    const prefix = element.getAttribute("id");
    if (SHOW_DEBUG_LOG) console.log('-' + url + '-');
    $('#deleteModal').modal('show');
    $('#dynamicId').val(id);
    $('#urlId').val(url);
    $('#toasterMsg').val(deleteMessagePrefix + ' details deleted successfully');
    $('#prefixId').val(prefix);
}