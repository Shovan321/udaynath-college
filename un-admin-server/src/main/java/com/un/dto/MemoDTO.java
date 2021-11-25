package com.un.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MemoDTO {
	private Long id;
	private String name;
	private Date creaatedDate;
	ReportDTO reportDTO;
}
