package com.un.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.un.dto.MemoDTO;
import com.un.dto.MemoReportDTO;
import com.un.dto.MemoRollNumber;
import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.service.MemoService;

@RestController
@RequestMapping(value = "api/memo")
public class MemoResource {
	@Autowired
	private MemoService memoService;

	@GetMapping
	public MemoReportDTO getAllMemoList() {
		List<MemoDTO> memoNameList = memoService.getMemoNameList();
		for (MemoDTO memoDTO : memoNameList) {
			ReportDTO reportDTO = memoDTO.getReportDTO();
			List<ReportRoomDTO> selectedRooms = reportDTO.getSelectedRooms();
			for (ReportRoomDTO roomDTO : selectedRooms) {
				List<List<String>> rollNumberList = roomDTO.getRollNumberList();
				List<List<MemoRollNumber>> memoRollNumbersList = new ArrayList<>();
				for (List<String> rollNumbers : rollNumberList) {
					List<MemoRollNumber> memoRollNumbers = new ArrayList<>();

					for (String roll : rollNumbers) {
						MemoRollNumber memoRollNumber = new MemoRollNumber();
						memoRollNumber.setRollMumber(roll);
						memoRollNumber.setStudentPresent(true);
						memoRollNumbers.add(memoRollNumber);
					}
					memoRollNumbersList.add(memoRollNumbers);
				}
				roomDTO.setMemoRollNumberList(memoRollNumbersList);
			}
		}
		MemoReportDTO reportDTO = new MemoReportDTO();
		reportDTO.setMemoModels(memoNameList);
		return reportDTO;
	}

	@PostMapping(produces = "application/zip")
	public void downloadmemoDetails(@RequestBody MemoDTO memoDTO) {
		ReportDTO reportDTO = memoDTO.getReportDTO();
		List<ReportRoomDTO> selectedRooms = reportDTO.getSelectedRooms();
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			List<List<MemoRollNumber>> memoRollNumberList = reportRoomDTO.getMemoRollNumberList();
			if (memoRollNumberList != null && !memoRollNumberList.isEmpty()) {
				for (List<MemoRollNumber> memoRollNumbers : memoRollNumberList) {
					if (memoRollNumbers != null) {
						/*
						 * for(MemoRollNumber memoRollNumber : memoRollNumbers) {
						 * 
						 * }
						 */
						long presentCount = memoRollNumbers.stream().filter(p -> p.isStudentPresent()).count();
						long absentCount = memoRollNumbers.stream().filter(p -> !p.isStudentPresent()).count();
						System.out.println(reportRoomDTO.getName() + " P: " + presentCount + " ,A: " + absentCount);
					}
				}
			}
		}
	}
}
