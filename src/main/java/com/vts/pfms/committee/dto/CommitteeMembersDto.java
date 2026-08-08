package com.vts.pfms.committee.dto;

import lombok.Data;

@Data
public class CommitteeMembersDto {

	
	private String CommitteeMainId;
	private String[] InternalMemberIds;
	private String InternalLabCode;
	private String[] ExternalMemberIds;
	private String ExternalLabCode;
	private String[] ExpertMemberIds;
	private String CreatedBy;
	// Prudhvi 27/03/2024
	/* ------------------ start ----------------------- */
	private String[] IndustrialPartnerRepIds;
	private String IndustrialPartnerLabCode;
	/* ------------------ end ----------------------- */
	
	private String[] RepIds;
	
	
	
}
