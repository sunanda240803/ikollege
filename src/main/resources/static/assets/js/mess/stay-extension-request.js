$(document).ready(function () {
    // Get the current date and time in ISO format
    const now = new Date();
    const isoNow = now.toISOString().slice(0, 16);

    function updateStayToDateMin() {
        const previousToDate = $('#previousToDate').val();
        const previousCheckoutTime = $('#previousCheckoutTime').val();

        if (previousToDate && previousCheckoutTime) {
            const previousDateTime = new Date(`${previousToDate}T${previousCheckoutTime}`);
            if (isNaN(previousDateTime.getTime())) {
                console.error("Invalid previousToDate or previousCheckoutTime.");
                return;
            }

            const nextDayStart = new Date(previousDateTime);
            nextDayStart.setMinutes(previousDateTime.getMinutes() + 1); 
            const minDateTime = nextDayStart.toISOString().slice(0, 16);

            $('#stayToDate').attr('min', minDateTime);
        } else {
            $('#stayToDate').attr('min', isoNow);
        }
    }

    function validateStayToDate() {
        const stayToDate = $('#stayToDate').val();
        const previousToDate = $('#previousToDate').val();
        const previousCheckoutTime = $('#previousCheckoutTime').val();

        const previousDateTime = new Date(`${previousToDate}T${previousCheckoutTime}`);
        if (isNaN(previousDateTime.getTime())) return;

        const maxAllowedDays = parseInt($('#maximumDays').val());
        const nextDayStart = new Date(previousDateTime);
        nextDayStart.setMinutes(previousDateTime.getMinutes() + 1);

        const maxAllowedDateTime = new Date(previousDateTime);
        maxAllowedDateTime.setDate(maxAllowedDateTime.getDate() + maxAllowedDays);

        const stayToDateValue = new Date(stayToDate);

        const requiredFeedback = $('#stayToDate').siblings('.invalid-feedback');
        const exceedFeedback = $('#stayToDate').siblings('.invalid-value-feedback');
        $('#stayToDate').siblings('.invalid-feedback, .invalid-value-feedback').addClass(displayNone);
        $('#stayToDate').removeClass(errorClass);

        if (!stayToDate) {
            requiredFeedback.removeClass(displayNone);
            $('#stayToDate').addClass(errorClass);
        } else if (stayToDateValue < nextDayStart || stayToDateValue > maxAllowedDateTime) {
            exceedFeedback.removeClass(displayNone);
            exceedFeedback.html(stayToDateLimit);
            $('#stayToDate').addClass(errorClass);
        }
    }

    updateStayToDateMin();

    $('#stayToDate').on('change', validateStayToDate);
    $('#previousToDate, #previousCheckoutTime').on('change', updateStayToDateMin);
});


$('#extensionSave').off("click").on("click", function(e) {
	e.preventDefault();

	const requiredFields = $('input[required]');
	const status = valRequiredMultiTextRadio(requiredFields);
	const selectedGuests = $('input[type="checkbox"][name^="guestList"]:checked');
	if (selectedGuests.length === 0) {
		showToast('Error', guestSelectionMsg);
		return false;
	}
	if (status) {
		$('#guestRequestForm').submit();
	} else {
		return false;
	}
});

function triggerDownload(element) {
	var fileName = element.getAttribute('data-filename');
	const url = contextPath + baseURL + downloadURL + "/" + fileName;
	window.location.href = url;
}