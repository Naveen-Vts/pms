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
@Table(name="milestone_activity")
public class MilestoneActivity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long MilestoneActivityId;
	private Long ProjectId;
	private int MilestoneNo;
	private Long ActivityType;
	private String ActivityName;
	private Date OrgStartDate;
	private Date OrgEndDate;
	private Date StartDate;
	private Date EndDate;
	private Long OicEmpId;
	private Long OicEmpId1;
	private int ProgressStatus;
	private int Weightage;
	private int ActivityStatusId;
	private String StatusRemarks;
	private int RevisionNo;
	private int Loading;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private int IsActive;
	private String DateOfCompletion;
	private String IsSunSet;
	
	
}
