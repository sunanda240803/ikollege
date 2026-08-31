$(document).ready(function() {
	   let phNumMaskElementArray = ['#mobileNumber,#phoneNumber'];
        inputMask(phNumMaskElementArray, mobileNumberMask);
	 	$('input[name="postSelect"]').on('change', function () {
			const selectedVal = $('input[name="postSelect"]:checked').val();
			if (selectedVal === 'others') {
                    $('#otherPost').removeClass('d-none').attr('required', true);
                } else {
					$('#otherPost').attr('required', false).removeClass('is-invalid');
					$('.otherPost').addClass(displayNone);
                    $('#otherPost').addClass('d-none').removeAttr('required').val('');
                }
		 });
    validateForm(); 
});
const candidateProfilePhotoMaxSize = 1 * 1024 * 1024; // 1 MB
function validateForm() {
    $('#profileSave').off("click").on("click", function(e) {
        e.preventDefault();
		const profileSave = $('#profileSave');
		profileSave.attr('disabled', true);
		updateInvalidDivClass();
		const selectedVal = $('input[name="postSelect"]:checked').val();
		if (selectedVal === 'others') {
			if($('#otherPost').val()!=null && $('#otherPost').val()!=""){
				$('.otherPost').addClass(displayNone);
			}else{
			$('.otherPost').removeClass(displayNone);
			}
			$('#otherPost').attr('required', true);
		}else{
			$('#otherPost').attr('required', false);
		}
        const requiredFields = $('input[required], select[required], textarea[required]');
        let isValid = validateRadioAndText(requiredFields);
		//for image validation
        let imageSelected = $('#studentPhoto')[0].files.length > 0;
        let imageElement = document.getElementById("studentProfileImage");
        let defaultSrc = imageElement.getAttribute("data-default");
        let isDefaultImage = imageElement.src.includes(defaultSrc);
        if (!imageSelected && isDefaultImage) {
            showToast('Error', 'Please choose profile image.');
            isValid = false;
            $('.imageValidation').removeClass('d-none');
        } else {
            $('.imageValidation').addClass('d-none');
        }

		const selectedImage = $('#studentPhoto')[0].files[0];
		if (selectedImage && selectedImage.size > candidateProfilePhotoMaxSize) {
			showToast('Error', 'Photo size should be 1 MB or less.');
			isValid = false;
		}
		
        if (isValid && validateMobilePhoneNumber()) {
            $('#otherCandidateForm').submit(); 
        } else {
            profileSave.attr('disabled', false);
			showToast('Error', 'Please fill all the mandatory fields and submit.');
        }
    });
}

$('#studentPhoto').on('change', function (event) {
    const file = event.target.files[0];
    const imgElement = document.getElementById('studentProfileImage');

	if (file && file.size > candidateProfilePhotoMaxSize) {
		showToast('Error', 'Photo size should be 1 MB or less.');
		event.target.value = '';
		imgElement.src = imgElement.getAttribute('data-default');
		return;
	}

    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        imgElement.src = e.target.result;
      };
      reader.readAsDataURL(file);
    } else {
      imgElement.src = imgElement.getAttribute('data-default');
    }
  });

function validateMobilePhoneNumber(){
	var mobileNumber = $('#mobileNumber').val();
	var phoneNumber =  $('#phoneNumber').val();
	if(mobileNumber==""){$('.mobileNo').addClass(displayNone);}
	if(phoneNumber==""){$('.phoneNo').addClass(displayNone);}
	isValid = false;
	if((mobileNumber!=null && mobileNumber!="") && mobileNumber.replace(/\D/g, '').length==10){
		$('.mobileNo').addClass(displayNone);
		isValid = true;
	}else if((mobileNumber!=null && mobileNumber!="") && mobileNumber.replace(/\D/g, '').length<10){
		$('.mobileNo').removeClass(displayNone);
		isValid = false;
	}
	if((phoneNumber!=null && phoneNumber!="") && phoneNumber.replace(/\D/g, '').length==10){
		$('.phoneNo').addClass(displayNone);
		isValid = true;
	}else if((phoneNumber!=null && phoneNumber!="") && phoneNumber.replace(/\D/g, '').length<10){
		$('.phoneNo').removeClass(displayNone);
		isValid = false;
	}
	return isValid;
}
