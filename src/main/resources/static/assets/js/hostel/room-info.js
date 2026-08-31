$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editRoomInfo(null);
});


function editRoomInfo(element) {
	const roomInfoId = element !== null ? element.getAttribute("data-room-info-id") : 0;
	const hostelId = !!$('#hostelOrFacilityMaster').val() ? $('#hostelOrFacilityMaster').val() : 0;

	fetch(contextPath + baseURL + getURL + "/" + roomInfoId + "/" + hostelId, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}).then(response => {
		return response.text();
	}).then(response => {
		$('#modalDiv').html(response);
		$('#room-info-modal').modal('show');
		updateSaveButtonStyle(roomInfoId, $('#room-info-modalSave'));
		$('#hostelOrFacilityMasterInput').val($('#hostelOrFacilityMaster').val());
		$('#floorNameInput').val($('#floorName').val());
		$('#vacationRoomCapacity').attr('readonly', !$('#vacation').is(':checked'));
		$('.selectpicker').selectpicker('refresh');
		$("#room-info-modalSave").on("click", function (e) {
			e.preventDefault();
			const isValid = valRequiredMultiTextRadio('#roomNumber,#capacity');
			const isSelValid = valRequiredSelection('#hostelOrFacilityMasterInput,#floorNameInput');
			var facitilityMaster = $('#hostelOrFacilityMasterInput');

			if (!valRequiredSelect(facitilityMaster)) {
				// facitilityMaster.selectpicker('setStyle', errorClass, 'add');
				facitilityMaster.parent().find('.dropdown-toggle').addClass(errorClass);

			} else {
				facitilityMaster.selectpicker('setStyle', errorClass, 'remove');
				facitilityMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
				$('#hostelInputVal').addClass('d-none');
				$('#hostelInputVal').removeClass('d-block');
			}

			let isvacationCapReq = true;
			if (document.getElementById('vacation').checked) {
				isvacationCapReq = valRequiredTextArray(['#vacationRoomCapacity']);
			} else {
				$('#vacationRoomCapacity').removeClass("is-invalid");
			}
			if (isValid && isSelValid && isvacationCapReq) {

				var hostelId = $('#hostelOrFacilityMasterInput').val();
				var floorId = $('#floorNameInput').val();
				var roomNo = $('#roomNumber').val().trim();
				var id = $('#id').val();
				let isValid = true;
				if (roomNo != '') {
					fetch(contextPath + baseURL + checkDetailsExistURL, {
						method: 'POST',
						headers: {
							'Content-Type': 'application/json'
						},
						body: JSON.stringify({
							"hostelId": hostelId,
							"floorId": floorId,
							"roomNo": roomNo,
							"id": !!id ? id : 0
						})
					}).then(response => {
						return response.text();
					}).then(response => {
						const resData = response;
						if (resData == false || resData == 'false') {
							$("#room-info-modalSave").attr('disabled', true);
							$('#roomInfoForm').submit();
							isValid = true;
						} else {
							$("#hostel-master-modalSave").attr('disabled', false);
							showToast('Error', 'Hostel room number already exist');
							isValid = false;
						}
						return isValid;

					})
				}
			}
		});

		$('#vacation').click(function () {
			$('#vacationRoomCapacity').attr('readonly', !$('#vacation').is(':checked'));
			if (!$('#vacation').is(':checked')) {
				$('#vacationRoomCapacity').val('');
			}
		});
	})
}

function deleteRoomInfo(element) {
	const roomInfoId = element !== null ? element.getAttribute("data-room-info-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + deleteURL + "/" + roomInfoId, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				deleteTableRow('deleteReq_' + roomInfoId);
				showToast('Success', 'Hostel room config deleted successfully.');
			} else {
				showToast('Failure', response.message);
			}

		});
		$('#deleteModalPositive').off('click');
	})
}


function getHostelFloorList(hostelInputId, id) {
	var hostelId = $('#' + hostelInputId).val();
	fetch(contextPath + baseURL + getFloorURL + "/" + hostelId, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}).then(response => {
		if (!response.ok) {
			return response.json().then(err => {
				throw err;
			}); // Handle errors
		}
		return response.json();
	}).then(responseJson => {
		if (Array.isArray(responseJson)) {
			var $select = $('#' + id);
			$select.empty();
			$select.append('<option value="">Select the Floor Name</option>');

			$.each(responseJson, function(index, floor) {
				$select.append('<option value="' + floor.id + '">' + floor.floorName + '</option>');
			});
		}

	});
}

function validateFile() {
	var fileInput = document.getElementById('file');
	var filePath = fileInput.value;

	var allowedExtensions = /(\.xlsx)$/i;
	if (filePath.trim() === '') {
		$('.selectFile2').removeClass('d-none');
		return false;
	}
	if (!allowedExtensions.exec(filePath)) {
		fileInput.value = '';
		$('.selectFile2').addClass('d-none');
		$('.validFile').removeClass('d-none');
		return false;
	}
	$('#bulkUploadForm').submit();
	return true;
}

/*function validateSearchFeild() {
	var facitilityMaster = $('#hostelOrFacilityMaster');
	if (!valRequiredSelect(facitilityMaster)) {	
		 facitilityMaster.selectpicker('setStyle', errorClass, 'add');
          facitilityMaster.parent().find('.dropdown-toggle').addClass(errorClass);        
	} else {
          facitilityMaster.selectpicker('setStyle', errorClass, 'remove');
          facitilityMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
         $('#hostelVal').addClass('d-none');
          $('#hostelVal').removeClass('d-block');
        }
	if(valRequiredSelect('#floorName')){
		return true;
	}
		return false;
}*/

function validateSearchFeild() {
    var facilityMaster = $('#hostelOrFacilityMaster');
    var isValid = true;

    // Validate hostelOrFacilityMaster
    if (!valRequiredSelect(facilityMaster)) {
        facilityMaster.selectpicker('setStyle', errorClass, 'add');
        facilityMaster.parent().find('.dropdown-toggle').addClass(errorClass);        
        isValid = false;
    } else {
        facilityMaster.selectpicker('setStyle', errorClass, 'remove');
        facilityMaster.parent().find('.dropdown-toggle').removeClass(errorClass);
        $('#hostelVal').addClass('d-none');
        $('#hostelVal').removeClass('d-block');
    }

    // Validate floorName
    if (!valRequiredSelect('#floorName')) {
        isValid = false;
    }

    // If all validations pass, set values and call createUrlWithParams
    if (isValid) {
        let hostelId = facilityMaster.val(); // Assuming this gets the selected hostel ID
        let floorId = $('#floorName').val(); // Assuming this gets the selected floor ID

        // Set these values in the additional param inputs
        $('input[name="additionalParam.hostelId"]').val(hostelId);
        $('input[name="additionalParam.floorId"]').val(floorId);

        // Call createUrlWithParams with the current page and size
        let page = $('#pageVal').val(); 
        let size = $('#sizeVal').val();
        let search= $('#search').val();
        createUrlWithParams(page, size , search);
    }

    return isValid;
}


