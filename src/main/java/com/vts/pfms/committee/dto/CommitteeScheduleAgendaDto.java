package com.vts.pfms.committee.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommitteeScheduleAgendaDto {

	private String ScheduleAgendaId;
	private String ScheduleId;
	private String ScheduleSubId;
	private String AgendaItem;
	private String PresentorLabCode;
	private String PresenterId;
	private String Duration;
	private String ProjectId;
	private String Remarks;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private String IsActive;
//	private byte[] AgendaAttachment;
//	private String DocId;
	private String[] DocLinkIds;
	private String groupName;
	
	
	
	
}
