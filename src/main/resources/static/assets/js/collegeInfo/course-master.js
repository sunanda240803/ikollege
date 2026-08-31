$(window).on('load', function() {
	$('#addNewId').removeAttr('disabled');
});

$('#addNewId').click(function() {
	editCourseMaster(null);
});

// save and update course master

function editCourseMaster(element) {
	const courseId = element !== null ? element.getAttribute("data-course-id") : 0;
	$.ajax({
		url: contextPath + baseURL + "/" + courseId,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#course-master-modal').modal('show');
			$('.selectpicker').selectpicker('refresh');
			$('#course-master-modalSave span').html(courseId > 0 ? 'Update' : 'Save');
			updateSaveButtonStyle(courseId,$('#course-master-modalSave'));
			$('#courseMasterId').val(courseId > 0 ? courseId : 0);
			$("#course-master-modalSave").on("click", function(e) {
				e.preventDefault();
				$('.alreadyExist').html('');
				let errorCount = 0;
				const elementArray = ['#courseMasterName','#courseMasterHead'];
				var isTextValid = valRequiredTextArray(elementArray);
				var isValidSelect = valRequiredSelect('#departmentId');
				var courseMaster = $('#departmentId');
				if (!valRequiredSelect(courseMaster)) {
					// courseMaster.selectpicker('setStyle', errorClass, 'add');
					courseMaster.parent().find('.dropdown-toggle').addClass(errorClass).removeClass(validClass);
					return false;
				} else {
					courseMaster.selectpicker('setStyle', errorClass, 'remove');
					courseMaster.parent().find('.dropdown-toggle').removeClass(errorClass).addClass(validClass);
				}
				if (isTextValid && (errorCount == 0) && isValidSelect) {
					//Check Course Name check Ajax	
					var id = !courseId ? 0 : courseId;
					var courseNameExist = checkCourseNameExist();
					var courseCode = checkCourseCodeExist();
					if (courseNameExist && courseCode) {
						$("#course-master-modalSave").attr('disabled', true);
						$('#courseMasterForm').submit();
					} else {
						$('#courseMasterName').removeClass(validClass);
						$('#courseMasterName').addClass('highlight-red');
						$('.alreadyExist').html('This Course Name is already exists');
						return false;
					}
				}
			});
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	})
}

// Delete course master
function deleteCourseMaster(element) {
	const courseId = element !== null ? element.getAttribute("data-course-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + baseURL + "/" + courseId,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success') {
					$('#deleteReq_' + courseId).parents('tr').remove();
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


let selectedRole;
// Add department
function showCategoryBox() {
	$('#addDepartment').removeClass(errorClass);
	$('#departmentSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').addClass(displayNone);
	$('#departmentInput, #saveCatBtn, #departmentCancelButton').removeClass(displayNone);
	$('#departmentName').val(0);
	$('#addDepartment').val('');
	$('#saveCatBtn').removeClass('btn-blue').addClass('btn-aqua');
	$('#saveCatBtn').find('i').removeClass('fa-rotate').addClass('fa-floppy-disk');
	$('#saveCatBtn').tooltip('dispose').attr('title', 'Save Department').tooltip({ trigger: 'hover' });  
	
}

// department cancel
$('#departmentCancelButton').click(function() {
	$('#addDepartment').removeClass(errorClass);
	$('#validateCatNameDrop').addClass(displayNone);
	$('#departmentInput, #saveCatBtn, #departmentCancelButton').addClass(displayNone);
	$('#departmentSelect, #addCatBtn, #editCatBtn, #deleteCatBtn').removeClass(displayNone);
});

//department update
$('#editCatBtn').click(function() {
	var id = $('#departmentName').val();
	if (id === null || id == "" || id == 0) {
		$('#validateCatNameDrop').removeClass(displayNone);
		$('#departmentSelect').removeClass("d-none");

	} else {
		$('#saveCatBtn').removeClass('btn-aqua').addClass('btn-blue');
		$('#saveCatBtn').find('i').removeClass('fa-floppy-disk').addClass('fa-rotate');
		$('#validateCatNameDrop').addClass(displayNone);
		const dropdown = document.getElementById("departmentName");
		const selectedOption = dropdown.options[dropdown.selectedIndex];
		const selectedText = selectedOption.text;
		$('#masterId').val(id);
		$('#addDepartment').val(selectedText);
		$('#departmentSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').addClass(displayNone);
		$('#departmentInput, #saveCatBtn, #departmentCancelButton').removeClass(displayNone);
		$('#saveCatBtn').tooltip('dispose').attr('title', 'Update Department').tooltip({ trigger: 'hover' });  
	}
});

//Department Save
$('#saveCatBtn').on("click", function(event) {
	$(this).prop('disabled', true);
	event.preventDefault();
	var departmentName = $('#addDepartment').val().trim();
	var id = $('#masterId').val().trim();

	if ($('#addDepartment').val() === '') {
		const divElement = document.getElementById('validateCatName');
		divElement.textContent = "Department Name is required";
		$('#addDepartment').addClass(errorClass);
		$("#saveCatBtn").attr('disabled', false);
		return false;
	}
	else {
		$('#validateCatName').html('');
		id = id != 0 ? id : 0;
		var validateUrl = contextPath + baseURL + baseURL1 + checkDepartmentNameExistURL + "/" + departmentName + "/" + id;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				if (response == false || response == 'false') {
					$('#masterId').val(id);
					$('#departmentSave').submit();
				} else {
					$('#addDepartment').addClass(errorClass);
					$('.invalid-feedback').html('Department Name is already exist');
					$("#saveCatBtn").attr('disabled', false);
					return false;
				}

			},
			error: function(error) {
				showToast('Error', 'Error occurred');
			}
		})

	}
});

//Delete department

function deleteDepartment() {
	var id = $('#departmentName').val();
	if (id === null || id == "" || id == 0) {
		$('#validateCatNameDrop').removeClass(displayNone);
		$('#departmentSelect').removeClass("d-none");
		return false;
	}
	$('#validateCatNameDrop').addClass(displayNone);
	$('#deleteModal').modal('show');
	$('#dynamicId').val(id);

	$('#deleteModalPositive').click(function() {
		$('#deleteModal').modal('hide');
		const deptId = $('#dynamicId').val();
		$.ajax({
			url: contextPath + baseURL + baseURL1 +"/" + deptId,
			type: "DELETE",
			success: function(response) {
				if (response != "") {
					showToast('Success', 'Department deleted successfully');
					$.each(response, function(key, value) {
						$('#departmentName')
							.find('option')
							.remove()
							.end()
							.append('<option value="0">Select</option>')
							.val('0');
						if (value.length !== 0) {
							value.forEach(function(item) {
								console.log(item);
								$('#departmentName').append($('<option>', {
									value: item.department_id,
									text: item.department_name,
									style: 'white-space:normal'
								}));

							});
						}
						$('.selectpicker').selectpicker('refresh');
						$('#validateCatNameDrop').addClass(displayNone);
						$('#departmentSelect,#addCatBtn, #editCatBtn, #deleteCatBtn').removeClass(displayNone);
						$('#departmentInput, #saveCatBtn, #departmentCancelButton').addClass(displayNone);
					});
				} else {
					showToast('Failure', response);
				}
			},
			error: function(xhr, status, error) {
			let errorMessage = 'Error occurred';
			if (xhr.responseJSON && xhr.responseJSON.message) {
				// Use the message from the server's response if available
				errorMessage = xhr.responseJSON.message;
			} else if (xhr.responseText) {
				// Use the responseText if available
				errorMessage = xhr.responseText;
			}
			showToast('Error', errorMessage);
		}
	});
	$('#deleteModalPositive').off('click');
});
}

// check course name exist
function checkCourseNameExist() {
	var courseName = $('#courseMasterName');
	var courseId = $('#courseMasterId');
	var courseNameVal = courseName.val().trim();
	var courseIdVal = courseId.val().trim();
	let isValid = false;
	if(courseNameVal === ""){
		$('#courseMasterName').addClass(errorClass);
		$('.nameExist').addClass(displayNone);
		isValid = false;
		return isValid;
	}
	else if (courseNameVal != '') {
		var validateUrl = contextPath + baseURL+ checkCourseNameExistURL + "/" + courseNameVal + "/" + courseIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == false || resData == 'false') {
					$('.nameExist').addClass(displayNone);
					isValid = true;
				} else {
					$('.nameExist').removeClass(displayNone);
					$('.nameExist').addClass('d-block');
					//$('.nameReq').addClass(displayNone);
					$("#course-master-modalSave").attr('disabled', false);
					isValid = false;
				}

			},
			error: function(error) {
				showToast('Error', 'Error occurred');
				return false;
			}
		})
	}
	return isValid;
}

function validateAlphabetWithSpaceInput(element) {
	let input = element.value.trim();
	const regexAlphabetWithSpace = /^[A-Za-z ]+$/;
	if (!regexAlphabetWithSpace.test(input)) {
		input = input.replace(/[^A-Za-z ]/g, '');
		element.value = input;
	}
}

function checkCourseCodeExist(){
	var courseName = $('#courseMasterName');
	var courseId = $('#courseMasterId');
	var courseNameVal = courseName.val().trim();
	var courseIdVal = courseId.val().trim();
	var courseCode = $('#courseMasterHead');
	var courseCodeVal = courseCode.val().trim();
	let isValid = false;
	if(courseCodeVal === ""){
		$('#courseMasterHead').addClass(errorClass);
		$('.codeExist').addClass(displayNone);
		isValid = false;
		return isValid;
	}
	else if (courseCodeVal != '') {
		var validateUrl = contextPath + baseURL+ checkCourseCodeExistURL + "/" + courseCodeVal + "/"+ courseIdVal;
		$.ajax({
			url: validateUrl,
			type: "GET",
			async: false,
			success: function(response) {
				const resData = response;
				if (resData == true || resData == 'true') {
					$('.codeExist').removeClass(displayNone);
					$('.codeExist').addClass('d-block');
					//$('.codeReq').addClass(displayNone);
					$("#course-master-modalSave").attr('disabled', false);
					isValid = false;
				} else {
					$('.codeExist').addClass('d-block');
					$('.codeExist').addClass(displayNone);
					isValid = true;
				}

			},
			error: function(error) {
				showToast('Error', 'Error occurred');
				return false;
			}
		})
	}
	return isValid;
}

// function clearInput() {
// 	var departmentId = $('#departmentId').val();
// 	if (departmentId != '') {
// 		$('#deptInuptVal').addClass(displayNone);
// 	}
// }
