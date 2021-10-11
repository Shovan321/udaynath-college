package com.un.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.un.entity.Department;
import com.un.entity.Room;

@Repository
public interface RoomRepo extends CrudRepository<Room, Long>{
	List<Room> findAllByDepartmentIn(List<Department> collect);
}
