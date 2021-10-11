package com.un.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.un.entity.Department;

@Repository
public interface DepartmentRepo extends CrudRepository<Department, Long>{

}
