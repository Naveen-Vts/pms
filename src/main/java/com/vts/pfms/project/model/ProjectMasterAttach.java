package com.vts.pfms.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_master_attach")
public class ProjectMasterAttach {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="project_attach_id")
	private Long ProjectAttachId;
	
	@Column(name="project_id")
	private Long ProjectId;
	
	@Column(name="path")
	private String Path;
	
	@Column(name="file_name")
	private String FileName;
	
	@Column(name="original_file_name")
	private String OriginalFileName;
	
	@Column(name="created_by")
	private String CreatedBy;
	
	@Column(name="created_date")
	private String CreatedDate;  
	
}
