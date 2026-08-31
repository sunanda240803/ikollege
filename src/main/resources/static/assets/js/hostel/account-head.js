$(document).ready(function() {
	var addNewId = $('#addNewId');
		addNewId.removeAttr('disabled');
		addNewId.click(function() {
			editAccHead(null);
		});
	
	if (modalError === true) {
		editAccHead(null);
    }
});

$(document).on('change', '#openingBalance', function () {
    var openingBalance = $(this).val(); 
    $('#closingBalance').val(openingBalance); 
});

function editAccHead(element) {
	const acchead = element !== null ? element.getAttribute("data-accHead-id") : 0;
	var btnUpdate = null;
	if(acchead!=0){btnUpdate=acchead};
	$.ajax({
		url: contextPath + baseURL + "/" + acchead,
		type: "GET",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#accountHeadModal').modal('show');
			updateInvalidDivClass();
			updateSaveButtonStyleForStringId(btnUpdate,$('#accountHeadModalSave'));
			$('#accountHead').val(acchead && acchead.length > 0 ? acchead : "");
			
			$("#accountHeadModalSave").on("click", function(e) {
				updateInvalidDivClass();
				const requiredFields = $('input[required],select[required]');
				let result = validateRadioAndText(requiredFields);
				if(acchead==0 && checkAccHeadExist() && result && validateBalanceDate()) {
					$("#accountHeadModalSave").attr('disabled', false);
					$('#accountHeadform').submit();
				}
				else if(result && validateBalanceDate()){
					$("#accountHeadModalSave").attr('disabled', false);
					$('#accountHeadform').submit();
				}else{
					$("#accountHeadModalSave").attr('disabled', false);
					return false;
				}
			});
			$("#accountHeadModalValidateBackend").off("click").on("click", function(e) {
				$('#accountHeadform').submit();
			})
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function deleteAcchead(element) {
	const acchead = element !== null ? element.getAttribute("data-accHead-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + acchead,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$("[id='deleteReq_" + acchead + "']").parents('tr').remove();
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
function validateBalanceDate(){
	var opDate = $('#openingBalanceDate').val();
	var clDate =  $('#closingBalanceDate').val();
	if(clDate!=null && clDate!=""){
		if(opDate > clDate || clDate < opDate){
			$('.balDate').text(messages.checkClosingDate);
			return false;
		}else{
			$('.balDate').text("");
			return true;
		}
	}else{
		$('.balDate').text("");
		return true;
	}
}

function checkAccHeadExist(){
	var accHead = $('#accountHead').val().trim();
	let isValid = false;
	if (accHead != '') {
		var validateUrl = contextPath + baseURL + checkAccHeadNameExistURL + "/" + accHead;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					$(".accHeadExist").addClass(displayNone);
					isValid = true;
				} else {
					$(".accHeadExist").removeClass(displayNone);
					$("#accountHeadModalSave").attr('disabled', false);
					isValid = false;
				}

			},
			error: function(error) {
				showToast('Error', 'Error occurred');
				return false;
			}
		})
	}
	return isValid;
}