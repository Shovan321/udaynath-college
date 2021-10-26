package com.un.dto;

import lombok.Data;

@Data
public class MemoDTO {
	private Long id;
	private String name;
	ReportDTO reportDTO;
}
