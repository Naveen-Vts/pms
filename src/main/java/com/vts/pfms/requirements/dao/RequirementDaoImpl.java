package com.vts.pfms.requirements.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.project.model.PfmsInititationRequirement;
import com.vts.pfms.project.model.RequirementSummary;
import com.vts.pfms.requirements.model.Abbreviations;
import com.vts.pfms.requirements.model.DocMembers;
import com.vts.pfms.requirements.model.DocumentFreeze;
import com.vts.pfms.requirements.model.DocumentTrans;
import com.vts.pfms.requirements.model.PfmsReqTypes;
import com.vts.pfms.requirements.model.PfmsSpecTypes;
import com.vts.pfms.requirements.model.PfmsSystemSubIntroduction;
import com.vts.pfms.requirements.model.PfmsTestTypes;
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
import com.vts.pfms.requirements.model.TestPlanMaster ;
import com.vts.pfms.requirements.model.TestPlanSummary;
import com.vts.pfms.requirements.model.TestScopeIntro;
import com.vts.pfms.requirements.model.TestSetUpAttachment;
import com.vts.pfms.requirements.model.TestSetupMaster;
import com.vts.pfms.requirements.model.TestTools;
import com.vts.pfms.requirements.model.VerificationData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
@Transactional
@Repository
public class RequirementDaoImpl implements RequirementDao {
	private static final Logger logger=LogManager.getLogger(RequirementDaoImpl.class);
	java.util.Date loggerdate=new java.util.Date();

	@PersistenceContext
	EntityManager manager;


	private static final String REQLIST="SELECT a.InitiationReqId, a.requirementid,a.reqtypeid,a.requirementbrief,a.requirementdesc,a.priority,a.needtype,a.remarks,a.category,a.constraints,a.linkedrequirements,a.linkedDocuments,a.linkedPara,'0',a.ReqMainId,a.ParentId,a.Demonstration,a.Test,a.Analysis,a.Inspection,a.SpecialMethods,a.Criticality,SUBSTRING_INDEX(a.requirementid, '_', -1) AS 'requirement_number',a.LinkedSubSystem,TestStage,Derivedtype FROM pfms_initiation_req a WHERE ReqInitiationId=:ReqInitiationId AND isActive='1' ORDER BY ParentId,requirement_number";
	@Override
	public List<Object[]> RequirementList(String reqInitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(REQLIST);
		query.setParameter("ReqInitiationId", reqInitiationId);
		List<Object[]> RequirementList=(List<Object[]> )query.getResultList();	
		return RequirementList;
	}

	private static final String SPECIFICATIONMASTERLIST="SELECT a.SpecsMasterId, a.SpecificationName,a.Description, a.SpecsParameter, a.SpecsUnit, a.SpecsInitiationId, a.SpecValue,\r\n"
			+ "CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.emp_name) AS 'empname',\r\n"
			+ "a.CreatedDate, a.ModifiedBy, a.ModifiedDate, a.IsActive,a.sid,a.mainid,a.ParentId,a.maximumValue,a.minimumValue,a.specCount,d.SpecTypeCode, a.SpecTypeId, d.SpecType \r\n"
			+ "FROM pfms_specification_master a\r\n"
			+ "JOIN login b ON a.CreatedBy=b.UserName\r\n"
			+ "JOIN employee c ON b.empid=c.emp_id \r\n"
			+ "LEFT JOIN pfms_spec_types d ON d.SpecTypeId = a.SpecTypeId\r\n"
			+ "WHERE a.IsActive = '1' ORDER BY a.MainId,a.SpecTypeId";
	@Override
	public List<Object[]> SpecificationMasterList() throws Exception 
	{
		List<Object[]> SpecificarionMasterList=null;
		try {
			Query query=manager.createNativeQuery(SPECIFICATIONMASTERLIST);
			SpecificarionMasterList =(List<Object[]>) query.getResultList();
			return SpecificarionMasterList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	private static final String ABBREVIATIONS="SELECT AbbreviationsId,Abbreviations,Meaning FROM pfms_abbreviations WHERE TestPlanInitiationId=:TestPlanInitiationId AND SpecsInitiationId=:SpecsInitiationId";		
	@Override
	public List<Object[]> AbbreviationDetails(String testPlanInitiationId, String specsInitiationId) throws Exception {
		Query query = manager.createNativeQuery(ABBREVIATIONS);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		query.setParameter("SpecsInitiationId", specsInitiationId);
		return (List<Object[]>)query.getResultList();
	}
	@Override
	public long addAbbreviations(List<Abbreviations> iaList) throws Exception {
		Long l=0l;
		for(Abbreviations ia:iaList) {
			manager.persist(ia);
			l++;
		}
		return l;
	}
	@Override
	public long AddreqMembers(DocMembers r) throws Exception {

		manager.persist(r);
		manager.flush();

		return r.getMemeberId();
	}
	private static final String DOCMEMLIST = " SELECT a.empid,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.empname) AS 'empname' ,b.designation,a.labcode,b.desigid,c.MemeberId FROM employee a,employee_desig b,pfms_doc_members c WHERE a.isactive='1' AND a.DesigId=b.DesigId AND  a.empid = c.empid AND c.TestPlanInitiationId =:TestPlanInitiationId AND c.SpecsInitiationId=:SpecsInitiationId AND c.isactive =1 ORDER BY b.desigid ASC";

	@Override
	public List<Object[]> DocMemberList(String testPlanInitiationId, String specsInitiationId) throws Exception {

		Query query = manager.createNativeQuery(DOCMEMLIST);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		query.setParameter("SpecsInitiationId", specsInitiationId);
		return (List<Object[]>)query.getResultList();
	}
	private static final String TESTINTRO="SELECT TestPlanInitiationId,Introduction,SystemIdentification,SystemOverview FROM pfms_test_scope_intro WHERE TestPlanInitiationId=:TestPlanInitiationId AND isactive=1";
	@Override
	public Object[] TestScopeIntro(String testPlanInitiationId) throws Exception {
		Query query=manager.createNativeQuery(TESTINTRO);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		Object[]ReqIntro=null;
		try {
			ReqIntro=(Object[])query.getSingleResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ReqIntro;
	}
	@Override
	public long TestScopeIntroSubmit(TestScopeIntro pr) throws Exception {
		manager.persist(pr);
		manager.flush();
		return pr.getIntroId();

	}
	@Override
	public long TestScopeUpdate(TestScopeIntro pr, String details) throws Exception {
		System.out.println(details);
		if(details.equalsIgnoreCase("Introduction")) {
			System.out.println("a");
			String INTROUPDATE="UPDATE pfms_test_scope_intro SET Introduction=:Introduction,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE TestPlanInitiationId=:TestPlanInitiationId";
			Query query=manager.createNativeQuery(INTROUPDATE);
			query.setParameter("Introduction", pr.getIntroduction());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
			query.setParameter("TestPlanInitiationId", pr.getTestPlanInitiationId());
			return query.executeUpdate();

		}else if(details.equalsIgnoreCase("System Identification")) {
			System.out.println("a");
			String BLOCKUPDTE="UPDATE pfms_test_scope_intro SET SystemIdentification=:SystemIdentification, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE TestPlanInitiationId=:TestPlanInitiationId";
			Query query=manager.createNativeQuery(BLOCKUPDTE);
			query.setParameter("SystemIdentification", pr.getSystemIdentification());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
			query.setParameter("TestPlanInitiationId", pr.getTestPlanInitiationId());
			return query.executeUpdate();	

		}else if(details.equalsIgnoreCase("System Overview")) {
			System.out.println("a");
			String SYSUPDATE="UPDATE pfms_test_scope_intro SET SystemOverview=:SystemOverview, ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE TestPlanInitiationId=:TestPlanInitiationId";
			Query query=manager.createNativeQuery(SYSUPDATE);
			query.setParameter("SystemOverview", pr.getSystemOverview());
			query.setParameter("ModifiedBy", pr.getModifiedBy());
			query.setParameter("ModifiedDate", pr.getModifiedDate());
			query.setParameter("TestPlanInitiationId", pr.getTestPlanInitiationId());
			return query.executeUpdate();

		}

		return 0;
	}
	@Override
	public long addTestPlanSummary(TestPlanSummary rs) throws Exception {
		manager.persist(rs);
		return rs.getSummaryId();
	}
	private static final String DOCSUMUPD="UPDATE pfms_test_plan_summary SET AdditionalInformation=:AdditionalInformation,Abstract=:Abstract,Keywords=:Keywords,Distribution=:Distribution , reviewer=:reviewer,approver=:approver,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE SummaryId=:SummaryId AND isactive='1'";
	@Override
	public long editTestPlanSummary(TestPlanSummary rs) throws Exception {
		Query query = manager.createNativeQuery(DOCSUMUPD);
		query.setParameter("AdditionalInformation", rs.getAdditionalInformation());			
		query.setParameter("Abstract", rs.getAbstract());			
		query.setParameter("Keywords", rs.getKeywords());			
		query.setParameter("Distribution", rs.getDistribution());			
		query.setParameter("reviewer", rs.getReviewer());			
		query.setParameter("approver", rs.getApprover());			
		query.setParameter("ModifiedBy", rs.getModifiedBy());			
		query.setParameter("ModifiedDate", rs.getModifiedDate());			
		query.setParameter("SummaryId", rs.getSummaryId());	


		return query.executeUpdate();
	}
	private static final String DOCSUM="SELECT a.AdditionalInformation,a.Abstract,a.Keywords,a.Distribution,a.reviewer,a.approver,(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname),', ', d.designation)FROM employee e,employee_desig d WHERE  e.desigid=d.desigid AND e.empid=a.approver  ) AS 'Approver1',(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname),',', d.designation)FROM employee e,employee_desig d WHERE  e.desigid=d.desigid AND e.empid=a.reviewer) AS 'Reviewer1',a.summaryid,a.preparedby,(SELECT CONCAT(CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname),',', d.designation)FROM employee e,employee_desig d WHERE  e.desigid=d.desigid AND e.empid=a.PreparedBy) AS 'PreparedBy1',a.ReleaseDate,a.DocumentNo FROM pfms_test_plan_summary a WHERE a.TestPlanInitiationId =:TestPlanInitiationId AND a.SpecsInitiationId=:SpecsInitiationId AND a.isactive='1'";
	@Override
	public List<Object[]> getTestandSpecsDocumentSummary(String testPlanInitiationId, String specsInitiationId) throws Exception {

		Query query = manager.createNativeQuery(DOCSUM);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		query.setParameter("SpecsInitiationId", specsInitiationId);
		List<Object[]>DocumentSummary=(List<Object[]>)query.getResultList();

		return DocumentSummary;
	}

	/*
	 * @Override public long addTestApproch(TestApproach pr) throws Exception {
	 * manager.persist(pr); manager.flush(); return pr.getTestApproachId();
	 * 
	 * }
	 */
	/*
	 * private static final String
	 * TESTAPPROACH="UPDATE pfms_test_plan_approach SET TestApproach=:TestApproach,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE TestApproachId=:TestApproachId AND isactive='1'"
	 * ;
	 * 
	 * @Override public long editTestApproach(TestApproach rs) throws Exception {
	 * Query query = manager.createNativeQuery(TESTAPPROACH);
	 * query.setParameter("TestApproach", rs.getTestApproach());
	 * query.setParameter("ModifiedBy", rs.getModifiedBy());
	 * query.setParameter("ModifiedDate", rs.getModifiedDate());
	 * query.setParameter("TestApproachId", rs.getTestApproachId()); return
	 * query.executeUpdate(); }
	 */
	private static final String GETTESTAPPROACH="SELECT a.TestId,a.PointDetails FROM pfms_test_plan_contents a WHERE a.InitiationId =:InitiationId AND ProjectId =:ProjectId AND a.isactive='1' ";
	@Override
	public List<Object[]> getApproach(String initiationid, String ProjectId) throws Exception {

		Query query = manager.createNativeQuery(GETTESTAPPROACH);
		query.setParameter("InitiationId", initiationid);
		query.setParameter("ProjectId", ProjectId);
		List<Object[]>DocumentSummary=(List<Object[]>)query.getResultList();
		return DocumentSummary;
	}
	@Override
	public long TestDocContentSubmit(TestApproach pr) throws Exception {
		manager.persist(pr);
		manager.flush();
		return pr.getTestId();

	}
	@Override
	public long addTestApproch(TestApproach rs) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long editTestApproach(TestApproach rs) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	private static final String TESTCONTENT="SELECT PointName,PointDetails FROM pfms_test_plan_contents WHERE initiationid=:initiationid AND ProjectId=:ProjectId AND isactive=1";
	@Override
	public Object[] GetTestContent(String initiationid, String ProjectId) throws Exception {
		Query query=manager.createNativeQuery(TESTCONTENT);
		query.setParameter("initiationid", initiationid);
		query.setParameter("ProjectId", ProjectId);
		Object[]ReqIntro=null;
		try {
			ReqIntro=(Object[])query.getSingleResult();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ReqIntro;
	}
	private static final String TESTCONTENTLIST="SELECT PointName,PointDetails,TestId FROM pfms_test_plan_contents WHERE TestPlanInitiationId=:TestPlanInitiationId AND isactive=1";
	@Override
	public List<Object[]> GetTestContentList(String testPlanInitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery(TESTCONTENTLIST);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		List<Object[]>GetContentList=(List<Object[]>)query.getResultList();
		return GetContentList;
	}
	@Override
	public long TestContentUpdate(TestApproach pr, String details) throws Exception {
		String CONTENTUPDATE="UPDATE pfms_test_plan_contents SET PointDetails=:PointDetails, ModifiedBy=:ModifiedBy , ModifiedDate=:ModifiedDate WHERE TestId=:TestId";
		Query query=manager.createNativeQuery(CONTENTUPDATE);
		query.setParameter("TestId", pr.getTestId());
		query.setParameter("PointDetails", pr.getPointDetails());
		query.setParameter("ModifiedBy", pr.getModifiedBy());
		query.setParameter("ModifiedDate", pr.getModifiedDate());

		return query.executeUpdate();
	}
	@Override
	public long TestDocContentUpdate(String initiationid, String projectId, String attributes, String details,
			String userId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public long TestDocContentUpdate(TestApproach pr, String attributes) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insertTestAcceptanceFile(TestAcceptance re) throws Exception {
		manager.persist(re);
		manager.flush();
		return re.getTestId();
	}
	private static final String ACCEPTANCETESTING="SELECT TestId,Attributes,AttributesDetailas,FileName,FilePath FROM pfms_test_plan_acceptance WHERE TestPlanInitiationId=:TestPlanInitiationId AND isactive=1";
	@Override
	public List<Object[]> GetAcceptanceTestingList(String testPlanInitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery(ACCEPTANCETESTING);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		List<Object[]>GetContentList=(List<Object[]>)query.getResultList();
		return GetContentList;
	}

	@Override
	public long AcceptanceUpdate(TestAcceptance pr, String details) throws Exception {
		String fileName = pr.getFileName();
		System.out.println("filename"+fileName);
		String INTROUPDATE="UPDATE pfms_test_plan_acceptance SET AttributesDetailas=:AttributesDetailas, FileName=:FileName,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE TestId=:TestId  ";
		Query query=manager.createNativeQuery(INTROUPDATE);
		query.setParameter("AttributesDetailas", pr.getAttributesDetailas());
		query.setParameter("FileName", fileName);
		query.setParameter("ModifiedBy", pr.getModifiedBy());
		query.setParameter("ModifiedDate", pr.getModifiedDate());
		query.setParameter("TestId", pr.getTestId());
		return query.executeUpdate();
	}

	private static final String ACCEPETANCEDATA  ="SELECT TestId,Attributes,AttributesDetailas,FileName,FilePath FROM pfms_test_plan_acceptance WHERE TestId=:TestId";
	@Override
	public Object[] AcceptanceTestingList(String Testid) throws Exception
	{

		Query query=manager.createNativeQuery(ACCEPETANCEDATA);
		query.setParameter("Testid",Testid);
		Object[] ProjectDataSpecsFileData=(Object[])query.getSingleResult();
		return ProjectDataSpecsFileData;
	}
	private static final String EXCELDATA="SELECT TestId,Attributes,AttributesDetailas,FileName,FilePath FROM pfms_test_plan_acceptance WHERE InitiationId =:InitiationId AND ProjectId=:ProjectId AND isactive=1";
	@Override
	public Object[] AcceptanceTestingExcelData(String testPlanInitiationId) throws Exception {
		Query query = manager.createNativeQuery(EXCELDATA);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		Object[] result= (Object[])query.getSingleResult();
		return result;
	}


	private final String REQTYPELIST="SELECT RequirementId, ReqName , ReqParentId, ReqCode FROM pfms_req_types a WHERE ReqParentId= '0' AND RequirementId NOT IN (SELECT reqmainid FROM pfms_initiation_req WHERE parentid='0' AND isactive ='1' AND ReqInitiationId=:ReqInitiationId)";
	@Override
	public List<Object[]> requirementTypeList(String reqInitiationId) throws Exception{

		Query query = manager.createNativeQuery(REQTYPELIST);
		query.setParameter("ReqInitiationId", reqInitiationId);

		return (List<Object[]>)query.getResultList();
	}

	private static final String REQUPDATE = "UPDATE pfms_initiation_req SET RequirementDesc=:RequirementDesc,modifiedby=:modifiedby,modifieddate=:modifieddate,priority=:priority,NeedType=:needtype WHERE initiationreqid=:initiationreqid AND isActive=1";
	@Override
	public long RequirementUpdate(PfmsInititationRequirement pir) throws Exception {

		Query query = manager.createNativeQuery(REQUPDATE);
		query.setParameter("RequirementDesc",pir.getRequirementDesc() );
		query.setParameter("modifiedby",pir.getModifiedBy() );
		query.setParameter("modifieddate", pir.getModifiedDate());
		query.setParameter("priority", pir.getPriority());
		query.setParameter("needtype", pir.getNeedType());
		query.setParameter("initiationreqid", pir.getInitiationReqId());
		return query.executeUpdate();
	}

	private static final String REQMAINLIST="SELECT RequirementId,ReqName,ReqParentId,ReqCode FROM pfms_req_types WHERE RequirementId =:reqMainId UNION SELECT RequirementId,ReqName,ReqParentId,ReqCode FROM pfms_req_types WHERE ReqParentId =:reqMainId";
	@Override
	public List<Object[]> getReqMainList(String reqMainId) throws Exception {
		Query query = manager.createNativeQuery(REQMAINLIST);
		query.setParameter("reqMainId", reqMainId);

		return (List<Object[]>)query.getResultList();
	}
	private static final String REQTYPE="SELECT InitiationReqId,RequirementId FROM pfms_initiation_req WHERE ReqMainId =:reqMainId AND ParentId =:initiationReqId AND isactive='1'";
	@Override
	public List<Object[]> getreqTypeList(String reqMainId, String initiationReqId) throws Exception {

		Query query = manager.createNativeQuery(REQTYPE);
		query.setParameter("reqMainId", reqMainId);
		query.setParameter("initiationReqId", initiationReqId);

		List<Object[]>getreqTypeList = new ArrayList<>();
		try {
			getreqTypeList=(List<Object[]>)query.getResultList();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return getreqTypeList;
	}

	private static final String METHODLIST="SELECT a.VerificationDataId,b.VerificationMasterId,b.VerificationName,a.TypeofTest,a.Purpose FROM pfms_initiation_verification_data a,pfms_initiation_verification_master b WHERE a.VerificationMasterId=b.VerificationMasterId AND a.IsActive='1'";
	@Override
	public List<Object[]> getVerificationMethodList() throws Exception {
		try {
			Query query = manager.createNativeQuery(METHODLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	private static final String PARADETAILSMAIN="SELECT paraid,sqrid,'0',parano,paradetails FROM pfms_initiation_sqr_para WHERE ReqInitiationId=:ReqInitiationId AND isactive=1 order by SINo";
	@Override
	public List<Object[]> getProjectParaDetails(String reqInitiationId) throws Exception {

		Query query = manager.createNativeQuery(PARADETAILSMAIN);
		query.setParameter("ReqInitiationId", reqInitiationId);
		List<Object[]>list= new ArrayList<>();
		try {
			list = (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	private static final String VERIFICATIONLIST="select VerificationId,Provisions,ReqInitiationId,ProvisionsDetails from pfms_initiation_verification where ReqInitiationId=:ReqInitiationId and isactive='1'";
	@Override
	public List<Object[]> getVerifications(String reqInitiationId) throws Exception {

		Query query=manager.createNativeQuery(VERIFICATIONLIST);
		query.setParameter("ReqInitiationId", reqInitiationId);
		List<Object[]> verificationList=(List<Object[]>)query.getResultList();
		return verificationList;
	}

	private static final String REQEDITDATA="UPDATE pfms_initiation_req SET requirementdesc=:requirementdesc,priority=:priority,needtype=:needtype,remarks=:remarks,Constraints=:Constraints,linkedPara=:linkedPara,Demonstration=:Demonstration,Test=:Test,Analysis=:Analysis,Inspection=:Inspection,SpecialMethods=:SpecialMethods,criticality=:criticality,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE InitiationReqId=:InitiationReqId";
	@Override
	public long UpdatePfmsInititationRequirement(PfmsInititationRequirement pir) throws Exception {
		Query query=manager.createNativeQuery(REQEDITDATA);
		query.setParameter("requirementdesc",pir.getRequirementDesc());
		query.setParameter("priority",pir.getPriority());
		query.setParameter("needtype",pir.getNeedType());
		query.setParameter("remarks",pir.getRemarks());
		query.setParameter("Constraints",pir.getConstraints());
		query.setParameter("linkedPara",pir.getLinkedPara());
		query.setParameter("Demonstration",pir.getDemonstration());
		query.setParameter("Test",pir.getTest());
		query.setParameter("Analysis",pir.getAnalysis());
		query.setParameter("Inspection",pir.getInspection());
		query.setParameter("SpecialMethods",pir.getSpecialMethods());
		query.setParameter("criticality",pir.getCriticality());
		query.setParameter("ModifiedBy",pir.getModifiedBy());
		query.setParameter("ModifiedDate",pir.getModifiedDate());
		query.setParameter("InitiationReqId",pir.getInitiationReqId());
		return query.executeUpdate();
	}
	private static final String APPDOCDATA="SELECT docid,DocumentName FROM project_req_doc WHERE docid NOT IN  (SELECT docid FROM pfms_req_initiation_linkeddocs WHERE ReqInitiationId=:ReqInitiationId AND isactive='1')";
	@Override
	public List<Object[]> ApplicableDocumentList(String reqInitiationId) throws Exception {
		// TODO Auto-generated method stub
		Query query=manager.createNativeQuery(APPDOCDATA);
		query.setParameter("ReqInitiationId",reqInitiationId);
		return (List<Object[]>)query.getResultList();
	}
	private static final String APPDOCDATAS="SELECT a.LinkedId,b.DocumentName FROM pfms_req_initiation_linkeddocs a , project_req_doc b WHERE a.ReqInitiationId=:ReqInitiationId AND a.docid=b.docId AND isactive='1'";
	@Override
	public List<Object[]> ApplicableTotalDocumentList(String reqInitiationId) throws Exception {
		Query query=manager.createNativeQuery(APPDOCDATAS);
		query.setParameter("ReqInitiationId",reqInitiationId);
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long addDocs(List<ReqDoc> list) throws Exception {

		long count=0;
		for(ReqDoc rc:list) {
			manager.persist(rc);
			manager.flush();
			count++;
		}

		return count;
	}

	private static final String PRODUCTTREELISTBYPROJECTID = "SELECT a.MainId, a.SubLevelId, a.LevelName, a.Stage, a.Module, a.RevisionNo, a.SystemMainId, a.LevelCode FROM pfms_product_tree a,project_master b WHERE a.MainId>0 AND a.ProjectId=b.project_id AND b.project_id=:ProjectId AND a.IsActive='1' AND a.LevelId < 3 ORDER BY a.SubLevelId";
	@Override
	public List<Object[]> productTreeListByProjectId(String projectId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(PRODUCTTREELISTBYPROJECTID);
			query.setParameter("ProjectId", projectId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String INITIATIONREQLIST = "SELECT a.ReqInitiationId,a.ProjectId,a.InitiationId,a.ProductTreeMainId,a.InitiatedBy,a.InitiatedDate,b.emp_name,c.Designation,a.ReqVersion,d.ReqStatusCode,d.ReqStatus,d.ReqStatusColor FROM pfms_req_initiation a,employee b,employee_desig c,pfms_req_approval_status d WHERE a.IsActive=1 AND a.InitiatedBy=b.emp_id AND b.desig_id=c.desig_id AND a.ReqStatusCode=d.ReqStatusCode AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.InitiationId=:InitiationId ORDER BY a.ReqInitiationId DESC";
	@Override
	public List<Object[]> initiationReqList(String projectId, String mainId, String initiationId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(INITIATIONREQLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", mainId);
			query.setParameter("InitiationId", initiationId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String GETPREPROJECTLIST = "SELECT a.InitiationId,a.ProjectProgramme,a.ProjectShortName,a.ProjectTitle,a.EmpId FROM pfms_initiation a WHERE a.IsActive AND a.LabCode=:LabCode AND (CASE WHEN :LoginType IN ('A','Z','E','L') THEN 1=1 ELSE a.EmpId=:EmpId END)";
	@Override
	public List<Object[]> getPreProjectList(String loginType,String labcode, String empId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPREPROJECTLIST);
			query.setParameter("LoginType", loginType);
			query.setParameter("LabCode", labcode);
			query.setParameter("EmpId", empId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getPreProjectList "+e);
			return new ArrayList<>();
		}
	}

	@Override
	public long addRequirementInitiation(RequirementInitiation requirementInitiation) throws Exception {
		try {
			manager.persist(requirementInitiation);
			manager.flush();
			return requirementInitiation.getReqInitiationId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addRequirementInitiation "+e);
			return 0L;
		}
	}

	@Override
	public RequirementInitiation getRequirementInitiationById(String reqInitiationId) throws Exception{
		try {
			return manager.find(RequirementInitiation.class, Long.parseLong(reqInitiationId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRequirementInitiationById "+e);
			return null;
		}
	}

	@Override
	public PfmsInititationRequirement getPfmsInititationRequirementById(String InitiationReqId) throws Exception{
		try {
			return manager.find(PfmsInititationRequirement.class, Long.parseLong(InitiationReqId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getPfmsInititationRequirementById "+e);
			return null;
		}
	}

	@Override
	public Long addOrUpdatePfmsInititationRequirement(PfmsInititationRequirement pfmsInititationRequirement) throws Exception{
		try {
			manager.persist(pfmsInititationRequirement);
			manager.flush();
			return pfmsInititationRequirement.getInitiationReqId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addOrUpdatePfmsInititationRequirement "+e);
			return 0L;
		}
	}

	private static final String ROADMAPTRANSLIST = "SELECT a.DocInitiationTransId,c.EmpNo,c.EmpName,d.Designation,a.ActionDate,a.Remarks,b.ReqStatus,b.ReqStatusColor FROM pfms_doc_trans a,pfms_req_approval_status b,employee c,employee_desig d WHERE  a.ReqStatusCode = b.ReqStatusCode AND a.ActionBy=c.EmpId AND c.DesigId = d.DesigId AND a.DocInitiationId=:DocInitiationId AND a.DocType=:DocType ORDER BY a.DocInitiationTransId ";
	@Override
	public List<Object[]> projectDocTransList(String docInitiationId, String docType) throws Exception {

		try {
			Query query = manager.createNativeQuery(ROADMAPTRANSLIST);
			query.setParameter("DocInitiationId",docInitiationId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO projectDocTransList "+e);
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public long addRequirementTransaction(DocumentTrans transaction) throws Exception {
		try {
			manager.persist(transaction);
			manager.flush();
			return transaction.getDocInitiationTransId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addRequirementTransaction "+e);
			return 0L;
		}
	}

	@Override
	public RequirementSummary getRequirementSummaryByReqInitiationId(String reqInitiationId) throws Exception{
		try {
			Query query = manager.createQuery("FROM RequirementSummary WHERE IsActive=1 AND ReqInitiationId=:ReqInitiationId");
			query.setParameter("ReqInitiationId", Long.parseLong(reqInitiationId));
			return (RequirementSummary)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRequirementSummaryByReqInitiationId "+e);
			return null;
		}
	}

	private static final String PROJECTREQUIREMENTPENDINGLIST  ="CALL pfms_req_doc_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> projectRequirementPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(PROJECTREQUIREMENTPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectRequirementPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String PROJECTREQUIREMENTAPPROVEDLIST  ="CALL pfms_req_doc_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> projectRequirementApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(PROJECTREQUIREMENTAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectRequirementApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	// Test Plan Changes from Bharath
	@Override
	public long TestDetailsAdd(TestDetails Td) throws Exception {

		manager.persist(Td);
		manager.flush();
		return Td.getTestId();
	}

	private static final String TESTTYPELIST="SELECT a.TestToolsId,a.TestType,a.TestTools,a.TestSetupName FROM pfms_test_plan_testingtools a";
	@Override
	public List<Object[]> TestTypeList() throws Exception {
		Query query=manager.createNativeQuery(TESTTYPELIST);
		return (List<Object[]>)query.getResultList();
	}

	private static final String STAGESAPPLICABLE="SELECT a.Id, '0' AS InitiationId,'0' AS ProjectId,stagesapplicableName FROM pfms_test_plan_stagesapplicable a WHERE a.IsActive=1";
	@Override
	public List<Object[]> StagesApplicable() throws Exception {
		Query query=manager.createNativeQuery(STAGESAPPLICABLE);
		List<Object[]>requirementFiles=(List<Object[]>)query.getResultList();
		return requirementFiles;
	}

	private static final String TESTTYPECOUNT="SELECT MAX(TestCount) FROM pfms_testdetails WHERE TestPlanInitiationId=:TestPlanInitiationId AND isactive='1'";
	@Override
	public long numberOfTestTypeId(String testPlanInitiationId) throws Exception {

		Query query=manager.createNativeQuery(TESTTYPECOUNT);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		Long x=(Long)query.getSingleResult();

		if(x==null) {
			return 0;
		}
		return x.longValue();
	}

	//	private static final String TESTUPDATE="UPDATE  pfms_testdetails SET Name=:Name, Objective=:Objective,Description=:Description,PreConditions=:PreConditions,PostConditions=:PostConditions,Constraints=:Constraints,SafetyRequirements=:SafetyRequirements,Methodology=:Methodology,ToolsSetup=:ToolsSetup,PersonnelResources=:PersonnelResources,EstimatedTimeIteration=:EstimatedTimeIteration,Iterations=:Iterations,Schedule=:Schedule,StageApplicable=:StageApplicable,Pass_Fail_Criteria=:Pass_Fail_Criteria,Remarks=:Remarks,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate,IsActive='1' WHERE TestId=:TestId";
	//	@Override
	//	public long TestDUpdate(TestDetails pir, String TestId) throws Exception {
	//
	//		Query query=manager.createNativeQuery(TESTUPDATE);
	//		query.setParameter("Name", pir.getName());
	//		query.setParameter("Objective", pir.getObjective());
	//		query.setParameter("Description", pir.getDescription());
	//		query.setParameter("PreConditions", pir.getPreConditions());
	//		query.setParameter("PostConditions", pir.getPostConditions());
	//		query.setParameter("Constraints", pir.getConstraints());
	//		query.setParameter("SafetyRequirements", pir.getSafetyRequirements());
	//		query.setParameter("Methodology", pir.getMethodology());
	//		query.setParameter("ToolsSetup", pir.getToolsSetup());
	//		query.setParameter("PersonnelResources", pir.getPersonnelResources());
	//		query.setParameter("EstimatedTimeIteration", pir.getEstimatedTimeIteration());
	//		query.setParameter("Iterations",pir.getIterations() );
	//		query.setParameter("Schedule", pir.getSchedule());
	//		query.setParameter("Pass_Fail_Criteria", pir.getPass_Fail_Criteria());
	//		query.setParameter("StageApplicable", pir.getStageApplicable());
	//		query.setParameter("Remarks", pir.getRemarks());
	//		query.setParameter("TestId", TestId);
	//		query.setParameter("ModifiedBy", pir.getModifiedBy());
	//		query.setParameter("ModifiedDate", pir.getModifiedDate());
	//		query.executeUpdate();
	//		return 1l;
	//	}

	private static final String VERIFICATIONMETHODLIST="SELECT a.VerificationDataId,b.VerificationMasterId,b.VerificationName,a.TypeofTest,a.Purpose FROM pfms_initiation_verification_data a,pfms_initiation_verification_master b WHERE a.VerificationMasterId=b.VerificationMasterId AND a.IsActive='1' AND a.initiationid=:initiationId AND projectid=:projectId";
	@Override
	public List<Object[]> getVerificationMethodList(String projectId, String initiationId) throws Exception {
		Query query = manager.createNativeQuery(VERIFICATIONMETHODLIST);

		query.setParameter("projectId", projectId);
		query.setParameter("initiationId", initiationId);
		return (List<Object[]>)query.getResultList();
	}

	private final String TestType="SELECT a.TestId ,a.TestDetailsId ,a.Name ,a.Objective,a.Description,a.PreConditions ,a.PostConditions ,a.Constraints,a.SafetyRequirements,a.Methodology,a.ToolsSetup,a.PersonnelResources,a.EstimatedTimeIteration ,a.Iterations  ,a.Schedule ,a.Pass_Fail_Criteria , a.Remarks ,a. TestPlanInitiationId, '0' AS ProjectId,a.SpecificationId,a.RequirementId,a.StageApplicable,a.IsActive,b.TestSetupName,a.Timetype,a.NumberofRows,a.NumberofCycles  FROM pfms_testdetails a LEFT JOIN pfms_test_plan_testingtools b ON  b.TestToolsId = a.ToolsSetup WHERE TestId=:r AND a.IsActive='1'";
	@Override
	public 	List<Object[]> TestType(String r) throws Exception {

		Query query =manager.createNativeQuery(TestType);
		query.setParameter("r", r);
		List<Object[]> testType=(List<Object[]>)query.getResultList();
		return testType;
	}

	private static final String TESTDOCSUM="SELECT a.AdditionalInformation,a.Abstract,a.Keywords,a.Distribution,a.reviewer,a.approver,(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname)FROM employee e WHERE e.empid=a.approver ) AS 'Approver1',(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),''), e.empname)FROM employee e WHERE e.empid=a.reviewer) AS 'Reviewer1',a.summaryid FROM pfms_test_plan_summary a WHERE a.TestPlanInitiationId =:TestPlanInitiationId AND a.isactive='1'";
	@Override
	public List<Object[]> getDocumentSummary(String testPlanInitiationId) throws Exception {

		Query query = manager.createNativeQuery(TESTDOCSUM);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		List<Object[]>DocumentSummary=(List<Object[]>)query.getResultList();

		return DocumentSummary;
	}

	private static final String TESTDETAILSLIST="SELECT a.TestId ,a.TestDetailsId ,a.Name ,a.Objective,a.Description,a.PreConditions ,a.PostConditions ,a.Constraints,a.SafetyRequirements,a.Methodology,a.ToolsSetup,a.PersonnelResources,a.EstimatedTimeIteration ,a.Iterations  ,a.Schedule ,a.Pass_Fail_Criteria , a.Remarks ,a. TestPlanInitiationId, '0' AS ProjectId,a.SpecificationId,a.RequirementId,a.IsActive,a.StageApplicable,a.parentid,a.mainid FROM pfms_testdetails a WHERE TestPlanInitiationId=:TestPlanInitiationId AND a.IsActive='1'";
	@Override
	public List<Object[]> TestDetailsList(String testPlanInitiationId) throws Exception {

		Query query=manager.createNativeQuery(TESTDETAILSLIST);
		query.setParameter("TestPlanInitiationId", testPlanInitiationId);
		List<Object[]> TestDetailList=(List<Object[]> )query.getResultList();	
		return TestDetailList;
	}

	@Override
	public Long insertTestType(TestTools pt) throws Exception {
		manager.persist(pt);
		return pt.getTestToolsId();
	}

	private static final String INITIATIONTESTPLANLIST = "SELECT a.TestPlanInitiationId,a.ProjectId,a.InitiationId,a.ProductTreeMainId,a.InitiatedBy,a.InitiatedDate,b.emp_name,c.Designation,a.TestPlanVersion,d.ReqStatusCode,d.ReqStatus,d.ReqStatusColor FROM pfms_test_plan_initiation a,employee b,employee_desig c,pfms_req_approval_status d WHERE a.IsActive=1 AND a.InitiatedBy=b.emp_id AND b.desig_id=c.desig_id AND a.ReqStatusCode=d.ReqStatusCode AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.InitiationId=:InitiationId ORDER BY a.TestPlanInitiationId DESC";
	@Override
	public List<Object[]> initiationTestPlanList(String projectId, String mainId, String initiationId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(INITIATIONTESTPLANLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", mainId);
			query.setParameter("InitiationId", initiationId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	// Test Plan Changes from Bharath End

	@Override
	public long addTestPlanInitiation(TestPlanInitiation testplanInitiation) throws Exception {
		try {
			manager.persist(testplanInitiation);
			manager.flush();
			return testplanInitiation.getTestPlanInitiationId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addTestPlanInitiation "+e);
			return 0L;
		}
	}

	@Override
	public TestPlanSummary getTestPlanSummaryById(String summaryId) throws Exception {
		try {

			return manager.find(TestPlanSummary.class, Long.parseLong(summaryId)) ;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestPlanSummaryById "+e);
			return null;
		}
	}

	@Override
	public TestPlanInitiation getTestPlanInitiationById(String testPlanInitiationId) throws Exception{
		try {
			return manager.find(TestPlanInitiation.class, Long.parseLong(testPlanInitiationId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestPlanInitiationById "+e);
			return null;
		}
	}

	@Override
	public SpecsInitiation getSpecsInitiationById(String specsInitiationId) throws Exception {
		try {
			return manager.find(SpecsInitiation.class, Long.parseLong(specsInitiationId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getSpecsInitiationById "+e);
			return null;
		}
	}
	@Override
	public TestDetails getTestPlanDetailsById(String testId) throws Exception {
		try {

			return manager.find(TestDetails.class, Long.parseLong(testId)) ;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestPlanDetailsById "+e);
			return null;
		}
	}


	@Override
	public TestPlanSummary getTestPlanSummaryByTestPlanInitiationId(String testPlanInitiationId) throws Exception{
		try {
			Query query = manager.createQuery("FROM TestPlanSummary WHERE IsActive=1 AND TestPlanInitiationId=:TestPlanInitiationId");
			query.setParameter("TestPlanInitiationId", Long.parseLong(testPlanInitiationId));
			return (TestPlanSummary)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestPlanSummaryByTestPlanInitiationId "+e);
			return null;
		}
	}

	private static final String PROJECTTESTPLANPENDINGLIST  ="CALL pfms_testplan_doc_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> projectTestPlanPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(PROJECTTESTPLANPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectTestPlanPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String PROJECTTESTPLANAPPROVEDLIST  ="CALL pfms_testplan_doc_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> projectTestPlanApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(PROJECTTESTPLANAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectTestPlanApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long addSpecsInitiation(SpecsInitiation specsInitiation) throws Exception {
		manager.persist(specsInitiation);
		return specsInitiation.getSpecsInitiationId();
	}

	private static final String GETDUPLICATECOUNTOFTESTTYPE = "SELECT COUNT(TestType) AS TestTypeCount FROM pfms_test_plan_testingtools WHERE IsActive=1 AND TestType=:TestType";
	@Override
	public int getDuplicateCountofTestType(String testType) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETDUPLICATECOUNTOFTESTTYPE);
			query.setParameter("TestType", testType);
			Long count = (Long)query.getSingleResult();
			return count.intValue();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getDuplicateCountofTestType " + e);
			e.printStackTrace();
			return 0;
		}
	}

	private static final String TESTPLANAPPROVALFLOWDATA = "SELECT (SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.PreparedBy AND c.desig_id=d.desig_id) AS PreparedBy, \r\n"
			+ "	(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Reviewer AND c.desig_id=d.desig_id) AS Reviewer, \r\n"
			+ "	(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Approver AND c.desig_id=d.desig_id) AS Approver\r\n"
			+ "FROM pfms_test_plan_summary a WHERE a.TestPlanInitiationId = (SELECT b.TestPlanInitiationId FROM pfms_test_plan_initiation b WHERE b.InitiationId=:InitiationId AND b.ProjectId=:ProjectId AND ProductTreeMainId=:ProductTreeMainId AND b.TestPlanVersion=1.0 AND b.IsActive=1 LIMIT 1)";
	@Override
	public Object[] getTestPlanApprovalFlowData(String initiationId, String projectId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(TESTPLANAPPROVALFLOWDATA);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectTestPlanApprovedList " + e);
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public DocumentFreeze getDocumentFreezeByDocIdandDocType(String docInitiationId, String docType) throws Exception {
		try {
			Query query = manager.createQuery("FROM DocumentFreeze WHERE DocInitiationId=:DocInitiationId AND DocType=:DocType AND IsActive=1");
			query.setParameter("DocInitiationId", Long.parseLong(docInitiationId));
			query.setParameter("DocType", docType);
			return (DocumentFreeze)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getDocumentFreezeByDocIdandDocType " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addDocumentFreeze(DocumentFreeze freeze) throws Exception {
		try {
			manager.persist(freeze);
			manager.flush();
			return freeze.getDocFreezeId();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO addDocumentFreeze " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String GETFIRSTVERSIONTESTPLANINITIATIONID = "SELECT a.TestPlanInitiationId FROM pfms_test_plan_initiation a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.TestPlanVersion=1.0 AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionTestPlanInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONTESTPLANINITIATIONID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			Long count = (Long)query.getSingleResult();
			return count.longValue();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getFirstVersionTestPlanInitiationId " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String GETFIRSTVERSIONREQINITIATIONID = "SELECT a.ReqInitiationId FROM pfms_req_initiation a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.ReqVersion=1.0 AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionReqInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONREQINITIATIONID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			Long count = (Long)query.getSingleResult();
			return count.longValue();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getFirstVersionReqInitiationId " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String REQUIRMENTAPPROVALFLOWDATA = "SELECT (SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.PreparedBy AND c.desig_id=d.desig_id) AS PreparedBy, \r\n"
			+ "	(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Reviewer AND c.desig_id=d.desig_id) AS Reviewer, \r\n"
			+ "	(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Approver AND c.desig_id=d.desig_id) AS Approver\r\n"
			+ "FROM pfms_initiation_req_summary a WHERE a.ReqInitiationId = (SELECT b.ReqInitiationId FROM pfms_req_initiation b WHERE b.ProjectId=:ProjectId AND b.InitiationId=:InitiationId AND ProductTreeMainId=:ProductTreeMainId AND b.ReqVersion=1.0 AND b.IsActive=1 LIMIT 1)";
	@Override
	public Object[] getRequirementApprovalFlowData(String initiationId, String projectId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REQUIRMENTAPPROVALFLOWDATA);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getRequirementApprovalFlowData " + e);
			e.printStackTrace();
			return null;
		}

	}

	private static final String SPECLIST="SELECT SpecsId,SpecificationName,Description,SpecsInitiationId,LinkedRequirement,SpecsParameter,SpecsUnit,ParentId,MainId,SpecValue,LinkedSubSystem,maximumValue,minimumValue,IsMasterData FROM pfms_specification_details WHERE SpecsInitiationId=:specsInitiationId AND isactive='1'";
	@Override
	public List<Object[]> getSpecsList(String specsInitiationId) throws Exception {

		Query query = manager.createNativeQuery(SPECLIST);
		query.setParameter("specsInitiationId", specsInitiationId);
		return (List<Object[]>)query.getResultList();
	}

	private static final String GETFIRSTVERSIONSPECIFICATIONSID = "SELECT a.SpecsInitiationId FROM pfms_specifications_initiation a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.SpecsVersion='1.0' AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionSpecsInitiationId(String initiationId, String projectId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONSPECIFICATIONSID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			Long count = (Long)query.getSingleResult();
			return count.longValue();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getFirstVersionSpecsInitiationId " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String SPECSAPPROVALFLOW="SELECT (SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.PreparedBy AND c.desig_id=d.desig_id) AS PreparedBy, \r\n"
			+ "			(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Reviewer AND c.desig_id=d.desig_id) AS Reviewer,\r\n"
			+ "			(SELECT CONCAT(c.emp_name,', ',d.Designation) FROM employee c,employee_desig d WHERE c.emp_id=a.Approver AND c.desig_id=d.desig_id) AS Approver\r\n"
			+ "			FROM pfms_test_plan_summary a WHERE a.SpecsInitiationId = (SELECT b.SpecsInitiationId FROM pfms_specifications_initiation b WHERE b.ProjectId=:ProjectId AND b.InitiationId=:InitiationId AND ProductTreeMainId=:ProductTreeMainId AND b.SpecsVersion=1.0 AND b.IsActive=1 LIMIT 1)";

	@Override
	public List<Object[]> getSpecsPlanApprovalFlowData(String projectId, String initationId, String productTreeMainId)
			throws Exception {

		Query query = manager.createNativeQuery(SPECSAPPROVALFLOW);
		query.setParameter("ProjectId", projectId);
		query.setParameter("InitiationId", initationId);
		query.setParameter("ProductTreeMainId", productTreeMainId);
		List<Object[]> list =  (List<Object[]>)query.getResultList();
		if(list.size()>0) {
			return list;
		}else {
			return null;
		}
	}



	private static final String PROJECTSPECSPENDINGLIST  ="CALL pfms_specification_doc_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> projectSpecificationPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(PROJECTSPECSPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO projectSpecificationPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}
	private static final String ALLSQR ="select * from pfms_initiation_sqr where reqInitiationId <> :reqInitiationId";
	@Override
	public List<Object[]> getAllSqr(String reqInitiationId) throws Exception {
		Query query = manager.createNativeQuery(ALLSQR);
		query.setParameter("reqInitiationId", reqInitiationId);
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long AddReqType(PfmsReqTypes pr) throws Exception {
		manager.persist(pr);
		manager.flush();
		return pr.getRequirementId();
	}


	private static final String DELSQR = "UPDATE pfms_initiation_sqr_para SET IsActive='0' WHERE ParaId=:ParaId ";
	@Override
	public long deleteSqr(String paraId) throws Exception {

		Query query = manager.createNativeQuery(DELSQR);
		query.setParameter("ParaId", paraId);
		return query.executeUpdate();
	}

	private static final String UPDATESL="UPDATE pfms_initiation_sqr_para SET SINo=:SINo WHERE ParaId=:para";

	@Override
	public long updateSerialParaNo(String para, String SINo) throws Exception {


		Query query =  manager.createNativeQuery(UPDATESL);

		query.setParameter("para", para);
		query.setParameter("SINo", SINo);
		return query.executeUpdate();
	}

	private static final String DELETEINITIATIONREQ = "UPDATE pfms_initiation_req SET IsActive='0' WHERE InitiationReqId=:InitiationReqId ";
	@Override
	public long deleteInitiationReq(String InitiationReqId) throws Exception {

		Query query = manager.createNativeQuery(DELETEINITIATIONREQ);
		query.setParameter("InitiationReqId", InitiationReqId);
		return query.executeUpdate();
	}
	private static final String DELETEINITIATIONSPE = "UPDATE pfms_specification_details SET IsActive='0' WHERE SpecsId=:SpecsId ";
	@Override
	public long deleteInitiationSpe(String SpecsId) throws Exception {

		Query query = manager.createNativeQuery(DELETEINITIATIONSPE);
		query.setParameter("SpecsId", SpecsId);
		return query.executeUpdate();
	}

	@Override
	public long addSpecMaster(PfmsSpecTypes pst) throws Exception {
		manager.persist(pst);
		return pst.getSpecificationMainId();
	}

	private static final String SPECMASTERLIST="SELECT * FROM pfms_specification_types WHERE SpecificationMainId NOT IN (SELECT mainid FROM pfms_specification_details WHERE SpecsInitiationId=:SpecsInitiationId and isactive='1')";
	@Override
	public List<Object[]> getSpecMasterList(String SpecsInitiationId) throws Exception {

		Query query = manager.createNativeQuery(SPECMASTERLIST);
		query.setParameter("SpecsInitiationId", SpecsInitiationId);
		return (List<Object[]>)query.getResultList();
	}

	private static final String SPECNAMES="SELECT * FROM pfms_specification_types WHERE SpecificationMainId=:mainId";
	@Override
	public Object[] getSpecName(String mainId) throws Exception {
		Query query = manager.createNativeQuery(SPECNAMES);
		query.setParameter("mainId", mainId);
		List<Object[]>list = (List<Object[]>)query.getResultList();
		return list.get(0);
	}
	@Override
	public long addTestMaster(PfmsTestTypes pt) throws Exception {
		manager.persist(pt);
		manager.flush();
		return pt.getTestMainId();
	}

	private static final String TESTPLANMAINLIST="SELECT * FROM pfms_test_types WHERE testMainId NOT IN (SELECT MainId FROM pfms_testdetails WHERE TestPlanInitiationId = :testPlanInitiationId AND isactive='1')";
	@Override
	public List<Object[]> getTestPlanMainList(String testPlanInitiationId) throws Exception {

		Query query = manager.createNativeQuery(TESTPLANMAINLIST);
		query.setParameter("testPlanInitiationId", testPlanInitiationId);
		return (List<Object[]>)query.getResultList();
	}
	private static final String TESTNAMES="SELECT * FROM pfms_test_types WHERE testMainId=:mainId";
	@Override
	public Object[] getTestTypeName(String mainId) throws Exception {
		Query query = manager.createNativeQuery(TESTNAMES);
		query.setParameter("mainId", mainId);
		List<Object[]>list = (List<Object[]>)query.getResultList();
		return list.get(0);
	}
	private static final String TESTDLT=" UPDATE pfms_testdetails SET isactive='0' WHERE TestId=:testId";

	@Override
	public long deleteTestPlan(String testId) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery(TESTDLT);
		query.setParameter("testId", testId);		
		return query.executeUpdate();
	}

	/* Soumyakanta Swain */

	private static final String VERIFYMASTERLIST="SELECT VerificationMasterId,VerificationName FROM pfms_initiation_verification_master WHERE IsActive='1'";
	@Override
	public List<Object[]> getVerificationListMaster() throws Exception {
		Query query = manager.createNativeQuery(VERIFYMASTERLIST);
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public long addVerificationData(List<VerificationData> verifyList) throws Exception {

		for(VerificationData obj : verifyList) {
			manager.persist(obj);
			manager.flush();
		}
		return 1l;
	}

	private static final String VERIFYDATALIST="SELECT a.VerificationDataId,b.VerificationName,a.TypeofTest,a.Purpose,a.VerificationMasterId FROM pfms_initiation_verification_data a,pfms_initiation_verification_master b WHERE a.VerificationMasterId=b.VerificationMasterId AND a.IsActive='1' AND a.VerificationMasterId=:verificationId ORDER BY a.VerificationDataId ";
	@Override
	public List<Object[]> getverificationDataList(String verificationId) throws Exception {
		Query query = manager.createNativeQuery(VERIFYDATALIST);
		query.setParameter("verificationId", verificationId);

		return (List<Object[]>)query.getResultList();
	}

	private static final String VERIFYDATAEDIT="UPDATE pfms_initiation_verification_data SET TypeofTest=:testType,Purpose=:purpose,ModifiedBy=:modifiedBy,ModifiedDate=:modifiedDate WHERE VerificationDataId=:verificationDataId";
	@Override
	public long verificationDataEdit(VerificationData verifiData) throws Exception {
		Query query = manager.createNativeQuery(VERIFYDATAEDIT);
		query.setParameter("verificationDataId", verifiData.getVerificationDataId());
		query.setParameter("testType", verifiData.getTypeofTest());
		query.setParameter("purpose", verifiData.getPurpose());
		query.setParameter("modifiedBy", verifiData.getModifiedBy());
		query.setParameter("modifiedDate", verifiData.getModifiedDate());
		return query.executeUpdate();
	}

	@Override
	public long specMasterAddSubmit(SpecificationMaster sp) throws Exception {
		manager.persist(sp);
		manager.flush();
		return sp.getSpecsMasterId();
	}

	@Override
	public SpecificationMaster SpecificationMaster(long specsMasterId)throws Exception {
		try {
			return manager.find(SpecificationMaster.class, specsMasterId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO SpecificationMaster "+e);
			return null;
		}
	}


	private static final String TESTPLANMASTER="SELECT a.TestMasterId,a.Name,a.Objective,a.Description,CONCAT(IFNULL(CONCAT(c.title,' '),IFNULL(CONCAT(c.salutation,' '),'')), c.emp_name) AS 'empname'\r\n"
			+ ",a.PreConditions ,a.PostConditions,a.Constraints,a.SafetyRequirements,a.Methodology,a.ToolsSetup,a.PersonnelResources,a.Pass_Fail_Criteria,a.Remarks,a.StageApplicable FROM pfms_testplan_master a,login b,employee c WHERE  a.CreatedBy=b.UserName AND b.empid=c.emp_id AND a.IsActive = '1'";
	@Override
	public List<Object[]> TestPlanMaster() throws Exception {
		List<Object[]> TestPlanMasterList=null;
		try {
			Query query=manager.createNativeQuery(TESTPLANMASTER);
			TestPlanMasterList =(List<Object[]>) query.getResultList();
			return TestPlanMasterList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}

	@Override
	public TestPlanMaster getTestPlanById(long testMasterId) {
		try {
			return manager.find(TestPlanMaster.class, testMasterId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestPlanById "+e);
			return null;
		}
	}
	@Override
	public long testPlanMasterAdd(TestPlanMaster tp) throws Exception {
		manager.persist(tp);
		manager.flush();
		return tp.getTestMasterId();
	}


	/* *************************************** IGI / ICD / IRS / IDD DOCUMENT ********************************* */

	private static final String IGIDOCPENDINGLIST  ="CALL pfms_igi_doc_pending(:EmpId,:LabCode)";
	@Override
	public List<Object[]> igiDocumentPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(IGIDOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO igiDocumentPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String IGIDOCAPPROVEDLIST  ="CALL pfms_igi_doc_approved(:EmpId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> igiDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(IGIDOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO igiDocumentApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String ICDDOCPENDINGLIST  ="CALL pfms_icd_doc_pending(:EmpId,:LabCode)";
	@Override
	public List<Object[]> icdDocumentPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(ICDDOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO icdDocumentPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String ICDDOCAPPROVEDLIST  ="CALL pfms_icd_doc_approved(:EmpId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> icdDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(ICDDOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO icdDocumentApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String IRSDOCPENDINGLIST  ="CALL pfms_irs_doc_pending(:EmpId,:LabCode)";
	@Override
	public List<Object[]> irsDocumentPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(IRSDOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO irsDocumentPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
	
	private static final String IRSDOCAPPROVEDLIST  ="CALL pfms_irs_doc_approved(:EmpId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> irsDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(IRSDOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO irsDocumentApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String IDDDOCPENDINGLIST  ="CALL pfms_idd_doc_pending(:EmpId,:LabCode)";
	@Override
	public List<Object[]> iddDocumentPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(IDDDOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO iddDocumentPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
	
	private static final String IDDDOCAPPROVEDLIST  ="CALL pfms_idd_doc_approved(:EmpId,:FromDate,:ToDate)";
	@Override
	public List<Object[]> iddDocumentApprovedList(String empId, String FromDate, String ToDate) throws Exception {
		
		try {			
			Query query= manager.createNativeQuery(IDDDOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO iddDocumentApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	/* *************************************** IGI / ICD / IRS / IDD DOCUMENT END********************************* */
	
	private static final String PRODUCTTREELISTBYINITID= "SELECT a.MainId, a.SubLevelId, a.LevelName, a.Stage, a.Module, a.RevisionNo, a.SystemMainId, a.LevelCode FROM pfms_product_tree a,pfms_initiation b WHERE a.MainId>0 AND a.InitiationId=b.InitiationId AND b.InitiationId=:InitiationId AND a.IsActive='1' AND a.LevelId< 3 ORDER BY a.SubLevelId";

	@Override
	public List<Object[]> productTreeListByInitiationId(String initiationId) throws Exception {
		try {
			Query query=manager.createNativeQuery(PRODUCTTREELISTBYINITID);
			query.setParameter("InitiationId", initiationId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public List<TestPlanMaster> getAllTestPlans() throws Exception {
		try {

			Query query = manager.createNativeQuery("SELECT * FROM pfms_testplan_master where isActive='1'", TestPlanMaster.class);
			return query.getResultList();

		}catch (Exception e) {
			logger.error(new Date() + " Inside DAO getAllTestPlans " + e);
			return null;
		}
	}

	@Override
	public List<SpecificationMaster> getAllSpecPlans() throws Exception {
		try {

			Query query = manager.createNativeQuery("SELECT * FROM pfms_specification_master where isActive='1'", SpecificationMaster.class);
			return query.getResultList();

		}catch (Exception e) {
			logger.error(new Date() + " Inside DAO getAllSpecPlans " + e);
			return null;
		}
	}
	
	@Override
	public int deleteSpecificationMasterById(String specsMasterId) {
		try {
			Query query = manager.createNativeQuery("UPDATE pfms_specification_master a SET a.IsActive = 0 WHERE a.SpecsMasterId =:SpecsMasterId OR a.ParentId =:SpecsMasterId OR a.ParentId IN ( SELECT SpecsMasterId FROM ( SELECT b.SpecsMasterId FROM pfms_specification_master b WHERE b.ParentId =:SpecsMasterId ) AS subquery )");
			query.setParameter("SpecsMasterId", Long.parseLong(specsMasterId));
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<SpecificationTypes> getSpecificationTypesList() {
		try {
			Query query = manager.createQuery("FROM SpecificationTypes");
			return (List<SpecificationTypes>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SpecificationTypes>();
		}
	}
	
	@Override
	public List<TestSetupMaster> getTestSetupMaster() throws Exception {
	
		try {
			Query query = manager.createQuery("FROM TestSetupMaster t ORDER BY t.testSetUpId ASC");
			return (List<TestSetupMaster>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<TestInstrument> getTestInstrument() throws Exception {
		try {
			Query query = manager.createQuery("FROM TestInstrument");
			return (List<TestInstrument>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public long setTestInstrument(TestInstrument t) throws Exception {
		manager.persist(t);
		manager.flush();
		
		return t.getInstrumentId();
				
	}
	
	@Override
	public long addTestSetupMaster(TestSetupMaster tp) throws Exception {
		
		manager.persist(tp);
		manager.flush();
		
		return tp.getSetupId();
				
	}
	
	@Override
	public TestSetupMaster getTestSetupMasterById(Long setUpid) throws Exception {
		
		try {
			return manager.find(TestSetupMaster.class, setUpid);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getTestSetupMasterById "+e);
			return null;
	}
	}
	
	@Override
	public long saveTestSetUpAttachment(TestSetUpAttachment tp) throws Exception {
		
		manager.persist(tp);
		manager.flush();
		return tp.getAttachmentId();
	}
	
	@Override
	public List<TestSetUpAttachment> getTestSetUpAttachment(String setUpId) throws Exception {
		
		try {
			Query query = manager.createQuery("FROM  TestSetUpAttachment t where t.setupId = :setUpId and t.isActive=1 ORDER BY t.setupId DESC");		
			
			query.setParameter("setUpId", Long.parseLong(setUpId));	
			
			
			return (List<TestSetUpAttachment>)query.getResultList();
		}catch (Exception e) {
		
			e.printStackTrace();
			return new ArrayList<TestSetUpAttachment>();
		}
	
	}
	
	@Override
	public List<PfmsSystemSubIntroduction> getActiveSubIntroductionByMainId(Long mainId) throws Exception {
		
		  String jpql = "SELECT p FROM PfmsSystemSubIntroduction p WHERE p.mainId = :mainId AND p.isActive = 1";
		    try {
		        return (List<PfmsSystemSubIntroduction>)manager.createQuery(jpql, PfmsSystemSubIntroduction.class)
		                 .setParameter("mainId", mainId)
		                 .getResultList();
		    } catch (NoResultException e) {
		        return new ArrayList<>(); // or throw a custom exception if preferred
		    }
	}
	
	
	@Override
	public long savePfmsSystemSubIntroduction(PfmsSystemSubIntroduction ps) throws Exception {
		
		manager.persist(ps);
		manager.flush();
		
		return ps.getIntroductionId();
	}
	
	
	private static final String MILISACTIVE = "DELETE from  milestone_activity  WHERE MilestoneActivityId=:id";
	@Override
	public int setMilestoneInActive(String id) throws Exception {
	
		Query query = manager.createNativeQuery(MILISACTIVE);		
		
		query.setParameter("id", id);
		return query.executeUpdate();
	}
}
