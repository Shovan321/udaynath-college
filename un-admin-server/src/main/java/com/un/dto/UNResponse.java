package com.un.dto;

import java.util.List;

import lombok.Data;

@Data
public class UNResponse<T> {
	List<T> data;
	int pageNumber;
	int totalSize;
	int pageCount;
}
