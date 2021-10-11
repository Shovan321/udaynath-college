package com.un.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ReportRoomDTO {
	String deptId;
	String deptName;
	String id;
	String name;
	String noOfRow;
	List<List<String>> rollNumberList;
	String rowCapacity;
	String title;
	Date date;
}
