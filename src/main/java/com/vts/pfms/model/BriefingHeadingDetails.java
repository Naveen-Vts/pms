package com.vts.pfms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "briefing_heading_details")
public class BriefingHeadingDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long DetailsId;
	private Long HeadingId;
	private Long ScheduleId;
	private String Details;
	private String CreatedBy;
	private LocalDateTime CreatedDate;
	private String ModifiedBy;
	private LocalDateTime ModifiedDate;
	private Integer IsActive;
	
	public Long getDetailsId() {
		return DetailsId;
	}
	public Long getHeadingId() {
		return HeadingId;
	}
	public Long getScheduleId() {
		return ScheduleId;
	}
	public String getDetails() {
		return Details;
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
	public void setDetailsId(Long detailsId) {
		DetailsId = detailsId;
	}
	public void setHeadingId(Long headingId) {
		HeadingId = headingId;
	}
	public void setScheduleId(Long scheduleId) {
		ScheduleId = scheduleId;
	}
	public void setDetails(String details) {
		Details = details;
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
	
	@Override
	public String toString() {
		return "BriefingHeadingDetails [DetailsId=" + DetailsId + ", HeadingId=" + HeadingId + ", ScheduleId="
				+ ScheduleId + ", Details=" + Details + ", CreatedBy=" + CreatedBy + ", CreatedDate=" + CreatedDate
				+ ", ModifiedBy=" + ModifiedBy + ", ModifiedDate=" + ModifiedDate + ", IsActive=" + IsActive + "]";
	}
	
	
}
