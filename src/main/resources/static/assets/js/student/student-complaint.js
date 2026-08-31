$(document).ready(function() {
	 if (modalError === true) {
        openStudentComplaint(null);
    }

    $('#addNewId').click(function () {
        openStudentComplaint(null);
    });
});



function openStudentComplaint() {
		const id = 0;
	$.ajax({
		url: contextPath + baseURL + "/0",
		type: "GET",
		success: function(response) {
			// Load the modal content into the modalDiv
			$('#modalDiv').html(response);

			// Show the modal
			$('#student-complaint-modal').modal('show');
			updateSaveButtonStyle(id, $('#student-complaint-modalSave'));

			$('.complaintDropdown').addClass('d-none');
			$('.complaint').addClass('d-none');

			const checkedRadio = document.querySelector('input[name="stuComplaintType"]:checked');
			const checkBoxValue = checkedRadio ? checkedRadio.value : null;
			checkComplaintType(checkBoxValue)


			$('#student-complaint-modalSave').off("click").on("click", function(e) {
				e.preventDefault();
				$("#student-complaint-modalSave").attr('disabled', true);
				updateInvalidDivClass();

				// Validate required fields
				const requiredFields = $('input[required], select[required], textarea[required]');
				const status = valRequiredMultiTextRadio(requiredFields);

				if (status) {
					$('#studentComplaintForm').submit();
				} else {
					$('#student-complaint-modalSave').attr('disabled', false);
					return false;
				}

			});

			$("#student-complaint-modalValidateBackend").off("click").on("click", function(e) {
				$('#studentComplaintForm').submit();
			})

		},
		error: function(error) {
			// Handle error scenario
			showToast('Error', 'Error occurred while loading the modal');
		}
	});
}







function checkComplaintType(type) {
    $('#complaintAbout').val('');
    
    if (type === 'hostel' || type === 'mess') {
        $('.complaint').removeClass('d-none'); 
        $('.complaintDropdown').removeClass('d-none'); 

        let hiddenValue = $('#complaintsHidden').val();

        $('#complaintAbout option').each(function() {
            const optionType = $(this).data('type');
            if (optionType === type || $(this).val() === '') {
                $(this).show(); 

                if (hiddenValue && $(this).val() === hiddenValue) {
                    $(this).prop('selected', true); 
                }
            } else {
                $(this).hide(); 
            }
        });
    } else {
        $('.complaintDropdown').addClass('d-none');
        $('.complaint').addClass('d-none');
    }
}

function downloadExcelReport(){
    const fromDate = $('#fromDate').val();
    const toDate = $('#toDate').val();
    let complaintType = $('input[name="complaintType"]:checked').val();
    if (complaintType === undefined) {
        complaintType = '';
    }
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const isFieldsValid = validateRadioAndText(requiredFields);
    if(isFieldsValid) {
        window.location.href =
            `${contextPath}${baseURL}${downloadExcelURL}` +
            `?complaintType=${encodeURIComponent(complaintType)}` +
            `&fromDate=${encodeURIComponent(fromDate)}`+
            `&toDate=${encodeURIComponent(toDate)}`;
    } else {
        showToast('Error', 'Please enter the required details.');
    }
}