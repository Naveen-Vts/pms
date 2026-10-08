package com.vts.pfms.model;

public class TotalDemand {
	 private String ProjectId;
     private String ProjectCode;
     private String DemandCount;
     private String EstimatedCost;
     private String SupplyOrderCount;
     private String TotalOrderCost;
     private String TotalExpenditure;
     
	 public String getProjectId() {
		 return ProjectId;
	 }
	 public void setProjectId(String projectId) {
		 ProjectId = projectId;
	 }
	 public String getProjectCode() {
		 return ProjectCode;
	 }
	 public void setProjectCode(String projectCode) {
		 ProjectCode = projectCode;
	 }
	 public String getDemandCount() {
		 return DemandCount;
	 }
	 public void setDemandCount(String demandCount) {
		 DemandCount = demandCount;
	 }
	 public String getEstimatedCost() {
		 return EstimatedCost;
	 }
	 public void setEstimatedCost(String estimatedCost) {
		 EstimatedCost = estimatedCost;
	 }
	 public String getSupplyOrderCount() {
		 return SupplyOrderCount;
	 }
	 public void setSupplyOrderCount(String supplyOrderCount) {
		 SupplyOrderCount = supplyOrderCount;
	 }
	 public String getTotalOrderCost() {
		 return TotalOrderCost;
	 }
	 public void setTotalOrderCost(String totalOrderCost) {
		 TotalOrderCost = totalOrderCost;
	 }
	 public String getTotalExpenditure() {
		 return TotalExpenditure;
	 }
	 public void setTotalExpenditure(String totalExpenditure) {
		 TotalExpenditure = totalExpenditure;
	 }
     
     
}
