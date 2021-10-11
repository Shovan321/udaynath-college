package com.un.dto;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class StudentResponseDTO {
	List<List<Student>> studentsList = new LinkedList<>();
	List<String> studentNameList = new LinkedList<>();
}
