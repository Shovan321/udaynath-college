package com.un.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.un.dto.Student;
import com.un.dto.StudentResponseDTO;

@Service
public class StudentService {

	public StudentResponseDTO processStudentFile(XSSFWorkbook workbook) {
		StudentResponseDTO dto = new StudentResponseDTO();
		List<List<Student>> studentsList = new LinkedList<>();
		List<String> studentNameList = new LinkedList<>();
		for (Sheet sheet : workbook) {
			List<Student> students = new LinkedList<>();
			for (Row row : sheet) {
				Cell roolNumberCell = row.getCell(0);
				String rollNumber = getValue(roolNumberCell);
				if(rollNumber != null && rollNumber.isEmpty()) {
					continue;
				}
				Cell nameCell = row.getCell(1);
				String name = getValue(nameCell);
				Student s = new Student();
				s.setName(name);
				s.setRoolNumber(rollNumber);
				students.add(s);
			}
			studentsList.add(students);
			studentNameList.add(sheet.getSheetName().trim());
		}
		dto.setStudentNameList(studentNameList);
		dto.setStudentsList(studentsList);
		return dto;
	}

	public String getValue(Cell cell) {
		String data = "";
		try {
			return cell.getBooleanCellValue()+"";
		} catch (Exception e) {
		}
		try {
			return cell.getStringCellValue();
		} catch (Exception e) {
		}
		try {
			return Double.valueOf(cell.getNumericCellValue()).longValue()+"";
		} catch (Exception e) {
		}
		return data;
	}
}
