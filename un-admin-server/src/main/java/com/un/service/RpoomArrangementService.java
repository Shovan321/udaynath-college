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
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.hibernate.engine.jdbc.StreamUtils;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
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
		String file = this.getClass().getResource("/static/Seat_Chart_Template.docx").getFile();
		InputStream input = new FileInputStream(file);

		XWPFDocument document = new XWPFDocument(input);
		List<XWPFTable> tables = document.getTables();

		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
		int lastRowIndex = rollNumberList.stream().max(Comparator.comparing(List::size)).get().size();

		XWPFTable contentTable = tables.get(0);
		XWPFTableRow prevRow = contentTable.getRow(1);
		XWPFTableCell prevRowCell = prevRow.getCell(1);

		for (XWPFTableRow sRow : contentTable.getRows()) {
			for (XWPFTableCell cell : sRow.getTableCells()) {
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
			}
		}

		for (int rI = 0; rI < lastRowIndex - 20; rI++) {
			XWPFTableRow createRow = contentTable.createRow();

			createRow.setHeight(prevRow.getHeight());

			for (XWPFTableCell cell : createRow.getTableCells()) {
				CTTc ctTc = cell.getCTTc();
				ctTc.addNewTcPr().addNewTcBorders();

				ctTc.getTcPr().setTcBorders(prevRowCell.getCTTc().getTcPr().getTcBorders());
				cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);

			}
		}
		int serialNo = 1;
		for (int i = 1; i <= lastRowIndex; i++) {
			XWPFTableCell cell = contentTable.getRow(i).getCell(0);
			if (cell.getText() == null || cell.getText().isEmpty()) {
				cell.setText(serialNo++ + "");
			}
		}
		int headerColumnSize = rollNumberList.size();
		for (int columnIndex = 1; columnIndex <= headerColumnSize; columnIndex++) {
			List<String> oneRowData = rollNumberList.get(columnIndex - 1);

			for (int rowIndex = 0; rowIndex < oneRowData.size(); rowIndex++) {
				XWPFTableRow contentRow = contentTable.getRow(rowIndex + 1);
				XWPFTableCell cell = null;
				if (contentRow != null) {
					String value = oneRowData.get(rowIndex);
					cell = contentRow.getCell(columnIndex);
					cell.setText(value);
				}
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		document.close();
		out.close();

		return out.toByteArray();
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
