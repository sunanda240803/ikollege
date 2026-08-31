function showErrorToast(title, message) {
    let errorMessage = message;
    console.log(title);
    console.log(message);
    if (title === 'Error' && message !== undefined && message !== '') {
        errorMessage = "<ul>";
        let errors = message.split("/n");
        for (let i = 0; i < errors.length; i++) {
            errorMessage += "<li>" + errors[i] + "</li>";
        }
        errorMessage += "</ul>";
    }
    showToast(title, errorMessage);
}
function showToast(title, message) {
    if (title !== '' && title !== null) {
        let className = "";
        let icon = "";
        switch (title) {
            case 'Success':
                className = 'successToast text-success';
                icon = "fa-circle-check";
                break;
            case 'Logout':
                className = "infoToast text-primary"
                icon = "fa-info-circle";
                break;
            default:
                className = 'errorToast text-danger';
                icon = "fa-ban";
                break;
        }

        new bs5.Toast({
            header: '<i class="fas ' + icon + ' me-2"></i>' + title,
            body: message,
            className: className,
            delay: 5000,
            btnCloseWhite: true
        }).show()
    }
}

function showLogoutToast (logoutReason) {
    switch (logoutReason) {
        case 'sessionExpired':
            showErrorToast("Logged out", 'Session expired. Please login to continue.');
            break;
        case 'noPrivilege':
            showErrorToast("Logged out", 'You dont have any Privileges. Contact System Admin.');
            break;
        case 'logout':
            showToast("Logout", "You have been logged out successfully.")
            break;
        case 'logout':
            showToast("Login failed", "You have been logged out successfully.")
            break;
        case 'invalidCredentials':
            showErrorToast("Login failed", "Invalid Login ID or password. Please try again.");
            break;
        default:
            showErrorToast("Logged out", 'Reason Unknown. Please login again to continue.');
    }
}
  function validateForm() {
    var isValid = true;
    var username = document.getElementById("username").value.trim();
    var password = document.getElementById("password").value.trim();
    var institution = document.getElementById("institution").value;

    var usernameError = document.getElementById("username-error");
    var passwordError = document.getElementById("password-error");
    var institutionError = document.getElementById("institution-error");

    // Clear previous error messages
    usernameError.textContent = "";
    passwordError.textContent = "";
    institutionError.textContent = "";

    try {
        // Validate each field individually
        if (!username) {
            usernameError.textContent = "Please Enter Login ID";
            isValid = false;
        }
        if (!password) {
            passwordError.textContent = "Please Enter Password";
            isValid = false;
        }
        if (!institution || institution === "0") {
            institutionError.textContent = "Please select institution";
            isValid = false;
        }
    } catch (error) {
        console.error("An error occurred while validating the form:", error);
        // Handle the error gracefully, if needed
    }

    return isValid;
}
