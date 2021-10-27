package com.un.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
			List<RollNumberForSeatArrangement> rollPerRoom, ZipOutputStream zipOut) {
		try {

			String file = this.getClass().getResource("/static/signature_sheet.xlsx").getFile();
			InputStream input = new FileInputStream(file);
			Workbook document = new XSSFWorkbook(input);
			Sheet outputSheet = document.getSheet(sheetName);

			int rowNmber = 3;
			for (RollNumberForSeatArrangement roll : rollPerRoom) {
				int cellNumber = 0;
				Row signetureRow = outputSheet.createRow(rowNmber++);
				Cell cell = signetureRow.createCell(cellNumber++);
				cell.setCellValue(roll.getPrefix() + roll.getRollNumber());
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			byte[] responseByteArray = out.toByteArray();
			String name = reportRoomDTO.getName();
			ZipEntry zipEntry = new ZipEntry((prefix + " " + name + " - Sigeture sheet").toUpperCase() + ".xlsx");
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
}
