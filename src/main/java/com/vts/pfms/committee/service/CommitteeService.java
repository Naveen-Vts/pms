package com.vts.pfms.committee.service;

import java.util.List;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vts.pfms.committee.dto.CommitteeConstitutionApprovalDto;
import com.vts.pfms.committee.dto.CommitteeDto;
import com.vts.pfms.committee.dto.CommitteeInvitationDto;
import com.vts.pfms.committee.dto.CommitteeMainDto;
import com.vts.pfms.committee.dto.CommitteeMembersDto;
import com.vts.pfms.committee.dto.CommitteeMembersEditDto;
import com.vts.pfms.committee.dto.CommitteeMinutesAttachmentDto;
import com.vts.pfms.committee.dto.CommitteeMinutesDetailsDto;
import com.vts.pfms.committee.dto.CommitteeScheduleAgendaDto;
import com.vts.pfms.committee.dto.CommitteeScheduleDto;
import com.vts.pfms.committee.dto.CommitteeSubScheduleDto;
import com.vts.pfms.committee.dto.EmpAccessCheckDto;
import com.vts.pfms.committee.dto.MeetingCheckDto;
import com.vts.pfms.committee.model.CommitteScheduleMinutesDraft;
import com.vts.pfms.committee.model.CommitteeAircraft;
import com.vts.pfms.committee.model.CommitteeCARS;
import com.vts.pfms.committee.model.CommitteeDefaultAgenda;
import com.vts.pfms.committee.model.CommitteeDivision;
import com.vts.pfms.committee.model.CommitteeInitiation;
import com.vts.pfms.committee.model.CommitteeLetter;
import com.vts.pfms.committee.model.CommitteeMeetingDPFMFrozen;
import com.vts.pfms.committee.model.CommitteeMember;
import com.vts.pfms.committee.model.CommitteeMinutesAttachment;
import com.vts.pfms.committee.model.CommitteeMomAttachment;
import com.vts.pfms.committee.model.CommitteeProject;
import com.vts.pfms.committee.model.CommitteeRepresentative;
import com.vts.pfms.committee.model.CommitteeScheduleAgendaDocs;
import com.vts.pfms.committee.model.CommitteeSchedulesMomDraftRemarks;
import com.vts.pfms.committee.model.CommitteeSubSystem;
import com.vts.pfms.committee.model.PmsEnote;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.master.dto.ProjectFinancialDetails;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.print.model.CommitteeProjectBriefingFrozen;
import com.vts.pfms.print.model.MinutesFinanceList;

import jakarta.servlet.http.HttpServletRequest;

public interface CommitteeService {
	
	public List<Object[]> EmployeeList(String labcode) throws Exception;
	public List<Object[]> ExpertList() throws Exception;
	public List<Object[]> AllLabList() throws Exception;
	public Object[] CommitteeName(String CommitteeId) throws Exception;
	public long CommitteeDetailsSubmit(CommitteeMainDto committeemaindto)throws Exception;
	public long CommitteeAdd(CommitteeDto committeeDto) throws Exception;
	public List<Object[]> CommitteeListActive(String isglobal,String Projectapplicable, String LabCode) throws Exception;
	public Object[] CommitteeDetails(String committeeid) throws Exception;
	public long CommitteeEditSubmit(CommitteeDto committeeDto) throws Exception;
	public List<Object[]> CommitteeMainList(String labcode) throws Exception;
	public List<Object[]> EmployeeListWithoutMembers(String committeemainid,String LabCode) throws Exception;
	public int CommitteeMemberDelete(String committeememberid, String modifiedby)throws Exception;
	public List<Object[]> EmployeeListNoMembers(String labid, String committeemainid) throws Exception;
	public long CommitteeMainMembersAddSubmit(String committeemainid, String[] Member,String userid) throws Exception;
	public Long CommitteeScheduleAgendaEdit(CommitteeScheduleAgendaDto scheduleagendadto,String attachmentid) throws Exception;
	public long CommitteeScheduleAddSubmit(CommitteeScheduleDto committeescheduledto) throws Exception;
	public List<Object[]> CommitteeScheduleListNonProject(String committeeid) throws Exception;
	public Object[] CommitteeScheduleEditData(String CommitteeScheduleId) throws Exception;
	public List<Object[]> AgendaReturnData(String CommitteeScheduleId) throws Exception;
	public List<Object[]> ProjectList(String LabCode) throws Exception;
	//public long CommitteeMainEdit(CommitteeMainDto committeemaindto) throws Exception;
 	public Long CommitteeAgendaSubmit(List<CommitteeScheduleAgendaDto> scheduleagendadtos) throws Exception;
	public int CommitteeAgendaDelete(String committeescheduleagendaid, String attachmentid,String Modifiedby, String  scheduleid,String AgendaPriority) throws Exception;
	public Long CommitteeAgendaPriorityUpdate(String[] agendaid,String[] priority) throws Exception;
	public List<Object[]> MinutesUnitList(String CommitteeScheudleId) throws Exception;
	
	
 	public Long CommitteeScheduleUpdate(CommitteeScheduleDto committeescheduledto) throws Exception;
 	public List<Object[]> CommitteeMinutesSpecList(String CommitteeScheduleId) throws Exception;
 	public Long CommitteeMinutesInsert(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception;	
 	public Object[] CommitteeMinutesSpecDesc(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception;
 	public Object[] CommitteeMinutesSpecEdit(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception;
 	public Object[] CommitteeMinutesSpecEditForAde(CommitteeMinutesDetailsDto committeeminutesdetailsdto) throws Exception;
 	public Long CommitteeMinutesUpdate(CommitteeMinutesDetailsDto committeeminutesdetailsdto)throws Exception;
 	public Long ScheduleMinutesUnitUpdate(CommitteeMinutesDetailsDto detailsdto) throws Exception;
 	public int ScheduleMinutesUnitUpdate(String UnitId,String Unit,String UserId) throws Exception;
 	
 	public long CommitteeSubScheduleSubmit(CommitteeSubScheduleDto committeesubscheduledto) throws Exception;
 	public List<Object[]> CommitteeSubScheduleList(String scheduleid) throws Exception;
 	public List<Object[]> CommitteeMinutesSub()throws Exception;
 	public List<Object[]> CommitteeMinutesSpecdetails()throws Exception;
 	public List<Object[]> CommitteeScheduleMinutes(String scheduleid) throws Exception;
 	public List<Object[]> CommitteeAttendance(String CommitteeScheduleId) throws Exception;

 	public int MeetingAgendaApproval(String CommitteeScheduleId,String UserId,String EmpId,String Option) throws Exception;
	public List<Object[]> MeetingApprovalAgendaList(String EmpId) throws Exception;
 	public int MeetingAgendaApprovalSubmit( String ScheduleId, String Remarks,String UserId, String EmpId,String Option) throws Exception;
 	
 	public Object[] CommitteeScheduleData(String committeescheduleid) throws Exception;
 	public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception;
	public List<Object[]> EmployeeListNoInvitedMembers(String scheduleid,String LabCode) throws Exception;
	public Long CommitteeInvitationCreate(CommitteeInvitationDto committeeinvitationdto) throws Exception;
	public Long CommitteeInvitationDelete(String committeeinvitationid) throws Exception;
	public Long CommitteeAttendanceToggle(String InvitationId) throws Exception;
	public List<Object[]> ExternalMembersNotInvited(String scheduleid) throws Exception;
	public List<Object[]> CommitteeAgendaPresenter(String scheduleid) throws Exception;
	public List<Object[]> PresenterRemovalEmpList(List<Object[]> Employeelist, List<Object[]> PresenterList) throws Exception;

	
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype,String LabCode) throws Exception;
	public Object[] projectdetails(String projectid) throws Exception;
	public List<Object[]> ProjectScheduleListAll(String projectid) throws Exception;
	public List<Object[]> ProjectApplicableCommitteeList(String projectid) throws Exception;
	public int UpdateComitteeMainid(String committeemainid, String scheduleid) throws Exception;
	public List<Object[]> ProjectCommitteeScheduleListAll(String projectid, String committeeid) throws Exception;
	
	public List<Object[]> ChaipersonEmailId(String CommitteeMainId) throws Exception;
	public Object[] ProjectDirectorEmail(String ProjectId) throws Exception;
	public Object[] RtmddoEmail() throws Exception;
	public String UpdateOtp(CommitteeScheduleDto committeescheduledto) throws Exception;
	public String KickOffOtp(String CommitteeScheduleId) throws Exception;

	
	public List<Object[]> UserSchedulesList(String EmpId,String MeetingId) throws Exception;
	public List<Object[]> MeetingSearchList(String MeetingId ,String LabCode) throws Exception;
	public Object[] CommitteeScheduleDataPro(String committeescheduleid, String projectid) throws Exception;
	public Object[] LabDetails(String LabCode) throws Exception;
	
	public long ProjectCommitteeAdd(String ProjectId,String[] Committee ,String UserId) throws Exception;
	public List<Object[]> ProjectMasterList(String ProjectId) throws Exception;
	public long ProjectCommitteeDelete(String[] CommitteeProject,String user ) throws Exception;
	public List<Object[]> ProjectCommitteesListNotAdded(String projectid,String LabCode) throws Exception;
	public List<Object[]> CommitteeNonProjectList() throws Exception;
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId,String divisionid,String initiationid,String projectstatus) throws Exception;
	public int CommitteeProjectUpdate(String ProjectId,String CommitteeId) throws Exception;
	public List<Object[]> CommitteeAutoScheduleList(String ProjectId, String committeeid, String divisionid , String initiationid,String projectstatus) throws Exception;
	
	public int UpdateMeetingVenue(CommitteeScheduleDto csdto) throws Exception;
	public long MinutesAttachmentAdd(CommitteeMinutesAttachmentDto dto) throws Exception;
	public List<Object[]> MinutesAttachmentList(String scheduleid) throws Exception;
	public CommitteeMinutesAttachment MinutesAttachDownload(String attachmentid) throws Exception;
	public List<Object[]> PfmsCategoryList() throws Exception;
	public int MinutesAttachmentDelete(String attachmentid) throws Exception;
	public int MeetingMinutesApproval(String CommitteeScheduleId,String UserId,String EmpId,String Option) throws Exception;

	public List<Object[]> MeetingApprovalMinutesList(String EmpId) throws Exception;
 	public int MeetingMinutesApprovalSubmit( String ScheduleId, String Remarks,String UserId, String EmpId,String Option) throws Exception;

	public List<Object[]> CommitteeAllAttendance(String CommitteeScheduleId) throws Exception;
	public List<Object[]> MeetingReports(String EmpId,String Term, String ProjectId,String divisionid,String initiationid,String logintype,String LabCode) throws Exception;
	public List<Object[]> MeetingReportListAll(String fdate,String tdate, String ProjectId) throws Exception;
	//public List<Object[]> MeetingReportListEmp(String fdate,String tdate, String ProjectId,String EmpId) throws Exception;
	public Object[] KickOffMeeting(HttpServletRequest req, RedirectAttributes redir) throws Exception;
	public int UpdateCommitteeInvitationEmailSent(String scheduleid) throws Exception;
	public	List<Object[]> MinutesViewAllActionList(String scheduleid) throws Exception;
	public List<Object[]> ProjectCommitteesList(String LabCode) throws Exception;



	public List<Object[]> ExternalEmployeeListFormation(String LabId,String committeemainid) throws Exception;
	public long CommitteeMembersInsert(CommitteeMembersDto dto) throws Exception;
	public List<Object[]> ExternalMembersNotAddedCommittee(String committeemainid) throws Exception;
	public List<Object[]> CommitteeAllMembers(String committeemainid) throws Exception;
	public List<Object[]> ExternalEmployeeListInvitations(String labcode, String scheduleid) throws Exception;
	public Object[] ProjectBasedMeetingStatusCount(String projectid) throws Exception;
	public List<Object[]> allprojectdetailsList() throws Exception;
	public List<Object[]> PfmsMeetingStatusWiseReport(String projectid, String statustype) throws Exception;
	public List<Object[]> ProjectCommitteeFormationCheckList(String projectid) throws Exception;
	public Object[] ProjectCommitteeDescriptionTOR(String projectid, String Committeeid) throws Exception;
	public int ProjectCommitteeDescriptionTOREdit(CommitteeProject committeeproject) throws Exception;
	//public List<Object[]> CommitteeAgendaListByMeetingId(String meetingid) throws Exception;
	public long CommitteePreviousAgendaAdd(String scheduleidto, String[] fromagendaids, String userid) throws Exception;
	public Object[] CommitteeNamesCheck(String name, String sname,String projectid,String LabCode) throws Exception;
	//public List<Object[]> ChairpersonEmployeeList() throws Exception;
	public List<Object[]> divisionList() throws Exception;
	public List<Object[]> CommitteedivisionAssigned(String divisionid) throws Exception;
	public List<Object[]> CommitteedivisionNotAssigned(String divisionid, String LabCode ) throws Exception;
	public long DivisionCommitteeAdd(String divisionid, String[] Committee, String UserId) throws Exception;
	public List<Object[]> DivisionCommitteeFormationCheckList(String divisionid) throws Exception;
	public long DivisionCommitteeDelete(String[] CommitteeProject, String user) throws Exception;
	public int DivisionCommitteeDescriptionTOREdit(CommitteeDivision committeedivision) throws Exception;
	public Object[] DivisionCommitteeDescriptionTOR(String divisionid, String Committeeid) throws Exception;
//	public Object[] CommitteeMainEditDataId(String committeemainid) throws Exception;
	public Object[] DivisionData(String divisionid) throws Exception;
	public List<Object[]> DivisionScheduleListAll(String divisionid) throws Exception;
	public List<Object[]> DivisionCommitteeScheduleList(String divisionid, String committeeid) throws Exception;
	public List<Object[]> DivisionCommitteeMainList(String divisionid) throws Exception;
	public List<Object[]> DivisionMasterList(String divisionid) throws Exception;
	public List<Object[]> DivCommitteeAutoScheduleList(String divisionid) throws Exception;
	
	public List<Object[]> CommitteeActionList(String EmpId) throws Exception;
	public Object[] CommitteeLastScheduleDate(String committeeid) throws Exception;
	public int CommitteeDivisionUpdate(String divisionid, String CommitteeId) throws Exception;

	public List<Object[]> MinutesOutcomeList() throws Exception;
	public List<Object[]> InitiatedProjectDetailsList() throws Exception;
	public List<Object[]> InitiationMasterList(String initiationid) throws Exception;
	public List<Object[]> InitiationCommitteeFormationCheckList(String initiationid) throws Exception;
	public List<Object[]> InitiationCommitteesListNotAdded(String initiationid,String LabCode) throws Exception;
	public Long InvitationSerialnoUpdate(String[] newslno, String[] invitationid) throws Exception;
	public List<Object[]> CommitteeRepList() throws Exception;
	public List<Object[]> CommitteeMemberRepList(String committeemainid) throws Exception;
	public List<Object[]> CommitteeRepNotAddedList(String committeemainid) throws Exception;
	public long CommitteeRepMemberAdd(String[] repids, String committeemainid, String createdby) throws Exception;
	public int CommitteeMemberRepDelete(String memberrepid) throws Exception;
	public List<Object[]> ChairpersonEmployeeListFormation(String LabCode, String committeemainid) throws Exception;
	public List<Object[]> PreseneterForCommitteSchedule(String LabCode)throws Exception;
	public List<Object[]> CommitteeAllMembersList(String committeemainid) throws Exception;
	public int CommitteeMemberUpdate(CommitteeMember model) throws Exception;
	public int CommitteeMainMemberUpdate(CommitteeMembersEditDto dto,CommitteeMainDto cmd) throws Exception;
	public Object[] CommitteMainData(String committeemainid) throws Exception;
	public Long LastCommitteeId(String CommitteeId, String projectid, String divisionid,String initiationid, String carsInitiationId, String programmeId) throws Exception;
	public long InitiationCommitteeAdd(String initiation, String[] Committee, String UserId) throws Exception;
	public long InitiationCommitteeDelete(String[] CommitteeProject, String user) throws Exception;
	public Object[] Initiationdetails(String initiationid) throws Exception;
	public Object[] InitiationCommitteeDescriptionTOR(String initiationid, String Committeeid) throws Exception;
	public int InitiationCommitteeDescriptionTOREdit(CommitteeInitiation committeeinitiation) throws Exception;
	public List<Object[]> InitiaitionMasterList(String initiationid) throws Exception;
	public int CommitteeInitiationUpdate(String initiationid, String CommitteeId) throws Exception;
	public List<Object[]> InitiationCommitteeMainList(String initiationid) throws Exception;
	public List<Object[]> InitiationScheduleListAll(String initiationid) throws Exception;
	public List<Object[]> InitiationCommitteeScheduleList(String initiationid, String committeeid) throws Exception;
	public List<Object[]> LoginDivisionList(String empid) throws Exception;
	public Object[] ProposedCommitteeMainId(String committeemainid) throws Exception;
	public Object[] GetProposedCommitteeMainId(String committeeid, String projectid, String divisionid, String initiationid)
			throws Exception;
	public Object[] CommitteeMainApprovalData(String committeemainid) throws Exception;
	public long CommitteeMainApprove(CommitteeConstitutionApprovalDto dto) throws Exception;
	public List<Object[]> ProposedCommitteList() throws Exception;
	public List<Object[]> ApprovalStatusList(String committeemainid) throws Exception;
	public List<Object[]> ProposedCommitteesApprovalList(String loginid,String EmpId) throws Exception;
	public List<Object[]> ComConstitutionApprovalHistory(String committeemainid) throws Exception;
	public List<Object[]> MeetingReportListEmp(String fdate, String tdate, String ProjectId, String EmpId) throws Exception;
	public List<Object[]> ConstitutionApprovalFlow(String committeemainid) throws Exception;
	public int CommitteeMinutesDelete(String scheduleminutesid) throws Exception;
	public int CommitteeScheduleDelete(CommitteeScheduleDto dto) throws Exception;
	public int ScheduleCommitteeEmpCheck(EmpAccessCheckDto dto) throws Exception;
	public List<Object[]> EmpScheduleData(String empid, String scheduleid) throws Exception;
	public List<Object[]> DefaultAgendaList(String committeeid,String LabCode) throws Exception;
	public List<Object[]> ProcurementStatusList(String projectid) throws Exception;
	public List<Object[]> ActionPlanSixMonths(String projectid) throws Exception;
	public List<Object[]> CommitteeMinutesSpecNew() throws Exception;
	public List<Object[]> MilestoneSubsystems(String projectid) throws Exception;
	public List<Object[]> EmployeeScheduleReports(HttpServletRequest req,String empid, String rtype) throws Exception;
	public List<Object[]> EmployeeDropdown(String empid, String logintype, String projectid) throws Exception;
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode) throws Exception;
	public Object[] AgendaDocLinkDownload(String filerepid) throws Exception;
	public List<Object[]> AgendaList(String CommitteeScheduleId) throws Exception;
	public List<Object[]> AgendaLinkedDocList(String scheduleid) throws Exception;
	public int AgendaUnlinkDoc(CommitteeScheduleAgendaDocs agendadoc) throws Exception;
	public int PreDefAgendaEdit(CommitteeDefaultAgenda agenda) throws Exception;
	public long PreDefAgendaAdd(CommitteeDefaultAgenda agenda) throws Exception;
	public int PreDefAgendaDelete(String agendaid) throws Exception;
	public int MeetingNo(Object[] scheduledata) throws Exception;
	public long insertMinutesFinance(MinutesFinanceList finance) throws Exception;
	public long getLastPmrcId(String projectid,String committeeid,String scheduleId) throws Exception;
    public int updateMinutesFrozen(String schduleid)throws Exception;
    public List<ProjectFinancialDetails> getMinutesFinance(String scheduleid) throws Exception;
	public List<Object[]> ClusterList() throws Exception;
	public List<Object[]> ClusterExpertsList(String committeemainid) throws Exception;
	public List<Object[]> ClusterExpertsListForCommitteeSchdule() throws Exception;
	public List<Object[]> ClusterLabs(String LabCode) throws Exception;
	public Object[] LabInfoClusterLab(String LabCode) throws Exception;
	public List<Object[]> LastPMRCActions(long scheduleid, String committeeid, String proid, String isFrozen) throws Exception;
	public Object[] getDefaultAgendasCount(String committeeId, String LabCode) throws Exception;
	public LabMaster LabDetailes(String LabCode) throws Exception;
	public long FreezeDPFMMinutes(CommitteeMeetingDPFMFrozen dpfm) throws Exception;
	public CommitteeMeetingDPFMFrozen getFrozenDPFMMinutes(String scheduleId) throws Exception;
	public Object[] ProjectDataDetails(String projectid) throws Exception;
	public List<Object[]> totalProjectMilestones(String projectid) throws Exception;
	public CommitteeProjectBriefingFrozen getFrozenCommitteeMOM(String committeescheduleid)throws Exception;
	public long doMomFreezing(CommitteeProjectBriefingFrozen briefing)throws Exception;
	public List<Object[]> getEnvisagedDemandList(String projectid)throws Exception;
	public int MomFreezingUpdate(String committeescheduleid)throws Exception;
	public List<Object[]> getTodaysMeetings(String date)throws Exception;// to get all the scheduleid and projectid of the meeting scheduled on that day
	public List<Object[]> actionDetailsForNonProject(String committeeId, String scheduledate) throws Exception;// 27-11 to get all action points for non project
	public List<Object[]> CommitteeOthersList(String projectid, String divisionid, String initiationid, String projectstatus)throws Exception; //project dashborad others meting list
	public Long MomAttach(CommitteeMomAttachment cm,String LabCode) throws Exception;// new Method
	public Long UpdateMomAttach(Long scheduleId) throws Exception;
	public Object[] MomAttachmentFile(String committeescheduleid) throws Exception;
	public List<Object[]> MomReportList(String projectId, String committeeId) throws Exception;
	// Prudhvi 27/03/2024
	/* ------------------ start ----------------------- */
	List<Object[]> IndustryPartnerRepListInvitationsMainMembers(String industryPartnerId, String committeemainid) throws Exception;
	/* ------------------ end ----------------------- */
	public List<Object[]> ConstitutionApprovalFlowData(String committeemainid)throws Exception;
	public int MemberSerialNoUpdate(String[] newslno, String[] memberId) throws Exception;
	public long saveCommitteeLetter(CommitteeLetter committeeLetter) throws Exception;
	public List<Object[]> getcommitteLetters(String commmitteeId, String projectId, String divisionId, String initiationId) throws Exception;
	public Object[] getcommitteeLetter(String letterId) throws Exception;
	public long UpdateCommitteLetter(String letterId) throws Exception;	
	
	//Prakarsh---
	public List<Object[]> MeettingList(String projectId, String committeeId) throws Exception;
	public List<Object[]> MeettingList(String projectid);
	public List<Object[]> SpecialEmployeeListInvitations(String labCode,String scheduleid)throws Exception;
	public Object allconstitutionapprovalflowData(String committeemainid)throws Exception;
	
	//enote
	public Object[] CommitteMainEnoteList(String committeemainid,String ScheduleId) throws Exception;
	public PmsEnote getPmsEnote(String EnoteId)throws Exception;
	public long addPmsEnote(PmsEnote pe)throws Exception;
	public long EnoteForward(PmsEnote pe, String remarks, Long empId, String flow,String Username)throws Exception;
	public List<Object[]> EnoteTransactionList(String enoteTrackId)throws Exception;
	public List<Object[]> eNotePendingList(long empId,String Type)throws Exception;
	public Object[] NewApprovalList(String enoteId)throws Exception;
	public List<Object[]> eNoteApprovalList(long empId, String fromDate, String toDate)throws Exception;
	public List<Object[]> EnotePrintDetails(long parseLong,String type)throws Exception;
	public List<Object[]> getAgendaAttachId(String agendaid)throws Exception;
	public long addAgendaLinkFile(CommitteeScheduleAgendaDocs docs)throws Exception;
	public List<Object[]> MomeNoteApprovalList(long empId, String fromDate, String toDate)throws Exception;
	
	public List<Object[]> carsScheduleList(String carsInitiationId) throws Exception;
	public void InvitationRoleoUpdate(String[] role, String[] empNo, String[] labCode, String userId,String []invitationid)throws Exception;


	public List<MeetingCheckDto> getMeetingCheckDto(String date, String committeemainid)throws Exception;
	public List<MeetingCheckDto> getMeetingCheckDto(String empid, String labocode,String scheduleid)throws Exception;
	
		/*  this method 
		 * will help to get all meeting list based on committee later 
		 * you can filter based on your need
		 */
	public List<Object[]> previousMeetingHeld(String committeeid)throws Exception  ;
	public List<Object[]> getRecommendationsOfCommittee(String committeeid)throws Exception  ;
	public List<Object[]> getDecisionsofCommittee(String committeeid)throws Exception  ;
	
	/* ********************************************* Programme AD ************************************************ */
	public List<ProgrammeMaster> getProgrammeMasterList()throws Exception;
	public Long getCommitteeMainIdByProgrammeId(String programmeId) throws Exception;
	public List<Object[]> prgmScheduleList(String programmeId) throws Exception;
	public List<ProgrammeProjects> getProgrammeProjectsList(String programmeId) throws Exception;
	public List<Object[]> prgmProjectList(String programmeId) throws Exception;
	public ProgrammeMaster getProgrammeMasterById(String programmeId) throws Exception;
	/* ********************************************* Programme AD End************************************************ */
	public long sentMomDraft(CommitteScheduleMinutesDraft cmd)throws Exception;
	public List<CommitteScheduleMinutesDraft> getCommitteScheduleMinutesDraftList()throws Exception;
	public List<Object[]> getdraftMomList(String empId, String pageSize)throws Exception;
	public long saveCommitteeSchedulesMomDraftRemarks(CommitteeSchedulesMomDraftRemarks cmd)throws Exception;
	public List<Object[]> getCommitteeSchedulesMomDraftRemarks(Long scheduleId)throws Exception;
	public List<Object[]> preProjectlist(String labCode)throws Exception;
	public FileRepUploadPreProject getPreProjectAgendaDocById(String filerepid)throws Exception;
	
	public Object[] carsCommitteeDescriptionTOR(String carsInitiationId, String committeeId) throws Exception;
	public CommitteeCARS getCommitteeCARSById(String comCARSInitiationId) throws Exception;
	public Long addCommitteeCARS(CommitteeCARS committeeCARS) throws Exception;
	
//	---------------------------------- Naveen R 3/9/25 MOM Check ------------------------------------------
	public List<Object[]> CommitteeScheduleMinutesforAction(String committeescheduleid) throws Exception;
	public Long addRepresentative(CommitteeRepresentative rep) throws Exception;
	public CommitteeRepresentative getRepresentativeById(String repId) throws Exception;
	public Long getRepNameDuplicateCount(String repName) throws Exception;
	public Long getRepCodeDuplicateCount(String repCode) throws Exception;
	public List<Object[]> getAircraftList(String committeeScheduleId) throws Exception;
	public List<Object[]> getSubSystemList(String committeeScheduleId) throws Exception;
	public Long addAircraft(CommitteeAircraft aircraft) throws Exception;
	public CommitteeAircraft getAircraftById(String aircraftId) throws Exception;
	public Long addSubSystem(CommitteeSubSystem sub) throws Exception;
	public CommitteeSubSystem getSubSystemById(String subsystemId) throws Exception;
	public List<Object[]> committeeScheduleMinutesforActionForMomADE(String committeescheduleid) throws Exception;
	public Object[] CommitteeScheduleEditDataforMom(String committeescheduleid) throws Exception;
	public List<Object[]> PrgmAgendaList(String scheduleid) throws Exception;

	// Naveen R 05-03-2026 
	public List<Object[]> getMeetingCountList(String committeeId) throws Exception;
	public List<Object[]> CommitteeScheduleMinutesMom(String committeescheduleid) throws Exception;
	

}

