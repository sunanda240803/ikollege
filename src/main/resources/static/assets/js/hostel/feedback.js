$(window).on('load', function() {
	 if (modalError === true) {
        editFeedback(null);
    }
	
	var addNewId = $('#addNewId');
	addNewId.removeAttr('disabled');
	addNewId.click(function() {
		editFeedback(null);
	});
	

});




async function editFeedback(element) {
    const feedId = element !== null ? element.getAttribute("data-feed-id") : 0;
    $.ajax({
        url: contextPath + baseURL + "/" + feedId,
        type: "GET",
        success: function(response) {
            $('#modalDiv').html(response);
            $('#feedback-modal').modal('show');
            updateSaveButtonStyle(feedId, $('#feedback-modalSave'));

            const selectedWeightage = $('#feedback-modal input[name="feedbackWeightage"][value="' + response.feedbackWeightage + '"]');
            selectedWeightage.prop('checked', true);  

            $("#feedback-modalSave").off("click").on("click", async function(e) {
                e.preventDefault();
                updateInvalidDivClass();
                let isValid = true;

                // Validate required textarea fields
                $('textarea[required]').each(function () {
                    if ($(this).val().trim() === "") {
                        $(this).siblings('.invalid-feedback').show();  
                        isValid = false;
                    } else {
                        $(this).siblings('.invalid-feedback').hide();  
                    }
                });

                // Check if a radio button is selected for weightage
                const isRadioSelected = $('input[name="feedbackWeightage"]:checked').length > 0;
                if (!isRadioSelected) {
                    $(".RadioCheckToggle").siblings('.invalid-feedback').show();  
                    isValid = false;
                } else {
                    $(".RadioCheckToggle").siblings('.invalid-feedback').hide();  
                }

                if (isValid) {
                    $('#feedback').submit();  
                } else {
                    $("#feedback-modalSave").attr('disabled', false);  
                    return false;  
                }
            });

            
            $("#feedback-modalValidateBackend").off("click").on("click", function(e) {
                $('#feedback').submit();
            });
        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}



function deleteFeed(element) {
	const feedId = element !== null ? element.getAttribute("data-feed-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + "/" + feedId
			, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
				return response.json();
			}).then(response => {
				if (response.status === 'Success') {
					$('#deleteFeed_' + feedId).parents('tr').remove();
					showToast(response.status, response.message);
				} else {
					showToast('Failure', response.message);
				}
			});
		$('#deleteModalPositive').off('click');
	})
}



