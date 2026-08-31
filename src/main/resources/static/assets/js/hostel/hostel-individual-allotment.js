$(document).ready(function() {
    $("#getDataButton").click(function () {
        getRoomList(this);
    });

    $('.vacant, .occupied, .partially-occupied').click(function() {
        occupiedModal();
    });

    $(document).on("click", "#candidateCheckButton", function() {
        checkCandidateExist();
    });

    $(document).on("click", "#studentCheckButton", function() {
        checkCandidateExist();
    });

    $(document).on("click", "#checkChangeButton", function() {
        checkForChange($(this));
    });

    $(document).on("click", "#allocateChangeButton", function() {
        allocateForChange();
    });

    $(document).on("click", "#candidateSubmitButton", function() {
        saveCandidateDetails();
    });

    $(document).on("click", "#directSubmitButton", function() {
        saveDirectDetails();
    });

    $(document).on("click", "#studentSubmitButton", function() {
        saveStudentDetails();
    });
});

function updateSubmitButton(selectedOption) {
    $("#studentSubmitButton, #candidateSubmitButton, #directSubmitButton, #submitButton").prop("id", "submitButton");
    if (selectedOption === "student") {
        $("#submitButton").prop("id", "studentSubmitButton");
    } else if (selectedOption === "candidate") {
        $("#submitButton").prop("id", "candidateSubmitButton");
    } else if (selectedOption === "direct") {
        $("#submitButton").prop("id", "directSubmitButton");
    }
}

function occupiedModal(element) {
    const date = element ? element.getAttribute("data-date") : '0';
    const roomId = element ? element.getAttribute("data-room-id") : '0';
    const hostelId = element ? element.getAttribute("data-hostel-id") : '0';
    const baseUrl = element ? element.getAttribute("data-url") : '0';
    const isGuest = element.getAttribute("data-guest") === 'true';

    if(isGuest) {
        const requestURL = `${contextPath}${baseUrl}/${date}/${roomId}/${hostelId}`;
        showLoader();
        fetch(requestURL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => response.text()).then(response => {
                $('#occupiedModalDiv').html(response);
                hideLoader();
                $('#occupiedModal').modal('show');
            }).catch(error =>{
                hideLoader();
                showToast('Error:', `Can't open modal ${error}`)
        });
    } else {
        if (!roomId || roomId === '0') {
            showToast('Error:', "Can't open modal. Room ID is missing.");
            return;
        }
        const requestURL = `${contextPath}${baseUrl}/${date}/${roomId}/${hostelId}`;
        showLoader();
        fetch(requestURL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.text())
            .then(response => {
                $('#occupiedModalDiv').html(response);
                hideLoader();
                $('#occupiedModal').modal('show');

                $(document).off("click", "[id$='swapButton']").on("click", "[id$='swapButton']", function () {
                    let subRoomId = $(this).attr("id").replace("swapButton", "");
                    $(".occupantSection, #allocateSection, #changeSection, #allocateStudentButton, #checkButton, #submitButton").addClass(displayNone);
                    $("#swapSection, #backButton").removeClass(displayNone);
                });

                $(document).off("click", "[id$='changeButton']").on("click", "[id$='changeButton']", function () {
                    let subRoomId = $(this).attr("id").replace("changeButton", "");
                    $(".occupantSection, #swapSection, #submitButton, #checkButton, #allocateSection, #allocateStudentButton").addClass(displayNone);
                    $("#changeSection, #backButton, #checkChangeButton").removeClass(displayNone);
                });

                $(document).off("click", "[id$='deleteButton']").on("click", "[id$='deleteButton']", function () {
                    let subRoomId = $(this).attr("id").replace("deleteButton", "");
                    $(".occupantSection, #swapSection, #changeSection, #checkButton, #allocateSection, #allocateStudentButton, #checkChangeButton, #submitButton").addClass(displayNone);
                    $("#backButton, #deleteSection, #deleteActionButton").removeClass(displayNone);
                });

                $(document).off("click", "[id$='allocateButton']").on("click", "[id$='allocateButton']", function () {
                    let element = $(this).attr("data-sub-room-id");
                    const subRoomId = element !== null ? element : '';
                    $("#subRoomId").text(subRoomId);
                    $(".occupantSection, #swapSection, #changeSection, #studentSection, #candidateSection, #directSection, #checkButton, #vacationStudentSection").addClass(displayNone);
                    $("#allocateSection, #backButton").removeClass(displayNone);
                    $("input[name='studentType']").off("change").on("change", function () {
                        if ($("#regular").is(":checked")) {
                            $("#vacationStudentSection, #directSubmitButton, #allocateStudentButton, #re-AllocateStudentButton, #existingStudentHostelDetails").addClass(displayNone);
                            $("#regularStudentSection").removeClass(displayNone);
                        } else if ($("#vacation").is(":checked")) {
                            $("#studentSection, #candidateSection, #directSection, #regularStudentSection, #submitButton, #directSubmitButton, #allocateStudentButton, #re-AllocateStudentButton, #existingStudentHostelDetails").addClass(displayNone);
                            $("#vacationStudentSection").removeClass(displayNone);
                        }
                    });

                    $("input[name='vacationStudentType']").off("change").on("change", function () {
                        $("#studentSection, #candidateSection, #directSection").addClass(displayNone);
                        if ($("#student").is(":checked")) {
                            updateSubmitButton('student');
                            $("#candidateDetails, #submitButton, #candidateSubmitButton, #studentSubmitButton, #directSubmitButton, #candidateDetails").addClass(displayNone);
                            $("#studentSection").removeClass(displayNone);
                        } else if ($("#candidate").is(":checked")) {
                            updateSubmitButton('candidate');
                            $("#candidateDetails, #submitButton, #directSubmitButton, #candidateSubmitButton, #studentSubmitButton, #studentDetails").addClass(displayNone);
                            $("#candidateSection").removeClass(displayNone);
                        } else if ($("#direct").is(":checked")) {
                            updateSubmitButton('direct');
                            $("#candidateDetails, #candidateSubmitButton, #studentSubmitButton, #studentSection").addClass(displayNone);
                            $("#directSection, #submitButton, #directSubmitButton").removeClass(displayNone);
                        }
                    });
                });

                $("#backButton").off("click").on("click", function () {
                    $('#studentSubmitButton, #candidateSubmitButton, #directSubmitButton').prop('id', 'submitButton');
                    $("#regularStudentSection, #vacationStudentSection, #studentSection, #candidateDetails," +
                        "#candidateSection, #directSection, #changeSection, " +
                        "#swapSection, #allocateSection, #submitButton, #swapBtn," +
                        "#allocateStudentButton, #backButton, #checkButton, " +
                        "#allocateChangeButton, #checkChangeButton, " +
                        "#deleteSection, #deleteActionButton, #existingStudentHostelDetails, #re-AllocateStudentButton").addClass(displayNone);
                    $(".occupantSection").removeClass(displayNone);
                    $("input[name='studentType']").prop("checked", false);
                });

                $("#swapBtn").off("click").on("click", function () {
                    $("#submitButton").addClass(displayNone);
                });

                $("#appointmentNature").change(function () {
                    if ($(this).val() === "Other") {
                        $("#otherAppointmentNatureDiv").removeClass(displayNone);
                    } else {
                        $("#otherAppointmentNatureDiv").addClass(displayNone);
                    }
                });

            })
            .catch(error =>{
                hideLoader();
                showToast('Error:', `Can't open modal ${error}`)
            });
    }
}

function roomDetailsModal(element) {
    const roomId = element !== null ? element.getAttribute("data-room-id") : '0';
    const baseUrl = element !== null ? element.getAttribute("data-url") : '0';
    const requestURL = `${contextPath}${baseUrl}/${roomId}`;
    showLoader();
    fetch(requestURL, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.text())
        .then(response => {
            $('#roomDetailsModalDiv').html(response);
            hideLoader();
            $('#roomDetailsModal').modal('show');
        })
        .catch(error => showToast('Error:', `Can't able to open modal ${error}`));
}

function checkCandidateExist() {
    let email = $("#email").val().trim();
    let subRoomId = $("#subRoomId").val().trim();
    let hostelId = parseInt($("#selectedHostelId").val().trim());
    let studentId = $("#vacationStudentId").val().trim();
    let selectedDate = new Date($("#selectedDate").text());
    let formattedDate = selectedDate.getFullYear() + '-' +
        String(selectedDate.getMonth() + 1).padStart(2, '0') + '-' +
        String(selectedDate.getDate()).padStart(2, '0');
    let screenType = $("input[name='vacationStudentType']:checked").attr("id").trim();
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const isFieldsValid = validateRadioAndText(requiredFields);
    const isEmailValid = validateEmailMsg($('#email')[0], true);
    if (!isFieldsValid)
        return;
    else if (screenType === 'candidate' && (!isEmailValid))
        return;
    $('#loadingHostelDetails').removeClass(displayNone);
    const requestUrl = `${contextPath}${baseURL}${checkCandidateOrStudent}?email=${encodeURIComponent(email)}&subRoomId=${encodeURIComponent(subRoomId)}
    &hostelId=${hostelId}&screenType=${encodeURIComponent(screenType)}
    &studentId=${encodeURIComponent(studentId)}&selectedDate=${encodeURIComponent(formattedDate)}`;

    fetch(requestUrl, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            $('#loadingHostelDetails').addClass(displayNone);
            if (data) {
                console.log(data);
                if (data.error === null) {
                    console.log(data);
                    data.subRoomId = subRoomId;
                    console.log(data.subRoomId);
                    updateDetails(screenType, data);
                } else {
                    $(`#${screenType}SubmitButton, #${screenType}Details`).addClass(displayNone);
                    showToast("Error:", data.error);
                }
            } else {
                $('#loadingHostelDetails').addClass(displayNone);
                throw new Error("Invalid response format: Data is null or undefined");
            }
        })
        .catch(error => {
            $('#loadingHostelDetails').addClass(displayNone);
            console.error("Fetch error:", error);
            showToast("Error:", "An error occurred while fetching candidate details.");
        });
}

function updateDetails(screenType, data) {
    let prefix = screenType === 'student' ? "#student" : "#candidate";
    let categoryText = 'N/A';
    let formattedStayFrom = data.stayFromDate
        ? new Date(data.stayFromDate).toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' })
        : 'N/A';
    let formattedStayTo = data.stayToDate
        ? new Date(data.stayToDate).toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' })
        : 'N/A';
    let dob = data.dob
        ? new Date(data.dob).toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' })
        : 'N/A';

    $(`${prefix}SubmitButton, ${prefix}Details`).removeClass(displayNone);
    $(`${prefix}Name`).text(data.studentName === 'null null' ? 'N/A' : data.studentName);
    $(`${prefix}Email`).text(data.email || 'N/A');
    if (data.category !== null) {
        categoryText = (data.category === 'Other' || data.category === 'OTHER')
            ? `${data.category} ( ${data.otherCategory} )`
            : data.category;
    }
    $(`${prefix}Category`).text(categoryText);
    $(`${prefix}StayFromDate`).text(formattedStayFrom);
    $(`${prefix}StayToDate`).text(formattedStayTo);
    $(`${prefix}Dob`).text(dob);
    $('#requestId').text(data.requestId);
    $('#subRoomId').text(data.subRoomId);
    $('#studentId').text(data.studentId);
    $(`${prefix}Gender`).text(data.gender);
    $(`${prefix}Dining`).text(data.dining === true ? 'Yes' : 'No');
}


function saveCandidateDetails() {
    let email = $("#email").val().trim();
    let candidateName = $("#candidateName").text();
    let category = $("#candidateCategory").text();
    let stayFromDate = new Date($("#candidateStayFromDate").text().trim());
    let formattedStayFromDate = stayFromDate.getFullYear() + '-' +
        String(stayFromDate.getMonth() + 1).padStart(2, '0') + '-' +
        String(stayFromDate.getDate()).padStart(2, '0');

    let stayToDate = new Date($("#candidateStayToDate").text().trim());
    let formattedStayToDate = stayToDate.getFullYear() + '-' +
        String(stayToDate.getMonth() + 1).padStart(2, '0') + '-' +
        String(stayToDate.getDate()).padStart(2, '0');

    console.log(formattedStayToDate);
    let dining = $("#candidateDining").text() === 'Yes' ? 'Y' : 'N';
    let hostelId = $("#selectedHostelId").text();
    let roomId = $("#roomId").text();
    let subRoomId = $("#subRoomId").val().trim();
    let requestId = $("#requestId").text();
    let gender = $("#candidateGender").text();
    let dob = new Date($("#candidateDob").text()).toISOString().split('T')[0];
    let screenType = $("input[name='vacationStudentType']:checked").attr("id");

    let candidateData = {
        email: email,
        studentName: candidateName,
        dob: dob,
        natureOfAppointment: category,
        stayFromDate: formattedStayFromDate,
        stayToDate: formattedStayToDate,
        diningRequired: dining,
        hostelId: hostelId,
        roomId: roomId,
        subRoomid: subRoomId,
        requestid: requestId,
        gender: gender,
        screenType: screenType
    };
    console.log(candidateData);
    saveFetchFunction(candidateData, screenType);
}

function saveStudentDetails() {
    let email = $("#studentEmail").text();
    let candidateName = $("#studentName").text();
    let category = $("#studentCategory").text();

    let stayFrom = $("#studentStayFromDate").text().trim();
    stayFrom = stayFrom ? formatDate(stayFrom) : null;

    let stayTo = $("#studentStayToDate").text().trim();
    stayTo = stayTo ? formatDate(stayTo) : null;

    let dining = $("#studentDining").text() === 'Yes' ? 'Y' : 'N';
    let hostelId = $("#selectedHostelId").text();
    let roomId = $("#roomId").text();
    let subRoomId = $("#subRoomId").val().trim();
    let requestId = $("#requestId").text();
    let studentId = $("#vacationStudentId").val().trim();
    let gender = $("#studentGender").text();
    let dob = $("#studentDob").text().trim();
    dob = dob ? new Date(dob) : null;
    let screenType = $("input[name='vacationStudentType']:checked").attr("id");

    let candidateData = {
        email: email,
        studentName: candidateName,
        dob: dob,
        natureOfAppointment: category,
        stayFromDate: stayFrom,
        stayToDate: stayTo,
        diningRequired: dining,
        hostelId: hostelId,
        roomId: roomId,
        subRoomid: subRoomId,
        studentId: studentId,
        requestid: requestId,
        gender: gender,
        screenType: screenType
    };
    saveFetchFunction(candidateData, screenType);
}

function saveDirectDetails() {

    if(!validateDirectDetails())
        return;

    let email = $("#directEmail").val().trim();
    let studentName = $("#directStudentName").val();
    let studentId = $("#directStudentId").val();
    let dob = new Date($("#directDOB").val()).toISOString().split('T')[0];
    let gender = $("input[name='gender']:checked").attr("id") === 'male' ? 'M' : 'F';
    let category = $("#appointmentNature").val();
    let stayFrom = new Date($("#directStayFrom").val()).toISOString().split('T')[0];
    let stayTo = new Date($("#directStayTo").val()).toISOString().split('T')[0];
    let dining = $("#diningRequired").val() === 'yes' ? 'Y' : 'N';
    let hostelId = parseInt($("#selectedHostelId").val().trim());
    let roomId = $("#roomId").text();
    let subRoomId = $("#subRoomId").val().trim();
    let requestId = $("#requestId").text();
    let screenType = $("input[name='vacationStudentType']:checked").attr("id");

    if ($('#appointmentNature').val() === "Other" && $('#otherAppointmentNature').val().trim() !== '') {
        category += ' (' + $('#otherAppointmentNature').val().trim() + ')';
    }

    let directData = {
        email: email,
        studentName: studentName,
        dob: dob,
        natureOfAppointment: category,
        stayFromDate: stayFrom,
        stayToDate: stayTo,
        diningRequired: dining,
        hostelId: hostelId,
        roomId: roomId,
        subRoomid: subRoomId,
        requestid: requestId,
        studentId: studentId,
        gender: gender,
        screenType: screenType
    };
    saveFetchFunction(directData, screenType);
}

function formatDate(dateStr) {
    if (!dateStr) return null;
    let d = new Date(dateStr);
    let year = d.getFullYear();
    let month = String(d.getMonth() + 1).padStart(2, '0');
    let day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function validateDirectDetails() {
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const emailID = $('#directEmail');
    const dateRanges = [
        {fromElement: "directStayFrom", toElement: "directStayTo"}
    ];
    const isFieldsValid = validateRadioAndText(requiredFields);
    let isDateRangeValid = validateBetweenDateRanges(dateRanges);
    const isEmailValid = validateEmailMsg(emailID[0], true);
    return isFieldsValid && isEmailValid && isDateRangeValid;
}

function saveFetchFunction(candidateData, screenType){
    fetch(`${contextPath}${baseURL}${saveStudent}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(candidateData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text();
        })
        .then(message => {
            let [user, status] = message.split("-");
            let isSaved = status === 'saved';
            let screenType = user.trim() === 'candidate' ? 'Candidate' : 'Student';
            showToast(isSaved ? "Success" : "Error", isSaved ?
                (screenType + ' Information Saved Successfully!') : message);
            if (isSaved){
                setTimeout(function() {
                    location.reload();
                }, 1500);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            showToast("Error:", `An error occurred while saving ${screenType} details.`);
        });
}

function toggleChangeStudent(element) {
    $('#selectedSubRoomId').val(element.id.replace('_changeButton', ''));
    $('#changeStudentId').html(element.getAttribute("data-student-id"))
    loadHostelForChange();
}

function toggleDeleteStudent(element) {
    const studentType = $('#allocationType').val();
    const studentId = element.getAttribute("data-student-id");
    $('#selectedSubRoomId').val(element.id.replace('_deleteButton', ''));
    if ((studentType === 'Student' || studentType === 'StudentApp') && studentId !== '')
        $('#deleteStudentIdOrEmail').html(studentId);
    else {
        $('#deleteStudentIdOrEmail').html(element.getAttribute("data-email-id"));
        $('#deleteStudentIdOrEmailLabel').html('Email Id');
    }
}

function deallocate() {
    const studentIdOrEmail = $('#deleteStudentIdOrEmail');
    const studentType = $('#allocationType').val();
    const roomAllotmentId = $('#roomAllotmentId').val();

    $.ajax({
        url:contextPath + baseURL + deallocateURL,
        type:'POST',
        data: {
            'screenType' : 'delete',
            'studentType' : studentType,
            'roomAllotmentId' : roomAllotmentId,
            'currentStudentId' : studentIdOrEmail.html(),
            'emailId' : studentIdOrEmail.html(),
        },
        success: function (response) {
            if(response){
                showSuccessToast('Allocation details deleted successfully.')
            } else{
                showToast('Error','Failed To delete the Allocation details');
            }
        },
        error: function (error) {
            showToast('Error','Failed To delete the Allocation details');
        }
    });

}

function loadHostelForChange() {
    const selectHostelList = $('#hostelNameOption');
    fetch(contextPath + baseURL + getHostelListURL)
        .then(response => response.json())
        .then(responseJson => {
            if (Array.isArray(responseJson)) {
                selectHostelList.empty();
                selectHostelList.append('<option value="0">' + hostelSelectText + '</option>');
                $.each(responseJson, function (index, hostel) {
                    selectHostelList.append('<option value="' + hostel.id + '">' + hostel.hostelName + '</option>');
                });
                loadFloorForChange();
            }
        });
    selectHostelList.empty();
    selectHostelList.append('<option value="0">' + hostelSelectText + '</option>');
    loadFloorForChange();
}

function loadFloorForChange() {
    const selectHostelList = $('#hostelNameOption');
    const selectFloorList = $('#floorName');
    if (selectHostelList.val() !== '0') {
        fetch(contextPath + baseURL + getFloorListURL + '/' + selectHostelList.val())
            .then(response => response.json())
            .then(responseJson => {
                if (Array.isArray(responseJson)) {
                    selectFloorList.empty();
                    selectFloorList.append('<option value="0">' + floorSelectText + '</option>');
                    $.each(responseJson, function(index, floor) {
                        selectFloorList.append('<option value="' + floor.id + '">' + floor.floorName + '</option>');
                    });
                    loadRoomForChange();
                }
            });
    } else {
        selectFloorList.empty();
        selectFloorList.append('<option value="0">' + hostelSelectText + ' ' + firstText + '</option>');
        loadRoomForChange();
    }
}

function loadRoomForChange() {
    const selectHostelList = $('#hostelNameOption');
    const selectFloorList = $('#floorName');
    const selectRoomList = $('#roomNo');
    if (selectHostelList.val() !== '0' && selectFloorList.val() !== '0') {
        fetch(contextPath + baseURL + getRoomListURL + '/' + selectHostelList.val() + '/' + selectFloorList.val())
            .then(response => response.json())
            .then(responseJson => {
                if (Array.isArray(responseJson)) {
                    selectRoomList.empty();
                    selectRoomList.append('<option value="0">' + roomSelectText + '</option>');
                    $.each(responseJson, function(index, room) {
                        selectRoomList.append('<option value="' + room.id + '" data-capacity="' + room.capacity + '">' + room.roomNo + '</option>');
                    });
                    loadSeatForChange();
                }
            });
    } else {
        selectRoomList.empty();
        selectRoomList.append('<option value="0">' + floorSelectText + ' ' + firstText + '</option>');
        loadSeatForChange();
    }
}

function loadSeatForChange() {
    const selectHostelList = $('#hostelNameOption');
    const selectFloorList = $('#floorName');
    const selectRoomList = $('#roomNo');
    const selectSeatList = $('#seatName');
    selectSeatList.empty();
    if (selectHostelList.val() !== '0' && selectFloorList.val() !== '0' && selectRoomList.val() !== '0') {
        const roomOptionSelected = selectRoomList.find(':selected');
        const roomCapacity = roomOptionSelected.attr('data-capacity');
        selectSeatList.append('<option value="0">' + seatSelectText + '</option>');
        let character = 'A'.charCodeAt(0);
        for (let i= 0; i< roomCapacity; i++) {
            let char = String.fromCharCode(character++);
            selectSeatList.append('<option value="' + char + '">' + char + '</option>');
        }
    } else {
        selectSeatList.append('<option value="0">' + roomSelectText + ' ' + firstText + '</option>');
    }
}

function resetAllocateButton() {
    $('#allocateChangeButton').addClass(dNone);
    $('#changeHostelStatus').removeClass(validClass).removeClass(errorClass);
}

function checkForChange(jElement) {
    jElement.attr('disabled', true);
    const selectHostelList = $('#hostelNameOption');
    const selectFloorList = $('#floorName');
    const selectRoomList = $('#roomNo');
    const selectSeatList = $('#seatName');
    const studentId = $('#changeStudentId').html();
    if (selectHostelList.val() !== '0' && selectFloorList.val() !== '0' && selectRoomList.val() !== '0' && selectSeatList.val() !== '0') {
        $.ajax({
            url:contextPath + baseURL + checkHostelOccupancyURL,
            type:'GET',
            data:{
                'hostelId': $('#selectedHostelId').val() ,
                'floorId': $('#selectedFloorId').val(),
                'roomId': $('#selectedRoomId').val(),
                'subRoomId': $('#selectedSubRoomId').val(),
                'newHostelId': selectHostelList.val() ,
                'newFloorId': selectFloorList.val(),
                'newRoomId': selectRoomList.val(),
                'newSubRoomId': selectSeatList.val(),
                'screenType': 'change',
                'currentStudentId':studentId,
            },
            success: function (response) {
                console.log(response);
                const status = $('#changeHostelStatus');
                const allocate = $('#allocateChangeButton');
                status.removeClass(validClass).removeClass(errorClass);
                allocate.removeClass(dNone);
                if(response) {
                    if (!response['isGenderCorrect']) {
                        showToast('Error', `In this hostel, ${response['gender']} student cannot be allocated`);
                        jElement.attr('disabled', false);
                        allocate.addClass(dNone);
                        return;
                    }
                    if (response['roomAllotmentId'] > 0) {
                        status.addClass(errorClass);
                        allocate.find("span").html('Force-Allocate');
                    } else {
                        status.addClass(validClass);
                        allocate.find("span").html('Allocate');
                    }
                } else {
                    allocate.addClass(dNone);
                }
                jElement.attr('disabled', false);
            },
            error: function (error) {
                console.log(error);
                jElement.attr('disabled', false);
            }
        });
    } else {
        jElement.attr('disabled', false);
    }
}

function allocateForChange() {
    const studentId = $('#changeStudentId').html();
    const selectHostelList = $('#hostelNameOption');
    const selectFloorList = $('#floorName');
    const selectRoomList = $('#roomNo');
    const selectSeatList = $('#seatName');
    if (selectHostelList.val() !== '0' && selectFloorList.val() !== '0' && selectRoomList.val() !== '0' && selectSeatList.val() !== '0') {
        $.ajax({
            url:contextPath+baseURL + allocateURL,
            type:'POST',
            data:{
                'hostelId': selectHostelList.val() ,
                'floorId': selectFloorList.val(),
                'roomId': selectRoomList.val(),
                'subRoomId': selectSeatList.val(),
                'screenType': 'change',
                'currentStudentId':studentId,
            },
            success: function (response) {
                if(response){
                    showSuccessToast('Change of hostel update successfully.')
                } else{
                    showToast('Error','Failed To change the hostel details');
                }
            },
            error: function (error) {
                showToast('Error','Failed To change the hostel details');
            }
        });
    }
}

function toggleSwapStudents(element){
    let currentStudentId = element.getAttribute("data-student-id");
    $('#currentStudentId').text(currentStudentId);
    $("#allocateStudentButton, #checkButton, #changeSection, #occupantSection, #allocateSection, #submitButton").addClass(displayNone);
    $("#backButton, #swapSection").removeClass(displayNone);
}

function validateStudentDetails(student,roomId, date, type) {
    const studentId = $('#'+student).val();
    const subRoomId = $('#subRoomId').val();
    const hostelId = $('#selectedHostelId').val();
    const floorId = $('#selectedFloorId').val();
    if ($('#existingStudentHostelDetails').hasClass(displayNone)) {
        $('#loadingHostelDetails').removeClass(displayNone);
    }
    if (studentId !== null && studentId !== '' && studentId.trim().length > 0) {
        $('#'+student).removeClass(validClass);
        $.ajax({
            url: contextPath + baseURL + validateSwapDetails,
            type: 'GET',
            data: {
                'hostelId':hostelId,
                'floorId':floorId,
                'roomId': roomId,
                'subRoomId': subRoomId,
                'screenType': type,
                'newStudentId':studentId
            },
            success: function (response) {
                $('#loadingHostelDetails').addClass(displayNone);
                if(response.hasPrivilege) {
                    if (type === 'swap' && response.status === 'exist') {
                        toggleCheck(true, 'checkBtn1');
                        $('#exHostelName').text(response.hostelName);
                        $('#exSubRoom').text(response.subRoomId);
                        $('#exFloorName').text(response.floorName);
                        $('#exRoomNo').text(response.roomNo);
                        $('#existingStudentHostelDetails').removeClass(displayNone);
                        $('#swapStudentMsg').removeClass(displayNone);
                        $("#swapBtn").removeClass(displayNone);
                        $("#re-AllocateStudentButton, #reAllocateStudentMsg").addClass(displayNone);
                    }
                    else if(type === 'regular'){
                        toggleCheck(true, 'checkBtn2');
                        $("#swapBtn, #swapStudentMsg").addClass(displayNone);
                        if(response.status === 'alreadyAllotted'){
                            $('#existingStudentHostelDetails').addClass(displayNone);
                            $('#reAllocateStudentMsg').addClass(displayNone);
                            $('#re-AllocateStudentButton').addClass(displayNone);
                            $('#allocateStudentButton').addClass(displayNone);
                        }
                        else if(response.status === 'exist'){
                            $('#exHostelName').text(response.hostelName);
                            $('#exSubRoom').text(response.subRoomId);
                            $('#exFloorName').text(response.floorName);
                            $('#exRoomNo').text(response.roomNo);
                            $('#existingStudentHostelDetails').removeClass(displayNone);
                            $('#reAllocateStudentMsg').removeClass(displayNone);
                            $('#re-AllocateStudentButton').removeClass(displayNone);
                            $('#allocateStudentButton').addClass(displayNone);
                        }
                        else{
                            $('#reAllocateStudentMsg').addClass(displayNone);
                            $('#re-AllocateStudentButton').addClass(displayNone);
                            $('#allocateStudentButton').removeClass(displayNone);

                        }

                    }
                    else {
                        $('#existingStudentHostelDetails').addClass(displayNone);
                        $("#swapBtn").addClass(displayNone);
                        showToast('Error:', response.errorMessage);
                    }
                }
                else{
                    showToast('Error:', response.errorMessage);
                }
            },
            error: function (error) {
                $('#loadingHostelDetails').addClass(displayNone);
                showTable('Error:', error.message);
            }
        });
    }
    else{
        $('#'+student).addClass(errorClass);
    }
}

function roomNumberModal() {
    $('#roomNumberModalDiv').load(contextPath + '/dev/hostel-individual-allotment-room-details-modal.html', function() {
        $('#roomNumberModal').modal('show');
        validateForm();
    });
}

function getRoomList(url) {
    const baseUrl = url.getAttribute('data-url');
    let hostelId = $('#hostelIdList').val();
    let floorId = $('#floorNameList').val();
    const requiredFields = $('input[required]:visible, select[required]:visible, textarea[required]:visible');
    const isFieldsValid = validateRadioAndText(requiredFields);
    let occupancyStatus = $('#occupancyStatus').val();
    let separator = String(baseURL).includes("?") ? "&" : "?";
    let queryString = `hostelId=${encodeURIComponent(hostelId)}&occupancyStatus=${encodeURIComponent(occupancyStatus)}&floorId=${encodeURIComponent(floorId)}`;
    if (isFieldsValid) {
        window.location.replace(`${contextPath}${baseUrl}${separator}${queryString}`);
    } else {
        return false;
    }
}

function toggleCheck(status,btn){
    if(status){
        $('#'+btn).prop('disabled',false)
    }
    else{
        $('#'+btn).prop('disabled',true)
    }
}

function swapStudents(){
    let currentStudentId = $('#currentStudentId').text().trim();
    let newStudentId = $('#studentId1').val().trim();
    const swapBtn = $('#swapBtn');
    const backButton = $('#backButton');
    const occupiedModalCancel = $('#occupiedModalCancel');
    swapBtn.attr('disabled', true);
    backButton.attr('disabled', true);
    occupiedModalCancel.attr('disabled', true);
    $.ajax({
        url: contextPath + baseURL + swapURL,
        type:'POST',
        data: {
          'currentStudentId':currentStudentId,
          'newStudentId':newStudentId
        },
        success: function (response) {
            if(response!==null && response!=='' && response){
                showSuccessToast('Swapped Students Successfully!');
            }
            else{
                swapBtn.attr('disabled', false);
                backButton.attr('disabled', false);
                occupiedModalCancel.attr('disabled', false);
                showToast('Error','Failed To Swapped Students');
            }
        },
        error: function (error) {
            swapBtn.attr('disabled', false);
            showTable('Error:', error.message);
        }
    });
}

function showSuccessToast(msg){
    showToast('Success',msg);
    setTimeout(function (){
        window.location.reload();
    },3000);
}

function setSubRoomId(element){
    let subRoom = element.getAttribute('data-sub-room-id');
    $('#subRoomId').val(subRoom);
    console.log(subRoom);
}

function allocateStudent(type,roomId){
    let currentStudentId = $('#studentId2').val().trim();
    const subRoomId = $('#subRoomId').val();
    const hostelId = $('#selectedHostelId').val();
    const floorId = $('#selectedFloorId').val();
    const allocateStudentButton = $('#allocateStudentButton');
    const backButton = $('#backButton');
    const occupiedModalCancel = $('#occupiedModalCancel');
    allocateStudentButton.attr('disabled', true);
    backButton.attr('disabled', true);
    occupiedModalCancel.attr('disabled', true);
    $.ajax({
        url:contextPath+baseURL + allocateURL,
        type:'POST',
        data:{
            'hostelId':hostelId,
            'floorId':floorId,
            'roomId': roomId,
            'subRoomId': subRoomId,
            'screenType': type,
            'currentStudentId':currentStudentId,
        },
        success: function (response) {
            if(response && response === true){
                showSuccessToast('Allocated Student Successfully!');
            }
            else{
                allocateStudentButton.attr('disabled', false);
                backButton.attr('disabled', false);
                occupiedModalCancel.attr('disabled', false);
                showToast('Error','Failed To Allocate Student');
            }
        },
        error: function (error) {

        }
    });
}

function getHostelFloorList(hostelInputId, id, url) {
    const baseUrl = url.getAttribute('data-url');
    const hostelId = $('#' + hostelInputId).val();
    if (!hostelId) {
        return;
    }
    fetch(contextPath + baseUrl + getFloorURL + "/" + hostelId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw err;
            }); // Handle errors
        }
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return response.json();
        } else {
            return response.text().then(txt => {
                throw new Error("Expected JSON but got: " + txt);
            });
        }
    }).then(responseJson => {
        if (Array.isArray(responseJson)) {
            const $select = $('#' + id);
            $select.empty();
            $select.append('<option value="">Select the Floor Name</option>');

            $.each(responseJson, function(index, floor) {
                $select.append('<option value="' + floor.id + '">' + floor.floorName + '</option>');
            });
        }

    });
}


