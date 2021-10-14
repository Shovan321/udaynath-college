package com.un.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.service.RpoomArrangementService;

@RequestMapping(value = "/api/dashboard")
@RestController
public class RoomArrangementResource {
	@Autowired
	private RpoomArrangementService service;
	@PostMapping(value = "/room-arrangement", produces="application/zip")
	public void downloadRoomArrangementDetails(HttpServletResponse response,@RequestBody ReportDTO dto) throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
		List<ReportRoomDTO> selectedRooms = dto.getSelectedRooms();
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			reportRoomDTO.setTitle(dto.getTitle().toUpperCase());
			byte[] roomArrangementReport = service.getRoomArrangementReport(response, reportRoomDTO);
			
			ZipEntry zipEntry = new ZipEntry(reportRoomDTO.getName()+".docx");
			zipEntry.setSize(roomArrangementReport.length);
			zipOut.putNextEntry(zipEntry);
			
			InputStream inputStream = new ByteArrayInputStream(roomArrangementReport);
			StreamUtils.copy(inputStream, zipOut);
			zipOut.closeEntry();
		}
		zipOut.finish();
		zipOut.close();
		response.setStatus(HttpServletResponse.SC_OK);
		String zipFileName="room-memo.zip";
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
		response.addHeader("file-name", zipFileName);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
	}
}
