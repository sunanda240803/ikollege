document.addEventListener("DOMContentLoaded", function() {
	updateDeductMessAmount();
	updateCardCharges();
	const selectedOption = getSelectedMessOption();
	if (selectedOption) {
	    toggleSections(selectedOption);
	}
		
	if (window.temporaryAccomodationDto.paymentAdviceList != null && window.temporaryAccomodationDto.paymentAdviceList.length > 0) {
		showPaymentAdviceDiv(false);
	}
	
	const errorMsgDiv = document.getElementById("errorMsg");
    if (errorMsgDiv) {
        errorMsgDiv.style.display = "none";
    } 
});

window.addEventListener('popstate', function(event) {
	const currentUrl = window.location.href;

	fetch(currentUrl, {
		method: 'GET'
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.text();
		})
		.then(html => {
			document.open();
			document.write(html);
			document.close();
		})
		.catch(error => {
			console.error('Error:', error);
			showToast('Error', error.message);
		});
});

function filterTable() {
	const input = document.getElementById("tableSearch");
	const filter = input.value.toLowerCase();
	const table = document.getElementById("hostelRoomAllotmentLogs");
	const rows = table.getElementsByTagName("tr");

	for (let i = 1; i < rows.length; i++) { // Skip the header row
		const cells = rows[i].getElementsByTagName("td");
		let match = false;

		for (let j = 0; j < cells.length; j++) {
			if (cells[j]) {
				const cellText = cells[j].textContent || cells[j].innerText;
				if (cellText.toLowerCase().indexOf(filter) > -1) {
					match = true;
					break;
				}
			}
		}

		rows[i].style.display = match ? "" : "none";
	}
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
	if (status !== 'downloadExcel') {
		createUrlWithParams(page, size, search);
	} else {
		let url = "/" + $('#excelUrl').val();
		createAndReturnUrlWithParams(page, size, search, url);
	}
}

function viewDetails(element) {
	const url = element !== null ? contextPath + "/" + element.getAttribute("data") : null;

	fetch(url, {
		method: 'GET'
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.text();
		})
		.then(html => {
			history.pushState(null, '', url);
			document.open();
			document.write(html);
			document.close();
		})
		.catch(error => {
			console.error('Error:', error);
			showToast('Error', error.message);
		});
} 

function deleteData(id) {
	const url = getUrl(deleteUrl) + '?id='+id;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		$.ajax({
			url: url,
			type: "DELETE",
			success: function(response) {
				if (response.status === 'Success' || response.status === 'success') {
					location.reload();
				}
				showToast(response.status, response.message);
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

function getSelectedMessOption() {
	const messOption = document.querySelector('input[name="diningType"]:checked');
	return messOption ? messOption.value : null;
}

function toggleSections(option) {
	const messCheck = document.getElementById('messCheck');
	const hostelSection = document.getElementById('hostelSection');
	const messSection = document.getElementById('messSection');
	const checkbox = document.getElementById("cardChargesCheckbox");

	messCheck.style.display = 'none';
	hostelSection.style.display = 'none';
	messSection.style.display = 'none';

	if (option === 'Accommodation and Mess both') {
		messCheck.style.display = 'block';
		hostelSection.style.display = 'block';
		messSection.style.display = 'block';
		checkbox.checked = true;
	} else if (option === 'Accommodation Only') {
		hostelSection.style.display = 'block';
		checkbox.checked = false;
	} else if (option === 'Mess Only') {
		messCheck.style.display = 'block';
		messSection.style.display = 'block';
		checkbox.checked = true;
	}
	updateCardCharges();
}

function updateTotalAmount() {
	const selectedOption = getSelectedMessOption(); // Get the selected mess option
	const totalAmountInput = document.getElementById("totalAmount");

	let stayAmount = 0;
	let breakfastMessAmount = 0;
	let lunchMessAmount = 0;
	let dinnerMessAmount = 0;
	let cardCharges = 0;
	let messAccountAmount = 0;

	if (selectedOption === 'Accommodation and Mess both' || selectedOption === 'Accommodation Only') {
		stayAmount = parseFloat(document.getElementById("stayAmount")?.value) || 0;
	}

	if (selectedOption === 'Accommodation and Mess both' || selectedOption === 'Mess Only') {
		breakfastMessAmount = parseFloat(document.getElementById("breakfastMessAmount")?.value) || 0;
		lunchMessAmount = parseFloat(document.getElementById("lunchMessAmount")?.value) || 0;
		dinnerMessAmount = parseFloat(document.getElementById("dinnerMessAmount")?.value) || 0;
		cardCharges = parseFloat(document.getElementById("cardCharges")?.value) || 0;
		messAccountAmount = parseFloat(document.getElementById("messAccountAmount")?.value) || 0;
	}

	// Check if deductMessAmountCheckbox is checked
	const deductMessAmountCheckbox = document.getElementById("deductMessAmountCheckbox");
	const deductMessAmount = deductMessAmountCheckbox?.checked ? messAccountAmount : 0;

	// Check if cardChargesCheckbox is checked
	const cardChargesCheckbox = document.getElementById("cardChargesCheckbox");
	const applicableCardCharges = cardChargesCheckbox?.checked ? cardCharges : 0;

	// Calculate the total amount
	let totalAmount = stayAmount + breakfastMessAmount + lunchMessAmount + dinnerMessAmount + applicableCardCharges - deductMessAmount;

	// Ensure totalAmount does not go negative
	totalAmount = Math.max(totalAmount, 0);

	// Update the total amount field
	totalAmountInput.value = totalAmount.toFixed(2);
}

// Update payment details based on dropdown selection
function updatePaymentDetails() {
	const categorySelect = document.getElementById("category");
	const selectedOption = categorySelect.options[categorySelect.selectedIndex];

	// Get data attributes from the selected option
	const paymentPerDay = selectedOption.getAttribute("data-stay-amount");
	const breakfastCoupon = selectedOption.getAttribute("data-breakfast-coupon-rate");
	const lunchCoupon = selectedOption.getAttribute("data-lunch-coupon-rate");
	const dinnerCoupon = selectedOption.getAttribute("data-dinner-coupon-rate");

	// Update the input fields
	document.getElementById("payment").value = paymentPerDay || "N/A";
	document.getElementById("breakfastCouponRate").value = breakfastCoupon || "N/A";
	document.getElementById("lunchCouponRate").value = lunchCoupon || "N/A";
	document.getElementById("dinnerCouponRate").value = dinnerCoupon || "N/A";

	calculateStayDays();
	updateMessDetails();
	updateTotalAmount();
}

// Calculate stay days and update the stay amount
function calculateStayDays() {
	const stayFromDate = document.getElementById("stayFromDate").value;
	const stayToDate = document.getElementById("stayToDate").value;
	const stayNoOfDaysInput = document.getElementById("stayNoOfDays");

	if (stayFromDate && stayToDate) {
		const fromDate = new Date(stayFromDate);
		const toDate = new Date(stayToDate);

		// Calculate the difference in time
		const timeDifference = toDate - fromDate;

		// Convert the time difference to days
		const daysDifference = timeDifference / (1000 * 60 * 60 * 24);

		// Update the stayNoOfDays input
		if (daysDifference >= 0) {
			stayNoOfDaysInput.value = daysDifference + 1; // Include the start date
		} else {
			stayNoOfDaysInput.value = "Invalid Dates";
		}
	} else {
		stayNoOfDaysInput.value = ""; // Clear the field if dates are missing
	}

	calculateStayAmount();
}

// Calculate stay amount
function calculateStayAmount() {
	const payment = parseFloat(document.getElementById("payment").value) || 0;
	const stayNoOfDays = parseInt(document.getElementById("stayNoOfDays").value) || 0;
	const stayAmountInput = document.getElementById("stayAmount");

	const stayAmount = payment * stayNoOfDays;

	stayAmountInput.value = stayAmount.toFixed(2);

	updateTotalAmount();
}

// Update mess details based on dining dates
function updateMessDetails() {
	const diningFromDate = document.getElementById("diningFromDate").value;
	const diningToDate = document.getElementById("diningToDate").value;

	const breakfastCouponRate = parseFloat(document.getElementById("breakfastCouponRate").value) || 0;
	const lunchCouponRate = parseFloat(document.getElementById("lunchCouponRate").value) || 0;
	const dinnerCouponRate = parseFloat(document.getElementById("dinnerCouponRate").value) || 0;

	const breakfastCoupon = document.getElementById("breakfastCoupon");
	const lunchCoupon = document.getElementById("lunchCoupon");
	const dinnerCoupon = document.getElementById("dinnerCoupon");

	const breakfastMessAmount = document.getElementById("breakfastMessAmount");
	const lunchMessAmount = document.getElementById("lunchMessAmount");
	const dinnerMessAmount = document.getElementById("dinnerMessAmount");

	if (diningFromDate && diningToDate) {
		const fromDate = new Date(diningFromDate);
		const toDate = new Date(diningToDate);

		// Calculate the number of days
		const timeDifference = toDate - fromDate;
		const numberOfDays = timeDifference / (1000 * 60 * 60 * 24) + 1; // Include the start date

		if (numberOfDays > 0) {
			// Update coupon values
			breakfastCoupon.value = numberOfDays;
			lunchCoupon.value = numberOfDays;
			dinnerCoupon.value = numberOfDays;

			// Calculate mess amounts
			breakfastMessAmount.value = (numberOfDays * breakfastCouponRate).toFixed(2);
			lunchMessAmount.value = (numberOfDays * lunchCouponRate).toFixed(2);
			dinnerMessAmount.value = (numberOfDays * dinnerCouponRate).toFixed(2);
		} else {
			// Invalid date range
			breakfastCoupon.value = "";
			lunchCoupon.value = "";
			dinnerCoupon.value = "";

			breakfastMessAmount.value = "";
			lunchMessAmount.value = "";
			dinnerMessAmount.value = "";
		}
	} else {
		// Clear fields if dates are missing
		breakfastCoupon.value = "";
		lunchCoupon.value = "";
		dinnerCoupon.value = "";

		breakfastMessAmount.value = "";
		lunchMessAmount.value = "";
		dinnerMessAmount.value = "";
	}

	updateTotalAmount();
}

function updateCardCharges() {
	const checkbox = document.getElementById("cardChargesCheckbox");
	const cardChargesDiv = document.getElementById("cardChargesDiv");

	// Show or hide the card charges div
	cardChargesDiv.style.display = checkbox.checked ? 'block' : 'none';

	updateTotalAmount();
}

function updateDeductMessAmount() {
	const checkbox = document.getElementById("deductMessAmountCheckbox");
	const messAccountAmountDiv = document.getElementById("messAccountAmountDiv");

	// Show or hide the mess account amount div
	messAccountAmountDiv.style.display = checkbox.checked ? 'block' : 'none';

	updateTotalAmount();
}

function validateMandatoryFields() {
	// Clear previous error messages and classes
	clearErrorMessages();

	const selectedOption = getSelectedMessOption(); // Get the selected mess option
	const category = document.getElementById("category");
	const stayFromDate = document.getElementById("stayFromDate");
	const stayToDate = document.getElementById("stayToDate");
	const messName = document.getElementById("messName");
	const diningFromDate = document.getElementById("diningFromDate");
	const diningToDate = document.getElementById("diningToDate");

	let isValid = true;

	if (!category.value) {
		displayErrorMessage(category, categoryRequired);
		isValid = false;
	}

	if (selectedOption === 'Accommodation and Mess both' || selectedOption === 'Accommodation Only') {
		if (!stayFromDate.value) {
			displayErrorMessage(stayFromDate, stayFromDateRequired);
			isValid = false;
		}
		if (!stayToDate.value) {
			displayErrorMessage(stayToDate, stayToDateRequired);
			isValid = false;
		}
		if (stayFromDate.value && stayToDate.value) {
			const fromDate = new Date(stayFromDate.value);
			const toDate = new Date(stayToDate.value);
			if (toDate < fromDate) {
				displayErrorMessage(stayToDate, stayDateCondtion1);
				isValid = false;
			}
			// Check if stay dates are within stayExtensionList
			if (!isWithinStayExtensionList(fromDate, toDate)) {
				displayErrorMessage(stayToDate, stayDateCondtion2);
				isValid = false;
			}
		}
	}

	if (selectedOption === 'Accommodation and Mess both' || selectedOption === 'Mess Only') {
		if (!messName.value) {
			displayErrorMessage(messName, messNameRequired);
			isValid = false;
		}
		if (!diningFromDate.value) {
			displayErrorMessage(diningFromDate, messFromDateRequired);
			isValid = false;
		}
		if (!diningToDate.value) {
			displayErrorMessage(diningToDate, messToDateRequired);
			isValid = false;
		}
		if (diningFromDate.value && diningToDate.value) {
			const fromDate = new Date(diningFromDate.value);
			const toDate = new Date(diningToDate.value);
			if (toDate < fromDate) {
				displayErrorMessage(diningToDate, messDateCondtion1);
				isValid = false;
			}
			// Check if dining dates are within stayExtensionList
			if (!isWithinStayExtensionList(fromDate, toDate)) {
				displayErrorMessage(diningToDate, messDateCondtion2);
				isValid = false;
			}
		}
	}

	return isValid;
}

function isWithinStayExtensionList(fromDate, toDate) {
	const stayExtensionList = window.temporaryAccomodationDto?.stayExtensionList;

	if (!stayExtensionList || stayExtensionList.length === 0) {
		return false; // No valid stay extension periods
	}

	// Normalize input dates to midnight
	const normalizedFromDate = new Date(fromDate).setHours(0, 0, 0, 0);
	const normalizedToDate = new Date(toDate).setHours(0, 0, 0, 0);

	// Get the earliest and latest dates from the stayExtensionList
	const firstFromDate = Math.min(...stayExtensionList.map(ext => new Date(ext.stayFrom).setHours(0, 0, 0, 0)));
	const lastToDate = Math.max(...stayExtensionList.map(ext => new Date(ext.stayTo).setHours(0, 0, 0, 0)));

	// Check if the given range falls within the overall range
	return normalizedFromDate >= firstFromDate && normalizedToDate <= lastToDate;
}

function displayErrorMessage(field, message) {
	const invalidFeedbackClass = "invalid-feedback";
	const isInvalidClass = "is-invalid";
	
    field.classList.add(isInvalidClass);

    let errorElement = field.nextElementSibling;
    if (!errorElement || !errorElement.classList.contains(invalidFeedbackClass)) {
        errorElement = document.createElement("div");
        errorElement.className = invalidFeedbackClass;
        field.parentNode.appendChild(errorElement);
    }
    errorElement.innerText = message;
}

function clearErrorMessages() {
	const invalidFeedbackClass = "invalid-feedback";
	const isInvalidClass = "is-invalid";
		
	const invalidFields = document.querySelectorAll("."+isInvalidClass);
	invalidFields.forEach((field) => field.classList.remove(isInvalidClass));

	const errorMessages = document.querySelectorAll("."+invalidFeedbackClass);
	errorMessages.forEach((error) => error.remove());
}

async function saveTemporaryAccommodation() {
	// Validate mandatory fields
	if (!validateMandatoryFields() || !await checkDates()) {
		return;
	}
	resetFields();
	const url = getUrl(saveUrl);
	const requestId = parseInt(document.getElementById("requestId").value, 10);
	const data = {
		requestId: requestId,
		categoryId: document.getElementById("category").value,
		hostelPayFromDate: document.getElementById("stayFromDate").value,
		hostelPayToDate: document.getElementById("stayToDate").value,
		hostelTotalNoOfDays: document.getElementById("stayNoOfDays").value,
		hostelRatePerDay: document.getElementById("payment").value,
		messId: document.getElementById("messName").value,
		messPayFromDate: document.getElementById("diningFromDate").value,
		messPayToDate: document.getElementById("diningToDate").value,
		noOfBreakfastCoupons: document.getElementById("breakfastCoupon").value,
		noOfLunchCoupons: document.getElementById("lunchCoupon").value,
		noOfDinnerCoupons: document.getElementById("dinnerCoupon").value,
		breakfastCouponRate: document.getElementById("breakfastCouponRate").value,
		lunchCouponRate: document.getElementById("lunchCouponRate").value,
		dinnerCouponRate: document.getElementById("dinnerCouponRate").value,
		totalBreakfastAmount: Math.round(parseFloat(document.getElementById("breakfastMessAmount").value) || 0),
		totalLunchAmount: Math.round(parseFloat(document.getElementById("lunchMessAmount").value) || 0),
		totalDinnerAmount: Math.round(parseFloat(document.getElementById("dinnerMessAmount").value) || 0),
		cardCharges: document.getElementById("cardCharges").value,
		messaccAmount: document.getElementById("messAccountAmount").value,
		overallAmount: Math.round(parseFloat(document.getElementById("totalAmount").value) || 0),
	};

	// Make an AJAX call to the controller
	fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(data),
	})
		.then((response) => {
			if (response.ok) {
				return response.json();
			} else {
				throw new Error('Failed to save data');
			}
		})
		.then((result) => {
			showToast(result.status, result.message);
			location.reload();
		})
		.catch((error) => {
			displayErrorMessages('Error saving data: ' + error.message);
			console.error(error);
		});
}

function resetFields() {
	const selectedOption = getSelectedMessOption();
	
	if (selectedOption === 'Mess Only') {	//If it is Mess only then reset accomodation values
	    document.getElementById("stayFromDate").value = "";
	    document.getElementById("stayToDate").value = "";
	    document.getElementById("stayNoOfDays").value = "";
	    document.getElementById("payment").value = "";
		document.getElementById("stayAmount").value = "";
	} else if (selectedOption === 'Accommodation Only') {	//If it is Accomodation only then reset mess values 
		document.getElementById("diningFromDate").value = "";
	    document.getElementById("diningToDate").value = "";
	    document.getElementById("breakfastCoupon").value = "";
	    document.getElementById("lunchCoupon").value = "";
	    document.getElementById("dinnerCoupon").value = "";
		document.getElementById("breakfastCouponRate").value = "";
		document.getElementById("lunchCouponRate").value = "";
		document.getElementById("dinnerCouponRate").value = "";
	    document.getElementById("breakfastMessAmount").value = "";
	    document.getElementById("lunchMessAmount").value = "";
	    document.getElementById("dinnerMessAmount").value = "";

	    document.getElementById("cardCharges").value = "";
	    document.getElementById("messAccountAmount").value = "";
	}
    
}

function showPaymentAdviceDiv(show) {
	const paymentAdviceDiv = document.getElementById("paymentAdvice");

	if (show) {
		paymentAdviceDiv.style.display = "block";
	} else {
		paymentAdviceDiv.style.display = "none";
	}
}

function openPopup(url, callback) {
	$.ajax({
		url: url,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#payment-advice-view-modal').modal('show');
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function openHostelPopup(data) {
	const gender = document.getElementById("gender").value;
	const url = data !== null ? contextPath + "/" + data + "&gender=" + gender : null;
	$.ajax({
		url: url,
		type: "Get",
		success: function(response) {
			$('#modalDiv').html(response);
			$('#payment-advice-hostel-view-modal').modal('show');
		},
		error: function(error) {
			showToast('Error', 'Error occured');
		}
	});
}

function validatePaymentForm() {
	const rows = document.querySelectorAll('modalBody tbody tr');
	let isAnyRowSelected = false;
	let selectedPaymentCount = 0;
	let isValid = true;
	let errorMessages = [];
	const referenceNumbers = new Set();
	const currentDate = new Date();

	rows.forEach(row => {
		const checkbox = row.querySelector('input[type="checkbox"]');
		const paymentDate = row.querySelector('input[name^="paymentDate"]');
		const referenceNo = row.querySelector('input[name^="referenceNo"]');
		const paymentAmount = row.querySelector('input[name^="paymentAmount"]');

		if (checkbox.checked) {
			isAnyRowSelected = true;
			selectedPaymentCount++;

			if (!paymentDate.value) {
				isValid = false;
				paymentDate.classList.add('is-invalid');
				errorMessages.push(paymentDateRequired);
			} else {
				const paymentDateValue = new Date(paymentDate.value);
				if (paymentDateValue > currentDate) {
					isValid = false;
					paymentDate.classList.add('is-invalid');
					errorMessages.push(paymentDateCondition);
				} else {
					paymentDate.classList.remove('is-invalid');
				}
			}

			if (!referenceNo.value) {
				isValid = false;
				referenceNo.classList.add('is-invalid');
				errorMessages.push(referenceNumberRequired);
			} else {
				if (referenceNumbers.has(referenceNo.value)) {
					isValid = false;
					referenceNo.classList.add('is-invalid');
					errorMessages.push(referenceNumberCondition);
				} else {
					referenceNumbers.add(referenceNo.value);
					referenceNo.classList.remove('is-invalid');
				}
			}

			if (!paymentAmount.value || isNaN(paymentAmount.value) || parseFloat(paymentAmount.value) <= 0) {
				isValid = false;
				paymentAmount.classList.add('is-invalid');
				errorMessages.push(paymentAmountRequired);
			} else {
				paymentAmount.classList.remove('is-invalid');
			}
		} else {
			paymentDate.classList.remove('is-invalid');
			referenceNo.classList.remove('is-invalid');
			paymentAmount.classList.remove('is-invalid');
		}
	});

	if (!isAnyRowSelected) {
		isValid = false;
		errorMessages.push(paymentTypeCheck);
	}

	if (selectedPaymentCount > 2) {
		isValid = false;
		errorMessages.push(paymentTypeCondition);
	}

	displayErrorMessages(errorMessages);
	return isValid;
}

function savePaymentForm(action) {
	if (!validatePaymentForm()) {
		return;
	}

	const url = getUrl(action === 'save' ? savePaymentURL : updatePaymentURL);
	const requestId = document.getElementById("modalRequestId").value;
	const candidateId = document.getElementById("modalcandidateId").value;
	const id = document.getElementById("modalId").value;

	// Collect selected payment details
	const rows = document.querySelectorAll('modalBody tbody tr');
	const selectedPayments = [];
	let totalPaymentAmount = 0;

	rows.forEach(row => {
		const checkbox = row.querySelector('input[type="checkbox"]');
		if (checkbox.checked) {
			const paymentType = checkbox.value;
			const paymentDate = row.querySelector('input[name^="paymentDate"]').value;
			const referenceNo = row.querySelector('input[name^="referenceNo"]').value;
			const paymentAmount = parseFloat(row.querySelector('input[name^="paymentAmount"]').value) || 0;

			// Convert paymentDate to ISO 8601 format
			const paymentDateISO = new Date(paymentDate).toISOString();

			selectedPayments.push({
				paymentType: paymentType,
				paymentDate: paymentDateISO, // Use the converted date
				referenceNo: referenceNo,
				paymentAmount: paymentAmount
			});

			totalPaymentAmount += paymentAmount; // Sum up the payment amounts
		}
	});

	if (selectedPayments.length > 2) {
		displayErrorMessages(paymentTypeCondition);
		return;
	}

	const totalAmount = parseFloat(document.getElementById("modalTotalAmount").value) || 0;
	const balanceAmount = parseFloat(document.getElementById("modalBalanceAmount").value) || 0;

	if (balanceAmount === 0 && totalPaymentAmount !== totalAmount) {
		displayErrorMessages([paymentAmountCondition2]);
		return;
	} else if (balanceAmount > 0 && totalPaymentAmount + balanceAmount !== totalAmount) {
		displayErrorMessages([paymentAmountCondition1]);
		return;
	}

	const data = {
		requestId: requestId,
		candidateId: candidateId,
		id: id
	};

	selectedPayments.forEach((payment, index) => {
		const paymentIndex = index + 1;
		data[`paymentDate${paymentIndex}`] = payment.paymentDate;
		data[`paymentAmount${paymentIndex}`] = payment.paymentAmount;
		data[`paymentReferenceNo${paymentIndex}`] = payment.referenceNo;
		data[`paymentType${paymentIndex}`] = payment.paymentType;
	});

		fetch(url, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(data),
	})
		.then(response => {
			if (response.ok) {
				return response.json();
			} else {
				throw new Error('Failed to save data');
			}
		})
		.then(response => {
			showToast(response.status, response.message);
			closePopupAndRefresh();
		})
		.catch(error => {
			// Handle error
			displayErrorMessages(['Error saving data: ' + error.message]);
			console.error(error);
		});
}

function displayErrorMessages(messages) {
	const errorMessagesDiv = document.getElementById('errorMessages');
	if (errorMessagesDiv) {
		errorMessagesDiv.innerHTML = messages.map(msg => `${msg}`).join('');
	}
}

function closePopupAndRefresh() {
	if (window.opener) {
		window.close();
		window.opener.location.reload();
	} else {
		location.reload();
	}
}

function checkReferenceNo(referenceFieldId) {
	const referenceNo = document.getElementById(referenceFieldId).value;

	if (!referenceNo) {
		return;
	}

	const url = getUrl(checkReferenceNumberURL);

	fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({ referenceNumber: referenceNo }),
	})
		.then(response => response.json())
		.then(result => {
			const referenceField = document.getElementById(referenceFieldId);
			if (result.data > 0) {
				referenceField.classList.add("is-invalid");
				let errorElement = referenceField.nextElementSibling;
				if (!errorElement || !errorElement.classList.contains("invalid-feedback")) {
					errorElement = document.createElement("div");
					errorElement.className = "invalid-feedback";
					errorElement.innerText = referenceNumberExists;
					referenceField.parentNode.appendChild(errorElement);
				}
			} else {
				referenceField.classList.remove("is-invalid");
				const errorElement = referenceField.nextElementSibling;
				if (errorElement && errorElement.classList.contains("invalid-feedback")) {
					errorElement.remove();
				}
			}
		})
		.catch(error => {
			console.error("Error checking reference number:", error);
		});
}

async function checkDates() {
    const requestId = parseInt(document.getElementById("requestId").value, 10);
    const data = {
        requestId: requestId,
        hostelPayFromDate: document.getElementById("stayFromDate").value,
        hostelPayToDate: document.getElementById("stayToDate").value,
        messPayFromDate: document.getElementById("diningFromDate").value,
        messPayToDate: document.getElementById("diningToDate").value,
    };
    const url = getUrl(checkDatesURL);

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        const result = await response.json();
        const errorMsgDiv = document.getElementById("errorMsg");

        if (result.data > 0) {
            if (errorMsgDiv) {
                errorMsgDiv.classList.add("invalid-feedback");
                errorMsgDiv.style.display = "block";
            }
            return false;
        } else {
            if (errorMsgDiv) {
                errorMsgDiv.style.display = "none";
                errorMsgDiv.classList.remove("invalid-feedback");
            }
            return true;
        }
    } catch (error) {
        console.error("Error checking Dates:", error);
        const errorMsgDiv = document.getElementById("errorMsg");
        if (errorMsgDiv) {
            errorMsgDiv.classList.add("invalid-feedback");
            errorMsgDiv.innerHTML = "An error occurred while checking the dates. Please try again.";
            errorMsgDiv.style.display = "block";
        }
        return false;
    }
}

function approveData(id) {
	const url = getUrl(approveUrl) + '?id='+id;
	$('#approval-modal').modal('show');
	$('#approval-modalPositive').off('click').click(function() {
		$('#approval-modal').modal('hide');
		$.ajax({
			url: url,
			type: "PUT",
			success: function(response) {				
				showToast(response.status, response.message);
				if (response.status === 'Success' || response.status === 'success') {
					location.reload();
				}
			},
			error: function() {
				showToast('Error', 'Error occurred');
			}
		});
		$('#approval-modalPositive').off('click');
	})
}

function openPdf(data) {
	const url = data !== null ? contextPath + "/" + data : null;
	window.open(url, '_blank');
}

function getRoomList(element) {
    const hostelId = element.value;
    const requestId = document.getElementById("modalRequestId").value;
    const paymentAccomAdviceId = document.getElementById("modalId").value;
	
    const url = getUrl(getRoomListUrl) + `?requestId=${requestId}&paymentAccomAdviceId=${paymentAccomAdviceId}&hostelId=${hostelId}`;
	showModalLoader();
	fetch(url, {		
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
			populateRoomDropdown(data.data);
			hideModalLoader();
			clearErrorMessages();
        })
        .catch(error => {
            console.error('Error fetching room list:', error);
			hideModalLoader();
        });
}

function getSeatList() {
	const roomDropdown = document.getElementById("roomNo");
    const roomNo = roomDropdown.options[roomDropdown.selectedIndex].textContent;
    const requestId = document.getElementById("modalRequestId").value;
    const paymentAccomAdviceId = document.getElementById("modalId").value;
	const hostelId = document.getElementById("hostelName").value;
	
    const url = getUrl(getSeatUrl) + `?requestId=${requestId}&paymentAccomAdviceId=${paymentAccomAdviceId}&hostelId=${hostelId}&roomNo=${roomNo}`;
	showModalLoader();
	fetch(url, {		
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
			populateSeatDropdown(data.data);
			hideModalLoader();
			clearErrorMessages();
        })
        .catch(error => {
            console.error('Error fetching room list:', error);
			hideModalLoader();
        });
}

function populateRoomDropdown(roomMap) {
    const roomDropdown = document.getElementById("roomNo"); 
	roomDropdown.innerHTML = '<option value="" selected>Select</option>';
	Object.entries(roomMap).forEach(([roomId, roomNo]) => {
        const option = document.createElement("option");
        option.value = roomId;
        option.textContent = roomNo;
        roomDropdown.appendChild(option);
    });
}

function populateSeatDropdown(seatList) {
    const seatDropdown = document.getElementById("seat"); 
	seatDropdown.innerHTML = '<option value="" selected>Select</option>';
	seatList.forEach(seat => {
        const option = document.createElement("option");
        option.value = seat;
        option.textContent = seat;
        seatDropdown.appendChild(option);
    });
}

function getUrl(path){
	return contextPath + temporaryAccomodationURL + path;
}

function showModalLoader() {
    const loader = document.getElementById("modalLoader");
    if (loader) {
        loader.style.display = "block";
		$('#modalLoader').removeClass('d-none');
		$('html, body').addClass('loading-active');
    }
}

function hideModalLoader() {
    const loader = document.getElementById("modalLoader");
    if (loader) {
        loader.style.display = "none";
		$('#modalLoader').addClass('d-none');
		$('html, body').removeClass('loading-active');
    }
}

function saveHostel() {
	clearErrorMessages();
    const hostelDropdown = document.getElementById("hostelName");
    const roomDropdown = document.getElementById("roomNo");
    const seatDropdown = document.getElementById("seat");

    const hostelId = hostelDropdown.value;
    const roomId = roomDropdown.value;
    const seat = seatDropdown.value;

    if (!hostelId) {
        displayErrorMessage(hostelDropdown, "Hostel Name is Required");
        return;
    }
    if (!roomId) {
        displayErrorMessage(roomDropdown , "Room No. is Required");
        return;
    }
    if (!seat) {
        displayErrorMessage(seatDropdown, "Seat is Required");
        return;
    }

	const requestId = document.getElementById("modalRequestId").value;
    const candidateId = document.getElementById("modalcandidateId").value;
    const paymentAccomAdviceId = document.getElementById("modalId").value;

    const data = {
        requestId: requestId,
        candidateId: candidateId,
        id: paymentAccomAdviceId,
        hostelId: hostelId,
        roomId: roomId,
        seat: seat
    };

    const url = getUrl(saveHostelUrl);
    showModalLoader();
    fetch(url, {
        method: "put",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
	.then(response => {
		if (response.ok) {
			return response.json();
		} else {
			throw new Error('Failed to save data');
		}
	})
	.then(response => {
		showToast(response.status, response.message);
		closePopupAndRefresh();
	})
	.catch(error => {
		displayErrorMessages(['Error saving data: ' + error.message]);
		console.error(error);
        hideModalLoader();
		showToast(error, 'Error saving data: ' + error.message);
	});
}

function changeMess() {
    clearErrorMessages();

    const fromDateElement = document.getElementById("fromDate");
    const messNameDropdown = document.getElementById("messNameDrpdwn");
    const messPayTo = document.getElementById('messPayTo').value;
    const currentMessId = document.getElementById('currentMessId').value;

    const fromDate = fromDateElement.value;
    const messId = messNameDropdown.value;
    
	if (!fromDate) {
        displayErrorMessage(fromDateElement, "From Date is Required");
        return;
    }
    if (!messId) {
        displayErrorMessage(messNameDropdown, "New Mess Name is Required");
        return;
    }
    if (currentMessId === messId) {
        displayErrorMessages(["Selected Mess cannot be the same as the Current Mess"]);
        return;
    }
    const today = new Date();
    const fromDateObj = new Date(fromDate);
    if (fromDateObj <= today) {
        displayErrorMessage(fromDateElement, "From Date must be a future date");
        return;
    }

    /*const messPayToDateObj = new Date(messPayTo);
    if (fromDateObj > messPayToDateObj) {
        displayErrorMessage(fromDateElement, "From Date must be less than or equal to Mess Pay To Date");
        return;
    }*/

	const requestId = document.getElementById("modalRequestId").value;
    const paymentAccomAdviceId = document.getElementById("modalId").value;

    const data = {
        requestId: requestId,
        id: paymentAccomAdviceId,
        messId: messId
    };
    const url = getUrl(changeMessUrl);
    showModalLoader();
    fetch(url, {
        method: "put",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
	.then(response => {
		if (response.ok) {
			return response.json();
		} else {
			throw new Error('Failed to save data');
		}
	})
	.then(response => {
		showToast(response.status, response.message);
		closePopupAndRefresh();
	})
	.catch(error => {
		displayErrorMessages(['Error saving data: ' + error.message]);
		console.error(error);
	});
}


