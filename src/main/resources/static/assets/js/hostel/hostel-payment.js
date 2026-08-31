$(document).ready(function() {
	$('#addNewId').click(function() {
		addHostelPayment();
	});

	if (modalError === true) {
		addHostelPayment();
	}
});

async function addHostelPayment() {
	const id = 0;

	const response = await fetch(contextPath + baseURL + "/0", {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	});

	const modalHtml = await response.text();
	$('#modalDiv').html(modalHtml);
	$('#hostel-payment-modal').modal('show');
	updateSaveButtonStyle(id, $('#show-event-master-modalSave'));

	$("#hostel-payment-modalSave").off("click").on("click", async function (e) {
		e.preventDefault();
		updateInvalidDivClass();
		$(this).attr('disabled', true);
		const requiredFieldsMap = {
			ICOLLECT: '#paymentAmount,#paymentDate,#duNumber,#confirmDunumber',
			DD: '#paymentAmount,#paymentDate,#ddNumber,#ifscCode',
			BANK_LOAN: '#paymentAmount,#paymentDate,#loanAccNo,#bankName,#branchName'
		};

		let selectedOption = $("input[name='paymentType']:checked").val();
		let requiredFields = requiredFieldsMap[selectedOption] || [];
		let status = requiredFields.length ? valRequiredMultiTextRadio(requiredFields) : true;

		if (selectedOption === 'ICOLLECT' && status) {
			const confirmDunumberElement = $('#confirmDunumber');
			let errorDiv = confirmDunumberElement.siblings(invalidFeedback);

			if ($('#duNumber').val() !== $('#confirmDunumber').val()) {
				errorDiv.html(errorDiv.siblings('.invalid-feedback-duNumber').html());
				confirmDunumberElement.addClass(errorClass).removeClass(validClass);
				status = false;
			} else {
				errorDiv.html(errorDiv.siblings(invalidFeedbackReq).html());
				confirmDunumberElement.addClass(validClass).removeClass(errorClass);
				status = true;

				// Check if DU number exists
				const existsStatus = await checkDuNumberExists($('#duNumber').val().trim());
				const duNumberElement = $('#duNumber');
				const duErrorDiv = duNumberElement.siblings(invalidFeedback);

				if (existsStatus) {
					duNumberElement.addClass(errorClass).removeClass(validClass);
					duErrorDiv.html('Ref. Number already exists');
					status = false;
				} else {
					duErrorDiv.html(duErrorDiv.siblings(invalidFeedbackReq).html());
					duNumberElement.addClass(validClass).removeClass(errorClass);
					status = true;
				}
			}
		}
		else if (selectedOption === 'DD' && status) {
			// Check if DU number exists
			status = await validateAndCheckIfscCode(document.getElementById('ifscCode'),'');
		}

		if (status) {
			$('#hostelPaymentForm').submit();
		}
		else{
			$(this).attr('disabled', false);
		}
	});

	$("#hostel-payment-modalValidateBackend").off("click").on("click", function (e) {
		$('#hostelPaymentForm').submit();
	})
}

function checkFeePaymentOption(paymentType) {
	const fields = {
		all: $('.dd, .bank, .icollect, .common, .positive'),
		common: $('.common'),
		icollect: $('.icollect'),
		dd: $('.dd, .dd.bank'),
		bankLoan: $('.bank, .dd.bank'),
		positive: $('.positive'),
	};

	const inputs = {
		paymentDate: $('#paymentDate'),
		paymentAmount: $('#paymentAmount'),
		duNumber: $('#duNumber'),
		ifscCode: $('#ifscCode'),
		ddNumber: $('#ddNumber'),
		loanAccNo: $('#loanAccNo'),
		bankName: $('#bankName'),
		branchName: $('#branchName'),
	};

	function setInputState(inputKeys, state) {
		inputKeys.forEach((key) => inputs[key].prop(state.prop, state.value));
	}

	fields.all.addClass(dNone);
	setInputState(
		['paymentDate', 'paymentAmount', 'duNumber', 'ifscCode', 'ddNumber', 'loanAccNo', 'bankName', 'branchName'],
		{ prop: 'disabled', value: true }
	);

	if (paymentType !== 'ALREADY_PAID') {
		fields.common.removeClass(dNone);
		setInputState(['paymentDate', 'paymentAmount'], { prop: 'disabled', value: false });
	}

	switch (paymentType) {
		case 'ICOLLECT':
			fields.icollect.removeClass(dNone);
			setInputState(['duNumber'], { prop: 'disabled', value: false });
			break;

		case 'DD':
			fields.dd.removeClass(dNone);
			setInputState(
				['bankName', 'branchName', 'ifscCode', 'ddNumber'],
				{ prop: 'disabled', value: false }
			);
			setInputState(['bankName', 'branchName'], { prop: 'readonly', value: true });
			break;

		case 'BANK_LOAN':
			fields.bankLoan.removeClass(dNone);
			setInputState(
				['bankName', 'branchName', 'loanAccNo'],
				{ prop: 'disabled', value: false }
			);
			setInputState(['bankName', 'branchName'], { prop: 'readonly', value: false });
			break;

		case 'ALREADY_PAID':
			fields.positive.removeClass(dNone);
			break;
	}
}

function deleteHostelPayment(element){
	const id = element !== null ? element.getAttribute("data-id") : 0;
	$('#deleteModal').modal('show');
	$('#deleteModalPositive').click(function () {
		$('#deleteModal').modal('hide');
		fetch(contextPath + baseURL + "/" + id
			, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(response => {
			return response.json();
		}).then(response => {
			if (response.status === 'Success') {
				$('#deletePayment_' + id).parents('tr').remove();
				showToast('Success', 'Hostel Payment deleted successfully');
			} else {
				showToast('Failure', response.message);
			}
		});
		$('#deleteModalPositive').off('click');
	})
}

function validatePaymentDate(element,fromDate,toDate) {
	const inputDate = new Date(element.value);
	const startDate = new Date(fromDate);
	const endDate = new Date(toDate);

	const paymentDateElement = $('#paymentDate');
	const errorDiv = paymentDateElement.siblings(invalidFeedback);
	// Check if the input date is within the range
	if (inputDate < startDate || inputDate > endDate) {
		errorDiv.html(errorDiv.siblings('.invalid-feedback-invalid-date').html());
		paymentDateElement.addClass(errorClass).removeClass(validClass);
		element.value = '';
	}
	else{
		errorDiv.html(errorDiv.siblings(invalidFeedbackReq).html());
		paymentDateElement.addClass(validClass).removeClass(errorClass);
	}
}

function checkDuNumberExists(duNumber) {
	updateInvalidDivClass();
	return fetch(contextPath + baseURL + existsURL + "/" + duNumber, {
		method: 'GET',
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then(response => response.text())
		.then(response => response === 'true'); // Return a boolean
}


