$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editTerminalInfo(null);
});

function editTerminalInfo(element) {
	const terminalId = element !== null ? element.getAttribute("data-terminal-id") : 0;
	fetch(contextPath + baseURL + "/" + terminalId
		, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			return response.text();
		}).then(response => {
			$('#modalDiv').html(response);
			$('#mess-terminal-modal').modal('show');
			updateSaveButtonStyle(terminalId, $('#mess-terminal-modalSave'));
			$('#terminalId').val(terminalId > 0 ? terminalId : 0);
			$('.selectpicker').selectpicker('refresh');
//let ipMaskElementArray = ['#terminalIp'];
			//inputMask(ipMaskElementArray, IPAddressMask);
			$("#mess-terminal-modalSave").on("click", function(e) {
				e.preventDefault();

				var facitilityMaster = $('#messId');

				if (!valRequiredSelect(facitilityMaster)) {
					// facitilityMaster.selectpicker('setStyle', errorClass, 'add');
					facitilityMaster.parent().find('.dropdown-toggle').addClass(errorClass);

				} else {
					facitilityMaster.selectpicker('setStyle', errorClass, 'remove');
					facitilityMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
					$('#messInputVal').addClass(displayNone);
					$('#messInputVal').removeClass('d-block');
				}
				const isValid = valRequiredMultiTextRadio('#messId,#terminalIp,#macId,#terminalDescription, input[name="issuedBy"]');
				if (isValid) {



					//					//Check Mess Name check Ajax	
					//					if (!checkMessNameExist()) {
					//						return;
					//					}

					$("#mess-terminal-modalSave").attr('disabled', false);
					$('#messTerminalForm').submit();

				}
				else {
					$("#mess-terminal-modalSave").attr('disabled', false);
					errorCount = 0;
					return false;
				}

			});
		});
}


function checkMessNameExist() {
	var messName = $('#messName');
	var messId = $('#messId');
	var messNameVal = messName.val().trim();
	var messIdVal = messId.val();
	let isValid = false;
	if (messName != '') {
		var validateUrl = contextPath + baseURL + checkMessNameExistURL + "/" + messNameVal + "/" + messIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					messName.removeClass(errorClass).addClass(validClass);
					$('.nameExist').addClass(displayNone);
					isValid = true;
				} else {
					messName.removeClass(validClass).addClass(errorClass);
					$('.nameExist').removeClass(displayNone);
					$('.nameExist').addClass('d-block');
					$('.nameReq').addClass(displayNone);
					$("#mess-terminal-modalSave").attr('disabled', false);
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

function deleteTerminalInfo(element) {
	const terminalId = element !== null ? element.getAttribute("data-terminal-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + terminalId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + terminalId).parents('tr').remove();
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