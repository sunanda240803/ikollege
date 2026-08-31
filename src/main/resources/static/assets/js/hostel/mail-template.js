$(document).ready(function() {
	$('#addNewId').click(function() {
		window.location.href = contextPath + baseURL + '/'+0;
	});
});


$('#mailTemplateSave').off("click").on("click", async function(e) {
	updateInvalidDivClass();
	$("#mailTemplateSave").attr('disabled', true);
	const requiredFields = $('input[required], textarea[required]');
	let result = validateRadioAndText(requiredFields);

	let mailContent = true;
	const nicE = new nicEditors.findEditor('contentId');
	const blogContent = nicE.getContent();
	const replacedContent = blogContent.replace("<br />", "")
	$('#contentId').val(replacedContent);

	if ($('#contentId').val() !== null && $('#contentId').val().trim() !== '' && $('#contentId').val().trim() !== "" && $('#contentId').val().trim() !== '<br>') {
		$('#contentId').removeClass(errorClass);
	} else if ($('#contentId').val().trim() === '<br>') {
		$('#contentId').addClass(errorClass);
		mailContent = false;
	} else {
		$('#contentId').addClass(errorClass);
		mailContent = false;
	}

	if (result && mailContent) {
		const isExists = await validateMailType();
		if(!isExists) {
			$('#mail-template-form').submit();
		}
		else{
			$("#mailTemplateSave").attr('disabled', false);
		}
	} else {
		$("#mailTemplateSave").attr('disabled', false);
	}
});

bkLib.onDomLoaded(function() {
	new nicEditor({
		fullPanel: true,
		buttonList: ['fontSize','bold','italic','underline','strikeThrough','hr','image','upload','forecolor','link','indent','outdent',
                    'unlink','left','center','right','justify','ol','ul','xhtml']
	}).panelInstance('contentId');

});

//bkLib.onDomLoaded(function() {
//new nicEditor({maxHeight : 350,buttonList : ['fontSize','bold','italic','underline','strikeThrough','hr','image','upload','forecolor','link','indent','outdent',
//'unlink','left','center','right','justify','ol','ul','xhtml']}).panelInstance('contentId');
//});

function validateMailType() {
	return new Promise((resolve, reject) => {
		$.ajax({
			url: contextPath + baseURL + validatingUrl,
			type: 'POST',
			data: { 'mailType': $('#mailType').val().trim() },
			success: function (response) {
				const mailTypeElement = $('#mailType');
				const errorDiv = mailTypeElement.siblings(invalidFeedback);
				if(response && ($('#activeFlag').val()===null || $('#activeFlag').val().trim() === '')) {
					errorDiv.html(errorDiv.siblings('.invalid-feedback-exists').html());
					mailTypeElement.addClass(errorClass).removeClass(validClass);
					resolve(true);
				}
				else{
					errorDiv.html(errorDiv.siblings('.invalid-feedback-req').html());
					mailTypeElement.addClass(validClass).removeClass(errorClass);
					resolve(false);
				}
			},
			error: function (error) {
				showToast('Error', error.message);
				reject(error);
			}
		});
	});
}

