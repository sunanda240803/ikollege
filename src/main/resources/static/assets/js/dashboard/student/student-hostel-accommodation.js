$(document).ready(function () {
    $('#cancelModalPositive').removeClass('btn-secondary').addClass('btn-primary');
    $('#addNewId').click(function() {
		window.location.href = contextPath + hostelAccommodationURL + newURL;
    });

	$('#category').change(function() {
		if ($(this).val() === 'other') {
			$('#categoryOthersContainer').removeClass('d-none');
		} else {
			$('#categoryOthersContainer').addClass('d-none').val('');
		}
	});

	$('#saveId').off("click").on("click", function(e) {
		e.preventDefault();
		validateForm();
	});

	$('#applicationType').on('change',function () {
		if ($('#applicationType').val() === 'messRebate'){
			$('.messRebate').removeClass(displayNone);
		} else {
			$('.messRebate').addClass(displayNone);
		}
	})
});

function validateForm() {
	const saveHeader = $('#saveId');
	const saveButton = $('#saveForm');
	const validateBackendBtn = $('#validateBackendBtn');
	const cancelForm = $('#cancelForm');
	validateBackendBtn.attr('disabled', true);
	saveHeader.attr('disabled', true);
	saveButton.attr('disabled', true);
	cancelForm.attr('disabled', true);
	updateInvalidDivClass();
	const requiredFields = $('input[required], select[required], textarea[required]');
	let result = validateRadioAndText(requiredFields);
	if (result) {
		result = validateEmailDomain(requiredFields);
	}
	if (result) {
		const dateValid = dateBetweenAppointmentFromTo($('#appointmentFrom'), $('#appointmentTo'), $('#stayFrom'), $('#stayTo'));
		if (dateValid) {
			let isValid = true;
			const authorityEmail = $('#authorityEmail');
			isValid &= validateDateErrMsg('appointmentTo', 'appointmentFrom');
			isValid &= validateDateErrMsg('stayTo', 'stayFrom');
			isValid &= validateEmailMsg(authorityEmail[0], true);
			if ($('#category').val() === 'other') {
				isValid &= validateRadioAndText('#categoryOthers');
			}
			if (isValid !== 0) {
				validateApprovalDates().then(isValid => {
					if (!isValid) {
						saveHeader.attr('disabled', false);
						saveButton.attr('disabled', false);
						validateBackendBtn.attr('disabled', false);
						cancelForm.attr('disabled', false);
						return false;
					} else {
						$('#hostelAccommodationForm').submit();
					}
				});
			} else {
				saveHeader.attr('disabled', false);
				saveButton.attr('disabled', false);
				validateBackendBtn.attr('disabled', false);
				cancelForm.attr('disabled', false);
				showToast('Error', 'Please fill all the mandatory fields and submit.');
				return false;
			}
		} else {
			saveHeader.attr('disabled', false);
			saveButton.attr('disabled', false);
			validateBackendBtn.attr('disabled', false);
			cancelForm.attr('disabled', false);
			showToast('Error', 'Please fill all the mandatory fields and submit.');
			return false;
		}
	} else {
		saveHeader.attr('disabled', false);
		saveButton.attr('disabled', false);
		validateBackendBtn.attr('disabled', false);
		cancelForm.attr('disabled', false);
		showToast('Error', 'Please fill all the mandatory fields and submit.');
		return false;
	}
}

 function validateBackend(){
	$('#saveId').attr('disabled', true);
	$('#saveForm').attr('disabled', true);
	$('#cancelForm').attr('disabled', true);
	 $(this).attr('disabled', true);
	$('#hostelAccommodationForm').submit();
 }

function dateBetweenAppointmentFromTo(appointmentFrom, appointmentTo, stayFrom, stayTo) {
	let errorDiv1 = stayFrom.siblings('.invalid-feedback');
	let errorDiv2 = stayTo.siblings('.invalid-feedback');
	if (stayFrom.val() < appointmentFrom.val() || stayFrom.val() > appointmentTo.val()) {
		stayFrom.removeClass(validClass).addClass(errorClass);
		errorDiv1.html(errorDiv1.siblings('.invalid-between-feedback').html());
		return false;
	} else if (stayTo.val() < appointmentFrom.val() || stayTo.val() > appointmentTo.val()) {
		stayTo.removeClass(validClass).addClass(errorClass);
		errorDiv2.html(errorDiv2.siblings('.invalid-between-feedback').html());
		return false;
	} else {
		stayFrom.removeClass(errorClass).addClass(validClass);
		stayTo.removeClass(errorClass).addClass(validClass);
		errorDiv1.html('');
		errorDiv2.html('');
		return true;
	}
}

function validateApprovalDates() {
	return new Promise((resolve) => {
		const appointmentFrom = $('#appointmentFrom');
		const appointmentTo = $('#appointmentTo');
		fetch(contextPath + hostelAccommodationURL + checkDetailsExistURL + "?appointmentFrom=" + appointmentFrom.val() +
			"&appointmentTo=" + appointmentTo.val())
			.then(response => response.text())
			.then(response => {
				if (response === 'Approved Date') {
					let appointmentFromErrorDiv = appointmentFrom.siblings('.invalid-feedback');
					appointmentFrom.removeClass(validClass).addClass(errorClass);
					appointmentFromErrorDiv.html(appointmentFromErrorDiv.siblings('.invalid-already-approved-feedback').html());
					resolve(false);
				} else if (response === 'Pending Date') {
					$('#infoModal').modal('show');
					$('#infoModalPositive').off("click").on("click", function(e) {
						e.preventDefault();
						resolve(true);
					});
					$('#infoModalCancel').off("click").on("click", function(e) {
						e.preventDefault();
						resolve(false);
					});
				} else {
					appointmentFrom.removeClass(errorClass).addClass(validClass);
					resolve(true);
				}
			})
			.catch(error => {
				console.error("Fetch error:", error);
				resolve(false);
			});
	});
}

function resendMail(element) {
	const requestId = element !== null ? element.getAttribute("data-accommodation-id") : 0;
	$.ajax({
		url: contextPath + hostelAccommodationURL + resendMailURL + "/" + requestId,
		type: "Get",
		success: function(response) {
			$('#resendEmailModal').modal('show');
			$('#modalMailDiv').html(response);
			$("#resendEmailModalPositive").on("click", function(e) {
				$('#resendEmailModal').modal('hide');
				e.preventDefault();
				$('#hostelAccommodationResendMail').submit();
			});
		}
	});
}

function cancelPopup(element) {
	const id = element !== null ? element.getAttribute("data-accommodation-id") : 0;
	const status = element !== null ? element.getAttribute("data-cancel") : "";
	$.ajax({
		url: contextPath + hostelAccommodationURL + cancelURL + "/" + id + "/" + status,
		type: "Get",
		success: function(response) {
			$('#modalCancelDiv').html(response);
			if (status === 'Cancelled') {
				$('#cancelModal').modal('show');
				$("#cancelModalPositive").on("click", function(e) {
					e.preventDefault();
					$('#cancelRequestForm').submit();
				});
			} else {
				$('#cancel-request-modalSave').find('span').text('Confirm').end().find('i').removeClass().addClass('fa fa-check-circle');
				$('#cancel-request-modalSave').removeClass('btn-tmPrimary').addClass('btn-secondary');  
				$('#cancel-request-modalCancel').removeClass('btn-secondary').addClass('btn-tmSecondary');  
				
				$('#cancel-request-modal').modal('show');
				$("#cancelDescription").val('');
				$("#cancel-request-modalSave").on("click", function(e) {
					e.preventDefault();
					const saveButton = $(this);
					saveButton.attr('disabled', true);
					let isValid = true;
					const requiredFields = $('textarea[required]');
					isValid = validateRadioAndText(requiredFields);
					if (isValid) {
						$('form[id="cancelRequestForm"]').submit();
					} else {
						saveButton.attr('disabled', false);
					}
				});
			}
		}
	});
}

function studentHostelAccommodationView(element) {
    const extensionId = element !== null ? element.getAttribute("data-extension-id") : 0;
    console.log("extensionId :-"+extensionId);
    fetch(contextPath + hostelAccommodationURL + getViewURL + "/" + extensionId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        return response.text();
    }).then(response => {
        console.log(extensionId);
        $('#modalViewDiv').html(response);
        $('#student-hostel-accommodation-view').modal('show');
		$('#student-hostel-accommodation-viewCancel').html(`
			<i class="fa-solid fa-circle-xmark me-1"></i>
			<span>Close</span>
		`);
        $("#student-hostel-accommodation-viewSave").off("click").on("click", function (e) {
            e.preventDefault();
            const requiredFields = $('input[required], select[required], textarea[required]').filter(function () {
                return $(this).is(':visible');
            });
            const isFieldsValid = validateRadioAndText(requiredFields);
            console.log('isFieldsValid :-'+isFieldsValid);
            if (isFieldsValid) {
                $('#appointment-details-form').submit();
            } else {
                return false;
            }
        });
    });
}

function triggerDownload(element) {
    const fileName = element.getAttribute('data-filename');
    const downloadUrl = contextPath + file + downloadURL + '/HOSTEL_ACCOMMODATION/' + fileName;

    fetch(downloadUrl, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw errorData;
                });
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            // Show toast for errors
            if (error.errors && (error.code === 500 || error.code === 400)) {
                showToast(error.status, error.errors);
            } else {
                showToast('Error', 'File Not Found');
            }
        });
}

function getExcelFile(){
	const requiredFields = $('input[required], select[required]');
	const isValid = validateRadioAndText(requiredFields);
	if (isValid) {
		$('input[name="additionalParam.applicationType"]').val($('#applicationType').val() || '');
		$('input[name="additionalParam.validationStatus"]').val($('#validationStatus').val() || '');
		$('input[name="additionalParam.approvalFromDate"]').val($('#approvalFromDate').val() || '');
		$('input[name="additionalParam.approvalToDate"]').val($('#approvalToDate').val() || '');
		$('input[name="additionalParam.submittedFromDate"]').val($('#submittedFromDate').val() || '');
		$('input[name="additionalParam.submittedToDate"]').val($('#submittedToDate').val() || '');
		$('input[name="additionalParam.rebateFrom"]').val($('#rebateFromDate').val() || '');
		$('input[name="additionalParam.rebateTo"]').val($('#rebateToDate').val() || '');

		let url = `${contextPath}${baseURL}${downloadURL}`;
		let additionalParamsString = $('.additional-param');
		let queryString = '';
		additionalParamsString.each(function() {
			let paramName = $(this).attr('name');
			let paramValue = $(this).val();
			if (paramName && paramValue !== undefined) {
				if (queryString.length > 0) queryString += '&';
				queryString += `${paramName}=${encodeURIComponent(paramValue)}`;
			}
		});
		if (queryString) {
			url += `?${queryString}`;
		}
		window.location.href = url;
	} else {
		showToast('Error', 'Please fill all mandatory fields.');
		return false;
	}
}