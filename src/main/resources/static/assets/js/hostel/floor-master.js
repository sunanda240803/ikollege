

$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editFloor(null);
});



function editFloor(element) {
	const floorId = element !== null ? element.getAttribute("data-floor-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + floorId,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#floor-master-modal').modal('show');
			updateSaveButtonStyle(floorId,$('#floor-master-modalSave'));
			$('#floorId').val(floorId > 0 ? floorId : 0);
$('.selectpicker').selectpicker('refresh');
			$("#floor-master-modalSave").on("click", function(e) {
				e.preventDefault();
				let errorCount = 0;
				var isSelectValid = valRequiredSelection('#hostelId');
				var floorName = $("#floorNameId");

var facitilityMaster = $('#hostelId');

	if (!valRequiredSelect(facitilityMaster)) {	
		 // facitilityMaster.selectpicker('setStyle', errorClass, 'add');
          facitilityMaster.parent().find('.dropdown-toggle').addClass(errorClass);
        
	} else {
          facitilityMaster.selectpicker('setStyle', errorClass, 'remove');
          facitilityMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
         $('#hostelInputVal').addClass(displayNone);
          $('#hostelInputVal').removeClass('d-block');
        }
				if (floorName.val() === '') {
					var count = errorCount;
					errorCount += fieldEmpty(floorName);
					if (count !== errorCount) {
						$('.floorNameReq').removeClass(displayNone);
						$('.floorNameExist').addClass(displayNone);
					}
				}

				if (isSelectValid && (errorCount == 0)) {

					//Check Floor Name check Ajax	
					if (!checkFloorNameExist()) {
						return;
					}

					$("#floor-master-modalSave").attr('disabled', false);
					$('#floorMasterForm').submit();

				} else {
					$("#floor-master-modalSave").attr('disabled', false);
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

function checkFloorNameExist() {
	var floorName = $('#floorNameId');
	var floorId = $('#floorId');
	var hostelId = $('#hostelId');
	
	var floorNameVal = floorName.val().trim();
	var floorIdVal = floorId.val();
	var hostelIdVal = hostelId.val();
	let isValid = false;
	
	if (floorNameVal != '') {
		var validateUrl = contextPath + baseURL + checkDetailsExistURL + "/" + floorNameVal + "/" + hostelIdVal + "/" + floorIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					floorName.removeClass(errorClass).addClass(validClass);
					$('.floorNameExist').addClass(displayNone);
					isValid = true;
				} else {
					floorName.removeClass(validClass).addClass(errorClass);
					$('.floorNameExist').removeClass(displayNone);
					$('.floorNameExist').addClass('d-block');
					$('.floorNameReq').addClass(displayNone);
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

function deleteFloor(element) {
	const floorId = element !== null ? element.getAttribute("data-floor-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath+ baseURL + "/"+ floorId,
			type: "Delete",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + floorId).parents('tr').remove();
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
