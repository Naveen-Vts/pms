package com.vts.pfms.master.model;



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
@Table(name="division_group")

public class DivisionGroup {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long GroupId;
	@Column(name = "group_code")
	private String GroupCode;
	
	@Column(name = "group_name")
	private String GroupName;
	
	@Column(name = "group_head_id")
	private Long GroupHeadId;
	
	@Column(name = "created_by")
	private String CreatedBy;
	
	@Column(name = "created_date")
	private String CreatedDate;
	
	@Column(name = "modified_by")
	private String ModifiedBy;
	
	@Column(name = "modified_date")
	private String ModifiedDate;
	
	@Column(name = "is_active")
	private int IsActive;
	
	@Column(name = "lab_code")
	private String LabCode;
	
	@Column(name = "td_id")
	private String TDId;
	
}