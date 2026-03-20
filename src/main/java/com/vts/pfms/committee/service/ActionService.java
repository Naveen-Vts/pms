package com.vts.pfms.committee.service;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vts.pfms.committee.dao.ActionSelfDao;
import com.vts.pfms.committee.dto.ActionAssignDto;
import com.vts.pfms.committee.dto.ActionMainDto;
import com.vts.pfms.committee.dto.ActionSubDto;
import com.vts.pfms.committee.dto.OldRfaUploadDto;
import com.vts.pfms.committee.dto.RfaActionDto;
import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.committee.model.ActionAttachment;
import com.vts.pfms.committee.model.ActionMain;
import com.vts.pfms.committee.model.RfaAssign;
import com.vts.pfms.committee.model.RfaInspection;

public interface ActionService {


	public List<Object[]> EmployeeList(String LabCode) throws Exception;
	public List<Object[]> AssignedList(String EmpId ) throws Exception;
	public Object[] GetActionReAssignData(String Actionassignid)throws Exception;
	public Object[] GetProjectData(String projectid)throws Exception;
	public long ActionMainInsert(ActionMainDto main , ActionAssignDto assign)throws Exception;
	public List<Object[]> AssigneeList(String EmpId) throws Exception;
	public List<Object[]> AssigneeData(String MainId ,String Assignid) throws Exception;
	public List<Object[]> SubList(String MainId) throws Exception;
	public List<Object[]> AssigneeDetails(String assignid) throws Exception;
	public long ActionSubInsert(ActionSubDto main)throws Exception;
	public ActionAttachment ActionAttachmentDownload(String achmentid) throws Exception;
	public int ActionSubDelete(String id,String UserId) throws Exception;
	public int ActionForward(String mainid,String assignid,String UserId) throws Exception;
	public List<Object[]> ForwardList(String EmpId) throws Exception;
	public long ActionClosed(String id,String Remarks,String UserId ,String assignid , String levelcount) throws Exception;
	public long ActionSendBack(String id,String Remarks,String UserId, String assignid) throws Exception;
	public List<Object[]> StatusList(String EmpId,String fdate, String tdate) throws Exception;
	public List<Object[]> ActionList(String EmpId) throws Exception;
	public List<Object[]> CommitteeActionList(String EmpId) throws Exception;
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception ;
	public List<Object[]> ScheduleActionList(String ScheduleId) throws Exception;
	public List<Object[]> MeetingContent(String ScheduleId) throws Exception;
	public List<Object[]> ActionNoSearch(String ActionMainId) throws Exception;
	public String ScheduleActionItem(String ScheduleId) throws Exception;
	public List<Object[]> ActionReports(String EmpId,String Term,String Position,String Type,String LabCode) throws Exception ;
	public List<Object[]> ActionSearch(String EmpId,String No,String Position) throws Exception;
	public List<Object[]> ProjectList() throws Exception;
	public List<Object[]> ActionCountList(String ProjectId) throws Exception;
	public List<Object[]> projectdetailsList(String EmpId) throws Exception;
	public List<Object[]> ActionWiseReports(String Term,String ProjectId) throws Exception ;
	public List<Object[]> ActionPdcReports(String Emp,String ProjectId,String Position,String From,String To ) throws Exception ;
	public int ActionExtendPdc(String id,String date,String UserId ,String assignid) throws Exception;
	public List<Object[]> ActionSelfList(String EmpId)throws Exception;
	public List<Object[]> SearchDetails(String MainId ,String assignid) throws Exception;
	public List<Object[]> allprojectdetailsList() throws Exception;
	public List<Object[]> ActionWiseAllReport(String ProjectId) throws Exception;
	public long ActionSelfReminderAddSubmit(ActionSelfDao actionselfdao) throws Exception;
	public List<Object[]> ActionSelfReminderList(String empid,String fromdate,String todate) throws Exception;
	public int ActionSelfReminderDelete(String actionid) throws Exception;
	public List<Object[]> getActionAlertList() throws Exception;
	public List<Object[]> getActionToday(String empid,String ai) throws Exception;
	public List<Object[]> getActionTommo(String empid,String ai) throws Exception;
	public List<Object[]> getMeetingAlertList() throws Exception;
	public List<Object[]> getMeetingToday(String empid) throws Exception;
	public List<Object[]> getMeetingTommo(String empid) throws Exception;
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype , String LabCode) throws Exception;
	public List<Object[]> AllEmpNameDesigList() throws Exception;
	public List<Object[]> ProjectEmpList(String projectid) throws Exception;
	public List<Object[]> EmployeeDropdown(String empid, String logintype,String projectid) throws Exception;
	public Object[] ActionDetailsAjax(String actionid ,String assignid) throws Exception;
	public int ActionMainEdit(ActionMain main) throws Exception;
	public int ActionAssignEdit(ActionAssign assign) throws Exception;
	public List<Object[]> AllLabList() throws Exception;
	public Object[] GetActionMainData(String Actionmainid)throws Exception;
	public List<Object[]> ClusterExpertsList() throws Exception;
	public List<Object[]> ClusterFilterExpertsList(String Labcode , String MainId)throws Exception;
	public Object[] LabInfoClusterLab(String LabCode) throws Exception;
	public List<Object[]> LabEmployeeList(String LabCode) throws Exception;
	public List<Object[]> LabEmpListFilterForAction(String LabCode , String MainId)throws Exception;
	public List<Object[]> ActionSubLevelsList(String ActionAssignId) throws Exception;
	public List<Object[]> ActionSubList(String assignid) throws Exception;
	public long ActionMainInsertFromOnboard(ActionMainDto main , ActionAssign assign)throws Exception;
	public long IssueSubInsert(ActionSubDto main) throws Exception;
	public List<Object[]> GetIssueList( String Empid )throws Exception;
	public int IssueClosed(String id, String Remarks, String UserId ,String assignid)throws Exception;
	public List<Object[]> GetRecomendationList(String projectid ,  String committeid)throws Exception;
	public List<Object[]> GetDecisionList(String projectid ,  String committeid)throws Exception;
	public Object[] ActionAssignDataAjax(String assignid) throws Exception;
	public List<Object[]> GetRecDecSoughtList(String projectid,String  committeeid , String type)throws Exception;
	public List<Object[]> getActualDecOrRecSought(String scheduleid , String type)throws Exception;
	public List<Object[]> getDecOrRecSought(String scheduleid , String type)throws Exception;
	public List<Object[]> GetActionList(String empid)throws Exception;
	public List<Object[]> ActionMonitoring(String ProjectId , String Status)throws Exception;
	public List<Object[]> GetActionListForFevorite(String fromdate , String todate , String projectid , String  empid)throws Exception;
	public Long AddFavouriteList(String[] favoriteid , Long empid ,String userid)throws Exception;
	public List<Object[]> GetFavouriteList(String empid)throws Exception;
	public ActionAssign getActionAssign(String actionassignId) throws Exception;
	public List<Object[]> GetRfaActionList(String EmpId,String projectType, String projectId, String initiationId,String fdate,String tdate) throws Exception;
	public List<Object[]> ProjectTypeList() throws Exception;
	public List<Object[]> PriorityList() throws Exception;
	
	public Long RfaActionSubmit(RfaActionDto rfa,String LabCode,String UserId,String[] assignee,String[] CCEmpName) throws Exception;
	public Object[] RfaActionEdit(String rfaid) throws Exception;
	public Object[] RfaLabDetails(String LabCode) throws Exception;
	public Long RfaEditSubmit(RfaActionDto rfa, String[] assignee, String[] CCEmpName) throws Exception;
	public Object[] RfaPrintData(String rfaid)throws Exception;
	public List<Object[]> RfaForwardList(String EmpId) throws Exception;
	public List<Object[]> RfaInspectionApprovalList(String EmpId) throws Exception;
	public List<Object[]> RfaInspectionList(String EmpId) throws Exception;
	public List<Object[]> RfaForwardApprovedList(String EmpId) throws Exception;
	public List<Object[]> RfaInspectionApprovedList(String EmpId) throws Exception;
	public long RfaActionForward(String rfaStatus, String projectid, String UserId, String rfa,String EmpId, String rfaEmpId)throws Exception;
	public Object[] getRfaAssign(String rfa) throws Exception;
	public Long RfaModalSubmit(RfaInspection inspection,RfaActionDto rfa) throws Exception;
	public Object[] RfaAssignAjax(String rfaId)throws Exception;
	public Long RfaModalUpdate(RfaInspection inspection,RfaActionDto rfa) throws Exception;
	public Long RfaReturnList(String rfaStatus, String UserId, String rfa,String EmpId,String assignee,String assignor,String replyMsg) throws Exception;
	public String getAssignDetails(String empId, Long rfaId)throws Exception;
	public Object[] getRfaAddData(String rfaId)throws Exception;
	public Object[] getRfaInspectionData(String rfaId)throws Exception;
	public List<Object[]> getrfaRemarks(String rfaId,String Status)throws Exception;
	public List<Object[]> ProjectApplicableCommitteeList(String projectid) throws Exception;
	public List<Object[]> MeettingCount(String committeeid, String projectid)throws Exception;
	public List<Object[]> MeettingList(String committeeid, String projectid, String scheduleid,String status)throws Exception;
	public List<Object[]> MeettingActionList(String committeeid, String projectid, String scheduleid, String empId)throws Exception;
	public List<Object[]> getAllEmployees(String flag)throws Exception;
	public List<Object[]> getRfaModalEmpList(String labCode)throws Exception;
	public List<Object[]> getRfaTDList(String labCode)throws Exception;
	public List<Object[]> getRfaTransList(String rfaTransId)throws Exception;
	public Object[] RfaAttachmentDownload(String rfaid)throws Exception;
	public List<Object[]> AssigneeEmpList()throws Exception;
	//public List<String> CCAssigneeList(String rfaid)throws Exception;
	public List<String> CCAssignorList(String rfaid)throws Exception;
	public List<Object[]> GetRfaActionList1(String projectType, String projectId, String initiationId, String fdate, String tdate)throws Exception;
	public List<Object[]> RfaProjectwiseList(String empId, String projectType, String projectId, String initiationId, String fdate, String tdate)throws Exception;
	public List<Object[]> getmodifieddate(String userId, int pid)throws Exception;
	public List<Object[]> getProjectByDirectorID(String empId)throws Exception;
	public List<Object[]> getRecentWeeklyUpdateDate(String string)throws Exception;
	public List<Object[]> getRiskDetails(String userId, int pid)throws Exception;
	List<Object[]> getMilestoneDate( int pid)throws Exception;
	public List<Object[]> getMeetingDate(int pid)throws Exception;
	public List<Object[]> getProdurementDate(int pid)throws Exception;
	public List<Object[]> getAllRecentUpdateList(String projectId)throws Exception;
	public Object getEmpnameById(int int1)throws Exception;
	public Object getProjectCodeById(int ProjectId)throws Exception;
	public Object getProjectShortNameById(int ProjectId)throws Exception;
	public int ActionSubDeleteUpdate(String ActionAssignId, String progress, String progressDate, String progressRemarks)throws Exception;
	public int ActionRemarksEdit(String actionAssignId, String progress, String progressRemarks, String UserId)throws Exception;
	public int actionSubRemarksEdit(String actionSubId, String progress, String progressRemarks, String UserId)throws Exception;
	public List<String> rfaMailSend(String rfa)throws Exception;
	public List<Object[]> getRfaNoTypeList()throws Exception;
	public List<Object[]> RfaCCList()throws Exception;
	public List<Object[]> rfaTotalActionList(String projectType,String projectid, String rfatypeid, String fdate, String tdate);
	public int CommitteActionEdit(ActionAssignDto actionAssign)throws Exception;
	public List<Object[]> RfaPendingCount(String empId)throws Exception;
	//prakarsh
	public  List<Object[]>  ActionReportsNew(String empId, String string, String string2, String string3, String labCode,
			String logintype)throws Exception;
	public Long oldRfaUploadSubmit(OldRfaUploadDto rfadto)throws Exception;
	public List<Object[]> getoldRfaUploadList(String labCode, String projectId)throws Exception;
	public long oldRfaUploadEditSubmit(OldRfaUploadDto rfadto)throws Exception;
	
	public List<Object[]> getMeetingList(long ProjectId, String fromDate, String toDate) throws Exception;
	public List<Object[]> getMeetingAction(long MeetingId, String loginType, String empId)throws Exception;
	public int CommitteActionDelete(ActionAssignDto actionAssign)throws Exception;
	public List<Object[]> getvendorList()throws Exception;
	public Long rfaCloseForExternal(RfaActionDto rfa)throws Exception;
	public Object[] getActionMainAttachMent(String mainid)throws Exception;
	
	//newly Added
	public List<Object[]> TotalActions()throws Exception;
	public List<Object[]> getProjects(String empId)throws Exception;
	public int ActionAssignerEdit(ActionAssign assign)throws Exception;
	
	// Naveen 22/10/25 Non Project Action No Change
	public long insertActionMainFornonProject(ActionMainDto mainDto, ActionAssignDto assign)throws Exception;
	public List<Object[]> assigneeListForFromExternal()throws Exception;
	public List<Object[]> divisionList()throws Exception;
	public List<Object[]> getDivisionEmployeesList(String divisionId) throws Exception;
	public List<Object[]> getRfaActionsOnRFAtype(String projectType, String projectid, String rfatypeid, String rfaType,String empId, String fdate, String tdate,String rfaStatus) throws Exception;
	
}
