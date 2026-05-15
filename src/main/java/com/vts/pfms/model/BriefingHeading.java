package com.vts.pfms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "briefing_heading")
public class BriefingHeading {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long HeadingId;
	private Long ProjectId;
	private Integer Seniority;
	private String Heading;
	private String CreatedBy;
	private LocalDateTime CreatedDate;
	private String ModifiedBy;
	private LocalDateTime ModifiedDate;
	private Integer IsActive;
	
	
	public Long getHeadingId() {
		return HeadingId;
	}
	public Long getProjectId() {
		return ProjectId;
	}
	public String getHeading() {
		return Heading;
	}
	public String getCreatedBy() {
		return CreatedBy;
	}
	public LocalDateTime getCreatedDate() {
		return CreatedDate;
	}
	public String getModifiedBy() {
		return ModifiedBy;
	}
	public LocalDateTime getModifiedDate() {
		return ModifiedDate;
	}
	public Integer getIsActive() {
		return IsActive;
	}
	public void setHeadingId(Long headingId) {
		HeadingId = headingId;
	}
	public void setProjectId(Long projectId) {
		ProjectId = projectId;
	}
	public void setHeading(String heading) {
		Heading = heading;
	}
	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		CreatedDate = createdDate;
	}
	public void setModifiedBy(String modifiedBy) {
		ModifiedBy = modifiedBy;
	}
	public void setModifiedDate(LocalDateTime modifiedDate) {
		ModifiedDate = modifiedDate;
	}
	public void setIsActive(Integer isActive) {
		IsActive = isActive;
	}
	public Integer getSeniority() {
		return Seniority;
	}
	public void setSeniority(Integer seniority) {
		Seniority = seniority;
	}

	
	
}
