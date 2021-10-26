package com.un.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Data;

@Entity
@Data
public class MemoDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String name;
	
	@Column(columnDefinition="TEXT")
	private String memoDetails;
	
	@Column
	private Date createdDate;
	
	@Column
	private Date updatedDate;
	
	@PrePersist
	public void prePersist() {
		Date creadedDate = new Date();
		setCreatedDate(creadedDate);
		setUpdatedDate(creadedDate);
	}
	
	@PreUpdate
	public void preUpdate() {
		setUpdatedDate(new Date());
	}
}
