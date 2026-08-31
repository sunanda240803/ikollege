$(document).ready(function() {
	$('#addNewId').removeAttr('disabled');

	$('#addNewId').click(function() {
		editCategory(null);
	});
});

function editCategory(element) {
	const id = element !== null ? element.getAttribute("data-category-id") : 0;
	const type = element !== null ? element.getAttribute("data-category-type") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + type + "/" + id,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#asset-configuration-modal').modal('show');
			updateSaveButtonStyle(id, $('#asset-configuration-modalSave'));
			$('#categoryId').val(id > 0 ? id : 0);

			if (id > 0) {
				$('#categoryType').attr('disabled', true);
			}

			// Attach the change event handler after loading content
			attachCategoryTypeChangeHandler();

			// Trigger the change event to ensure the correct state is set on load
			$('#categoryType').trigger('change');

			$("#asset-configuration-modalSave").on("click", function(e) {
				e.preventDefault();
				$(this).attr('disabled', true);
				const maxDigits = 10;
				const errorClass = "is-invalid";
				const validClass = "is-valid";
				let errorCount = 0;
				let categoryType = $("#categoryType");
				let categoryName = $("#categoryName");
				let assetCategoryShortcode = $('#assetCategoryShortcode');
				let minorRepairCost = $('#minorRepairCost');
				let majorRepairCost = $('#majorRepairCost');
				let replacementCost = $('#replacementCost');
				let assetCategoryDescription = $('#assetCategoryDescription');
				let id = $('#categoryId').val();

				$('.form-control').removeClass(errorClass).removeClass(validClass);
				$('.form-select').removeClass(errorClass).removeClass(validClass);

				let isSelectValid = valRequiredSelection(categoryType);

				if (categoryName.val() === '') {
					$('.errMsg').addClass('d-none');
					if (categoryType.val() === 'Category') {
						categoryName.addClass(errorClass);
						$('.assetCategoryReq').removeClass('d-none');
					} else if (categoryType.val() === 'Maintenance') {
						categoryName.addClass(errorClass);
						$('.maintenanceTypeReq').removeClass('d-none');
					}
					errorCount++;
				}

				if (categoryType.val() === 'Category') {
					if (assetCategoryShortcode.val() === '') {
						errorCount++;
						assetCategoryShortcode.addClass(errorClass);
						$('.shortcodeErrMsg').addClass('d-none');
						$('.shortcodeReq').removeClass('d-none');
					}
					if (minorRepairCost.val() !== '') {
						var count = errorCount;
						errorCount += validateDecimalLength(minorRepairCost, maxDigits);
						if (count !== errorCount) minorRepairCost.addClass(errorClass);
						else minorRepairCost.removeClass(errorClass).addClass(validClass);
					} else minorRepairCost.removeClass(errorClass);
					if (majorRepairCost.val() !== '') {
						var count = errorCount;
						errorCount += validateDecimalLength(majorRepairCost, maxDigits);
						if (count !== errorCount) majorRepairCost.addClass(errorClass);
						else majorRepairCost.removeClass(errorClass).addClass(validClass);
					} else majorRepairCost.removeClass(errorClass);
					if (replacementCost.val() !== '') {
						var count = errorCount;
						errorCount += validateDecimalLength(replacementCost, maxDigits);
						if (count !== errorCount) replacementCost.addClass(errorClass);
						else replacementCost.removeClass(errorClass).addClass(validClass);
					} else replacementCost.removeClass(errorClass);
					if (assetCategoryDescription.val() !== '') {
						assetCategoryDescription.addClass(validClass);
					} else assetCategoryDescription.removeClass(validClass);
				}

				if (isSelectValid && errorCount == 0) {
					$.ajax({
						url: contextPath + baseURL + checkDetailsExistURL,
						type: "GET",
						data: {
							categoryType: categoryType.val(),
							categoryName: categoryName.val(),
							id: !id ? 0 : id
						},
						success: function(response) {
							$('.errMsg').addClass('d-none');

							// If category name does not exist
							if (response === false || response === 'false') {
								categoryName.removeClass(errorClass).addClass(validClass);

								// If 'Maintenance', directly submit the form
								if (categoryType.val() === 'Maintenance') {
									submitForm();
								}
								// If 'Category', proceed to check the shortcode existence
								else if (categoryType.val() === 'Category') {
									$.ajax({
										url: contextPath + baseURL + checkShortcodeExistURL,
										type: "GET",
										data: {
											assetCategoryShortcode: assetCategoryShortcode.val(),
											id: !id ? 0 : id
										},
										success: function(response) {
											$('.shortcodeErrMsg').addClass('d-none');

											// If shortcode does not exist
											if (response === false || response === 'false') {
												assetCategoryShortcode.removeClass(errorClass).addClass(validClass);
												submitForm(); // Proceed to submit form for 'Category'
											} else {
												// Shortcode exists
												assetCategoryShortcode.removeClass(validClass).addClass(errorClass);
												$('.shortcodeExist').removeClass('d-none');
												$("#asset-configuration-modalSave").attr('disabled', false);
												return false;
											}
										},
										error: function() {
											showToast('Error', 'Error occurred while checking shortcode');
										}
									});
								}
							} else {
								// Category name exists
								categoryName.removeClass(validClass).addClass(errorClass);
								if (categoryType.val() === 'Category') {
									$('.assetCategoryExist').removeClass('d-none');
								} else if (categoryType.val() === 'Maintenance') {
									$('.maintenanceTypeExist').removeClass('d-none');
								}
								$("#asset-configuration-modalSave").attr('disabled', false);
								return false;
							}
						},
						error: function() {
							showToast('Error', 'Error occurred while checking details');
						}
					});
				} else {
					$("#asset-configuration-modalSave").attr('disabled', false);
				}
			});

		},
		error: function() {
			showToast('Error', 'Error occured');
		}
	});
}

function submitForm() {
	$('#categoryType1').val($('#categoryType').val());
	$('#minorRepairCost1').val($('#minorRepairCost').val());
	$('#majorRepairCost1').val($('#majorRepairCost').val());
	$('#replacementCost1').val($('#replacementCost').val());

	$('form[id="assetCategoryForm"]').submit();
}

function deleteCategory(element) {
	const id = element !== null ? element.getAttribute("data-category-id") : 0;
	const type = element !== null ? element.getAttribute("data-category-type") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + type + "/" + id,
			type: "Delete",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + id).parents('tr').remove();
				}
				showToast(response.status, response.message);
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

function attachCategoryTypeChangeHandler() {
	$('#categoryType').off('change').on('change', function() {
		const selectedOption = $(this).val();
		let categoryName = $('#categoryName');
		let label = '';
		$('.form-control').removeClass(errorClass).removeClass(validClass);

		if (selectedOption === 'Category') {
			label = 'Asset Category';
			$('.forCategory').removeClass('d-none');
		} else if (selectedOption === 'Maintenance') {
			label = 'Maintenance Type';
			$('.forCategory').addClass('d-none');
		} else {
			$('#assetCategoryOrMaintenance').addClass('d-none');
			$('.forCategory').addClass('d-none');
		}
		if (selectedOption !== '0') {
			$('#assetTypeLabel').text(label);
			categoryName.attr('placeholder', 'Enter '+label);
			$('#assetCategoryOrMaintenance').removeClass('d-none');
		}
	});
}

function validateAssetConfigName(catId) {
	const categoryName = $('#categoryName').val().trim();
	let categoryType = $("#categoryType");
	const id = $('#assetId').val();
	$('.errMsg').removeClass('d-block').addClass('d-none');
	if (!categoryName) {
		if (categoryType.val() === 'Category') {
			$(catId).addClass(errorClass);
			$('.assetCategoryReq').removeClass('d-none');
		} else if (categoryType.val() === 'Maintenance') {
			$(catId).addClass(errorClass);
			$('.maintenanceTypeReq').removeClass('d-none');
		}
		return;
	}
	$.ajax({
		url: contextPath + baseURL + checkDetailsExistURL,
		type: "GET",
		data: {
			categoryType: categoryType.val(),
			categoryName: categoryName,
			id: !id ? 0 : id
		},
		success: function (response) {
			if (response) {
				$(catId).removeClass(validClass).addClass(errorClass);
				if (categoryType.val() === 'Category') {
					$('.assetCategoryExist').removeClass('d-none');
				} else if (categoryType.val() === 'Maintenance') {
					$('.maintenanceTypeExist').removeClass('d-none');
				}
			} else {
				$(catId).removeClass(errorClass).addClass(validClass);
				$("#asset-configuration-modalSave").attr('disabled', false);
				if (categoryType.val() === 'Category') {
					$('.assetCategoryExist').addClass('d-none');
				} else if (categoryType.val() === 'Maintenance') {
					$('.maintenanceTypeExist').addClass('d-none');
				}
			}
		},
		error: function () {
			showToast('Error', 'Error occurred while checking Asset Name');
		}
	});
}

function validateAssetShortCode(catId) {
	const assetCategoryShortcode = $('#assetCategoryShortcode').val().trim();
	const id = $('#assetId').val();
	$('.shortcodeErrMsg').removeClass('d-block').addClass('d-none');
	if (!assetCategoryShortcode) {
		$('.shortcodeReq').removeClass('d-none').addClass('d-block');
		return;
	}
	$.ajax({
		url: contextPath + baseURL + checkShortcodeExistURL,
		type: "GET",
		data: {
			assetCategoryShortcode: assetCategoryShortcode,
			id: !id ? 0 : id
		},
		success: function(response) {
			$('.shortcodeErrMsg').addClass('d-none');

			// If shortcode does not exist
			if (response === false || response === 'false') {
				$(catId).removeClass(errorClass).addClass(validClass);
			} else {
				// Shortcode exists
				$(catId).removeClass(validClass).addClass(errorClass);
				$('.shortcodeExist').removeClass('d-none');
				$("#asset-configuration-modalSave").attr('disabled', false);
				return false;
			}
		},
		error: function() {
			showToast('Error', 'Error occurred while checking shortcode');
		}
	});
}
