$(window).on('load', function() {
	var addNewId = $('#addNewId');
	addNewId.removeAttr('disabled');
	addNewId.click(function() {
		editGuestAccommodationCharge(null);
	});
});


function editGuestAccommodationCharge(element) {
	const guestId = element !== null ? element.getAttribute("data-guest-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + guestId,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#guest-accomm-charge-modal').modal('show');
			updateSaveButtonStyle(guestId, $('#guest-accomm-charge-modalSave'));
			$("#guest-accomm-charge-modalSave").off("click").on("click", function(e) {
				const requiredFields = $('input[required], textarea[required]');
				const status = valRequiredMultiTextRadio(requiredFields);
				if (status) {
					 if (validateFromAndToDate('fromDate', 'toDate')) {
					$('#guestAccommodationform').submit();
					}else{return false;}
				} else {
					$("#guest-accomm-charge-modalSave").attr('disabled', false);
					return false;
				}
			});
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

