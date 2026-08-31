// Select All Functionality
$(window).on('load', function() {
	$('#addNewId').addClass('d-none');
});

document.getElementById('selectAllHostels').addEventListener('change', function() {
	const isChecked = this.checked;
	const checkboxes = document.querySelectorAll('.hostelCheckbox');
	checkboxes.forEach(checkbox => {
		checkbox.checked = isChecked;
	});
});

// Validate at least one checkbox is selected
function validateHostelSelection() {
	const checkboxes = document.querySelectorAll('.hostelCheckbox');
	const anyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
	const elementTextArray = ['#userName'];
	const isValid = valRequiredTextArray(elementTextArray);
	if (isValid) {

		if (!anyChecked) {
			$('#validateError').html('Please select at least one hostel.');
			$('#searchHostelName').addClass('is-invalid');
			$('#searchHostelName').focus();
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

function validateUser(){
	
	let input = document.getElementById('userName');
		let datalist = document.getElementById('names');
		let options = datalist.querySelectorAll('option');
		let valid = Array.from(options).some(option => option.value === input.value);
		
		if (!valid) {
			$('#validateError').html('Enter a valid username');
			$('#userName').addClass('is-invalid').removeClass('is-valid');
			return false;
		}else {
			$('#userName').addClass('is-valid').removeClass('is-invalid');
			return true;
		}
		return true;
}

$(document).ready(function() {
	// Filter hostels based on search input
	$('#searchHostelName').on('keyup', function() {
		const searchTerm = $(this).val().toLowerCase();

		if (searchTerm.length > 0) {
			// Hide the "Select All Hostels" section when searching
			$('#searchAllCheck').hide();
		} else {
			// Show the "Select All Hostels" section when search is cleared
			$('#searchAllCheck').show();
		}

		$('.searchHostel .form-check').each(function() {
			const label = $(this).find('.form-check-label').text().toLowerCase();

			if (label.indexOf(searchTerm) !== -1) {
				$(this).show();  // Show matched hostel
			} else {
				$(this).hide();  // Hide unmatched hostel
			}
		});
	});
});

// Example starter JavaScript for disabling form submissions if there are invalid fields
(function() {
	'use strict'

	// Fetch all the forms we want to apply custom Bootstrap validation styles to
	var forms = document.querySelectorAll('.needs-validation')

	// Loop over them and prevent submission
	Array.prototype.slice.call(forms)
		.forEach(function(form) {
			form.addEventListener('submit', function(event) {
				if (!form.checkValidity()) {
					event.preventDefault()
					event.stopPropagation()
				}

				form.classList.add('was-validated')
			}, false)
		})
})()




function deleteMapping(element) {
	const userName = element !== null ? element.getAttribute("data-user-id") : null;
	const hostelId = element !== null ? element.getAttribute("data-hostel-id") : 0;
	const id = element !== null ? element.getAttribute("id") : null;

	$('#deleteModal').modal('show');
	$('#dynamicId').val(id);

	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + deleteURL, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				'Cache-Control': 'no-cache'
			},
			cache: 'no-cache',
			body: JSON.stringify({
				"hostelId": hostelId,
				"userName": userName
			})
		}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				$('#' + id).parents('tr').remove();
				showToast('Success', 'Hostel user mapping deleted successfully');
			} else {
				showToast('Failure', response.message);
			}
		});
		$('#deleteModalPositive').off('click');
	})
}