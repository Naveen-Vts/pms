package com.vts.pfms.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_health")
public class ProjectHealth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ProjectHealthId;
	private String LabCode;
	private long ProjectId;
	private String ProjectShortName;
	private String ProjectCode;
	private String PDC;
	private String SanctionDate;
	private Long PMRCHeld;
	private Long PMRCPending;
	private Long PMRCTotal;
	private long PMRCTotalToBeHeld;
	private Long EBHeld;
	private Long EBPending;
	private Long EBTotal;
	private long EBTotalToBeHeld;
	private Long MilPending;
	private Long MilDelayed;
	private Long MilCompleted;
	private Long ActionPending;
	private Long ActionForwarded;
	private Long ActionDelayed;
	private Long ActionCompleted;
	private Long RiskCompleted;
	private Long RiskPending;
	private Double Expenditure;
	private Double OutCommitment;
	private Double Dipl;
	private Double Balance;
	private String ProjectType;
	private String EndUser;
	private Long TodayChanges;
	private Long WeeklyChanges;
	private Long MonthlyChanges;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;

	//Newly Added		
	private Long PJBHeld;
	private Long PJBPending;
	private Long PJBTotal;

	private Long PMBHeld;
	private Long PMBPending;
	private Long PMBTotal;

	private Long ABHeld;
	private Long ABPending;
	private Long ABTotal;

	private Long PJBTotalToBeHeld;
	private Long PMBTotalToBeHeld;
	private Long ABTotalToBeHeld;

	//DLRL
	private Long MilToBeDone;
}
