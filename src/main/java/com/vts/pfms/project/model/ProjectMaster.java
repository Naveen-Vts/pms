package com.vts.pfms.project.model;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "project_master")
public class ProjectMaster implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="project_id")
    private Long ProjectId;
	
	@Column(name="project_main_id")
	private Long ProjectMainId;
	
	@Column(name="project_code")
	private String ProjectCode;
	
	@Column(name="project_short_name")
	private String ProjectShortName;
	
	@Column(name="project_imms_cd")
	private String ProjectImmsCd;
	
	@Column(name="project_name")
    private String ProjectName;
	
	
	@Column(name="project_description")
    private String ProjectDescription;
	
	
	@Column(name="unit_code")
    private String UnitCode;
	
	
	@Column(name="project_type")
    private Long ProjectType;
	
	@Column(name="project_type_id")
    private Long ProjectTypeId;
	
	@Column(name="sanction_no")
    private String SanctionNo;
	
	@Column(name="sanction_date")
    private Date SanctionDate;
	
	@Column(name="total_sanction_cost")
	private Double TotalSanctionCost;
	@Column(name="sanction_cost_re")
    private Double SanctionCostRE;
	@Column(name="sanction_cost_fe")
    private Double SanctionCostFE;
	@Column(name="pdc")
	private Date PDC;
	@Column(name="project_director")
	private Long ProjectDirector;
	@Column(name="project_category")
    private Long ProjectCategory;
	@Column(name="proj_sanc_authority")
    private String ProjSancAuthority;
	@Column(name="board_reference")
    private String BoardReference;
	@Column(name="revision_no")
    private Long RevisionNo;
	@Column(name="work_center")
    private String WorkCenter;
	@Column(name="lab_participating")
    private String LabParticipating;
	@Column(name="objective")
    private String Objective;
	@Column(name="deliverable")
    private String Deliverable;
	@Column(name="scope")
    private String Scope;
	@Column(name="application")
    private String Application;
	@Column(name="end_user")
    private String EndUser;
	@Column(name="created_by")
	private String CreatedBy;
	@Column(name="created_date")
    private String CreatedDate;
	@Column(name="modified_by")
    private String ModifiedBy;
	@Column(name="modified_date")
    private String ModifiedDate;
	@Column(name="is_active")
    private int isActive;
	@Column(name="is_main_wc")
    private int IsMainWC;
	@Column(name="lab_code")
    private String LabCode;
	@Column(name="is_ccs")
    private String IsCCS;
	@Column(name="platform_id")
    private Long PlatformId; //srikant
	@Column(name="platform")
    private String Platform; //srikant 
}
