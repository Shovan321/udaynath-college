package com.un.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
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
import com.un.util.DocsUtil;
import com.un.util.UNContsant;

@Service
public class RpoomArrangementService {

	@Autowired
	SignetureSheetService signetureSheetService;
	
	String fontFamily = "Calibri";

	private void createReportHeader(ReportRoomDTO reportRoomDTO, XWPFHeaderFooterPolicy policy, String type, String date) {
		@SuppressWarnings("static-access")
		XWPFHeader headerD = policy.createHeader(policy.DEFAULT);

		setHeaderAndTitleText(headerD.createParagraph(), UNContsant.ROOM_ARRANGEMENT_HEADER, false);
		setHeaderAndTitleText(headerD.createParagraph(), reportRoomDTO.getTitle(), false);
		setHeaderAndTitleText(headerD.createParagraph(), type, false);
		setHeaderAndTitleText(headerD.createParagraph(), "Date : "+date, true);
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
			RoomAndInvigilatorDetail invDetails, String examName, String date, String sittingOfExam,
			List<XWPFDocument> seatChartDocuments, Integer rollLength)
			throws IOException, ParseException {

		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
		if (rollNumberList == null || rollNumberList.isEmpty()) {
			return null;
		}
		long count = rollNumberList.stream().filter(d -> d != null).flatMap(d -> d.stream())
				.filter(d -> d != null && !d.isEmpty()).count();
		if (count == 0l) {
			return null;
		}
		String file = this.getClass().getResource("/static/Seat_Chart_Template.docx").getFile();
		InputStream input = new FileInputStream(file);

		XWPFDocument document = new XWPFDocument(input);
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		List<Integer> invListOfParagraph = new ArrayList<>();
		invListOfParagraph.add(8);
		invListOfParagraph.add(15);
		invListOfParagraph.add(17);
		invListOfParagraph.add(18);
		invListOfParagraph.add(19);
		int invIndex = 0;
		List<InvesilotersDTO> invesiloters = invDetails.getInvesiloters();
		for (Integer key : invListOfParagraph) {
			try {
				XWPFParagraph xwpfParagraph = paragraphs.get(key);
				XWPFRun iRunElement = (XWPFRun) xwpfParagraph.getIRuns().get(0);
				InvesilotersDTO invesilotersDTO = invesiloters.get(invIndex);
				String name = invesilotersDTO.getName();
				if (name != null) {
					iRunElement.setText(" " + name);
					invIndex++;
				}
			} catch (Exception e) {
			}
		}

		try {
			DocsUtil.replaceParagraph(paragraphs.get(1), "EXAMNAMEWILLREPLACE", examName);
			DocsUtil.replaceParagraph(paragraphs.get(2), "ROOMNAMEREPLACE", reportRoomDTO.getName());
			DocsUtil.replaceParagraph(paragraphs.get(3), "22.04.2021", date);
			DocsUtil.replaceParagraph(paragraphs.get(3), "SITTINGREPLACE", sittingOfExam);
		} catch (Exception e) {
		}
		List<XWPFTable> tables = document.getTables();

		int lastRowIndex = rollNumberList.stream().max(Comparator.comparing(List::size)).get().size();

		XWPFTable contentTable = tables.get(0);
		XWPFTableRow prevRow = contentTable.getRow(1);
		XWPFTableCell prevRowCell = prevRow.getCell(1);

		for (XWPFTableRow sRow : contentTable.getRows()) {
			for (XWPFTableCell cell : sRow.getTableCells()) {
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				XWPFParagraph xwpfParagraph = cell.getParagraphs().get(0);
				xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);
				XWPFRun createRun = xwpfParagraph.createRun();
				createRun.setFontSize(10);
			}
		}

		for (int rI = 0; rI < lastRowIndex - 20; rI++) {
			XWPFTableRow createRow = contentTable.createRow();

			createRow.setHeight(prevRow.getHeight());

			for (XWPFTableCell cell : createRow.getTableCells()) {
				CTTc ctTc = cell.getCTTc();
				ctTc.addNewTcPr().addNewTcBorders();

				ctTc.getTcPr().setTcBorders(prevRowCell.getCTTc().getTcPr().getTcBorders());
				XWPFParagraph xwpfParagraph = cell.getParagraphs().get(0);
				xwpfParagraph.setAlignment(ParagraphAlignment.CENTER);
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
					if (cell == null) {
						cell = contentRow.createCell();
					}
					if (value == null) {
						value = "";
					}
					cell.setText(value);
				}
			}
		}
		int rollNumberLength = rollLength != null ? rollLength : 3;
		
		Map<String, List<String>> countOfSTudent = rollNumberList.stream()
			.flatMap(d -> d.stream())
			.filter(d -> d != null && (d.length() > rollNumberLength))
			.collect(Collectors.groupingBy(d -> d.substring(0, d.length() - rollNumberLength)));
		
		List<String> countList = new ArrayList<>();
		countOfSTudent.forEach((k, v) -> {
			int size = v.size();
			countList.add(k+" :"+size);
		});
		
		String countString = countList.stream().collect(Collectors.joining(", "));
		DocsUtil.replaceParagraph(paragraphs.get(20), "StudentCountPerClass", "STUDENT COUNT: "+ countString);
		
		long totalSTudentCount = rollNumberList.stream()
				.flatMap(d -> d.stream())
				.filter(d -> d != null && (d.length() > rollNumberLength)).count();
		XWPFParagraph p = paragraphs.get(13);
		DocsUtil.replaceParagraph(p, "TOTAL", "TOTAL  ="+totalSTudentCount);
		
		seatChartDocuments.add(document);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		document.close();
		out.close();

		return out.toByteArray();
	}

	public byte[] getRoomArrangementReport(HttpServletResponse response, ReportDTO dto, ZipOutputStream zipOut)
			throws IOException {

		int rollNumberLength = 3;
		if(dto.getRollNumberLength() != null) {
			rollNumberLength = dto.getRollNumberLength();
		}
		dto.setRollNumberLength(rollNumberLength);
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
		String dateOfExam = dto.dateOfExam;
		dateOfExam = getFormatedDate(dateOfExam);
		boolean firstTime = true;
		int rowIndex = 1;
		List<List<String>> roomsRollList = new ArrayList<>();
		
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			if (firstTime) {
				reportRoomDTO.setTitle(dto.getExamName().toUpperCase());
				XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
				if (policy.getDefaultHeader() == null && policy.getFirstPageHeader() == null
						&& policy.getDefaultFooter() == null) {

					createReportHeader(reportRoomDTO, policy, UNContsant.ROOM_ARRANGEMENT_HEADER_2, dateOfExam);
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
				String prefix = data.substring(0, length - rollNumberLength);
				roll.setPrefix(prefix);
				roll.setRollNumber(Integer.valueOf(data.substring(length - rollNumberLength)));
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
				roomsRollList.add(getStudentDetailsReportTable(table, rowIndex, max, min, prefix, name, document));
				signetureSheetService.generateSignetureSheet(reportRoomDTO, prefix, rollPerRoom, zipOut,
						dto.getExamName(), dto.getDateOfExam(), dto.getSittingOfExam());
			}
		}

		roomsRollList.sort((o1, o2) -> o1.get(1).compareTo(o2.get(1)));//by rooll number
		for(List<String> cur : roomsRollList) {
			XWPFTableRow newRow = table.createRow();
			newRow.getCell(0).setText(rowIndex+"");
			XWPFTableCell cell = newRow.getCell(1);
			cell.setText(cur.get(1));
			newRow.getCell(2).setText(cur.get(2));
			rowIndex++;
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

	public byte[] getDutyLitReport(HttpServletResponse response, ReportDTO dto, ZipOutputStream zipOut)
			throws IOException {
		List<ReportRoomDTO> selectedRooms = dto.getSelectedRooms();
		if(selectedRooms == null || selectedRooms.isEmpty()) {
			return null;
		}
		XWPFDocument document = new XWPFDocument();
		XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
		if (policy.getDefaultHeader() == null && policy.getFirstPageHeader() == null
				&& policy.getDefaultFooter() == null) {
			ReportRoomDTO reportRoomDTO = selectedRooms.get(0);
			reportRoomDTO.setTitle(dto.getExamName().toUpperCase());
			String dateOfExam = dto.dateOfExam;
			dateOfExam = getFormatedDate(dateOfExam);
			createReportHeader(reportRoomDTO, policy, UNContsant.DUTY_LIST, dateOfExam);
		}
		
		XWPFTable table = document.createTable();
		table.setWidth("100.00%");
		XWPFTableRow row = table.getRow(0); // First row
		// Columns
		row.getCell(0).setText("Sl. No.");
		row.addNewTableCell().setText("ROOM NO");
		row.addNewTableCell().setText("NAME");

		List<RoomAndInvigilatorDetail> selectedRoomsForInvesiloter = dto.getSelectedRoomsForInvesiloter();

		int rowIndex = 1;
		for (RoomAndInvigilatorDetail detail : selectedRoomsForInvesiloter) {
			if ("Invesiloters".equalsIgnoreCase(detail.getId())) {
				continue;
			}
			List<InvesilotersDTO> invesiloters = detail.getInvesiloters();
			if (invesiloters != null && !invesiloters.isEmpty()) {
				for (InvesilotersDTO inv : invesiloters) {
					XWPFTableRow newRow = table.createRow();
					newRow.getCell(0).setText((rowIndex) + "");
					newRow.getCell(1).setText(detail.getId());
					newRow.getCell(2).setText(inv.getName());
					rowIndex++;
				}
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.write(out);
		out.close();
		document.close();
		byte[] responseByteArray = out.toByteArray();
		ZipEntry zipEntry = new ZipEntry("Duty-List" + ".docx");
		zipEntry.setSize(responseByteArray.length);
		zipOut.putNextEntry(zipEntry);

		InputStream inputStream = new ByteArrayInputStream(responseByteArray);
		StreamUtils.copy(inputStream, zipOut);
		zipOut.closeEntry();
		return responseByteArray;
	}

	private List<String> getStudentDetailsReportTable(XWPFTable table, int rowIndex, int max, int min, String prefix,
			String name, XWPFDocument document) {
		//XWPFTableRow newRow = table.createRow();
		//newRow.getCell(0).setText((rowIndex) + "");
		String minPrefix = getDataWithPrefix(min);
		String maxPrefix = getDataWithPrefix(max);
		//XWPFTableCell cell = newRow.getCell(1);
		//cell.setText(prefix + minPrefix + " TO " + prefix + maxPrefix);
		//newRow.getCell(2).setText(name);
		
		List<String> rowDatas = new ArrayList<>();
		rowDatas.add((rowIndex) + "");
		rowDatas.add(prefix + minPrefix + " TO " + prefix + maxPrefix);
		rowDatas.add(name);
		//return rowIndex + 1;
		return rowDatas;
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
	private String getFormatedDate(String dateOfExam) {
		return dateOfExam.replaceAll("/.", "/");
	}
}
