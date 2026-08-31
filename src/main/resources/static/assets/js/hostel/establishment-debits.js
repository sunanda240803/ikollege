$(document).ready(function () {

  function toggleFields() {
    var fixedOption = $('#fixedOption').is(':checked');
    var variableOption = $('#variableOption').is(':checked');
    var excludeExchange = $('#excludeExchange').is(':checked');
    var uploadField = $('#uploadField');
    var amountField = $('#amountField');
    var amountInput = $('#amount');
    var fileInput = $('#upload_fileUpload');
    var studentDetailsTable = $('#studentDetailsTable');

    if (fixedOption || excludeExchange) {
      uploadField.addClass('d-none');
      amountField.removeClass('d-none');
      studentDetailsTable.removeClass('d-none');
      amountInput.prop('required', true);
      fileInput.prop('required', false);
    } else if (variableOption) {
      uploadField.removeClass('d-none');
      amountField.addClass('d-none');
      studentDetailsTable.addClass('d-none');
      amountInput.prop('required', false);
      fileInput.prop('required', true);
    }
  }

  toggleFields();

  $('input[name=selectOptions]').on('change', function () {
    toggleFields();
    $('#amount').val('').removeClass('is-invalid');
    $('#upload_fileUpload').val('').removeClass('is-invalid');
  });

  $('#submitForm').on('click', function () {
    saveEstablishmentList();
  });

  function validateInputs() {
    $(".is-invalid").removeClass('is-invalid')
    $('#uploadField').find('.invalid-feedback').hide();

    const requiredFields = $('input[required]:visible,select[required]:visible,textarea[required]:visible');
    const requiredSelect = $('#hostelName');
    const isSelValid = valRequiredSelection(requiredSelect);

    if(isSelValid == false) {
      requiredFields.closest('.form-group').find('.bootstrap-select').removeClass('is-valid')
      requiredFields.closest('.form-group').find('.bootstrap-select').addClass('is-invalid')
    } else {
      requiredFields.closest('.form-group').find('.bootstrap-select').addClass('is-valid')
      requiredFields.closest('.form-group').find('.bootstrap-select').removeClass('is-invalid')

    }

    let result = validateRadioAndText(requiredFields);

    console.log('isSelValid', isSelValid)

    if ($('input[name="selectOptions"]:checked').length === 0) {
      return false;
    }

    const selectedOption = $('input[name="selectOptions"]:checked').val();

    if (selectedOption !== 'variable') {
      const amount = $('#amount').val();
      if (amount === '') {
        $('#amount').addClass('is-invalid');
        $('#amountField').find('.invalid-feedback')
          .text('Amount is required')
          .show();
        return false;
      } else if (isNaN(amount)) {
        $('#amount').addClass('is-invalid');
        $('#amountField').find('.invalid-feedback')
          .text('Please enter a valid number')
          .show();
        return false;
      }
    } else if (selectedOption === 'variable') {
      const fileInput = $("#upload_fileUpload")[0];

      if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
        $('#upload_fileUpload').addClass('is-invalid');
        $('#uploadField').find('.invalid-feedback')
          .text('Please select a file')
          .show();
        return false;
      }

      const file = fileInput.files[0];

      if (file.type !== 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' &&
        file.type !== 'application/vnd.ms-excel' &&
        file.type !== 'application/vnd.ms-office') {
        $('#upload_fileUpload').addClass('is-invalid');
        $('#uploadField').find('.invalid-feedback')
          .text('Invalid file type. Please upload a valid Excel file')
          .show();
        return false;
      }
    }

    return true;
  }

  $('#upload_dropZone').off('click').on('click', function (e) {
    e.preventDefault();
    e.stopPropagation();
    $("#uploadField").click();
  });

  $('#getDetails').bind('click', function (e) {
    e.preventDefault();
    validateInputs()
  });

  function calculateTotalAmount() {
    var totalAmount = 0;
    $('.total').each(function () {
      var amount = parseFloat($(this).val()) || 0;
      totalAmount += amount;
    });
    console.log('totalAmount', totalAmount)
    $('#total-amount').val(totalAmount.toFixed(2));
  }

  calculateTotalAmount();

  $('.total').on('input', function () {
    calculateTotalAmount();
  });

  $('#amount').on('input', function () {
    $(this).removeClass('is-invalid');
    $('#amountField').find('.invalid-feedback').hide();
  });

  $('#upload_fileUpload').on('change', function () {
    $(this).removeClass('is-invalid');
    $('#uploadField').find('.invalid-feedback').hide();
  });
});

function validateSearchField() {
  const hostelMaster = $('#hostelOrFacilityMaster');
  const requiredFields = $('input[required], select[required], textarea[required]');
  const isValid = validateRadioAndText(requiredFields);
  if (isValid) {
    let hostelId = hostelMaster.val();
    let date = $('#date').val();
    let description = $('#description').val();
    let amount = $('#amount').val();
    let selectedRadio = $('input[name="selectOptions"]:checked').val();
    $('input[name="additionalParam.hostelId"]').val(hostelId);
    $('input[name="additionalParam.description"]').val(description);
    $('input[name="additionalParam.fixed"]').val(selectedRadio);
    $('input[name="additionalParam.selectOptions"]').val(selectedRadio);
    $('input[name="additionalParam.amount"]').val(amount);
    $('input[name="additionalParam.date"]').val(date);

    let page = $('#pageVal').val();
    let size = $('#sizeVal').val();
    let search= $('#search').val();
    createUrlWithParams(page, size , search);
  } else {
    showToast('Error', 'Please enter all required fields correctly.');
    return false;
  }
  return isValid;
}

function saveEstablishmentList() {
  let date = $("#date").val();
  date = date ? new Date(date) : null;
  let hostelId = $("#hostelOrFacilityMaster").val();
  let description = $('#description').val();
  let amount = $('#amount').val();
  let totalAmount = $('#total-amount').val();
  let selectedRadio = $('input[name="selectOptions"]:checked').val();

  const dataToSend = {
    date: date,
    hostelId: hostelId,
    description: description,
    amount: parseFloat(amount),
    totalAmount: parseFloat(totalAmount),
    selectedOption: selectedRadio
  };
  showLoader();
  $.ajax({
    url: `${contextPath}${baseURL}${saveEstablishmentListURL}`,
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(dataToSend),
    success: function (response) {
        hideLoader();
      if (response === 'saved'){
        showToast('Success', 'Establishment List Saved Successfully!');
        setTimeout(function() {
          location.reload();
        }, 2000);
      } else {
          hideLoader();
        showToast('Error', 'Something Went Wrong!');
      }
    },
    error: function (error) {
      showToast('Error', error.responseText);
    }
  });
}

function validateFile() {
  const fileInput = $('#uploadFile1_fileUpload')[0];
  const file = fileInput.files[0];
  const errorDiv = $('#documentUploadSection .invalid-feedback');

  if (!file) {
    errorDiv.text('Please select a file before submitting.').show();
    return;
  } else {
    errorDiv.text('').hide();
  }

  const formData = new FormData();
  formData.append('file', file);
  formData.append('date', $('#date').val());
  formData.append('hostelId', $('#hostelOrFacilityMaster').val());
  formData.append('description', $('#description').val());
  formData.append('totalAmount', $('#total-amount').val());
  formData.append('selectOptions', $('input[name="selectOptions"]:checked').val());

  if (!$('#amountField').hasClass('d-none')) {
    formData.append('amount', $('#amount').val());
  }

  $.ajax({
    url: `${contextPath}${baseURL}${uploadURL}`,
    type: 'POST',
    data: formData,
    processData: false,
    contentType: false,
    success: function(response) {
      const $errorSection = $('.text-danger');
      const $errorList = $errorSection.find('ul');
      $errorList.empty();
      if (response.errorList && response.errorList.length > 0) {
        $errorSection.removeClass(displayNone);
        $.each(response.errorList, function(index, err) {
          $errorList.append($('<li>').text(err));
        });
      } else {
        $errorSection.addClass(displayNone);
        showToast('Success', 'Establishment Debit Details saved successfully!');
        setTimeout(function() {
          location.reload();
        }, 2000);
      }
    }
  });
}