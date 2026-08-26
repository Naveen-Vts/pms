package com.vts.pfms.committee.dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CommitteeInvitationDto {

	private String committeeInvitationId;
	private String committeeScheduleId;
	private ArrayList<String> empIdList;
	private String attendance;
	private String createdBy;
	private String createdDate;
	private String modifiedBy;
	private String modifiedDate;
	private ArrayList<String> labCodeList;
	private ArrayList<String> desigids;
	private String reptype;
	private String inviteFlag;
	
	private String isOnlineAttendance;
	private Long revisionNo;
	private String parentInvitationId;
	
	private String empId;
	private String invitationId;
	private String designationId;
	private String empLabCode;
	private String repCode;
	
}
