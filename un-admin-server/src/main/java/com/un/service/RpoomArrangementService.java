package com.un.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.un.dto.InvesilotersDTO;
import com.un.dto.ReportRoomDTO;
import com.un.dto.RoomAndInvigilatorDetail;
import com.un.util.ExcelUtil;
import com.un.util.ReportResponseProvider;
import com.un.util.UNContsant;

@Service
public class RpoomArrangementService {
	String fontFamily = "Calibri";

	public byte[] getRoomArrangementReport(HttpServletResponse response, ReportRoomDTO reportRoomDTO)
			throws IOException {

		XWPFDocument document = new XWPFDocument();

		XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
		if (policy.getDefaultHeader() == null && policy.getFirstPageHeader() == null
				&& policy.getDefaultFooter() == null) {

			createReportHeader(reportRoomDTO, policy);

			getStudentDetailsReportTable(reportRoomDTO, document);

		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		out.close();
		byte[] responseByteArray = out.toByteArray();
		return responseByteArray;
	}

	private void getStudentDetailsReportTable(ReportRoomDTO reportRoomDTO, XWPFDocument document) {
		XWPFTable table = document.createTable();
		table.setWidth("100.00%");
		XWPFTableRow row = table.getRow(0); // First row
		// Columns
		row.getCell(0).setText("Sl. No.");
		row.addNewTableCell().setText("ROLL NO.");
		row.addNewTableCell().setText("ROOM NO.");
		row.addNewTableCell().setText("REG BACK");
		row.addNewTableCell().setText("SITTING & TIMING");
		int rowIndex = 1;
		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
		for (List<String> list : rollNumberList) {
			for (String rollNumber : list) {
				XWPFTableRow newRow = table.createRow();
				newRow.getCell(0).setText((rowIndex) + "");
				newRow.getCell(1).setText(rollNumber);
				rowIndex++;
			}
		}
	}

	private void createReportHeader(ReportRoomDTO reportRoomDTO, XWPFHeaderFooterPolicy policy) {
		XWPFHeader headerD = policy.createHeader(policy.DEFAULT);

		setHeaderAndTitleText(headerD.createParagraph(), UNContsant.ROOM_ARRANGEMENT_HEADER, false);
		setHeaderAndTitleText(headerD.createParagraph(), reportRoomDTO.getTitle(), false);
		setHeaderAndTitleText(headerD.createParagraph(), UNContsant.ROOM_ARRANGEMENT_HEADER_2, false);
		setHeaderAndTitleText(headerD.createParagraph(), "Date : DD/YY/MM", true);
	}

	private void setHeaderAndTitleText(XWPFParagraph paragraph, String pText, boolean right) {
		if (right) {
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
		} else {
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		}
		XWPFRun header = paragraph.createRun();
		header.setText(pText);
		header.setFontSize(14);
		header.setBold(true);
		header.setFontFamily(fontFamily);
	}

	public byte[] getSeatChartArrangementReport(HttpServletResponse response, ReportRoomDTO reportRoomDTO, RoomAndInvigilatorDetail invDetails)
			throws IOException, ParseException {
		Workbook document = new HSSFWorkbook();
		Sheet sheet = document.createSheet("Sheet");

		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();

		boolean headerRowPrintStatus = true;
		Row headerRow = sheet.createRow(0);
		Cell slTextCell = headerRow.createCell(0);
		slTextCell.setCellValue("Sl.No.");
		int lastRowIndex = 1;
		for (int columnIndex = 1; columnIndex < rollNumberList.size(); columnIndex++) {
			int incrementalCellForHeaderIndex = 1;
			List<String> oneRowData = rollNumberList.get(columnIndex - 1);
			for (int rowIndex = 1; rowIndex <= oneRowData.size(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					row = sheet.createRow(rowIndex);
				}
				if (headerRowPrintStatus) {
					Cell headerColumnCell = headerRow.createCell(incrementalCellForHeaderIndex++);
					headerColumnCell.setCellValue("Row" + rowIndex);
					Cell slCell = row.createCell(0);
					slCell.setCellValue(rowIndex);
				}
				Cell cell = row.createCell(columnIndex);
				cell.setCellValue(oneRowData.get(rowIndex - 1));
				lastRowIndex = rowIndex + 1;
			}
			headerRowPrintStatus = false;
		}
		List<InvesilotersDTO> invesiloters = invDetails.getInvesiloters();
		Row invRow = sheet.createRow(lastRowIndex++);
		Cell invCell = invRow.createCell(0);
		invCell.setCellValue("INVIGILATORS");
		if(invesiloters == null) {
			invesiloters = new ArrayList<>();
		}
		int resultCellRowIndex = lastRowIndex;
		for(InvesilotersDTO inv : invesiloters) {
			invRow = sheet.createRow(lastRowIndex++);
			invCell = invRow.createCell(0);
			invRow.createCell(1);
			invCell.setCellValue(inv.getName());
			ExcelUtil.mergeCell(sheet, lastRowIndex-1, lastRowIndex-1, 0, 1);
		}
		
		lastRowIndex = resultCellRowIndex;
		List<String> textList = Arrays.asList("REGULAR", "TOTAL");
		for(int i = 0; i< 2; i++) {
			invRow = sheet.getRow(lastRowIndex++);
			if(invRow == null) {
				lastRowIndex = lastRowIndex - 1;
				invRow = sheet.createRow(lastRowIndex++);				
			}
			invCell = invRow.createCell(2);
			invCell.setCellValue(textList.get(i));
			invRow.createCell(3);
			ExcelUtil.mergeCell(sheet, lastRowIndex-1, lastRowIndex-1, 2, 3);
		}
		
		lastRowIndex = resultCellRowIndex;
		textList = Arrays.asList("TOTAL", "PRESENT", "ABSENT");
		for(int i = 0; i< 3; i++) {
			invRow = sheet.getRow(lastRowIndex++);
			if(invRow == null) {
				lastRowIndex = lastRowIndex - 1;
				invRow = sheet.createRow(lastRowIndex++);				
			}
			invCell = invRow.createCell(4);
			invCell.setCellValue(textList.get(i));
			invRow.createCell(5);
			ExcelUtil.mergeCell(sheet, lastRowIndex-1, lastRowIndex-1, 4, 5);
		}
		ExcelUtil.setCellBorder(document, sheet);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		document.close();
		out.close();
		byte[] responseByteArray = out.toByteArray();
		return responseByteArray;
	}

}
