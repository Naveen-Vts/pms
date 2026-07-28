package com.vts.pfms.master.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="pfms_industry_partner_rep")
public class IndustryPartnerRep  implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "industry_partner_rep_id")
	private Long IndustryPartnerRepId;
	@Column(name = "rep_name")
	private String RepName;
	@Column(name = "rep_designation")
	private String RepDesignation;
	@Column(name = "rep_mobile_no")
	private String RepMobileNo;
	@Column(name = "rep_email")
	private String RepEmail;
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
	
	@ManyToOne
	@JoinColumn(name = "industry_partner_id")
	private IndustryPartner industryPartner;
}
