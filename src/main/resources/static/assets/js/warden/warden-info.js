$(document).ready(function () {
    if (modalError === true) {
        openAddWardenInfo();
    }
});

$(document).off("click").on('click', '#addNewId', function () {
    openAddWardenInfo();
});

function openAddWardenInfo() {
    const url = getUrl(addURL);
    $.ajax({
        url: url,
        type: "Get",
        success: function (response) {
            $('#modalDiv').html(response);
            $('#wardenInfoModal').modal('show');
        },
        error: function (error) {
            showToast('Error', 'Error occured');
        }
    });
}

function filterTable() {
    const input = document.getElementById("tableSearch");
    const filter = input.value.toLowerCase();
    const table = document.getElementById("wardenInfoList");
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

function editDetails(element) {
    const url = element !== null ? contextPath + "/" + element.getAttribute("data") : null;

    $.ajax({
        url: url,
        type: "Get",
        success: function (response) {
            $('#modalDiv').html(response);
            $('#wardenInfoModal').modal('show');
        },
        error: function (error) {
            showToast('Error', 'Error occured');
        }
    });
}

function deleteData(element) {
    const url = element !== null ? contextPath + "/" + element.getAttribute("data") : null;
    $('#deleteModal').modal('show');
    $('#deleteModalPositive').off('click').click(function () {
        $('#deleteModal').modal('hide');
        $.ajax({
            url: url,
            type: "DELETE",
            success: function (response) {
                if (response.status === 'Success' || response.status === 'success') {
                    location.reload();
                }
                showToast(response.status, response.message);
            },
            error: function () {
                showToast('Error', 'Error occurred');
            }
        });
        $('#deleteModalPositive').off('click');
    })
}


function getUrl(path) {
    return contextPath + wardenInfoURL + path;
}