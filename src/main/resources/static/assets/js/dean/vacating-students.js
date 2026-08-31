document.addEventListener('DOMContentLoaded', function () {
    const studentHostelRoomVacatingRequestDto = window.studentHostelRoomVacatingRequestDto;

    initializePage(studentHostelRoomVacatingRequestDto);

    if (studentHostelRoomVacatingRequestDto.hostelOrWardenApprovalStatus === "Approved") {
        disableAllInputs();
    }
});

function initializePage(dto) {
    const selectedRadio = document.querySelector('input[name="condition"]:checked');
    if (selectedRadio) {
        toggleRoomPenaltyAmount(selectedRadio);
    }

    const radioButtons = document.querySelectorAll('.form-check-input');
    radioButtons.forEach(radio => {
        if (radio.checked) {
            togglePenaltyReason(radio);
        }
    });

    if ((!dto.hostelName || dto.hostelName === '') || (!dto.roomNo || dto.roomNo === 0)) {
        hideElementById('verificationId');
        hideElementById('totalAmountdiv');
    }
}

function disableAllInputs() {
    const inputs = document.querySelectorAll("input, select, textarea, button");
    inputs.forEach(input => {
        input.disabled = true;
    });
}

function hideElementById(id) {
    const element = document.getElementById(id);
    if (element) {
        element.style.display = 'none';
    }
}

async function getCost(element) {
    const url = `${contextPath}${vacatingStudentURL}${assetCostURL}`;
    const assetCondition = element.value;
    const assetCategory = element.closest('td').previousElementSibling.textContent.trim();
    const penaltyAmountField = element.closest('tr').querySelector('#penaltyAmount');
    const requestUrl = `${url}?assetCategory=${encodeURIComponent(assetCategory)}&assetCondition=${encodeURIComponent(assetCondition)}`;

    try {
        const response = await fetch(requestUrl, { method: 'GET', headers: { 'Content-Type': 'application/json' } });
        if (!response.ok) throw new Error('Failed to fetch data from the server');

        const data = await response.json();
        console.log('Response from server:', data);

        if (penaltyAmountField) {
            penaltyAmountField.value = data.data;
            calculateTotalPenaltyAmount();
            togglePenaltyReason(element);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to fetch penalty charges');
    }
}

async function updatePenaltyCharges(element) {
    const url = `${contextPath}${vacatingStudentURL}${assetCostURL}`;
    const assetCondition = element.value;
    const assetCategory = 'ROOM CONDITION';
    const requestUrl = `${url}?assetCategory=${encodeURIComponent(assetCategory)}&assetCondition=${encodeURIComponent(assetCondition)}`;

    try {
        const response = await fetch(requestUrl, { method: 'GET', headers: { 'Content-Type': 'application/json' } });
        if (!response.ok) throw new Error('Failed to fetch penalty charges');

        const data = await response.json();
        console.log('Response from server:', data);

        const penaltyChargesField = document.getElementById('penaltyCharges');
        if (penaltyChargesField) {
            penaltyChargesField.value = data.data;
            togglePenaltyChargeReason(data.data);
            calculateTotalPenaltyAmount();
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to fetch penalty charges');
    }
}

function calculateTotalPenaltyAmount() {
    const penaltyAmountFields = document.querySelectorAll('.penalty-amount');
    const totalAmount = Array.from(penaltyAmountFields).reduce((sum, field) => {
        return sum + (parseFloat(field.value) || 0);
    }, 0);

    const totalAmountField = document.getElementById('totalAmount');
    if (totalAmountField) {
        totalAmountField.value = totalAmount.toFixed(2);
    }
}

function toggleRoomPenaltyAmount(element) {
    const $roomPenaltyAmountDiv = $('#roomPenaltyAmount');
    const $penaltyChargesField = $('#penaltyCharges');
    const $paintingTypeDropdown = $('#paintingType');

    if ($(element).val() === 'badCondition') {
        $roomPenaltyAmountDiv.show();
    } else if ($(element).val() === 'goodCondition') {
        $roomPenaltyAmountDiv.hide();
        $penaltyChargesField.val('');
        $paintingTypeDropdown.val('');
        togglePenaltyChargeReason(null);
    }
    calculateTotalPenaltyAmount();
}

function togglePenaltyReason(radio) {
    const row = radio.closest('tr');
    if (!row) return console.error('Radio button is not inside a <tr> element.');

    const penaltyReasonInput = row.querySelector('#penaltyReason');
    if (radio.value === 'Good') {
        penaltyReasonInput.value = '';
        penaltyReasonInput.disabled = true;
        penaltyReasonInput.removeAttribute('required');
        penaltyReasonInput.classList.remove('is-invalid');
    } else {
        penaltyReasonInput.disabled = false;
        penaltyReasonInput.setAttribute('required', 'required');
        penaltyReasonInput.classList.add('form-control');
    }
}

function togglePenaltyChargeReason(data) {
    const penaltyDescriptionField = document.getElementById('penaltyDescription');
    if (penaltyDescriptionField) {
        penaltyDescriptionField.disabled = data == null;
        if (data == null) penaltyDescriptionField.value = '';
    }
}

function updateStudentDtoFromForm() {
	validateForm();	
    var studentHostelRoomVacatingRequestDto = window.studentHostelRoomVacatingRequestDto;
	const url = contextPath + '/public' + vacatingStudentURL + updateStatusURL;
    const inventoryRows = document.querySelectorAll('.inventory-row');
    const updatedInventoryList = [];

    inventoryRows.forEach(row => {
		const assetId = row.querySelector('#assetId').value.trim();
        const assetCode = row.querySelector('td:nth-child(1)').textContent.trim();
        const assetCategory = row.querySelector('td:nth-child(2)').textContent.trim();
        const assetName = row.querySelector('td:nth-child(3)').textContent.trim();
        const penaltyAmount = row.querySelector('#penaltyAmount').value;
        const penaltyReason = row.querySelector('#penaltyReason').value;

        // Collect the selected condition (radio button)
        const selectedCondition = row.querySelector('select')?.value;

        // Build the updated inventory object
        updatedInventoryList.push({
			assetId: assetId,
            assetCode: assetCode,
            assetCategory: assetCategory,
            assetName: assetName,
            penaltyAmount: penaltyAmount,
            penaltyReason: penaltyReason,
            assetCondition: selectedCondition
        });
    });

    // Update other fields in the DTO
    studentHostelRoomVacatingRequestDto.inventoryDtoList = updatedInventoryList;
	studentHostelRoomVacatingRequestDto.totalAmount = Math.round(parseFloat(document.getElementById('totalAmount').value) || 0);
    studentHostelRoomVacatingRequestDto.recommendedBy = document.getElementById('recommendedBy').value;
    studentHostelRoomVacatingRequestDto.checkedBy = document.getElementById('checkedBy').value;
    studentHostelRoomVacatingRequestDto.employeeId = document.getElementById('employeeId').value;
	studentHostelRoomVacatingRequestDto.penaltyReason = document.getElementById('penaltyDescription').value;
	studentHostelRoomVacatingRequestDto.roomPaintingType = document.getElementById('paintingType').value;
    
    console.log("Updated DTO:", studentHostelRoomVacatingRequestDto);
	updateStatus(url, studentHostelRoomVacatingRequestDto);
}

function validateForm(event) {
    const fieldsToValidate = [
        { id: 'penaltyReason', isCollection: true },
        { id: 'penaltyDescription' },
        { id: 'checkedBy' },
        { id: 'employeeId' },
        { id: 'recommendedBy' }
    ];

    let isValid = true;

    fieldsToValidate.forEach(field => {
        const elements = field.isCollection
            ? document.querySelectorAll(`#${field.id}`)
            : [document.getElementById(field.id)];

        elements.forEach(element => {
            if (element && !element.disabled && element.value.trim() === '') {
                element.classList.add('is-invalid');
                isValid = false;
            } else if (element) {
                element.classList.remove('is-invalid');
            }
        });
    });

    const roomConditionRadios = document.querySelectorAll('input[name="condition"]');
    const roomConditionSelected = Array.from(roomConditionRadios).some(radio => radio.checked);
    if (!roomConditionSelected) {
        roomConditionRadios.forEach(radio => radio.classList.add('is-invalid'));
        isValid = false;
    } else {
        roomConditionRadios.forEach(radio => radio.classList.remove('is-invalid'));
    }
	
	// Validate paintingType dropdown if "Bad Condition" is selected
    const badConditionRadio = document.querySelector('input[name="condition"][value="badCondition"]');
    const paintingTypeDropdown = document.getElementById('paintingType');
    if (badConditionRadio && badConditionRadio.checked) {
        if (!paintingTypeDropdown || paintingTypeDropdown.value.trim() === '') {
            paintingTypeDropdown.classList.add('is-invalid');
            isValid = false;
        } else {
            paintingTypeDropdown.classList.remove('is-invalid');
        }
    }
	
	const isFacultyValid = isFacultyAvailable();
    if (!isFacultyValid) {
        isValid = false;
    }

    if (!isValid) event.preventDefault();
}

function isFacultyAvailable() {
    const url = contextPath + vacatingStudentURL + checkFacultyURL;
    const employeeIdField = document.getElementById('employeeId');
    const requestUrl = `${url}?employeeId=${encodeURIComponent(employeeIdField.value)}`;
    let isAvailable = false;

    const xhr = new XMLHttpRequest();
    xhr.open('GET', requestUrl, false); // `false` makes the request synchronous
    xhr.setRequestHeader('Content-Type', 'application/json');

    try {
        xhr.send();

        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            console.log('Response from server for Faculty check:', response);

            if (response.data) {
                employeeIdField.classList.remove('is-invalid');
                isAvailable = true;
            } else {
                employeeIdField.classList.add('is-invalid');
            }
        } else {
            console.error('Failed to send data to the server');
            alert('Failed to send data to the server');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to send data to the server');
    }

    return isAvailable;
}

function updateStatus(url, dto){
    showLoader();
    $('#recommendApproveBtn').attr('disabled', true);
    $('#approveBtn').attr('disabled', true);
	fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			action: "test",
			dto: dto
		}),
	})
		.then(	response => {
            hideLoader();
	        if (!response.ok) {
                $('#recommendApproveBtn').attr('disabled', false);
                $('#approveBtn').attr('disabled', false);
	            throw new Error('Network response was not ok');
	        }
	        return response.json(); // Assuming the response is HTML
		})
		.then(data => {
            hideLoader();
			showToast(data.status, data.message);
			// Refresh the page after showing the toast
	        setTimeout(function() {
                location.reload();
            }, 5000);
		})
		.catch((error) => {
            hideLoader();
            $('#recommendApproveBtn').attr('disabled', false);
            $('#approveBtn').attr('disabled', false);
			console.error('Error:', error);
			showToast('Error', error.message);
		});
}

function getAdminReportExcelFile(){
    const requiredFields = $('input[required], select[required]');
    const isValid = validateRadioAndText(requiredFields);
    const reportType = $('#reportType').val();
    const fromDate = $('#fromDateId').val() !== '' ? $('#fromDateId').val() : '-';
    const toDate = $('#toDateId').val() !== '' ? $('#toDateId').val() : '-';
    const params = `${reportType},${fromDate},${toDate}`;
    const dateRanges = [
        {fromElement: "fromDateId", toElement: "toDateId"}
    ];

    let isDateRangeValid = validateBetweenDateRanges(dateRanges);
    if (isValid && isDateRangeValid) {
        window.location.href = `${contextPath}${baseURL}${downloadURL}?params=${params}`
    } else {
        showToast('Error', 'Please fill all mandatory fields.');
        return false;
    }
}