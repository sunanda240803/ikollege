$(document).ready(function() {
	let id = $('#id').val();
	updateSaveButtonStyleForStringId(id, $('#saveForm'));
});

function validateForm() {
	const saveButton = $('#saveForm');
	saveButton.attr('disabled', true);

	let fromDate = $('#fromDate');
	let toDate = $('#toDate');

	$('.form-control').removeClass(errorClass).removeClass(validClass);
	$('.fd').addClass(displayNone);
	$('.td').addClass(displayNone);

	var fromDateValid = validateFromDateToDate(
		fromDate,
		null,
		$('.fromDateReq'),
		$('.fromDateFuture'),
		null
	);

	const reqField = $('input[required]');
	var startDateValid = validateRadioAndText(reqField);

	var toDateValid = validateFromDateToDate(
		toDate,
		fromDate,
		$('.toDateReq'),
		$('.toDateFuture'),
		$('.toDateAfterFromDate')
	);

	if (fromDateValid && toDateValid && startDateValid) {
		$('#hostel-enroll-config-form').submit();
	} else {
		saveButton.attr('disabled', false);
		return false;
	}
}
