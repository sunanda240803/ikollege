let ledgerValid = false;

function calculateTotal() {
    let vegPrice = parseInt($("#vegRate").val());
    let nonVegPrice = parseInt($("#nonVegRate").val());

    let vegCount = parseInt($("#vegCount").val()) || 0;
    let nonVegCount = parseInt($("#nonVegCount").val()) || 0;

    let vegTotal = vegCount > 0 ? vegCount * vegPrice : 0;
    let nonVegTotal = nonVegCount > 0 ? nonVegCount * nonVegPrice : 0;
    let totalAmount = vegTotal + nonVegTotal;

    $("#vegTotal").text("₹" + vegTotal);
    $("#nonVegTotal").text("₹" + nonVegTotal);
    $("#overallTotal").text("₹" + totalAmount);
    $("#totalAmountId").val(totalAmount);

    // Enable Pay button only if total amount > 0
    if (totalAmount > 0) {
        $("#payButton").prop("disabled", false);
    } else {
        $("#payButton").prop("disabled", true);
    }

    // Check if "Use Ledger Balance" is checked
    if ($("#useLedgerBalance").is(":checked")) {
        let isValid = checkLedgerBalance(); // Call ledger balance check
        if (!isValid) {
            return false;
        }
    }
}

$(document).ready(function () {
    let isValid = true;

    $("#useLedgerBalance").change(function () {
        if (this.checked) {
            checkLedgerBalance();
        } else {
            $("#ledgerBalanceInfo").addClass(dNone);
            $("#payButton").show();
            $("#saveButton").addClass(dNone);
            $('#errorSpanId').html('');
        }
    });

    $("#payButton").on("click", function (event) {
        let overallTotal = parseFloat($("#totalAmountId").val()) || 0;
        if (overallTotal <= 0) {
            event.preventDefault();
            $('#errorSpanId').html('Total amount must be greater than zero.');
            return;
        }

        isValid = checkCouponLimit();
        if (!isValid) {
            event.preventDefault();
            $(this).prop("disabled", true);
            return false;
        }

    });

    $("#saveButton").on("click", function (event) {
        let overallTotal = parseFloat($("#totalAmountId").val()) || 0;
        if (overallTotal <= 0) {
            event.preventDefault();
            $('#errorSpanId').html('Total amount must be greater than zero.');
            return;
        }

        isValid = checkCouponLimit();
        if (!isValid) {
            event.preventDefault();
            $(this).prop("disabled", true);
            return false;
        }

        isValid = checkLedgerBalance(); // Call ledger balance check
        if (!isValid) {
            event.preventDefault();
            return false;
        }

    });

    const maxCount = parseInt($('#maxCoupon').val()) || 0;
    let lastVeg = 0;
    let lastNonVeg = 0;

    $("#vegCount").on("input", function () {
        const vegCount = parseInt($(this).val()) || 0;
        let totalSavedCoupon = parseInt($("#noOfCouponsTotal").val()) || 0;
        let totalInputCoupon = (parseInt($("#vegCount").val()) || 0) + (parseInt($("#nonVegCount").val()) || 0);

        if ((totalInputCoupon + totalSavedCoupon) > maxCount) {
            $(this).val(lastVeg);
            alert("Total coupons cannot exceed " + maxCount);
        } else {
            lastVeg = vegCount;
        }
        calculateTotal();
    });

    $("#nonVegCount").on("input", function () {
        const nonVegCount = parseInt($(this).val()) || 0;
        let totalSavedCoupon = parseInt($("#noOfCouponsTotal").val()) || 0;
        let totalInputCoupon = (parseInt($("#vegCount").val()) || 0) + (parseInt($("#nonVegCount").val()) || 0);

        if ((totalInputCoupon + totalSavedCoupon) > maxCount) {
            $(this).val(lastNonVeg);
            alert("Total coupons cannot exceed " + maxCount);
        } else {
            lastNonVeg = nonVegCount;
        }
        calculateTotal();
    });
});

function checkCouponLimit() {
    let maxCoupon = parseInt($("#maxCoupon").val()) || 0;
    let totalSavedCoupon = parseInt($("#noOfCouponsTotal").val()) || 0;
    let totalInputCoupon = parseInt(parseInt($("#vegCount").val()) + parseInt($("#nonVegCount").val())) || 0;

    if (maxCoupon < (totalInputCoupon + totalSavedCoupon)) {
        $('#errorSpanId').html('No.Of Coupons Limit Exceeded.');
        return false;
    }
    return true;
}

function checkLedgerBalance() {
    $('#errorSpanId').html('');
    var ledgerBalance = parseFloat($("#ledgerBalance").val());
    $("#ledgerBalanceInfo").removeClass(dNone);

    let overallTotal = parseFloat($("#totalAmountId").val()) || 0;
    if (overallTotal === 0 || ledgerBalance >= overallTotal || ledgerBalance > 0) {
        $("#payButton").hide();
        $("#saveButton").removeClass(dNone);
        $('#errorSpanId').html('');
        return true;
    } else {
        $('#errorSpanId').html('Insufficient ledger balance! Please proceed with online payment.');
        $("#useLedgerBalance").prop("checked", false);
        $("#saveButton").addClass(dNone);
        $("#payButton").show().prop("disabled", false);
        return false;
    }
}