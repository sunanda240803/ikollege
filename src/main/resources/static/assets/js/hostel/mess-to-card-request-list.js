$(document).ready(function() {
	$("#addNewId i").attr("class", "fa-solid fa-file-excel me-1");
	$('#addNewId').removeClass('btn-success').addClass('btn-pink');
	$('#addNewId span').text('Mess To Card Request List Excel');

	$("#updateId i").attr("class", "fa-solid fa-right-left me-1");
	$('#updateId span').text('Transfer');
	
	$('#updateId').click(function () {
        transferAmount();
    });
	
	$("#toggleFilter").click(function() {
		const isExpanded = $(this).attr("aria-expanded") === "true";
		const toggleFilterDiv = $(".toggleFilterSection");

		if (isExpanded) {
			$("#toggleFilterText").text("Hide Advanced Filter");
			$(".keywordSearch").addClass('d-none');
			toggleFilterDiv.removeClass("col-lg-6").addClass("col-lg-12");
		} else {
			$("#toggleFilterText").text("Show Advanced Filter");
			$(".keywordSearch").removeClass('d-none');
			toggleFilterDiv.removeClass("col-lg-12").addClass("col-lg-6");
		}
	});
	
	$('#addNewId').click(function() {
		let requestStatus = $('#requestStatus').val();
	    let requestDate = $('#requestDate').val();
	    let transferDate = $('#transferDate').val();
		let search = $('#search').val();
		$('#requestedStatus').val(requestStatus);
		$('#requestedDate').val(requestDate);
		$('#transferredDate').val(transferDate);
		$('#searchData').val(search);
		$('#excelDownloadForm').submit();
	});
});

function getMessCardRequestListValidate() {
	let requestStatus = $('#requestStatus').val();
    let requestDate = $('#requestDate').val();
    let transferDate = $('#transferDate').val();

    // Set these values in the additional param inputs
    $('input[name="additionalParam.requestedStatus"]').val(requestStatus);
    $('input[name="additionalParam.requestDate"]').val(requestDate);
    $('input[name="additionalParam.transferredDate"]').val(transferDate);

    // Call createUrlWithParams with the current page and size
    let page = $('#pageVal').val(); 
    let size = $('#sizeVal').val();
    let search= $('#search').val();
    createUrlWithParams(page, size , search);
}

function messCardRequestApproval(key) {
	var id = key.id;
	var status = key.getAttribute('data-bs-title');
	var rejectReason = null;
    $('#infoModal').modal('show');
    $('#infoModalPositive').off("click").on("click", function (e) {
        fetch(contextPath + baseURL + approveRejectRequest + "/" + id + "?status=" + status+ "&rejectReason="+rejectReason, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.text())
            .then(response => {
				console.log(response);
				console.log(response.status);
                if(response === 'true' || response !=null){
                    showToast('Success', 'The request has been approved successfully')
					setTimeout(function() {
			            window.location.href = contextPath + baseURL;
			        }, 3000);
                }
                else{
                    showToast('Error', "Failed to approve request.");
                }
            });
    })
}
function messCardRequestReject(key) {
	var id = key.id;
	var status = key.getAttribute('data-bs-title');
	var rejectReason = $('#reason').val("");
    $('#reject-request-modal').modal('show');
	$('#reject-request-modalValidateBackend').addClass(displayNone);
    $('#reject-request-modalSave').off("click").on("click", function (e) {
		rejectReason = $('#reason').val();
		if(rejectReason==null || rejectReason==""){
			$('#reason').addClass('is-invalid');
			return false;
		}else{
			$('#reason').removeClass('is-invalid');
			$('#reject-request-modal').modal('hide');
	        fetch(contextPath + baseURL + approveRejectRequest + "/" + id + "?status=" + status + "&rejectReason="+rejectReason, {
	            method: 'GET',
	            headers: {
	                'Content-Type': 'application/json'
	            }
	        })
	            .then(response => response.text())
	            .then(response => {
	                if(response === 'true' || response !=null){
	                    showToast('Success', 'The request has been rejected successfully');
						setTimeout(function() {
							window.location.href = contextPath + baseURL;
				        }, 3000);
	                }
	                else{
	                    showToast('Error', "Failed to reject request.");
	                }
	            });
			}
    })
}

function viewLedger(key) {
    const studentId = key.id;
    const bookType = $(key).data("id") === 'MS' ? 'Mess' : 'Card';
    try {
        const $ledgerForm = $("#ledgerForm");
        if ($ledgerForm.length) {
            $ledgerForm.find("input[name='studentID']").val(studentId);
            $ledgerForm.find("input[name='selectedCriteria']").val(bookType);
            $ledgerForm.trigger("submit");
        } else {
            showToast('Error', 'Something went wrong');
        }
    } catch (error) {
        showToast('Error', 'Something went wrong');
    }
}

function transferAmount(){
	let selectedStudentIds = [];
	let selectedAmounts = [];
	let selectedMessCardIds = [];
    document.querySelectorAll('#messToCardTableId tbody input[type="checkbox"]:checked').forEach(function (checkbox) {
        let row = checkbox.closest('tr'); 
        let studentId = checkbox.id;
		let messCardId = checkbox.getAttribute('data-id');
        let transferAmount = row.querySelector('td:nth-child(6)').innerText.trim();

        selectedStudentIds.push(studentId);
		selectedMessCardIds.push(messCardId);
        selectedAmounts.push(transferAmount);
    });
    if (selectedStudentIds.length === 0) {
       showToast('Error', "No student selected for amount transfer.");
       return false;
		}else{
			$('#infoModal').modal('show');
				$('#deleteModalText').text(transferMsg);
				$('#infoModalPositive').off("click").on("click", function (e) {
					$('#studentIdData').val(selectedStudentIds.join(","));
					$('#messCardIdData').val(selectedMessCardIds.join(","));
					$('#transferAmountData').val(selectedAmounts.join(","));
					$('#transferAmountForm').submit();
			});
		}
}