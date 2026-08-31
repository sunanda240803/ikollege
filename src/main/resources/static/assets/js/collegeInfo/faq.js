$(window).on('load', function() {
	var addNewId = $('#addNewId');
	addNewId.removeAttr('disabled');
	addNewId.click(function() {
		editFaqInfo(null);
	});
});

function editFaqInfo(element) {
	const faqId = element !== null ? element.getAttribute("data-faq-id") : 0;
	fetch(contextPath + baseURL + "/" + faqId
		, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			return response.text();
		}).then(response => {
			$('#modalDiv').html(response);
			$('#faq-modal').modal('show');
			updateSaveButtonStyle(faqId, $('#faq-modalSave'));
			$('#faqId').val(faqId > 0 ? faqId : 0);

			$('#faq-modalSave').off("click").on("click", function(e) {
				const requiredFields = $('textarea[required]');
				let result = validateRadioAndText(requiredFields);
				if (result) {
					$('#faqForm').submit();
				} else {
					$("#faq-modalSave").attr('disabled', false);
					return false;
				}
			});
		});
}

function deleteFaqInfo(element) {
	const faqId = element !== null ? element.getAttribute("data-faq-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + faqId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
//					const table = $('#faqTable').DataTable();
					 $('#deleteReq_' + faqId).parents('tr').remove();
//					table.row(row).remove().draw();
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