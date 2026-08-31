$(document).ready(function() {
	$('#feedbackForm').on('submit', function(event) {
		let isValid = true;

		// Loop through each row in the table
		$('tbody tr').each(function() {
			const row = $(this);
			const radios = row.find('input[type="radio"]'); // Find radio buttons in the current row
			const selected = radios.is(':checked'); // Check if any radio button is selected

			if (!selected) {
				isValid = false; // If no radio is selected, mark form as invalid
				row.addClass('error-row'); // Optional: highlight the row
			} else {
				row.removeClass('error-row'); // Remove error class if valid
			}
		});

		if (!isValid) {
			event.preventDefault(); // Prevent form submission
			showToast("Error", feedbackValidation)
		}
	});

	validateLoginIssueSave();
    blinkCheckbox();
});

// Priority mess selection validation
function validateMessSelection() {
    const isSelected = $('.btn-check:checked').length > 0;
    if (!isSelected) {
        showToast("Error", messValidationMsg);
        return false;
    }
//    let isPeriodOpen = false;
//    $.ajax({
//        url: `${contextPath}${baseUrl}${checkMessPeriod}`,
//        type: 'GET',
//        async: false,
//        success: function(result) {
//            if (!result) {
//                showToast("Error", "Mess Registration period is closed");
//                isPeriodOpen = false;
//            } else {
//                isPeriodOpen = true;
//            }
//        },
//        error: function() {
//            showToast("Error", "Couldn't validate mess registration period.");
//            isPeriodOpen = false;
//        }
//    });
//
//    return isPeriodOpen;
}

function showInfoModal(type,studentEditStatus,feedBack,messId) {
	const infoModal = new bootstrap.Modal(document.getElementById('infoModal'));

	if(type === 'feedBackForm' && !studentEditStatus && feedBack !=='Enable') {
        showLoader();
        fetch(`${contextPath}${baseUrl}${getMessFeedback}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network error");
                }
                return response.json();
            }).then(isAllowed => {
                hideLoader();
                if (isAllowed) {
                    $('#deleteModalText').html(feedBackSubmittedMsg);
                } else {
                    $('#deleteModalText').html(feedBackNotSubmittedMsg);
                }
                infoModal.show();
            }).catch(err => {
                hideLoader();
                showToast('Error', "Something went wrong");
            });
	}
	else if(type === 'studentBioData' && !studentEditStatus){
		$('#deleteModalText').html(bioDataFilledMsg);
		infoModal.show();
	}
}

function validateAndFetchStudent() {
    var studentId = $('#studentId').val().trim();

    if (!studentId) {
        showToast('Error', 'Student ID cannot be empty');
        return;
    }
    // ✅ Validate student ID format
    if (!/^[A-Za-z0-9]+$/.test(studentId)) {
        showToast('Error', 'Invalid Student ID format');
        return;
    }
    // ✅ Call student details API
    getStudentDetails(studentId);
}

function getStudentDetails(studentId) {
    disableSaveButton();
    document.getElementById("loadingSpinner").style.display = "block";

$.ajax({
        url: contextPath + baseUrl + studentInfoURL + "/" + studentId,
        type: "GET",
        success: function(response) {
            if(response!=null && response!="") {
                $('#studentName').text(response.studentName || '');
                $('#hostelName').text(response.hostelName || '');
                $('#roomNo').text(response.roomNumber || '');
                $('#genderId').text(
                    response.gender === 'M' ? 'Male' :
                    response.gender === 'F' ? 'Female' : ''
                );

                // ✅ Load mess list using studentId
                 setTimeout(() => loadMessFragmentByStudentId(studentId), 50);
            }else{
                showToast('Error', 'Student ID is invalid');
                $('#studentId').val('');
                $('#studentName').text('');
                $('#hostelName').text('');
                $('#roomNo').text('');
                $('#genderId').text('');
                 enableSaveButton();
                document.getElementById("loadingSpinner").style.display = "none";
            }

        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}

//document.addEventListener("DOMContentLoaded", function () {
//    const genderInputs = document.querySelectorAll('input[name="gender"]');
//    genderInputs.forEach(input => {
//        input.addEventListener("change", function () {
//            const selectedGender = this.value;
//            if (selectedGender) {
//                loadMessFragment(selectedGender);
//            }
//        });
//    });
//});

function loadMessFragmentByStudentId(studentId) {
    const container = document.getElementById("messInnerContainer");
    container.innerHTML = "<p>Loading mess list...</p>";
    disableSaveButton();

    fetch(`${contextPath}${baseUrl}/getMessFragmentByGender?studentId=${studentId}`)
        .then(response => response.text())
        .then(html => {
            container.innerHTML = html;
            enableSaveButton(); // ✅ Re-enable Save only after mess list loads
            document.getElementById("loadingSpinner").style.display = "none";
        })
        .catch(error => {
            console.error("Error loading mess list fragment:", error);
            container.innerHTML = "<p class='text-danger'>Failed to load mess list.</p>";
            enableSaveButton();
            document.getElementById("loadingSpinner").style.display = "none";
        });
}

function disableSaveButton() {
    document.getElementById("loginIssueSave").disabled = true;
}

function enableSaveButton() {
    document.getElementById("loginIssueSave").disabled = false;
}

function validateLoginIssueSave() {
    $('#loginIssueSave').on('click', function(event) {
        disableSaveButton();
        updateInvalidDivClass();
        const captchaCodeResetPass = $('#captchaCode').val();

        let isValid = true;
        const requiredFields = $('input[required], textarea[required]');
        isValid &= validateRadioAndText(requiredFields);
        if (!isValid) {
            showToast('Error', 'Please fill all the mandatory fields and save.');
            enableSaveButton();
            return false;
        }

        const isSelected = $('.btn-check:checked').length > 0;
        if (!isSelected) {
            showToast("Error", messValidationMsg);
            enableSaveButton();
            return false;
        }

        // Asynchronous CAPTCHA validation
            validateCaptcha(captchaCodeResetPass).then(isCaptchaValid => {
                if (isCaptchaValid) {
                     $('#loginIssueForm').submit();
                } else {
                    $requiredMessage.removeClass(displayNone);
                    $('#captchaCode').addClass(errorClass).removeClass(validClass);
                    $(".captchaError").addClass(errorClass).html('<div class="text-danger"> Invalid Captcha </div>');
                    showToast("Error", "Invalid Captcha");
                    enableSaveButton();
                }
            }).catch(error => {
                console.error('CAPTCHA validation failed:', error);
                $('#errorDiv').html('CAPTCHA validation failed. Please try again.');
                enableSaveButton();
            });


    });
}

function blinkCheckbox() {
    const hoverFill = '.hover-fill';
    $(hoverFill).each(function () {
        if ($(this).hasClass("btn-outline-orange")) {
            $(this).data("type", "orange");
        } else {
            $(this).data("type", "primary");
        }
    });
    $(hoverFill).on("mouseenter", function () {
        if ($(this).data("type") === "orange") {
            $(this)
                .removeClass("btn-outline-orange")
                .addClass("btn-orange");
        } else {
            $(this)
                .removeClass("btn-outline-tmPrimary")
                .addClass("btn-tmPrimary");
        }
    });
    $(hoverFill).on("mouseleave", function () {
        const input = $("#" + $(this).attr("for"));
        if (!input.prop("checked")) {
            if ($(this).data("type") === "orange") {
                $(this)
                    .removeClass("btn-orange")
                    .addClass("btn-outline-orange");
            } else {
                $(this)
                    .removeClass("btn-tmPrimary")
                    .addClass("btn-outline-tmPrimary");
            }
        }
    });
    $(".btn-check").on("change", function () {
        $(".hover-fill").each(function () {
            const input = $("#" + $(this).attr("for"));

            if ($(this).data("type") === "orange") {
                $(this)
                    .removeClass("btn-orange btn-outline-orange")
                    .addClass(input.prop("checked") ? "btn-orange" : "btn-outline-orange");
            } else {
                $(this)
                    .removeClass("btn-tmPrimary btn-outline-tmPrimary")
                    .addClass(input.prop("checked") ? "btn-tmPrimary" : "btn-outline-tmPrimary");
            }
        });
    });
}