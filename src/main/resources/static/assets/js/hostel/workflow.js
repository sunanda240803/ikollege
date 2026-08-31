$(document).ready(function() {
	$('#addNewId').removeAttr('disabled');

	if (modalError === true) {
		editWorkflowMaster(null);
	}

	$('#addNewId, #editButton').click(function() {
		editWorkflowMaster(null);
	});

	$('#deleteButton').click(function() {
		deleteWorkflowMaster(this);
	});
});
function editWorkflowMaster(element) {
	const workflowId = element !== null ? element.getAttribute("data-workflow-master-id") : 0;
	fetch(contextPath + baseURL + getURL + "/" + workflowId
		, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
		return response.text();
	}).then(response => {
		$('#modalDiv').html(response);
		updateInvalidDivClass();
		$('#hostel-workflow-model').modal('show');
		updateSaveButtonStyle(workflowId, $('#hostel-workflow-modelSave'));
		$("#hostel-workflow-modelSave").off("click").on("click", function (e) {
			e.preventDefault();
			updateInvalidDivClass();
			const saveButton = $(this);
			const requiredFields = $('input[required], select[required], input[required]');
			let status = valRequiredMultiTextRadio(requiredFields);
			let emailElement = $('#email');
			let emailStatus = true;
			let emailValue = emailElement.val();
			let isMandatory = emailValue.length >= 1;
			if (isMandatory) {
				emailStatus = validateEmailMsg(emailElement[0], isMandatory);
			}else {
				emailElement.removeClass(errorClass).removeClass(validClass);
			}
			if (status && emailStatus) {
				saveButton.attr('disabled', true);
				$('#hostel-workflow-form').submit();
			} else {
				return false;
			}
		});
		$("#hostel-workflow-modelValidateBackend").off("click").on("click", function (e) {
			e.preventDefault();
			$('#hostel-workflow-form').submit();
		})
	});
}

function deleteWorkflowMaster(element) {
	const workflowId = element !== null ? element.getAttribute("data-workflow-master-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function () {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + deleteURL + "/" + workflowId
			, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				$('#deleteEvent_' + workflowId).parents('tr').remove();
				showToast('Success', 'Hostel Workflow Master deleted successfully');
			} else {
				showToast('Failure', response.message);
			}
		});
		$('#deleteModalPositive').off('click');
	})
}