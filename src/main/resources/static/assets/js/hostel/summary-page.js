function getList(element){
    const row = element.closest('tr');
    const accommodationType = row.querySelectorAll('td')[0].innerText.trim();
    const categoryRow = accommodationType;
    const clickedText = element.innerText.trim();
    const columnName = element.getAttribute('data-column');
    const isVacatingScreen = accommodationType.includes($('#vacation').val());
    const isOtherCandidateScreen = accommodationType.includes($('#candidate').val()) || accommodationType.includes($('#other').val());
    const isScholarScreen = accommodationType.includes($('#scholar').val());
    const allottedVal = $('#allotted').val();
    const checkIn = $('#checkIn').val();
    const checkOut = $('#checkOut').val();
    let baseUrl = isVacatingScreen ? '/studentVacating' :
        (isOtherCandidateScreen ? '/deanOtherCandidateRequests' :
            (isScholarScreen ? '/scholars' : '/studentAccommodationRequest')
        );

    const validationStatusMap = {
        "allottedTotal": allottedVal,
        "checkedIn": checkIn,
        "pendingCheckedIn": allottedVal,
        "tobCheckedOutToday": checkOut,
        "checkedOutToday": checkOut,
        "pendingCheckedOut": checkIn
    };

    const status = validationStatusMap[columnName] || "";
    const params = new URLSearchParams();
    const filtersToClear = getFiltersInputs(isVacatingScreen)

    const statusKey = isVacatingScreen ? 'approveStatus' : 'validationStatus';
    const fullStatusKey = "additionalParam." + statusKey;

    params.set("searchFilter", "true");
    params.set("page", "1");
    params.set("size", "25");

    if (status !== "") {
        params.set(fullStatusKey, status);
    }

    filtersToClear.forEach(key => {
        const fullKey = "additionalParam." + key;
        if (fullKey === fullStatusKey && status !== "") {
            return;
        }
        params.set(fullKey, "");
    });
    params.set("searchFilter", "true");
    window.location.href = contextPath + baseUrl + "?" + params.toString();
}

function getEnrollAndVacatingScreen(element){
    const status = element.getAttribute('data-column');
    const rowName = element.getAttribute('data-row');
    const isVacatingScreen = rowName.includes("vacant");
    let baseUrl = isVacatingScreen ? '/studentVacating' : '/hostelEnrollment';
    const params = new URLSearchParams();
    const filtersToClear = getFiltersInputs(isVacatingScreen);
    const statusKey = isVacatingScreen ? 'approvalStatus' : 'validationStatus';
    const fullStatusKey = "additionalParam." + statusKey;

    params.set("searchFilter", "true");
    params.set("page", "1");
    params.set("size", "25");

    if (status !== "" && status !== "total") {
        params.set(fullStatusKey, status);
    }

    filtersToClear.forEach(key => {
        const fullKey = "additionalParam." + key;
        if (fullKey === fullStatusKey && status !== "" && status !== "total") {
            return;
        }
        params.set(fullKey, "");
    });
    params.set("searchFilter", "true");
    window.location.href = contextPath + baseUrl + "?" + params.toString();
}

function getFiltersInputs(isVacatingScreen){
    return !isVacatingScreen ? [
        "validationStatus", "category", "approvalFromDate", "approvalToDate",
        "submittedFromDate", "submittedToDate",
        "appointmentFromDate", "appointmentToDate",
        "stayFromDate", "stayToDate", "studentName", "studentId", "hostelName", "currentDayStayFlag"
    ] : ['approvalStatus', 'vacatingReason', 'submittedFromDate',
        'submittedToDate', 'vacatingFromDate', 'vacatingToDate',
        'studentName', 'studentId', 'hostelName'];
}