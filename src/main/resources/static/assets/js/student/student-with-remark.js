$(document).ready(function () {
    $('#addNewId').removeAttr('disabled');

    if (modalError === true) {
        editStudentWithRemarkDetails(null);
    }

    $('#addNewId, #editButton').click(function () {
        editStudentWithRemarkDetails(null);
    });

    $('#deleteButton').click(function () {
        deleteStudentWithRemark();
    });

    $('#revokeBlackListButton').click(function () {
        revokeBlackListing();
    });

    $('#uploadFileButton').click(function () {
        validateFile();
    });
});

function editStudentWithRemarkDetails(element) {
    const studentRemarkId = element !== null ? element.getAttribute("data-student-remark-id") : 0;
    showLoader();
    fetch(contextPath + baseURL + getURL + "/" + studentRemarkId
        , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.text();
    }).then(response => {
        $('#modalDiv').html(response);
        hideLoader();
        $('#student-with-remark-modal').modal('show');
        updateSaveButtonStyle(studentRemarkId, $('#student-with-remark-modalSave'));
        if (studentRemarkId !== 0) {
            const $studentNameInput = $('#studentName');
            const $studentIdInput = $('#studentId');
            $studentNameInput.removeAttr('hidden');
            $studentNameInput.attr('required', 'required');
            $studentNameInput.attr('readonly', 'readonly');
            $studentIdInput.attr('readonly', 'readonly');
        }
        $("#student-with-remark-modalSave").off("click").on("click", async function (e) {
            e.preventDefault();
            updateInvalidDivClass();
            const saveButton = $(this);
            const requiredFields = $('input[required], select[required], textarea[required]');
            let status = valRequiredMultiTextRadio(requiredFields);
            const dateRanges = [
                {fromElement: "fromDate", toElement: "toDate"}
            ];
            const dateValidationStatus = validateBetweenDateRanges(dateRanges);
            const studentId = $('#studentId');
            const errorDiv = studentId.siblings('.invalid-feedback');
            const isUpdate = element === null;
            const isValidStudentId = await validateStudentId(studentId.val(), studentId, errorDiv, isUpdate);
            if (status && dateValidationStatus && isValidStudentId) {
                saveButton.attr('disabled', true);
                $('#student-with-remark-form').submit();
            } else {
                return false;
            }
        });

        $("#student-with-remark-modalValidateBackend").off("click").on("click", function (e) {
            $('#student-with-remark-modal-form').submit();
        })

    });
}

function deleteStudentWithRemark(element) {
    const studentRemarkId = element !== null ? element.getAttribute("data-student-remark-id") : 0;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').click(function () {
        $('#deleteModal').modal('hide');
        fetch(contextPath + baseURL + deleteURL + "/" + studentRemarkId
            , {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            if (response.status === 'Success') {
                $('#deleteStudentRemark_' + studentRemarkId).parents('tr').remove();
                showToast('Success', 'Student With Remark deleted successfully.');
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    })
}

function revokeBlackListing(element) {
    const studentRemarkId = element !== null ? element.getAttribute("data-student-remark-id") : 0;
    $('#warningModal').modal('show');
    $('#warningModalPositive').click(function () {
        $('#warningModal').modal('show');
        fetch(contextPath + baseURL + revokeURL + "/" + studentRemarkId
            , {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
            return response.json();
        }).then(response => {
            if (response.status === 'Success') {
                let revokedStudentRemarkId = $('#revokedStudentRemark_' + studentRemarkId);
                let row = revokedStudentRemarkId.closest('tr');
                row.find('td:nth-child(6)').text('Revoked');
                let currentDate = new Date().toLocaleDateString('en-US', {
                    month: 'short',
                    day: '2-digit',
                    year: 'numeric'
                });
                row.find('td:nth-child(7)').text(currentDate);
                revokedStudentRemarkId.addClass(displayNone);
                showToast('Success', 'Student With Remark revoked from blacklisting successfully.');
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#warningModalPositive').off('click');
    })
}

function validateFile() {
    const fileInput = $('#uploadFile1_fileUpload');
    const errorDiv = fileInput.closest('#documentUploadSection').find('.invalid-feedback');
    if (!fileInput.val()) {
        errorDiv.text('Please select a file before submitting.').show();
    } else {
        errorDiv.text('').hide();
        $('#studentRemarkUploadForm').submit();
    }
}

async function validateStudentId(studentIdVal, studentId, errorDiv, isUpdate) {
    try {
        const fromDate = $('#fromDate').val();
        const toDate = $('#toDate').val();
        const response = await fetch(`${contextPath + baseURL}/${studentIdVal}?fromDate=${fromDate}&toDate=${toDate}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });
        if (!studentIdVal || studentIdVal === '') {
            studentId.addClass(errorClass);
            errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html()).removeClass(displayNone);
            return false;
        } else if (response.ok) {
            const data = await response.json();
            if (!studentIdVal.trim()) {
                studentId.addClass(errorClass);
                errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html()).removeClass(displayNone);
                return false;
            }else if (data.studentIDNotExist) {
                studentId.addClass(errorClass);
                console.log('Id Not Exist')
                errorDiv.html(errorDiv.siblings('.invalid-exist-feedback').html()).removeClass(displayNone);
                return false;
            }else if (data.hasPreviousId) {
                studentId.addClass(errorClass);
                errorDiv.html(errorDiv.siblings('.invalid-prev-feedback').html()).removeClass(displayNone);
                return false;
            }else if (data.hasOverlappingDates && isUpdate) {
                studentId.addClass(errorClass);
                errorDiv.html(errorDiv.siblings('.invalid-date-feedback').html()).removeClass(displayNone);
                return false;
            }else{
                studentId.removeClass(errorClass);
                errorDiv.html('').addClass(displayNone);
                return true;
            }
        } else {
            if (!studentIdVal.trim()) {
                studentId.addClass(errorClass);
                errorDiv.html(errorDiv.siblings('.invalid-req-feedback').html()).removeClass(displayNone);
                return false;
            } else {
                console.log('empty input.')
                showToast('Failure', 'Failed to validate student ID. Please enter a proper student ID.');
                return false;
            }
        }
    } catch (error) {
        showToast('Failure', 'Error while validating student ID: ' + error);
        return false;
    }
}


