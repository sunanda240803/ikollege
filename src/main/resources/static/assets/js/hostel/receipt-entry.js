$(document).ready(function () {
    $("#saveButton").on('click', function (e) {
        e.preventDefault();
        updateInvalidDivClass();
        const requiredFields = $('input[required]:visible,select[required]:visible,textarea[required]:visible');
        let result = validateRadioAndText(requiredFields);
        $(this).attr('disabled', true);
        if (result) {
            $.ajax({
                url: contextPath + baseURL + '/' + $('#studentId').val(),
                type: 'GET',
                success: function (response) {
                  if(response !== null && response!==''){
                    $('#studentId').addClass(errorClass)
                    $('#studentIdValidationDiv').text(response);
                  }
                  else{
                    $('#studentId').addClass(validClass)
                    $('#studentIdValidationDiv').text("IITM Student ID is Required.");
                    $('#receipt-entry-form').submit();
                  }
                },
                error: function (error) {

                }
            });
        } else {
            $(this).attr('disabled', false);
        }
    });

    $('#ddChequeNo').closest('.col-lg-3').hide();
    $('#ddChequeDate').closest('.col-lg-3').hide();
    $('#bankName').closest('.col-lg-3').hide();

    function toggleFieldsBasedOnBankSelection() {
        const bankDropdown = $('#selectBank');
        const ddChequeNo = $('#ddChequeNo');
        const ddChequeDate = $('#ddChequeDate');
        const bankName = $('#bankName');

        if (bankDropdown.val() === 'CASH' || bankDropdown.val() === '') {
            ddChequeNo.closest('.col-lg-3').hide();
            ddChequeDate.closest('.col-lg-3').hide();
            bankName.closest('.col-lg-3').hide();

            ddChequeNo.removeAttr('required');
            ddChequeDate.removeAttr('required');
            bankName.removeAttr('required');
        } else {
            ddChequeNo.closest('.col-lg-3').show();
            ddChequeDate.closest('.col-lg-3').show();
            bankName.closest('.col-lg-3').show();

            ddChequeNo.attr('required', 'required');
            ddChequeDate.attr('required', 'required');
            bankName.attr('required', 'required');
        }
    }

    $('#selectBank').on('change', function () {
        toggleFieldsBasedOnBankSelection();
    });

    toggleFieldsBasedOnBankSelection();

});