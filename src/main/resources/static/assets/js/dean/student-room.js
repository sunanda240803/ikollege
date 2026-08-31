$(document).ready(function() {
    const updateId = $('#updateId');
    const userRole = $('#userRole');
	$("#addNewId i").attr("class", "fa-solid fa-file-excel me-1");
	$('#addNewId').removeClass('btn-success').addClass('btn-pink');
    if (userRole.val() !== 'Guest Allotment') {
        updateId.addClass(displayNone);
    } else {
        updateId.removeClass('btn-blue').addClass('btn-success');
        updateId.html(`
            <i class="fa-solid fa-door-closed me-1"></i>
            <span>Room Availability Status</span>
        `);
        updateId.click(function(){
            window.location.href = `${contextPath}${guestGUIUrl}`;
        })
    }
	$("#toggleFilter").click(function() {
		const isExpanded = $(this).attr("aria-expanded") === "true";
		const toggleFilterDiv = $(".toggleFilterSection");

		if (isExpanded) {
            $("#toggleFilterText").text(hideAdvanceFilter);
            $(".keywordSearch").addClass('d-none');
        } else {
            $("#toggleFilterText").text(showAdvanceFilter);
            $(".keywordSearch").removeClass('d-none');
        }
	});
    $('#additionalButton').click(function() {
        downloadRequestReport();
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const paginationLinks = document.querySelectorAll('.page-link');

    paginationLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault(); // Prevent the default GET request

            const page = this.getAttribute('data-page'); // Get the page number
            const form = document.getElementById('studentRequestFilterForm'); // Create a form for POST

            // Set form attributes
            form.method = 'POST';

            // Add a hidden input for the page number
            const pageInput = document.createElement('input');
            pageInput.type = 'hidden';
            pageInput.name = 'page';
            pageInput.value = page;
            form.appendChild(pageInput);

            // Add CSRF token if needed (for security)
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
            if (csrfToken && csrfHeader) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = csrfHeader;
                csrfInput.value = csrfToken;
                form.appendChild(csrfInput);
            }

            // Submit the form
            document.body.appendChild(form);
            form.submit();
        });
    });
});

function editStudentRoomModal() {
	$('#student-room-modal').modal('show');
}
function openEditStudentRoomModal(button) {
    var dataurl = button.getAttribute('data-url2');
    const url2 = contextPath + baseUrl + studentEditUrl + "/" + dataurl;
    fetch(url2)
        .then(response => response.text())
        .then(html => {
            document.getElementById("modalDiv").innerHTML = html;
            $('#modalDiv').html(html); // Load the modal HTML

            // Show the modal
            const modal = new bootstrap.Modal($('#student-room-modal'));
            modal.show();

            $("#toggleFilter").click(function() {
                const isExpanded = $(this).attr("aria-expanded") === "true";
                const toggleFilterDiv = $(".toggleFilterSection");

                if (isExpanded) {
                    $("#toggleFilterText").text(hideAdvanceFilter);
                    $(".keywordSearch").addClass('d-none');
                } else {
                    $("#toggleFilterText").text(showAdvanceFilter);
                    $(".keywordSearch").removeClass('d-none');
                }
            });


                $("#student-room-modalSave").click(function() {
                    const requiredFields = $('input[required]');
                    const isValid = validateRadioAndText(requiredFields);

                    if (isValid) {
                         saveStudentRoomDetail();
                    } else {
                        return false;
                    }
                });


            // Add event listeners to each radio button to call changeLabel
            document.querySelectorAll('input[name="paymentType"]').forEach(radio => {
                radio.addEventListener('click', function() {
                    changeLabel(this.value);  // Update label based on selected payment type
                });
            });
        })
        .catch(error => {
            showToast('Error');
        });
    }
function redirectToStudentView(button) {
    const url = button.getAttribute('data-url');  // Get the dynamic URL from the data-url attribute
    if (url) {
        const redirectUrl = contextPath + baseUrl + studentViewUrl + "/" + url;  // Navigate to the URL
        window.open(redirectUrl, "_blank");
    }
}


function submitFilterForm() {
    var form = document.getElementById('studentRequestFilterForm');
    form.submit();
}
function saveStudentRoomDetail() {

}

function changeLabel(paymentType) {
    let label = document.getElementById('duNoLabel');
    let errorLabel = document.getElementById('paymentReferenceError');

    if (paymentType === paymentTypeDigital) {
        label.textContent = paymentRefNoLabel;
        errorLabel.textContent = paymentRefNoLabel + ' ' + isRequiredLabel;
    } else if (paymentType === paymentTypeOnline) {
        label.textContent = billNoLabel;
        errorLabel.textContent = billNoLabel + ' ' + isRequiredLabel;
    }

}

function saveStudentRoomDetail() {
    var form = document.getElementById('studentRoomEditForm');

    // Use AJAX to submit the form
    $.ajax({
        url: form.action,  // Use the form's action URL
        type: 'POST',
        data: $(form).serialize(), // Serialize the form data
        success: function(response) {
            if (response.status === 'Success') {
               // Close the modal on successful submission
               const modal = new bootstrap.Modal($('#student-room-modal'));
               modal.hide();
               showToast(response.status, response.message);
               submitFilterForm();

            } else {
                showToast(response.error, response.message);
            }


        },
        error: function(xhr, status, error) {
            // Handle errors if any
            console.error('Error submitting form:', error);
        }
    });
}


    function createUrlWithParams(page, size, search) {
        let currentUrl = contextPath + $('#currentUrl').val();

        let additionalParamsString = $('.additional-param');

        let queryString = '';
        additionalParamsString.each(function() {
            let paramName = $(this).attr('name');
            let paramValue = $(this).val();

            if (queryString.length > 0) {
                queryString += '&';
            }
            queryString += `${paramName}=${encodeURIComponent(paramValue)}`;
        });

        // Append search param if it's not null or empty
        if (search && search.trim() !== '') {
            if (queryString.length > 0) {
                queryString += '&';
            }
            queryString += `search=${encodeURIComponent(search.trim())}`;
        }

        let fullUrl = `${currentUrl}?page=${page}&size=${size}`;
        if (queryString.length > 0) {
            fullUrl += `&${queryString}`;
        }

        // Update the form's action attribute with the full URL
        let form = $('#studentRequestFilterForm');
        form.attr('action', fullUrl);

        form.submit();
    }

    function downloadRequestReport() {
        // Get form data
        const formData = new FormData(document.getElementById('studentRequestFilterForm'));

        // Convert FormData to JSON
        const jsonData = {};
        formData.forEach((value, key) => {
            jsonData[key] = value;
        });

        // Send POST request using fetch
        fetch(contextPath + baseUrl + excelDownloadUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(jsonData),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
             // Extract the filename from the Content-Disposition header
            const contentDisposition = response.headers.get('Content-Disposition');
            let filename; // Default filename in case the header is not present

            if (contentDisposition && contentDisposition.includes('filename=')) {
                filename = contentDisposition
                    .split('filename=')[1]
                    .split(';')[0]
                    .replace(/['"]/g, '');
            }

            return response.blob().then(blob => ({ blob, filename }));
        })
        .then(({ blob, filename }) => {
            // Create a link element to trigger the download
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Error downloading Excel file:', error);
            showToast('Error', error);

        });
    }

     function editRoomAllotment(buttonElement) {
         const index = buttonElement.getAttribute('data-index');
         const row = document.getElementById('row_' + index);
         // Highlight the row
         toggleRowHighlight(row, true);
         // Hostel
         const hostelText = row.querySelector('#hostelText_' + index);
         const hostelDropdown = row.querySelector('#hostelDropdown_' + index);

         if (hostelText && hostelDropdown) {
             hostelText.style.display = 'none'; // Hide text
             hostelDropdown.style.display = 'inline-block'; // Show dropdown

             // Pre-select the current value in the dropdown
             const currentHostelName = hostelText.innerText.trim();
             for (let option of hostelDropdown.options) {
                 if (option.text === currentHostelName) {
                     option.selected = true;
                     break;
                 }
             }
             // Call fetchRoomNumbers to populate the room dropdown
             fetchRoomNumbersWithIndex(hostelDropdown, index);
         }

         // Room
         const roomText = row.querySelector('#roomText_' + index);

         if (roomText) {
             roomText.style.display = 'none'; // Hide text
         }

        // Hide the Edit button
        const editButton = row.querySelector('#editButton_' + index);
        if (editButton) editButton.style.display = 'none';

        // Show the Allot and De-allot buttons
        const allotButton = row.querySelector('#allotButton_' + index);
        const deAllotButton = row.querySelector('#deAllotButton_' + index);
        if (allotButton) allotButton.style.display = 'inline-block';
        if (deAllotButton) deAllotButton.style.display = 'inline-block';

         // Enable the Save button
         const saveButton = document.getElementById('saveButton_' + index);
         if (saveButton) saveButton.disabled = false;
     }


    function fetchRoomNumbersWithIndex(selectElement, index) {
        const hostelId = selectElement.value;
        const roomDropdown = document.getElementById('roomDropdown_' + index);
        const roomLoader = document.getElementById('roomLoader_' + index);

        if (hostelId) {
            // Show the loader
            if (roomLoader) roomLoader.style.display = 'inline-block';

            // Hide the dropdown while loading
            if (roomDropdown) roomDropdown.style.display = 'none';

            // Fetch room numbers for the selected hostel
            fetch(contextPath + roomByHostelUrl + '/' + hostelId)
                .then(response => response.json())
                .then(data => {
                    // Clear existing options
                    if (roomDropdown) roomDropdown.innerHTML = '<option value="">Select Room</option>';

                    // Add new options
                    data.forEach(room => {
                        const option = document.createElement('option');
                        option.value = room.id;
                        option.text = room.roomNo;
                        option.setAttribute('data-buildingId', room.building?.id);
                        if (roomDropdown) roomDropdown.appendChild(option);
                    });

                    // Hide the loader
                    if (roomLoader) roomLoader.style.display = 'none';

                    // Show the dropdown
                    if (roomDropdown) roomDropdown.style.display = 'inline-block';

                    // Pre-select the current room after the dropdown is populated
                    const roomText = document.querySelector('#roomText_' + index);
                    if (roomText) {
                        const currentRoomNo = roomText.innerText.trim();
                        for (let option of roomDropdown.options) {
                            if (option.text === currentRoomNo) {
                                option.selected = true;
                                break;
                            }
                        }
                    }
                })
                .catch(error => {
                    console.error('Error fetching room numbers:', error);

                    // Hide the loader in case of error
                    if (roomLoader) roomLoader.style.display = 'none';

                    // Show the dropdown (even if empty)
                    if (roomDropdown) roomDropdown.style.display = 'inline-block';
                });
        } else {
            // Hide the dropdown and loader if no hostel is selected
            if (roomDropdown) roomDropdown.style.display = 'none';
            if (roomLoader) roomLoader.style.display = 'none';
        }
    }

    function fetchRoomNumbers(selectElement) {
        const index = selectElement.id.split('_')[1];
        const hostelId = selectElement.value;
        const roomDropdown = document.getElementById('roomDropdown_' + index);
        const roomLoader = document.getElementById('roomLoader_' + index);
        const saveButton = document.getElementById('saveButton_' + index);

        if (hostelId) {
            // Show the loader
            if (roomLoader) roomLoader.style.display = 'inline-block';

            // Hide the dropdown while loading
            if (roomDropdown) roomDropdown.style.display = 'none';

                // Fetch room numbers for the selected hostel
                const payload = {
                    fromDate: selectElement.getAttribute('data-fromdate'),
                    toDate: selectElement.getAttribute('data-todate'),
                    requestId: selectElement.getAttribute('data-requestid'),
                    hostelId: hostelId
                };

                fetch(contextPath + baseUrl + guestRoomsByHostel, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                })
                    .then(response => response.json())
                    .then(data => {
                        // Clear existing options
                        if (roomDropdown) roomDropdown.innerHTML = '<option value="">Select Room</option>';

                        // Add new options
                        data.forEach(room => {
                            const option = document.createElement('option');
                            option.value = room.roomId;
                            option.text = room.roomNo;
                            option.setAttribute('data-buildingId', room.floorId);
                            if (roomDropdown) roomDropdown.appendChild(option);
                        });

                        // Hide the loader
                        if (roomLoader) roomLoader.style.display = 'none';

                        // Show the dropdown
                        if (roomDropdown) roomDropdown.style.display = 'inline-block';

                        // Enable the Save button
                        if (saveButton) saveButton.disabled = false;
                    })
                    .catch(error => {
                        console.error('Error fetching room numbers:', error);

                        // Hide the loader in case of error
                        if (roomLoader) roomLoader.style.display = 'none';

                        // Show the dropdown (even if empty)
                        if (roomDropdown) roomDropdown.style.display = 'inline-block';
                    });
        } else {
            // Hide the dropdown and loader if no hostel is selected
            if (roomDropdown) roomDropdown.style.display = 'none';
            if (roomLoader) roomLoader.style.display = 'none';

            // Disable the Save button
            if (saveButton) saveButton.disabled = true;
        }
    }

    function enableSaveButton(selectElement) {
        const index = selectElement.id.split('_')[1];
        const saveButton = document.getElementById('saveButton_' + index);

        if (selectElement.value) {
            saveButton.disabled = false;
        } else {
            saveButton.disabled = true;
        }
    }

    function saveRoomAllotment(buttonElement) {
        const index = buttonElement.getAttribute('data-index');
        const row = document.getElementById('row_' + index);
        // Highlight the row
        toggleRowHighlight(row, true);
        const hostelDropdown = document.getElementById('hostelDropdown_' + index);
        const roomDropdown = document.getElementById('roomDropdown_' + index);

        const selectedHostelId = hostelDropdown.value;
        const selectedRoomId = roomDropdown.value;
        // Get the selected option from the room dropdown
        const selectedOption = roomDropdown.options[roomDropdown.selectedIndex];
        // Access the buildingId from the data-buildingId attribute
        const selectedBuildingId = selectedOption.dataset.buildingid;
        const guestId = buttonElement.getAttribute('data-guestId');

        if (selectedHostelId && selectedRoomId) {
            const data = {
                hostelId: selectedHostelId,
                buildingId: selectedBuildingId,
                roomId: selectedRoomId,
                requestId: buttonElement.closest('tr').querySelector('td:nth-child(1)').innerText,
                guestId: guestId
            };

            fetch(contextPath + baseUrl + roomAllotmentUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(d => d.json())
            .then(response => {
                // Refresh the row or update the UI as needed
                if (response.status === 'Success') {
                   // Close the modal on successful submission
                   showToast(response.status, response.message);
                   submitFilterForm();

                } else {
                    showToast(response.status, response.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error', error);
            });
        } else {
            showToast('Error', 'Please select both hostel and room.');
        }
    }


    function retainRoomAllotment(buttonElement) {
        const index = buttonElement.getAttribute('data-index');
        const row = document.getElementById('row_' + index);
        // Highlight the row
        toggleRowHighlight(row, true);
         const guestId = buttonElement.getAttribute('data-guestId');
        const data = {
            requestId: buttonElement.closest('tr').querySelector('td:nth-child(1)').innerText,
            guestId: guestId
        };

        fetch(contextPath + baseUrl + roomAllotmentRetainUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(d => d.json())
        .then(response => {
            // Refresh the row or update the UI as needed
            if (response.status === 'Success') {
               // Close the modal on successful submission
               showToast(response.status, response.message);
               submitFilterForm();

            } else {
                showToast(response.status, response.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Error', error);
        });
    }

    function toggleRowHighlight(row, highlight = true) {
        if (highlight) {
            // Add highlight styles
            row.style.backgroundColor = '#f0f8ff'; // Light blue background for the row
            Array.from(row.cells).forEach(cell => {
                cell.style.backgroundColor = '#f0f8ff'; // Light blue background for each cell
                cell.style.border = '1px solid #3498db'; // Blue border for each cell
            });
        } else {
            // Remove highlight styles
            row.style.backgroundColor = ''; // Reset background for the row
            Array.from(row.cells).forEach(cell => {
                cell.style.backgroundColor = ''; // Reset background for each cell
                cell.style.border = ''; // Reset border for each cell
            });
        }
    }