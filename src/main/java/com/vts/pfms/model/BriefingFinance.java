package com.vts.pfms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "briefing_finance")
public class BriefingFinance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long FinanceId;
	private Long ScheduleId;
	private String CategoryName;
	private BigDecimal Allotment;
	private BigDecimal Sanction;
	private BigDecimal Balance;
	private BigDecimal OutStanding;
	private BigDecimal Expenditure;
	private BigDecimal Inr;
	private BigDecimal Fe;
	private String CreatedBy;
	private LocalDateTime CreatedDate;
	private Integer IsActive;
	
	public Long getFinanceId() {
		return FinanceId;
	}
	public Long getScheduleId() {
		return ScheduleId;
	}
	public String getCategoryName() {
		return CategoryName;
	}
	public BigDecimal getAllotment() {
		return Allotment;
	}
	public BigDecimal getSanction() {
		return Sanction;
	}
	public BigDecimal getBalance() {
		return Balance;
	}
	public BigDecimal getOutStanding() {
		return OutStanding;
	}
	public BigDecimal getExpenditure() {
		return Expenditure;
	}
	public BigDecimal getInr() {
		return Inr;
	}
	public BigDecimal getFe() {
		return Fe;
	}
	public String getCreatedBy() {
		return CreatedBy;
	}
	public LocalDateTime getCreatedDate() {
		return CreatedDate;
	}
	public Integer getIsActive() {
		return IsActive;
	}
	public void setFinanceId(Long financeId) {
		FinanceId = financeId;
	}
	public void setScheduleId(Long scheduleId) {
		ScheduleId = scheduleId;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	public void setAllotment(BigDecimal allotment) {
		Allotment = allotment;
	}
	public void setSanction(BigDecimal sanction) {
		Sanction = sanction;
	}
	public void setBalance(BigDecimal balance) {
		Balance = balance;
	}
	public void setOutStanding(BigDecimal outStanding) {
		OutStanding = outStanding;
	}
	public void setExpenditure(BigDecimal expenditure) {
		Expenditure = expenditure;
	}
	public void setInr(BigDecimal inr) {
		Inr = inr;
	}
	public void setFe(BigDecimal fe) {
		Fe = fe;
	}
	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		CreatedDate = createdDate;
	}
	public void setIsActive(Integer isActive) {
		IsActive = isActive;
	}
	
	@Override
	public String toString() {
		return "BriefingFinance [FinanceId=" + FinanceId + ", ScheduleId=" + ScheduleId + ", CategoryName="
				+ CategoryName + ", Allotment=" + Allotment + ", Sanction=" + Sanction + ", Balance=" + Balance
				+ ", OutStanding=" + OutStanding + ", Expenditure=" + Expenditure + ", Inr=" + Inr + ", Fe=" + Fe
				+ ", CreatedBy=" + CreatedBy + ", CreatedDate=" + CreatedDate + ", IsActive=" + IsActive + "]";
	}
	
}
