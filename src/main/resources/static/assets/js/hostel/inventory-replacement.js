function replacedOrRepaired(element){
	const id = element !== null ? element.getAttribute("data-category-id") : 0;
	$('#warningModal').modal('show');
	$('#warningModalPositive').click(function () {
		$('#warningModal').modal('hide');
		fetch(contextPath + baseURL + replacedOrRepairedURL + "/" + id
			, {
				method: 'PUT',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				$('#replacedOrRepairedStatus_' + id).parents('tr').remove();
				showToast('Success', 'Room Inventory Replaced / Repaired status updated successfully.');
				setTimeout(function() {
					location.reload();
				}, 1500);
			} else {
				showToast('Failure', response.message);
			}
		});
		$('#warningModalPositive').off('click');
	})
}

function getRoomInventoryListValidate() {
	const isValid = valRequiredMultiSelect('#hostelName');
	if(isValid){
		let hostelId = $('#hostelName').val();
		$('input[name="additionalParam.hostelId"]').val(hostelId);
		let page = $('#pageVal').val();
		let size = $('#sizeVal').val();
		let search= $('#search').val();
		createUrlWithParams(page, size , search);
	}
	else return false;
}