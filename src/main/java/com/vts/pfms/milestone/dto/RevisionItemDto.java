package com.vts.pfms.milestone.dto;

import lombok.Data;

@Data
public class RevisionItemDto {

	private String mainId;
	private String resourceUtilizationId;
	private String itemName;
	private String itemValue;
	private String revisionNo;
	private String itemDays;
	private String itemTotal; 
}