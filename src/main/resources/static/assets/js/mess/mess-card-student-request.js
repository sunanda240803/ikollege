
$(document).ready(function() {
	
	 if (modalError === true) {
        messToCardAmountTransfer();
    }

    $('#addNewId').click(function () {
        messToCardAmountTransfer();
    });

});

function validateAmountTransferred() {
    let amountTransferred = $('#amountTransferred');
   let ledgerBalance = parseFloat($('#ledgerBalanceId').val().replace(/,/g, '') || 0);
    let transferValue = amountTransferred.val().trim(); 
    let transferAmount = parseInt(transferValue || 0);
    let maxAmount =  parseFloat($('#maxAmount').val() || 0).toFixed(2);
   let totalFinalAmount = parseFloat($('#totalFinalAmount').val() || 0); 

    
    $('.errMsg').addClass(dNone);
    amountTransferred.removeClass(errorClass).removeClass(validClass);
   
    if (transferValue === '') {
        $('.amountTransferredReq').removeClass(dNone).text(messages.amountRequired); 
        amountTransferred.addClass(errorClass);
        return false; 
    }   else if (transferAmount > maxAmount) {
        $('.amountTransferredReq').removeClass(dNone).text(messages.amountLessThan1000 + maxAmount); 
        amountTransferred.addClass(errorClass);
        return false;
    } else if (transferAmount > ledgerBalance) {
        $('.amountTransferredReq').removeClass(dNone).text(messages.insufficientBalance); 
        amountTransferred.addClass(errorClass);
        return false;
    } else if (transferAmount + totalFinalAmount > maxAmount) { 
        $('.amountTransferredReq').removeClass(dNone).text(messages.checkPreviousRequest); 
        amountTransferred.addClass(errorClass);
        return false;
    
    } else {
        amountTransferred.addClass(validClass).removeClass(errorClass);
    }

    return true;
}




function messToCardAmountTransfer() {
	$.ajax({
		url: contextPath + baseURL + "/" + 0,
		type: "GET",
		success: function(response) {
			$('#modalDiv').html(response);
             selectAndFormatCurrencyTags();
			$('#money-transfer-modal').modal('show');

			$('#money-transfer-modalSave').off("click").on("click", function(e) {
				e.preventDefault();
				updateInvalidDivClass();
				const isValid = validateAmountTransferred();

				if (isValid) {
					$('#moneyTransferRequest').submit();
				} else {
					$('#money-transfer-modalSave').attr('disabled', false);
					return false;
				}
			});
			$("#money-transfer-modalValidateBackend").off("click").on("click", function(e) {
				$('#moneyTransferRequest').submit();
			})
		},
		error: function(error) {
			showToast('Error', 'Error occurred while loading the modal');
		}
	});
}

