package com.vts.pfms.admin.model;

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
@Table(name="division_master")
public class DivisionMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="division_id")
	private Long divisionId;
	
	@Column(name="lab_code")
	private String labCode;
	
	@Column(name="division_code")
	private String divisionCode;
	
	@Column(name="division_name")
	private String divisionName;
	
	@Column(name="division_short_name")
	private String divisionShortName;
	
	@Column(name="division_head_id")
	private long divisionHeadId;
	
	@Column(name="group_id")
	private long groupId;
	
	@Column(name="is_active")
	private Integer isActive;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="modified_date")
	private String modifiedDate;
	
}
