$(document).ready(function () {
    const addNew = $("#addNewId");
    addNew.html('<i class="fa-solid fa-circle-plus me-1"></i> <span>Top up Amount</span>');
    addNew.on('click', function (e) {
        topUpAmountAdd();
    });

    $('#uploadFileButton').click(function () {
        validateFile();
    });
});

function saveFoodCourtList(){
    const messPeriod = $('#messPeriod');
    const insertAllModal = $('#insertAllModal');
    $(insertAllModal).modal('show');
    $('#insertAllModalPositive').off("click").on("click", function () {
        $.ajax({
            url: `${contextPath}${baseURL}${saveFoodCourtURL}?messPeriod=${messPeriod.val()}`,
            type: 'POST',
            success: function (response) {
                if (response === 'saved') {
                    showToast('Success', 'Food Court Details Saved Successfully!');
                    setTimeout(function () {
                        location.reload();
                    }, 2000);
                } else {
                    showToast("Error", response);
                }
            },
            error: function (error) {
                showToast('Error', error.responseText);
            }
        });
    });
}

function topUpAmountAdd() {
    fetch(`${contextPath}${baseURL}${getURL}`, {
        method: 'GET',
        headers : {
            'Content-Type': 'application/json'
        }
    }).then(res => {
        return res.text();
    }).then(res => {
        $('#modalDiv').html(res)
        $('#foodCourtTopUpModal').modal('show');
        $("#foodCourtTopUpModalSave").off('click').on('click', function (e) {
            const requiredFields = $('input[required], select[required], textarea[required]');
            const isValid = validateRadioAndText(requiredFields);
            if (isValid) {
                $('#foodCourtTopUpForm').submit();
            } else {
                return false;
            }
        });
    }).catch(error =>{
       showToast('Error', error.error());
    });
}

function checkStudentExist(){
    $.ajax({
        url: `${contextPath}${baseURL}${checkStudentURL}?studentId=${$('#studentId').val()}`,
        type: 'GET',
        contentType: "application/json",
        success: function (response) {
            const studentId = $('#studentId');
            let errorDiv = studentId.siblings('.invalid-feedback');
            console.log(response)
            if (response.studentId && response.foodCourtLedgerDto) {
                $('#studentName').val(response.firstName + ' ' + response.lastName);
                studentId.addClass(validClass).removeClass(errorClass);
                $('#studentDetailsSection').removeClass(displayNone);
                $('#messName').text(response.foodCourtLedgerDto.messName);
                $('#currentBalance').text(parseFloat(response.foodCourtLedgerDto.amount).toFixed(2));
            } else {
                if (studentId.val() === ''){
                    $('#studentDetailsSection').addClass(displayNone);
                    studentId.addClass(errorClass).removeClass(validClass);
                    errorDiv.html(errorDiv.val());
                } else if (!response.studentId) {
                    $('#studentDetailsSection').addClass(displayNone);
                    studentId.addClass(errorClass).removeClass(validClass);
                    errorDiv.html(errorDiv.siblings('.invalid-exist-feedback').html());
                    $('#studentName').val('');
                    showToast("Error", 'Student ID doesn\'t exist.');
                } else if (!response.foodCourtLedgerDto) {
                    $('#studentDetailsSection').addClass(displayNone);
                    $('#studentName').val('');
                    studentId.removeClass(validClass);
                    errorDiv.html(errorDiv.siblings('.invalid-mess-period-exist-feedback').html());
                    showToast("Error", 'This student does not exist in the Food Court for the current mess period');
                }
            }
        },
        error: function (error) {
            showToast('Error', error.responseText);
        }
    });
}

function validateFile() {
    const fileInput = $('#uploadFile1_fileUpload');
    const errorDiv = fileInput.closest('#documentUploadSection').find('.invalid-feedback');
    if (!fileInput.val()) {
        errorDiv.text('Please select a file before submitting.').show();
    } else {
        errorDiv.text('').hide();
        $('#foodCourtUploadForm').submit();
    }
}

function getSelectedPeriod() {
    const messPeriod = $('#messPeriod').val();
    if (messPeriod === null || messPeriod === 0 || messPeriod === '') {
        showToast('Error','Please select a Mess Period');
        return;
    }
    window.location.href = `${contextPath}${baseURL}?id=${messPeriod}`;
}