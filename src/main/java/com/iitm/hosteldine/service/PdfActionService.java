package com.iitm.hosteldine.service;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException; // Correct WriterException
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter; // Correct QRCodeWriter
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfActionService {
	
	private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;
	public static final UnitValue VALUE_100_P = UnitValue.createPercentValue(100);
	public static final String NEXT_LINE = "\n";
	public static final String YES = "Yes";
	public static final String NO= "No";
	public static final String Y= "y";
	public static final String NA= "N/A";
	public static final String EMPTY_STRING= " ";
	public static final String RUPEES="Rs.";
	public static final String PDF_EXTENSION=".pdf";
	public String getTempFileLocation() {
        return simsConfigDataService.getSimConfigValue(SimsConfigDataService.TEMP_FILE_LOCATION);
    }
	public String getLogo() {
        return simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
    }

	private static PdfFont TIMES_BOLD;
    private static PdfFont TIMES_ROMAN;
    private static PdfFont HELVETICA_BOLD;

    static {
        try {
        	TIMES_BOLD = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        	TIMES_ROMAN = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            HELVETICA_BOLD = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public PdfFont getHelveticaBold() {
        return HELVETICA_BOLD;
    }
    
    public Cell getCell(String text, boolean isHeader) {
        Cell cell = new Cell();
        Paragraph paragraph = new Paragraph(text)
                .setFontSize(10)
                .setPaddingTop(10F); 
        if (isHeader) {
            paragraph.setBold();  // Apply bold if isHeader is true
        }
        cell.add(paragraph);
        
        cell.setBorder(Border.NO_BORDER);  // Remove border
        cell.setTextAlignment(TextAlignment.LEFT);
        return cell;
    }
    
	public void addWatermarkImage(PdfDocument pdfDoc) throws IOException {
		String imagePath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.WATERMARK_IMAGE_PATH);
		// String imagePath = "static/assets/img/client/watermark_logo.png";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try (InputStream imageStream = classLoader.getResourceAsStream(imagePath)) {
			if (imageStream == null) {
				throw new IOException("Image not found at path: " + imagePath);
			}

			// Load and convert image to grayscale using BufferedImage
			BufferedImage colorImage = ImageIO.read(imageStream);
			BufferedImage grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(),
					BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = grayImage.getGraphics();
			g.drawImage(colorImage, 0, 0, null);
			g.dispose();

			// Write grayscale image to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(grayImage, "png", baos);
			ImageData imageData = ImageDataFactory.create(baos.toByteArray());
			Image watermarkImage = new Image(imageData);

			// Loop through each page and add the watermark image
			for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
				PdfPage page = pdfDoc.getPage(i);
				if (page == null)
					continue;

				// Get page size and scale the image to 75% of the page
				Rectangle pageSize = page.getPageSize();
				float maxWidth = pageSize.getWidth() * 0.75f;
				float maxHeight = pageSize.getHeight() * 0.75f;
				watermarkImage.scaleToFit(maxWidth, maxHeight);

				PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
				Canvas canvas = new Canvas(pdfCanvas, pageSize);

				// Calculate center position for the watermark
				float xPosition = (pageSize.getWidth() - watermarkImage.getImageScaledWidth()) / 2;
				float yPosition = (pageSize.getHeight() - watermarkImage.getImageScaledHeight()) / 2;

				// Apply low opacity to give a watermark effect
				PdfExtGState gState = new PdfExtGState();
				gState.setFillOpacity(0.05f); // Adjust as needed for lighter watermark
				pdfCanvas.saveState(); // Save the state
				pdfCanvas.setExtGState(gState);

				// Draw the watermark image
				watermarkImage.setFixedPosition(xPosition, yPosition);
				canvas.add(watermarkImage);

				pdfCanvas.restoreState(); // Restore the state
				canvas.close(); // Close the canvas
			}
		} catch (IOException e) {
			System.out.println("Error loading or processing image: " + e.getMessage());
		}
	}

    public ImageData getWatermarkImageData() throws IOException {

        String imagePath = simsConfigDataService
                .getSimConfigValue(SimsConfigDataService.WATERMARK_IMAGE_PATH);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream imageStream = classLoader.getResourceAsStream(imagePath)) {

            if (imageStream == null) {
                throw new IOException("Image not found at path: " + imagePath);
            }

            BufferedImage colorImage = ImageIO.read(imageStream);

            BufferedImage grayImage = new BufferedImage(
                    colorImage.getWidth(),
                    colorImage.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );

            Graphics g = grayImage.getGraphics();
            g.drawImage(colorImage, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(grayImage, "png", baos);

            return ImageDataFactory.create(baos.toByteArray());
        }
    }
	
	 public Resource addWatermarkAndGetResource(String inputFilePath, String outputFileName) throws IOException {
	        // Get the temporary file location
	        String tempFileLocation = simsConfigDataService.getSimConfigValue(SimsConfigDataService.TEMP_FILE_LOCATION);
	        String watermarkedFilePath = tempFileLocation + outputFileName;

	        // Add the watermark to the PDF
	        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFilePath), new PdfWriter(watermarkedFilePath))) {
	            addWatermarkImage(pdfDocument);
	        }

	        // Return the watermarked file as a resource
	        Path outputPath = Paths.get(watermarkedFilePath);
	        return new UrlResource(outputPath.toUri());
	    }
    
    public void addTableTextValue(String text, Object value, Table table) {
        addTableTextValue(text, value, table, new int[]{1, 1});
    }

    public void addTableTextValue(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(null);
        textCell.setBold();
        textCell.setPaddingBottom(10F);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals(PdfActionService.EMPTY_STRING) ? value.toString() : PdfActionService.NA));
        valueCell.setWidth(UnitValue.createPercentValue(30));
       valueCell.setBorder(null);
        valueCell.setPaddingBottom(10F);
        //valueCell.setMaxHeight(20);
        table.addCell(valueCell);
    }
    
    public void addTableTextValue1(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(null);
        textCell.setPaddingBottom(10F);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals(PdfActionService.EMPTY_STRING) ? value.toString() : PdfActionService.NA));
        valueCell.setWidth(UnitValue.createPercentValue(30));
       valueCell.setBorder(null);
        valueCell.setPaddingBottom(10F);
        table.addCell(valueCell);
    }
    
    public IBlockElement addFullWidthTitle(String title, TextAlignment alignment) {
        Paragraph titlePara = new Paragraph(title)
                .setBold()
                .setFontSize(14);
        Cell titleCell = new Cell();
        titleCell.add(titlePara);
        titleCell.setTextAlignment(alignment);
        titleCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        titleCell.setMinHeight(20);
        titleCell.setBorder(null);
        titleCell.setPaddingBottom(5F);
        Table titleTable = new Table(1);
        titleTable.setWidth(VALUE_100_P);
        titleTable.addCell(titleCell);
        return titleTable;
    }

    public IBlockElement addFullWidthSubTitle(String title, TextAlignment alignment) {
        Paragraph titlePara = new Paragraph(title)
                .setFontSize(11);
        Cell titleCell = new Cell();
        titleCell.add(titlePara);
        titleCell.setTextAlignment(alignment);
        titleCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        titleCell.setMinHeight(20);
        titleCell.setBorder(null);
        titleCell.setPaddingBottom(5F);
        Table titleTable = new Table(1);
        titleTable.setWidth(VALUE_100_P);
        titleTable.addCell(titleCell);
        return titleTable;
    }
    public void addDocumentHeader(Document document, String headerTitle) throws IOException, java.io.IOException {
    	 String logoPath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream(logoPath);

        if (imageStream != null) {
            Image headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
            headerLogo.setHeight(50);
            headerLogo.setWidth(50);
            headerLogo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            // Create Header Table
            Table headerTable = new Table(new float[]{1, 4});
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(headerLogo).setBorder(Border.NO_BORDER));

            Paragraph headerText = new Paragraph(headerTitle)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(5F);
            headerTable.addCell(new Cell().add(headerText).setBorder(Border.NO_BORDER));
            document.add(headerTable);
        }
    }

	public void addDocumentHeader(Document document, String headerTitle, String subtitle) throws IOException {
		String logoPath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream imageStream = classLoader.getResourceAsStream(logoPath);

		if (imageStream != null) {
			Image headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
			headerLogo.setHeight(50);
			headerLogo.setWidth(50);
			headerLogo.setHorizontalAlignment(HorizontalAlignment.LEFT);

			// Create Header Table
			Table headerTable = new Table(new float[] { 1, 4 });
			headerTable.setWidth(UnitValue.createPercentValue(100));
			headerTable.addCell(new Cell().add(headerLogo).setBorder(Border.NO_BORDER));

			// Create a stacked title + subtitle block
			Paragraph headerText = new Paragraph().add(new Text(headerTitle).setBold().setFontSize(16)).add(NEXT_LINE)
					.add(new Text(subtitle).setFontSize(14)).setTextAlignment(TextAlignment.CENTER).setMarginBottom(5F);

			headerTable.addCell(new Cell().add(headerText).setBorder(Border.NO_BORDER));
			document.add(headerTable);
		}
	}

    public void addDocumentGuestCouponHeader(Document document, String headerTitle, String subtitle, String reqDateFormat) throws IOException {
        String logoPath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream(logoPath);

        if (imageStream != null) {
            Image headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
            headerLogo.setHeight(50);
            headerLogo.setWidth(50);
            headerLogo.setHorizontalAlignment(HorizontalAlignment.LEFT);
            headerLogo.setMarginLeft(-5);

            // Create Header Table
            Table headerTable = new Table(new float[]{1, 4});
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(headerLogo).setBorder(Border.NO_BORDER));

            // Title and Subtitle in a single cell with extra spacing
            Paragraph headerText = new Paragraph(headerTitle)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(13)
                    .setMultipliedLeading(1f);

            Paragraph subtitleText = new Paragraph(subtitle)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(13)
                    .setMultipliedLeading(1f)
                    .setMarginTop(7F);

            Paragraph reqDate = new Paragraph(reqDateFormat)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setBold();

            Cell headerTextCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            headerTextCell.add(headerText);
            headerTextCell.add(subtitleText);
            headerTextCell.add(reqDate);

            headerTable.addCell(headerTextCell);
            document.add(headerTable);
        }
    }

	public byte[] generateQRCode(String couponNumber, String couponId, String validToDate)
			throws WriterException, IOException {
		String qrText = couponNumber + couponId + Constants.BACKTICK + validToDate;

		QRCodeWriter qrCodeWriter = new QRCodeWriter();

		// Set margin (quiet zone) to 0
		Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
		hints.put(EncodeHintType.MARGIN, 0);

		BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 100, 100, hints);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);

		MatrixToImageWriter.writeToStream(bitMatrix, FileUploadConstants.PNG_FORMAT.toUpperCase(), outputStream,
				config);

		return outputStream.toByteArray();
	}

	public void addPageBreaker(Document document) {
		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	}

    public Image getAuthoritySignature() throws IOException {
        try (InputStream imageStream = getClass().getClassLoader().getResourceAsStream(simsConfigDataService.getSimConfigValue("AUTHORITY_SIGNATURE_PATH"))) {
            if (imageStream != null) {
                return new Image(ImageDataFactory.create(imageStream.readAllBytes()))
                        .setHeight(30)
                        .setWidth(70)
                        .setMarginTop(3)
                        .setMarginLeft(1)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            }
        }
        return new Image(ImageDataFactory.create(new byte[0]));
    }

    public void addDocumentHeaderWithProfile(Document document, String headerTitle, String subtitle, byte[] studentProfileImage)
            throws IOException {
        String logoPath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO);
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream(logoPath);

        Image headerLogo = null;
        if (imageStream != null) {
            headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
            headerLogo.setHeight(50);
            headerLogo.setWidth(50);
            headerLogo.setHorizontalAlignment(HorizontalAlignment.LEFT);
        }

        Image studentProfile = null;
        if (studentProfileImage != null) {
            studentProfile = new Image(ImageDataFactory.create(studentProfileImage));
            studentProfile.setWidth(60);
            studentProfile.setHeight(80);
        }

        // Create Header Table with 3 columns: Logo | Title & Subtitle | Profile Image
        Table headerTable = new Table(new float[] { 1, 4, 1 });
        headerTable.setWidth(UnitValue.createPercentValue(100));

        Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
        if (headerLogo != null) {
            logoCell.add(headerLogo);
        }
        headerTable.addCell(logoCell);

        // Title and Subtitle in a single cell with extra spacing
        Paragraph headerText = new Paragraph(headerTitle)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16)
                .setMarginBottom(10F); // Increased bottom margin for spacing

        Paragraph subtitleText = new Paragraph(subtitle)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(14)
                .setMarginTop(10F); // Increased top margin for more space

        Cell headerTextCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        headerTextCell.add(headerText);
        headerTextCell.add(subtitleText);
        headerTable.addCell(headerTextCell);

        Cell profileCell = new Cell().setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        if (studentProfile != null) {
            profileCell.add(studentProfile);
        } else {
            profileCell.add(new Paragraph(messageSource.getMessage("message.label.no.image", null, Locale.getDefault())));
        }
        headerTable.addCell(profileCell);

        document.add(headerTable);
        document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));
    }
}
