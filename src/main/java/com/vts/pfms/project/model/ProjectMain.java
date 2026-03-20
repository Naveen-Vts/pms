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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_main")
public class ProjectMain implements Serializable{


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="project_main_id")
    private Long projectMainId;
	
	@Column(name="project_type_id")
	private Long projectTypeId;
	
	@Column(name="category_id")
	private Long categoryId;
	
	@Column(name="project_code")
	private String projectCode;
	
	@Column(name="project_name")
    private String projectName;
	
	@Column(name="project_description")
    private String projectDescription;
	
	@Column(name="project_short_name")
    private String projectShortName;
	
	
	@Column(name="unit_code")
    private String unitCode;
	
	
	@Column(name="sanction_no")
    private String sanctionNo;
	
	
	@Column(name="sanction_date")
    private Date sanctionDate;
	
	
	@Column(name="total_sanction_cost")
    private Double totalSanctionCost;
	
	@Column(name="sanction_cost_re")
    private Double sanctionCostRE;
	
	@Column(name="sanction_cost_fe")
    private Double sanctionCostFE;
	
	
	@Column(name="pdc")
	private Date PDC;
	
	
	@Column(name="project_director")
    private Long projectDirector;
	
	
	@Column(name="proj_sanc_authority")
    private String projSancAuthority;
	
	
	@Column(name="board_reference")
    private String boardReference;
	
	
	@Column(name="revision_no")
    private Long revisionNo;
	
	
	@Column(name="work_center")
    private String workCenter;
	
	
	@Column(name="end_user")
    private String endUser;
	
	@Column(name="objective")
    private String objective;
	
	
	@Column(name="deliverable")
    private String deliverable;
	
	
	@Column(name="lab_participating")
    private String labParticipating;
	
	@Column(name="application")
    private String application;
	
	
	@Column(name="scope")
    private String scope;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
    private String createdDate;
	
	@Column(name="modified_by")
    private String modifiedBy;
	
	@Column(name="modified_date")
    private String modifiedDate;
	
	@Column(name="is_active")
    private int isActive;
	
	@Column(name="is_main_wc")
    private int isMainWC;
	
	@Column(name="platform_id")
    private Long platformId; //srikant
}
