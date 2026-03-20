package com.vts.pfms.milestone.dto;

import java.io.InputStream;

import lombok.Data;


@Data
public class FileUploadDto {
	private String UserId;
	private String PathName;
	private String ProjectId;
	private String InitiationId;
	private String FileId;
	private String FileName;
    private String FileNamePath;
    private String Rel;
    private String Ver;
	private InputStream IS;
	private String Description;
	private String SubL1;
	private String Docid;
	private String LabCode;
	private String FileRepMasterId;
	private String AgendaId;
	private String DocumentName;
	private String isNewVersion;
	private String fileType;
	private String ReportType;
  
	
	
}
