package com.un.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.un.dto.ReportRoomDTO;
import com.un.util.ReportResponseProvider;
import com.un.util.UNContsant;

@Service
public class RpoomArrangementService {
	String fontFamily = "Calibri";

	public ResponseEntity<InputStreamResource> getRoomArrangementReport(HttpServletResponse response,
			ReportRoomDTO reportRoomDTO) throws IOException {

		@SuppressWarnings("resource")
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
		ResponseEntity<InputStreamResource> body = ReportResponseProvider.getResponse(response, responseByteArray,
				"test.docx");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
		return body;

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
		List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
		int rowIndex = 1;
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

}
