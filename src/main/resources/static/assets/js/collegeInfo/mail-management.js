$(window).on('load', function() {
	$('#contentModal').on('show.bs.modal', function (event) {
		var button = $(event.relatedTarget); // Button that triggered the modal
		var content = button.data('content'); // Extract info from data-* attributes

		// Update the modal's content
		$('#modalContent').html(content);
	});
});

function getMailDetails() {
	const dateRanges = [
		{ fromElement: "fromDate", toElement: "toDate" }
	];
	const isDateRangeValid = validateBetweenDateRanges(dateRanges);
	if (isDateRangeValid) {
		$('#mailManagementId').submit();
	}
}