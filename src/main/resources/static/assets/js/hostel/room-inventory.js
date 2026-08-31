$(document).ready(function() {
	$('#addNewId').removeAttr('disabled');
});
$('#addNewId').click(function() {
	editRoomInventory(null);
});
function getRoomInventoryListValidatee() {
	if (valRequiredMultiSelect('#hostelName, #assetCondition')) return true;
	else return false;
}

function editRoomInventory(element) {
	const id = element !== null ? element.getAttribute("data-category-id") : 0;
	// const id = element.getAttribute("data-category-id");
	$.ajax({
		url: contextPath + baseURL + detailsURL + "/" + id,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#room-inventory-modal').modal('show');
			if(id === 0) $('.assetCondition').addClass('d-none');
			else $('.assetCondition').removeClass('d-none');
			updateSaveButtonStyle($('#id').val(), $('#room-inventory-modalSave'));
			$('.selectpicker').selectpicker('refresh');
			if (id !== 0) {
				$('.addInventory').addClass(displayNone);
				$('.editInventory').removeClass(displayNone);
			} else {
				$('.addInventory').removeClass(displayNone);
				$('.editInventory').addClass(displayNone);
				$('#id').val(0);
			}

			if ($('#hostelIdModal option').length === 1) {
				const hostelSelect = $('#hostelIdModal');
				hostelSelect.on('mousedown', function(e) {
					e.preventDefault();
				});
				hostelSelect.css({
					'pointer-events': 'none',
					'background-color': '#e9ecef',
					'opacity': '1'
				});
				setTimeout(function () {
					getRoomList('hostelIdModal', 'roomId');
				}, 300);
			}

			$("#room-inventory-modalSave").on("click", function(e) {
				e.preventDefault();
				const saveButton = $(this);
				const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
				let multiSelectSelector = '#hostelIdModal, #roomId';
				if ($('.addInventory:visible').length > 0) {
					multiSelectSelector = '#assetCategoryIdModal, ' + multiSelectSelector;
				}
				const isValidMultiSelect = valRequiredMultiSelect(multiSelectSelector);
				let status = valRequiredMultiTextRadio(requiredFields);
				saveButton.attr('disabled', true);
				const form = $('form[id="roomInventoryModalForm"]');
				if (status && isValidMultiSelect) {
					form.submit();
				} else {
					saveButton.attr('disabled', false);
					return false;
				}
			});
		}
	});
}

function deleteRoomInventory(element) {
	const id = element.getAttribute("data-category-id");
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + detailsURL + "/" + id,
			type: "Delete",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + id).parents('tr').remove();
					showToast('Success', 'Room Inventory deleted successfully!');
					setTimeout(function() {
						location.reload();
					}, 2000);
				} else {
					showToast('Failure', response.message);
				}
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}


function getRoomList(hostelId, id) {
	var hostelIdVal = $('#' + hostelId).val();

	if (!hostelIdVal || hostelIdVal === "0") {
		var $select = $('#' + id);
		$select.empty();  // Clear the existing room options
		$select.append('<option value="0">Select Room Number</option>');  // Add default option
		$('.selectpicker').selectpicker('refresh');  // Refresh selectpicker UI
		return;
	}

	fetch(contextPath + baseURL + roomListURL + "/" + hostelIdVal, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	}).then(response => {
		if (!response.ok) {
			return response.json().then(err => {
				throw err;
			});
		}
		return response.json();
	}).then(responseJson => {
		if (Array.isArray(responseJson)) {
			var $select = $('#' + id);
			$select.empty();  // Clear the current options
			$select.append('<option value="0">Select Room Number</option>');

			$.each(responseJson, function(index, room) {
				$select.append('<option value="' + room.id + '">' + room.roomNo + '</option>');
			});

			// Refresh the select picker to apply the changes
			$('.selectpicker').selectpicker('refresh');
		}
	}).catch(err => {
		console.error("Error fetching room list: ", err);
	});
}

function getRoomInventoryListValidate() {
	var isValid = valRequiredMultiSelect('#hostelName, #assetCondition');
	if(isValid){
		let hostelId = $('#hostelName').val(); // Assuming this gets the selected hostel ID
        let assetCondition = $('#assetCondition').val(); // Assuming this gets the selected floor ID

        // Set these values in the additional param inputs
        $('input[name="additionalParam.hostelId"]').val(hostelId);
        $('input[name="additionalParam.assetCondition"]').val(assetCondition);

        // Call createUrlWithParams with the current page and size
        let page = $('#pageVal').val(); 
        let size = $('#sizeVal').val();
        let search= $('#search').val();
        createUrlWithParams(page, size , search);
	}
	else return false;
}