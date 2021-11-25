package com.un.web;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.un.dto.AlertRoom;
import com.un.dto.InvesilotersDTO;
import com.un.dto.RoomInveiloterDTO;
import com.un.service.InvesiloterService;

@RestController
@RequestMapping(value = "/api/invesiloters")
public class InvesiloterResource {
	
	@Autowired
	private InvesiloterService service ; 
	@PostMapping
	public List<InvesilotersDTO> processStudent(@RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		List<InvesilotersDTO> processStudentFile = service.processStudentFile(workbook);
		return processStudentFile;
	}
	
	@PostMapping(value = "/manage-invesiloter")
	public RoomInveiloterDTO mappingRoomAndInvesiloter(@RequestBody RoomInveiloterDTO dto) {
		RoomInveiloterDTO mappingRoomAndInvesiloter = service.mappingRoomAndInvesiloter(dto);
		
		List<AlertRoom> alertsRooms = mappingRoomAndInvesiloter.getAlertsRooms();
		for (AlertRoom alertRoom : alertsRooms) {
			List<InvesilotersDTO> invesiloters = alertRoom.getInvesiloters();
			invesiloters.sort(Comparator.comparing(InvesilotersDTO::getDepartment));
		}
		return mappingRoomAndInvesiloter;
	}
}
