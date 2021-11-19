package com.un.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.dto.RoomAndInvigilatorDetail;
import com.un.service.MemoService;
import com.un.service.RpoomArrangementService;

@RequestMapping(value = "/api/dashboard")
@RestController
public class RoomArrangementResource {
	@Autowired
	private RpoomArrangementService service;

	@Autowired
	private MemoService memoService;

	@PostMapping(value = "/room-arrangement", produces = "application/zip")
	public void downloadRoomArrangementDetails(HttpServletResponse response, @RequestBody ReportDTO dto)
			throws IOException, ParseException {
		ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

		service.getRoomArrangementReport(response, dto, zipOut);
		
		List<ReportRoomDTO> selectedRooms = dto.getSelectedRooms();
		List<RoomAndInvigilatorDetail> selectedRoomsForInvesiloter = dto.getSelectedRoomsForInvesiloter();
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
			if (rollNumberList == null || rollNumberList.isEmpty()) {
				continue;
			}
			RoomAndInvigilatorDetail invDetails = selectedRoomsForInvesiloter.stream()
					.filter(r -> r.getId().equals(reportRoomDTO.getName())).findAny()
					.orElse(new RoomAndInvigilatorDetail());
			reportRoomDTO.setTitle(dto.getTitle().toUpperCase());
			byte[] roomArrangementReport = service.getSeatChartArrangementReport(response, reportRoomDTO, invDetails);

			String name = reportRoomDTO.getName();
			ZipEntry zipEntry = new ZipEntry(name + "- Seat Chart" + ".docx");
			zipEntry.setSize(roomArrangementReport.length);
			zipOut.putNextEntry(zipEntry);

			InputStream inputStream = new ByteArrayInputStream(roomArrangementReport);
			StreamUtils.copy(inputStream, zipOut);
			zipOut.closeEntry();
		}
		memoService.save(dto);
		zipOut.finish();
		zipOut.close();
		response.setStatus(HttpServletResponse.SC_OK);
		String zipFileName = "room-memo.zip";
		response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
		response.addHeader("file-name", zipFileName);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"");
	}
}
