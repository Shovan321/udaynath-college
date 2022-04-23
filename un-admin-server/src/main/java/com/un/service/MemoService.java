package com.un.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.un.dto.MemoDTO;
import com.un.dto.ReportDTO;
import com.un.dto.ReportRoomDTO;
import com.un.entity.MemoDetails;
import com.un.repo.MemoRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemoService {

	@Autowired
	private MemoRepo memoRepo;

	@Autowired
	private ObjectMapper objectMapper;

	public MemoDetails save(ReportDTO reportDTO) {
		try {
			String writeValueAsString = objectMapper.writeValueAsString(reportDTO);
			MemoDetails details = new MemoDetails();
			details.setName(reportDTO.getTitle());
			details.setMemoDetails(writeValueAsString);
			return memoRepo.save(details);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public List<MemoDTO> getMemoNameList() {

		List<MemoDTO> dtos = new ArrayList<>();
		memoRepo.findAll().forEach(m -> {
			MemoDTO memoDTO = new MemoDTO();
			memoDTO.setId(m.getId());
			memoDTO.setName(m.getName());
			memoDTO.setCreaatedDate(m.getCreatedDate());
			try {
				ReportDTO reportDTO = objectMapper.readValue(m.getMemoDetails(), ReportDTO.class);
				reportDTO = filterReportDTO(reportDTO);
				memoDTO.setReportDTO(reportDTO);
				if(!reportDTO.getSelectedRooms().isEmpty()) {
					dtos.add(memoDTO);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
		try {
			dtos.sort(Comparator.comparing(MemoDTO::getCreaatedDate).reversed());
		} catch (Exception e) {
		}
		return dtos;
	}

	private ReportDTO filterReportDTO(ReportDTO reportDTO) {
		List<ReportRoomDTO> selectedRooms = reportDTO.getSelectedRooms();
		for (ReportRoomDTO reportRoomDTO : selectedRooms) {
			List<List<String>> rollNumberList = reportRoomDTO.getRollNumberList();
			List<List<String>> newRollNumberList = new ArrayList<>();
			if(rollNumberList == null) {
				continue;
			}
			for (List<String> rollNumbers : rollNumberList) {
				List<String> newRollnumbers = rollNumbers.stream()
					.filter(p -> p != null && !p.isEmpty())
					.collect(Collectors.toList());
				if(!newRollnumbers.isEmpty()) {
					newRollNumberList.add(newRollnumbers);					
				}
			}
			reportRoomDTO.setRollNumberList(newRollNumberList);
		}
		return reportDTO;
	}

	public void delete(String examName) {
		List<MemoDetails> findByName = memoRepo.findByName(examName);
		for (MemoDetails memoDetails : findByName) {
			memoRepo.delete(memoDetails);			
		}
	}
}
