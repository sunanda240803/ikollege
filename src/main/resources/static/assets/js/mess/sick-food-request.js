$(document).ready(function() {

    $('#addNewId').click(function() {
        window.location.href = contextPath + baseURL + "/0";
    });

    let phNumMaskElementArray = ['#mobileNumber'];
    inputMask(phNumMaskElementArray, mobileNumberMask);

});

$('#sickFoodSave').off("click").on("click", function(e) {
    const cancelId = $('#cancel');
    $(this).attr('disabled', true);
    cancelId.attr('disabled', true);
    e.preventDefault();
	let status=true;
	    const isChecked = $('#messSession input[type="checkbox"]:checked').length > 0;
    const requiredFields = $('input[required], select[required], textarea[required]');
     status = valRequiredMultiTextRadio(requiredFields);
       let mobileNumberValid = false;
            let reqMobileField = ['#mobileNumber'];
            reqMobileField.forEach(function(element1) {
                let element = $(element1);
                let errorDiv = element.siblings('.invalid-feedback');
                if (!!!element.val()) {
                    element.addClass("is-invalid");
                    $(element).removeClass(validClass);
                    errorDiv.html(element.siblings('.invalid-req-feedback').html());
                    mobileNumberValid = false;
                } else if (!!element.val() && $(element).inputmask("unmaskedvalue").length !== 10) {
                    $(element).addClass(errorClass);
                    $(element).removeClass(validClass);
                    errorDiv.html(element.siblings('.invalid-val-feedback').html());
                    mobileNumberValid = false;
                } else {
                    $(element).addClass(validClass).removeClass(errorClass);
                    mobileNumberValid = true;
                }
            });
            
    // If no checkbox is selected, show validation message
    if (!isChecked) {
        $('#messSession').addClass(errorClass); // Show the validation message
        $(this).attr('disabled', false);
        cancelId.attr('disabled', false);
        status =false; // Prevent form submission
    }
    else{
	$('#messSession').addClass(validClass); // Show the validation message
	$('#messSession').removeClass(errorClass);
}
	
	if (status && mobileNumberValid) {
		$('#sickFoodForm').submit();
	}else{
		return false;
	}
});



document.addEventListener('DOMContentLoaded', function () {
    setMinDateForRequest(); 
    const dateInput = document.getElementById('requestDate');
    const sickFoodSaveButton = document.getElementById('sickFoodSave');
    
    if (dateInput) {
        dateInput.addEventListener('blur', function () {
            const requestDate = this.value;
            toggleSaveButtonVisibility(requestDate, sickFoodSaveButton);
            checkSickFoodRequestExists(requestDate);
        });

        // Initial check to toggle the button if the date is pre-filled
        toggleSaveButtonVisibility(dateInput.value, sickFoodSaveButton);
    }
});


function setMinDateForRequest() {
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('requestDate');
    if (dateInput) {
        dateInput.setAttribute('min', today);
    }
}

function toggleSaveButtonVisibility(requestDate, button) {
    const today = new Date().toISOString().split('T')[0];
    if (requestDate && new Date(requestDate) < new Date(today)) {
        if (button) {
            button.remove(); 
        }
    }
}


function checkSickFoodRequestExists(requestDate) {
    const $dateInput = $('#requestDate');
    const $errorMessage = $('#errorMessage');

    if (requestDate) {
        // AJAX call to check if the sick food request exists
        $.ajax({
            url: contextPath + baseURL + existURL + "/" + requestDate,
            type: 'GET',
            success: function (data) {
                 if (data) {
                    $errorMessage.text('You have already requested food for the same day.').removeClass('d-none').addClass('text-danger');
                    $dateInput.val('');  
                } else {
                    $errorMessage.addClass('d-none').removeClass('text-danger');
                }
            },
            error: function () {
                console.error('Error checking sick food request.');
            }
        });
    }
}

function openFeedbackModal(button) {
	const sessionId = $(button).data("session-id");
	$("#sessionId").val(sessionId);
	const requestId = $(button).data("request-id");
	$("#requestId").val(requestId);
	$('#feedbackModal').modal('show');
}
 $('#feedbackModalSave').off("click").on("click", function(e) {
	event.preventDefault();

	const feedback = $("#feedback").val();
	const rating = $("#ratingContainer input[name='rating']:checked").val();
	const sessionId = $("#sessionId").val();
	const requestId = $("#requestId").val();

	$.ajax({
		url: contextPath + baseURL + status,
		type: 'POST',
		data: {
			id: requestId,
			messSession: sessionId,
			studentFeedback: feedback,
			feedbackRating: rating
		},
		success: function(response) {
            if(response!==null && response!=='') {
                if(response.status === 'Success') {
                    const feedbackButton = document.querySelector(`[data-request-id='${requestId}']`);
                    if (feedbackButton) {
                        feedbackButton.classList.add('hidden');
                    }
                }
                showToast(response.status, response.message);
            }
            setTimeout(() => {
                location.reload();
            }, 5000);
		},
		error: function() {
			console.error('Error checking FeedBack Submit.');
		}
	});
});



function openFoodDeliveryModal(button) {
	const sessionId = $(button).data("session-id");
	$("#sessionId").val(sessionId);
	const requestId = $(button).data("request-id");
	$("#requestId").val(requestId);
	$('#warningModal').modal('show');
}
 $('#warningModalPositive').off("click").on("click", function(e) {
	event.preventDefault();

	const sessionId = $("#sessionId").val();
	const requestId = $("#requestId").val();

	$.ajax({
		url: contextPath + baseURL + foodNotDeliverStatus,
		type: 'POST',
		data: {
			id: requestId,
			messSession: sessionId
			
		},
		success: function(response) {
            if (response !== null && response !== '') {
                if (response.status === 'Success') {
                    $("#notDeliverButton_" + requestId).addClass('d-none')
                }
                showToast(response.status, response.message);
            }
            setTimeout(() => {
                location.reload();
            }, 5000);
        },
		error: function() {
			console.error('Error checking FeedBack Submit.');
		}
	});
});



