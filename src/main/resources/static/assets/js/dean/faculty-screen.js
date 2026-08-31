document.addEventListener('DOMContentLoaded', function() {

    // Add row button functionality
    document.getElementById('addRowBtn').addEventListener('click', function() {
        let isFormValid = true;
         $('.required-input').each(function () {
              if(this.style.display !== 'none') {
                  if (!validateField($(this))) {
                    isFormValid = false;
                  }
              }
          });

          if (isFormValid) {
                addNewRow();
            }
    });

    // Remove row delegation (since rows are dynamic)
    document.getElementById('rowsContainer').addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-row')) {
            e.preventDefault();
            const rowContainer = e.target.closest('.row-container');
            rowContainer.remove();
        }
    });

    // Form submission handling
    document.getElementById('submitBtn').addEventListener('click', function(e) {
      let isFormValid = true;
      //validateAndSubmitGuestCouponForm();
       $('.required-input').each(function () {
           if(this.style.display !== 'none') {
              if (!validateField($(this))) {
                isFormValid = false;
              }
           }
       });

       if (!isFormValid) {
          e.preventDefault();
       } else {
          if(!validateForm()) {
		    e.preventDefault();
          } else {
            $('#studentBulkUploadForm').submit();
          }
       }
    });

    // Initial add row if no rows exist
    if (document.querySelectorAll('.row-container').length === 0) {
        addNewRow();
    }
    $('select[id^="dining_"]').each(function() {
        toggleSessionRequired(this);
    });

    $('select[id^="session"]').each(function() {
        toggleLimitedSessionRequired(this);
    });
});
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
function validateUploadedFile() {
	$('.errorList').html('');
	const fileInput = document.getElementById('uploadFile');
	 if(fileInput.style.display !== 'none'){
	    return true;
	 }
	const filePath = fileInput.value;
	const allowedExtensions = /(\.xlsx)$/i;
	if (filePath.trim() !== '') {
		if (!allowedExtensions.exec(filePath)) {
			fileInput.value = '';
			$('.selectFile2').addClass('d-none');
			$('.validFile').removeClass('d-none');
			return false;
		}
		return true;
	} else {
		$('.selectFile2').removeClass('d-none');
		return false;
	}
}


function showError(element, message) {
    element.classList.add('is-invalid');
    const errorElement = element.nextElementSibling;
    if (errorElement && errorElement.classList.contains('error-message')) {
        errorElement.textContent = message;
    } else {
        // Create error message element if it doesn't exist
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message text-danger';
        errorDiv.textContent = message;
        element.parentNode.insertBefore(errorDiv, element.nextSibling);
    }
}

function addNewRow() {
    const rowsContainer = document.getElementById('rowsContainer');
    const rowCount = document.querySelectorAll('.row-container').length;

    const newRow = document.createElement('div');
    newRow.className = 'row-container';
    newRow.innerHTML = `
        <div class="row g-3  mt-4">
            <div class="col-md-11 row">
                <div class="col-md-2">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.stayFrom}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="date" class="form-control required-input" name="rows[${rowCount}].stayFrom" required>
                   <div class="invalid-feedback">${messages.validation.accommodation.stayFrom.required}</div>
                </div>

                <div class="col-md-2">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.stayTo}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="date" class="form-control required-input" name="rows[${rowCount}].stayTo" required>
                     <div class="invalid-feedback">${messages.validation.accommodation.stayTo.required}</div>
                </div>

                <div class="col-md-2">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.maleParticipants}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="number" class="form-control required-input" name="rows[${rowCount}].maleParticipants" min="0" required>
                    <div class="invalid-feedback">${messages.validation.accommodation.maleParticipants.required}</div>
                </div>

                <div class="col-md-2">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.femaleParticipants}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="number" class="form-control required-input" name="rows[${rowCount}].femaleParticipants" min="0" required>
                    <div class="invalid-feedback">${messages.validation.accommodation.femaleParticipants.required}</div>
                </div>

                <div class="col-md-2">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.dining}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <select class="form-select required-input" id="dining_${rowCount}" name="rows[${rowCount}].dining"
                        onchange="toggleSessionRequired(this)">
                        <option value="">Select</option>
                        ${createOptions(diningStatusList)}
                    </select>
                   <div class="invalid-feedback">${messages.validation.accommodation.dining.required}</div>
                </div>
                
                <div class="col-md-2 d-none" id="session_${rowCount}_container">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.session}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <select class="form-select required-input"
                            id="session${rowCount}"
                            name="rows[${rowCount}].session"
                            onchange="toggleLimitedSessionRequired(this)">
                        <option value="">Select</option>
                        ${createOptions(sessionList)}
                    </select>
                   <div class="invalid-feedback">${messages.validation.accommodation.session.required}</div>
                </div>
                
                <div class="col-md-2 d-none mt-2" id="breakfastCount_${rowCount}_container">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.breakfastCount}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="number" class="form-control required-input" name="rows[${rowCount}].breakfastCount" min="0">
                    <div class="invalid-feedback">${messages.validation.accommodation.breakfastCount.required}</div>
                </div>
                <div class="col-md-2 d-none mt-2" id="lunchCount_${rowCount}_container">
                 <label class="form-label me-3">
                        <span>${messages.label.accommodation.lunchCount}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="number" class="form-control required-input" name="rows[${rowCount}].lunchCount" min="0" required>
                    <div class="invalid-feedback">${messages.validation.accommodation.lunchCount.required}</div>
                </div>
                <div class="col-md-2 d-none mt-2" id="dinnerCount_${rowCount}_container">
                    <label class="form-label me-3">
                        <span>${messages.label.accommodation.dinnerCount}</span>
                        <th:block th:replace="~{common/template/mandatory-symbol}" />
                    </label>
                    <input type="number" class="form-control required-input" name="rows[${rowCount}].dinnerCount" min="0" required>
                    <div class="invalid-feedback">${messages.validation.accommodation.dinnerCount.required}</div>
                </div>
            </div>

           <div class="col-md-1 mt-5">
               <button type="button" id="deleteRowBtn" class="btn btn-danger btn-sm remove-row">${messages.button.delete}</button>
           </div>
        </div>
    `;

    rowsContainer.appendChild(newRow);

    // Focus on the first field of the new row
    newRow.querySelector('input').focus();
}

// Then modify your addNewRow function to use these variables:
function createOptions(list) {
    return list.map(item =>
        `<option value="${item.value}">${item.text}</option>`
    ).join('');
}


document.addEventListener('DOMContentLoaded', function() {
    const showUploadSectionBtn = document.getElementById('showUploadSectionBtn');
    const cancelUploadBtn = document.getElementById('cancelUploadBtn');
    const uploadFileSection = document.getElementById('uploadFileSection');
    const downloadButtonsContainer = document.getElementById('downloadButtonsContainer');
    const uploadFileInput = document.getElementById('uploadFile');

    if (showUploadSectionBtn && uploadFileSection) {
        showUploadSectionBtn.addEventListener('click', function() {
            // Show upload section and hide download buttons
           uploadFileSection.style.display = 'block';
           downloadButtonsContainer.style.display = 'none';
           uploadFileInput.classList.add('required-input');
        });
    }

    if (cancelUploadBtn && uploadFileSection) {
        cancelUploadBtn.addEventListener('click', function() {
           // Hide upload section and show download buttons
           uploadFileSection.style.display = 'none';
           uploadFileInput.classList.remove('required-input');
           downloadButtonsContainer.style.display = 'block';
            // Clear the file input
           document.getElementById('uploadFile').value = '';
        });
    }

    function handleActionClick(element) {
        const url = element.getAttribute('data-url');
        if (url) {
            window.location.href = url;
        }
    }
});

function toggleSessionRequired(selectElement) {
    const $row = $(selectElement).closest('.row-container');
    const index = selectElement.id.split('_')[1];
    const $sessionContainer = $row.find(`#session_${index}_container`);
    const $sessionSelect = $row.find(`#session${index}`);
    if (selectElement.value === 'required') {
        $sessionContainer.removeClass('d-none');
        $sessionSelect.addClass('required-input');
    } else {
        $sessionContainer.addClass('d-none');
        $sessionSelect.removeClass('required-input is-invalid').val('');
        $row.find(`[id^="breakfastCount_"], [id^="lunchCount_"], [id^="dinnerCount_"]`)
            .addClass('d-none')
            .find('input')
            .removeClass('required-input')
            .val('');
    }
}

function toggleLimitedSessionRequired(selectElement) {
    const $select = $(selectElement);
    const $row = $select.closest('.row-container');
    const index = $select.attr('id').replace('session', '');
    const showCounts = $select.val() === 'limited session';
    const $countContainers = $row.find(`#breakfastCount_${index}_container, #lunchCount_${index}_container, #dinnerCount_${index}_container`);

    $countContainers.toggleClass('d-none', !showCounts);
    $countContainers.find('input').each(function () {
        const $input = $(this);
        if (showCounts) {
            $input.addClass('required-input').removeClass('is-invalid');
        } else {
            $input.removeClass('required-input is-invalid').val('');
        }
    });
}



/**
 * Validates the entire form (required fields, dates, file upload).
 * @returns {boolean} - `true` if form is valid, `false` otherwise.
 */
function validateForm() {
    let isValid = true;
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // 2. Validate Event Dates
    const eventFromDate = document.getElementById('eventFromDate');
    const eventToDate = document.getElementById('eventToDate');

    if (eventFromDate && eventFromDate.value) {
        const fromDate = new Date(eventFromDate.value);

        // Event From Date must be future
        if (fromDate < today) {
            eventFromDate.classList.add('is-invalid');
            eventFromDate.nextElementSibling.textContent = 'Event From Date must be a future date';
            isValid = false;
        }

        // Event To Date must be after From Date
        if (eventToDate && eventToDate.value) {
            const toDate = new Date(eventToDate.value);
            if (toDate <= fromDate) {
                eventToDate.classList.add('is-invalid');
                eventToDate.nextElementSibling.textContent = 'Event To Date must be after Event From Date';
                isValid = false;
            }
        }
    }

    // 3. Validate Stay Dates in each row
    document.querySelectorAll('.row-container').forEach((row, index) => {
        const stayFrom = document.getElementById(`stayFrom_${index}`);
        const stayTo = document.getElementById(`stayTo_${index}`);

        if (stayFrom && stayFrom.value) {
            const fromDate = new Date(stayFrom.value);

            // Stay From Date must be future
            if (fromDate < today) {
                stayFrom.classList.add('is-invalid');
                stayFrom.nextElementSibling.textContent = 'Stay From Date must be a future date';
                isValid = false;
            }

            // Stay To Date must be after From Date
            if (stayTo && stayTo.value) {
                const toDate = new Date(stayTo.value);
                if (toDate <= fromDate) {
                    stayTo.classList.add('is-invalid');
                    stayTo.nextElementSibling.textContent = 'Stay To Date must be after Stay From Date';
                    isValid = false;
                }
            }
        }
    });
    if(isValid){
        return validateUploadedFile();
    }

    return isValid;
}
