package com.un.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.un.entity.MemoDetails;

@Repository
public interface MemoRepo extends CrudRepository<MemoDetails, Long>{
	List<MemoDetails> findByName(String name);
}
