package com.un.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.RoomDTO;
import com.un.dto.UNResponse;
import com.un.service.RoomService;

@RestController
@RequestMapping(value = "/api/room")
public class RoomResource {
	
	@Autowired
	RoomService service;
	
	@PostMapping
	public void create(@RequestBody RoomDTO dto) {
		service.create(dto);
	}
	
	@GetMapping
	public UNResponse<RoomDTO> findAll(){
		List<RoomDTO> rooms = service.findAll();
		UNResponse<RoomDTO> res = new UNResponse<>();
		res.setData(rooms);
		return res;
	}
	@GetMapping(value = "/{selectedIds}")
	public UNResponse<RoomDTO> findAllByDepartmentIds(@PathVariable String selectedIds){
		List<RoomDTO> rooms = service.findAllByDepartmentIn(selectedIds);
		UNResponse<RoomDTO> res = new UNResponse<>();
		res.setData(rooms);
		return res;
	}
	@DeleteMapping(value = "/{roomId}")
	public void delete(@PathVariable Long roomId) {
		service.delete(roomId);
	}
}
