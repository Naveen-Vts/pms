package com.vts.pfms.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProjectUtilizationBriefingDto {

	private Long resourceUtilizationId;
	private Long projectId;
	private String financialYear;
	private String quarter;
	
	private Long firstQuarter;
	private Long secondQuarter;
	private Long thirdQuarter;
	private Long fourthQuarter;
	
	private Long cummulativePastYears;
	private Long cummulativeTillDate;
	
	private String nameOfTraining;
	private String nameOfInfrastructure;
		
	private String desigCrade;
	
	
}
