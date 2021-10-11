package com.un.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.un.dto.DepartmentDTO;
import com.un.entity.Department;
import com.un.mapper.DepartmentMapper;
import com.un.repo.DepartmentRepo;

@Service
public class DepartmentService {

	@Autowired
	DepartmentMapper mapper;
	
	@Autowired
	DepartmentRepo repo;
	
	public DepartmentDTO create(DepartmentDTO dto) {
		Department entity = mapper.toEntity(dto);
		entity = repo.save(entity);
		return mapper.toDTO(entity);
	}

	public List<DepartmentDTO> findAll() {
		Iterable<Department> findAll = repo.findAll();
		List<DepartmentDTO> dtos = new ArrayList<>();
		findAll.forEach(e -> dtos.add(mapper.toDTO(e)));
		try {
			dtos.sort(Comparator.comparing(DepartmentDTO::getDepartmentCode));
		} catch (Exception e) {
		}
		return dtos;
	}

	public void delete(Long deptId) {
		repo.deleteById(deptId);
	}

}
