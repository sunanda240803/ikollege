$(document).ready(function () {
    $('#selectAllMess').on('change', function () {
        $('.messCheckbox').prop('checked', $(this).is(':checked'));
    });

    $('.messCheckbox').on('change', function () {
        const isChecked = $('#selectAllMess').is(':checked');
        if(isChecked) {
            $('#selectAllMess').prop('checked', false);
        }
    })
});

$(document).ready(function() {
    // Filter hostels based on search input
    $('#searchMessName').on('keyup', function() {
        const searchTerm = $(this).val().toLowerCase();

        if (searchTerm.length > 0) {
            // Hide the "Select All Hostels" section when searching
            $('#searchAllCheck').hide();
        } else {
            // Show the "Select All Hostels" section when search is cleared
            $('#searchAllCheck').show();
        }

        $('.searchMess .form-check').each(function() {
            const label = $(this).find('.form-check-label').text().toLowerCase();

            if (label.indexOf(searchTerm) !== -1) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });
});


$('#saveMess').on('click', function (e) {
    updateInvalidDivClass();
    e.preventDefault();
    $("#saveMess").attr('disabled', true);
    const requiredFields = $('input[required]');
    let result = validateRadioAndText(requiredFields);

    const isAnyChecked = $('.messCheckbox:checked').length > 0;

    $('.invalid-feedback.mt-2').hide();
    let isValid = true;

    if (!isAnyChecked) {

        $('.invalid-feedback.mt-2').show();
        isValid = false;
    }

    if(result){
        isValid = validateUser();
    }

    if (isValid) {
        $('#messCouponMappingForm').submit();
    }
    else{
        $("#saveMess").attr('disabled', false);
    }
});

function deleteCouponUserMapping(element) {
    const id = element.getAttribute('data-id');
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
                $('#' + element.id).parents('tr').remove();
                showToast(response.status, response.message);
            } else {
                showToast('Failure', response.message);
            }
        });
        $('#deleteModalPositive').off('click');
    });
}

