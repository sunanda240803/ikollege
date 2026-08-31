$(document).ready(function () {
    // Initially Enables the 'Add Members' button
    const saveFormBtn = $("#updateId");
	updateSaveButtonStyle(1, saveFormBtn);
    saveFormBtn.on("click", function (e) {
		$(this).attr('disabled', true);	
        e.preventDefault();
        $('form[id="menuPrivilegeForm"]').submit();
    });
    checkNewRole(false);
});

function checkNewRole(tableReset) {
    const roleSelected = $('#role')[0];
    const role = roleSelected.selectedOptions[0].value;
    const newRole = $('#newRole');
    const newRoleName = $('#newRoleName');
    const saveForm = $('#saveForm');
    toggleGetMenu(false);
    if (role === '-1') {
        newRole.removeClass("d-none");
        if (newRoleName.val().length > 2) {
            toggleGetMenu(true);
            saveForm.removeClass('disabled');
        }
    } else if (role !== '0') {
        toggleGetMenu(true);
        newRole.addClass("d-none");
        fieldFilled(newRoleName)
    } else if (role === '0') {
        saveForm.addClass('disabled');
    }
    if (tableReset) {
        resetTable();
    }
}

function toggleGetMenu(show) {
    const getMenu = $('#getMenu');
    const saveFormBtn = $("#updateId");
    const deleteBtn = $("#deleteRole");
    if (show) {
        getMenu.removeClass('disabled');
        saveFormBtn.removeClass('disabled');
        deleteBtn.removeClass('disabled');
    } else {
        getMenu.addClass('disabled');
        saveFormBtn.addClass('disabled');
        deleteBtn.addClass('disabled');
    }
}

function validateRoleName() {
    const roleSelected = $('#role')[0];
    const role = roleSelected.selectedOptions[0].value;
    const newRole = $('#newRoleName');
    let errorCount = 0;
    if (role === '-1') {
        errorCount += fieldEmpty(newRole)
    } else {
        fieldFilled(newRole)
    }
    return errorCount === 0;
}

function resetTable() {
    $('#menuPrivilegeForm').html();
    $('#saveForm').addClass('disabled');
}

function selectAll(input) {
    const dataMenuSplit = input.id.split('_');
    const dataMenu = +dataMenuSplit[dataMenuSplit.length - 1];
    const isChecked = input.checked;
    if (dataMenu === 0) {
        const checkbox = $('.submenu_' + input.dataset['menu']);
        $.each(checkbox, function () {
            if ((isChecked && !$(this)[0].checked) || (!isChecked && $(this)[0].checked)) {
                $(this)[0].click();
            }
        });
    }
    countSelected(input);
}

function countSelected(input) {
    const pMenuId = input.dataset['menu'];
    const showCount = $('#selected_' + pMenuId);
    const totalCount = $('#total_' + pMenuId);
    let count = 0;
    $('#rolePrivClps_' + pMenuId).find(".submenu_" + pMenuId).each(function () {
        if ($(this).is(":checked")) count++;
    })
    const firstMenu = $("#schoolInfoChk_" + pMenuId + "_0");
    if (+totalCount.html() > 1) {
        if ((+count >= (+totalCount.html() - 1) && !firstMenu[0].checked)) {
            firstMenu[0].checked = true;
            firstMenu.val(true);
            count++;
        } else if ((+count < +totalCount.html() && firstMenu[0].checked)) {
            firstMenu[0].checked = false;
            firstMenu.val(false);
            count--;
        }
    }
    showCount.html(count);
}

function deleteRoleFunc() {
    const roleSelected = $('#role')[0];
    const role = roleSelected.selectedOptions[0].value;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function() {
	   $.ajax({
           		url: contextPath + '/roleMenuPrivilege/delete-role/' + role,
           		type: "POST",
           		success: function(response) {
                       const isSaved = response === 'saved';
                       const res = isSaved ? 'Success' : 'Error';
                       const message = isSaved ? 'Role Deleted Successfully!.' : response;
                       showToast(res, message);
                       if (isSaved) {
               		    toggleGetMenu(false);
                           setTimeout(function() {
                               location.reload();
                           }, 5000);
                       }
                   },
           		error: function() {
           			showToast('Error', 'Something went wrong.');
           		}
           	});
	});
}