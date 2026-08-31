$(document).ready(function() {
    // Set data-l1-id for each child checkbox to associate it with the parent checkbox
    document.addEventListener('DOMContentLoaded', function() {
        // Associate each child checkbox with its parent (l1) using data-l1-id attribute
        document.querySelectorAll('.child-checkbox').forEach(function(checkbox) {
            const l2Id = checkbox.id.split('-')[1]; // Get the l2 ID from the checkbox ID
            const l1Id = checkbox.closest('.row').querySelector('.parent-checkbox').id.split('-')[1]; // Get the parent (l1) ID
            checkbox.setAttribute('data-l1-id', l1Id); // Set the data-l1-id for each child checkbox
        });
    });
    // Handle role change to update the URL
    document.getElementById('selectRole').addEventListener('change', function() {
        const selectedRole = this.value;
        let currentUrl = $('#currentUrl').val();
        let fullUrl = contextPath + baseURL + `/` + selectedRole;
        window.location.href = fullUrl;
    });

});

// Function to toggle child checkboxes based on the parent checkbox state
function toggleChildCheckboxes(l1Id, isChecked) {
    // Get all child checkboxes (l2) associated with the parent (l1)
    const childCheckboxes = document.querySelectorAll(`.child-checkbox[data-l1-id='${l1Id}']`);

    // Loop through the child checkboxes and set their checked status to match the parent's
    childCheckboxes.forEach(function(checkbox) {
        checkbox.checked = isChecked;
    });

    // After toggling, update the parent checkbox based on the child states
    updateParentCheckbox(l1Id);
}

// Function to update the parent checkbox based on child checkboxes' states
function updateParentCheckbox(l1Id) {
    // Get all child checkboxes (l2) associated with the parent (l1)
    const childCheckboxes = document.querySelectorAll(`.child-checkbox[data-l1-id='${l1Id}']`);
    const parentCheckbox = document.getElementById(`l1-${l1Id}`);

    // Check if all child checkboxes are selected
    const allChecked = Array.from(childCheckboxes).every(function(checkbox) {
        return checkbox.checked;
    });

    // Set the parent checkbox to checked if all child checkboxes are checked, otherwise unchecked
    parentCheckbox.checked = allChecked;
}


// Validate form before submission
function validateForm() {
    const isAnyCheckboxSelected = $('.parent-checkbox:checked').length > 0 || $('.child-checkbox:checked').length > 0;

    // if (!isAnyCheckboxSelected) {
    //     showToast('Error', 'Please select at least one option');
    //     return false;
    // }
    sendSaveApiCall();  // Save the selected checkbox values to hidden field
    return true;
}




// Function to send Save API call
function sendSaveApiCall() {
    // Get the selected role
    const selectedRole = document.getElementById('selectRole').value;

    // Get the selected checkboxes from hidden input
    const selectedCheckboxes = document.getElementById('selectedCheckboxes').value;
    const checkedTabs = [];
    const uncheckedTab = [];

    // Collect selected parent and child checkbox IDs
    document.querySelectorAll('input[type="checkbox"]:checked').forEach((checkbox) => {
        checkedTabs.push(checkbox.id);
    });

    // Prepare request data
    const requestData = {
        role: selectedRole,
        checkedTabList: JSON.parse(JSON.stringify(checkedTabs))
    };
    let currentUrl = $('#currentUrl').val();
    let fullUrl = contextPath + baseURL;
    // Send API request
    fetch(fullUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
//    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        showToast('Success', 'Data saved successfully!');
        window.scrollTo({
            top: document.body.scrollHeight,
            behavior: 'smooth'
        });
       // window.location.reload(); // Refresh page after successful save
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Error', 'Failed to save data.');
    });
}