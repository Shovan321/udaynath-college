package com.un.web;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.service.RpoomArrangementService;
import com.un.util.UNContsant;

@RequestMapping(value = "/api/dashboard")
@RestController
public class RoomArrangementResource {
	@Autowired
	private RpoomArrangementService service;
	@PostMapping("/room-arrangement")
	public ResponseEntity<InputStreamResource> downloadRoomArrangementDetails(HttpServletResponse response,@RequestBody ReportDTO dto) throws IOException {
		List<ReportRoomDTO> selectedRooms = dto.getSelectedRooms();
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			reportRoomDTO.setTitle(dto.getTitle().toUpperCase());
			return service.getRoomArrangementReport(response, reportRoomDTO);
		}
		return null;
	}
}
