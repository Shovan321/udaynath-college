package com.un.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Room {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long id;
	
	@Column
	public String name;
	
	@Column
	public int noOfRow;
	
	@Column
	public int rowCapacity;
	
	@ManyToOne
	@JoinColumn(name = "dept_id")
	public Department department;
}
