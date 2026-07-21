package com.vts.pfms.master.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="pfms_industry_partner")
public class IndustryPartner implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="industry_partner_id")
	private Long IndustryPartnerId;
	@Column(name="industry_name")
	private String IndustryName;
	@Column(name="industry_address")
	private String IndustryAddress;
	@Column(name="industry_city")
	private String IndustryCity;
	@Column(name="industry_pin_code")
	private String IndustryPinCode;
	@Column(name="created_by")
	private String CreatedBy;
	@Column(name="created_date")
	private String CreatedDate;
	@Column(name="modified_by")
	private String ModifiedBy;
	@Column(name="modified_date")
	private String ModifiedDate;
	@Column(name="is_active")
	private int IsActive;
	
	@JsonIgnore
	@OneToMany(mappedBy = "industryPartner", cascade = CascadeType.ALL)
	private List<IndustryPartnerRep> rep;
}
