package com.vts.pfms.committee.model;


import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="pfms_Favourite_List")
public class FavouriteList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long FavouriteId;
	private Long EmpId;
	private Long ActionAssignId;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private int IsActive;
}
