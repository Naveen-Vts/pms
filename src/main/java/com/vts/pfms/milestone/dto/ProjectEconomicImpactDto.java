package com.vts.pfms.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEconomicImpactDto {

	private String economicImpactId;
    private String projectId;
    private String indigenousContentAndIndigenization;
    private String internationalCollaborationsExecuted;
    private String intellectualPropertyRights;
    private String exportPotential;
    private String infrastructureCreated;
    private String revisionNo;
    private String createdBy;
    private String createdDate;
    private String modifiedBy;
    private String modifiedDate;
    private String isActive;

    private String status;
}