package com.vts.pfms.master.dao;

import java.util.List;

import com.vts.pfms.committee.model.PfmsEmpRoles;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.master.dto.DivisionEmployeeDto;
import com.vts.pfms.master.model.DivisionEmployee;
import com.vts.pfms.master.model.DivisionGroup;
import com.vts.pfms.master.model.DivisionTd;
import com.vts.pfms.master.model.Employee;
import com.vts.pfms.master.model.HolidayMaster;
import com.vts.pfms.master.model.IndustryPartner;
import com.vts.pfms.master.model.IndustryPartnerRep;
import com.vts.pfms.master.model.LabPmsEmployee;
import com.vts.pfms.master.model.MilestoneActivityType;
import com.vts.pfms.master.model.PfmsFeedback;
import com.vts.pfms.master.model.PfmsFeedbackAttach;
import com.vts.pfms.master.model.PfmsFeedbackTrans;
import com.vts.pfms.master.model.RoleMaster;
import com.vts.pfms.model.LabMaster;

public interface MasterDao  {
	
	public List<Object[]> OfficerList() throws Exception;
	public List<Object[]> DesignationList() throws Exception;
	public List<Object[]> OfficerDivisionList() throws Exception;
	public List<Object[]> OfficerEditData(String OfficerId) throws Exception;
	public int OfficerMasterDelete(Employee employee) throws Exception;
	public List<String> EmpNoCheck() throws Exception;
	public Long OfficeMasterInsert(Employee employee) throws Exception;
	public int OfficerMasterUpdate(Employee employee) throws Exception;
	public List<Object[]> LabList()throws Exception;
	public Long OfficeMasterExternalInsert(Employee empExternal)throws Exception;
	public List<Object[]> ExternalOfficerList(String LabCode)throws Exception;
	public List<Object[]> ExternalOfficerEditData(String officerId) throws Exception;
	public int OfficerExtUpdate(Employee employee) throws Exception;
	public Object[] getOfficerDetalis(String officerId)throws Exception;
	public List<Object[]> updateAndGetList(Long empId, String newSeniorityNumber)throws Exception;
	public int  updateAllSeniority(Long empIdL, Long long1)throws Exception;
	public List<Object[]> DivisionList() throws Exception;
	public List<Object[]> DivisionEmpList(String divisionid) throws Exception;
	public List<Object[]> DivisionNonEmpList(String divisionid) throws Exception;
	public Object[] DivisionData(String divisionid) throws Exception;
	public int DivsionEmployeeRevoke(DivisionEmployeeDto dto) throws Exception;
	public long DivisionAssignSubmit(DivisionEmployee model) throws Exception;
	public List<Object[]> ActivityList() throws Exception;
	public Object[] ActivityNameCheck(String activityTypeId, String activityType) throws Exception;
	public long ActivityAddSubmit(MilestoneActivityType model) throws Exception;
	public List<Object[]> GroupsList(String LabCode) throws Exception;
	public List<Object[]> GroupHeadList(String LabCode) throws Exception;
	public long GroupAddSubmit(DivisionGroup model) throws Exception;
	public Object[] GroupAddCheck(String gcode) throws Exception;
	public Object[] GroupsData(String groupid) throws Exception;
	public int GroupMasterUpdate(DivisionGroup model) throws Exception;
	public List<Object[]> LabMasterList() throws Exception;
	public List<Object[]> EmployeeList() throws Exception;
	public List<Object[]> LabMasterEditData(String LabId) throws Exception;
	public int LabMasterUpdate(LabMaster labmaster) throws Exception;
	public List<Object[]> LabsList() throws Exception;
	public List<String> EmpExtNoCheck() throws Exception;
	public int OfficerExtDelete(Employee employee) throws Exception;
	public List<Object[]> empNoCheckAjax(String empno) throws Exception;
	public List<Object[]> extEmpNoCheckAjax(String empno) throws Exception;
	public Long FeedbackInsert(PfmsFeedback feedback) throws Exception;
	public long FeedbackAttachInsert(PfmsFeedbackAttach main) throws Exception;
	public List<Object[]> FeedbackList(String fromdate , String todate , String empid ,String LabCode,String feedbacktype) throws Exception;
	public List<Object[]> FeedbackListForUser(String LabCode , String empid) throws Exception;
	public Object[] FeedbackContent(String feedbackid) throws Exception;
	public List<Object[]> GetfeedbackAttch()throws Exception;
	public PfmsFeedback getPfmsFeedbackById(String feedbackId);
	public List<Object[]> GetfeedbackAttchForUser(String empid)throws Exception;
	public PfmsFeedbackAttach FeedbackAttachmentDownload(String achmentid) throws Exception;
	public List<Object[]> TDList(String LabCode)throws Exception;
	public List<Object[]> TDHeadList(String LabCode) throws Exception;
	public Object[] TDsData(String tdid) throws Exception;
	public long TDAddSubmit(DivisionTd dtd)throws Exception;
	public Object[] TDAddCheck(String tCode)throws Exception;
	public int TDMasterUpdate(DivisionTd model)throws Exception;
	public List<Object[]> TDListAdd()throws Exception;
	public int UpdateActivityType(String activityType, String activityTypeId, String isTimeSheet, String activityCode) throws Exception;
	public int DeleteActivityType(String activityType)throws Exception;
	public List<Object[]> industryPartnerList() throws Exception;
	public Object[] industryPartnerDetailsByIndustryPartnerRepId(String industryPartnerRepId) throws Exception;
	public List<IndustryPartner> getIndustryPartnerList() throws Exception;
	public IndustryPartner getIndustryPartnerById(String industryPartnerId) throws Exception;
	public IndustryPartnerRep getIndustryPartnerRepById(String industryPartnerRepId) throws Exception;
	public long addIndustryPartner(IndustryPartner partner) throws Exception;
	public int revokeIndustryPartnerRep(String industryPartnerRepId) throws Exception;
	public List<Object[]> industryPartnerRepDetails(String industryPartnerId, String industryPartnerRepId) throws Exception;
	public Employee getEmployeeById(String empId) throws Exception;
	public List<Object[]> HolidayList(String yr) throws Exception;
	public HolidayMaster getHolidayData(Long holidayid);
	public long HolidayEditSubmit(HolidayMaster hw);
	public long HolidayAddSubmit(HolidayMaster holiday);
	public List<Object[]> labPmsEmployeeList(String LabCode) throws Exception;
	public LabPmsEmployee labemployeefindbyEmpId(long EmpId) throws Exception;
	public Object[] getDesigId(String designation) throws Exception;
	public long updateEmployee(Employee employee) throws Exception;
	public long EmployeeMasterInsert(Employee employee) throws Exception;
	public List<Object[]> getEmployees()throws Exception;
	public PfmsEmpRoles getPfmsEmpRolesById(String roleid)throws Exception;
	public long addPfmsEmpRoles(PfmsEmpRoles pf)throws Exception;
	public List<PfmsEmpRoles>getAllEmpRoles() throws Exception ;
	public List<Object[]> CheckGroupMasterCode(String TdCode) throws Exception;
	public List<Object[]> checkDivisionMasterId(String groupId) throws Exception;
	public List<Object[]> getFeedbackTransByFeedbackId(String feedbackId) throws Exception;
	public long addPfmsFeedbackTrans(PfmsFeedbackTrans transaction) throws Exception;

	/* **************************** Programme Master - Naveen R  - 16/07/2025 **************************************** */
	public List<Object[]> getProgramMasterList() throws Exception;
	public long addProgrammeMaster(ProgrammeMaster master) throws Exception;
	public int removeProjectLinked(String programmeId) throws Exception;
	public long addProgrammeProjects(ProgrammeProjects programmeprojects) throws Exception;
	public Long ProgramCodeCheck(String programmeCode, String prgrammeId) throws Exception;
	public List<Object[]> getProjectList(String labcode) throws Exception;
	public List<Object[]> getProjectList(String labcode, String programmeId) throws Exception;
	/* **************************** Programme Master - Naveen R  - 16/07/2025 End**************************************** */
	
	public Long addRoleMaster(RoleMaster roleMaster)throws Exception;
	
	// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check start
	public Long getRoleNameDulicateCount(String roleName) throws Exception;
	public Long getRoleCodeDuplicateCount(String roleCode) throws Exception;
	// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check end
	public List<LabMaster> getAllLabMaster() throws Exception;
}
