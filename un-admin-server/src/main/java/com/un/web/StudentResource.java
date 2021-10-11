package com.un.web;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.un.dto.StudentResponseDTO;
import com.un.service.StudentService;

@RestController
@RequestMapping(value = "/api/student")
public class StudentResource {
	@Autowired
	private StudentService studentService;
	
	@PostMapping
	public StudentResponseDTO processStudent(@RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		return studentService.processStudentFile(workbook);
	}
}
