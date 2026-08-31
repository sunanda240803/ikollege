$(document).ready(function() {
});

function checkAdvanceFilter() {
	const requiredFields = $('select[required]');
	var isValid = validateRadioAndText(requiredFields);
	if (isValid) {
		let filters = $("#filters").val().replace(/[\[\]{}]/g, '').split(',').map(f => f.trim().split('=')[0]);
		filters.forEach(filter => {
			let inputName = `additionalParam.${filter}`;
			let value = $(`#${filter}`).val() || '';
			$(`input[name="${inputName}"]`).val(value);
		});
		let page = $('#pageVal').val();
		let size = $('#sizeVal').val();
		//Due to search field not available, used a static variable
		$('#search').val("0");
		let search = $('#search').val();
		createUrlWithParams(page, size, search);
	}
}

function handleActionClick(element) {
	checkInAndOut(element);
}

//function checkInAndOut(element) {
//	const url = element !== null ? element.getAttribute("data-url") : null;
//	const status = element !== null ? element.getAttribute("data-bs-title") : null;
//	$.ajax({
//		url: contextPath + "/" + url,
//		type: "Get",
//		success: function(response) {
//			$('#deleteModalText').text('Do you want to ' +status+ ' the student?');
//			$('#infoModal').modal('show');
//			$('#modalCheckStatusDiv').html(response);
//			$("#infoModalPositive").on("click", function(e) {
//				$('#infoModal').modal('hide');
//				e.preventDefault();
//				$('#regularStudentStatusId').submit();
//			});
//		}
//	});
//}



function checkInAndOut(element) {
    const url = element !== null ? element.getAttribute("data-url") : null;
    const status = element !== null ? element.getAttribute("data-bs-title") : null;

	const statusStr = status.includes('Check Out')  ? 'Check Out' : status;
    $('#deleteModalText').text('Do you want to ' +statusStr+ ' the student?');
	$('#infoModal').modal('show');
	 const clickedBtn = element;
    $('#infoModalPositive').click(function () {
        $('#infoModal').modal('hide');
        fetch(contextPath + "/" + url, {
            method: 'POST'
        })
        .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
        .then(data => {
            console.log(data);
            hideLoader();
            if(data.status=='Success'){
                let finalStatusText = '';
                let finalClass = '';
                if (status === 'Check In') {
                    finalStatusText = 'Checked In';
                    finalClass = 'text-success fw-bold';
                } else if (status.includes('Check Out')) {
                    finalStatusText = 'Checked Out';
                    finalClass = 'text-danger fw-bold';
                }

                 clickedBtn.outerHTML =
                        `<span class="${finalClass}">${finalStatusText}</span>`;

                clickedBtn.disabled = true;
                showToast('Success', data.message);
            } else {
                showToast('Error', data.error)
            }
        })
        .catch(error => {
            hideLoader();
            showToast('Error', savedError);
        });

        $('#infoModalPositive').off('click');
    });
}
