package com.vts.pfms.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "employee_desig")
public class EmployeeDesig {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="desig_id")
	private long desigId;
	@Column(name="desig_code")
	private String desigCode;
	@Column(name="designation")
	private String designation;
	@Column(name="desig_limit")
	private long desigLimit;
	@Column(name="desig_cadre")
	private String desigCadre;
	@Column(name="desig_sr")
	private int desigSr;
	
	
}
