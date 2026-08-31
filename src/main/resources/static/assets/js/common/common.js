$(document).ready(function() {
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
    $('.menu-link').on('click', function() {
        showLoader();
        setTimeout(function() {
            hideLoader();
        }, 5000);
    });
    // showLoader();
    setTimeout(function() {
        hideLoader();
    }, 500);
    // hideLoader();

    // Trim text input and textarea values on blur
    $('input[type="text"], textarea').on('blur', function () {
        trimValue($(this));
    });

    // Handle date fields in modals
    $(document).on('shown.bs.modal', '.themeModal', function () {
        $(this).find('input[type="text"], textarea').on('blur', function () {
            trimValue($(this));
        });

        let subDateInputs = $(this).find('input[type="date"]')
        subDateInputs.on('blur', function () {
            restrictDatesOnBlur($(this));
        });
    });

    // Remove event handlers when the modal is hidden to avoid memory leaks
    $(document).on('hidden.bs.modal', '.themeModal', function () {
        $(this).find('input[type="text"], textarea').off('blur');
        $(this).find('input[type="date"]').off('keydown');
    });

    const dateInputs = $('input[type="date"]')
    dateInputs.on('blur', function () {
        restrictDatesOnBlur($(this));
    });

    $(document).on('blur', '.uppercase', function() {
        $(this).val($(this).val().toUpperCase());
    });
    disableBrowserBack();
    
    // set currency format
    selectAndFormatCurrencyTags();
});

function disableBrowserBack() {
    //Below code not working. need to implement.
    // window.history.pushState(null, "", window.location.href);
    // window.onpopstate = function() {
    //     window.history.pushState(null, "", window.location.href);
    // };
    // console.log('back disabled 2');
}

function trimValue(element) {
    element.val(element.val().trim());
}

function restrictDatesOnBlur(element) {
    let minDate = element.attr('min');
    let maxDate = element.attr('max');

    if (minDate === undefined) {
        minDate = new Date();
        minDate.setFullYear(minDate.getFullYear() - 200);
    } else {
        minDate = new Date(minDate);
    }

    if (maxDate === undefined) {
        maxDate = new Date();
        maxDate.setFullYear(maxDate.getFullYear() + 200);
    } else {
        maxDate = new Date(maxDate);
    }

    const value = new Date(element.val());
    if (value < minDate) {
        element.val(minDate.toISOString().split('T')[0]);
    } else if (value > maxDate) {
        element.val(maxDate.toISOString().split('T')[0]);
    }
}

function showLoader() {
    $('#loader').removeClass('d-none');
    $('html, body').addClass('stop-scrolling');
}

function hideLoader() {
    $('#loader').addClass('d-none');
    $('html, body').removeClass('stop-scrolling');
}

function updateSaveButtonStyle(id, saveButton) {
	if (id > 0) { //update
		saveButton.removeClass('btn-tmPrimary').addClass('btn-blue');
		saveButton.find('i').removeClass('fa-floppy-disk').addClass('fa-rotate');
		saveButton.find('span').text('Update');
	} else { //save
		saveButton.removeClass('btn-blue').addClass('btn-tmPrimary');
		saveButton.find('i').removeClass('fa-rotate').addClass('fa-floppy-disk');
		saveButton.find('span').text('Save');
	}
}


function updateSaveButtonStyleForStringId(id, saveButton) {
	if (id !=null) { //update
		saveButton.removeClass('btn-aqua').addClass('btn-blue');
		saveButton.find('i').removeClass('fa-floppy-disk').addClass('fa-rotate');
		saveButton.find('span').text('Update');
	} else { //save
		saveButton.removeClass('btn-blue').addClass('btn-aqua');
		saveButton.find('i').removeClass('fa-rotate').addClass('fa-floppy-disk');
		saveButton.find('span').text('Save');
	}
}


function handleCommonFormSubmission(formId, validateFunction, buttons) {
    // Disable all buttons in the list
    buttons.forEach(button => {
        button.attr('disabled', true);
    });

    if (validateFunction()) {
        $(formId).submit();
    } else {
        buttons.forEach(button => {
            button.attr('disabled', false);
        });
        return false;
    }
}

function resetFileValue(domElem) {
    domElem.value = '';
}

document.addEventListener('DOMContentLoaded', function() {
	if (document.querySelector('.modal')) {
		$('.modal').modal({
			backdrop: 'static',
			keyboard: false
		});
	}
});

function loadDataTable(jqueryTable) {
    jqueryTable.DataTable({
        "language": {
            "emptyTable": noDataInTableLabel,
        },
        "pageLength" : 25
    });
}

function viewBioData() {
    window.location.href = contextPath + getStudentRegistrationURL + getStudentRegistrationURLSuffix;
}

function inputMask(elementArray,pattern){	
	elementArray.forEach(function(element) { 
		$(element).inputmask(pattern)
        $(element).addClass('masked');
	});
}

function disableSelectIfEmpty(selectElem) {
    const existingValue = selectElem.attr('disabled');
    if (selectElem.length >= 1) {
        if (existingValue !== 'disabled') {
            if (selectElem.find("option").length > 1) {
                selectElem.removeAttr('disabled');
            } else {
                selectElem.attr('disabled', existingValue);
            }
        }
    }
}

// Pagination Validation
function constructPaginationUrl(page, size, search, url) {
    // Get currentUrl from hidden field if url is not provided
    let finalUrl = url || $('#currentUrl').val();

    // Get all elements with class 'additional-param'
    let additionalParamsString = $('.additional-param');

    // Build query string from additionalParams
    let queryString = '';
    additionalParamsString.each(function() {
        let paramName = $(this).attr('name');  // Get the name attribute
        let paramValue = $(this).val();        // Get the value attribute

        if (queryString.length > 0) {
            queryString += '&';
        }
        queryString += `${paramName}=${encodeURIComponent(paramValue)}`;
    });

    // Append search param if it's not null or empty
    if (search && search.trim() !== '') {
        if (queryString.length > 0) {
            queryString += '&';
        }
        queryString += `search=${encodeURIComponent(search.trim())}`;
    }

    // Construct the full URL with page, size, and additionalParams (including search if present)
    let fullUrl = contextPath + `${finalUrl}?page=${page}&size=${size}`;
    if (queryString.length > 0) {
        fullUrl += `&${queryString}`;
    }

    return fullUrl;
}

// Method to redirect to the constructed URL
function createUrlWithParams(page, size, search) {
    let fullUrl = constructPaginationUrl(page, size, search);
    window.location.href = fullUrl;
}

// Method to open the constructed URL in a new tab
function createAndReturnUrlWithParams(page, size, search, url) {
    let fullUrl = constructPaginationUrl(page, size, search, url);
    window.open(fullUrl, '_blank');
}

// Currency format
function selectAndFormatCurrencyTags() {
    const currencyElements = document.querySelectorAll('.currency');

    currencyElements.forEach(element => {
        let value = parseFloat(element.innerText.replace(/,/g, "")) || 0;

        if (!isNaN(value)) {
            // Round off to two decimal places or add '.00' if no decimal values
            const formattedValue = value % 1 !== 0 ? value.toFixed(2) : value.toFixed(2).replace(/\.(\d)0$/, '.$1').replace(/\.$/, '.00');

            const amount = parseFloat(formattedValue).toLocaleString("en-IN", {minimumFractionDigits: 2, maximumFractionDigits: 2});
            element.innerText = amount;
        }
    });
}


function updateInvalidDivClass(){
    // Find all elements with the class 'backendErrors'
    const errorElements = document.querySelectorAll('.backendErrors');

    // Add the 'd-none' class to each element
    errorElements.forEach(element => {
        element.classList.add('d-none');
        element.classList.add('invalid-feedback');
    });

    // Find all elements with the class 'invalid-feedback-frontEnd'
    const invalidErrorElements = document.querySelectorAll('.invalid-feedback-frontEnd');

    invalidErrorElements.forEach(element => {
        element.classList.remove('invalid-feedback-frontEnd');
        element.classList.add('invalid-feedback');
    });
}

function validateFromDateToDate(field, compareField, requiredMessage, futureMessage, rangeMessage) {
	const today = new Date().toISOString().split('T')[0];
	const value = field.val();

	// Required validation
	if (!value) {
		field.addClass(errorClass);
		requiredMessage.removeClass(displayNone);
		return false;
	}
	// Future date validation
	else if (value < today) {
		field.addClass(errorClass);
		futureMessage.removeClass(displayNone);
		return false;
	}
	// Date range validation
	else if (compareField && compareField.val() && value <= compareField.val()) {
		field.addClass(errorClass);
		rangeMessage.removeClass(displayNone);
		return false;
	} else {
		field.addClass(validClass);
		return true;
	}
}

function integrateModalNicEditor(element) {
    // Initialize nicEditor
    const editor = new nicEditor({
        fullPanel: true,
        xhtml: true,
        buttonList: ['bold', 'italic', 'underline', 'html', 'fontSize', 'fontFamily']
    });
    editor.panelInstance(element.id);

    // Apply styles to nicEdit panel container
    $('.nicEdit-panelContain').parent().css({
        'width': '100%',
        'overflow': 'visible'
    });

    // Apply styles to nicEdit main editor
    const mainEditorStyles = {
        'height': '300px',
        'overflow-x': 'hidden',
        'overflow-y': 'auto',
        'white-space': 'pre-wrap',
        'font-family': 'monospace'
    };
    $('.nicEdit-main').css(mainEditorStyles);

    // Remove width styling for main editor and parent on hover
    const removeWidthStyle = function () {
        $('.nicEdit-main').css('width', '');
    };

    $('.nicEdit-main').each(function () {
        $(this).parent().css('width', '');
    }).hover(removeWidthStyle);

    $('.nicEdit-main').parent().hover(removeWidthStyle);
}

function otherCandidateDetails(){
	var id = $('#userId').val();
	if(id!=null && id!=""){
		var url = contextPath + profileURL + "/" + id;
		window.location.href = url;
	}
}
function accomodationRequest(){
	var url = contextPath + accommodationURL+ '/'+requestKey;
	window.location.href = url;
}

function isValidFileExtension(inputElem) {
	return (/(\.png|\.jpg|\.jpeg|\.svg)$/i).exec(inputElem.val());
}

function showSelectedImage(input, imgBox) {
    const inputElem = $('#' + input.id);
    let errorDiv = inputElem.siblings('.invalid-feedback');
    errorDiv.html(inputElem.siblings('.invalid-value-feedback').html());
    inputElem.removeClass("is-invalid");
    errorDiv.html("");
    let defaultSrc = $('#' + imgBox).attr('data-default');
    if (defaultSrc === undefined) {
        defaultSrc = ''
    }
    $('#' + imgBox).attr('src', defaultSrc);
    if (input.files && input.files[0]) {
        const file = input.files[0];
        const maxSize = 2 * 1024 * 1024;
        if (file.size > maxSize) {
            inputElem.addClass("is-invalid");
            errorDiv.html(inputElem.siblings('.invalid-size-feedback').html());
            resetFileValue(input);
        } else if (!isValidFileExtension(inputElem)) {
            inputElem.addClass("is-invalid");
            errorDiv.html(inputElem.siblings('.invalid-img-feedback').html());
            resetFileValue(input);

        } else {
            const reader = new FileReader();
            reader.onload = function (e) {
                const img = new Image();
                img.onload = function () {
                    $('#' + imgBox).attr('src', e.target.result);
                    inputElem.removeClass("is-invalid");
                };
                img.src = String(e.target.result);
            };
            reader.readAsDataURL(file);
        }
    }
}

function showSelectedImageAndPDF(input, imgBox) {
    const inputElem = $('#' + input.id);
    let errorDiv = inputElem.siblings('.invalid-feedback');
    errorDiv.html(inputElem.siblings('.invalid-value-feedback').html());
    inputElem.removeClass("is-invalid");
    errorDiv.html("");

    let defaultSrc = $('#' + imgBox).attr('data-default') || '';

    // Reset image preview to default
    $('#' + imgBox).attr('src', defaultSrc).show();

    if (input.files && input.files[0]) {
        const file = input.files[0];
        const maxSize = 2 * 1024 * 1024; // 2MB size limit
        const fileExtension = file.name.split('.').pop().toLowerCase();

        if (file.size > maxSize) {
            inputElem.addClass("is-invalid");
            errorDiv.html(inputElem.siblings('.invalid-size-feedback').html());
            resetFileValue(input);
        } else if (!['jpg', 'jpeg', 'png', 'pdf'].includes(fileExtension)) {
            inputElem.addClass("is-invalid");
            errorDiv.html(inputElem.siblings('.invalid-img-feedback').html());
            resetFileValue(input);
        } else {
            if (fileExtension === 'pdf') {
                // If file is a PDF, render the first page as an image
                const fileReader = new FileReader();
                fileReader.onload = function () {
                    const pdfData = new Uint8Array(this.result);
                    pdfjsLib.getDocument({ data: pdfData }).promise.then(pdf => {
                        pdf.getPage(1).then(page => {
                            let scale = 1.5;
                            let viewport = page.getViewport({ scale: scale });
                            let canvas = document.createElement("canvas");
                            let context = canvas.getContext("2d");
                            canvas.width = viewport.width;
                            canvas.height = viewport.height;
                            let renderContext = {
                                canvasContext: context,
                                viewport: viewport
                            };
                            page.render(renderContext).promise.then(() => {
                                // Convert the canvas to a data URL and set it as the image source
                                $('#' + imgBox).attr('src', canvas.toDataURL("image/png")).show();
                            });
                        });
                    });
                };
                fileReader.readAsArrayBuffer(file);
            } else {
                // If file is an image, display it
                const reader = new FileReader();
                reader.onload = function (e) {
                    $('#' + imgBox).attr('src', e.target.result).show();
                };
                reader.readAsDataURL(file);
            }
        }
    }
}

// Preview SVG/PNG/JPG
function previewImage(input, previewIds, validTypes) {
	const inputElem = $('#' + input.id);
	let errorDiv = inputElem.siblings('.invalid-feedback');
	errorDiv.html(inputElem.siblings('.invalid-value-feedback').html());
	inputElem.removeClass("is-invalid");
	errorDiv.html("");

	previewIds.forEach(function (previewId) {
		let defaultSrc = $(previewId).attr('data-default');
		if (defaultSrc === undefined) {
			defaultSrc = '';
		}
		$(previewId).attr('src', defaultSrc);
	});

	if (input.files && input.files[0]) {
		const file = input.files[0];
		const maxSize = 2 * 1024 * 1024;

		if (file.size > maxSize) {
			inputElem.addClass("is-invalid");
			errorDiv.html(inputElem.siblings('.invalid-size-feedback').html());
			resetFileValue(input);
			return;
		}

		const fileType = file.type.toLowerCase();
		if (validTypes && !validTypes.includes(fileType)) {
			inputElem.addClass("is-invalid");
			errorDiv.html(inputElem.siblings('.invalid-format-feedback').html());
			resetFileValue(input);
			return;
		} else if (fileType !== 'image/svg+xml' && fileType !== 'image/png' && fileType !== 'image/jpeg') {
			inputElem.addClass("is-invalid");
			errorDiv.html(inputElem.siblings('.invalid-format-feedback').html());
			resetFileValue(input);
			return;
		}

		const reader = new FileReader();
		reader.onload = function (e) {
			previewIds.forEach(function (previewId) {
				$(previewId).attr('src', e.target.result);
			});
			inputElem.removeClass("is-invalid");
		};
		reader.readAsDataURL(file);
	}
}

function validateRadioGroup(radioButtons) {
    let valid = true;
    const groups = {};

    radioButtons.each(function () {
        const name = $(this).attr('name');
        if (!groups[name]) {
            groups[name] = [];
        }
        groups[name].push(this);
    });

    for (let name in groups) {
        const checked = groups[name].some(radio => $(radio).is(':checked'));
        const radioGroup = $(groups[name][0]).closest('.form-group-upload');

        if (!checked) {
            radioGroup.addClass('is-invalid');
            radioGroup.find('.checkRadio-bg').addClass('is-invalid');
            radioGroup.find('.invalid-feedback').show();
            valid = false;
        } else {
            radioGroup.removeClass('is-invalid');
            radioGroup.find('.checkRadio-bg').removeClass('is-invalid');
            radioGroup.find('.invalid-feedback').hide();
        }
    }

    return valid;
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


/* Dynamic tab table horizontal scroll logic */
function smoothHorizontalScroll(container, distance, duration = 350) {
    const start = container.scrollLeft;
    const startTime = performance.now();

    function animate(currentTime) {
        const timeElapsed = currentTime - startTime;
        const progress = Math.min(timeElapsed / duration, 1);
        container.scrollLeft = start + (distance * progress);
        if (progress < 1) {
            requestAnimationFrame(animate);
        }
    }
    requestAnimationFrame(animate);
}

$(document).on('click', '#scrollLeft', function () {
    const container = $(this).closest('.themeForm').find('.table-responsive')[0];
    if (container) {
        smoothHorizontalScroll(container, -400);
    }
});

$(document).on('click', '#scrollRight', function () {
    const container = $(this).closest('.themeForm').find('.table-responsive')[0];
    if (container) {
        smoothHorizontalScroll(container, 400);
    }
});

function validateMobileStart(input) {
    let value = input.value.replace(/[^0-9]/g, '');
    if (value.startsWith('0')) {
        showToast('Error', 'Mobile Number should not start with 0');
        input.value = '';
        input.focus();
        return false;
    }
}