package com.un.dto;


import lombok.Data;

@Data
public class RoomDTO {
	public Long id;
	public String name;
	public int noOfRow;
	public int rowCapacity;
	public Long deptId;
	public String deptName;
	public Integer start;
	public Integer end;
}
