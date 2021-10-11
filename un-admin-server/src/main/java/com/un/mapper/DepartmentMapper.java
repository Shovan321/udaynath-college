package com.un.mapper;

import org.springframework.stereotype.Component;

import com.un.dto.DepartmentDTO;
import com.un.entity.Department;

@Component
public class DepartmentMapper {
	public DepartmentDTO toDTO(Department dept) {
		DepartmentDTO departmentDTO = new DepartmentDTO();
		departmentDTO.setId(dept.getId());
		departmentDTO.setDepartmentName(dept.getDepartmentName());
		departmentDTO.setDepartmentCode(dept.getDepartmentCode());
		return departmentDTO;
	}
	public Department toEntity(DepartmentDTO dto) {
		Department department = new Department();
		department.setId(dto.getId());
		department.setDepartmentName(dto.getDepartmentName());
		department.setDepartmentCode(dto.getDepartmentCode());
		return department;
	}
}
