package com.vts.pfms.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="lab_master")
public class LabMaster implements Serializable {

	
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="lab_master_id")
	private int LabMasterId;
	@Column(name="lab_code")
	private String LabCode;
	@Column(name="lab_name")
	private String LabName;
	@Column(name="lab_unit_code")
	private String LabUnitCode;
	@Column(name="lab_address")
	private String LabAddress;
	@Column(name="lab_city")
	private String LabCity;
	@Column(name="lab_pin")
	private String LabPin;
	@Column(name="lab_tel_no")
	private String LabTelNo;
	@Column(name="lab_fax_no")
	private String LabFaxNo;
	@Column(name="lab_email")
	private String LabEmail;
	@Column(name="lab_authority")
	private String LabAuthority;
	@Column(name="lab_authority_id")
	private Long LabAuthorityId;
	@Column(name="lab_rfp_email")
	private String LabRfpEmail;
	@Column(name="lab_id")
	private Long LabId;
	@Column(name="cluster_id")
	private Long ClusterId;
	@Column(name="lab_uri")
	private String LabURI;
	@Column(name="lab_logo")
	private byte[] LabLogo;
	@Column(name="created_by")
	private String CreatedBy;
	@Column(name="created_date")
	private String CreatedDate;
	@Column(name="modified_by")
	private String ModifiedBy;
	@Column(name="modified_date")
	private String ModifiedDate;
	
}
