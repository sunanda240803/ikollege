const addressURL = "/addressForm";
const countryURL = "/countryList";
const stateURL = "/stateList/";
const cityURL = "/cityList/";
let countrySelectMap = new Map();

function initCountryDropdown(countryId) {
	const selectElements = [$('#' + countryId),
	$('#' + countryId.replace('country', 'state')),
	$('#' + countryId.replace('country', 'city'))];
	$.each(selectElements, function() {
		disableSelectIfEmpty($(this));
	});
	getCountryList(false, countryId);
	const country = selectElements[0];
	const state = selectElements[1];
	country.on('change', function() {
		onCountryChange(true, $(this).attr('id'));
	});
	state.on('change', function() {
		onStateChange(true, $(this).attr('id'));
	});
}

function getCountryList(reset, countryId) {
	let countryVal = reset ? $('#' + countryId).val() : countrySelectMap.get(countryId);
	const countrySelect = $('#' + countryId);
	if (!reset && (countryVal === 0 || countryVal === undefined)) {
		let tempCountryVal = countrySelect.attr('data-value');
		if (tempCountryVal > 0) countryVal = tempCountryVal;
	}
	if (reset) {
		countrySelectMap.set(countryId, 0);
	}
	$.ajax({
		url: contextPath + addressURL + countryURL,
		type: "GET",
		success: function(response) {
			console.log(response);
			updateSelect(countrySelect, response);
			
			$.each(response, function(index, data) {  
    const optionData = $("<option class='list-entry'></option>")
        .attr("value", data.countryId)  
        .text(data.countryName);  
    countrySelect.append(optionData);  
    if (countryVal === optionData.attr('value')) {
        optionData.attr('selected', true);  
        onCountryChange(false, countryId);  
    }
});
		},
		error: function() {
			showToast('Error', 'Error occurred');
		}
	});
}

function onCountryChange(reset, countryId) {
	const stateId = $('#' + countryId.replace('country', 'state'))
	resetSelect(stateId, ' Country');
	onStateChange(reset, stateId.attr('id'));
	getStateListByCountry(reset, countryId);
}

function getStateListByCountry(reset, countryId) {
	let countryVal = reset ? $('#' + countryId).val() : countrySelectMap.get(countryId);
	const stateId = countryId.replace('country', 'state');
	let stateVal = reset ? $('#' + stateId).val() : countrySelectMap.get(stateId);
	const stateSelect = $('#' + stateId);
	const countrySelect = $('#' + countryId);
	if (!reset && (stateVal === 0 || stateVal === undefined)) {
		let tempCountryVal = countrySelect.attr('data-value');
		let tempStateVal = stateSelect.attr('data-value');
		if (tempStateVal > 0) {
			countryVal = tempCountryVal;
			stateVal = tempStateVal;
		};
	}
	if (reset) {
		countrySelectMap.set(stateId, 0);
	}
	$.ajax({
		url: contextPath + addressURL +  stateURL + countryVal,
		type: "GET",
		success: function(response) {
			updateSelect(stateSelect, response);
			$.each(response, function(index, data) { 
				console.log("data:", data); 
				const optionData = $("<option class='list-entry'></option>")
					.attr("value", data.stateId)  
					.text(data.stateName); 
				stateSelect.append(optionData);  
				if (stateVal === optionData.attr('value')) {
					optionData.attr('selected', true);
					onStateChange(false, stateId);
				}
			});
		},
		error: function() {
			showToast('Error', 'Error occurred');
		}
	});
}

function onStateChange(reset, stateId) {
	const cityId = $('#' + stateId.replace('state', 'city'))
	resetSelect(cityId, ' State');
	if ($('#' + stateId + ' option').length > 1) {
		getCityByState(reset, stateId);
	}
}

function getCityByState(reset, stateId) {
	let stateVal = reset ? $('#' + stateId).val() : countrySelectMap.get(stateId);
	const cityId = stateId.replace('state', 'city');
	let cityVal = reset ? $('#' + cityId).val() : countrySelectMap.get(cityId);
	const stateSelect = $('#' + stateId);
	const citySelect = $('#' + cityId);
	if (!reset && (cityVal === 0 || cityVal === undefined)) {
		let tempStateVal = stateSelect.attr('data-value');
		let tempCityVal = citySelect.attr('data-value');
		if (tempCityVal > 0) {
			stateVal = tempStateVal;
			cityVal = tempCityVal;
		};
	}
	if (reset) {
		countrySelectMap.set(cityId, 0);
	}
	$.ajax({
		url: contextPath +addressURL+ cityURL + stateVal,
		type: "GET",
		success: function(response) {
			updateSelect(citySelect, response);
			$.each(response, function(index, data) {  
				console.log("data:", data);  
				const optionData = $("<option class='list-entry'></option>")
					.attr("value", data.cityId)  
					.text(data.cityName);  
				citySelect.append(optionData);
				if (cityVal === optionData.attr('value')) {
					optionData.attr('selected', true);
				}

			});
		},
		error: function() {
			showToast('Error', 'Error occurred');
		}
	});
}

function resetSelect(select, suffix) {
	select.empty();
	addDefaultOption(select, false, suffix)
}

function updateSelect(select, data) {
	const isEnabled = select.attr('data-enable');
	select.attr('disabled', true);
	if (data !== undefined && data.length > 0) {
		if (isEnabled === undefined || isEnabled === 'true') {
			select.removeAttr('disabled');
		}
		select.empty();
		addDefaultOption(select, false, '');
	}
}

function addDefaultOption(select, empty, suffix) {
	addOption(select, empty, 'Select', '0', suffix);
}

function addOption(select, empty, label, value, suffix) {
	const className = empty ? 'empty-list' : 'list-entry'
	const suffixToAdd = empty ? '' : suffix;
	select.append($("<option class='" + className + "' value='" + value + "'>" + label + suffixToAdd + "</option>"));
}

