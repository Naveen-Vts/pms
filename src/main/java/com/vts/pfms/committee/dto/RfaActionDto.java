package com.vts.pfms.committee.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RfaActionDto 
{
	private Long RfaId;
	private String LabCode;
	private String projectType;
	private Long ProjectId;
	private String RfaNo;
	private String ProjectCode;
	private String RfaTypeId;
	private Date RfaDate;
	private Long PriorityId;
	private Long CategoryId;
	private String Statement;
	private String Description;
	private String Reference;
	private Long AssigneeId;
	private String AssignorId;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private String AssignorAttachment;
	private String AssigneAttachment;
	private MultipartFile multipartfile;
	private Long RfaCCId;
	private Long CCEmpId;
	private Long ActionBy;
	private int IsActive;
	private String TypeOfRfa;
	private String VendorCode;
	private String rfastatus;
	private String labcode;
	
	
	private String BoxNo;
	
	private Date SWdate;
	
	private String FPGA;
	
	private String RigVersion;
	private String RfaRaisedByName;
	//29-04-2025
	
}
