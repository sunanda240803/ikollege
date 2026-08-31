$(document).ready(function() {
	$('#searchAccountHead').on('keyup', function() {
		const searchTerm = $(this).val().toLowerCase();

		if (searchTerm.length > 0) {
			$('#searchAllCheck').hide();
		} else {
			$('#searchAllCheck').show();
		}

		$('.searchAccHead .form-check').each(function() {
			const label = $(this).find('.form-check-label').text().toLowerCase();

			if (label.indexOf(searchTerm) !== -1) {
				$(this).show();
			} else {
				$(this).hide();
			}
		});
	});
});

document.getElementById('selectAllAccHead').addEventListener('change', function() {
	const isChecked = this.checked;
	const checkboxes = document.querySelectorAll('.accHeadCheckbox');
	checkboxes.forEach(checkbox => {
		checkbox.checked = isChecked;
	});
});

function validateUserName() {
	updateInvalidDivClass();
	const checkboxes = document.querySelectorAll('.accHeadCheckbox');
	const anyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
	const elementTextArray = ['#userName'];
	const isValid = valRequiredTextArray(elementTextArray);
	
	if (isValid) {
		if (!anyChecked) {
			let errorDiv = $('#searchAccountHead').siblings('.invalid-feedback');
			$('#searchAccountHead').removeClass(validClass).addClass(errorClass);
			errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html());
			return false;
		}
		if (!validateUser()) {
			return false;
		}
		return true;
	} else {
		return false;
	}
}

function validateUser() {
	let input = document.getElementById('userName');
	let datalist = document.getElementById('names');
	let options = datalist.querySelectorAll('option');
	let valid = Array.from(options).some(option => option.value === input.value);
	let errorDiv1 = $('#userName').siblings('.invalid-feedback');
	
	if (!valid) {
		$('#userName').removeClass(validClass).addClass(errorClass);
		errorDiv1.html(errorDiv1.siblings('.invalid-user-feedback').html());
		return false;
	} else {
		$('#userName').removeClass(errorClass).addClass(validClass);
		return true;
	}
}

function deleteMapping(element) {
	const accHead = element !== null ? element.getAttribute("data-acc-head") : null;
	const catererName = element !== null ? element.getAttribute("data-caterer-name") : 0;
	const id = element !== null ? element.getAttribute("id") : null;

	$('#deleteModal').modal('show');
	$('#dynamicId').val(id);

	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				'Cache-Control': 'no-cache'
			},
			cache: 'no-cache',
			body: JSON.stringify({
				"accHead": accHead,
				"catererName": catererName
			})
		}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				$('#' + id).parents('tr').remove();
				showToast('Success', 'Caterer user mapping deleted successfully');
			} else {
				showToast('Failure', response.message);
			}
		});
		$('#deleteModalPositive').off('click');
	})
}