$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editMessInfo(null);
});

function editMessInfo(element) {
	const messId = element !== null ? element.getAttribute("data-mess-id") : 0;
	fetch(contextPath + baseURL + "/" + messId
		, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			return response.text();
		}).then(response => {
			$('#modalDiv').html(response);
			$('#mess-master-modal').modal('show');
			const foodCourtAmountInput = $('#foodCourtAmount');
			if (foodCourtAmountInput && foodCourtAmountInput.val()) {
				const val = parseFloat(foodCourtAmountInput.val());
				if (!isNaN(val)) {
					foodCourtAmountInput.val(val.toFixed(2));
				}
			}
			updateSaveButtonStyle(messId, $('#mess-master-modalSave'));
			$('#messId').val(messId > 0 ? messId : 0);

			$("#mess-master-modalSave").on("click", function(e) {
				e.preventDefault();
				const isValid = valRequiredMultiTextRadio('#messName,#floorName,#description,#messHead,#capacity,#genderOption,#messType, input[name="isFoodCourt"],input[name="isJainFood"]');
				const isDateValid = validateDateRange('#fromDate', '#toDate');
				
				if (isValid && isDateValid) {
					//Check Mess Name check Ajax	
					if (!checkMessNameExist()) {
						return;
					}

					//Check Mess Head check Ajax	
					if (!checkMessHeadExist()) {
						return;
					}

					$("#mess-master-modalSave").attr('disabled', false);
					$('#messMasterForm').submit();

				}
				else {
					$("#mess-master-modalSave").attr('disabled', false);
					errorCount = 0;
					return false;
				}

			});
		});
}


function checkMessNameExist() {
	var messName = $('#messName');
	var messId = $('#messId');
	var messIdVal = messId.val();
	let isValid = false;
	
	if (!!messName.val()) {
		var validateUrl = contextPath + baseURL + checkMessNameExistURL + "/" + messName.val().trim() + "/" + messIdVal;
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
					$("#mess-master-modalSave").attr('disabled', false);
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

function checkMessHeadExist() {
	var messHead = $('#messHead');
	var messId = $('#messId');
	var messHeadVal = messHead.val().trim();
	var messIdVal = messId.val();
	let isValid = false;
	if (messHeadVal != '') {
		var validateUrl = contextPath + baseURL + checkMessHeadExistURL + "/" + messHeadVal + "/" + messIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					messHead.removeClass(errorClass).addClass(validClass);
					$('.messHeadExist').addClass(displayNone);
					isValid = true;
				} else {
					messHead.removeClass(validClass).addClass(errorClass);
					$('.messHeadReq').addClass(displayNone);
					$('.messHeadExist').removeClass(displayNone);
					$("#mess-master-modalSave").attr('disabled', false);
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

function deleteMessInfo(element) {
	const messId = element !== null ? element.getAttribute("data-mess-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + messId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + messId).parents('tr').remove();
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

function validateDateRange(fromElement, toElement) {
	let isValid = true;
	const fromDate = new Date($(fromElement).val());
	const toDate = new Date($(toElement).val());

	if (fromDate && toDate && toDate <= fromDate) {
		$(toElement).addClass(errorClass);
		isValid = false;
	} else {
		$(toElement).removeClass(errorClass);
		$(toDate).addClass("is-valid");
		isValid = true;
	}
	return isValid;
}

function calculateVacancyCount(value, index) {
	$('#vacancyCount_' + index).val(value - $('#allocatedCount_' + index).val());
	return true;
}
$('#updateId').click(function (){
$('#messMasterConfig').submit();
});