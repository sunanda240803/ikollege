$(document).ready(function () {
    const dateColumns = ["Appointment From", "Appointment To", "Stay From", "Stay To", "Rebate From", "Rebate To",
        "Submitted Date", "From Date", "To Date", "Date of Vacating", "Date Of Complaint", "Inspection Date",
        "Dining From", "Dining To"];

    const defaultSortColumnName = "Submitted Date";
    const columnNames = [];
    $('table thead th').each(function () {
        columnNames.push($(this).text().trim());
    });

    $('table').DataTable({
        columnDefs: [
            {
                targets: "_all",
                render: function (data, type, row, meta) {

                    const columnName = columnNames[meta.col];
                    if (dateColumns.some(dateColumn => columnName === dateColumn)) {
                        if (type === 'sort') {
                            if (!data || data.trim() === '') return '';
                            const date = new Date(data);
                            if (isNaN(date.getTime())) return data;
                            return date.getTime();
                        }
                    }
                    return data;
                }
            }
        ],
        order: [[getColumnIndexByName(defaultSortColumnName), 'desc']]
    });
});

function getColumnIndexByName(columnName) {
    let index = -1;

    $(`table thead th`).each(function (i) {
        if ($(this).text().trim().toLowerCase() === columnName.toLowerCase()) {
            index = i;
            return false; // break loop
        }
    });
console.log(index);
    return index;
}