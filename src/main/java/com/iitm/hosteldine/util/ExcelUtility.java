package com.iitm.hosteldine.util;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExcelUtility {

	/**
	 * Create Cell
	 *
	 * @param row
	 * @param columnCount
	 * @param value
	 * @param style
	 */
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof LocalDate) {
			cell.setCellValue((LocalDate) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		}else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	public void createCell(Row row, int columnCount, Object value, CellStyle style, XSSFSheet sheet, int columnWidth) {
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else {
			cell.setCellValue((String) value);
		}
		sheet.setColumnWidth(columnCount, columnWidth);
		cell.setCellStyle(style);
	}

	/**
	 * Set Header lines
	 *
	 * @param row
	 * @param columnCount
	 * @param headerData
	 * @param workbook
	 */
	public void createHeader(Row row, int columnCount, Object[] headerData, XSSFWorkbook workbook) {

		/**
		 * Header Style
		 */
		CellStyle style1 = workbook.createCellStyle();
		XSSFFont font1 = workbook.createFont();
		font1.setBold(true);
		font1.setFontHeight(12);
		style1.setFont(font1);
		// Set borders for cells
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);

		/**
		 * Header lines
		 */
		for (Object field : headerData) {
			createCell(row, columnCount++, field, style1);
		}
	}

	/**
	 * Set Header lines
	 *
	 * @param row
	 * @param columnCount
	 * @param headerData
	 * @param workbook
	 */
	public void createHeaderWithCenterStyling(Row row, int columnCount, Object[] headerData, XSSFWorkbook workbook) {

		/**
		 * Header Style
		 */
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(12);
		style.setFont(font);

		// Set the alignment to center
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		// Set borders for cells
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);

		/**
		 * Header lines
		 */
		for (Object field : headerData) {
			createCell(row, columnCount++, field, style);
		}
	}

	/**
	 * Set Date lines
	 *
	 * @param row0
	 * @param columnCount
	 * @param headerDataNew
	 * @param workbook
	 */
	void createDate(Row row0, int columnCount, Object[] headerDataNew, XSSFWorkbook workbook) {
		/**
		 * Header Style
		 */
		CellStyle style1 = workbook.createCellStyle();
		XSSFFont font1 = workbook.createFont();
		font1.setBold(true);
		font1.setFontHeight(12);
		style1.setFont(font1);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);

		/**
		 * Date lines
		 */
		for (Object field : headerDataNew) {
			createCell(row0, columnCount++, field, style1);
		}

	}

	/**
	 * Set Data Styles
	 *
	 * @param workbook
	 * @return
	 */
	public XSSFCellStyle setDataStyle(XSSFWorkbook workbook) {
		XSSFCellStyle style3 = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(10);
		style3.setBorderTop(BorderStyle.THIN);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setFont(font);
		return style3;
	}

	private static boolean hasNonEmptyCell(Row row) {
		for (Cell cell : row) {
			if (cell == null || cell.getCellType() == CellType.BLANK
					|| cell.getCellType() == CellType.STRING && cell.getStringCellValue().isEmpty()) {
				return false;
			} else {
				return true;
			}

		}
		return false; // All cells in the row are blank
	}

	public static int countNonEmptyRows(Sheet sheet) {
		int count = 0;

		for (Row row : sheet) {
			if (hasNonEmptyCell(row)) {
				count++;
			}
		}

		return count;
	}

	// Bulk room config dropdown
	public static void addDropdownDataToHiddenSheet(XSSFSheet hiddenSheet, String columnName,
			List<String> dropdownOptions) {
		// Determine the column to place the data based on columnName
		int columnIndex = 0; // Start with first column (A)
		if (columnName.equals("FloorNames")) {
			columnIndex = 1;
		} else if (columnName.equals("Categories")) {
			columnIndex = 6;
		} else if (columnName.equals("vacation")) {
			columnIndex = 4;
		} else if (columnName.equals("Gender")) {
			columnIndex = 3;
		}  else if (columnName.equals("Course Head")) {
			columnIndex = 4;
		}

		for (int i = 0; i < dropdownOptions.size(); i++) {
			Row row = hiddenSheet.getRow(i);
			if (row == null) {
				row = hiddenSheet.createRow(i);
			}
			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(dropdownOptions.get(i));
		}
	}

	public static void addDropdownToColumn(Sheet sheet, int firstRow, int lastRow, int column, String formula) {
		DataValidationHelper validationHelper = sheet.getDataValidationHelper();
		DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
		CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, column, column);
		DataValidation validation = validationHelper.createValidation(constraint, addressList);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);
	}

	public static void cellError(String msg, int column, int rowCount, HostelRoomInfoDto object) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0)
			object.setExcelErrorMsg(msg);
		else
			object.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		System.out.println(object.getExcelErrorMsg());
		object.setError("error");
	}

	public static String getCellValueAsString(Cell cell) {
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return String.valueOf((int) cell.getNumericCellValue()); // Cast to int to avoid decimal points

		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return "";
		}
	}

	// Dropdown for sheet
	public void addDropdownDataToHiddenSheet(XSSFSheet hiddenSheet, Integer columnIndex, List<String> dropdownOptions) {
		// Determine the columnIndex to place the data based on columnName
		for (int i = 0; i < dropdownOptions.size(); i++) {
			Row row = hiddenSheet.getRow(i);
			if (row == null) {
				row = hiddenSheet.createRow(i);
			}
			Cell cell = row.createCell(columnIndex);
			cell.setCellValue(dropdownOptions.get(i));
		}
	}

	public void cellError(String msg, int column, int rowCount, RoomInventoryForm object) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0)
			object.setExcelErrorMsg(msg);
		else
			object.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		System.out.println(object.getExcelErrorMsg());
		object.setError("error");
	}
	
	public void createAndSetColumn(Sheet sheet, Row row, int columnCount, int columnWidth, String cellValue, CellStyle style) {
	    createCell(row, columnCount, cellValue, style);
	    // If columnWidth is provided (-1 indicates dynamic), set it
	    if (columnWidth > 0) {
	        sheet.setColumnWidth(columnCount, columnWidth);
	    }
	}
	
    // Method to set the style for the header cells
    public XSSFCellStyle setHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        
        // Set the font for the header
        XSSFFont font = workbook.createFont();
        font.setBold(true);  // Make the font bold
        font.setFontHeightInPoints((short) 12);  // Set the font size
        
        // Apply the font to the style
        style.setFont(font);
        
        // Set alignment options
        style.setAlignment(HorizontalAlignment.CENTER);  // Horizontal alignment to center
        style.setVerticalAlignment(VerticalAlignment.CENTER);  // Vertical alignment to center
        
        // Add borders to the header cells
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    // Method to set the style for centered text (for merged cells)
    public XSSFCellStyle setCenterAlignStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        
        // Set the alignment to center
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Set borders for cells
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

}
