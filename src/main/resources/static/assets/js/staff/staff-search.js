function editFacultyDetails(element) {
	const staffId = element.getAttribute("data-staff-id");
	window.location =contextPath+targetURL+'/'+staffId
}
$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	window.location = contextPath+targetURL+'/new'
});


// Delete staff search
function deleteStaff(element) {
	const staffId = element !== null ? element.getAttribute("data-staff-id") : 0;
	$('#deleteModal').modal('show');
   $('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
		   url: contextPath + baseURL + "/" + staffId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					deleteTableRow('deleteStaff_' + staffId);
				}
				showToast(response.status, response.message);
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

function viewStaffCred(element) {
    const staffId = element ? element.getAttribute("data-staff-id") : 0;
    $('#resetPasswordModal').modal('show');
    $('#deleteModalText').html(`Are you sure you want to reset password for <b>${staffId}</b>?`);
    $('#resetPasswordModalPositive').off('click').on('click', function () {
            $.ajax({
                url: contextPath + baseURL + getResetPasswordURL + "/" + staffId,
                type: "POST",
                success: function (res) {
                    $('#resetPasswordModal').one('hidden.bs.modal', function () {
                        $('#successModal modalheader')
                            .html('Password Reset Successful');
                        $('#successModal modalbody span')
                            .html(`
                                <b>Username:</b> ${staffId} <br>
                                <b>Password:</b> ${res.newPassword}
                            `);
                        $('#successModal').modal('show');
                        $('#successModalCancel').addClass(displayNone)
                        $('#successModalPositive').off('click').on('click', function(){
                            location.reload();
                        });
                    });
                    $('#resetPasswordModal').modal('hide');
                },
                error: function () {
                    showToast('Error', 'Reset failed');
                }
            });
        });
}