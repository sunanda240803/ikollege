$(document).ready(function() {
	var tableLength = $('#tableLength').val();
	if (tableLength !== null && tableLength !== undefined && tableLength != 0) {
		$('#updateId').attr('disabled', false);
	} else {
		$('#updateId').attr('disabled', true);
	}
	$('#updateId').off("click").on("click", function(e) {
		e.preventDefault();
		validateForm()
	});
	if($('#roleId').val()!=null && $('#roleId').val()!='' && ($('#roleId').val()=='Warden' || $('#roleId').val()=='Dean')){
		$('#updateId').addClass(displayNone);
		$('#saveForm').addClass(displayNone);
	}
});

function showStudentList() {
	const requiredFields = $('input[required], select[required]');
	var isValid = validateRadioAndText(requiredFields);
	if (isValid) {
		let searchCriteria = $('input[name="searchCriteria"]:checked').val();
		let field = $('#field').val();
		let searchString = $('#searchString').val();

		// Set these values in the additional param inputs
		$('input[name="additionalParam.searchCriteria"]').val(searchCriteria);
		$('input[name="additionalParam.field"]').val(field);
		$('input[name="additionalParam.searchString"]').val(searchString);

		// Call createUrlWithParams with the current page and size
		let page = $('#pageVal').val();
		let size = $('#sizeVal').val();
		let search = $('#search').val();
		createUrlWithParams(page, size, search);
	}
}

function validateForm() {
	$('#commonSearchSaveId').submit();
}

function viewStudentBioData(element) {
    const studentId = element.getAttribute("data-student-id");
    const url = contextPath + getStudentRegistrationURL + "/" + studentId;
//    window.location.href = url;
    const form = document.getElementById('studentBioData');
    console.log(url);
    form.action = url;
    form.submit();

}

function activeOrInactiveStudent(element) {
    const studentId = element.getAttribute("data-student-id");
    const activeStatus = element.getAttribute("data-student-status");
    $('#activeInactiveStudentModal').modal('show');
    $('#deleteModalText').text(`Are you sure you want to ${activeStatus === 'Y' ? 'inactive' : 'active'} the student ?.`);
    $('#activeInactiveStudentModalPositive').off("click").on("click", function () {
        showLoader();
        $.ajax({
            url: contextPath + baseURL + updateURL + "/" + studentId + "/" + activeStatus,
            method: "POST",
            success: function(response) {
                hideLoader();
                showToast(response.status, response.message);
                setTimeout(function() {
                    location.reload();
                }, 5000);
            },
            error: function(xhr) {
                hideLoader();
                showToast('Error', 'Failed to update student status.');
            }
        });
    });
}