$(document).ready(function() {
	$(document).on('blur', '.relationContact', function () {
		validateMobileStart(this);
	});
});

function triggerPageLoadFunctions() {
	addParentDetails(true);
	updateContactLoopByName();

	// Handle row deletion with tooltip disposal
	$(document).on('click', '.deleteBtn', function() {
		const row = $(this).closest('tr');
		const rowCount = $('#familyTable tbody tr').length;

		// Prevent deleting the last remaining row
		if (rowCount > 1) {
			row.find('[data-bs-toggle="tooltip"]').tooltip('dispose');

			// Clear the relationName field value in this row
			const relationNameInput = row.find('[id^="relationName"]');
			if (relationNameInput.length > 0) {
				relationNameInput.val('');
			}
			checkProofUpload(this);
			row.remove();
		}
	});
	// Listen for changes in the input fields
	$('#addFamilyMember').on('click', updateTable);

	// Handle change event for radio buttons
	$('input[name="guardianStatus"]').on('change', function() {
		if ($(this).val() === 'Y') {
			$('#localGuardianSection').removeClass('d-none'); // Show fields
			fatherAndMotherReq(false);
		} else {
			$('#localGuardianSection').addClass('d-none'); // Hide fields
			fatherAndMotherReq(true);
		}
	});
}


function addParentDetails(onLoad) {
	const destinationTable = $('#familyTable tbody');
	const templateRow = $('#parentDetailsTemplate');
	const templateValueRow = $('#parentDetailsValueTemplate');
	const count = $('#familyMemberCount');
	if (onLoad) {
		if (familyDetails && familyDetails.length > 0) {
			let proofCount = 0;
			for (let i = 0; i < familyDetails.length; i++) {
				const memberData = familyDetails[i];
				let editFamilyDetails = $('#familyDetailsEdit').val() === "true";;
				if(editFamilyDetails!=null && editFamilyDetails!='' && editFamilyDetails==='true'){
					editFamilyDetails = true;
				}else if(editFamilyDetails.length<0){
					editFamilyDetails = false;
				}
				if(editFamilyDetails==true){
					createNewRow(destinationTable, templateRow, 'relationDetails_' + count.val(), count, memberData,true);
				}else{
					createNewRow(destinationTable, templateValueRow, 'relationDetails_' + count.val(), count, memberData,false);
				}
				let descriptionText = `${memberData['relationName'].toUpperCase()} (${memberData['relationType']}) - ${idProof}`;
				let relationType = memberData['relationType'].toUpperCase();
				if (memberData['proofFileName'] && memberData['proofType']) {
					if (relationType !== 'GUARDIAN' && editFamilyDetails==false) {
						addUploadDocSection(i, descriptionText, memberData['proofFileName'], memberData['proofType'],editFamilyDetails);
					}else if(editFamilyDetails==true){
						addUploadDocSection(i, descriptionText, memberData['proofFileName'], memberData['proofType'],editFamilyDetails);
					}
					proofCount++;
				} else {
					if (relationType !== 'GUARDIAN') {
						console.log(relationType);
						addUploadDocSection(i, descriptionText, null, null);
					}
				}
			}
//			if (proofCount === familyDetails.length) {
//				$('#uploadProofDiv').hide();
//			}
		} else {
			for (let i = 0; i < 5; i++) {
				createNewRow(destinationTable, templateRow, 'relationDetails_' + count.val(), count, null,null);
			}
		}
		fatherAndMotherReq(true);
	} else {
		createNewRow(destinationTable, templateRow, 'relationDetails_' + count.val(), count, null,null);
	}	
	
	inputMask(['.relationContact'], mobileNumberMask);
}

function fatherAndMotherReq(value) {
	if (value === true) {
		$('#relationName0').attr('required', true);
		$('#relationName1').attr('required', true);
	} else {
		$('#relationName0').attr('required', false);
		$('#relationName1').attr('required', false);
	}
}

function createNewRow(tableBody, templateRow, newId, rowCount, data,admin) {
	const currentRowCount = rowCount.val();
	let newRow = templateRow.clone();
	newRow.attr('id', newId);

	let relationValue = '';
	$.each(newRow.find('*'), function () {
		const id = $(this).attr('id');
		if (data) {
			let dataName = $(this).attr('data-name');
			if (dataName && data) {
				if(admin!=null && admin!='' && admin==true){
					$(this).val(data[dataName]);
				}else{
					$(this).text(data[dataName]);
				}
				
				let name = $(this).attr('name');
				if (name) {
					name = name.replace("<index>", currentRowCount);
				}
				
				$(this).attr('id', id + currentRowCount);
				$(this).attr('name', name);
				$(this).attr('data-rowcount', currentRowCount);
				$(this).removeAttr('data-template');
				$(this).val(data[dataName]);//
			}
			if (id === 'bioDataIdHidden') {
				let name = $(this).attr('name');
				if (name) {
					name = name.replace("<index>", currentRowCount);
					$(this).attr('name', name);
					$(this).val(data['id']);
				}
			}
		} else {
			if (id) {
				let name = $(this).attr('name');
				if (name) {
					name = name.replace("<index>", currentRowCount);
				}
				$(this).attr('id', id + currentRowCount);
				$(this).attr('name', name);
				$(this).attr('data-rowcount', currentRowCount);
				$(this).removeAttr('data-template');

				// Dropdown logic
				if (id === 'relationType') {
					if (currentRowCount === '0') {
						relationValue = 'Father';
						$(this).val(relationValue).prop('disabled', true).removeAttr('required');
					} else if (currentRowCount === '1') {
						relationValue = 'Mother';
						$(this).val(relationValue).prop('disabled', true).removeAttr('required');
					} else if (currentRowCount === '2') {
						relationValue = 'Brother';
						$(this).val(relationValue);
					} else if (currentRowCount === '3') {
						relationValue = 'Sister';
						$(this).val(relationValue);
					}else if (currentRowCount === '4') {
						relationValue = 'Spouse';
						$(this).val(relationValue);
					}

					// Update hidden input with the selected dropdown value
					$(this).closest('td').find('#relationTypeHidden').val(relationValue);
				}
				if (id === 'relationContact') {
					inputMask([$(this)], mobileNumberMask);
				}

				
				// if (relationValue === 'Father' || relationValue === 'Mother') {
				// 	$(this).attr('required', true);
				// }
		}
		}
		// Logic for delete button
		if (id === 'relationDelete') {
			if (currentRowCount === '0' || currentRowCount === '1') {
				$(this).addClass('d-none');
			}
		}
	});
	tableBody.append(newRow);
	rowCount.val(+currentRowCount + 1);
}

function addUploadDocSection(index, descriptionText, proofFileName, proofType, admin, memberId) {
    const template = $('#dynamicTemplate').html();
    
    // Replace placeholders with adjusted indices
    let populatedTemplate = template.replace(/{index}/g, index);

    const $template = $(populatedTemplate);

	console.log($template.find(`#proofTypeId_${index}`));
	console.log(memberId);
	$template.find(`#proofTypeId_${index}`).val(memberId);

    // If proofFileName exists, show the div with the proofFileName and hide the description input
    if (proofFileName && admin==false) {
        $template.find(`#fileDescBox_${index}`).show(); // Show the div for proofFileName
        $template.find(`#fileDesc_${index}`).text(descriptionText); // Display proofFileName in the span
        $template.find(`#description_${index}`).remove(); // Remove the description input field

        $template.find(`#fileNameBox_${index}`).show(); // Show the div for proofFileName
        $template.find(`#fileName_${index}`)
            .text(proofFileName) // optional, if you want to show name as fallback
            .attr('src', contextPath + fileURL + imageURL + `/GUEST_ACCOMMODATION/` + proofFileName)
            .css({ width: '120px', height: '170px' }); // <-- add this line
        $template.find(`#file_${index}`).remove(); // Remove the input field for file
    } else {
        // Otherwise, show the input field for the description and hide the proofFileName box
        $template.find('input[type="text"][id^="description"]').val(descriptionText);
        $template.find(`#fileDescBox_${index}`).remove(); // Remove the div for proofFileName
        $template.find(`#description_${index}`).show(); // Show the input field for description

        $template.find(`#fileNameBox_${index}`).remove(); // Remove the div for proofFileName
        $template.find(`#file_${index}`).show(); // Show the input field for file
		$template.find(`input,select`).removeAttr('data-template'); // Show the input field for file
		if (proofFileName) {
			$template.find(`#familyIdProfImage${index}`).text(proofFileName)
				.attr('src', contextPath + fileURL + imageURL + `/GUEST_ACCOMMODATION/` + proofFileName)
				.css({ width: '120px', height: '170px' });
		}
    }

    // If proofType exists, show the div with the proofType and hide the select field
    if (proofType && admin==false) {
        $template.find(`#fileTypeBox_${index}`).show(); // Show the div for proofType
        $template.find(`#fileType_${index}`).text(proofType); // Display proofType in the span
        $template.find(`#proofType_${index}`).remove(); // Remove the select field for proofType
    } else {
		$template.find(`#proofType_${index}`).val(proofType);
		$template.find(`#fileType_${index}`).val(proofType);
        $template.find(`#fileTypeBox_${index}`).remove(); // Remove the div for proofType
    }

    $('#fileUploadContainer').append($template); 
}

function updateContactLoopByName() {
	const parentContact = $('#telephoneOrMobileNumber1');
	parentContact.text('');
	$('.relationName').each(function() {
		updateParentContactByName($(this));
	});
}

function updateContactLoopByNumber() {
	const parentContact = $('#telephoneOrMobileNumber1');
	parentContact.text('');
	$('.relationContact').each(function() {
		updateParentContactByContact($(this));
	});
}

function updateParentContactByName(jqueryElem) {
	updateParentContact(jqueryElem.attr('id'), jqueryElem.attr('id').replace('relationName', 'relationContact'));
}

function updateParentContactByContact(jqueryElem) {
	updateParentContact(jqueryElem.attr('id').replace('relationContact', 'relationName'), jqueryElem.attr('id'));
}

function updateParentContact(nameId, contactId) {
	const signedParentName = $('#signedParentName');
	const signedParentText = $('#signedParentText');
	const relationName = $('#' + nameId);
	const relationContact = $('#' + contactId);
	const parentContact = $('#telephoneOrMobileNumber1');
	if (signedParentName.val() !== '') {
		if ((signedParentName.val() && relationName.val().toUpperCase() === signedParentName.val().toUpperCase())
			||
			(relationName.val() && relationName.val().trim().toUpperCase().normalize() === signedParentText.text().trim().toUpperCase().normalize())
		) {
			parentContact.text(relationContact.val());
		}
	}
}

function checkProofUpload(element) {
	const id = element.id;
	const rowcount = element.getAttribute("data-rowcount");
	const idPrefix = id.replace(rowcount, '');
	let relationName, relationType;
	
	if (idPrefix === 'relationName') {
		relationName = $("#" + id);
		relationType = $('#relationType' + rowcount);
		if (relationName.val() !== '') {
			toggleFamilyDetailsRequired(true, rowcount);
		} else {
			toggleFamilyDetailsRequired(false, rowcount);
			relationName.removeClass(validClass);
			fatherAndMotherReq(true);
		}
	} else {
		relationName = $('#relationName' + rowcount);
		relationType = $('#' + id);
	}

	if (relationName.val() !== '' && relationType.val() !== '') {
		let index = rowcount;
		const proof = $('#proof_' + index);
		const name = relationName.val();
		let descriptionText = `${name.toUpperCase()} (${relationType.val()}) - ${idProof}`;
		
		if (proof.length === 0) {
			addUploadDocSection(index, descriptionText, null, null);
		} else {
			proof.find('#description_' + index).val(descriptionText);
		}

		// $('#proofType_' + index).attr('required', true);
		// $('#description_' + index).attr('required', true);

	} else {
		// Remove proof section if either is empty
		$('#proof_' + rowcount).remove();
		// $('#proofType_' + rowcount).removeAttr('required');
		// $('#description_' + rowcount).removeAttr('required');
	}
}

function toggleFamilyDetailsRequired(required, rowcount) {
	$.each($('#relationDetails_' + rowcount + ' input'), function (item) {
		if (required) {
			if ($(this).attr('data-required-class')) {
				$(this).attr('required', true);
			}
		} else {
			$(this).removeAttr('required');
			$(this).removeClass(errorClass);
		}
	});
}

function validateFamilyInfoDetails() {
	let studentMobileNumber = $('#mobileNumber').val();
	let studentPersonalEmail = $('#studentPersonalEmail').val();
	let status = true;
	let contactNumbers = new Set();

	$('[id^="relationContact"]').each(function () {
		let relationContactNumber = $(this).val().trim();
		const invalidDiv = $(this).siblings('.invalid-feedback');

		if (relationContactNumber !== '') {
			// Check if relationContact matches studentMobileNumber
			if (relationContactNumber === studentMobileNumber) {
				invalidDiv.html('Student and Family member contact number should not be the same.');
				$(this).addClass(errorClass);
				status = false;
			}
			// Check for duplicate relationContact numbers
			else if (contactNumbers.has(relationContactNumber)) {
				invalidDiv.html('Duplicate contact number found.');
				$(this).addClass(errorClass);
				status = false;
			}
			else {
				invalidDiv.html($(this).siblings('.invalid-req-feedback').html());
				$(this).removeClass(errorClass).addClass(validClass);
				contactNumbers.add(relationContactNumber);
			}
		}
	});

	$('[id^="relationEmail"]').each(function() {
		let relationEmail = $(this).val().trim();
		const invalidDiv = $(this).siblings('.invalid-feedback');
		if (relationEmail !== '') {
			if (relationEmail === studentPersonalEmail) {
				invalidDiv.html('Student and Family member mail id should not be the same.');
				$(this).addClass(errorClass);
				status = false;
			}
		}
	});
	return status;
}

function updateFamilyAddress(element){
	const addressElement = $(element).closest('td').find('input[type="text"]');
	let stuAddress = $('#address').val();

	if(stuAddress!=='' && stuAddress!==null){
		if ($(element).prop('checked')) {
			addressElement.prop('readonly', true).val(stuAddress);
		} else {
			addressElement.prop('readonly', false).val('');
		}
	}
	else{
		$(element).prop('checked', false);
	}
}