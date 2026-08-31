$(document).ready(function() {
    $("input[name='deliveryType']").on("change", toggleDeliverySection);
    toggleDeliverySection();
    renderCart();

    let phNumMaskElementArray = ['#mobileNumber', '#contactNumber']
    inputMask(phNumMaskElementArray, mobileNumberMask);
});

function minusPlus(doMinus, element) {
    const minus = $('#' + element.id);
    const input = $('#' + minus.attr('data-for'));
    const itemId = minus.attr('data-item-id');
    const maxCount = minus.attr('data-max-count');
    const showId = minus.attr('data-show-id');
    if (input) {
        if (doMinus) {
            if (input.val() > 0) {
                const val = +input.val()-1;
                input.val(val);
                manualQuantityUpdate(itemId, val);
            }
        } else {
            const val = +input.val() + 1;
            if(val > parseInt(maxCount)) {
                alert('Maximum purchase count reached for the show');
                return false;
            }
            const item = cart.find(cartItem => cartItem.id === itemId);
            //check student purchase limit
            if (item !== null) {
                if (!checkStudentLimit(item.price)) {
                    return false;
                }
            }

            const totalCount = cart.filter(cartItem=>cartItem.showId === showId)
                .reduce((sum, item) => {
                return sum + (item.quantity);
            }, 0);
            if(totalCount >= maxCount){
                alert('Maximum purchase count reached for the show');
                return false;
            }

            input.val(val);
            manualQuantityUpdate(itemId, val);
        }
    }
}

function toggleDeliverySection() {
    if ($("#delivery").is(":checked")) {
        $("#deliverySection").removeClass(displayNone);
        $(".delivery-required").attr("required", true);
    } else {
        $("#deliverySection").addClass(displayNone);
        $(".delivery-required").removeAttr("required");
    }
}

$("#purchase").off("click").on("click", function (e) {
    const purchaseAmount = parseFloat($("#totalPurchaseAmount").val());
    if(isNaN(purchaseAmount) || purchaseAmount<=0){
        alert("Total Amount must be greater than 0");
        return false;
    }
    const status = valRequiredMultiTextRadio('#pickUp,#delivery,#termsCondition');
    if(status){
        let selectedOption = $("input[name='deliveryType']:checked").val();
        if(selectedOption === 'Delivery') {
            const requiredFields = $('input[required], select[required], textarea[required]');
            let result = valRequiredMultiTextRadio(requiredFields);
            let mobileNumberValid = false;
            let reqMobileField = ['#contactNumber'];
            reqMobileField.forEach(function(element1) {
                let element = $(element1);
                let errorDiv = element.siblings('.invalid-feedback');
                if (!!!element.val()) {
                    element.addClass("is-invalid");
                    $(element).removeClass(validClass);
                    errorDiv.html(element.siblings('.invalid-req-feedback').html());
                    mobileNumberValid = false;
                } else if (!!element.val() && $(element).inputmask("unmaskedvalue").length !== 10) {
                    $(element).addClass(errorClass);
                    $(element).removeClass(validClass);
                    errorDiv.html(element.siblings('.invalid-val-feedback').html());
                    mobileNumberValid = false;
                } else {
                    $(element).addClass(validClass).removeClass(errorClass);
                    mobileNumberValid = true;
                }
            });
            if (result && mobileNumberValid) {
                $("#purchase").attr('disabled', true);
                $('#show-tickets-t-shirts-form').submit();
            } else {
                return false;
            }
        }
        else{
            $("#purchase").attr('disabled', true);
            $('#show-tickets-t-shirts-form').submit();
        }
    }
});


let cart = [];

function addItemToCart(item) {
    const id = item.id;
    const name = item.getAttribute("seat-name");
    const price = parseFloat(item.getAttribute("seat-price"));
    const showId = item.getAttribute("show-id");
    const maxCount = item.type === "radio" ? 1 : item.getAttribute("seat-count");
    const quantity = 1;

    if (item.type === "radio") {
        const existingRadio = cart.find(cartItem => cartItem.showId === showId);
        if (existingRadio && existingRadio.id === id) {
            item.checked = false;
            cart = cart.filter(cartItem => cartItem.id !== id);
            const seatCountInput = document.getElementById("seatCount_" + id);
            if (seatCountInput) seatCountInput.value = 0;
            renderCart();
            return;
        }

        cart = cart.filter(cartItem => cartItem.showId !== showId);
        document.querySelectorAll(`input[show-id="${showId}"]`).forEach(seat => {
            const seatCountInput = document.getElementById("seatCount_" + seat.id);
            if (seatCountInput) seatCountInput.value = 0;
        });
    }

    //check student purchase limit
    if (item.checked && !checkStudentLimit(quantity * price)) {
        item.checked = false;
        return false;
    }

    // Set the seat count
    $(`#seatCount_${id}`).val(1);

    // Check for existing items in the cart
    const existingItem = item.type === "checkbox"
        ? cart.find(cartItem => cartItem.id === id)
        : cart.find(cartItem => cartItem.showId === showId);

    if (existingItem) {
        if (item.type === "checkbox") {
            // Remove checkbox item from the cart
            cart = cart.filter(cartItem => cartItem.id !== id);
            $(`#seatCount_${id}`).val(0);
        } else {
            // Remove existing radio button item
            $(`#seatCount_${existingItem.id}`).val(0);
            cart = cart.filter(cartItem => cartItem.showId !== showId);
            cart.push({ id, name, price, quantity, showId, maxCount });
        }
    } else {
        if(item.type === "checkbox") {
            const existingCheckBoxItem = cart.find(cartItem => cartItem.showId === showId);
            const totalCountForShow = cart.filter(cartItem=>cartItem.showId === showId)
                .reduce((sum, item) => {
                    return sum + (item.quantity);
                }, 0);

            if (existingCheckBoxItem && existingCheckBoxItem.quantity === parseInt(existingCheckBoxItem.maxCount) ||(totalCountForShow >= maxCount)) {
                item.checked = false;
                alert("Maximum purchase count reached for the show");
                return;
            }
        }
        // Add new item to the cart
        cart.push({ id, name, price, quantity, showId, maxCount });
    }

    // Re-render the cart
    renderCart();
}


// Update quantity
function updateQuantity(itemId, change) {
    const item = cart.find(i => i.id === parseInt(itemId));
    if (item) {
        item.quantity = Math.max(1, item.quantity + change); // Ensure quantity doesn't drop below 1
        renderCart();
    }
}

// Calculate total amount
function calculateTotal() {
    return cart.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2);
}

// Render the cart items
function renderCart() {
    const selectedItemsDiv = document.getElementById("selected-items");
    selectedItemsDiv.innerHTML = ""; // Clear previous items
    cart.forEach(item => {
        const price = parseFloat(item.price) || 0;
        // Determine if buttons should be disabled
        const isDisabled = item.maxCount === 1 ? "disabled" : "";
        selectedItemsDiv.innerHTML += `
      <div class="left-right-text border-bottom pb-2 mb-2">
        <div>
          <h6 class="mb-0">${item.name}</h6>
          <small class="text-muted">₹ ${price.toFixed(2)}</small>
        </div>
        <div class="input-group w-auto">
          <button type="button" class="btn btn-outline-danger" data-for="qty_${item.id}" ${isDisabled} data-item-id="${item.id}"
                  onclick="minusPlus(true, this)" id="minus_${item.id}" ><i class="fa fa-minus"></i></button>
          <input class="form-control form-control-sm qty-input" value="${item.quantity}" ${isDisabled} type="number" id="qty_${item.id}" onchange="manualQuantityUpdate(${item.id}, this.value)">
          <button type="button" class="btn btn-outline-success" data-for="qty_${item.id}" ${isDisabled} data-item-id="${item.id}" data-show-id="${item.showId}"
                  onclick="minusPlus(false, this)" id="plus_${item.id}" data-max-count="${item.maxCount}" ><i class="fa fa-plus"></i></button>
        </div>
      </div>
    `;
    });

    // Update total amount
    const totalPurchase = calculateTotal();
    document.getElementById("total-amount").textContent = totalPurchase;
    document.getElementById("totalPurchaseAmount").value = parseFloat(totalPurchase);
}

// Manual quantity update from input
function manualQuantityUpdate(itemId, value) {
    const item = cart.find(i => i.id === itemId);
    if (item) {
        // Initial Render
        renderCart();
        $('#seatCount_'+itemId).val(parseInt(value));
        item.quantity = Math.max(1, parseInt(value) || 1);
        renderCart();
    }
}

// Purchase Items
function purchaseItems() {
    alert("Thank you for your purchase!");
}

function checkStudentLimit(additionalAmount) {
    let totalPurchasedItem = parseFloat($('#totalPurchasedAmount').val()) || 0;
    const studentLimit = parseFloat($('#creditLimit').val()) || 0;

    if (studentLimit > 0) {
        if (Array.isArray(cart) && cart.length > 0) {
            totalPurchasedItem += cart.reduce((sum, item) => {
                const price = parseFloat(item.price) || 0;
                const quantity = parseInt(item.quantity, 10) || 0;
                return sum + (price * quantity);
            }, 0);
        }
        totalPurchasedItem += parseFloat(additionalAmount);

        if (totalPurchasedItem > studentLimit) {
            alert(`Maximum amount that can be spent by a student is Rs. ${studentLimit.toFixed(2)}/-`);
            return false;
        }
    }
    return true;
}

function showChartModal(chartType, element) {
    const imageLocation = element.getAttribute('data-location') || '';
    const imageName = imageLocation.split('/')[4];
    const defaultImages = {
        show: "/assets/img/student/img_4.png",
        sizeChart: "/assets/img/student/img_3.png",
    };

    // Helper function to set the image source
    function setImageSource(imageId, fallbackImage) {
        const imgElement = document.getElementById(imageId);
        imgElement.src = (imageLocation && imageName !== 'null' && imageName !== '')
            ? contextPath + imageLocation
            : contextPath + fallbackImage;
    }

    if (chartType === 'show') {
        setImageSource('oatLayout', defaultImages.show);
        $('#oat-layout-modal').modal('show');
    } else {
        setImageSource('sizeChart', defaultImages.sizeChart);
        $('#size-chart-modal').modal('show');
    }
}



