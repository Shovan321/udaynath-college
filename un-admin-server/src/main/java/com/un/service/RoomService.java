package com.un.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.un.dto.RoomDTO;
import com.un.entity.Department;
import com.un.entity.Room;
import com.un.mapper.RoomMapper;
import com.un.repo.RoomRepo;

@Service
public class RoomService {
	@Autowired
	private RoomRepo roomRepo;

	@Autowired
	private RoomMapper roomMapper;

	public void create(RoomDTO dto) {
		if (dto.getStart() != null && dto.getEnd() != null) {
			String deptName = dto.getDeptName();
			for (int i = dto.getStart(); i <= dto.getEnd(); i++) {
				dto.setDeptName(deptName + "-" + i);
				Room entity = roomMapper.toEntity(dto);
				roomRepo.save(entity);
			}
		} else {
			Room entity = roomMapper.toEntity(dto);
			roomRepo.save(entity);
		}
	}

	public List<RoomDTO> findAll() {
		Iterable<Room> findAll = roomRepo.findAll();
		List<RoomDTO> dtos = new ArrayList<>();
		findAll.forEach(a -> dtos.add(roomMapper.toDTO(a)));
		try {
			dtos.sort(Comparator.comparing(RoomDTO::getDeptId).thenComparing(RoomDTO::getName));
		} catch (Exception e) {
		}
		return dtos;
	}

	public List<RoomDTO> findAllByDepartmentIn(String selectedIds) {
		String trim = selectedIds.trim();
		List<String> asList = Arrays.asList(trim.split(","));
		List<Department> collect = asList.stream().map(m -> m.trim()).map(m -> Long.valueOf(m))
				.map(m -> new Department(m)).collect(Collectors.toList());

		List<RoomDTO> dtos = new ArrayList<>();
		List<Room> findAllByDepartmentIn = roomRepo.findAllByDepartmentIn(collect);

		findAllByDepartmentIn.forEach(a -> dtos.add(roomMapper.toDTO(a)));

		dtos.sort(Comparator.comparing(RoomDTO::getDeptName).thenComparing(RoomDTO::getName));

		return dtos;
	}

	public void delete(Long roomId) {
		roomRepo.deleteById(roomId);
	}

}
