package com.vts.pfms.pfts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PmmgPmsDmdDetails {

	private String demandNo;
	private String demandDate;
	private String projectCode;
	private String itemName;
	private String soNo;
	private String soDate;
	private String dpDate;
	private String firmName;
	private String procurementStage;
	
}
