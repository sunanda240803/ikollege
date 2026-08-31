let isButtonClicked = false;
$(document).ready(function() {
	$("#applicationNoModalSave").off("click").on("click", function(e) {
		var applicationNo = $('#applicationNumber').val();
		const requiredFields = $('input[required]');
		const status = valRequiredMultiTextRadio(requiredFields);
		if (!status) {
			return false;
		}
		$.ajax({
			url: contextPath + getStudentRegistrationURL + validateApplicationNoURL,
			type: "Get",
			data: { applicationNo: applicationNo },
			success: function(response) {
				if (response) {
					window.location.href = contextPath + getPriorityURL + getTabURL;
				} else {
					$("#applicationNoModal").modal('hide');
					$('#biodataModal').modal('show');
				}
			},
			error: function() {
				console.error('Error occured.');
			}
		});

		return true;

	});

	$('#biodataModalPositive').off("click").on("click", function(e) {
		viewBioData();
	});
});


function loadInventoryFragment() {
    if (isButtonClicked) {
        return; 
    }
    isButtonClicked = true; 
    $.ajax({
        url: contextPath + dashboardURL + studentURL + getInventoryUrl,
        type: "GET",
        success: function (response) {
            $("#modalDiv").html(response);
            $('#view-inventory').modal('show');
        },
        error: function (xhr, status, error) {
            alert("Unable to load inventory details. Please try again later.");
        },
        complete: function () {
            isButtonClicked = false; 
        }
    });
}

// Temporary on click function
/*
function checkAndUpdateDaysScholarStatus(){
const url = contextPath + vacatingUrl + checkUpdateDaysScholarUrl;
        $.ajax({
            type: "POST",
            url: contextPath + vacatingUrl + checkUpdateDaysScholarUrl,
            success: function (response) {
               showToast(response !== 'ERROR' ? 'Success' : 'Error', response !== 'ERROR' ? 'Updated successfully' : 'Failure');
            },
            error: function (xhr, status, error) {
                showToast('Error', 'Failure');
            }
        });
}*/
