package com.un.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AlertRoom implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String studentCount;
	private List<InvesilotersDTO> invesiloters = new ArrayList<>();
}
