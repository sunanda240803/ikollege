
$('#wardenInfoModalSave').off("click").on("click", function (e) {
    const url = getUrl(saveWardenInfoURL);
    const fileInput = $('#layout')[0].files[0];
    const selectedHostelId = $('input[name="hostelId"]:checked').first().attr('id');
    let imageName = "";
    let imageContent = "";
    let base64File = "";

    if (!validateMandatoryFields(fileInput, selectedHostelId)) {
        return;
    }

    if (fileInput) {
        const reader = new FileReader();
        reader.onload = function () {
            base64File = reader.result.split(',')[1];
            imageName = fileInput.name;
            imageContent = base64File;
            sendFormData(url, imageName, imageContent, selectedHostelId);
        };
        reader.readAsDataURL(fileInput);
    } else {
        sendFormData(url, imageName, imageContent, selectedHostelId);
    }
});



function sendFormData(url, imageName, imageContent, selectedHostelId) {
    const formData = new FormData();
    formData.append("id", $('#id').val());
    formData.append("wardenName", $('#wardenName').val());
    formData.append("imageName", imageName);
    formData.append("imageContent", imageContent);
    formData.append("officeNo", $('#officeNo').val());
    formData.append("wardenEmail", $('#wardenEmail').val());
    formData.append("phoneNumber", $('#phoneNumber').val());
    formData.append("ldapUsername", $('#ldapUsername').val());
    formData.append("alternateEmail", $('#alternateEmail').val());
    formData.append("wardenInfoUrl", $('#wardenInfoUrl').val());
    formData.append("associateWardenName", $('#associateWardenName').val());
    formData.append("associateLdapUsername", $('#associateLdapName').val());
    formData.append("hostelId", selectedHostelId);

    fetch(url, {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.ok) {
                showToast("Success", "Data Saved Successfully");
                $('#wardenInfoModal').modal('hide');
                closePopupAndRefresh();
            } else {
                throw new Error('Failed to save data');
            }
        })
        .catch(error => {
            showToast('Error', 'Error occured');
            console.error(error);
        });
}

function validateMandatoryFields(fileInput, selectedHostelId) {
    let isValid = true;
	const invalidClass = 'is-invalid';
	const invalidFeedback = '.invalid-feedback';
	updateInvalidDivClass();
    const requiredFields = $('input[required], select[required], textarea[required]');
    isValid = valRequiredMultiTextRadio(requiredFields);

    if (!fileInput && !$('#layout').prop('disabled')) {
        $('#layout').addClass(invalidClass);
        $('#layout').siblings(invalidFeedback).text('Please select an image to upload.');
        isValid = false;
    } else {
        $('#layout').removeClass(invalidClass);
        $('#layout').siblings(invalidFeedback).text('');
    }

    if (!selectedHostelId) {
        $('#hostelDiv').addClass(invalidClass);
        $('#hostelDiv').siblings(invalidFeedback).text('Please select a hostel.');
        isValid = false;
    } else {
        $('#hostelDiv').removeClass(invalidClass);
        $('#hostelDiv').siblings(invalidFeedback).text('');
    }

    isValid = validateEmailDomain($('#wardenEmail')) && isValid;

    const alternateEmail = $('#alternateEmail').val();
    if (alternateEmail) {
        isValid = validateEmailDomain($('#alternateEmail')) && isValid;
    } else {
        $('#alternateEmail').removeClass('is-invalid');
        $('#alternateEmail').siblings(invalidFeedback).text('');
    }

    return isValid;
}

$("#wardenInfoModalValidateBackend").on("click", function (e) {
    e.preventDefault();
    $('form[id="wardenDetailsForm"]').submit();
});

function closePopupAndRefresh() {
    if (window.opener) {
        window.close();
        window.opener.location.reload();
    } else {
        location.reload();
    }
}

function getUrl(path) {
    return contextPath + wardenInfoURL + path;
}