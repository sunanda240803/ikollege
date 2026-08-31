package com.iitm.hosteldine.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;

public class WatermarkEventHandler implements IEventHandler {

    private final ImageData imageData;

    public WatermarkEventHandler(ImageData imageData) {
        this.imageData = imageData;
    }

    @Override
    public void handleEvent(Event event) {

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();

        Rectangle pageSize = page.getPageSize();

        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
        Canvas canvas = new Canvas(pdfCanvas, pageSize);

        Image watermarkImage = new Image(imageData);

        // Scale image
        float maxWidth = pageSize.getWidth() * 0.75f;
        float maxHeight = pageSize.getHeight() * 0.75f;
        watermarkImage.scaleToFit(maxWidth, maxHeight);

        // Center position
        float x = (pageSize.getWidth() - watermarkImage.getImageScaledWidth()) / 2;
        float y = (pageSize.getHeight() - watermarkImage.getImageScaledHeight()) / 2;

        // Opacity
        PdfExtGState gState = new PdfExtGState();
        gState.setFillOpacity(0.05f);

        pdfCanvas.saveState();
        pdfCanvas.setExtGState(gState);

        watermarkImage.setFixedPosition(x, y);
        canvas.add(watermarkImage);

        pdfCanvas.restoreState();
        canvas.close();
    }
}
