$(document).ready(function() {
    $('#addNewId').click(function() {
        window.location.href = contextPath + "/guestCouponRequest/add";
    });
    if (typeof deleteStatus !== "undefined" && deleteStatus) {
        showToast('Error', 'Pending Requests Removed');
    }

	$('#diningFromDate, #diningToDate').on('change', async function() {
        $("#diningToDate").prop("readonly", false);
        let isValid = true;
        // const diningFromId = $('#diningFromDate');
        // const diningToId = $('#diningToDate');

        // if(diningFromId.val() && diningToId.val()) {
                isValid=await validateDates();
                if(!isValid){
                    $('.couponTable').addClass('d-none');
                    return false;
                }else {
                    const categoryType = getCategoryType();
                    const mealType = getMealType();
                    if ((categoryType != 'Online Coupon' || categoryType != 'Hostel Residents') && mealType != "NonVeg") {
                        populateCouponRates();
                        generateTableRows();
                        $(".couponTable").show();
                    }
                }
                // else{
                //     checkStudentAllottedMessPeriod(diningFromDate, diningToDate).then(function (isAllowed) {
                //         if (isAllowed) {
                //             populateCouponRates();
                //             generateTableRows();
                //         }
                //     });
                // }
        // }else{
        //     $('.couponTable').addClass('d-none');
        //     return false;
        // }
	});
	
	$('#bulkCoupon').on('change', function(){
        populateCouponRates();
		generateTableRows();
	});
	
	$("#saveFormBackend").on("click", function(e) {
		e.preventDefault();
        $('#addCouponRequestForm').submit();
	});

	attachSaveEvent();

	//const requestId = $('#requestId').val();
	const categoryTypeVal = $('#category').val();
	const mealType = getMealType();
	if (checkRequestId()) {
		$('#addNewId').addClass(displayNone);
		toggleDetails(categoryTypeVal);
		 if ((categoryTypeVal != 'Online Coupon' || categoryTypeVal != 'Hostel Residents') && mealType != "NonVeg") {
		    $('.couponTable').removeClass('d-none');
		}
	}
	
	$('#updatePaymentForm').on('click', function() {
		const requiredFields = $('input[required], select[required]');
		let result = validateRadioAndText(requiredFields);
		if (result) {
			const url = contextPath + baseURL + viewURL;
			const form = document.getElementById('addCouponRequestForm');
			form.action = url;
			form.submit();
		}
	});

    var dto = typeof dto !== "undefined" ? dto : null;
    if (dto && (dto.requestId === null || dto.requestId <= 0)) {
        if((dto.overallAmount !== null && dto.overallAmount >= 0) ||
            (dto.vegOrNonVeg !== null && dto.vegOrNonVeg !== '') && (dto.messId !== null && dto.messId > 0)
            && (dto.diningFrom !== null && dto.diningTo !== null)) {
            populateCouponRates();
            generateTableRows();
            if (dto.overallAmount === null || dto.overallAmount <= 0) {
                showToast('Error', 'Please select at least one mess session');
            }
        }
    }
});


function getCategoryType() {
    return $('input[name="category"]:checked').val();
}

function getMealType(){
    return $("input[name='vegOrNonVeg']:checked").val();
}

function checkRequestId() {
    const requestId = $('#requestId').val();
    return requestId !== null && requestId !== "" && requestId !== undefined && requestId !== "0";
}



async function validateDates() {
    let today = new Date();
    today.setHours(0, 0, 0, 0); // remove time

    const diningFromId = $('#diningFromDate');
    const diningToId = $('#diningToDate');

    let fromDate = new Date(diningFromId.val());
    fromDate.setHours(0, 0, 0, 0);
    let toDate = new Date(diningToId.val());
    toDate.setHours(0, 0, 0, 0);

    const categoryType = getCategoryType();
    const mealType = getMealType();

    // Auto-set NonVeg 'toDate' to 6 days after 'fromDate' (total 7 days)
    if ((categoryType === 'Online Coupon' || categoryType === 'Hostel Residents') && mealType === "NonVeg") {
        toDate = addDays(fromDate, parseInt(nonVegMaxDays)); // 6 days after fromDate
        diningToId.val(toDate.toISOString().split('T')[0]).prop("readonly", true); // YYYY-MM-DD
    }

    // Basic validations
    if (!diningFromId.val() || !diningToId.val()) {
        $('.couponTable').addClass('d-none');
        return false;
    }

    if (toDate < fromDate) {
        showToast('Error', 'Dining To Date cannot be before Dining From Date.');
        diningToId.val("");
        return false;
    }

    if (categoryType !== 'Online Coupon' && categoryType !== 'Hostel Residents') {
        if (fromDate < today) {
            showToast('Error', 'Dining From date should not be a past date.');
            diningFromId.val("");
            return false;
        }
        populateCouponRates();
        generateTableRows();
    } else {
        if (fromDate <= today) {
            showToast('Error', 'Dining From date should not be today or in the past.');
            diningFromId.val("");
            return false;
        }

        const cutoffTime=checkCutOffTime(fromDate,today);
        if(!cutoffTime) return false;

        const diffDays = diffInDays(fromDate, toDate); // inclusive difference
        if (mealType === "NonVeg"){
            if(parseInt(diffDays) !== parseInt(nonVegMaxDays)) { // always 7 days total including from date
                showToast('Error', 'For Non-Veg selection, Dining To Date should be exactly '+nonVegMaxDays+' days from Dining From Date.');
                diningToId.val("");
                return false;
            }
        }else{
            if(diffDays > (parseInt(vegMaxDays)-1)){
                showToast('Error', 'Not allowed to request more than '+vegMaxDays+' days.');
                diningToId.val("");
                return false;
            }
        }

        // Check if entered dates are within the mess period or the one-day exception
        if (!validateDiningPeriod(fromDate, toDate)) {
               showToast('Error', 'Dining Period exceeds the Current Mess Period ' + messPeriodDto.diningFromDate + ' to ' + messPeriodDto.diningToDate);
               diningFromId.val("");
               diningToId.val("");
               return false;
        }

        const isAllowed =  checkStudentAllottedMessPeriod(diningFromId.val(), diningToId.val());
        if (!isAllowed) return false;

        if (mealType === "NonVeg") {
            const isSessionAvailable =  checkSameSessionAndMessAvailWithinDates(diningFromId.val(), diningToId.val());
            if (!isSessionAvailable) return false;

            generateTableRows();

            const days = diffDays; // inclusive
            const perDayAmount = parseInt(nonVegDiscountedAmt) || 0;
            const totalAmount = days * perDayAmount;

            // Update summary section
            $("#summaryDays").text(days);
            $("#summaryPerDay").text(perDayAmount);
            $("#summaryTotal").text(totalAmount);
            $('#totalAmount').text(totalAmount);

            $(".couponTable").hide();
            $("#summarySection").show();
        }
    }
    return true;
}

// Helper to add days to a date (returns a new Date)
function addDays(date, days) {
    const result = new Date(date.getTime());
    result.setDate(result.getDate() + days);
    result.setHours(0, 0, 0, 0); // reset time
    return result;
}

// Helper to calculate difference in days (inclusive)
function diffInDays(startDate, endDate) {
    const msPerDay = 1000 * 60 * 60 * 24;
    return Math.round((endDate - startDate) / msPerDay);
}

function checkCutOffTime(fromDate,today) {
    // Block if Dining From = tomorrow AND time ≥ cutoff today
    // Split cutoff into hour + minutes
    var parts = cutoffTime.split(":"); //20:30
    var cutoffHour = parseInt(parts[0]);                  // 20
    var cutoffMinute = parts.length > 1 ? parseInt(parts[1]) : 0; // 30
    // Build cutoff Date for today
    var now = new Date();
    var cutoffToday = new Date(now.getFullYear(), now.getMonth(), now.getDate(), cutoffHour, cutoffMinute);
    // Format cutoff time for display (12h with AM/PM)
    var displayHour = cutoffHour % 12 === 0 ? 12 : cutoffHour % 12;
    var displayMinute = cutoffMinute.toString().padStart(2, "0");
    var amPm = cutoffHour < 12 ? "AM" : "PM";
    var cutoffDisplay = displayHour + ":" + displayMinute + " " + amPm;
    // Tomorrow's date
    var tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    // Validation
    if (fromDate.getTime() === tomorrow.getTime() && now >= cutoffToday) {
        showToast('Error', 'Cannot apply for tomorrow dining, after ' + cutoffDisplay + ' today');
        diningFromId.val("");
        diningToId.val("");
        return false;
    }
    return true;
}

function validateDiningPeriod(diningFrom, diningTo) {
    // Current mess period start and end dates
    const currentMessPeriodStart = new Date(messPeriodDto.diningFromDate);
    const currentMessPeriodEnd = new Date(messPeriodDto.diningToDate);

    // Allow dining for one day after the current period
    const nextDayAfterMessPeriod = new Date(currentMessPeriodEnd);
    nextDayAfterMessPeriod.setDate(currentMessPeriodEnd.getDate() + 1);
    nextDayAfterMessPeriod.setHours(0, 0, 0, 0);
    const today = new Date();

    // Check if today's date is Oct 10, and if dining dates are Oct 11
    if (today.toDateString() === currentMessPeriodEnd.toDateString() && diningFrom.getTime() === nextDayAfterMessPeriod.getTime() && diningTo.getTime() === diningFrom.getTime()) {
        return true;  // Allow Oct 11 dining if today is Oct 10
    }

    // Check if dining dates are within the mess period
    if (diningFrom >= currentMessPeriodStart && diningTo <= currentMessPeriodEnd) {
        return true;
    }

    return false;
}


function getGuestCouponList() {
	// Set these values in the additional param inputs
	$('input[name="additionalParam.name"]').val($('#studentName').val());
	$('input[name="additionalParam.studentId"]').val($('#studentId').val());
	$('input[name="additionalParam.diningFrom"]').val($('#diningFromDateId').val());
	$('input[name="additionalParam.diningTo"]').val($('#diningToDateId').val());
	$('input[name="additionalParam.submittedFrom"]').val($('#submittedFromDate').val());
	$('input[name="additionalParam.submittedTo"]').val($('#submittedToDate').val());
	$('input[name="additionalParam.category"]').val($('#category').val());
	$('input[name="additionalParam.paymentStatus"]').val($('#paymentStatus').val());

	// Call url with params with the current page and size
	let page = $('#pageVal').val();
	let size = $('#sizeVal').val();
	let search = $('#search').val();
	createUrlWithParams(page, size, search);
}

function viewGuestCoupon(element) {
	const requestId = element.getAttribute("data-request-id");
	window.location.href = contextPath + baseURL + viewURL + "?encryptedId=" + requestId;
}

function attachSaveEvent() {
	$('#saveForm').on('click', async function() {
		$(this).attr('disabled', true);
		updateInvalidDivClass();

		let isValid = true;
		const requiredFields = $('input[required], select[required], textarea[required]');
		isValid &= validateRadioAndText(requiredFields);

		var categoryTypeId = $('input[name="category"]:checked').attr('id');

		if (["student", "hostelResidents"].includes(categoryTypeId)) {
			isValid &= validateRadioAndText('#studentId,#studentName,#hostelName,#roomNo');
		} else if (categoryTypeId === "faculty") {
			isValid &= validateRadioAndText('#facultyName,#department,#program');
			isValid &= validateEmailMsg($('#email')[0], true);
		} else if (categoryTypeId === "projFaculty") {
			isValid &= validateRadioAndText('#emailApplicationNo,#staffName');
		} else if (categoryTypeId === "others") {
			isValid &= validateRadioAndText('#otherName,#purpose');
			isValid &= validateEmailMsg($('#otherEmail')[0], true);
		}

		if (isValid) {
			// Validate meal selection or textbox entry based on bulk coupon check
			const bulkCouponChecked = $('#bulkCoupon').prop('checked');
			if (!bulkCouponChecked) {
				// Ensure at least one meal checkbox is selected
				const mealChecked = $('.meal-checkbox:checked').length > 0;
				if (!mealChecked) {
                    showToast('Error', 'Please select at least one mess session');
					$(this).attr('disabled', false);
					return false;
				}
			} else {
				// Ensure at least one text box has a value entered
				let mealValuesEntered = false;
				$('.numeric').each(function() {
					if ($(this).val() && $(this).val() > 0) {
						mealValuesEntered = true;
					}
				});

				if (!mealValuesEntered) {
					showToast('Error', 'Please enter a value for at least one meal type.');
					$(this).attr('disabled', false);
					return false;
				}
			}

			// Validate that total amount is not zero
			const totalAmount = parseFloat($('#totalAmount').text());
			if (totalAmount <= 0) {
				showToast('Error', 'Total amount should be greater than zero.');
				$(this).attr('disabled', false);
				return false;
			}
			
			if (isValid && (categoryTypeId === 'onlineCoupon' || categoryTypeId === 'hostelResidents')) {
				isValid = validateDates();
                if(isValid){
                    isValid = validateMealSelectionBeforeSave();
                }
			}

			// If validation is successful, submit the form
			if (isValid) {
				$('#bfRPU').val($('#bfRatePerUnit').text());
				$('#lunchRPU').val($('#lunchRatePerUnit').text());
				$('#dinnerRPU').val($('#dinnerRatePerUnit').text());
                $('#snacksRPU').val($('#snacksRatePerUnit').text());
				$('#totAmt').val($('#totalAmount').text());

				const rows = $('#couponTable tbody tr[data-row-id]');
				var frequencyArr = [];
				rows.each(function(index) {
					var frequency;
					if (bulkCouponChecked) {
						frequency = {
							date: $(this).find('td').eq(0).text(),
							noOfBreakfast: $('#breakfast_' + (index + 1)).val() || 0,
							noOfLunch: $('#lunch_' + (index + 1)).val() || 0,
							noOfDinner: $('#dinner_' + (index + 1)).val() || 0,
							noOfSnacks: $('#snacks_' + (index + 1)).val() || 0
						};
					} else {
						frequency = {
							date: $('#dateLabel_' + (index + 1)).data('date'),
							havingBreakfast: $('#breakfast_' + (index + 1)).prop('checked'),
							havingLunch: $('#lunch_' + (index + 1)).prop('checked'),
							havingDinner: $('#dinner_' + (index + 1)).prop('checked'),
							havingSnacks: $('#snacks_' + (index + 1)).prop('checked')
						};
					}
					frequencyArr.push(frequency);
				});

				$('#foodFrequency').val(JSON.stringify(frequencyArr));
				$('#addCouponRequestForm').submit();
			} else {
				// Enable the save button again if validation fails
				$(this).attr('disabled', false);
				return false;
			}
		} else {
			showToast('Error', 'Please fill all the mandatory fields and save.');
			$(this).attr('disabled', false);
			return false;
		}
	});
}

function validateMealSelectionBeforeSave() {
    const isNonVeg = getMealType() === 'NonVeg';

    if (isNonVeg) {
        let isValid = true;
        let errorMessage = "For Non-Veg meal type, all meals must be selected per day.";

        $('#couponTable tbody tr').each(function () {
            const rowId = $(this).data('row-id');
            if (rowId) {  // Ignore static rows (subtotal, total, etc.)
                const allMealCheckboxes = $(`.meal-checkbox[data-row-id="${rowId}"]`);
                const checkedMealCheckboxes = allMealCheckboxes.filter(':checked');

                if (allMealCheckboxes.length !== checkedMealCheckboxes.length) {
                    isValid = false;
                    $(`#dateLabel_${rowId}`).addClass('text-danger'); // Highlight the date label
                } else {
                    $(`#dateLabel_${rowId}`).removeClass('text-danger'); // Remove highlight if valid
                }
            }
        });

        if (!isValid) {
            alert(errorMessage); // Show error message
            return false;
        }
    }
    return true;
}

// Function to generate table rows with checkboxes
function generateTableRows() {
    const fromDate = $('#diningFromDate');
    const toDate = $('#diningToDate');
    const bulkCouponChecked = $('#bulkCoupon').prop('checked');
    const categoryType = getCategoryType();
    const mealType=getMealType();
    let checked = "";
    let disabled = "";

    if (mealType === 'NonVeg') {
        checked = "checked";
        disabled = "disabled";
    }

    // Show Day Total column only if NOT bulk and category is the allowed set
    const showDayTotal = (categoryType === 'Hostel Residents' || categoryType === 'Online Coupon');

    // Toggle the header column visibility (your thead already has .dayTotal)
    $('.dayTotal').toggle(showDayTotal);

    if (fromDate.val() && toDate.val()) {
        if (fromDate.val() <= toDate.val()) {
            fromDate.removeClass(errorClass).addClass(validClass);
            toDate.removeClass(errorClass).addClass(validClass);
            $('.couponTable').removeClass('d-none');
            $('.select-all-column').toggle(!bulkCouponChecked);

            const fromDateObj = new Date(fromDate.val());
            const toDateObj = new Date(toDate.val());
            const dateArray = [];

            for (let date = new Date(fromDateObj); date <= toDateObj; date.setDate(date.getDate() + 1)) {
                dateArray.push(new Date(date));
            }

            const tbody = $('#couponTable tbody');
            tbody.empty();

            dateArray.forEach((date, index) => {
                const formattedDate = formatDate(date, dateFormat);
                const rowId = index + 1;

                let row = `
                    <tr data-row-id="${rowId}">
                        <td class="text-center">
                            ${bulkCouponChecked ?
                    `<span id="dateLabel_${rowId}" data-row-id="${rowId}" data-date="${formattedDate}">${formattedDate}</span>`  :
                    `<div class="RadioCheckToggle">
                                    <input type="checkbox" class="btn-check row-select" id="date_${rowId}" data-row-id="${rowId}" autocomplete="off" ${checked} ${disabled}>
                                    <label class="btn btn-outline-tmPrimary" for="date_${rowId}" id="dateLabel_${rowId}" data-date="${formattedDate}">${formattedDate}</label>
                                </div>`}
                        </td>
                        <td class="text-center">
                            ${bulkCouponChecked ?
                    `<input class="form-control numeric" type="number" id="breakfast_${rowId}" data-row-id="${rowId}"/>` :
                    `<input class="form-check-input meal-checkbox" type="checkbox" id="breakfast_${rowId}" data-row-id="${rowId}" ${checked} ${disabled}/>`}
                        </td>
                        <td class="text-center">
                            ${bulkCouponChecked ?
                    `<input class="form-control numeric" type="number" id="lunch_${rowId}" data-row-id="${rowId}"/>` :
                    `<input class="form-check-input meal-checkbox" type="checkbox" id="lunch_${rowId}" data-row-id="${rowId}" ${checked} ${disabled}/>`}
                        </td>
                        <td class="text-center">
                            ${bulkCouponChecked ?
                    `<input class="form-control numeric" type="number" id="dinner_${rowId}" data-row-id="${rowId}"/>` :
                    `<input class="form-check-input meal-checkbox" type="checkbox" id="dinner_${rowId}" data-row-id="${rowId}" ${checked} ${disabled}/>`}
                        </td>
                        <td class="text-center">
                            ${bulkCouponChecked ?
                    `<input class="form-control numeric" type="number" id="snacks_${rowId}" data-row-id="${rowId}"/>` :
                    `<input class="form-check-input meal-checkbox" type="checkbox" id="snacks_${rowId}" data-row-id="${rowId}" ${checked} ${disabled}/>`}
                        </td>
                `;

                // NEW: append the Day Total cell only when needed
                if (showDayTotal) {
                    row += `
                        <td class="text-center day-total-cell">
                            <div class="form-control form-graybox">
                                <span class="graybox-text" id="dayTotal_${rowId}">0</span>
                            </div>
                        </td>
                    `;
                }

                row += `</tr>`;
                tbody.append(row);
            });

            // Static totals row (unchanged) - ensure this is placed after the dynamic rows
            let staticRows = `
                <tr>
                    <td class="text-end fw-bold align-middle">Rate Per Unit <i class="fa-solid fa-indian-rupee-sign"></i></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="bfRatePerUnit"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="lunchRatePerUnit"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="dinnerRatePerUnit"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="snacksRatePerUnit"></span></div></td>
                 `;
            if (showDayTotal) {
                staticRows += `<td></td>`; // empty cell for day total column
            }

            staticRows += `</tr>
                <tr id="subTotalId" class="d-none">
                    <td class="text-end fw-bold align-middle">Sub Total <i class="fa-solid fa-indian-rupee-sign"></i></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="bfSubTotal"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="lunchSubTotal"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="dinnerSubTotal"></span></div></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="snacksSubTotal"></span></div></td>
                `;

            if (showDayTotal) {
                staticRows += `<td></td>`; // keep alignment for Sub Total row
            }

            staticRows += `</tr>
                <tr>
                    <td colspan="${showDayTotal ? 5 : 4}" class="text-end fw-bold align-middle">Total Amount <i class="fa-solid fa-indian-rupee-sign"></i></td>
                    <td class="text-center"><div class="form-control form-graybox"><span class="graybox-text" id="totalAmount"></span></div></td>
                </tr>`;
            tbody.append(staticRows);

            // Attach event listeners after elements are appended
            attachEventListeners();
            /*if (showDayTotal) {
                calculateAllDayTotals(); // initialize to zeros based on current state
            }*/
        } else {
            toDate.removeClass(validClass).addClass(errorClass);
            let errorDiv = toDate.siblings('.invalid-feedback');
            errorDiv.html(errorDiv.siblings('.invalid-future-feedback').html());
            $('.couponTable').addClass('d-none');
        }
    } else {
        $('.couponTable').addClass('d-none');
    }
}

function attachEventListeners() {
    var categoryType= getCategoryType();
    const showDayTotal = (categoryType === 'Hostel Residents' || categoryType === 'Online Coupon');

    if (!showDayTotal) {
        $("#subTotalId").removeClass("d-none").addClass("d-table-row");
    } else {
        $("#subTotalId").removeClass("d-block").addClass("d-none");
    }
	// When row-select checkbox (date) is changed
	$('.row-select').on('change', function() {
		const rowId = $(this).data('row-id');
		const isChecked = $(this).prop('checked');

		// Update all the meal checkboxes in this row
		$(`.meal-checkbox[data-row-id="${rowId}"]`).prop('checked', isChecked);

		// Iterate through each meal checkbox in the row and apply additional checks
		$(`.meal-checkbox[data-row-id="${rowId}"]`).each(function() {
			if ($(this).is(':checked')) {
				const idPrefix = $(this).attr('id').split('_')[0];
                checkSameDateAndSession(rowId, idPrefix)
                    .then((firstResult) => {
                        if (firstResult) { // ✅ only proceed if true
                            return checkMessAvailability(rowId, idPrefix);
                        } else {
                            console.log("First check failed (duplicate request). Skipping second check.");
                            return Promise.resolve(false); // stop chain
                        }
                    })
                    .then((secondResult) => {
                        // ✅ Run only after Ajax completes successfully
                        calculateMealSubtotals();
                        if (showDayTotal) updateDayTotalForRow(rowId); // NEW
                        updateTotalAmount();
                    })
                    .catch((err) => {
                        console.error("Availability check failed:", err);
                    });
			}
		});

        calculateMealSubtotals();
        if (showDayTotal) updateDayTotalForRow(rowId); // NEW
        updateTotalAmount();
	});

	// When meal checkbox changes
    $('.meal-checkbox').on('change', function() {
		const rowId = $(this).data('row-id');
        // Check for other conditions
        if ($(this).is(':checked')) {
            const idPrefix = $(this).attr('id').split('_')[0];
            checkSameDateAndSession(rowId, idPrefix)
                .then((firstResult) => {
                    if (firstResult) { // ✅ only proceed if true
                        return checkMessAvailability(rowId, idPrefix);
                    } else {
                        console.log("First check failed (duplicate request). Skipping second check.");
                        return Promise.resolve(false); // stop chain
                    }
                })
                .then((secondResult) => {
                    // ✅ Run only after Ajax completes successfully
                    calculateMealSubtotals();
                    if (showDayTotal) updateDayTotalForRow(rowId); // NEW
                    updateTotalAmount();
                })
                .catch((err) => {
                    console.error("Availability check failed:", err);
                });
        }
		calculateMealSubtotals();
        if (showDayTotal) updateDayTotalForRow(rowId); // NEW
		updateTotalAmount();
		checkIfRowShouldBeSelected(rowId);
	});

	// When numeric input changes (for bulk coupons)
	$('.numeric').on('change', function() {
		const rowId = $(this).data('row-id');
		// Check for other conditions
        if ($(this).val() && $(this).val() > 0) {
			const idPrefix = $(this).attr('id').split('_')[0];
            checkMessAvailability(rowId, idPrefix)
                .then(() => {
                    // ✅ Run only after Ajax completes successfully
                    calculateBulkSubtotals();
                    updateTotalAmount();
                })
                .catch(err => {
                    console.error("Availability check failed:", err);
                });
		}else{
            calculateBulkSubtotals();
            updateTotalAmount();
        }
	});
}

// NEW: Calculate day total for each row based on meal selection
function updateDayTotalForRow(rowId) {
    const dinnerRate = parseInt($('#dinnerRatePerUnit').text()) || 0;
    const bfRate = parseInt($('#bfRatePerUnit').text()) || 0;
    const lunchRate = parseInt($('#lunchRatePerUnit').text()) || 0;
    const snacksRate = parseInt($('#snacksRatePerUnit').text()) || 0;

    let dayTotal = 0;
    let label = false;

    const isBF = $(`#breakfast_${rowId}`).is(':checked');
    const isLC = $(`#lunch_${rowId}`).is(':checked');
    const isDR = $(`#dinner_${rowId}`).is(':checked');
    const isET = $(`#snacks_${rowId}`).is(':checked');

    // If all sessions selected, apply discounted rate
    if (isBF && isLC && isDR && isET) {
        var mealType = getMealType();
        if (mealType === 'NonVeg') {
            dayTotal = parseInt(nonVegDiscountedAmt);
            $("#configDiscountedAmountId").val(nonVegDiscountedAmt);
        } else {
            dayTotal = parseInt(vegDiscountedAmt);
            $("#configDiscountedAmountId").val(vegDiscountedAmt);
        }
        // Add label indicating discounted amount
        label = true;
    } else {
        // Calculate per-session rate if not all sessions are selected
        if (isBF) dayTotal += bfRate;
        if (isLC) dayTotal += lunchRate;
        if (isDR) dayTotal += dinnerRate;
        if (isET) dayTotal += snacksRate;
    }
    // Update the Day Total cell with the calculated value
    const rowTotal = $(`#dayTotal_${rowId}`);
    rowTotal.html("₹ " + dayTotal).attr("data-dayamount", dayTotal);
    if (label) {
        rowTotal.addClass("discounted-amount");
    } else {
        rowTotal.removeClass("discounted-amount")
    }
}


// NEW: Initialize day totals for all rows
function calculateAllDayTotals() {
    $('[data-row-id]').each(function() {
        const rowId = $(this).data('row-id');
        if (rowId) updateDayTotalForRow(rowId);
    });
}

// Function to check if all meal checkboxes in the row are checked and update the row-select checkbox accordingly
function checkIfRowShouldBeSelected(rowId) {
    const allMealChecked = $(`.meal-checkbox[data-row-id="${rowId}"]:checked`).length === $(`.meal-checkbox[data-row-id="${rowId}"]`).length;

    // If all meal checkboxes are checked, select the row (date checkbox), otherwise unselect it
    $(`#date_${rowId}`).prop('checked', allMealChecked);
}

// Function to calculate the subtotal for all selected meals (across all rows)
function calculateMealSubtotals() {
    const dinnerRate = parseInt($('#dinnerRatePerUnit').text()) || 0;
    const bfRate = parseInt($('#bfRatePerUnit').text()) || 0;
    const lunchRate = parseInt($('#lunchRatePerUnit').text()) || 0;
    const snacksRate = parseInt($('#snacksRatePerUnit').text()) || 0;

    let totalDinnerSubtotal = 0;
    let totalBfSubtotal = 0;
    let totalLunchSubtotal = 0;
    let totalSnacksSubtotal = 0;

    // Loop through all the meal checkboxes to calculate the total for each meal type
    $('.meal-checkbox').each(function () {
        const id = $(this).attr('id');
        const isChecked = $(this).prop('checked');
        let rate = 0;

        // Determine the rate for the selected meal type
        if (id.startsWith('dinner')) {
            rate = dinnerRate;
        } else if (id.startsWith('lunch')) {
            rate = lunchRate;
        } else if (id.startsWith('breakfast')) {
            rate = bfRate;
        } else if (id.startsWith('snacks')) {
            rate = snacksRate;
        }

        // If the checkbox is checked, add the rate to the total
        if (isChecked) {
            if (id.startsWith('dinner')) {
                totalDinnerSubtotal += rate;
            } else if (id.startsWith('lunch')) {
                totalLunchSubtotal += rate;
            } else if (id.startsWith('breakfast')) {
                totalBfSubtotal += rate;
            } else if (id.startsWith('snacks')) {
                totalSnacksSubtotal += rate;
            }
        }
    });

    // Update the global total subtotals for each meal type
    $('#dinnerSubTotal').text(totalDinnerSubtotal);
    $('#lunchSubTotal').text(totalLunchSubtotal);
    $('#bfSubTotal').text(totalBfSubtotal);
    $('#snacksSubTotal').text(totalSnacksSubtotal);
}

// Function to calculate the subtotal for bulk coupon numeric inputs
function calculateBulkSubtotals() {
    const dinnerRate = parseInt($('#dinnerRatePerUnit').text()) || 0;
    const bfRate = parseInt($('#bfRatePerUnit').text()) || 0;
    const lunchRate = parseInt($('#lunchRatePerUnit').text()) || 0;
    const snacksRate = parseInt($('#snacksRatePerUnit').text()) || 0;

    let totalDinnerSubtotal = 0;
    let totalBfSubtotal = 0;
    let totalLunchSubtotal = 0;
    let totalSnacksSubtotal = 0;

    $('.numeric').each(function () {
        const id = $(this).attr('id');
        let quantity = parseInt($(this).val()) || 0;
        if (quantity < 0) $(this).val(0); // Prevent negative values
        quantity = Math.max(quantity, 0);

        let rate = id.startsWith('dinner') ? dinnerRate : id.startsWith('lunch') ? lunchRate : id.startsWith('breakfast') ? bfRate: snacksRate;
        let subtotal = quantity * rate;

        if (id.startsWith('dinner')) totalDinnerSubtotal += subtotal;
        else if (id.startsWith('lunch')) totalLunchSubtotal += subtotal;
        else if (id.startsWith('breakfast')) totalBfSubtotal += subtotal;
        else if (id.startsWith('snacks')) totalSnacksSubtotal += subtotal;
    });

    $('#dinnerSubTotal').text(totalDinnerSubtotal);
    $('#lunchSubTotal').text(totalLunchSubtotal);
    $('#bfSubTotal').text(totalBfSubtotal);
    $('#snacksSubTotal').text(totalSnacksSubtotal);
}

// Function to update the total amount (grand total)
function updateTotalAmount() {
    var categoryType= getCategoryType();
    if(categoryType === 'Hostel Residents' || categoryType === 'Online Coupon'){
        updateTotalAmountForOnline();
    }else {
        let grandTotal=0;
        const totalDinner = parseInt($('#dinnerSubTotal').text()) || 0;
        const totalLunch = parseInt($('#lunchSubTotal').text()) || 0;
        const totalBf = parseInt($('#bfSubTotal').text()) || 0;
        const totalSnacks = parseInt($('#snacksSubTotal').text()) || 0;

        grandTotal = totalDinner + totalLunch + totalBf + totalSnacks;
        $('#totalAmount').text(grandTotal);
    }
}

function updateTotalAmountForOnline(){
    let overall = 0;
    // Loop each day row only once
    $('#couponTable tbody tr[data-row-id]').each(function() {
        const rowId = $(this).data('row-id');
        if (rowId) {
            const dayTotal = parseInt($('#dayTotal_' + rowId).attr("data-dayamount")) || 0;
            overall += dayTotal;
        }
    });

    $('#totalAmount').text(overall);
}

function toggleDetails(type) {
	const fields = {
		all: $('.studentDetails, .facultyDetails, .projectStaffDetails, .othersDetails,.hostelResidents, .mealType'),
		student: $('.studentDetails'),
		faculty: $('.facultyDetails'),
		projectStaff: $('.projectStaffDetails'),
		others: $('.othersDetails'),
		hostelResidents: $('.hostelResidents'),
		mealType: $('.mealType'),
	};

	const typeMapping = {
		'IITM Students': 'IITM Students',
		'IITM Faculty': 'IITM Faculty',
		'Project Staff': 'Project Staff',
		'Hostel Residents': 'Hostel Residents',
		'Others': 'Others'
	};

	const normalizedType = typeMapping[type] || type;

	fields.all.addClass(dNone);
	$('.couponTable').addClass('d-none');
    $("#summarySection").hide();

	// Clear all form fields in the section before showing the details
    $('input[type="text"], input[type="email"],input[type="date"], textarea').val(''); // Clears text inputs and textareas
    $('input[type="date"]').prop('readonly', false);

    // Clear select dropdowns (set to the first option)
    $('select').each(function () {
        $(this).prop('selectedIndex', 0); // Sets the select dropdown to the first option
    });

    if (type != 'Hostel Residents' && type != 'Online Coupon' && !checkRequestId()) {
    // For all other categories → show ALL messes
        const dropdown = document.getElementById('messName');
        allMesses.forEach(mess => {
            const opt = document.createElement('option');
            opt.value = mess.id;
            opt.textContent = mess.messName;
            dropdown.appendChild(opt);
        });
    }
	
	fields.all.find('.form-control, .form-select').removeClass('is-valid is-invalid');

	const showBulkCoupon = ['IITM Faculty', 'Project Staff', 'Others'].includes(type);
	$('.bulkCoupon').toggleClass('d-none', !showBulkCoupon);
    if (!showBulkCoupon) {
        $('#bulkCoupon').prop('checked', false);
    }

	const dynamicFields = $('#messName, #diningFromDate, #diningToDate').closest('.col-lg-4, .col-lg-3');
	dynamicFields.toggleClass('col-lg-3', showBulkCoupon).toggleClass('col-lg-4', !showBulkCoupon);

	if (fields[normalizedType]) {
		fields[normalizedType].removeClass(dNone);
	}

	if (type === 'IITM Students') {
		fields.student.removeClass(dNone);
	}

	if (type === 'IITM Faculty') {
		fields.faculty.removeClass(dNone);
	}

	if (type === 'Hostel Residents' || type === 'Online Coupon') {
		fields.student.removeClass(dNone);
		fields.mealType.removeClass(dNone);
	}

	if (type === 'Project Staff') {
		fields.projectStaff.removeClass(dNone);
	}

	if (type === 'Others') {
		fields.others.removeClass(dNone);
	}
}

function getStudentDetails() {
    var studentId = $('#studentId').val();

    $.ajax({
        url: contextPath + baseURL + studentInfoURL + "/" + studentId,
        type: "GET",
        success: function(response) {
            if(response!=null && response!="") {
                $('#studentName').val(response['studentName']);
                $('#roomNo').val(response['roomNumber']);
                $('#mobileNo').val(response['contactNumber']);
                $('#hostelName').val(response['hostelId']);
            }else{
                showToast('Error', 'Student ID is invalid');
                $('#studentId').val('');
            }
            
        },
        error: function(error) {
            showToast('Error', 'Error occurred');
        }
    });
}

function populateCouponRates() {
    var fromDate = $('#diningFromDate').val();
    var toDate = $('#diningToDate').val();
    var mealType;
    var categoryType= getCategoryType();
	
	if(categoryType === 'Hostel Residents' || categoryType === 'Online Coupon'){
   	 	mealType = getMealType();
   	 	categoryType='Hostel Residents' + '-' + mealType; //Hostel Residents-Veg/Hostel Residents-NonVeg
   	 }

    if (fromDate && toDate) {
        if (fromDate <= toDate) {
            var requestParam = {
                fromDate: fromDate,
                toDate: toDate,
                category: categoryType
            }
            var queryString = $.param(requestParam);

            $.ajax({
                url: contextPath + baseURL + configRateURL + "?" + queryString,
                type: "GET",
                success: function(response) {
					if(response!=null && response!=""){
						 $('#bfRatePerUnit').text(response['breakfastAmount']);
                   		 $('#lunchRatePerUnit').text(response['lunchAmount']);
                    	$('#dinnerRatePerUnit').text(response['dinnerAmount']);
                        $('#snacksRatePerUnit').text(response['snacksAmount']);
                    	return true;
					}else{
						showToast('Error', 'The Amount is not Configured for these selected dates');
						return false;
					}
                },
                error: function(error) {
                    showToast('Error', 'Error occurred');
                }
            });
        }
    }
}

function checkSameDateAndSession(rowId, sessionId) {
    return new Promise((resolve, reject) => {
        var categoryType = getCategoryType();
        if (categoryType === 'Hostel Residents' || categoryType === 'Online Coupon') {
            var studentId = $('#studentId').val();
            var date = $('#dateLabel_' + rowId).data('date');
            var session = getSessions(sessionId);
            // ✅ Validate required params before sending Ajax
            if (!studentId || !date || !session) {
                showToast('Error', 'Missing required data (Student ID, Date, or Session).');
                console.error("Invalid Params =>", { studentId, date, session });

                // rollback checkbox state if params are invalid
                $(`.meal-checkbox[data-row-id="${rowId}"][id^="${sessionId}"]`)
                    .prop('checked', false);

                return reject("Missing required params");
            }

            var requestParam = { studentId, date, session };
            var queryString = $.param(requestParam);

            $.ajax({
                url: contextPath + baseURL + checkSessionURL + "?" + queryString,
                type: "GET",
                success: function (response) {
                    console.log("checkSameDateAndSession response:", response);

                    if (response != null && response === true) {
                        showToast('Error', 'Request already made for this same date and session');

                        // Uncheck + disable the triggering checkbox
                        $(`.meal-checkbox[data-row-id="${rowId}"][id^="${sessionId}"]`)
                            .prop('checked', false)
                            .prop('disabled', true);

                        calculateMealSubtotals();
                        updateTotalAmount();
                        checkIfRowShouldBeSelected(rowId);

                        return resolve(false); // ❌ Not allowed
                    } else {
                        return resolve(true);  // ✅ Allowed
                    }
                },
                error: function (xhr, status, error) {
                    showToast('Error', 'Error occurred while checking session.');
                    console.error("Ajax error:", status, error);
                    return reject(error);
                }
            });
        } else {
            // If categoryType is not relevant, resolve immediately
            return resolve(true);
        }
    });
}

function checkMessAvailability(rowId, sessionId) {
    return new Promise((resolve, reject) => {
        var messId = $('#messName').val();
        var date = $('#dateLabel_' + rowId).data('date');
        var session = getSessions(sessionId);
        const bulkCouponChecked = $('#bulkCoupon').prop('checked');

        var numericInput = $(`.numeric[data-row-id="${rowId}"][id^="${sessionId}"]`);
        var numericValue = parseInt(numericInput.val() || 0, 10);

        // ✅ Validate required params before sending Ajax
        if (!messId || !date || !session) {
            showToast('Error', 'Missing required data (Mess, Date, or Session).');
            console.error("Invalid Params =>", { messId, date, session });

            if (!bulkCouponChecked) {
                $(`.meal-checkbox[data-row-id="${rowId}"][id^="${sessionId}"]`)
                    .prop('checked', false);
            } else {
                numericInput.val('');
            }
            return reject("Missing required params");
        }

        var rateRequest = { messId, date, session };
        var queryString = $.param(rateRequest);

        $.ajax({
            url: contextPath + baseURL + checkMessAvailURL + "?" + queryString,
            type: "GET",
            success: function (response) {
                console.log('availCount ---- ' + response);
                var availCount = parseInt(response, 10);

                if (isNaN(availCount)) {
                    showToast('Error', 'Invalid response from server.');
                    return reject("Invalid response");
                }

                // --- Case 1: No capacity ---
                if (availCount <= 0) {
                    showToast('Error', 'Mess Capacity Exceeded. You cannot apply for the selected Mess and Session');

                    if (bulkCouponChecked) {
                        numericInput.val('');
                    } else {
                        $(`.meal-checkbox[data-row-id="${rowId}"][id^="${sessionId}"]`)
                            .prop('checked', false)
                            .prop('disabled', true);
                    }
                    return resolve(false);
                }

                // --- Case 2: Bulk coupon entered > available ---
                if (bulkCouponChecked && numericValue > availCount) {
                    showToast('Error', `Available capacity for the selected Mess and Session ${session} is ${availCount} only`);
                    numericInput.val('');
                    return resolve(false);
                }

                // ✅ Success case (valid selection)
                console.log("Selection allowed for row:", rowId, "session:", session);
                return resolve(true);
            },
            error: function (xhr, status, error) {
                showToast('Error', 'Error occurred while checking availability.');
                console.error("Ajax error:", status, error);
                return reject(error);
            }
        });
    });
}

function getSessions(session){
	switch (session) {
		case 'breakfast':
			return session='BF';

		case 'lunch':
			return session='LC';

		case 'dinner':
			return session='DR';

        case 'snacks':
			return session='ET';
	}
}

function checkStudentAllottedMessPeriod(diningFrom, diningTo) {
    return new Promise((resolve, reject) => {
        const studentId = $('#studentId').val();
        // ✅ Validate required params before sending Ajax
        if (!studentId || !diningFrom || !diningTo) {
            showToast('Error', 'Missing required data (Student, From date, or To date).');
            console.error("Invalid Params =>", { studentId, diningFrom, diningTo });
            return reject("Missing required params");
        }

        const requestParam = {
            studentId: studentId,
            fromDate: diningFrom,
            toDate: diningTo
        };
        const queryString = $.param(requestParam);

        $.ajax({
            url: contextPath + baseURL + checkAllottedMessURL + "?" + queryString,
            type: "GET",
            success: function (response) {
                console.log("checkStudentAllottedMessPeriod response:", response);
                if (response != null) {
                    const from = response.diningFrom || diningFrom;
                    const to   = response.diningTo   || diningTo;
                    showToast('Error', 'Already allotted for Mess during this period: ' + from + ' to ' + to);
                    $('#diningFromDate').val("");
                    $('#diningToDate').val("");
                    return resolve(false);
                }
                // ✅ No overlap found; allowed
                return resolve(true);
            },
            error: function (xhr, status, error) {
                showToast('Error', 'Error occurred while checking existing allotment.');
                console.error("Ajax error:", status, error);
                return reject(error);
            }
        });
    });
}

function checkSameSessionAndMessAvailWithinDates(fromDate, toDate) {
    return new Promise((resolve, reject) => {
        const studentId = $('#studentId').val();
        const messId = $('#messName').val();

        // ✅ Validate required params before sending Ajax
        if (!messId || !studentId || !fromDate || !toDate) {
            showToast('Error', 'Missing required data (Mess, Date).');
            console.error("Invalid Params =>", { studentId, messId, fromDate, toDate });
            return reject("Missing required params");
        }

        var rateRequest = { studentId, messId, fromDate, toDate };
        var queryString = $.param(rateRequest);
        console.log("log--"+contextPath + baseURL + checkSameSessionAndMessAvail + "?" + queryString);
        $.ajax({
            url: contextPath + baseURL + checkSameSessionAndMessAvail + "?" + queryString,
            type: "GET",
            success: function (response) {
                if (response && response.errorList && Array.isArray(response.errorList) && response.errorList.length > 0) {
                    let errorTitle = '';
                    if (response.status === "sameDateRequest") {
                        errorTitle = "You have already requested the following session(s):";
                    } else if (response.status === "messAvailCheck") {
                        errorTitle = "There is No Mess availability on the following dates and session(s):";
                    }

                    let errorMessage = `
                      <div style="color:#000; font-weight:600; margin-bottom:10px; font-size:15px; text-align:center;">
                        ${errorTitle}
                      </div>
                      <ul style="display:flex; flex-wrap:wrap; justify-content:center; gap:25px; list-style-type:disc; padding-left:20px; margin:0; font-size:14px;">
                    `;

                    response.errorList.forEach(function (item) {
                        errorMessage += `<li style="color:#333; font-size:14px;">${item.diningTo} - ${item.couponType}</li>`;
                    });

                    errorMessage += `</ul>
                      <div style="font-weight:600; color:#dc3545; margin-top:1rem; font-size:14px; text-align:center;">
                        To apply for the Non-Veg category, you must select a minimum of 7 consecutive days with all 4 sessions availabilities. 
                        Please choose a different date range.
                      </div>
                    `;

                    // Show the error message on the frontend
                    const errorElement = document.getElementById('errorArraySpanId');
                    if (errorElement) {
                        errorElement.innerHTML = errorMessage;
                        errorElement.style.display = ''; // Show the error element if it exists
                    } else {
                        console.error('Element with ID errorArraySpanId not found.');
                    }

                    return resolve(false); // Error case
                } else {
                    document.getElementById('errorArraySpanId').style.display = 'none';
                    return resolve(true); // Success case
                }
            },
            error: function (xhr, status, error) {
                showToast('Error', 'Error occurred while checking availability.');
                console.error("Ajax error:", status, error);
                return reject(error);
            }
        });
    });
}


function checkLdapAuthentication(element) {
	$('#ldap-authentication-modalSave span').text('Submit');
	$('#ldap-authentication-modal').modal('show');
	$('#ldap-authentication-modalSave').off('click').click(function() {
		const password = $('#password').val();
		let isValid = true;
		const requiredFields = $('input[required]');
		isValid &= validateRadioAndText(requiredFields);
		if (isValid) {
			$('#ldap-authentication-modal').modal('hide');
			$.ajax({
				url: contextPath + baseURL + authenticationURL,
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({ password: password }),
				success: function(response) {
					if (response.status === 'Success') {
						downloadGuestCouponSPDF(element);
						$('#password').val('').removeClass(errorClass).removeClass(validClass);
					}
					if (response.status === 'Failure') {
						showToast(response.status, response.message);
						$('#password').val('').removeClass(errorClass).removeClass(validClass);
					}
				},
				error: function() {
					showToast('Error', 'Error occurred');
				}
			});
		}
		$('#ldap-authentication-modalPositive').off('click');
	});
}

function downloadGuestCouponSPDF(element) {
	const requestId = element.getAttribute("data-request-id");
	const url = contextPath + baseURL + pdfDownloadURL + "/" + requestId;
	window.location.href = url;
}

function approveGuestCoupon(element) {
	const requestId = element !== null ? element.getAttribute("data-request-id") : 0;
	$.ajax({
		url: contextPath + baseURL + approveURL + "/" + requestId,
		type: "Get",
		success: function(response) {
			$('#approval-modal').modal('show');
			$('#modalApproveDiv').html(response);
			$("#approval-modalPositive").on("click", function(e) {
				$('#approval-modalModal').modal('hide');
				e.preventDefault();
				$('#guestCouponApproval').submit();
			});
		}
	});
}

function deleteGuestCouponDetails(element) {
const requestId = element !== null ? element.getAttribute("data-request-id") : null;

	$('#deleteModal').modal('show');
	$('#dynamicId').val(requestId);

	$('#deleteModalPositive').off('click').click(function() {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + '/' + requestId, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				'Cache-Control': 'no-cache'
			},
			cache: 'no-cache'
		}).then(response => response.json())
			.then(response => {
				if (response.status === 'Success') {
					$('#deleteReq_' + requestId).parents('tr').remove();
					showToast('Success', 'Coupon request deleted successfully');
				} else {
					showToast('Failure', response.message);
				}
			});
		$('#deleteModalPositive').off('click');
	});
}

function enableGuestCoupon(element) {
    const requestId = element !== null ? element.getAttribute("data-request-id") : null;

	$('#enableModal').modal('show');
	$('#dynamicId').val(requestId);

	$('#enableModalPositive').off('click').click(function() {
		$('#enableModal').modal('hide');
		fetch(contextPath + baseURL + enableUrl + '/' + requestId, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Cache-Control': 'no-cache'
			},
			cache: 'no-cache'
		}).then(response => response.json())
			.then(response => {
				if (response.status === 'Success') {
					$('#enableReq_' + requestId).closest('tr').removeClass('table-danger');
					showToast('Success', 'Coupon enabled successfully');
				} else {
					showToast('Failure', response.message);
				}
			});
		$('#enableModalPositive').off('click');
	});
}

function resendMail(element) {
	const requestId = element !== null ? element.getAttribute("data-request-id") : 0;
	$.ajax({
		url: contextPath + baseURL + resendMailURL + "/" + requestId,
		type: "Get",
		success: function(response) {
			$('#resendEmailModal').modal('show');
			$('#modalMailDiv').html(response);
			$("#resendEmailModalPositive").on("click", function(e) {
				$('#resendEmailModal').modal('hide');
				e.preventDefault();
				$('#guestCouponResendMail').submit();
			});
		}
	});
}

/*
function filterMessOptions() {
    toggleDetails('Online Coupon');
    const vegMessIds = $('#vegMessIds').val().split(",");
    const nonVegMessIds = $('#nonVegMessIds').val().split(",");
    const dropdown =$('#messName');
    if (!dropdown) {
        console.error("Dropdown element not found!");
        return;
    }

    const jsonData = dropdown.getAttribute("data-mess-options");

    if (!jsonData) {
        console.error("Data attribute 'data-mess-options' is empty or not found!");
        return;
    }
    
    const messOptions = JSON.parse(jsonData);
        console.log(messOptions); // ✅ Successfully parsed JSON

    // Fetch Mess Options stored in `th:data-*` attribute
//    const messOptions = JSON.parse(dropdown.getAttribute("data-mess-options"));
//    const messOptions = JSON.parse(document.getElementById("messOptionsData").textContent);


    // Get selected type (Veg or Non-Veg)
    const selectedType =getMealType();
    if (!selectedType) return;

    // Clear existing options
    dropdown.innerHTML = '<option value="0">Select Mess</option>';

    // Determine which IDs to filter
    const selectedIds = selectedType === "Veg" ? vegMessIds : nonVegMessIds;

    // Filter and append matching options
    messOptions
        .filter(option => selectedIds.includes(option.id.toString())) // Ensure string match
        .forEach(option => {
            const opt = document.createElement("option");
            opt.value = option.id;
            opt.textContent = `${option.messName} - ${option.description}`;
            dropdown.appendChild(opt);
        });

}*/

function filterMessOptions() {
    const selectedType = document.querySelector('input[name="vegOrNonVeg"]:checked')?.value;
    const dropdown = document.getElementById('messName');

    // Clear existing options (keep only the default one)
    dropdown.innerHTML = '<option value="">Select Mess Name</option>';

    // Pick the correct mess ID list
    let allowedIds = [];
        if (selectedType === 'Veg') {
            allowedIds = Array.isArray(vegMessIds) ? vegMessIds : String(vegMessIds).split(',').map(v => v.trim());
        } else if (selectedType === 'NonVeg') {
            allowedIds = Array.isArray(nonVegMessIds) ? nonVegMessIds : String(nonVegMessIds).split(',').map(v => v.trim());
        }

    // Add only allowed messes
    allMesses.forEach(mess => {
        if (allowedIds.includes(String(mess.id))) {
            const opt = document.createElement('option');
            opt.value = mess.id;
            opt.textContent = mess.messName;
            dropdown.appendChild(opt);
        }
    });
}

function confirmPayment() {
    return confirm(
        "After completing the payment, please wait 10 minutes for the status to update. If the status is not updated after that time, kindly retry the payment."
    );
}

function resetCount(element) {
    const requestId = element !== null ? element.getAttribute("data-request-id") : null;
    $('#resetModal').modal('show');
    $('#resetModalPositive').off('click').click(function() {
        $('#resetModal').modal('hide');
        $.ajax({
            url: contextPath + baseURL + resetUrl + "/" + requestId,
            type: "POST",
            success: function(response) {
                console.log('Response : ' + response);
                if (response === 'updated') {
                    showToast('Success', 'Retry count reset successfully');
/*                    setTimeout(function() {
                        location.reload();
                    }, 5000);*/
                } else {
                    showToast('Error', "Something went wrong !, please try again later.");
                }
            },
            error : function (res) {
                showToast('Error', "Something went wrong !, please try again later.");
            }
        });
        $('#resetModalPositive').off('click');
    });
}
