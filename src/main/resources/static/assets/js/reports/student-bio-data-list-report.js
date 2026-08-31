function filterTable() {
	const input = document.getElementById("tableSearch");
	const filter = input.value.toLowerCase();
	const table = document.getElementById("studentBioDataList");
	const rows = table.getElementsByTagName("tr");

	for (let i = 1; i < rows.length; i++) {
		const cells = rows[i].getElementsByTagName("td");
		let match = false;

		for (let j = 0; j < cells.length; j++) {
			if (cells[j]) {
				const cellText = cells[j].textContent || cells[j].innerText;
				if (cellText.toLowerCase().indexOf(filter) > -1) {
					match = true;
					break;
				}
			}
		}

		rows[i].style.display = match ? "" : "none";
	}
}