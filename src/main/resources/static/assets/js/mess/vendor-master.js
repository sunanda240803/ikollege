$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editVendorInfo(null);
});



function editVendorInfo(element) {
	const vendorCode = element !== null ? element.getAttribute("data-vendor-code") : null;
	fetch(contextPath + baseURL + getURL + "/" + vendorCode
		, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			return response.text();
		}).then(response => {
			$('#modalDiv').html(response);
			$('#vendor-master-modal').modal('show');
			updateSaveButtonStyleForStringId(vendorCode, $('#vendor-master-modalSave'));

			vendorCode == null ? $('#mobileNoMask').val('') : "";
			vendorCode != null ? $('#vendorCode').attr("readonly", true) : $('#vendorCode').attr("readonly", false);
			let phNumMaskElementArray = ['#mobileNoMask'];
			inputMask(phNumMaskElementArray,mobileNumberMask);
			$("#vendor-master-modalSave").on("click", function(e) {
				e.preventDefault();
				const isValid = valRequiredMultiTextRadio('#vendorCode,#vendorName,#mobileNoMask,#pincode,#address1,#email,#contactPerson,#licenseNo,#licenseValidUpto,#panNo,input[name="isCaterer"]');
				// console.log("Validating");
				if (isValid) {
					if ($('#mobileNoMask').inputmask("unmaskedvalue").length !== 10) {
						$('#mobileNoMask').addClass(errorClass);
						$('#mobileNumVal').html('Mobile number must be 10 digits long');
						return false;
					} else {
						$('#mobileNoMask').removeClass(errorClass);
						$('#mobileNumVal').html(''); // Clear the error message if valid
					}
					$('.emailReq').addClass('d-none');
					if (validateEmail($("#email"), true) != 0) {
						$('#email').addClass(errorClass);
						$('.emailIdPattern').addClass('d-none');
						$('.emailIdPattern').removeClass('d-none');
						console.log("Validating");
						return false;
					}
					console.log("Validating");
					if (!!!$('#id').val()) {
						fetch(contextPath + baseURL + checkDetailsExistURL + "/" + $('#vendorCode').val()
							, {
								method: 'GET',
								headers: {
									'Content-Type': 'application/json'
								}
							}).then(response => {
								return response.text();
							}).then(response => {
								const resData = response;
								if (resData == false || resData == 'false') {
									$("#vendor-master-modalSave").attr('disabled', true);
									$('#vendorMasterForm').submit();
									isValid = true;
								} else {
									$("#vendor-master-modalSave").attr('disabled', false);
									showToast('Error', 'Vendor code already exist');
									isValid = false;
								}
								return isValid;
							});
					} else {
						$('#vendorMasterForm').submit();
					}


				}else{
					if ($('#mobileNoMask').inputmask("unmaskedvalue").length !== 10) {
						$('#mobileNoMask').addClass(errorClass);
						$('#mobileNumVal').html('Mobile number must be 10 digits long');
					} else {
						$('#mobileNoMask').removeClass(errorClass);
						$('#mobileNumVal').html(''); // Clear the error message if valid
					}

					// Validate Email
					const validateEmail = (email) => {
						return email.match(emailPattern);
					};
					const emailField = $("#email");
					const emailValue = emailField.val();
					if (!validateEmail(emailValue)) {
						emailField.addClass(errorClass).removeClass(validClass);
					}else{
						emailField.addClass(validClass).removeClass(errorClass);
					}

					// Validate Pan
					const validatePan = (panNo) => {
						return panNo.match(panPattern);
					};
					const panField = $("#panNo");
					const panValue = panField.val();
					if (!validatePan(panValue)) {
						panField.addClass(errorClass).removeClass(validClass);
					}else{
						panField.addClass(validClass).removeClass(errorClass);
					}
				}
				return false;

			});
		});
}


function deleteVendorInfo(element) {
	const vendorCode = element !== null ? element.getAttribute("data-vendor-code") : 0;
	$('#deleteModal').modal('show');
	$('#dynamicId').val(vendorCode);
	$('#deleteModalPositive').click(function() {
		$('#deleteModal').modal('hide');
		const vendorId = $('#dynamicId').val();
		fetch(contextPath + baseURL + deleteURL + "/" + vendorId
			, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
				return response.json();
			}).then(response => {
				
				if (response.status === 'Success') {
					$('#deleteReq_' + vendorId).parents('tr').remove();
					showToast('Success', 'Vendor Details deleted successfully');
				
				} else {
					showToast('Failure', response.message);
				}
			});

$('#deleteModalPositive').off('click');
	})
}



