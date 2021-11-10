package com.un.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.un.dto.InvesilotersDTO;
import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.dto.RollNumberForSeatArrangement;
import com.un.dto.RoomAndInvigilatorDetail;
import com.un.util.ExcelUtil;
import com.un.util.UNContsant;

@Service
public class RpoomArrangementService {

	@Autowired
	SignetureSheetService signetureSheetService;
	String fontFamily = "Calibri";

	private void createReportHeader(ReportRoomDTO reportRoomDTO, XWPFHeaderFooterPolicy policy) {
		@SuppressWarnings("static-access")
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

	public byte[] getSeatChartArrangementReport(HttpServletResponse response, ReportRoomDTO reportRoomDTO,
			RoomAndInvigilatorDetail invDetails) throws IOException, ParseException {

		String file = this.getClass().getResource("/static/Seat_Chart_Template.xlsx").getFile();
		InputStream input = new FileInputStream(file);
		Workbook document = new XSSFWorkbook(input);
		Sheet sheet = document.getSheetAt(0);

		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();

		// CellStyle cellStyle = sheet.getRow(1).getCell(0).getCellStyle();

		boolean headerRowPrintStatus = true;
		Row headerRow = sheet.createRow(1);
		Cell slTextCell = headerRow.createCell(0);
		slTextCell.setCellValue("Sl.No.");
		int lastRowIndex = rollNumberList.stream().max(Comparator.comparing(List::size)).get().size() + 2;
		int headerColumnSize = rollNumberList.size();
		for (int columnIndex = 1; columnIndex <= headerColumnSize; columnIndex++) {
			Cell headerColumnCell = headerRow.createCell(columnIndex);
			headerColumnCell.setCellValue("Row" + columnIndex);
			List<String> oneRowData = rollNumberList.get(columnIndex - 1);
			for (int rowIndex = 2; rowIndex <= oneRowData.size() + 1; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					row = sheet.createRow(rowIndex);
				}
				if (headerRowPrintStatus) {
					Cell slCell = row.createCell(0);
					slCell.setCellValue(rowIndex - 1);
				}
				Cell cell = row.createCell(columnIndex);
				String value = oneRowData.get(rowIndex - 2);
				cell.setCellValue(value);
			}
			headerRowPrintStatus = false;
		}
		if (headerColumnSize < 6) {
			for (int hi = headerColumnSize + 1; hi <= 6; hi++) {
				Cell blankCell = headerRow.createCell(hi);
				blankCell.setCellValue("Row" + hi);
			}
		}
		int breakRowIndex = lastRowIndex;
		List<InvesilotersDTO> invesiloters = invDetails.getInvesiloters();
		Row invRow = sheet.createRow(lastRowIndex++);
		Cell invCell = invRow.createCell(0);
		invCell.setCellValue("INVIGILATORS");
		ExcelUtil.mergeCell(sheet, lastRowIndex - 1, lastRowIndex - 1, 0, 1);
		if (invesiloters == null) {
			invesiloters = new ArrayList<>();
		}
		int resultCellRowIndex = lastRowIndex;
		for (InvesilotersDTO inv : invesiloters) {
			invRow = sheet.createRow(lastRowIndex++);
			invCell = invRow.createCell(0);
			invRow.createCell(1);
			invCell.setCellValue(inv.getName());
			ExcelUtil.mergeCell(sheet, lastRowIndex - 1, lastRowIndex - 1, 0, 1);
		}

		lastRowIndex = resultCellRowIndex;
		List<String> textList = Arrays.asList("REGULAR", "TOTAL");
		for (int i = 0; i < 2; i++) {
			invRow = sheet.getRow(lastRowIndex++);
			if (invRow == null) {
				lastRowIndex = lastRowIndex - 1;
				invRow = sheet.createRow(lastRowIndex++);
			}
			invCell = invRow.createCell(2);
			invCell.setCellValue(textList.get(i));
			invRow.createCell(3);
			ExcelUtil.mergeCell(sheet, lastRowIndex - 1, lastRowIndex - 1, 2, 3);
		}

		lastRowIndex = resultCellRowIndex;
		textList = Arrays.asList("TOTAL", "PRESENT", "ABSENT");
		for (int i = 0; i < 3; i++) {
			invRow = sheet.getRow(lastRowIndex++);
			if (invRow == null) {
				lastRowIndex = lastRowIndex - 1;
				invRow = sheet.createRow(lastRowIndex++);
			}
			invCell = invRow.createCell(4);
			invCell.setCellValue(textList.get(i));
			invRow.createCell(5);
			ExcelUtil.mergeCell(sheet, lastRowIndex - 1, lastRowIndex - 1, 4, 5);
		}
		ExcelUtil.setCellBorder(document, sheet, breakRowIndex);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		document.close();
		out.close();
		byte[] responseByteArray = out.toByteArray();
		return responseByteArray;
	}

	public byte[] getRoomArrangementReport(HttpServletResponse response, ReportDTO dto, ZipOutputStream zipOut)
			throws IOException {

		XWPFDocument document = new XWPFDocument();

		XWPFTable table = document.createTable();
		table.setWidth("100.00%");
		XWPFTableRow row = table.getRow(0); // First row
		// Columns
		row.getCell(0).setText("Sl. No.");
		row.addNewTableCell().setText("ROLL NO.");
		row.addNewTableCell().setText("ROOM NO.");
		row.addNewTableCell().setText("REG BACK");
		row.addNewTableCell().setText("SITTING & TIMING");

		List<ReportRoomDTO> selectedRooms = dto.getSelectedRooms();

		boolean firstTime = true;
		int rowIndex = 1;
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			if (firstTime) {
				reportRoomDTO.setTitle(dto.getTitle().toUpperCase());
				XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
				if (policy.getDefaultHeader() == null && policy.getFirstPageHeader() == null
						&& policy.getDefaultFooter() == null) {

					createReportHeader(reportRoomDTO, policy);
				}
				firstTime = false;
			}

			List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
			if (rollNumberList == null || rollNumberList.isEmpty()) {
				continue;
			}
			List<String> rollNumberSortedList = rollNumberList.stream().flatMap(m -> m.stream())
					.filter(p -> p != null && !p.isEmpty()).sorted(Comparator.comparing(String::valueOf))
					.collect(Collectors.toList());

			List<RollNumberForSeatArrangement> seatArrangement = new ArrayList<>();
			for (String data : rollNumberSortedList) {
				RollNumberForSeatArrangement roll = new RollNumberForSeatArrangement();
				int length = data.length();
				String prefix = data.substring(0, length - 3);
				roll.setPrefix(prefix);
				roll.setRollNumber(Integer.valueOf(data.substring(length - 3)));
				seatArrangement.add(roll);
			}

			Map<String, List<RollNumberForSeatArrangement>> studentSeatValue = seatArrangement.stream()
					.collect(Collectors.groupingBy(RollNumberForSeatArrangement::getPrefix));

			Set<String> keySet = studentSeatValue.keySet();
			for (String prefix : keySet) {
				List<RollNumberForSeatArrangement> rollPerRoom = studentSeatValue.get(prefix);
				IntSummaryStatistics collect = rollPerRoom.stream()
						.collect(Collectors.summarizingInt(RollNumberForSeatArrangement::getRollNumber));
				int max = collect.getMax();
				int min = collect.getMin();
				String name = reportRoomDTO.getName();
				rowIndex = getStudentDetailsReportTable(table, rowIndex, max, min, prefix, name, document);
				signetureSheetService.generateSignetureSheet(reportRoomDTO, prefix, rollPerRoom, zipOut);
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		out.close();
		document.close();
		byte[] responseByteArray = out.toByteArray();
		ZipEntry zipEntry = new ZipEntry("Room Arrangement" + ".docx");
		zipEntry.setSize(responseByteArray.length);
		zipOut.putNextEntry(zipEntry);

		InputStream inputStream = new ByteArrayInputStream(responseByteArray);
		StreamUtils.copy(inputStream, zipOut);
		zipOut.closeEntry();
		return responseByteArray;
	}

	private int getStudentDetailsReportTable(XWPFTable table, int rowIndex, int max, int min, String prefix,
			String name, XWPFDocument document) {
		XWPFTableRow newRow = table.createRow();
		newRow.getCell(0).setText((rowIndex) + "");
		String minPrefix = getDataWithPrefix(min);
		String maxPrefix = getDataWithPrefix(max);
		XWPFTableCell cell = newRow.getCell(1);
		cell.setText(prefix + minPrefix + " TO " + prefix + maxPrefix);
		newRow.getCell(2).setText(name);
		return rowIndex + 1;
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
