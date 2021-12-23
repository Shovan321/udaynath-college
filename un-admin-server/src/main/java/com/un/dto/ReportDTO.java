package com.un.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReportDTO {
	public String dateOfExam;
	public String title;
	public String examName;
	public List<ReportRoomDTO> selectedRooms;
	public List<RoomAndInvigilatorDetail> selectedRoomsForInvesiloter;
}
