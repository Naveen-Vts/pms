package com.vts.pfms.project.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name= "project_master_rev")
public class ProjectMasterRev 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="project_rev_id")
    private Long ProjectRevId;
	@Column(name="project_id")
	private Long ProjectId;
	@Column(name="revision_no")
	private Long RevisionNo;
	@Column(name="project_main_id")
	private Long ProjectMainId;
	@Column(name="project_code")
	private String ProjectCode;
	@Column(name="project_imms_cd")
	private String ProjectImmsCd;
	@Column(name="project_name")
	private String ProjectName;
	@Column(name="project_description")
    private String ProjectDescription;
	@Column(name="unit_code")
    private String UnitCode;
	@Column(name="project_type")
    private Long projectType;
	@Column(name="project_category")
    private Long ProjectCategory;
	@Column(name="sanction_no")
    private String sanctionNo;
	@Column(name="sanction_date")
    private Date sanctionDate;
	@Column(name="total_sanction_cost")
	private Double TotalSanctionCost;
	@Column(name="sanction_cost_re")
    private Double SanctionCostRE;
	@Column(name="sanction_cost_fe")
    private Double SanctionCostFE;
	@Column(name="pdc")
	private Date PDC;
	@Column(name="project_director")
	private Long projectDirector;
	@Column(name="proj_sanc_authority")
    private String projSancAuthority;
	@Column(name="board_reference")
    private String boardReference;
	@Column(name="is_main_wc")
    private int isMainWC;
	@Column(name="work_center")
    private String WorkCenter;
	@Column(name="scope")
    private String Scope;
	@Column(name="application")
    private String Application;
	@Column(name="lab_participating")
    private String labParticipating;
	@Column(name="objective")
    private String objective;
	@Column(name="deliverable")
    private String deliverable;
	@Column(name="remarks")
    private String remarks;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="created_date")
    private String createdDate;
	@Column(name="platform_id")
    private Long platformId; //srikant
	@Column(name="platform")
    private String platform; //srikant
}
