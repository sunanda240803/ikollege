function navigateWithHostelParams(element, additionalParams = {}) {
    const hostelId = element?.getAttribute("data-hostel-id") || 0;
    if (hostelId === 0) return;

    let separator = String(baseURL).includes("?") ? "&" : "?";
    let queryString = `hostelId=${encodeURIComponent(hostelId)}`;

    for (const [key, value] of Object.entries(additionalParams)) {
        queryString += `&${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
    }
    window.location.replace(`${contextPath}${baseURL}${separator}${queryString}`);
}

function viewHostelGUI(element) {
    navigateWithHostelParams(element);
}

function viewStatusGUI(element) {
    const status = element?.getAttribute("data-status")?.trim() || "0";
    navigateWithHostelParams(element, { occupancyStatus: status });
}