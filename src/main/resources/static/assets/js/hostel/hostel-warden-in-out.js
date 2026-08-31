$(document).ready(function () {
    $('.modal').modal({
        backdrop: 'static',
        keyboard: false
    });

    function validateForm(formSelector) {
        let isValid = true;
        const form = $(formSelector);
        form.find('.is-invalid').removeClass('is-invalid');

        form.find('input[required]:visible, select[required]:visible, textarea[required]:visible').each(function () {
            if (!$(this).val()) {
                $(this).addClass('is-invalid');
                isValid = false;
            }
        });
        return isValid;
    }

    $("#additionalButton2").on('click', function (e) {
        e.preventDefault();
        editWardenDetails(null);
    });
	
	if (modalError === true) {
		editWardenDetails(null);
    }
	
	$('.selectpicker').selectpicker('refresh');
});

function editWardenDetails(element) {
	const id = element !== null ? element.getAttribute("data-warden-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + id,
		type: "GET",
		success: function(response) {
			$('#hostelWardenInOutForm').remove();
			$('#modalDiv').html(response);
			if(element!==null){
				$("#hostelWardenInOutFormSave").find('span').text('Update');
			}else{
				$("#hostelWardenInOutFormSave").find('span').text('Save');
			}
			
			$("#hostelWardenInOutFormValidateBackend").off("click").on("click", function(e) {
				$('#hostelWardenDetailForm').submit();
			});
			$("#hostelWardenInOutFormSave").off("click").on("click", function(e) {
				updateInvalidDivClass();
				const requiredFields = $('input[required], select[required]');
				const requiredTextArea = $('textarea[required]');
				let isValid = validateRadioAndText(requiredTextArea);
				let status = valRequiredMultiTextRadio(requiredFields);
				if(isValid && status){
					if(validateAwayRequestDate()){
						$('#hostelWardenDetailForm').submit();
					}
				}else{
					$("#accountHeadModalSave").attr('disabled', false);
					return false;
				}
			});
			$('#hostelWardenInOutForm').modal('show');
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function getInchargeDetails(){
	var id = $('#alterHostelName').val();
	if(id!=null && id!=""){
		$.ajax({
	       url: contextPath + baseURL+ inchargeUrl + "/" + id,
	       type: "GET",
	       success: function (response) {
			if(response!=null && response!=""){
				console.log(response);
				   $('#wardenId').val(response.id);
		           $('#inchargeName').val(response.wardenName);
				   $('#inchargeEmail').val(response.wardenEmail);
				   $('#mobilePhone').val(response.phoneNumber);
				   $('#officePhone').val(response.officeNo);
			 }else{
				  updateInchargeDetails();
			 }
	       },
	       error: function () {
	           showToast('Error', "Error Occurred");
	       }
	   });
   }else{
	updateInchargeDetails();
 }
}
function updateInchargeDetails(){
	$('#inchargeId').val('');
   $('#inchargeName').val('');
   $('#inchargeEmail').val('');
   $('#mobilePhone').val('');
   $('#officePhone').val('');
}
function validateAwayRequestDate(){
	var fromDate = $('#awayFromDate').val();
	var toDate =  $('#awayToDate').val();
	if(fromDate!=null && toDate!=""){
		if(fromDate > toDate || toDate < fromDate){
			$('.awayToDate').text(messages.checkAwayDate);
			return false;
		}else{
			$('.awayToDate').text("");
			return true;
		}
	}else{
		$('.awayToDate').text("");
		return true;
	}
}