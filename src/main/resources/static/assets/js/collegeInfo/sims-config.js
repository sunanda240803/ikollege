$(window).on('load', function() {
	 if (modalError === true) {
        editSimsConfig(null);
    }
	
	var addNewId = $('#addNewId');
	addNewId.removeAttr('disabled');
	addNewId.click(function() {
		editSimsConfig(null);
	});
	

});


async function editSimsConfig(element) {
    const simsId = element !== null ? element.getAttribute("data-sims-id") : 0;
    
    $.ajax({
        url: contextPath + baseURL + "/" + simsId,
        type: "GET",
        success: function(response) {
            $('#modalDiv').html(response);
            $('#sims-config-modal').modal('show');
            updateSaveButtonStyle(simsId, $('#sims-config-modalSave'));
            
            // Save button click handler
            $("#sims-config-modalSave").off("click").on("click", async function(e) {
                e.preventDefault();
                updateInvalidDivClass();
                
                // Validate required fields
                const requiredFields = $('input[required], textarea[required]');
                const status = valRequiredMultiTextRadio(requiredFields);
                
                if (status) {
                    const configKey = $('#configKey').val();
                    
                   
                    const configKeyStatus = await checkConfigKeyExists(configKey, simsId); 
                    
                    if (configKeyStatus) {
                        $('#simsConfigForm').submit();
                    } else {
                        $("#sims-config-modalSave").attr('disabled', false);
                    }
                } else {
                    $("#sims-config-modalSave").attr('disabled', false);
                    return false;
                }
            });

            // Backend validate button click handler
            $("#sims-config-modalValidateBackend").off("click").on("click", function(e) {
                $('#simsConfigForm').submit();
            })
        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}



function deleteSims(element) {
	const simsId = element !== null ? element.getAttribute("data-sims-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + "/" + simsId
			, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
				return response.json();
			}).then(response => {
				if (response.status === 'Success') {
					$('#deleteSims_' + simsId).parents('tr').remove();
					showToast(response.status, response.message);
				} else {
					showToast('Failure', response.message);
				}
			});
		$('#deleteModalPositive').off('click');
	})
}

async function checkConfigKeyExists(configKey, id) {
    const $configInput = $('#configKey');
    const $errorMessage = $('#errorMessage');
    
    if (configKey) {
        try {
            
            const response = await fetch(contextPath + baseURL + existURL + "/" + configKey + "/" + id, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            const data = await response.json();
            
            if (data) {  
                $errorMessage.text(messages.configKey).removeClass('d-none').addClass('text-danger');
                $configInput.val('');  
                return false; 
            } else {
                $errorMessage.addClass('d-none').removeClass('text-danger');
                return true; 
            }
        } catch (error) {
            console.error('Error checking Config Key Exist:', error);
            return false; 
        }
    }
    return true; 
}









