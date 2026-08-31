$(document).ready(function() {
});

function validateFile() {
	let fileInput = $('#uploadFile')[0];
	let file = fileInput.files[0];

	// Hide all error messages initially
	$('.errMsg').addClass(displayNone);
	$('#uploadFile').removeClass(errorClass);

	// Validate if a file is selected
	if (!file) {
		$('#uploadFile').addClass(errorClass);
		$('.uploadFileReq').removeClass(displayNone);
		return false;
	}

	// Check if the file is an Excel file
	let allowedExtensions = /(\.xls|\.xlsx)$/i;
	if (!allowedExtensions.exec(file.name)) {
		$('#uploadFile').addClass(errorClass);
		$('.invalidType').removeClass(displayNone);
		return false;
	}

	// Define the expected base name
	const expectedBaseName = "Room Inventory Bulk Upload Template";

	// Get the file name without extension
	const fileNameWithoutExtension = file.name.split('.').slice(0, -1).join('.');

	// Check if the file name starts with the expected base name
	if (!fileNameWithoutExtension.startsWith(expectedBaseName)) {
	    $('#uploadFile').addClass(errorClass);
	    $('.invalidName').removeClass(displayNone);
	    return false;
	}
	$('#uploadFile').addClass(validClass);
	return true;
}