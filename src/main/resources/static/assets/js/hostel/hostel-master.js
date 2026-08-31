

$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editHostel(null);
});



function editHostel(element) {
	const hostelId = element !== null ? element.getAttribute("data-hostel-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + hostelId,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#hostel-master-modal').modal('show');
			updateSaveButtonStyle(hostelId,$('#hostel-master-modalSave'));
			$('#hostelId').val(hostelId > 0 ? hostelId : 0);

			$("#hostel-master-modalSave").on("click", function(e) {
				e.preventDefault();
				let errorCount = 0;
				var isRadioValid = valRequiredMultiTextRadio('input[name="hostelGenderType"]');

				var hostelName = $("#hostelNameId");
				var emailId = $("#hostelOfficeEmailId");
				var shortCode = $("#hostelShortCodeId");

				if (hostelName.val() === '') {
					var count = errorCount;
					errorCount += fieldEmpty(hostelName);
					if (count !== errorCount) {
						$('.nameReq').removeClass(displayNone);
						$('.nameExist').addClass(displayNone);
					}
				}
				if (emailId.val() !== '') {
					var count = errorCount;
					errorCount += validateEmail(emailId, true);
					if (count !== errorCount){
						$('.emailErrReq').addClass(displayNone);
						$('.studentEmailIdPattern').removeClass(displayNone);
					}
				} else {
					errorCount++;
					emailId.removeClass(validClass).addClass(errorClass);
					$('.emailErrReq').removeClass(displayNone);
					$('.studentEmailIdPattern').addClass(displayNone);
				}
				if (shortCode.val() === '') {
					var count = errorCount;
					errorCount += fieldEmpty(shortCode);
					$('.shortCode').addClass(displayNone);
					if (count !== errorCount) {
						$('.shortCodeReq').removeClass(displayNone);
						$('.shortCodeExist').addClass(displayNone);
					}
				}

				if (isRadioValid && (errorCount == 0)) {

					//Check Hostel Name check Ajax	
					if (!checkHostelNameExist()) {
						return;
					}

					//Check Hostel Shor Code check Ajax	
					if (!checkHostelShortCodeExist()) {
						return;
					}

					$("#hostel-master-modalSave").attr('disabled', false);
					$('#hostelMasterForm').submit();

				} else {
					$("#hostel-master-modalSave").attr('disabled', false);
					errorCount = 0;
					return false;
				}
			});
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function checkHostelNameExist() {
	var hostelName = $('#hostelNameId');
	var hostelId = $('#hostelId');
	var hostelNameVal = hostelName.val().trim();
	var hostelIdVal = hostelId.val();
	let isValid = false;
	if (hostelNameVal != '') {
		var validateUrl = contextPath + baseURL + checkHostelNameExistURL + "/" + hostelNameVal + "/" + hostelIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					hostelName.removeClass(errorClass).addClass(validClass);
					$('.nameExist').addClass(displayNone);
					isValid = true;
				} else {
					hostelName.removeClass(validClass).addClass(errorClass);
					$('.nameExist').removeClass(displayNone);
					$('.nameExist').addClass('d-block');
					$('.nameReq').addClass(displayNone);
					$("#hostel-master-modalSave").attr('disabled', false);
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

function checkHostelShortCodeExist() {
	var hostelCode = $('#hostelShortCodeId');
	var hostelId = $('#hostelId');
	var hostelCodeVal = hostelCode.val().trim();
	var hostelIdVal = hostelId.val();
	let isValid = false;
	if (hostelCodeVal != '') {
		var validateUrl = contextPath + baseURL + checkHostelCodeExistURL + "/" + hostelCodeVal + "/" + hostelIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					hostelCode.removeClass(errorClass).addClass(validClass);
					$('.shortCodeExist').addClass(displayNone);
					isValid = true;
				} else {
					hostelCode.removeClass(validClass).addClass(errorClass);
					$('.shortCodeReq').addClass(displayNone);
					$('.shortCodeExist').removeClass(displayNone);
					$("#hostel-master-modalSave").attr('disabled', false);
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


function deleteHostel(element) {
	const hostelId = element !== null ? element.getAttribute("data-hostel-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + hostelId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + hostelId).parents('tr').remove();
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

function updateCheckboxValue(checkbox) {
	checkbox.value = checkbox.checked ? 'Y' : 'N';
}
