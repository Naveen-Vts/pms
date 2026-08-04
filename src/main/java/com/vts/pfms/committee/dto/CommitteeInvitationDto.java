package com.vts.pfms.committee.dto;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CommitteeInvitationDto {



	private String CommitteeInvitationId;
	private String CommitteeScheduleId;
	private ArrayList<String> EmpIdList;
	private String Attendance;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private ArrayList<String> LabCodeList;
	private ArrayList<String> desigids;
	private String reptype;
	private String inviteFlag;
	
	private String isOnlineAttendance;
	private Long RevisionNo;
	private Long ParentInvitationId;
	
}
