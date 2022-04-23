package com.un.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.un.dto.ReportRoomDTO;
import com.un.dto.RollNumberForSeatArrangement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SignetureSheetService {
	@Value("${user.signeture.data}")
	public String sheetName;

	public synchronized void generateSignetureSheet(ReportRoomDTO reportRoomDTO, String prefix,
			List<RollNumberForSeatArrangement> rollPerRoom, ZipOutputStream zipOut, String examName, String date, String sitting) {
		try {

			String file = this.getClass().getResource("/static/signature_sheet.xlsx").getFile();
			InputStream input = new FileInputStream(file);
			Workbook document = new XSSFWorkbook(input);
			Sheet outputSheet = document.getSheet(sheetName);
			CellStyle cellStyle = getStyle(document);
	        
			Cell fCell = outputSheet.getRow(0).getCell(0);
			String stringCellValue = fCell.getStringCellValue();
			stringCellValue = stringCellValue.replace("S- 26", reportRoomDTO.getName());
			stringCellValue = stringCellValue.replace("3RD SEMESTER EXAMINATIONS -2021", examName);
			stringCellValue = stringCellValue.replace("20.04.2021", date);
			stringCellValue = stringCellValue.replace("SITTINGREPLACE", sitting);
			
			fCell.setCellValue(stringCellValue);
			int rowNmber = 3;
			for (RollNumberForSeatArrangement roll : rollPerRoom) {
				int cellNumber = 0;
				Row signetureRow = outputSheet.createRow(rowNmber++);
				Cell cell = signetureRow.createCell(cellNumber++);
				cell.setCellStyle(cellStyle);
				String rollNumber = getDataWithPrefix(roll.getRollNumber());
				for(int i = 1; i< 5; i++) {
					Cell bcell = signetureRow.createCell(i);
					bcell.setCellStyle(cellStyle);
				}
				cell.setCellValue(roll.getPrefix() + rollNumber);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			byte[] responseByteArray = out.toByteArray();
			String name = reportRoomDTO.getName();
			ZipEntry zipEntry = new ZipEntry((prefix + " " + name + " - Signature sheet").toUpperCase() + ".xlsx");
			zipEntry.setSize(responseByteArray.length);
			zipOut.putNextEntry(zipEntry);
			out.close();
			document.close();
			InputStream inputStream = new ByteArrayInputStream(responseByteArray);
			StreamUtils.copy(inputStream, zipOut);
			zipOut.closeEntry();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	private CellStyle getStyle(Workbook document) {
		CellStyle cellStyle = document.createCellStyle();
		
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return cellStyle;
	}
	String getDataWithPrefix(int number) {
		if (number <= 9) {
			return "00" + number;
		} else if (number <= 99) {
			return "0" + number;
		} else {
			return "" + number;
		}
	}
}
