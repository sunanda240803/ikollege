function viewResetPasswordModal() {
	$.ajax({
		url: contextPath + resetPasswordURL,
		type: "GET",
		success: function(response) {
			$('#resetPasswordDiv').html(response);
			$('#resetPassword').modal('show');
			clearFormElements('resetForm');
			
			$('#resetPasswordSave').click(function() {
			    const elementArray = ['#applicantPassword', '#confirmPassword', '#newPassword'];
			    const isValid = valRequiredTextArray(elementArray);
			    if (!isValid) {
			        $('#currentPasswordRequired').removeClass(displayNone);
			        $('#confirmPasswordRequired').removeClass(displayNone);
			        $('#newPasswordRequired').removeClass(displayNone);
			        $('#invalidCurrentPassword').addClass(displayNone);
			        $('#invalidConfirmPassword').addClass(displayNone);
			        $('#invalidNewPassword').addClass(displayNone);
			        return false;
			    }
			    const passwordValid = validatePasswordLength('#newPassword', 6);
			    const confirmPasswordValid = validatePasswordLength('#confirmPassword', 6);

			    if (!passwordValid || !confirmPasswordValid) {
			        $('#confirmPasswordRequired').addClass(displayNone);
			        $('#newPasswordRequired').addClass(displayNone);
			        $('#currentPasswordRequired').addClass(displayNone);
			        $('#invalidConfirmPassword').removeClass(displayNone).html('Password should be at least 6 characters');
			        $('#invalidNewPassword').removeClass(displayNone).html('Password should be at least 6 characters');
			        return false;
			    }

			    const oldPassword = $('#applicantPassword').val();
			    const confirmPassword = $('#confirmPassword');
			    const newPassword = $('#newPassword').val();
			    if (confirmPassword.val() !== newPassword) {
			        $('#confirmPasswordRequired').addClass(displayNone);
			        $('#newPasswordRequired').addClass(displayNone);
			        confirmPassword.addClass('is-invalid');
			        $("#invalidConfirmPassword").removeClass(displayNone).html("New Password and Confirm New Password must be same")
			        return false;
			    }
			    if ((newPassword !== null && newPassword !== '') && (oldPassword !== null && oldPassword !== '')) {
			        const formData = $('#resetForm').serialize(); // Serialize form data

			        // Create a Promise for the AJAX request
			        const resetPasswordPromise = new Promise((resolve, reject) => {
			            $.ajax({
			                type: "POST",
			                url: contextPath + resetPasswordURL,
			                data: formData,
			                success: function (response) {
			                    resolve(response);
			                },
			                error: function () {
			                    reject('Error in Updating the password');
			                }
			            });
			        });

			        // Handle the Promise result
			        resetPasswordPromise.then(response => {
			            if (response === 'success') {
			                $('#successModal').modal('show');
			                $('#successModalCancel').addClass(displayNone);
			                $('#resetPassword').modal('hide');
			            } else if (response === 'Password Mismatch') {
			                $('#resetPassword').modal('show');
			                $('#applicantPassword').addClass('is-invalid');
			                $("#currentPasswordRequired").addClass(displayNone).html('');
			                $("#invalidCurrentPassword").removeClass(displayNone).html('Current Password is invalid').css('display', 'block');
			            }
			        }).catch(error => {
			            $('#resetPassword').modal('show');
			            showToast('Failure', error);
			        });
			    }
			});
		}
	});
}

$('#successModalPositive').click(function() {
    window.location.href = contextPath + logoutURL;
});