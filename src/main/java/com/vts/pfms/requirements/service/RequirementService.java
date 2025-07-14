package com.vts.pfms.requirements.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vts.pfms.project.dto.PfmsInitiationRequirementDto;
import com.vts.pfms.project.model.PfmsInititationRequirement;
import com.vts.pfms.requirements.model.Abbreviations;
import com.vts.pfms.requirements.model.DocMembers;
import com.vts.pfms.requirements.model.DocumentFreeze;
import com.vts.pfms.requirements.model.PfmsReqTypes;
import com.vts.pfms.requirements.model.PfmsSystemSubIntroduction;
import com.vts.pfms.requirements.model.ReqDoc;
import com.vts.pfms.requirements.model.RequirementInitiation;
import com.vts.pfms.requirements.model.SpecificationMaster;
import com.vts.pfms.requirements.model.SpecificationTypes;
import com.vts.pfms.requirements.model.SpecsInitiation;
import com.vts.pfms.requirements.model.TestAcceptance;
import com.vts.pfms.requirements.model.TestApproach;
import com.vts.pfms.requirements.model.TestDetails;
import com.vts.pfms.requirements.model.TestInstrument;
import com.vts.pfms.requirements.model.TestPlanInitiation;
import com.vts.pfms.requirements.model.TestPlanMaster;
import com.vts.pfms.requirements.model.TestPlanSummary;
import com.vts.pfms.requirements.model.TestSetUpAttachment;
import com.vts.pfms.requirements.model.TestSetupMaster;
import com.vts.pfms.requirements.model.TestTools;
import com.vts.pfms.requirements.model.VerificationData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RequirementService {

	List<Object[]> RequirementList(String reqInitiationId)throws Exception;

	long ProjectRequirementAdd(PfmsInitiationRequirementDto prd, String userId, String labCode)throws Exception;

	public List<Object[]> AbbreviationDetails(String testPlanInitiationId, String specsInitiationId) throws Exception;

	public long AddDocMembers(DocMembers rm)throws Exception;
	public Object DocMemberList(String testPlanInitiationId, String specsInitiationId)throws Exception;
	public Object[] TestScopeIntro(String testPlanInitiationId) throws Exception;//method for intro
	public long TestScopeIntroSubmit(String testPlanInitiationId, String attributes, String details,String userId) throws Exception;
	public long TestScopeUpdate(String testPlanInitiationId, String attributes, String details,String userId)throws Exception;
	public long addTestPlanSummary(TestPlanSummary rs) throws Exception;
	public long editTestPlanSummary(TestPlanSummary rs) throws Exception;
	public Object getTestandSpecsDocumentSummary(String testPlanInitiationId, String specsInitiationId)throws Exception;
	public long addTestApproch(TestApproach rs)throws Exception;
	public long editTestApproach(TestApproach rs)throws Exception;
	public Object getApproach(String initiationId, String projectId)throws Exception;
	public long Update(String initiationId, String projectId, String attributes, String details,String userId) throws Exception;
	public Object[] GetTestContent(String initiationid, String ProjectId) throws Exception;
	public List<Object[]> GetTestContentList(String testPlanInitiationId) throws Exception;
	public long TestDocContentSubmit(String testPlanInitiationId, String attributes, String details,String userId)throws Exception;
	public long TestDocContentUpdate(String UpdateAction, String Details, String userId) throws Exception;
	public long insertTestAcceptanceFile(TestAcceptance re, String labCode)throws Exception;
	public 	List<Object[]> GetAcceptanceTestingList(String testPlanInitiationId)throws Exception;
	public long TestAcceptancetUpdate(String UpdateActionid, String Details, String userId, MultipartFile fileAttach,String LabCode) throws Exception;
	public Object[] AcceptanceTestingList(String testid)throws Exception;
	public Object[] AcceptanceTestingExcelData(String testPlanInitiationId) throws Exception;
	public long addAbbreviations(List<Abbreviations> iaList) throws Exception;


	List<Object[]> requirementTypeList(String reqInitiationId) throws Exception;

	long addPfmsInititationRequirement(PfmsInititationRequirement pir) throws Exception;

	long RequirementUpdate(PfmsInititationRequirement pir) throws Exception;

	List<Object[]> getReqMainList(String reqMainId) throws Exception;

	List<Object[]> getreqTypeList(String reqMainId, String initiationReqId) throws Exception;

	public List<Object[]> getVerificationMethodList()throws Exception;

	public List<Object[]> getProjectParaDetails(String reqInitiationId) throws Exception;

	public List<Object[]> getVerifications(String reqInitiationId) throws Exception;

	long UpdatePfmsInititationRequirement(PfmsInititationRequirement pir) throws Exception;

	List<Object[]> ApplicableDocumentList(String reqInitiationId)throws Exception;

	List<Object[]> ApplicableTotalDocumentList(String reqInitiationId)throws Exception;

	long addDocs(List<ReqDoc> list) throws Exception;
	
	public List<Object[]> productTreeListByProjectId(String projectId) throws Exception;
	public List<Object[]> initiationReqList(String projectId, String mainId, String initiationId) throws Exception;
	public List<Object[]> getPreProjectList(String loginType, String labcode, String empId) throws Exception;
	
	public long addRequirementInitiation(RequirementInitiation requirementInitiation) throws Exception;
	public RequirementInitiation getRequirementInitiationById(String reqInitiationId) throws Exception;
	public PfmsInititationRequirement getPfmsInititationRequirementById(String InitiationReqId) throws Exception;
	public Long addOrUpdatePfmsInititationRequirement(PfmsInititationRequirement pfmsInititationRequirement) throws Exception;
	public Long requirementInitiationAddHandling(String initiationId, String projectId, String productTreeMainId, String empId,String username, String version, String remarks) throws Exception;
	public List<Object[]> projectDocTransList(String docInitiationId, String docType) throws Exception;
	public long projectRequirementApprovalForward(String reqInitiationId, String action, String remarks, String empId, String labcode, String userId) throws Exception;
	public List<Object[]> projectRequirementPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> projectRequirementApprovedList(String empId, String FromDate, String ToDate) throws Exception;
	
	// Test Plan Changes from Bharath
	public long TestDetailsAdd(TestDetails td)throws Exception;
	public List<Object[]> TestTypeList()throws Exception;
	public List<Object[]> StagesApplicable()throws Exception;
	public Long numberOfTestTypeId(String testPlanInitiationId)throws Exception;
//	public long TestDetailasUpdate(TestDetails tdedit, String userId, String testId)throws Exception;
	public List<Object[]> TestSuiteList()throws Exception;
	public Object getVerificationMethodList(String projectId, String initiationid)throws Exception;
	public List<Object[]> TestDetailsList(String testPlanInitiationId)throws Exception;
	public List<Object[]> TestType(String r) throws Exception;
	public List<Object[]> getDocumentSummary(String testPlanInitiationId) throws Exception;
	public Long insertTestType(TestTools pt) throws Exception;
	public List<Object[]> initiationTestPlanList(String projectId, String mainId, String initiationId) throws Exception;

	// Test Plan Changes from Bharath End
	
	public Long testPlanInitiationAddHandling(String initiationId, String projectId, String productTreeMainId, String empId, String username, String version, String remarks) throws Exception;
	public TestPlanSummary getTestPlanSummaryById(String testPlanInitiationId) throws Exception;
	public TestPlanInitiation getTestPlanInitiationById(String testPlanInitiationId) throws Exception;
	public TestDetails getTestPlanDetailsById(String testId) throws Exception;
	public long projectTestPlanApprovalForward(String testPlanInitiationId, String action, String remarks, String empId, String labcode, String userId) throws Exception;
	public List<Object[]> projectTestPlanPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> projectTestPlanApprovedList(String empId, String FromDate, String ToDate) throws Exception;
	public int getDuplicateCountofTestType(String testType) throws Exception;
	public Object[] getTestPlanApprovalFlowData(String initiationId, String projectId, String productTreeMainId) throws Exception;
	public void testPlanPdfFreeze(HttpServletRequest req, HttpServletResponse res, String testPlanInitiationId, String labCode) throws Exception;
	public Long documentFreezeAddHandling(String docInitiationId, String docType, String pdfFilePath, String excelFilePath) throws Exception;
	public DocumentFreeze getDocumentFreezeByDocIdandDocType(String docInitiationId, String docType) throws Exception;
	public Long getFirstVersionTestPlanInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception;
	public Long getFirstVersionReqInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception;
	public void requirementPdfFreeze(HttpServletRequest req, HttpServletResponse res, String reqInitiationId, String labCode) throws Exception;
	public Object[] getRequirementApprovalFlowData(String initiationId, String projectId, String productTreeMainId)throws Exception;
	public Long getFirstVersionSpecsInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception;

	//specification starts
	public SpecsInitiation getSpecsInitiationById(String specsInitiationId) throws Exception;
	public long SpecificationInitiationAddHandling(String initiationId, String projectId, String productTreeMainId,String empId, String userId,String version,String remarks) throws Exception;
	public List<Object[]> getSpecsList(String specsInitiationId) throws Exception;
	public List<Object[]> getSpecsPlanApprovalFlowData(String projectId, String initationId, String productTreeMainId)throws Exception;
	public long projectSpecsApprovalForward(String specsInitiationId, String action, String remarks, String empId,String labcode, String userId)throws Exception;
	public List<Object[]> projectSpecificationPendingList(String empId, String labcode)throws Exception;
	public List<Object[]> projectSpecificationApprovedList(String empId, String fromdate, String todate)throws Exception;
	public void SpecsInitiationPdfFreeze(HttpServletRequest req, HttpServletResponse resp, String specsInitiationId, String labcode)throws Exception;
	public List<Object[]> getAllSqr(String reqInitiationId)throws Exception;
	public long AddReqType(PfmsReqTypes pr)throws Exception;
	public long deleteSqr(String paraId)throws Exception;
	public long updateSerialParaNo(String para, String sn)throws Exception;
	public long deleteInitiationReq(String InitiationReqId) throws Exception;
	public long deleteInitiationSpe(String SpecsId) throws Exception;
	public long addSpecMaster(String[] specificationCode, String[] specificationName)throws Exception;
	public List<Object[]> getSpecMasterList(String SpecsInitiationId)throws Exception;
	public Object[] getSpecName(String mainId)throws Exception;
	public long addTestMaster(String[] testplanCode, String[] testplanName)throws Exception;
	public List<Object[]> getTestPlanMainList(String testPlanInitiationId)throws Exception;
	public Object[] getTestTypeName(String mainid)throws Exception;
	public long deleteTestPlan(String testId)throws Exception;

	/* Soumya kanta Swain */
	public List<Object[]> getVerificationListMaster()throws Exception;
	public long addVerificationData(List<VerificationData> verifyList)throws Exception;
	public List<Object[]>getverificationDataList(String verificationId)throws Exception;
	public long verificationDataEdit(VerificationData verifiData)throws Exception;
	public List<Object[]> SpecificationMasterList() throws Exception;
	public long specMasterAddSubmit(SpecificationMaster sp)throws Exception;
	public SpecificationMaster getSpecificationMasterById(long SpecsMasterId)throws Exception;
	public List<Object[]> TestPlanMaster()throws Exception;
	public TestPlanMaster getTestPlanById(long TestMasterId)throws Exception;
	public long testPlanMasterAdd(TestPlanMaster tp)throws Exception;

	/* ********************************* IGI / ICD / IRS / IDD DOCUMENT DOCUMENT ********************************** */
	public List<Object[]> igiDocumentPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> igiDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception;
	public List<Object[]> icdDocumentPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> icdDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception;
	public List<Object[]> irsDocumentPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> irsDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception;
	public List<Object[]> iddDocumentPendingList(String empId, String labcode) throws Exception;
	public List<Object[]> iddDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception;

	/* *************************************** IGI / ICD / IRS / IDD DOCUMENT END ********************************* */
	
	public List<Object[]> productTreeListByInitiationId(String initiationId)throws Exception;

	public List<TestPlanMaster> getAllTestPlans()throws Exception;

	public List<SpecificationMaster> getAllSpecPlans()throws Exception;
	public int deleteSpecificationMasterById(String specsMasterId);
	public List<SpecificationTypes> getSpecificationTypesList();

	public List<TestSetupMaster> getTestSetupMaster()throws Exception;

	public List<TestInstrument> getTestInstrument()throws Exception;

	public long setTestInstrument(TestInstrument t)throws Exception;

	public long addTestSetupMaster(TestSetupMaster tp)throws Exception;

	public TestSetupMaster getTestSetupMasterById(Long setUpid)throws Exception;

	public long saveTestSetUpAttachment(MultipartFile[] attchments, long setUpId, String labCode)throws Exception;

	public List<TestSetUpAttachment> getTestSetUpAttachment(String setUpId)throws Exception;

	public long saveTestSetUpAttachement(TestSetUpAttachment tp)throws Exception;

	public List<PfmsSystemSubIntroduction> getActiveSubIntroductionByMainId(Long MainId)throws Exception;

	long savePfmsSystemSubIntroduction(PfmsSystemSubIntroduction ps)throws Exception;

	public int setMilestoneInActive(String id)throws Exception;
}
