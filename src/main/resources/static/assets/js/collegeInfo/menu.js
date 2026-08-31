$(document).ready(function() {
	$('#addNewId').click(function() {
		editMenu(null);
	});
});

function validateForm(){
	const requiredFields = $('input[required]');
	let isValid = true;
    requiredFields.each(function() {
        if (!$(this).val().trim()) {
            $(this).addClass('is-invalid');
            isValid = false;
        } else {
            $(this).removeClass('is-invalid');
        }
    });

    if (!valRequiredSelect($('#menuType')[0])) {
        isValid = false;
    }

	if(isValid){
		$('#menuMasterForm').submit();
	}else{
		return false;
	}
}

function editMenu(element) {
    const menuId = element !== null ? element.getAttribute("menu-id") : 0;

    $.ajax({
        url: contextPath + baseURL + "/" + menuId,
        type: "Get",
        success: function(response) {
            $('#modalDiv').html(response);
            $('#menu-management').modal('show');
            $('.selectpicker').selectpicker('refresh');
            $('#menu-managementSave span').html('Update');
            updateSaveButtonStyle(menuId, $('#menu-managementSave'));
            $('#menuId').val(menuId > 0 ? menuId : 0);
            $('#menu-managementSave').on("click", function(e) {
                validateForm();
            });
        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}

function deleteMenu(element) {
    const menuId = element !== null ? element.getAttribute("menu-id") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').off('click').click(function() {
        $('#deleteModal').modal('hide');
        $.ajax({
            url: contextPath + baseURL + "/" + menuId,
            type: "DELETE",
            success: function(response) {
                if (response === 'Success') {
                    $('#deleteReq_' + menuId).parents('tr').remove();
                }
                showToast(response, "In-activated the menu successfully");
            },
            error: function(error) {
                showToast('Error', 'Error occurred');
            }
        });

        $('#deleteModalPositive').off('click');
    });

}

function updateCheckboxValue(checkbox) {
	checkbox.value = checkbox.checked ? 'Y' : 'N';
}