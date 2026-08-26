package com.vts.pfms.master.dto;

public class ProjectSanctionDetailsMaster {
	
	private int sno;
	private String projectCode;
	private String projectName;
	//private String budgetHeadDescription;
	private Double sancAmt;
	private Double expAmt;
	private Double osComAmt;
	private Double dipl;
	private Double balAmt;
	private int projectId;
	private String asOnDate;
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
//	public String getBudgetHeadDescription() {
//		return budgetHeadDescription;
//	}
//	public void setBudgetHeadDescription(String budgetHeadDescription) {
//		this.budgetHeadDescription = budgetHeadDescription;
//	}
	public Double getSancAmt() {
		return sancAmt;
	}
	public void setSancAmt(Double sancAmt) {
		this.sancAmt = sancAmt;
	}
	public Double getExpAmt() {
		return expAmt;
	}
	public void setExpAmt(Double expAmt) {
		this.expAmt = expAmt;
	}
	public Double getOsComAmt() {
		return osComAmt;
	}
	public void setOsComAmt(Double osComAmt) {
		this.osComAmt = osComAmt;
	}
	public Double getDipl() {
		return dipl;
	}
	public void setDipl(Double dipl) {
		this.dipl = dipl;
	}
	public Double getBalAmt() {
		return balAmt;
	}
	public void setBalAmt(Double balAmt) {
		this.balAmt = balAmt;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getAsOnDate() {
		return asOnDate;
	}
	public void setAsOnDate(String asOnDate) {
		this.asOnDate = asOnDate;
	}
	
	
	
	 
}
