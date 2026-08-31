const selectedBulkIds = new Set();
jQuery(document).ready(function() {
	let filters = $("#filters").val();
	if (filters.includes("year=") && document.getElementById('year') != null) {
		const selectElement = document.getElementById('year');
		const currentYear = new Date().getFullYear();
		const earliestYear = 2010; // Optional: Define starting year
		for (let year = currentYear; year >= earliestYear; year--) {
			const option = document.createElement('option');
			option.text = year;
			option.value = year;
			selectElement.appendChild(option);
		}
		var yearValue = document.getElementById('yearId').value;
		selectElement.value = (yearValue != '' && yearValue != null) ? yearValue : "";
	}
	$('#additionalButton').click(function() {
		checkAdvanceFilter('downloadExcel');
	});

	$('#additionalButton2').click(function() {
		let url = $('#addNewUrl').val();
		if(url.includes('hdcComplaint')) {
			checkAdvanceFilter('hdcComplaint');						
		}else{
			checkAdvanceFilter('todayVacatingList');
		}
	});
	
	$('#addNewId').click(function() {
        addNewRecord();
    });

	const allowedPages = [contextPath + "/studentVacating"];
	const currentPath = window.location.pathname;
	if (allowedPages.includes(currentPath)) {
		$("#roomInventorySection").removeClass(displayNone);
	} else {
		$("#roomInventorySection").addClass(displayNone);
	}

	$(document).on('change', '.bulk-checkbox', function () {
		const fullUrl = $(this).attr('data-url');
		if (!fullUrl) return;

		const encryptedId = fullUrl.split('/').pop();

		if ($(this).is(':checked')) {
			selectedBulkIds.add(encryptedId);
		} else {
			selectedBulkIds.delete(encryptedId);
		}
		toggleBulkApproveRejectButtons();
	});

	$('#deanDashboardTable').on('draw.dt', function () {
		$('.bulk-checkbox').each(function () {
			const fullUrl = $(this).attr('data-url');
			if (!fullUrl) return;

			const encryptedId = fullUrl.split('/').pop();
			$(this).prop('checked', selectedBulkIds.has(encryptedId));
		});
	});

	function toggleBulkApproveRejectButtons() {
		const checkedCount = $('.bulk-checkbox:checked').length;

		if (checkedCount > 0) {
			$('#bulkApproveRejectButtons').removeClass('d-none');
		} else {
			$('#bulkApproveRejectButtons').addClass('d-none');
		}
	}
});
function addNewRecord() {
    const url = contextPath + "/" +  $('#addNewUrl').val();
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#addNewRecordModal'));
            modal.show();

            // Handle Save button click
            $('#addNewRecordModalSave').on('click', function (e) {
                e.preventDefault();
                let isFormValid = true;
                //validateAndSubmitGuestCouponForm();
                 $('.required-input').each(function () {
                    if (!validateField($(this))) isFormValid = false;
                });

                if (isFormValid) {
                    $("#addNewRecordModalSave").attr('disabled', false);
                    $('#addNewForm').submit();
                } else {
                    showToast('Error', messages.invalidForm);
                }
            });
        })
        .catch(error => {
            showToast('Error', messages.errorLoadingModal);
        });
}
function validateField($field) {
    const value = $field.val();
    const isValid = !!value.trim();
    const errorDiv = $field.siblings('.invalid-feedback');

    if (isValid) {
        $field.removeClass('is-invalid');
    } else {
        $field.addClass('is-invalid');
    }

    return isValid;
}
function checkAdvanceFilter(status) {
	let filters = $("#filters").val().replace(/[\[\]{}]/g, '').split(',').map(f => f.trim().split('=')[0]);
	filters.forEach(filter => {
		let safeFilter = CSS.escape(filter);
		let inputName = `additionalParam.${filter}`;
		let value = $(`#${filter}`).val() || '';
		$(`input[name="${inputName}"]`).val(value);
	});
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	//Due to search field not available, used a static variable
	$('#search').val("0");
	let search = $('#search').val();
	
	if (status === 'downloadExcel') {
		let url = "/" + $('#excelUrl').val();
		createAndReturnUrlWithParams(page, size, search, url);
	}else if (status === 'todayVacatingList') {
		$('input[name="additionalParam.validationStatus"]').val("CheckedOut");
		$('input[name="additionalParam.stayToDate"]').val(new Date().toISOString().split('T')[0]); //set the current date
		$('input[name="additionalParam.currentDayStayFlag"]').val('vacatingLink');
		createUrlWithParams(page, size, search);
	}else if(status === 'hdcComplaint'){
		let url = "/" + $('#addNewUrl').val();
		window.location.href= contextPath + url;
	}else {
		createUrlWithParams(page, size, search);
	}
}

function handleActionClick(element) {
	const displayName = element !== null ? element.getAttribute("data-bs-title") : null;
	const isNoteRequired = element !== null ? element.getAttribute("data-note-required") : null;
	if (isNoteRequired == 'true' && (displayName === 'Approve With Condition' || displayName === 'Approve' || displayName === 'Reject'
                    ||displayName === 'Reject Reversal')) {
        return statusUpdate(element);
    }
	if (displayName === 'Delete') {
		deleteAction(element);
	} else if (displayName === 'View') {
		viewDetails(element);
	} else if (displayName === 'ViewPopUp') {
     		viewDetailsInModal(element);
    } else if (displayName === 'Resend Mail' || displayName === 'Re-send Mail') {
		resendMailAction(element);
	} else if(displayName === 'Reject'){
		rejectHostelEnrollment(element)
	} else if(displayName === 'Due With Approve' || displayName === 'Override Approve'){
		approveHostelEnrollment(element);
	} else if (displayName === 'Approve'){
		approveAction(element);
	} else if (displayName === 'Download') {
        downloadAction(element);
    } else if (displayName === 'Reject Reversal') {
        statusUpdate(element);
	} else if (displayName === 'Edit') {
        viewDetails(element);
    } else if (displayName === 'PDF') {
		pdfAction(element);
	} else if (displayName === 'Allocate' || displayName === 'Reallocate') {
		allocateOrReallocateStudent(element);
	} else if (displayName === 'Change') {
		changeHostel(element);
	}  else if (displayName === 'Check In' || displayName === 'Check Out') {
		updateCheckInOrCheckOut(element);
	} else if (displayName === 'Send Message') {
        sendMessage(element);
	} else if (displayName === 'View NOC') {
		viewNOCDetails(element);
	}
    else if(displayName === 'View Food Request'){
        viewFoodRequestDetailsInModal(element);
    }
    else if(displayName === 'Accept Request' || displayName === 'Out For Delivery'){
        updateSickFoodRequestStatus(element);
    } else if(displayName === 'View Student Vacating Due'){
        viewVacatingStudentDueDetails(element);
    }
}

// View NOC
function viewNOCDetails(element) {
    const url = element?.getAttribute("data-url");
    if (!url) return;

    const parts = url.split("/").filter(Boolean);
    const endpoint = `/${parts[0]}`;
    const studentID = parts[1];

    const fullUrl = `${contextPath}${endpoint}`;
	
    const form = document.createElement("form");
    form.method = "POST";
    form.action = fullUrl;

    const input1 = document.createElement("input");
    input1.type = "hidden";
    input1.name = "selectedCriteria";
    input1.value = "Mess";

    const input2 = document.createElement("input");
    input2.type = "hidden";
    input2.name = "studentID";
    input2.value = studentID;

    form.appendChild(input1);
    form.appendChild(input2);
    document.body.appendChild(form);

    form.submit();
}

// Delete
function deleteAction(element) {
	const url = element !== null ? element.getAttribute("data-url") : null;
	const id = element !== null ? element.getAttribute("data-id") : null;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: contextPath + "/" + url,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success' || response.status === 'success') {
					deleteTableRow('id_' + id);
				}
				showToast(response.status, response.message);
				setTimeout(function() {
					location.reload();
				}, 2000);
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

// View
function viewDetails(element) {
    toggleRowHighlight(element);

    const url = element !== null ? element.getAttribute("data-url") : null;
	window.location.href = url;
    // fetch(url, {
    //     method: 'GET'
    // })
    // .then(response => {
    //     if (!response.ok) {
    //         throw new Error('Network response was not ok');
    //     }
    //     return response.text(); // Assuming the response is HTML
    // })
    // .then(html => {
    //     document.open();
    //     document.write(html);
    //     document.close();
    //     setTimeout(() => {
    //             highlightRow();
    //     }, 1000);
    // })
    // .catch(error => {
    //     console.error('Error:', error);
    //     showToast('Error', error.message);
    // });
}
function viewDetailsInModal(element) {
       const url = element !== null ? element.getAttribute("data-url") : null;
       fetch(url, {
           method: 'GET'
       })
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#mess-inspection-view-modal'));
            modal.show();

            // Add event listeners to each radio button to call changeLabel
            document.querySelectorAll('input[name="paymentType"]').forEach(radio => {
                radio.addEventListener('click', function() {
                    changeLabel(this.value);  // Update label based on selected payment type
                });
            });
        })
        .catch(error => {
            showToast('Error');
        });
    }


// Resend Mail
function resendMailAction(element) {
	const url = element !== null ? element.getAttribute("data-url") : null;
		$('#resendEmailModal').modal('show');
		$('#resendEmailModalPositive').off('click').click(function() {
			$('#resendEmailModal').modal('hide');
			$.ajax({
				url: contextPath + "/" + url,
				type: "POST",
				success: function(response) {
					showToast(response.status, response.message);
				},
				error: function() {
					showToast('Error', 'Error occurred');
				}
			});
			$('#resendEmailModalPositive').off('click');
		})
}

function approveHostelEnrollment(element){
	const url = element !== null ? element.getAttribute("data-url") : null;
	$('#hostelEnrollmentModal').modal('show');
	$('#hostelEnrollmentModalPositive').off('click').click(function() {
		const requiredFields = $('input[required]');
		const isValid = validateRadioAndText(requiredFields);
		if(isValid){
			$('#hostelEnrollmentModalPositive').modal('hide');
			let messPeriod = $('input[name="messPeriod"]:checked').val();
			$.ajax({
				url: contextPath + url,
				type: "GET",
				data:{messPeriod:messPeriod},
				success: function(response) {
					if (response.status === 'Success') {
					}
					showToast(response.status, response.message);
				},
				error: function() {
					showToast('Error', 'Error occurred');
				}
			});
			$('#hostelEnrollmentPositive').off('click');
		}
	});
}

function rejectHostelEnrollment(element){
	const url = element !== null ? element.getAttribute("data-url") : null;
	$('#reject-modal').modal('show');
	$('#reject-modalSave').off('click').click(function() {
		let status = valRequiredMultiTextRadio('#rejectReason');
		if(status){
			$.ajax({
				url: contextPath + url,
				type: "GET",
				data:{reason:$('#rejectReason').val()},
				success: function(response) {
					if (response.status === 'Success') {
					}
					showToast(response.status, response.message);
				},
				error: function() {
					showToast('Error', 'Error occurred');
				}
			});
			$('#reject-modal').off('click');
		}
	});
}

// Approve
function approveAction(element) {
	const url = element !== null ? element.getAttribute("data-url") : null;
	$('#approval-modal').modal('show');
	$('#approval-modalPositive').off('click').click(function() {
		$('#approval-modal').modal('hide');
		$.ajax({
			url: contextPath + "/" + url,
			type: "PUT",
			success: function(response) {
				showToast(response.status, response.message);
				// Refresh the page after showing the toast
                setTimeout(function() {
                    location.reload();
                }, 2000);
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#approval-modalPositive').off('click');
	})
}

function statusUpdate(element) {
        const url = element !== null ? element.getAttribute("data-url") : null;
       	let selectedStatusType = element !== null ? element.getAttribute("data-bs-title") : null;
       	let modalName;
       	let modalId;
       	 switch (selectedStatusType) {
                case "Approve":
                    modalName = "approveWithReason";
                     break;
                case "Approve With Condition":
                    modalName = "approveWithReason";
                    break;
                case "Reject":
                    modalName = "rejectWithReason";
                    break;
                case "Reject Reversal":
                    modalName = "rejectReversalWithReason";
                    break;
         }
        modalId = '#'+modalName+'Modal';
        $(modalId).modal('show');
        const errorDiv = document.querySelector(`${modalId} .invalid-feedback`);

        $(modalId+'Positive').off('click').click(function() {
           const textarea = document.querySelector(`${modalId} textarea[name="approvalNotes"]`);
           const approvalNotesString = textarea.value.trim();

            if (!approvalNotesString) {
                errorDiv.classList.remove(displayNone);
                textarea.classList.add(errorClass);
                return; // Prevent modal from closing or submission
            } else {
                errorDiv.classList.add(displayNone);
                textarea.classList.remove(errorClass);
            }

           $(modalId).modal('hide');

            let finalUrl = contextPath + "/" + url;
            const hiddenInput = document.createElement('input');
                    hiddenInput.type = 'hidden';
                    hiddenInput.name = 'approvalStatus';
                    hiddenInput.value = selectedStatusType;
            const form = document.getElementById(modalName+'Form');
            form.action = finalUrl;
            form.appendChild(hiddenInput);

            const formData = new FormData(form); // serialize form

            $.ajax({
                url: finalUrl,
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    showToast(response.status, response.message);
                    setTimeout(function () {
                        location.reload();
                    }, 2000);
                },
                error: function () {
                    showToast('Error', 'Error occurred');
                }
            });
            document.getElementById("#approvalNotesId").value = "";
            $(modalId+'Positive').off('click');
        })
}

function handleButtonClick(action, element) {
	var deanMessRebateDto = window.deanMessRebateDto;
    const mail = element.getAttribute("data-mail") === 'true';
	deanMessRebateDto.workflowApprovalNotes = document.getElementById("workflowApprovalNotes").value;
	deanMessRebateDto.workflowRejectReason = document.getElementById("workflowRejectReason").value;
	const url = contextPath + '/public' + updteStatusURL;
    $('#overrideAndApproved').attr('disabled', true);
    $('#rejected').attr('disabled', true);
    showLoader();
	fetch(url, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			action: action,
			deanMessRebateDto: deanMessRebateDto
		}),
	})
		.then(	response => {
	        if (!response.ok) {
                hideLoader();
                $('#overrideAndApproved').attr('disabled', false);
                $('#rejected').attr('disabled', false);
                throw new Error('Network response was not ok');
	        }
	        return response.json(); // Assuming the response is HTML
		})
		.then(data => {
            hideLoader();
			showToast(data.status, data.message);
			// Refresh the page after showing the toast
            setTimeout(function() {
                if (mail) {
                    window.location.href = contextPath;
                } else {
                    window.location.href = contextPath + messRebateURL;
                }
            }, 2000);
		})
		.catch((error) => {
            hideLoader();
            $('#overrideAndApproved').attr('disabled', false);
            $('#rejected').attr('disabled', false);
			console.error('Error:', error);
			showToast('Error', error.message);
		});
}

function triggerDownload(element) {
	let fileName = element.getAttribute("data-filename");
    window.location.href = contextPath + messRebateURL + downloadURL + "/" + fileName;
}

function downloadAction(element) {
	const url = element !== null ? element.getAttribute("data-url") : null;
    fetch(contextPath+"/" + url, {
                method: 'GET',
                headers: {}
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                 // Extract the filename from the Content-Disposition header
                const contentDisposition = response.headers.get('Content-Disposition');
                let filename; // Default filename in case the header is not present

                if (contentDisposition && contentDisposition.includes('filename=')) {
                    filename = contentDisposition
                        .split('filename=')[1]
                        .split(';')[0]
                        .replace(/['"]/g, '');
                }

                return response.blob().then(blob => ({ blob, filename }));
            })
            .then(({ blob, filename }) => {
                // Create a link element to trigger the download
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            })
            .catch(error => {
                console.error('Error downloading Excel file:', error);
                showToast('Error', error);

            });
}

function validateFile() {
	$('.errorList').html('');
	const fileInput = document.getElementById('uploadFile');
	const filePath = fileInput.value;
	const allowedExtensions = /(\.xlsx)$/i;
	if (filePath.trim() !== '') {
		if (!allowedExtensions.exec(filePath)) {
			fileInput.value = '';
			$('.selectFile2').addClass('d-none');
			$('.validFile').removeClass('d-none');
			return false;
		}
		$('#studentBulkUploadForm').submit();
		return true;
	} else {
		$('.selectFile2').removeClass('d-none');
		return false;
	}
}

// PDF Download
function pdfAction(element) {
	const dataUrl = element !== null ? element.getAttribute("data-url") : null;
	const url = contextPath + "/" + dataUrl;
	window.open(url, '_blank');
}

function hostelChange(el) {
	const rowId = el.getAttribute('data-row-id');
	const hostelId = $(el).val();
	const requestId = el.getAttribute('data-request-id');
	const roomSelect = $(".roomNumber[data-row-id='" + rowId + "']");
	const subRoomSelect = $(".subRoomId[data-row-id='" + rowId + "']");
	const button = $("button.allocate-btn[data-id^='" + rowId + "_']");
	roomSelect.empty();
	subRoomSelect.empty();
	roomSelect.append('<option value="0">Select Room Number</option>');
	subRoomSelect.append('<option value="">Select Seat</option>');
	roomSelect.prop('disabled', true);
	subRoomSelect.prop('disabled', true);

	let url;
	let encryptedKey;
	let screenType;
	url = button.attr('data-url');
	if (url !== undefined && url !== null && url !== ''){
		url = url.split('/');
		screenType = url[0];
		encryptedKey = url[url.length - 1];
	} else {
		encryptedKey = '';
		screenType = '';
	}

	if (hostelId) {
		fetch(`${contextPath}/${apiUrl}${getRoomListURL}?hostelId=${hostelId}&requestId=${requestId}
		&encryptedKey=${encryptedKey}&screenType=${screenType}`, {
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
			return response.json();
		}).then(responseJson => {
			if (Array.isArray(responseJson)) {
				console.log(responseJson);
				console.log(roomSelect.get());
				console.log(rowId);
				$.each(responseJson, function(index, room) {
					roomSelect.append('<option value="' + room.roomNo + '">' + room.roomNo + '</option>');
				});
			}
		});
		roomSelect.prop('disabled', false);
	} else {
		roomSelect.prop('disabled', true).val('');
		subRoomSelect.prop('disabled', true).val('');
		button.addClass('d-none');
	}
}

function roomChange(el) {
	const rowId = el.getAttribute('data-row-id');
	const roomNo = $(el).val();
	const subRoomSelect = $(".subRoomId[data-row-id='" + rowId + "']");
	const button = $("button.allocate-btn[data-id^='" + rowId + "_']");
	// const url = el.getAttribute('data-url');
	const hostelId = $("#hostelOrFacilityMasterInput_" + rowId).val();
	const requestId = el.getAttribute('data-request-id');

	let url;
	let encryptedKey;
	let screenType;

	url = button.attr('data-url');
	if (url !== undefined && url !== null && url !== ''){
		url = url.split('/');
		screenType = url[0];
		encryptedKey = url[url.length - 1];
	} else {
		encryptedKey = '';
		screenType = '';
	}

	if (roomNo) {
		fetch(`${contextPath}/${apiUrl}${getSeatListURL}?hostelId=${hostelId}&roomNo=${roomNo}&requestId=${requestId}
		&encryptedKey=${encryptedKey}&screenType=${screenType}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).then(response => {
			if (!response.ok) {
				return response.json().then(err => {
					throw err;
				});
			}
			return response.json();
		}).then(responseJson => {
			subRoomSelect.empty();
			subRoomSelect.append('<option value="">Select Seat</option>');

			if (Array.isArray(responseJson) && responseJson.length > 0) {
				const seatList = responseJson[0].vacantList;

				seatList.forEach(seat => {
					subRoomSelect.append('<option value="' + seat + '">' + seat.toUpperCase() + '</option>');
				});

				subRoomSelect.prop('disabled', false);
			} else {
				subRoomSelect.prop('disabled', true);
			}
		});
	} else {
		subRoomSelect.prop('disabled', true).val('');
		button.addClass('d-none');
	}
}

function subRoomChange(el) {
	const rowId = el.getAttribute('data-row-id');
	const hostelSelect = $(".hostelName[data-row-id='" + rowId + "']").val();
	const roomSelect = $(".roomNumber[data-row-id='" + rowId + "']").val();
	const seatSelect = $(".subRoomId[data-row-id='" + rowId + "']").val();
	const button = $("button.allocate-btn[data-id^='" + rowId + "_']");
	if (hostelSelect && roomSelect && seatSelect) {
		button.removeClass("d-none");
	} else {
		button.addClass('d-none');
	}
}

function allocateOrReallocateStudent(element){
	const url = element !== null ? element.getAttribute("data-url") : null;
	const rowId = element.getAttribute('data-row-id');
	const allottedStatus = element.getAttribute('data-bs-title');
	const hostelSelect = $(".hostelName[data-row-id='" + rowId + "']").val();
	const roomSelect = $(".roomNumber[data-row-id='" + rowId + "']").val();
	const seatSelect = $(".subRoomId[data-row-id='" + rowId + "']").val();
	if ((hostelSelect !== '' && hostelSelect !== 0) && (roomSelect !== '' && roomSelect !== 0) && seatSelect !== '') {
		$.ajax({
			url: `${contextPath}/${url}`,
			type: 'POST',
			data: {
				hostelId : hostelSelect,
				roomNo : roomSelect,
				status : allottedStatus,
				seatName : seatSelect
			},
			success: function (response) {
				if (response === 'saved'){
					showToast('Success', 'Hostel Allocated/Reallocated Successfully!');
					setTimeout(function() {
						location.reload();
					}, 2000);
				} else {
					showToast("Error", 'Something Went Wrong!');
				}
			},
			error: function (error) {
				showToast('Error', error.responseText);
			}
		});
	} else {
		showToast('Error:', 'Please select Hostel name, Room number and Seat');
	}
}

function changeHostel(element) {
	const rowId = element.getAttribute('data-row-id');
	const allocateButton = $("button.allocate-btn[data-id^='" + rowId + "_']");
	const changeButton = $("button.change-btn[data-id^='" + rowId + "_']");
	const hostelNameGrayBox = $(".hostelNameGrayBox[data-row-id='" + rowId + "']");
	const roomNoGrayBox = $(".roomNoGrayBox[data-row-id='" + rowId + "']");
	const seatGrayBox = $(".seatGrayBox[data-row-id='" + rowId + "']");
	const hostelNameSelect = $(".hostelNameSelect[data-row-id='" + rowId + "']");
	const roomNoSelect = $(".roomNoSelect[data-row-id='" + rowId + "']");
	const seatNameSelect = $(".seatSelect[data-row-id='" + rowId + "']");

	const currentHostelName = $(`.hostelNameGrayBox[data-row-id='${rowId}'] #hostelName`).text().trim();
	const currentRoomNo = $(`.roomNoGrayBox[data-row-id='${rowId}'] #roomNo`).text().trim();
	const currentSeat = $(`.seatGrayBox[data-row-id='${rowId}'] #seat`).text().trim();

	const hostelSelect = $(`.hostelNameSelect select[data-row-id='${rowId}']`);
	hostelSelect.find('option').each(function () {
		if ($(this).text().trim() === currentHostelName) {
			$(this).prop('selected', true);
			hostelSelect.trigger('change'); // Load rooms
		}
	});

	setTimeout(() => {
		const roomSelect = $(`.roomNoSelect select[data-row-id='${rowId}']`);
		roomSelect.prop('disabled', false);
		roomSelect.find('option').each(function () {
			if ($(this).text().trim() === currentRoomNo) {
				$(this).prop('selected', true);
				roomSelect.trigger('change'); // Load seats
			}
		});

		const seatSelect = $(`.seatSelect select[data-row-id='${rowId}']`);
		seatSelect.prop('disabled', false);
		seatSelect.find('option').each(function () {
			if ($(this).text().trim() === currentSeat) {
				$(this).prop('selected', true);
			}
		});
	}, 500);

	allocateButton.attr('data-bs-title', 'Reallocate');
	allocateButton.tooltip('dispose');
	allocateButton.tooltip();
	allocateButton.removeClass("d-none");
	changeButton.addClass("d-none");
	hostelNameGrayBox.addClass("d-none");
	roomNoGrayBox.addClass("d-none");
	seatGrayBox.addClass("d-none");
	hostelNameSelect.removeClass("d-none");
	roomNoSelect.removeClass("d-none");
	seatNameSelect.removeClass("d-none");
}

function roomInventoryList(){
	window.location.href = `${contextPath}/studentVacating/roomInventory`;
}

function updateCheckInOrCheckOut(element){
	const url = element !== null ? element.getAttribute("data-url") : null;
	const status = element.getAttribute('data-bs-title');

	const modalId = status == 'Check In'  ? 'checkin-modal' : 'checkout-modal';
    const modalPositiveButtonId = modalId + 'Positive'; // assuming button ID is modalId + 'Positive'

	$('#' + modalId).modal('show');
	$('#' + modalPositiveButtonId).off('click').click(function() {
        $('#' + modalId).modal('hide');
		$.ajax({
			url: `${contextPath}/${url}`,
			type: 'POST',
			success: function (response) {
				if (response !="" && response!=='Failure'){
					showToast('Success', response+' Successfully!');
					setTimeout(function() {
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
		$('#' + modalPositiveButtonId).off('click');
	});

}
function toggleRowHighlight(element) {
    const row = element.closest('tr');
    if (!row) return;
	$(row.parentNode.children).each(function () {
		$(this).removeClass('highlight-row');
	});
    row.classList.add('highlight-row');

    // Get all rows in the table body
    const allRows = Array.from(row.parentNode.children); // Convert HTMLCollection to array
    // Find the index of the clicked row (0-based position)
    const rowPosition = allRows.indexOf(row);
    // Store the row position before refresh
    sessionStorage.removeItem('highlightedRowPosition');
    sessionStorage.setItem('highlightedRowPosition', rowPosition);
}

function sendMessage(element) {
    const url = element?.getAttribute("data-url");
    const modalId = "#sendMessageModal";
    const buttonId = "sendMessageModalPositive";

    $(modalId).modal('show');

    // Clear previous values
    document.getElementById("sendMessageRecipient").value = "";
    document.getElementById("sendMessageSubject").value = "";
    document.getElementById("sendMessageMessage").value = "";

    const subjectInput = document.getElementById("sendMessageSubject");
    const messageInput = document.getElementById("sendMessageMessage");
    const subjectError = subjectInput?.nextElementSibling;
    const messageError = messageInput?.nextElementSibling;

    $(`#${buttonId}`).off('click').on('click', function () {
        const subjectVal = subjectInput.value.trim();
        const messageVal = messageInput.value.trim();

        let hasError = false;

        // Validate subject
        if (!subjectVal) {
            subjectInput.classList.add(errorClass);
            if (subjectError)
            subjectError.classList.remove(displayNone);
            hasError = true;
        } else {
            subjectInput.classList.remove(errorClass);
            if (subjectError)
            subjectError.classList.add(displayNone);
        }

        // Validate message
        if (!messageVal) {
            messageInput.classList.add(errorClass);
            if (messageError) messageError.classList.remove(displayNone);
            hasError = true;
        } else {
            messageInput.classList.remove(errorClass);
            if (messageError) messageError.classList.add(displayNone);
        }

        if (hasError) return; // Stop if any field is invalid

        // Submit the form via AJAX
        $(modalId).modal('hide');

        const form = document.getElementById("sendMessageForm");
        form.action = contextPath + "/" + url;
        const formData = new FormData(form);

        $.ajax({
            url: form.action,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                showToast(response.status, response.message);
            },
            error: function () {
                showToast("Error", "Error occurred");
            }
        });

        // Cleanup
        document.getElementById("sendMessageRecipient").value = "";
        document.getElementById("sendMessageSubject").value = "";
        document.getElementById("sendMessageMessage").value = "";

        $(`#${buttonId}`).off('click');
    });
}

function viewFoodRequestDetailsInModal(element) {
    const url = element !== null ? element.getAttribute("data-url") : null;
    fetch(url, {
        method: 'GET'
    })
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#food-request-modal'));
            modal.show();
        })
        .catch(error => {
            showToast('Error');
        });
}

function updateSickFoodRequestStatus(element) {
    if (!element) return;

    const displayName = element.getAttribute("data-bs-title");
    const url = contextPath + '/' +element.getAttribute("data-url");

    let modalId, buttonId;

    if (displayName === 'Accept Request') {
        modalId = '#acceptModal';
        buttonId = '#acceptModalPositive';
    } else {
        modalId = '#outForDeliveryModal';
        buttonId = '#outForDeliveryModalPositive';
    }

    $(modalId).modal('show');

    $(buttonId).off("click").on("click", function () {
        $.ajax({
            url: url,
            type: 'GET',
            success: function (response) {
                if(response!==null && response!==''){
                    showToast(response.status, response.message);
                    setTimeout(function() {
                        location.reload();
                    }, 5000);
                }
            },
            error: function (error) {
                showToast('Error', error.message);
            }
        });
    });
}

function viewVacatingStudentDueDetails(element) {
    const url = element !== null ? element.getAttribute("data-url") : null;
    showLoader();
    fetch(contextPath + url, {
        method: 'GET'
    })
        .then(response => response.text())
        .then(html => {
            hideLoader();
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html);
            const modal = new bootstrap.Modal($('#vacating-student-due-view-modal'));
            modal.show();
        })
        .catch(error => {
            hideLoader();
            showToast('Error', error.message);
        });
}

function approveVacatingStudentDueDetails(element) {
    const encryptedDetails = element !== null ? element.getAttribute("data-encrypted-details") : null;
    const baseUrl = element !== null ? element.getAttribute("data-baseurl") : null;
    showModalLoader();
    $.ajax({
        url: contextPath + baseUrl + '/' + encryptedDetails,
        type: "POST",
        success: function(response) {
            hideModalLoader();
            showToast(response.status, response.message);
            setTimeout(function() {
                location.reload();
            }, 5000);
            },
        error: function(e) {
            hideModalLoader();
            showToast('Error', e.message);
        }
    });
}

function bulkApproveOrReject(element) {
	const displayName = element !== null ? element.getAttribute("data-bs-title") : null;
	const modalId = '#acceptModal', buttonId = '#acceptModalPositive', modelContent = '#deleteModalText';
	$(modalId).modal('show');
	$(modelContent).html(`${getModalContent(displayName)}`);
	$(buttonId).off('click').on('click', function (e) {
		e.preventDefault();
		e.stopPropagation();
		handleBulkConfirm(displayName);
	});
}

function getModalContent(displayName){
	return displayName === 'BulkApprove' ?
		`
			<div class="mb-3 themeForm">
				<label class="form-label" for="stayFromDate">Stay From Date</label>
				<input type="date" class="form-control stay-from-date">
            </div>
            <div class="mb-3 themeForm">
				<label class="form-label" for="stayToDate">Stay To Date</label>
                <input type="date" class="form-control stay-to-date">
            </div>
            <div class="mb-3 themeForm">
				<label class="form-label" for="approvalNote">Approval Note</label>
                <textarea type="text" class="form-control" id="approvalNote" placeholder="Enter Approval Note"></textarea>
            </div>
		` :
		`
			<div class="mb-3 themeForm">
				<label class="form-label" for="rejectReason">Rejection Reason <span class="text-danger">*</span></label>
                <textarea type="text" class="form-control" id="rejectReason" placeholder="Enter Reject Reason" required></textarea>
                <div class="invalid-feedback">Rejection Reason Required</div>
            </div>
		`;
}

function handleBulkConfirm(displayName) {
	if (selectedBulkIds.size === 0) {
		showToast('Error', 'Please select at least one record');
		return;
	}

	const payload = {
		encryptedIds: Array.from(selectedBulkIds),
		stayFromDate: $('.stay-from-date').val() || null,
		stayToDate: $('.stay-to-date').val() || null,
		approvalNote: $('#approvalNote').val() || null,
		rejectionReason: $('#rejectReason').val() || null,
		approvalStatus: displayName === 'BulkApprove' ? 'Approved' : 'Rejected'
	};
	if (displayName === 'BulkReject' && !payload.rejectionReason) {
		$('#rejectReason').addClass('is-invalid');
		return;
	}
	showModalLoader();
	$.ajax({
		url: contextPath + '/' + getBaseUrlFromAnyCheckbox(),
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify(payload),
		success: function (response) {
			hideModalLoader();
			$('#acceptModal').modal('hide');
			showToast(response.status, response.message);
			setTimeout(() => location.reload(), 5000);
		},
		error: function () {
			hideModalLoader();
			showToast('Error', 'Bulk action failed');
		}
	});
}

function getBaseUrlFromAnyCheckbox() {
	const checkbox = $('.bulk-checkbox').first();
	const fullUrl = checkbox.attr('data-url');
	const parts = fullUrl.split('/');
	parts.pop();
	return parts.join('/');
}

function downloadMessInspectionFile(element){
    const fileName = element.getAttribute('file-name');
    const downloadUrl = contextPath + '/file/download/MESS_INSPECTION_FILE_PATH/' + fileName;
    fetch(downloadUrl, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw errorData;
                });
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            // Show toast for errors
            if (error.errors && (error.code === 500 || error.code === 400)) {
                showToast(error.status, error.errors);
            } else {
                showToast('Error', 'File Not Found');
            }
        });
}