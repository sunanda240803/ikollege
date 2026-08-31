$(document).ready(function() {
   initDragAndDrop();
});

function handleUploadFile(fileInput) {
    const id = fileInput.attr('data-id');
    const files = fileInput.prop('files');
    const fileInfo = $('#' + id + '_fileInfo');
    const fileError = $('#' + id + '_fileError');

    // Reset previous messages
    fileInfo.html('');
    fileError.html('');

    const fileTypesStr = fileInput.attr('data-files');
    /*const fileTypes = fileTypesStr.split(',');
    for(let i = 0; i < fileTypes.length; i++) {
        fileTypes[i] = fileTypes[i].replace('.', 'text/');
    }*/
    const fileTypes = fileTypesStr.split(',').map(type => {
        if (type.startsWith('.')) {
            // Convert extensions to MIME types
			switch (type.toLowerCase()) {
			    case '.csv': return 'text/csv';
				case '.jpg': return 'image/jpg';
			    case '.jpeg': return 'image/jpeg';
			    case '.bmp': return 'image/bmp';
			    case '.png': return 'image/png';
			    case '.svg': return 'image/svg+xml';
			    case '.pdf': return 'application/pdf';
			    case '.doc': return 'application/msword';
			    case '.docx': return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
			    case '.xls': return 'application/vnd.ms-excel';
			    case '.xlsx': return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
			    default: return ''; // Ignore unknown types
			}
        }
        return type.trim();
    });

    const uploadButton = $('#' + fileInput.attr('data-upload-button'));

    // Check if the file is a CSV
    let fileInfoStr = '<ul>';
    let fileErrorStr = '<ul>';
    for (let i = 0; i < files.length; i++) {
        let file = files[i];
        if (file && fileTypes.includes(file.type)) {
            const fileSize = (file.size / 1024).toFixed(2); // File size in KB
            const fileSizeMB = file.size / (1024 * 1024); // File size in MB
            if (fileSizeMB > 2) {
                fileErrorStr += (`<li>File: ${file.name} File size exceeds 2MB. Please upload a smaller file.</li>`);
                uploadButton.attr('disabled', true);
            } else {
                fileInfoStr += `<li>File: ${file.name} (${fileSize} KB)</li>`;
                uploadButton.removeAttr('disabled');
            }
        } else {
            const allowedTypes = fileTypesStr.split(',').join(', ');
            fileErrorStr += (`<li>File: ${file.name} Invalid file type. Accepted types are: ${allowedTypes}</li>`);
            uploadButton.attr('disabled', true);
        }
    }
    fileInfoStr += '</ul>';
    fileErrorStr += '</ul>';
    fileInfo.html(fileInfoStr);
    fileError.html(fileErrorStr);
}

function initDragAndDrop(){
	const dropZone = $('.drop-zone');
    const dropFileInput = $('.drop-file');

    dropZone.off('click').on('click', function() {
        const id = $(this).attr('data-id');
        $('#' + id + "_fileUpload")[0].click();
    });
    dropZone.on('dragover', function(e) {
        e.preventDefault();
        dropZone.addClass('dragover');
    });
    dropZone.on('dragleave', function() {
        dropZone.removeClass('dragover');
    });
    dropZone.on('drop', function(e) {
        e.preventDefault();
        dropZone.removeClass('dragover');
        const files = e.originalEvent.dataTransfer.files;
        const id = $(this).attr('data-id');
        const fileInput = $('#' + id + '_fileUpload');
        fileInput.prop('files', files);
        handleUploadFile(fileInput);
    });

    dropFileInput.on('change', function(e) {
        handleUploadFile($(this));
    });
}