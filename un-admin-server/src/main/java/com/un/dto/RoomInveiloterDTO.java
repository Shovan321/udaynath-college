package com.un.dto;

import java.util.List;

import lombok.Data;

@Data
public class RoomInveiloterDTO {
	private List<AlertRoom> alertsRooms;
	private Object selectedRooms;
	int invesiloterSize = 1;
}
