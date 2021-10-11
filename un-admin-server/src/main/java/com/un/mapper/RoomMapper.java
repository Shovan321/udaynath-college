package com.un.mapper;

import org.springframework.stereotype.Component;

import com.un.dto.RoomDTO;
import com.un.entity.Department;
import com.un.entity.Room;

@Component
public class RoomMapper {
	public Room toEntity(RoomDTO dto) {
		Room room = new Room();
		room.setId(dto.getId());
		room.setName(dto.getName());
		room.setNoOfRow(dto.getNoOfRow());
		room.setRowCapacity(dto.getRowCapacity());
		Department department = new Department();
		Long deptId = dto.getDeptId();
		if (deptId != null) {
			department.setId(deptId);
			room.setDepartment(department);
		}
		return room;
	}
	
	public RoomDTO toDTO(Room entity) {
		RoomDTO room = new RoomDTO();
		room.setId(entity.getId());
		room.setName(entity.getName());
		room.setNoOfRow(entity.getNoOfRow());
		room.setRowCapacity(entity.getRowCapacity());
		Department department = entity.getDepartment();
		if (department != null) {
			room.setDeptId(department.getId());
			String departmentName = department.getDepartmentName();
			room.setDeptName(departmentName);
		}
		return room;
	}
}
