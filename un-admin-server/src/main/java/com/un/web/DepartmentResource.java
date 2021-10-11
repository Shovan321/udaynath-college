package com.un.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.DepartmentDTO;
import com.un.dto.UNResponse;
import com.un.service.DepartmentService;

@RestController
@RequestMapping(value = "api/department")
public class DepartmentResource {
	
	@Autowired
	private DepartmentService departmentService;
	
	@PostMapping
	public DepartmentDTO create(@RequestBody DepartmentDTO dto) {
		return departmentService.create(dto);
	}
	
	@GetMapping
	public UNResponse<DepartmentDTO> findAll() {
		List<DepartmentDTO> dtos = departmentService.findAll();
		UNResponse<DepartmentDTO> body = new UNResponse<>();
		body.setData(dtos);
		return body ;
	}
	
	@DeleteMapping(value = "/{deptId}")
	public void delete(@PathVariable Long deptId) {
		departmentService.delete(deptId);
	}
}
