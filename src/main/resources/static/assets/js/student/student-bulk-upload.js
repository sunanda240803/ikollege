$(document).ready(function() {
	$("#updateId i").attr("class", "fa-solid fa-file-excel me-1");
	$("#updateId span").text("All Student Details Report");
	$('#updateId').removeClass('btn-success').addClass('btn-pink');
	$('#updateId').click(function() {
		window.open(contextPath + baseURL + downloadURL, '_blank');
	});

	$('#addNewId').click(function() {
		addNewStudent();
	});

});

function validateFile() {
	$('.errorList').html('');
	const fileInput = document.getElementById('uploadFile');
	const filePath = fileInput.value;
	const allowedExtensions = /(\.xlsx)$/i;
	if (filePath.trim() !== '') {
		if (!allowedExtensions.exec(filePath)) {
			fileInput.value = '';
			$('.selectFile2').addClass('d-none');
			$('.validFile').removeClass('d-none');
			return false;
		}
		$('#studentBulkUploadForm').submit();
		return true;
	} else {
		$('.selectFile2').removeClass('d-none');
		return false;
	}
}

function addNewStudent(){
    fetch(contextPath + baseURL + addUrl)
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#add-new-student-modal'));
            modal.show();
             $('#add-new-student-modalSave').on('click', function (e) {
                    validateAndSubmit(); // Call validate and submit function
             });
        })
        .catch(error => {
            console.error('Error:', errorLoadingModal);
        });
}

  // Form submission validation and trigger
  function validateAndSubmit() {
        const requiredFields = $('input[required], select[required], textarea[required]');
        const isValid = validateRadioAndText(requiredFields);
        if (isValid) {
             submitForm();
        } else {
            return false;
        }
  }

  function submitForm() {
	showModalLoader();
    const form = $('#add-new-student-form')[0]; // Get the raw DOM element

    const formData = new FormData(form);
    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
    .then(data => {
        console.log(data);
        if(data.errorList?.length > 0) {
            displayErrorsInModal(data.errorList);
        } else {
            closeAddModal();
            showToast('Success', savedSuccess);
        }
    })
    .catch(error => {
        showToast('Error', savedError);
    })
	.finally(() => {
		hideModalLoader(); 
	});
  }

// Function to display backend validation errors
  function displayErrorsInModal(errors) {
    $('.invalid-feedback').remove();
    errors.forEach(function (error) {
      const [field, message] = error.split('-');  // Extract field and message
      if(field != null) {
          const fieldElement = $('#' + field);
            if (fieldElement.length) {
              const errorElement = $('<div>').addClass('invalid-feedback').text(message);
              fieldElement.closest('.mb-3').append(errorElement);
              fieldElement.addClass('is-invalid');
            }
       }
    });
  }

  function closeAddModal() {
      const modalElement = document.getElementById('add-new-student-modal');
      const closeButton = modalElement.querySelector('.btn-close');
      if (closeButton) {
          closeButton.click(); // Programmatically trigger the close action
      }
  }
