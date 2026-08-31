$(document).ready(function() {
	$('#students').prop('checked',true);
});

$('#additionalButton').click(function() {
	window.location.href= contextPath + baseUrl + addNewUrl;
});

$("#hdcComplaintUpdate").off("click").on("click", async function (e) {
    e.preventDefault();
    updateInvalidDivClass();
    // $(this).attr("disabled", true);
    //const requiredFields = $('input[required], select[required], textarea[required]');
	var paymentStatus = $('#status').val();
	if(paymentStatus!=null && paymentStatus!="" && paymentStatus!="Pending"){
	    const requiredFields = $('input[required], select[required], textarea[required]');
	    let status = valRequiredMultiTextRadio(requiredFields);
	    var penaltyAmount = $('#penaltyAmount').val();
		var paidAmount = $('#paidAmount').val();
		$('.submitError').text('');
	    if (status) {
			penaltyAmount = parseFloat(penaltyAmount);
			paidAmount = parseFloat(paidAmount);
	       if(paidAmount>penaltyAmount){
				$('.submitError').text(invalidAmount);
				return false;
		   }
		   if(paidAmount===penaltyAmount && paymentStatus!='Paid'){
				$('.submitError').text(amountInvalidStatus);	
				return false;
		   }
		   if(paidAmount<penaltyAmount && paymentStatus=='Paid'){
				$('.submitError').text(statusInvalidAmount);	
				return false;
		   }
		   if(paidAmount<=0 && paymentStatus!="Pending"){
				$('.submitError').text(partialAmount);	
				return false;
		   }
			$('#hdcComplaintForm').submit();
	    } else {
	        $(this).attr("disabled", false);
	    }
	}else{
		$('#hdcComplaintForm').submit();
	}
});

function validatePenaltyStatus(){
	var paymentStatus = $('#status').val();
	$('.submitError').text('');
	if(paymentStatus==null || paymentStatus==""){
		$('#status').addClass('is-invalid');
		return false;
	}else if(paymentStatus!=null && paymentStatus!="" && paymentStatus=="Pending"){
		$('.pendingForm').addClass('d-none');
		$('#status').removeClass('is-invalid');
	}else if(paymentStatus!=null && paymentStatus!="" && paymentStatus!="Pending"){
		$('.pendingForm').removeClass('d-none');
		$('#status').removeClass('is-invalid');
	}
}

//Save flow
$("#hdcComplaintSave").off("click").on("click", async function (e) {
    e.preventDefault();
    updateInvalidDivClass();
	const requiredFields = $('input[required], select[required]');
	const requiredTextArea = $('textarea[required]');
	let isValid = validateRadioAndText(requiredTextArea);
	let status = valRequiredMultiTextRadio(requiredFields);
	var mail = $('#parentMail').prop('checked');
	if(mail){
		$('#mailToParent').val(true);
	}else{
		$('#mailToParent').val(false);
	}
	if(isValid && status){
		$('#hdcComplaintAddForm').submit();
	}
});
	
function getStudentDetails(){
	var id = $('#studentId').val();
	var student = $('#students').prop('checked'); 
	var others = $('#others').prop('checked'); 
	if(id!=null && id!=""){
		$.ajax({
	       url: contextPath + baseUrl + studentUrl + "/" + id,
	       type: "GET",
	       success: function (response) {
			   console.log(response);
			if(response!=null && response!=""){
				if(others){
					$('#studentId').val('');
					showToast('Error', validStudentId);
				}else{
		           $('#studentName').val(response.studentName);
				   $('#hostelId').val(response.hostelId);
				   $('#hostelId').selectpicker('refresh');
				   $('#roomNo').val(response.roomNumber);
				}
			 }else{
				if(student){
					showToast('Error', 'Student ID is invalid');
					$('#studentId').val('');
				}
				$('#studentName').val("");
			    $('#hostelId').val("");
			    $('#roomNo').val("");
			 }
	       },
	       error: function () {
	           showToast('Error', "Error Occurred");
	       }
	   });
   }
}
function getPreviousComplaints(){
	var id = $('#studentId').val();
	if(id!=null && id!=""){
		$.ajax({
	       url: contextPath +baseUrl+ previousComplaints + "/" + id,
	       type: "GET",
	       success: function (response) {
			//console.log(response);
			if(response && response.length > 0){
				$('.previousComp').removeClass(displayNone);
				const container = document.querySelector(".complaint-link");
				container.innerHTML = "";
				response.forEach(function(item) {
		            let a = container.cloneNode(true);
		            a.href = contextPath+baseUrl+item.studentId;
					a.target = "_blank";
		            a.textContent = "Complaint Id : " + item.id;
					let newDiv = document.createElement('div');
			        newDiv.appendChild(a);
			        container.appendChild(newDiv);
		        });
			 }else{
				$('.previousComp').addClass(displayNone);
			 }
	       },
	       error: function () {
	           showToast('Error', "Error Occurred");
	       }
	   });
   }
}
function validateDueDate(){
	var amount = $('#penaltyAmount').val();
	if(amount!=null && amount!=''){
		if(amount>0){
			$('.dueDate').removeClass(displayNone);
			$('#dueDate').val("");
			$('#dueDate').attr('required', true);
			$('#dueDate').removeClass('is-invalid');
		}else{
			$('.dueDate').addClass(displayNone);
			$('#dueDate').val("");
			$('#dueDate').removeAttr('required');
		}
	}else{
		$('.dueDate').addClass(displayNone);
		$('#dueDate').val("");
		$('#dueDate').removeAttr('required');
	}
}
$("#uploadFile_dropZone").off("blur").on("blur", async function (e) {
    const fileInput = this;
    const file = fileInput.files[0];
    const allowedExtensions = ['pdf', 'jpg', 'bmp', 'png', 'jpeg', 'doc', 'docx', 'xls', 'xlsx', 'txt'];
    const maxSizeInMB = 2;
    if (file) {
        const fileSizeInMB = file.size / (1024 * 1024);
        const fileName = file.name.toLowerCase();
        const fileExtension = fileName.split('.').pop();
        // Check file extension
        if (!allowedExtensions.includes(fileExtension)) {
            alert('Invalid file type! Allowed types: ' + allowedExtensions.join(', '));
            fileInput.value = '';
            return;
        }
        // Check file size
        if (fileSizeInMB > maxSizeInMB) {
            alert(`File size exceeds ${maxSizeInMB}MB limit!`);
            fileInput.value = '';
        }
    }
});

function validateStudentDetails(element){
	var data = $(element).val();
	if(data!=null && data!=""){
		if(data==='Students'){
			$('#hostelId').prop('disabled', false).attr('required', true);
			$('#roomNo').prop('disabled', false).attr('required', true);
			$('#hostelDetails').removeClass('d-none');
		}else{
			$('#hostelId').prop('disabled', true).removeAttr('required');
			$('#roomNo').prop('disabled', true).removeAttr('required');
			$('#hostelDetails').addClass('d-none');
		}
	}
}

function downloadFile(element){
    const fileName = element.getAttribute('file-name');
    const downloadUrl = contextPath + file + downloadURL + '/HDC_COMPLAINT_FILE_PATH/' + fileName;

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