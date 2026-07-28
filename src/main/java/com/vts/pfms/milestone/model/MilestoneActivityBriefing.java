package com.vts.pfms.milestone.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "milestone_activity_briefing")
@Data
public class MilestoneActivityBriefing {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long milestoneActivityBriefingId;
	private String points;
	private Long briefingPointId;
	private Long scheduleId;
	private LocalDate createdDate;
	private String createdBy;
	private String modifiedBy;
	private LocalDate modifiedDate;
	private Integer isActive;

}
