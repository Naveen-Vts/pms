package com.vts.pfms.milestone.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="milestone_activity_level")
public class MilestoneActivityLevel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ActivityId;
	private Long ParentActivityId;
	private Long ActivityLevelId;
	private String ActivityName;
	private Long ActivityType;
	private Date OrgStartDate;
	private Date OrgEndDate;
	private Date StartDate;
	private Date EndDate;
	private Long OicEmpId;
	private Long OicEmpId1;
	private Long Revision;
	private int ProgressStatus;	
	private int ActivityStatusId;
	private String StatusRemarks;
	private int Weightage;
	private int Loading;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private int IsActive;
	private String point5;
	private String point6;
	private String point9;
	private Long LinkedMilestonId;
	private String IsMasterData;
	private String IsSunSet;
	
	
    
}
