$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editDesignation(null);
});




// Save and update designation master
function editDesignation(element) {
    const designationId = element !== null ? element.getAttribute("data-designation-id") : 0;
    $.ajax({
        url: contextPath + baseURL + "/" + designationId,
        type: "GET",
        success: function(response) {
            $('#modalDiv').html(response);
            $('#designation-master-modal').modal('show');
            updateSaveButtonStyle(designationId, $('#designation-master-modalSave'));
            $('#designationId').val(designationId > 0 ? designationId : 0);
            
            $("#designation-master-modalSave").off("click").on("click", function(e) {
                e.preventDefault();
                $('.designationNameReq').addClass(displayNone);
                $('.designationNameExist').addClass(displayNone);
                $('.designationNameInvalid').addClass(displayNone);  
                $('#designationName').removeClass('is-invalid is-valid');
                
                let errorCount = 0;
                var designationNameField = $('#designationName').val().trim();
                
                if (designationNameField === '') {
                    $('.designationNameReq').removeClass(displayNone).addClass('d-block');
                    $('#designationName').addClass(errorClass);
                    errorCount++;
                } else {
                    if (!isAlphabeticWithSpaces(designationNameField)) {
                        $('#designationName').addClass(errorClass);
                        $('.designationNameInvalid').removeClass(displayNone).addClass('d-block'); 
                        errorCount++;
                    } else if (!checkDesignationNameExist()) {
                        $('#designationName').addClass(errorClass);
                        $('.designationNameExist').removeClass(displayNone).addClass('d-block'); 
                        errorCount++;
                    }
                }
                
                if (errorCount === 0) {
                    $("#designation-master-modalSave").attr('disabled', true);
                    $('#designationMasterForm').submit();
                } else {
                    $("#designation-master-modalSave").attr('disabled', false);
                }
            });
            
            $('#designationName').on('input', function() {
                let designationNameVal = $(this).val().trim();
                if (designationNameVal === '') {
                    $('.designationNameReq').removeClass(displayNone).addClass('d-block');
                    $('.designationNameExist').addClass(displayNone); 
                    $('.designationNameInvalid').addClass(displayNone); 
                    $(this).removeClass(validClass).addClass(errorClass);
                } else {
                    $('.designationNameReq').addClass(displayNone);
                    $(this).removeClass(errorClass);
                }
            });
        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}


// Delete designation master
function deleteDesignation(element) {
	const designationId = element !== null ? element.getAttribute("data-designation-id") : 0;
	$('#deleteModal').modal('show');
   $('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
		   url: contextPath + baseURL + "/" + designationId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
                    const table = $('#designationListDataTable').DataTable();
                    const row = $('#deletedes_' + designationId).parents('tr');
                    table.row(row).remove().draw(); 
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






// check designation name exist
function checkDesignationNameExist() {
	var designationName = $('#designationName');
	var designationId = $('#designationId');
	var designationNameVal = designationName.val().trim();
	var designationIdVal = designationId.val().trim();
	let isValid = false;
	if (designationNameVal !== '') {
		var validateUrl = contextPath + baseURL + checkDetailsExistURL + "/" + designationNameVal + "/" + designationIdVal;

		//var validateUrl = contextPath + "/checkDesignationNameExist/" + designationNameVal + "/" + designationIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,  // This is required for synchronous validation
			success: function(response) {
				if (response == false || response == 'false') {
					designationName.removeClass(errorClass).addClass(validClass);
					$('.designationNameExist').addClass(displayNone);
					isValid = true;
				} else {
					designationName.removeClass(validClass).addClass(errorClass);
					$('.designationNameExist').removeClass(displayNone).addClass('d-block');
					$('.designationNameReq').addClass(displayNone);
					isValid = false;
				}
			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		});
	}
	return isValid;
}


// Function to allow only alphabets and spaces
function isAlphabeticWithSpaces(str) {
    const regex = /^[a-zA-Z ]*$/;
    return regex.test(str);
}


