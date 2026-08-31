function getTempAccommodationData() {
	const dateRanges = [
		{ fromElement: "submittedFrom", toElement: "submittedTo" }
	];
	const isDateRangeValid = validateBetweenDateRanges(dateRanges);
	if (isDateRangeValid) {
		$('#paymentListForm').submit();
	}
}