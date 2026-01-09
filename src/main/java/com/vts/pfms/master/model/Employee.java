package com.vts.pfms.master.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="employee")
public class Employee {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	
	@Column(name="emp_id")
	private Long empId;
	
	@Column(name="emp_no")
	private String empNo;
	
	@Column(name="title")
	private String title;
	
	@Column(name="salutation")
	private String salutation;
	
	@Column(name="emp_name")
	private String empName;
	
	@Column(name="desig_id")
	private Long desigId;
	
	@Column(name="ext_no")
	private String extNo;
	
	@Column(name="mobile_no")
	private Long mobileNo;
	
	@Column(name="drona_email")
	private String dronaEmail;
	
	@Column(name="internal_email")
	private String internetEmail;
	
	@Column(name="email")
	private String email;
	
	@Column(name="division_id")
	private Long divisionId;
	
	@Column(name="sr_no")
	private Long srNo;
	
	@Column(name="is_active")
	private int isActive;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="modified_date")
	private String modifiedDate;
	
	@Column(name="lab_code")
	private String labCode;
	
	@Column(name="superior_officer")
	private String superiorOfficer;
	
	@Column(name="emp_status")
	private String empStatus; //srikant
}
