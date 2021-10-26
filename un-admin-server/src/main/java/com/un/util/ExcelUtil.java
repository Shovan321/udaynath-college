package com.un.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
	
	public static void setCellBorder(Workbook workbook, Sheet sheet){
		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		int maxCell = 0;
		for(Row row : sheet) {
			if(row == null) {
				continue;
			}
			short lastCellNum = row.getLastCellNum();
			if(maxCell < lastCellNum) {
				maxCell = lastCellNum;
			}
			for(int i=0; i< lastCellNum; i++) {
				Cell cell = row.getCell(i);
				if(cell != null)
				cell.setCellStyle(style);
			}
		}
		
		for(Row row : sheet) {
			if(row == null) {
				continue;
			}
			for(int i=0; i< maxCell; i++) {
				Cell cell = row.getCell(i);
				if(cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellStyle(style);
			}
		}
	}
}
