package com.vts.pfms.milestone.dto;

import java.time.LocalDateTime;

public class ProjectManPowerUtilizationDto {
	 
		private Long utilizationId;
	    private String financialYear;
	    private String projectId;
	    private String quarter;
	    private String manPowerCount;
	    private String desigCadre;
	    private String revisionNo;
	    private String createdBy;
	    private LocalDateTime createdDate;
	    private String modifiedBy;
	    private LocalDateTime modifiedDate;
	    private Integer isActive;
	    private String scientistCount;
	    private String technicalCount;
	    private String adminCount;
	    private String sciDaysCount;
	    private String techDaysCount;
	    private String adminDaysCount;
	    private String action;
	    
		public Long getUtilizationId() {
			return utilizationId;
		}
		public void setUtilizationId(Long utilizationId) {
			this.utilizationId = utilizationId;
		}
		public String getFinancialYear() {
			return financialYear;
		}
		public void setFinancialYear(String financialYear) {
			this.financialYear = financialYear;
		}
		public String getProjectId() {
			return projectId;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		public String getQuarter() {
			return quarter;
		}
		public void setQuarter(String quarter) {
			this.quarter = quarter;
		}
		public String getManPowerCount() {
			return manPowerCount;
		}
		public void setManPowerCount(String manPowerCount) {
			this.manPowerCount = manPowerCount;
		}
		public String getDesigCadre() {
			return desigCadre;
		}
		public void setDesigCadre(String desigCadre) {
			this.desigCadre = desigCadre;
		}
		public String getRevisionNo() {
			return revisionNo;
		}
		public void setRevisionNo(String revisionNo) {
			this.revisionNo = revisionNo;
		}
		public String getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		public LocalDateTime getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(LocalDateTime createdDate) {
			this.createdDate = createdDate;
		}
		public String getModifiedBy() {
			return modifiedBy;
		}
		public void setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
		}
		public LocalDateTime getModifiedDate() {
			return modifiedDate;
		}
		public void setModifiedDate(LocalDateTime modifiedDate) {
			this.modifiedDate = modifiedDate;
		}
		public Integer getIsActive() {
			return isActive;
		}
		public void setIsActive(Integer isActive) {
			this.isActive = isActive;
		}
		public String getScientistCount() {
			return scientistCount;
		}
		public void setScientistCount(String scientistCount) {
			this.scientistCount = scientistCount;
		}
		public String getTechnicalCount() {
			return technicalCount;
		}
		public void setTechnicalCount(String technicalCount) {
			this.technicalCount = technicalCount;
		}
		public String getAdminCount() {
			return adminCount;
		}
		public void setAdminCount(String adminCount) {
			this.adminCount = adminCount;
		}
		public String getSciDaysCount() {
			return sciDaysCount;
		}
		public void setSciDaysCount(String sciDaysCount) {
			this.sciDaysCount = sciDaysCount;
		}
		public String getTechDaysCount() {
			return techDaysCount;
		}
		public void setTechDaysCount(String techDaysCount) {
			this.techDaysCount = techDaysCount;
		}
		public String getAdminDaysCount() {
			return adminDaysCount;
		}
		public void setAdminDaysCount(String adminDaysCount) {
			this.adminDaysCount = adminDaysCount;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
	    


}
