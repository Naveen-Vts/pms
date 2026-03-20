package com.vts.pfms.milestone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="file_rep_upload")
public class FileRepUploadNew {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long FileRepUploadId;
		private Long FileRepId;
		private String FileNameUi;
		private String FileName;
		private String FilePath;
		private String FilePass;
		private Long VersionDoc;
		private Long ReleaseDoc;
		private String Description;
		private String CreatedBy;
		private String CreatedDate;
		private int IsActive;
		private String ReportType;
		public Long getFileRepUploadId() {
			return FileRepUploadId;
		}
		public void setFileRepUploadId(Long fileRepositoryUploadId) {
			FileRepUploadId = fileRepositoryUploadId;
		}
		public Long getFileRepId() {
			return FileRepId;
		}
		public void setFileRepId(Long fileRepositoryId) {
			FileRepId = fileRepositoryId;
		}
		
		public String getFileNameUi() {
			return FileNameUi;
		}
		public void setFileNameUi(String fileNameUi) {
			FileNameUi = fileNameUi;
		}
		public String getFileName() {
			return FileName;
		}
		public void setFileName(String fileName) {
			FileName = fileName;
		}
		public String getFilePath() {
			return FilePath;
		}
		public void setFilePath(String filePath) {
			FilePath = filePath;
		}
		public String getFilePass() {
			return FilePass;
		}
		public void setFilePass(String filePass) {
			FilePass = filePass;
		}
		
		public String getCreatedBy() {
			return CreatedBy;
		}
		public void setCreatedBy(String createdBy) {
			CreatedBy = createdBy;
		}
		public String getCreatedDate() {
			return CreatedDate;
		}
		public void setCreatedDate(String createdDate) {
			CreatedDate = createdDate;
		}
		public int getIsActive() {
			return IsActive;
		}
		public void setIsActive(int isActive) {
			IsActive = isActive;
		}
		public Long getVersionDoc() {
			return VersionDoc;
		}
		public void setVersionDoc(Long versionDoc) {
			VersionDoc = versionDoc;
		}
		public Long getReleaseDoc() {
			return ReleaseDoc;
		}
		public void setReleaseDoc(Long releaseDoc) {
			ReleaseDoc = releaseDoc;
		}
		public String getDescription() {
			return Description;
		}
		public void setDescription(String description) {
			Description = description;
		}
		public String getReportType() {
			return ReportType;
		}
		public void setReportType(String reportType) {
			ReportType=reportType;
		}
		
}
