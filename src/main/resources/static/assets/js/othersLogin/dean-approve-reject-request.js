$('#newCategory').change(function() {
	if ($(this).val() === 'others') {
		$('#categoryOthersContainer').removeClass('d-none');
	} else {
		$('#categoryOthersContainer').addClass('d-none').val('');
	}
});

$('#changeCatBtn').click(function() {
	$('.catChange').addClass(displayNone);
	$('.catNew').removeClass(displayNone);
});

$('#cancelCatButton').click(function() {
	$('#newCategory').val('');
	$('#categoryOthers').val('');
	$('.catChange').removeClass(displayNone);
	$('.catNew').addClass(displayNone);
});

function rejectRequest(element, reversal){
    $('#deleteModal').modal('show');
    $('#deleteModalCancel').removeClass(displayNone);
    $('#deleteBodyText').html(`
            <div class="mb-3 themeForm">
                <textarea type="text" class="form-control" id="rejectReason" maxlength="360" oninput="trimSpaces(this)" placeholder="Enter Reject Reason"></textarea>
                <div class="invalid-feedback">Rejection Reason Required</div>
            </div>
        `);
    const rejectReason = $('#rejectReason');
    const deleteModalPositive = $("#deleteModalPositive");

    $('modalheader').text(reversal === 'reverse' ? 'This request is in Approved state. Do you want to Reject the request?' :
        'Are you sure you want to Reject this accommodation request ?');
    deleteModalPositive.prop("disabled", true);

    if (rejectReason.val().trim() === "") {
        rejectReason.addClass(errorClass).removeClass(validClass);
    } else {
        rejectReason.addClass(validClass).removeClass(errorClass);
    }
    rejectReason.on("input", function () {
        if (rejectReason.val().trim() === "") {
            deleteModalPositive.prop("disabled", true);
        } else {
            deleteModalPositive.prop("disabled", false);
        }
    });
    deleteModalPositive.html(`
            <i class="fa fa-check-circle"></i> Confirm
            `);
    deleteModalPositive.off("click").on("click", function (e) {
        $('#infoModal').modal('hide');
        approvalStatusRequest(element, '');
    });
}

function approveRequest(element){
    const deleteModalText = $('#deleteModalText');
    const infoModalPositive = $("#infoModalPositive");
    if (!validateRequests()) {
        showToast('Error', 'Please enter mandatory field');
        return false;
    } else {
        $('#infoModal').modal('show');
        $('#infoModalCancel').removeClass(displayNone);
        $('modalheader').text("Information !");
        deleteModalText.empty();
        infoModalPositive.off("click").prop("disabled", false);
        deleteModalText.text('Are you sure you want to approve this accommodation request?');
        infoModalPositive.html(`
            <i class="fa fa-check-circle"></i> Confirm
            `);
        infoModalPositive.off("click").on("click", function (e) {
            $('#infoModal').modal('hide');
            approvalStatusRequest(element, '');
        });
    }
}

function approvalStatusRequest(element, mailResponse){
    const status = element !== null ? element.getAttribute("data-status") : 0;
    let rejectionReason = $("#rejectReason").val();
    let stayFrom = $("#stayFrom").val();
    let stayTo = $("#stayTo").val();
    let category = $("#category").val();
    let newCategory = $("#newCategory").val();
	let categoryOthers = $("#categoryOthers").val();
    let accomPriority = $("#accomPriority").val();
    let notes = $("#notes").val();
    let occupancy = $('#occupancy').val();
    let currentStayFrom = $("#currentStayFrom").val();
    let currentStayTo = $("#currentStayTo").val();
	let isValid = true;
	if ($('#newCategory').val() === 'others') {
		isValid = validateRadioAndText('#categoryOthers');
	}
	if (isValid) {
		showLoader();
        $('#overrideAndApproveButton').attr('disabled', true);
        $('#approveButton').attr('disabled', true);
        $('#rejectButton').attr('disabled', true);
		$.ajax({
			url: `${contextPath}${baseURL}/${status}`,
			type: 'POST',
			data: {
				'candidateAppointmentRequestDto.stayFrom': stayFrom,
				'candidateAppointmentRequestDto.stayTo': stayTo,
				'candidateAppointmentRequestDto.category': category,
				'candidateAppointmentRequestDto.newCategory': newCategory,
				'candidateAppointmentRequestDto.newCategoryOthers': categoryOthers,
				'candidateAppointmentRequestDto.accomPriority': accomPriority,
				'candidateAppointmentRequestDto.occupancy': occupancy,
				'candidateAppointmentRequestDto.notes': notes,
				'stayExtensionRequestForm.stayExtensionRequestDto.stayFrom': currentStayFrom,
				'stayExtensionRequestForm.stayExtensionRequestDto.stayTo': currentStayTo,
				rejectReason: rejectionReason !== "" ? rejectionReason : ""
			},
			success: function(response) {
				hideLoader();
				/*const splitStatus = response.split('`');
				const status = splitStatus[0];
				let message = splitStatus[1];
				if (message === 'OverrideApproved'){
					message = 'Approved';
				}
				if (status === 'updated' && mailResponse === '') {
					showToast("Success", `Student accommodation request ${message} successfully!`);
					setTimeout(function() {
						window.location.href = contextPath + baseURL;
					}, 5000);
				} else if(status === 'updated' && (mailResponse === 'r' || mailResponse === 'a')){
					showToast("Success", `Student accommodation request ${message} successfully!`);
					setTimeout(function() {
						window.location.href = contextPath + baseURL;
					}, 5000);
				} else {
					showToast("Error", response);
				}*/

				showToast(response.status, response.message);
				setTimeout(function() {
					window.location.href = `${contextPath}${baseURL}${category.toLowerCase() === 'interviews' ? interviewURL : ''}`;
				}, 5000);
			},
			error: function(error) {
				showToast('Error:', error.responseText);
                $('#overrideAndApproveButton').attr('disabled', false);
                $('#approveButton').attr('disabled', false);
                $('#rejectButton').attr('disabled', false);
			}
		});
	}
}

function saveRequest(element){
    if (validateRequests()) {
        saveDetails(element);
    } else {
        showToast('Error', 'Please enter mandatory field');
    }
}

function saveDetails(element){
    let newCategory = $("#newCategory").val();
    let categoryOthers = $("#categoryOthers").val();
    let notes = $("#notes").val();
    let stayFrom = new Date($("#stayFrom").val()).toISOString().split('T')[0];
    let stayTo = new Date($("#stayTo").val()).toISOString().split('T')[0];
    let currentStayFrom = $("#currentStayFrom").val();
    let currentStayTo = $("#currentStayTo").val();
    showLoader();
    $.ajax({
        url: `${contextPath}${baseURL}${saveURL}`,
        type: 'POST',
        data: {
            "candidateAppointmentRequestDto.stayFrom": stayFrom,
            "candidateAppointmentRequestDto.stayTo": stayTo,
            "candidateAppointmentRequestDto.newCategory": newCategory,
            "candidateAppointmentRequestDto.newCategoryOthers": categoryOthers,
            "candidateAppointmentRequestDto.notes": notes,
            'stayExtensionRequestForm.stayExtensionRequestDto.stayFrom': currentStayFrom,
            'stayExtensionRequestForm.stayExtensionRequestDto.stayTo': currentStayTo,
        },
        success: function (response) {
            hideLoader();
            if (response === 'updated') {
                showToast("Success", `Accommodation request details saved successfully!`);
                setTimeout(function() {
                    window.location.href = contextPath + baseURL;
                }, 5000);
            } else {
                showToast("Error", 'Something went wrong');
            }
        },
        error: function (error) {
            showToast('Error', 'Something went wrong');
        }
    });
}

function validateRequests(){
    const requiredFields = $('input[required], select[required], textarea[required]');
    let result = validateRadioAndText(requiredFields);
    const dateRanges = [
        { fromElement: "stayRequestFrom", toElement: "stayRequestTo" }
    ];
    const isDateRangeValid = validateBetweenDateRanges(dateRanges);
    return result && isDateRangeValid;
}

function reverseApprovalStatusRequest(element){
    const status = element !== null ? element.getAttribute("data-status") : 0;
    if (validateRequests()){
        if (status === 'Approved'){
            $('#infoModal').modal('show');
            $('#infoModalCancel').removeClass(displayNone);
            $('#deleteModalText').text('This request is in Rejected state. Do you want to Approve the request?');
            $('#infoModalPositive').html(`
            <i class="fa fa-check-circle"></i> Confirm
            `);
            $("#infoModalPositive").on("click", function(e) {
                $('#infoModal').modal('hide');
                approvalStatusRequest(element, '');
            });
        }
        else
            rejectRequest(element, 'reverse');
    } else {
        showToast('Error', 'Please enter mandatory field');
    }
}
