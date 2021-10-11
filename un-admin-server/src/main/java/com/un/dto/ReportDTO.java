package com.un.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReportDTO {
	public String title;
	public List<ReportRoomDTO> selectedRooms;
}
