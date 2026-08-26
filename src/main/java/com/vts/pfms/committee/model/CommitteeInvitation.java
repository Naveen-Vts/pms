package com.vts.pfms.committee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Entity
@Table(name= "committee_schedules_invitation")
public class CommitteeInvitation 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long CommitteeInvitationId;

	private long CommitteeScheduleId;
	private long EmpId;
	private String MemberType;
	private String Attendance;
	private String DesigId;
	private String LabCode;
	private long SerialNo;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private String EmpMeetingRole;
	
	//DLRL changes 
	private String isOnlineAttendence;	
	private Long RevisionNo;
	private Long ParentInvitationId;
	private Long IsActive;
}
