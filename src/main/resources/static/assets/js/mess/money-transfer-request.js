$(document).ready(function() {
    $("#moneyTransferRequestSave").on("click", function(e) {
        e.preventDefault();
        let amountTransferred = $('#amountTransferred');
        let errorCount = 0;
        $('.errMsg').addClass("d-none");
        
        if (amountTransferred.val() !== '') {
            let count = errorCount;
            errorCount += validateDecimalLength(amountTransferred, 10);
            if (count !== errorCount) {
                $('.amountTransferredLn').removeClass("d-none");
            }
        } else {
            amountTransferred.removeClass(validClass).addClass(errorClass);
            $('.amountTransferredReq').removeClass("d-none");
        }
    });
});
