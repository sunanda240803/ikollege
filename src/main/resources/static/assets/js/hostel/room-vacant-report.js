document.addEventListener('DOMContentLoaded', function() {
    const hostelIdElement = document.getElementById('hostelId');
    if (hostelIdElement && hostelIdElement.value) {
        getHostelFloorList(hostelIdElement);
    }
});

function getHostelFloorList(element) {
    const hostelId = element.value;
	
    const url = getUrl(hostelFloorListURL) + `?hostelId=${hostelId}`;
	fetch(url, {		
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
			populateHostelFloorDropdown(data.data);
			//clearErrorMessages();
        })
        .catch(error => {
            console.error('Error fetching hostel floor list:', error);
        });
}

function populateHostelFloorDropdown(hostelFloorList) {
    const hostelFloorDropdown = document.getElementById("hostelFloorId");
    const selectedFloorIdElement = document.getElementById("floorId");
    const selectedFloorId = selectedFloorIdElement ? selectedFloorIdElement.value : null;
    hostelFloorDropdown.innerHTML = '<option value="" selected>Select</option>';
	const allOption = document.createElement("option");
	allOption.value = "0";
	allOption.textContent = "All";
	if (selectedFloorId === "0") {
        allOption.selected = true;
    }
	hostelFloorDropdown.appendChild(allOption);
	
    Object.entries(hostelFloorList).forEach(([id, floor]) => {
        const option = document.createElement("option");
        option.value = floor.id;
        option.textContent = floor.floorName;
        if (selectedFloorId && floor.id == selectedFloorId) {
            option.selected = true;
        }
        hostelFloorDropdown.appendChild(option);
    });
}

function validateHostelForm() {
	const isInvalidCalss = 'is-invalid';
    let valid = true;
    const hostelId = document.getElementById('hostelId');
    const hostelFloorId = document.getElementById('hostelFloorId');
    hostelId.classList.remove(isInvalidCalss);
    hostelFloorId.classList.remove(isInvalidCalss);

    if (!hostelId.value) {
        hostelId.classList.add(isInvalidCalss);
        valid = false;
		return valid;
    }
    if (!hostelFloorId.value) {
        hostelFloorId.classList.add(isInvalidCalss);
        valid = false;
		return valid;
    }
    return valid;
}

function triggerDownload() {
    const hostelId = document.getElementById('hostelId').value;
    const floorId = document.getElementById('hostelFloorId').value;
    const stableRoom = document.getElementById('stableRoomId').checked ? 'stableRoom' : '';

	const hostelIdElement = document.getElementById('hostelId');
    const hostelName = hostelIdElement.options[hostelIdElement.selectedIndex]?.text || '';

    const params = new URLSearchParams({
        hostelId: hostelId,
        floorId: floorId,
        stableRoom: stableRoom,
        hostelName: hostelName
    });
		
    const url = getUrl(downloadUrl) + '?' + params.toString();
    window.open(url, '_blank');
}

function getUrl(path){
	return contextPath + baseURL + path;
}

function checkAdvanceFilter(status) {
	if(validateHostelForm()){
		let filters = $("#filters").val().replace(/[\[\]{}]/g, '').split(',').map(f => f.trim().split('=')[0]);
			filters.forEach(filter => {
				let safeFilter = CSS.escape(filter);
				let inputName = `additionalParam.${filter}`;
				let value = $(`#${filter}`).val() || '';
				$(`input[name="${inputName}"]`).val(value);
			});
			let page = $('#pageVal').val();
			let size = $('#sizeVal').val();
			//Due to search field not available, used a static variable
			$('#search').val("0");
			let search = $('#search').val();
			if (status !== 'downloadExcel') {
				createUrlWithParams(page, size, search);
			} else {
				let url = baseURL + excelURL;
				createAndReturnUrlWithParams(page, size, search, url);
			}
	}	
}

function updateStableRoomId() {
    var checkbox = document.getElementById('stableRoomId');
    checkbox.value = checkbox.checked ? 'stableRoom' : '';
}