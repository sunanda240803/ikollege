$(document).ready(function () {
    loadWidgets();
});

function loadWidgets() {
    $('.widget-parent').each(function () {
        let id = $(this).attr('data-id');
        let url = $(this).find('#widget_' + id).attr('data-url');
		loadWidget(url, id);
    });
}

function loadWidget(url, id) {
    $.ajax({
        url: contextPath + url,
        type: "GET",
        success: function (response) {
            $('#widget_' + id).html(response);
        }
    });
}