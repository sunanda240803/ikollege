document.addEventListener("DOMContentLoaded", function() {
	updateInvalidDivClass();
	const selectAll = document.getElementById("all");
	const checkboxes = [
		document.getElementById("bf"),
		document.getElementById("ln"),
		document.getElementById("dn")
	];

	selectAll.addEventListener("change", function() {
		checkboxes.forEach(cb => cb.checked = this.checked);
	});

	checkboxes.forEach(cb => {
		cb.addEventListener("change", function() {
			if (!this.checked) {
				selectAll.checked = false;
			} else if (checkboxes.every(x => x.checked)) {
				selectAll.checked = true;
			}
		});
	});

	const fromDate = new Date(document.getElementById("fromDate").value);
	const toDate = new Date(document.getElementById("toDate").value);

	const bfRate = parseFloat(document.getElementById("bfRate").value);
	const lnRate = parseFloat(document.getElementById("lnRate").value);
	const dnRate = parseFloat(document.getElementById("dnRate").value);

	document.getElementById("bfRateDisplay").textContent = bfRate;
	document.getElementById("lnRateDisplay").textContent = lnRate;
	document.getElementById("dnRateDisplay").textContent = dnRate;

	const tbody = document.getElementById("couponBody");

	let index = 0;
	for (let d = new Date(fromDate); d <= toDate; d.setDate(d.getDate() + 1)) {
		const dateStr = d.toISOString().split("T")[0];
		const displayDate = d.toLocaleDateString("en-US", {
			month: "short",
			day: "numeric",
			year: "numeric"
		});

		const row = document.createElement("tr");
		row.innerHTML = `
	        <td>
	            ${displayDate}
	            <input type="hidden" name="additionalCoupons[${index}].diningDate" value="${dateStr}">
	        </td>
	        <td>
	            <input type="number" min="0" value="0"
	                id="bf_${index}" name="additionalCoupons[${index}].noOfBreakfastCoupon"
	                class="form-control isAlphanumeric" onblur="if(this.value === '') this.value = 0;"
					oninput="calculateTotal()">
	        </td>
	        <td>
	            <input type="number" min="0" value="0" 
	                id="ln_${index}" name="additionalCoupons[${index}].noOfLunchCoupon"
	                class="form-control isAlphanumeric" onblur="if(this.value === '') this.value = 0;"
					oninput="calculateTotal()">
	        </td>
	        <td>
	            <input type="number" min="0" value="0" 
	                id="dn_${index}" name="additionalCoupons[${index}].noOfDinnerCoupon"
	                class="form-control isAlphanumeric" onblur="if(this.value === '') this.value = 0;"
					oninput="calculateTotal()">
	        </td>
	    `;
		tbody.appendChild(row);
		index++;
	}

	const accommodationRate = parseFloat(document.getElementById("convocationAccommodationRate")?.value || 0);
	const accommodationPreference = document.getElementById("accommodationPreference");
	const accommodationStatus = document.getElementById("accommodationStatus");
	const needAccommodationYes = document.getElementById("needAccommodationYes");
	const needAccommodationNo = document.getElementById("needAccommodationNo");
	const oldHostelYes = document.getElementById("oldHostelYes");
	const oldHostelNo = document.getElementById("oldHostelNo");
	const needAccommodationRadios = document.querySelectorAll('input[name="needAccommodationChoice"]');
	const oldHostelRadios = document.querySelectorAll('input[name="oldHostelChoice"]');
	const accommodationAllotmentNoteRow = document.getElementById("accommodationAllotmentNoteRow");
	const oldHostelQuestionRow = document.getElementById("oldHostelQuestionRow");
	const hostelNameRow = document.getElementById("hostelNameRow");
	const hostelName = document.getElementById("hostelName");
	const submitButtonText = document.getElementById("submitButtonText");
	const accommodationPreferenceValue = needAccommodationYes?.dataset.preference || "ACCOMMODATION";
	const hostelPreferenceValue = oldHostelYes?.dataset.preference || "HOSTEL";
	const hostelNotNeededPreferenceValue = oldHostelNo?.dataset.preference || "HOSTEL_NOT_NEEDED";
	function needsAccommodation() {
		return needAccommodationYes?.checked || false;
	}

	function oldHostelSelected() {
		return oldHostelYes?.checked || false;
	}

	function hostelNotNeededSelected() {
		return oldHostelNo?.checked || false;
	}

	function syncAccommodationSelectionFromPreference() {
		const preference = accommodationPreference?.value || "";
		if (!preference) {
			return;
		}

		if (preference === accommodationPreferenceValue) {
			needAccommodationYes.checked = true;
			needAccommodationNo.checked = false;
			oldHostelYes.checked = false;
			oldHostelNo.checked = false;
		} else if (preference === hostelPreferenceValue) {
			needAccommodationYes.checked = false;
			needAccommodationNo.checked = true;
			oldHostelYes.checked = true;
			oldHostelNo.checked = false;
		} else if (preference === hostelNotNeededPreferenceValue) {
			needAccommodationYes.checked = false;
			needAccommodationNo.checked = true;
			oldHostelYes.checked = false;
			oldHostelNo.checked = true;
		}
	}

	function syncAccommodationPreference() {
		if (!accommodationPreference) {
			return;
		}

		if (needsAccommodation()) {
			accommodationPreference.value = accommodationPreferenceValue;
		} else if (needAccommodationNo?.checked && oldHostelSelected()) {
			accommodationPreference.value = hostelPreferenceValue;
		} else if (needAccommodationNo?.checked && hostelNotNeededSelected()) {
			accommodationPreference.value = hostelNotNeededPreferenceValue;
		} else {
			accommodationPreference.value = "";
		}

		if (accommodationStatus) {
			const hasAccommodationSelection = needsAccommodation() || (needAccommodationNo?.checked || false);
			accommodationStatus.value = hasAccommodationSelection ? String(needsAccommodation()) : "";
		}
	}

	function setRadioGroupState(radios, enabled, required) {
		radios.forEach(radio => {
			radio.disabled = !enabled;
			radio.required = required;
			if (!enabled) {
				radio.checked = false;
			}
		});
	}

	function toggleAccommodationFields() {
		const showAllotmentNote = needsAccommodation();
		const showOldHostelQuestion = needAccommodationNo?.checked || false;
		const showHostelName = showOldHostelQuestion && oldHostelSelected();

		accommodationAllotmentNoteRow?.classList.toggle("d-none", !showAllotmentNote);
		oldHostelQuestionRow?.classList.toggle("d-none", !showOldHostelQuestion);
		setRadioGroupState(oldHostelRadios, showOldHostelQuestion, showOldHostelQuestion);
		hostelNameRow?.classList.toggle("d-none", !showHostelName);

		if (!hostelName) {
			syncAccommodationPreference();
			return;
		}

		if (showHostelName) {
			hostelName.disabled = false;
			hostelName.required = true;
			hostelName.classList.add("required-input");
		} else {
			hostelName.value = "";
			hostelName.disabled = true;
			hostelName.required = false;
			hostelName.classList.remove("required-input", errorClass, validClass);
		}

		syncAccommodationPreference();
	}

	window.calculateTotal = function() {
		toggleAccommodationFields();
		let subTotal = 0;
		for (let i = 0; i < index; i++) {
			const bfQty = parseInt(document.getElementById("bf_" + i).value || 0);
			const lnQty = parseInt(document.getElementById("ln_" + i).value || 0);
			const dnQty = parseInt(document.getElementById("dn_" + i).value || 0);

			subTotal += (bfQty * bfRate) + (lnQty * lnRate) + (dnQty * dnRate);
		}

		document.getElementById("subTotalAmount").textContent = subTotal;
		document.getElementById("summarySubTotal").textContent = subTotal;

		let accommodationAmount = needsAccommodation() ? accommodationRate : 0;
		if (accommodationAmount > 0) {
			document.getElementById("accommodationRow").style.display = "flex";
			document.getElementById("summaryAccommodation").textContent = accommodationAmount;
		} else {
			document.getElementById("accommodationRow").style.display = "none";
		}
		let total = subTotal + accommodationAmount;
		if (total > 0) {
			submitButtonText.textContent = messages["message.button.submit.pay"];
		} else {
			submitButtonText.textContent = messages["message.button.submit"];
		}
		document.getElementById("summaryTotal").textContent = total;
		document.getElementById("overallAmount").value = total;
	};

	needAccommodationRadios.forEach(radio => radio.addEventListener("change", calculateTotal));
	oldHostelRadios.forEach(radio => radio.addEventListener("change", calculateTotal));
	syncAccommodationSelectionFromPreference();
	calculateTotal();
	document.getElementById("convocationForm")
		.addEventListener("submit", async function(e) {

			e.preventDefault();

			const valid = await validateForm();

			if (valid) {
				this.submit();
			}
		});

	if (document.getElementById("convocationForm")) {
		initConvocationForm();
	}

	if (document.getElementById("convocationReportForm")) {
		initConvocationReport();
	}
});

async function validateForm() {
	updateInvalidDivClass();
	const saveButton = $('#saveForm');
	saveButton.attr('disabled', true);

	const requiredFields = $('input[required], select[required]');
	let isValid = true;

	isValid = isValid && validateRadioAndText(requiredFields);
	isValid = isValid && validateEmailMsg($('#email')[0], true);

	if (isValid) {
		const studentExists = await checkIfStudentExist();
		isValid = isValid && studentExists;
	}

	if (isValid) {
		const needAccommodation = document.getElementById("needAccommodationYes")?.checked || false;
		const noAccommodationWithHostel = (document.getElementById("needAccommodationNo")?.checked || false)
			&& (document.getElementById("oldHostelYes")?.checked || false)
			&& !!document.getElementById("hostelName")?.value;
		const hostelNotNeeded = (document.getElementById("needAccommodationNo")?.checked || false)
			&& (document.getElementById("oldHostelNo")?.checked || false);
		const complimentaryChecked = document.getElementById("bf")?.checked ||
			document.getElementById("ln")?.checked ||
			document.getElementById("dn")?.checked;
		const subTotal = parseInt(document.getElementById("subTotalAmount")?.textContent || "0");

		if (!(needAccommodation || noAccommodationWithHostel || hostelNotNeeded || complimentaryChecked || subTotal > 0)) {
			showToast('Failed', 'Please select at least one option in Accommodation, Complimentary, or Additional Coupons.');
			saveButton.attr('disabled', false);
			return false;
		}
	}

	if (isValid) {
		saveButton.attr('disabled', false);
		return true;
	}

	saveButton.attr('disabled', false);
	return false;
}

function checkIfStudentExist() {
	return new Promise((resolve) => {
		updateInvalidDivClass();
		const studentId = $('#studentId');

		studentId.val(studentId.val().toUpperCase().replaceAll(" ", ""));
		const errorDiv = studentId.siblings('.invalid-feedback');

		if (!studentId.val()) {
			studentId.removeClass(validClass).addClass(errorClass);
			errorDiv.html(studentId.siblings('.invalid-req-feedback').html());
			return resolve(false);
		}
		$.ajax({
			url: contextPath + baseURL + studentURL + "/" + encodeURIComponent(studentId.val()),
			method: "GET",
			success: function(response) {
				if (response !== null && response !== '') {
					checkIfAccommodationNotExist(studentId).then(resolve);
				} else {
					studentId.removeClass(validClass).addClass(errorClass);
					errorDiv.html(studentId.siblings('.invalid-not-exist-feedback').html());
					resolve(false);
				}
			},
			error: function() {
				console.error("Error fetching student");
				studentId.removeClass(validClass).addClass(errorClass);
				errorDiv.html("Error occurred");
				resolve(false);
			}
		});
	});
}

function checkIfAccommodationNotExist(studentId) {
	return new Promise((resolve) => {
		const errorDiv = studentId.siblings('.invalid-feedback');

		$.ajax({
			url: contextPath + baseURL + accommodationExistsURL + "/" + encodeURIComponent(studentId.val()),
			method: "GET",
			success: function(response) {
				const accommodationExists = response === true || response === "true";

				if (accommodationExists) {
					studentId.removeClass(validClass).addClass(errorClass);
					errorDiv.html(studentId.siblings('.invalid-accomm-exist-feedback').html());
					return resolve(false);
				}

				studentId.removeClass(errorClass).addClass(validClass);
				resolve(true);
			},
			error: function() {
				console.error("Error checking convocation accommodation");
				studentId.removeClass(validClass).addClass(errorClass);
				errorDiv.html("Error occurred");
				resolve(false);
			}
		});
	});
}

function initConvocationReport() {
	const exportButton = document.getElementById("filterData");
	if (exportButton) {
		exportButton.addEventListener("click", downloadExcelReport);
	}
}

function downloadExcelReport() {
	const fromDate = document.getElementById("fromDate")?.value || "";
	const toDate = document.getElementById("toDate")?.value || "";
	const paymentStatus = document.getElementById("paymentStatus")?.value || "";

	const params = new URLSearchParams();

	if (fromDate) {
		params.append("fromDate", fromDate);
	}
	if (toDate) {
		params.append("toDate", toDate);
	}
	if (paymentStatus) {
		params.append("paymentStatus", paymentStatus);
	}

	const queryString = params.toString();
	const url = `${contextPath}${baseURL}${downloadExcelURL}${queryString ? `?${queryString}` : ""}`;

	if (typeof showLoader === "function") {
		showLoader();
	}

	fetch(url, {
		method: "GET"
	})
		.then(response => {
			if (!response.ok) {
				throw new Error(`Download failed with status ${response.status}`);
			}
			return response.blob();
		})
		.then(blob => {
			const downloadUrl = window.URL.createObjectURL(blob);
			const link = document.createElement("a");

			link.href = downloadUrl;
			link.download = "AccommodationMessConvocationReport.xlsx";
			document.body.appendChild(link);
			link.click();
			link.remove();

			window.URL.revokeObjectURL(downloadUrl);

			if (typeof hideLoader === "function") {
				hideLoader();
			}
			if (typeof showToast === "function") {
				showToast("Success", "Excel downloaded successfully.");
			}
		})
		.catch(error => {
			console.error(error);

			if (typeof hideLoader === "function") {
				hideLoader();
			}
			if (typeof showToast === "function") {
				showToast("Error", "Failed to download Excel.");
			}
		});
}