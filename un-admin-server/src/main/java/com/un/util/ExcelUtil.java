package com.un.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtil {
	public static void mergeCell(Sheet sheet, int fr, int lr, int fc, int lc) {
		CellRangeAddress mergeCellValue = new CellRangeAddress(fr, lr, fc, lc);
		try {
			sheet.addMergedRegion(mergeCellValue);
		} catch (Exception e) {
		}
	}

	public static void setCellBorder(Workbook document, Sheet sheet, int maxRow) {

		CellStyle style = document.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		

		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		Font font = document.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 11);
		font.setFontName("Calibri");
		style.setFont(font);

		CellStyle leftAllignStyle = document.createCellStyle();
		leftAllignStyle.setBorderBottom(BorderStyle.THIN);
		leftAllignStyle.setBorderTop(BorderStyle.THIN);
		leftAllignStyle.setBorderRight(BorderStyle.THIN);
		leftAllignStyle.setBorderLeft(BorderStyle.THIN);

		leftAllignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		leftAllignStyle.setAlignment(HorizontalAlignment.LEFT);
		leftAllignStyle.setFont(font);

		int maxCell = 0;
		for (Row row : sheet) {
			if (row == null) {
				continue;
			}
			short lastCellNum = row.getLastCellNum();
			if (maxCell < lastCellNum) {
				maxCell = lastCellNum;
			}
		}

		int rowIndex = 0;
		for (Row row : sheet) {

			if (row == null) {
				continue;
			}
			for (int i = 0; i < maxCell; i++) {
				sheet.autoSizeColumn(i);
				Cell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellStyle(style);
				if (maxRow >= rowIndex) {
					cell.setCellStyle(leftAllignStyle);
				}
			}
			rowIndex++;
		}

	}
}
