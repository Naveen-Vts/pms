package com.vts.pfms.documents.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.documents.dto.ICDConnectionDTO;
import com.vts.pfms.documents.dto.ICDPinMapDTO;
import com.vts.pfms.documents.model.DataTypeMaster;
import com.vts.pfms.documents.model.FieldGroupLinked;
import com.vts.pfms.documents.model.FieldGroupMaster;
import com.vts.pfms.documents.model.FieldMaster;
import com.vts.pfms.documents.model.ICDConnectionConnectors;
import com.vts.pfms.documents.model.ICDConnectionInterfaces;
import com.vts.pfms.documents.model.ICDConnectionPurpose;
import com.vts.pfms.documents.model.ICDConnectionSystems;
import com.vts.pfms.documents.model.ICDConnectorMappedPins;
import com.vts.pfms.documents.model.ICDConnectorPinMapping;
import com.vts.pfms.documents.model.ICDConnectorPins;
import com.vts.pfms.documents.model.ICDDocumentConnections;
import com.vts.pfms.documents.model.ICDMechanicalInterfaces;
import com.vts.pfms.documents.model.ICDPurpose;
import com.vts.pfms.documents.model.ICDSystemAttach;
import com.vts.pfms.documents.model.IGIApplicableDocs;
import com.vts.pfms.documents.model.IGIConnector;
import com.vts.pfms.documents.model.IGIConnectorAttach;
import com.vts.pfms.documents.model.IGIConstants;
import com.vts.pfms.documents.model.IGIDocumentIntroduction;
import com.vts.pfms.documents.model.IGIDocumentMembers;
import com.vts.pfms.documents.model.IGIDocumentShortCodes;
import com.vts.pfms.documents.model.IGIDocumentShortCodesLinked;
import com.vts.pfms.documents.model.IGIDocumentSummary;
import com.vts.pfms.documents.model.IGIFieldDescription;
import com.vts.pfms.documents.model.IGIInterface;
import com.vts.pfms.documents.model.IGIInterfaceContent;
import com.vts.pfms.documents.model.IGIInterfaceTypes;
import com.vts.pfms.documents.model.IGILogicalChannel;
import com.vts.pfms.documents.model.IGILogicalInterfaces;
import com.vts.pfms.documents.model.IRSArrayMaster;
import com.vts.pfms.documents.model.IRSDocumentSpecifications;
import com.vts.pfms.documents.model.IRSFieldDescription;
import com.vts.pfms.documents.model.PfmsICDDocument;
import com.vts.pfms.documents.model.PfmsIDDDocument;
import com.vts.pfms.documents.model.PfmsIGIDocument;
import com.vts.pfms.documents.model.PfmsIGITransaction;
import com.vts.pfms.documents.model.PfmsIRSDocument;
import com.vts.pfms.documents.model.StandardDocuments;
import com.vts.pfms.documents.model.UnitMaster;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;


@Transactional
@Repository
public class DocumentsDaoImpl implements DocumentsDao{

	
	@PersistenceContext
	EntityManager manager;
	
	
	private static final Logger logger=LogManager.getLogger(DocumentsDaoImpl.class);

	@Override
	public long standardDocumentInsert(StandardDocuments model) throws Exception {
		try {
			manager.persist(model);
			manager.flush();

		} catch (Exception e) {
			logger.error(new Date() +"Inside DAO insertSocDetailsData "+ e);
			e.printStackTrace();
		}
		return model.getStandardDocumentId();
	}
	
	
	private static final String STANDARDDOCUMENTSLIST="SELECT a.StandardDocumentId,a.DocumentName,a.Description,a.FilePath,a.IsActive FROM pfms_standard_documents a ORDER BY a.IsActive DESC";
	@Override
	public List<Object[]> standardDocumentsList() throws Exception {
		try {
			Query query=manager.createNativeQuery(STANDARDDOCUMENTSLIST);
			return (List<Object[]>)query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String STANDARDATTCHMENTDATA="SELECT a.DocumentName,a.FilePath,a.Description FROM pfms_standard_documents a WHERE a.StandardDocumentId=:standardDocumentId";
	@Override
	public Object[] standardattachmentdata(String standardDocumentId) throws Exception {
		logger.info(new Date() + "Inside standardattachmentdata");
		try {
			Query query = manager.createNativeQuery(STANDARDATTCHMENTDATA);
			query.setParameter("standardDocumentId", standardDocumentId);
			Object[] standardattachmentdata = (Object[]) query.getSingleResult();
			return standardattachmentdata;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl standardattachmentdata", e);
			return null;
		}
	}
	
	
	@Override
	public StandardDocuments StandardDocumentDataById(long StandartDocumentId) throws Exception {
		try{
			StandardDocuments standarddocumentdetails=manager.find(StandardDocuments.class, StandartDocumentId);
			return standarddocumentdetails;
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO StandardDocumentDataById() " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String STANDARDDOCUMENTDELETE="UPDATE pfms_standard_documents a SET a.IsActive='0' WHERE a.StandardDocumentId=:standardDocumentId";
	@Override
	public long StandardDocumentDelete(long standardDocumentId) throws Exception {
		try {
			Query query =manager.createNativeQuery(STANDARDDOCUMENTDELETE);
			query.setParameter("standardDocumentId", standardDocumentId);
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/* ************************************************ IGI Document ***************************************************** */
	
	private static final String IGIDOCUMENTLIST="SELECT a.IGIDocId, a.IGIVersion, a.LabCode, a.InitiatedBy, a.InitiatedDate, a.IGIStatusCode, a.IGIStatusCodeNext, a.CreatedBy, a.CreatedDate, a.Remarks, \r\n"
			+ "CONCAT(IFNULL(CONCAT(b.Title,' '),(IFNULL(CONCAT(b.Salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation, d.ReqStatus, d.ReqStatusColor \r\n"
			+ "FROM pfms_igi_document a JOIN employee b ON a.InitiatedBy=b.emp_id LEFT JOIN employee_desig c ON b.desig_id=c.desig_id LEFT JOIN pfms_req_approval_status d ON a.IGIStatusCode=d.ReqStatusCode WHERE a.IsActive=1  ORDER BY a.IGIDocId DESC";
	@Override
	public List<Object[]> getIGIDocumentList() throws Exception {
		try {
			Query query=manager.createNativeQuery(IGIDOCUMENTLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addPfmsIGIDocument(PfmsIGIDocument  pfmsIgiDocument) throws Exception
	{
		try {
		    manager.persist(pfmsIgiDocument);
		    manager.flush();
			return pfmsIgiDocument.getIGIDocId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addPfmsIgiDocument " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	private static final String IGIDOCUMENTSUMMARYlIST="SELECT a.SummaryId, a.AdditionalInformation, a.Abstract, a.Keywords, a.Distribution, a.Reviewer, a.Approver, a.PreparedBy,\r\n"
			+ " CONCAT(IFNULL(CONCAT(e1.Title,' '),(IFNULL(CONCAT(e1.Salutation, ' '), ''))), e1.emp_name, ', ', d1.Designation) AS 'Approver1', \r\n"
			+ " CONCAT(IFNULL(CONCAT(e2.Title,' '),(IFNULL(CONCAT(e2.Salutation, ' '), ''))), e2.emp_name, ', ', d2.Designation) AS 'Reviewer1', \r\n"
			+ " CONCAT(IFNULL(CONCAT(e3.Title,' '),(IFNULL(CONCAT(e3.Salutation, ' '), ''))), e3.emp_name, ', ', d3.Designation) AS 'PreparedBy1', a.ReleaseDate \r\n"
			+ "FROM pfms_igi_document_summary a LEFT JOIN employee e1 ON e1.emp_id = a.Approver LEFT JOIN employee_desig d1 ON d1.desig_id = e1.desig_id LEFT JOIN employee e2 ON e2.emp_id = a.Reviewer LEFT JOIN employee_desig d2 ON d2.desig_id = e2.desig_id LEFT JOIN employee e3 ON e3.emp_id = a.PreparedBy LEFT JOIN employee_desig d3 ON d3.desig_id = e3.desig_id \r\n"
			+ "WHERE a.IsActive = 1 AND a.DocId=:DocId AND a.DocType=:DocType";
	@Override
	public List<Object[]> getDocumentSummaryList(String docId, String docType) throws Exception {
		try {
			Query query=manager.createNativeQuery(IGIDOCUMENTSUMMARYlIST);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
   @Override
	public IGIDocumentSummary getIGIDocumentSummaryById(String summaryId) throws Exception {
		try {
			
			return manager.find(IGIDocumentSummary.class, Long.parseLong(summaryId)) ;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DocumentsDAOImpl getIgiDocumentSummaryById "+e);
			return null;
		}
	}
   
	@Override
	public long addIGIDocumentSummary(IGIDocumentSummary rs) throws Exception {
		try {
			manager.persist(rs);
			manager.flush();
			return rs.getSummaryId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String IGIDOCUMENTMEMBERLIST = "SELECT a.emp_id, CONCAT(IFNULL(CONCAT(a.Title,' '),(IFNULL(CONCAT(a.Salutation, ' '), ''))), a.Emp_Name) AS 'EmpName',b.Designation,a.Lab_Code,b.Desig_Id,c.IGIMemeberId FROM employee a,employee_desig b,pfms_igi_document_members c WHERE a.is_active='1' AND a.Desig_Id=b.Desig_Id AND a.Emp_Id = c.EmpId AND c.DocId =:DocId AND c.DocType=:DocType AND c.IsActive =1 ORDER BY a.Sr_No=0, a.Sr_No";
	@Override
	public List<Object[]> getDocumentMemberList(String docId, String docType) throws Exception {
		try {
			Query query=manager.createNativeQuery(IGIDOCUMENTMEMBERLIST);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	private static final String DOCEMPLISTBYIGIDOCID="SELECT a.Emp_Id, CONCAT(IFNULL(CONCAT(a.Title,' '),(IFNULL(CONCAT(a.Salutation, ' '), ''))), a.Emp_Name) AS 'EmpName', b.Designation FROM employee a,employee_desig b WHERE a.Is_Active='1' AND a.Desig_Id=b.Desig_Id AND a.Lab_Code=:LabCode AND a.Emp_Id NOT IN (SELECT empid FROM pfms_igi_document_members WHERE DocId =:DocId AND DocType=:DocType AND IsActive = 1) ORDER BY a.Sr_No=0,a.Sr_No";
	@Override
	public List<Object[]> getDocmployeeListByDocId(String labCode, String docId, String docType) throws Exception {
		try {
			Query query=manager.createNativeQuery(DOCEMPLISTBYIGIDOCID);
			query.setParameter("LabCode", labCode);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addIGIDocumentMembers(IGIDocumentMembers igiDocumentMember) throws Exception {
		try {
			manager.persist(igiDocumentMember);
			manager.flush();
			return igiDocumentMember.getIGIMemeberId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	@Override
	public IGIDocumentMembers getIGIDocumentMembersById(Long igiMemeberId) throws Exception
	{
		try {
			return manager.find(IGIDocumentMembers.class, igiMemeberId);
		}catch (Exception e) {
			logger.error(new Date() + "Inside DocumentsDAOImpl getIGIDocumentMembersById "+e);
			e.printStackTrace();
			return null;
		}
	}

	private static final String DELETEIGIDOCUMENTMEMBERBYID = "UPDATE pfms_igi_document_members SET IsActive=0 WHERE IgiMemeberId=:IgiMemeberId";
	@Override
	public int deleteIGIDocumentMembers(String igiMemeberId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEIGIDOCUMENTMEMBERBYID);
			query.setParameter("IgiMemeberId", igiMemeberId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public long addIGIInterface(IGIInterface igiInterface) throws Exception {
		try {
			manager.persist(igiInterface);
			manager.flush();
			return igiInterface.getInterfaceId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<IGIInterface> getIGIInterfaceListByLabCode(String labCode) throws Exception {
		try {
			Query query = manager.createQuery("FROM IGIInterface WHERE LabCode = :LabCode");
			query.setParameter("LabCode", labCode);
			return (List<IGIInterface>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIInterface>();
		}
	}

	@Override
	public PfmsIGIDocument getPfmsIGIDocumentById(String igiDocId) throws Exception {
		try {
			return manager.find(PfmsIGIDocument.class, Long.parseLong(igiDocId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public IGIInterface getIGIInterfaceById(String interfaceId) throws Exception {
		try {
			return manager.find(IGIInterface.class, Long.parseLong(interfaceId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final String DUPLICATEINTERFACECODECOUNT = "SELECT COUNT(InterfaceId) AS 'Count' FROM pfms_igi_interfaces WHERE CASE WHEN InterfaceId<>0 THEN InterfaceId!=:InterfaceId END AND InterfaceCode=:InterfaceCode AND IsActive=1";
	@Override
	public Long getDuplicateInterfaceCodeCount(String interfaceId,String interfaceCode) throws Exception {

		try {
			Query query =manager.createNativeQuery(DUPLICATEINTERFACECODECOUNT);
			query.setParameter("InterfaceId", interfaceId);
			query.setParameter("InterfaceCode", interfaceCode);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateInterfaceCodeCount "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<IGIDocumentShortCodes> getIGIDocumentShortCodesList() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGIDocumentShortCodes WHERE IsActive=1 ORDER BY ShortCode");
			return (List<IGIDocumentShortCodes>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIDocumentShortCodes>();
		}
	}
	
	@Override
	public long addIGIDocumentShortCodes(IGIDocumentShortCodes igiDocumentShortCode) throws Exception {
		try {
			manager.persist(igiDocumentShortCode);
			manager.flush();

			return igiDocumentShortCode.getShortCodeId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<StandardDocuments> getStandardDocuments() throws Exception {
		try {
			Query query = manager.createQuery("FROM StandardDocuments WHERE IsActive=1");
			return (List<StandardDocuments>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<StandardDocuments>();
		}
	}
	
	private static final String GETIGIGAPPLICABLEDOCS = "SELECT a.IGIApplicableDocId, a.StandardDocumentId, b.DocumentName FROM pfms_igi_document_applicabledocs a, pfms_standard_documents b WHERE a.IsActive=1 AND a.StandardDocumentId=b.StandardDocumentId AND a.DocId=:DocId AND a.DocType = :DocType";
	@Override
	public List<Object[]> getIGIApplicableDocs(String docId, String docType) throws Exception {
		try {
			Query query=manager.createNativeQuery(GETIGIGAPPLICABLEDOCS);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addIGIApplicableDocs(IGIApplicableDocs igiApplicableDocs) throws Exception {
		try {
			manager.persist(igiApplicableDocs);
			manager.flush();
			return igiApplicableDocs.getStandardDocumentId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static final String DELETEIGIAPPLICABLEDOCUMENTBYID = "UPDATE pfms_igi_document_applicabledocs SET IsActive=0 WHERE IGIApplicableDocId=:IGIApplicableDocId";
	@Override
	public int deleteIGIApplicableDocument(String igiApplicableDocId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEIGIAPPLICABLEDOCUMENTBYID);
			query.setParameter("IGIApplicableDocId", igiApplicableDocId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static final String GETINTERFACETYPECOUNTBYTYPEID = "SELECT COUNT(InterfaceId) AS InterfaceCount FROM pfms_igi_interfaces WHERE IsActive=1 AND InterfaceTypeId=:InterfaceTypeId";
	@Override
	public Long getInterfaceTypeCountByinterfaceTypeId(String interfaceTypeId) throws Exception {

		try {
			Query query =manager.createNativeQuery(GETINTERFACETYPECOUNTBYTYPEID);
			query.setParameter("InterfaceTypeId", interfaceTypeId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getInterfaceCountByType "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	public static final String GETINTERFACECONTENTCOUNTBYCONTENTID = "SELECT COUNT(InterfaceId) AS InterfaceCount FROM pfms_igi_interfaces WHERE IsActive=1 AND InterfaceTypeId=:InterfaceTypeId AND InterfaceContentId=:InterfaceContentId";
	@Override
	public Long getInterfaceContentCountByinterfaceContentId(String interfaceTypeId, String interfaceContentId) throws Exception {
		
		try {
			Query query =manager.createNativeQuery(GETINTERFACECONTENTCOUNTBYCONTENTID);
			query.setParameter("InterfaceTypeId", interfaceTypeId);
			query.setParameter("InterfaceContentId", interfaceContentId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getInterfaceCountByType "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String GETFIRSTVERSIONIGIDOCID = "SELECT a.IGIDocId FROM pfms_igi_document a WHERE a.IGIVersion='1.0' AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionIGIDocId() throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONIGIDOCID);
			return (Long)query.getSingleResult();
			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DocumentsDAOImpl getFirstVersionIGIDocId " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public long addIGIDocumentShortCodesLinked(IGIDocumentShortCodesLinked igiDocumentShortCodeLinked) throws Exception {
		try {
			manager.persist(igiDocumentShortCodeLinked);
			manager.flush();

			return igiDocumentShortCodeLinked.getShortCodeLinkedId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String GETIGISHORTCODESLINKEDLISTBYTYPE = "SELECT a.ShortCodeId, a.ShortCode, a.FullName, a.ShortCodeType, a.IsActive, b.ShortCodeLinkedId FROM pfms_igi_document_shortcodes a, pfms_igi_document_shortcodes_linked b WHERE b.IsActive=1 AND a.ShortCodeId=b.ShortCodeId AND DocId=:DocId AND DocType=:DocType";
	@Override
	public List<Object[]> getIGIShortCodesLinkedListByType(String docId, String docType) throws Exception {
		try {
			Query query=manager.createNativeQuery(GETIGISHORTCODESLINKEDLISTBYTYPE);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}

	private static final String DELETEIGIDOCUMENTSHORTCODESLINKED = "UPDATE pfms_igi_document_shortcodes_linked SET IsActive=0 WHERE ShortCodeLinkedId=:ShortCodeLinkedId";
	@Override
	public int deleteIGIDocumentShortCodesLinked(String shortCodeLinkedId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEIGIDOCUMENTSHORTCODESLINKED);
			query.setParameter("ShortCodeLinkedId", shortCodeLinkedId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static final String DUPLICATEIGISHORTCODECOUNT = "SELECT COUNT(ShortCodeId) AS 'Count' FROM pfms_igi_document_shortcodes WHERE ShortCode=:ShortCode AND ShortCodeType=:ShortCodeType AND IsActive=1";
	@Override
	public Long getDuplicateIGIShortCodeCount(String shortCode,String shortCodeType) throws Exception {

		try {
			Query query = manager.createNativeQuery(DUPLICATEIGISHORTCODECOUNT);
			query.setParameter("ShortCode", shortCode);
			query.setParameter("ShortCodeType", shortCodeType);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateIGIShortCodeCount "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String IGIDOCTRANSLIST = "SELECT a.IGITransactionId,c.Emp_No,c.Emp_Name,d.Designation,a.ActionDate,a.Remarks,b.ReqStatus,b.ReqStatusColor FROM pfms_igi_trans a,pfms_req_approval_status b,employee c,employee_desig d WHERE a.StatusCode = b.ReqStatusCode AND a.ActionBy=c.Emp_Id AND c.Desig_Id = d.Desig_Id AND a.DocId=:DocId AND a.DocType=:DocType ORDER BY a.IGITransactionId";
	@Override
	public List<Object[]> igiTransactionList(String docId, String docType) throws Exception {

		try {
			Query query = manager.createNativeQuery(IGIDOCTRANSLIST);
			query.setParameter("DocId", docId);
			query.setParameter("DocType", docType);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO igiTransactionList "+e);
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<IGIInterfaceTypes> getIGIInterfaceTypesList() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGIInterfaceTypes WHERE IsActive=1 ORDER BY InterfaceTypeCode");
			return (List<IGIInterfaceTypes>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIInterfaceTypes>();
		}
	}
	
	@Override
	public List<IGIInterfaceContent> getIGIInterfaceContentList() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGIInterfaceContent WHERE IsActive=1 ORDER BY InterfaceContentCode");
			return (List<IGIInterfaceContent>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIInterfaceContent>();
		}
	}

	// srikant start
	private static final String INTERFACETYPEMASTER="SELECT a.InterfaceTypeId, a.InterfaceType, a.InterfaceTypeCode FROM pfms_igi_interface_types a WHERE a.IsActive = 1 ORDER BY a.InterfaceTypeId DESC";

	@Override
	public List<Object[]> interfaceTypeMasterList() throws Exception {
		try {
			Query query=manager.createNativeQuery(INTERFACETYPEMASTER);
			return (List<Object[]>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}

	@Override
	public IGIInterfaceTypes getIGIInterfaceTypeById(String interfaceTypeId) throws Exception {
		try {
		  return manager.find(IGIInterfaceTypes.class, Long.parseLong(interfaceTypeId));
		}catch (Exception e) {
			e.printStackTrace();
			return new IGIInterfaceTypes();
		}
	}

	@Override
	public long addIGIInterfaceTypes(IGIInterfaceTypes iGIInterfaceTypes) throws Exception {
		try {
			manager.persist(iGIInterfaceTypes);
			manager.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iGIInterfaceTypes.getInterfaceTypeId();
	}

	@Override
	public long addIGIInterfaceTypesContents(IGIInterfaceContent iGIInterfaceContent) throws Exception {
		try {
			manager.persist(iGIInterfaceContent);
			manager.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return iGIInterfaceContent.getInterfaceContentId();
	}

	private static final String IGIINTERFACETYPESCONTENTSDELETE="UPDATE pfms_igi_interface_content  SET IsActive='0' WHERE interfaceTypeId=:interfaceTypeId";

	@Override
	public int removeIGIInterfaceTypesContents(String interfaceTypeId) throws Exception{
			try {
				Query query =manager.createNativeQuery(IGIINTERFACETYPESCONTENTSDELETE);
				query.setParameter("interfaceTypeId", Long.parseLong(interfaceTypeId));
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;		
	}

	private static final String INTERFACEADDCHECK ="SELECT SUM(IF(InterfaceTypeCode =:interfaceTypeCode,1,0))   AS 'dCode','0' AS 'codecount'FROM pfms_igi_interface_types WHERE isactive=1 AND interfaceTypeId!=:interfaceTypeId";

	@Override
	public Object[] InterfaceAddCheck(String interfaceTypeCode, String interfaceId) throws Exception {
		try {
			Query query = manager.createNativeQuery(INTERFACEADDCHECK);
			query.setParameter("interfaceTypeCode", interfaceTypeCode);
			query.setParameter("interfaceTypeId", interfaceId);
			return (Object[]) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String INTERFACECONTENTADDCHECK="SELECT SUM(IF(InterfaceContentCode =:interfaceContentCode,1,0))   AS 'dCode','0' AS 'codecount'FROM pfms_igi_interface_content WHERE isactive=1 AND interfaceContentId!=:interfaceContentId ";

	@Override
	public Object[] InterfaceContentAddCheck(String interfaceContentCode, String contentId) throws Exception {
		try {
			Query query = manager.createNativeQuery(INTERFACECONTENTADDCHECK);
			query.setParameter("interfaceContentCode", interfaceContentCode);
			query.setParameter("interfaceContentId", contentId);
			return (Object[]) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//srikant end
	
	private static final String IGIDOCUMENTUSERREVOKE = "UPDATE pfms_igi_document SET IGIStatusCode='REV', IGIStatusCodeNext='FWD' WHERE IGIDocId=:IGIDocId";
	@Override
	public int igiDocumentUserRevoke(String igiDocId) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(IGIDOCUMENTUSERREVOKE);
			query.setParameter("IGIDocId", igiDocId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<IGILogicalInterfaces> getIGILogicalInterfaces() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGILogicalInterfaces");
			return (List<IGILogicalInterfaces>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGILogicalInterfaces>();
		}
	}
	

	@Override
	public IGILogicalInterfaces getIGILogicalInterfaceById(String logicalInterfaceId) throws Exception {
		try {
			return manager.find(IGILogicalInterfaces.class, Long.parseLong(logicalInterfaceId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	

	@Override
	public long addIGILogicalInterfaces(IGILogicalInterfaces igiLogicalInterfaces) throws Exception {
		try {
		    manager.persist(igiLogicalInterfaces);
		    manager.flush();
			return igiLogicalInterfaces.getLogicalInterfaceId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addIGILogicalInterfaces " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	public static final String LOGICALINTERFACECOUNTBYTYPE = "SELECT IFNULL(COUNT(LogicalInterfaceId), 0) AS 'Count' FROM pfms_igi_logical_interfaces WHERE LogicalChannelId=:LogicalChannelId AND MsgType=:MsgType";
	@Override
	public int getLogicalInterfaceCountByType(String logicalChannelId, String msgType) throws Exception {

		try {
			Query query = manager.createNativeQuery(LOGICALINTERFACECOUNTBYTYPE);
			query.setParameter("MsgType", msgType);
			query.setParameter("LogicalChannelId", logicalChannelId);
			Long count = (Long)query.getSingleResult();
			return count.intValue();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getLogicalInterfaceCountByType "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<IGILogicalChannel> getIGILogicalChannelList() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGILogicalChannel WHERE IsActive=1");
			return (List<IGILogicalChannel>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGILogicalChannel>();
		}
	}

	@Override
	public IGILogicalChannel getIGILogicalChannelById(String logicalChannelId) throws Exception {
		try {
			return manager.find(IGILogicalChannel.class, Long.parseLong(logicalChannelId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public long addIGILogicalChannel(IGILogicalChannel igiLogicalChannel) throws Exception
	{
		try {
		    manager.persist(igiLogicalChannel);
		    manager.flush();
			return igiLogicalChannel.getLogicalChannelId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addIGILogicalChannel " + e);
			e.printStackTrace();
			return 0 ;
		}
	}
	
	private static final String DELETELOGICALCHANNELBYID = "UPDATE pfms_igi_logical_channel SET IsActive=0 WHERE LogicalChannelId=:LogicalChannelId";
	@Override
	public int deleteIGILogicalChannelById(String logicalChannelId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETELOGICALCHANNELBYID);
			query.setParameter("LogicalChannelId", logicalChannelId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static final String FIELDMASTER="SELECT a.FieldMasterId, a.FieldName, a.FieldShortName, a.FieldCode, a.FieldDesc, a.DataTypeMasterId, a.TypicalValue, a.FieldMinValue, a.FieldMaxValue, a.InitValue, a.FieldOffSet, a.Quantum, c.Unit, a.Remarks, b.DataTypePrefix, b.DataLength, b.AliasName, b.DataStandardName, (SELECT GROUP_CONCAT(c.FieldGroupId SEPARATOR ',') FROM pfms_field_group_linked c WHERE c.FieldMasterId = a.FieldMasterId AND c.IsActive=1) AS FieldGroupIds  \r\n"
			+ "FROM pfms_field_master a LEFT JOIN pfms_data_type_master b ON a.DataTypeMasterId=b.DataTypeMasterId LEFT JOIN pfms_unit_master c ON a.UnitMasterId = c.UnitMasterId WHERE a.IsActive = 1 ORDER BY a.FieldMasterId, a.FieldName ASC";
	@Override
	public List<Object[]> fieldMasterList() throws Exception {
		try {
			Query query=manager.createNativeQuery(FIELDMASTER);
			return (List<Object[]>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	
	@Override
	public FieldMaster getFieldMasterById(String fieldMasterId) throws Exception {
		try {
		  return manager.find(FieldMaster.class, Long.parseLong(fieldMasterId));
		}catch (Exception e) {
			e.printStackTrace();
			return new FieldMaster();
		}
	}

	@Override
	public long addFieldMaster(FieldMaster fieldMaster) throws Exception {
		try {
			manager.persist(fieldMaster);
			manager.flush();
			return fieldMaster.getFieldMasterId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addFieldMaster "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String DATATYPEMASTER="SELECT a.DataTypeMasterId, a.DataTypePrefix, a.DataLength, a.AliasName, a.DataStandardName FROM pfms_data_type_master a WHERE a.IsActive = 1 ORDER BY a.DataLength, a.DataTypeMasterId DESC";
	@Override
	public List<Object[]> dataTypeMasterList() throws Exception {
		try {
			Query query=manager.createNativeQuery(DATATYPEMASTER);
			return (List<Object[]>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}

	@Override
	public DataTypeMaster getDataTypeMasterById(Long dataTypeMasterId) throws Exception {
		try{
			return manager.find(DataTypeMaster.class, dataTypeMasterId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getDataTypeMasterById " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addDataTypeMaster(DataTypeMaster dto) throws Exception {
		try {
			manager.persist(dto);
			manager.flush();
			return dto.getDataTypeMasterId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl dataTypeMasterSubmit "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String IGIFIELDDESCRIPTIONDELETE = "UPDATE pfms_igi_field_desc SET IsActive='0' WHERE LogicalInterfaceId=:LogicalInterfaceId";
	@Override
	public int removeIGIFieldDescription(String logicalInterfaceTypeId) throws Exception{
			try {
				Query query =manager.createNativeQuery(IGIFIELDDESCRIPTIONDELETE);
				query.setParameter("LogicalInterfaceId", Long.parseLong(logicalInterfaceTypeId));
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
	}
	
	@Override
	public Long addIGIFieldDescription(IGIFieldDescription igiFieldDescription) throws Exception {
		try {
			manager.persist(igiFieldDescription);
			manager.flush();
			return igiFieldDescription.getFieldDescId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addIGIFieldDescription "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String IGILOGICALINTERFACECONNECTIONLIST ="SELECT a.LogicalInterfaceId, a.MsgCode, a.MsgName, a.LogicalChannelId, a.MsgType, a.MsgDescription, a.DataRate, a.Protocals, a.AdditionalInfo,\r\n"
			+ "	b.SourceId, b.DestinationId, b.LogicalChannel, b.ChannelCode, b.Description,\r\n"
			+ "	c.LevelName AS 'SLevelName', c.LevelCode AS 'SLevelCode', d.LevelName AS 'DLevelName', d.LevelCode AS 'DLevelCode'\r\n"
			+ "FROM pfms_igi_logical_interfaces a, pfms_igi_logical_channel b, pfms_system_product_tree c, pfms_system_product_tree d\r\n"
			+ "WHERE a.IsActive=1 AND a.LogicalChannelId = b.LogicalChannelId AND b.SourceId = c.MainId AND b.DestinationId = d.MainId";
	@Override
	public List<Object[]> getIGILogicalInterfaceConnectionList() throws Exception {
		try {
			Query query=manager.createNativeQuery(IGILOGICALINTERFACECONNECTIONLIST);
			return (List<Object[]>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}

	@Override
	public List<FieldGroupMaster> getFieldGroupMasterList() {
		try {
			Query query = manager.createQuery("FROM FieldGroupMaster WHERE IsActive=1");
			return (List<FieldGroupMaster>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FieldGroupMaster>();
		}
	}

	@Override
	public Long addFieldGroupMaster(FieldGroupMaster fieldGroup) throws Exception {
		try {
			manager.persist(fieldGroup);
			manager.flush();
			return fieldGroup.getFieldGroupId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addFieldGroupMaster "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public FieldGroupMaster getFieldGroupMasterById(Long fieldGroupId) throws Exception {
		try{
			return manager.find(FieldGroupMaster.class, fieldGroupId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getFieldGroupMasterById " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Long addFieldGroupLinked(FieldGroupLinked fieldGroupLinked) throws Exception {
		try {
			manager.persist(fieldGroupLinked);
			manager.flush();
			return fieldGroupLinked.getFieldGroupLinkedId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addFieldGroupLinked "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String FIELDGROUPLINKEDDELETE = "UPDATE pfms_field_group_linked SET IsActive='0' WHERE FieldMasterId=:FieldMasterId";
	@Override
	public int removeFieldGroupLinked(String fieldMasterId) throws Exception {
			try {
				Query query =manager.createNativeQuery(FIELDGROUPLINKEDDELETE);
				query.setParameter("FieldMasterId", Long.parseLong(fieldMasterId));
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
	}

	@Override
	public List<FieldGroupLinked> getFieldGroupLinkedList(String fieldMasterId) {
		try {
			Query query = manager.createQuery("FROM FieldGroupLinked WHERE IsActive=1 AND FieldMasterId=:FieldMasterId");
			query.setParameter("FieldMasterId", Long.parseLong(fieldMasterId));
			return (List<FieldGroupLinked>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FieldGroupLinked>();
		}
	}

	@Override
	public List<ICDMechanicalInterfaces> getICDMechanicalInterfacesList() {
		try {
			Query query = manager.createQuery("FROM ICDMechanicalInterfaces WHERE IsActive=1");
			return (List<ICDMechanicalInterfaces>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ICDMechanicalInterfaces>();
		}
	}

	@Override
	public ICDMechanicalInterfaces getICDMechanicalInterfacesById(Long mechInterfaceId) {
		try{
			return manager.find(ICDMechanicalInterfaces.class, mechInterfaceId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getICDMechanicalInterfacesById " + e);
			e.printStackTrace();
			return new ICDMechanicalInterfaces();
		}
	}
	
	@Override
	public Long addICDMechanicalInterfaces(ICDMechanicalInterfaces mechanicalInterfaces) {
		try {
			manager.persist(mechanicalInterfaces);
			manager.flush();
			return mechanicalInterfaces.getMechInterfaceId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDMechanicalInterfaces "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<IGIConnector> getConnectorMasterList() {
		try {
			Query query = manager.createQuery("FROM IGIConnector WHERE IsActive=1");
			return (List<IGIConnector>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIConnector>();
		}
	}
	
	@Override
	public IGIConnector getIGIConnectorById(Long dataTypeMasterId) throws Exception {
		try{
			return manager.find(IGIConnector.class, dataTypeMasterId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getIGIConnectorById " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addIGIConnector(IGIConnector connector) throws Exception {
		try {
			manager.persist(connector);
			manager.flush();
			return connector.getConnectorId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addIGIConnector "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public List<IGIConstants> getIGIConstantsMasterList() {
		try {
			Query query = manager.createQuery("FROM IGIConstants WHERE IsActive=1");
			return (List<IGIConstants>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIConstants>();
		}
	}
	
	@Override
	public IGIConstants getIGIConstantsById(Long constantId) throws Exception {
		try{
			return manager.find(IGIConstants.class, constantId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getIGIConstantsById " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public long addIGIConstants(IGIConstants constant) throws Exception {
		try {
			manager.persist(constant);
			manager.flush();
			return constant.getConstantId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addIGIConstants "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public List<IGIConnectorAttach> getIGIConnectorAttachList() {
		try {
			Query query = manager.createQuery("FROM IGIConnectorAttach WHERE IsActive=1");
			return (List<IGIConnectorAttach>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIConnectorAttach>();
		}
	}
	
	@Override
	public IGIConnectorAttach getIGIConnectorAttachById(String connectorAttachId) throws Exception {
		try{
			return manager.find(IGIConnectorAttach.class, Long.parseLong(connectorAttachId));
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getIGIConnectorAttachById " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addIGIConnectorAttach(IGIConnectorAttach attach) throws Exception {
		try {
			manager.persist(attach);
			manager.flush();
			return attach.getConnectorAttachId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addIGIConnectorAttach "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	public static final String DUPLICATEFIELDNAMECOUNT = "SELECT IFNULL(COUNT(FieldMasterId), 0) AS 'Count' FROM pfms_field_master WHERE FieldName=:FieldName AND FieldMasterId<>:FieldMasterId AND IsActive=1";
	@Override
	public Long getDuplicateFieldNameCount(String fieldName,String fieldMasterId) throws Exception {

		try {
			Query query = manager.createNativeQuery(DUPLICATEFIELDNAMECOUNT);
			query.setParameter("FieldName", fieldName);
			query.setParameter("FieldMasterId", fieldMasterId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateFieldNameCount "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static final String DUPLICATEGROUPNAMECOUNT = "SELECT IFNULL(COUNT(FieldGroupId), 0) AS 'Count' FROM pfms_field_group WHERE GroupName=:GroupName AND FieldGroupId<>:FieldGroupId AND IsActive=1";
	@Override
	public Long getDuplicateGroupNameCount(String groupName,String fieldGroupId) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(DUPLICATEGROUPNAMECOUNT);
			query.setParameter("GroupName", groupName);
			query.setParameter("FieldGroupId", fieldGroupId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateGroupNameCount "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	public static final String DUPLICATEGROUPCODECOUNT = "SELECT IFNULL(COUNT(FieldGroupId), 0) AS 'Count' FROM pfms_field_group WHERE GroupCode=:GroupCode AND FieldGroupId<>:FieldGroupId AND IsActive=1";
	@Override
	public Long getDuplicateGroupCodeCount(String groupCode,String fieldGroupId) throws Exception {
		
		try {
			Query query = manager.createNativeQuery(DUPLICATEGROUPCODECOUNT);
			query.setParameter("GroupCode", groupCode);
			query.setParameter("FieldGroupId", fieldGroupId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateGroupCodeCount "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	/* ************************************************ IGI Document End***************************************************** */
	
	/* ************************************************ ICD Document ***************************************************** */
	private static final String ICDDOCUMENTLIST = "SELECT a.ICDDocId, a.ICDVersion, a.LabCode, a.InitiatedBy, a.InitiatedDate, a.ICDStatusCode, a.ICDStatusCodeNext, a.CreatedBy, a.CreatedDate, a.Remarks, \r\n"
			+ "CONCAT(IFNULL(CONCAT(b.Title,' '),(IFNULL(CONCAT(b.Salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation, a.ProjectId, a.InitiationId, d.ReqStatus, d.ReqStatusColor, a.productTreeMainId \r\n"
			+ "FROM pfms_icd_document a JOIN employee b ON a.InitiatedBy=b.emp_id LEFT JOIN employee_desig c ON b.desig_id=c.desig_id LEFT JOIN pfms_req_approval_status d ON a.ICDStatusCode=d.ReqStatusCode WHERE a.IsActive=1 AND a.ProjectId=:ProjectId AND a.InitiationId=:InitiationId AND ProductTreeMainId=:ProductTreeMainId ORDER BY a.ICDDocId DESC";
	@Override
	public List<Object[]> getICDDocumentList(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query=manager.createNativeQuery(ICDDOCUMENTLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addPfmsICDDocument(PfmsICDDocument  pfmsIGIDocument) throws Exception
	{
		try {
		    manager.persist(pfmsIGIDocument);
		    manager.flush();
			return pfmsIGIDocument.getICDDocId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addPfmsICDDocument " + e);
			e.printStackTrace();
			return 0 ;
		}
	}
	
	@Override
	public PfmsICDDocument getPfmsICDDocumentById(String icdDocId) throws Exception {
		try {
			return manager.find(PfmsICDDocument.class, Long.parseLong(icdDocId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private static final String GETFIRSTVERSIONICDDOCID = "SELECT a.ICDDocId FROM pfms_icd_document a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND ProductTreeMainId=:ProductTreeMainId AND a.ICDVersion='1.0' AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionICDDocId(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONICDDOCID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (Long)query.getSingleResult();
			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DocumentsDAOImpl getFirstVersionICDDocId " + e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public long addICDDocumentConnections(ICDDocumentConnections connection) throws Exception {
		try {
			manager.persist(connection);
			manager.flush();
			
			return connection.getICDConnectionId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String GETICDCONEECTIONSLIST = "SELECT a.ICDConnectionId, a.ICDDocId, a.DrawingNo, a.DrawingAttach, \r\n"
			+ "	GROUP_CONCAT(DISTINCT e.MainId SEPARATOR ',') AS SubSystemIdsS1, \r\n"
			+ "	GROUP_CONCAT(DISTINCT f.MainId SEPARATOR ',') AS SubSystemIdsS2, \r\n"
			+ "	GROUP_CONCAT(DISTINCT CONCAT(e.LevelName,' (', e.LevelCode,')')  SEPARATOR ', ') AS LevelNamesS1, \r\n"
			+ "	GROUP_CONCAT(DISTINCT CONCAT(f.LevelName,' (', f.LevelCode,')')  SEPARATOR ', ') AS LevelNamesS2, \r\n"
			+ "	GROUP_CONCAT(DISTINCT e.LevelCode  SEPARATOR ',') AS LevelCodesS1, \r\n"
			+ "	GROUP_CONCAT(DISTINCT f.LevelCode  SEPARATOR ',') AS LevelCodesS2, \r\n"
			+ "	GROUP_CONCAT(DISTINCT CONCAT(e.MainId,'#',e.ElementType) SEPARATOR ',') AS ElementTypesS1, \r\n"
			+ "	GROUP_CONCAT(DISTINCT CONCAT(f.MainId,'#',f.ElementType) SEPARATOR ',') AS ElementTypesS2,\r\n"
			+ "	GROUP_CONCAT(DISTINCT c.InterfaceId SEPARATOR ',') AS InterfaceIds, \r\n"
			+ "	GROUP_CONCAT(DISTINCT d.InterfaceCode SEPARATOR ',') AS InterfaceCodes, \r\n"
			+ "	GROUP_CONCAT(DISTINCT d.InterfaceName SEPARATOR ', ') AS InterfaceNames, \r\n"
			+ "	GROUP_CONCAT(DISTINCT l.InterfaceType SEPARATOR ',') AS InterfaceTypes,\r\n"
			+ "	GROUP_CONCAT(DISTINCT d.ParameterData SEPARATOR ', ') AS ParameterDatas,\r\n"
			+ "	GROUP_CONCAT(DISTINCT k.Purpose SEPARATOR ', ') AS Purpose, GROUP_CONCAT(DISTINCT j.PurposeId SEPARATOR ',') AS PurposeIds	\r\n"
			+ "FROM pfms_icd_connections a \r\n"
			+ "LEFT JOIN pfms_icd_connection_systems b ON a.ICDConnectionId = b.ICDConnectionId \r\n"
			+ "LEFT JOIN pfms_product_tree e ON b.SubSystemId = e.MainId AND b.SubSystemType = 'A' \r\n"
			+ "LEFT JOIN pfms_product_tree f ON b.SubSystemId = f.MainId AND b.SubSystemType = 'B' \r\n"
			+ "LEFT JOIN pfms_icd_connection_interfaces c ON a.ICDConnectionId = c.ICDConnectionId AND c.IsActive = 1 \r\n"
			+ "LEFT JOIN pfms_igi_interfaces d ON c.InterfaceId = d.InterfaceId \r\n"
			+ "LEFT JOIN pfms_icd_connection_purpose j ON a.ICDConnectionId = j.ICDConnectionId AND j.IsActive = 1 \r\n"
			+ "LEFT JOIN pfms_icd_purpose k ON j.PurposeId = k.PurposeId \r\n"
			+ "LEFT JOIN pfms_igi_interface_types l ON l.InterfaceTypeId = d.InterfaceTypeId\r\n"
			+ "WHERE a.IsActive=1 AND b.IsActive=1 AND ICDDocId=:ICDDocId \r\n"
			+ "GROUP BY a.ICDConnectionId, a.ICDDocId, a.DrawingNo, a.DrawingAttach";
	@Override
	public List<ICDConnectionDTO> getICDConnectionsList(String icdDocId) throws Exception {
	    try {
	        Query query = manager.createNativeQuery(GETICDCONEECTIONSLIST);
	        query.setParameter("ICDDocId", icdDocId);
	        List<Object[]> results = query.getResultList();

	        List<ICDConnectionDTO> dtoList = new ArrayList<>();
	        if(results!=null && results.size()>0 && results.get(0)[0]!=null) {
		        for (Object[] row : results) {
		            ICDConnectionDTO dto = new ICDConnectionDTO();
	
		            int i = 0;
		            dto.setICDConnectionId(((Number) row[i++]).longValue());
		            dto.setICDDocId(((Number) row[i++]).longValue());
		            dto.setDrawingNo((String) row[i++]);
		            dto.setDrawingAttach((String) row[i++]);
		            
		            dto.setSubSystemIdsS1((String) row[i++]);
		            dto.setSubSystemIdsS2((String) row[i++]);
		            dto.setLevelNamesS1((String) row[i++]);
		            dto.setLevelNamesS2((String) row[i++]);
		            dto.setLevelCodesS1((String) row[i++]);
		            dto.setLevelCodesS2((String) row[i++]);
		            dto.setElementTypesS1((String) row[i++]);
		            dto.setElementTypesS2((String) row[i++]);
		            
		            dto.setInterfaceIds((String) row[i++]);
		            dto.setInterfaceCodes((String) row[i++]);
		            dto.setInterfaceNames((String) row[i++]);
		            dto.setInterfaceTypes((String) row[i++]);
		            dto.setParameterDatas((String) row[i++]);
		            
		            dto.setPurpose((String) row[i++]);
		            dto.setPurposeIds((String) row[i++]);
	
		            dtoList.add(dto);
		        }
	        }
	        return dtoList;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}


	private static final String DELETEICDCONNECTIONBYID = "UPDATE pfms_icd_connection_interfaces SET IsActive=0 WHERE ConInterfaceId=:ConInterfaceId";
	@Override
	public int deleteICDConnectionById(String conInterfaceId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONBYID);
			query.setParameter("ConInterfaceId", conInterfaceId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public long addPfmsIGITransaction(PfmsIGITransaction transaction) throws Exception {
		try {
		    manager.persist(transaction);
		    manager.flush();
			return transaction.getIGITransactionId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addPfmsIGITransaction " + e);
			e.printStackTrace();
			return 0 ;
		}
	}
	
	private static final String PRODUCTTREEALLLIST = "SELECT a.MainId, a.SubLevelId, a.LevelName, a.Stage, a.Module, a.RevisionNo, a.SystemMainId, a.LevelCode, CASE WHEN 0=:ProjectId THEN a.InitiationId ELSE a.ProjectId END AS 'ProjectId', a.ParentLevelId, a.LevelId, a.ElementType FROM pfms_product_tree a LEFT JOIN pfms_system_product_tree b ON b.MainId=a.SystemMainId WHERE a.MainId>0 AND a.ProjectId=:ProjectId AND a.InitiationId=:InitiationId AND a.IsActive='1' ORDER BY FIELD(a.ElementType, 'I', 'E'), a.SubLevelId";
	@Override
	public List<Object[]> getProductTreeAllList(String projectId, String initiationId)throws Exception
	{
		try {
			Query query=manager.createNativeQuery(PRODUCTTREEALLLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("InitiationId", initiationId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}
	
	private static final String ICDDOCUMENTUSERREVOKE = "UPDATE pfms_icd_document SET ICDStatusCode='REV', ICDStatusCodeNext='FWD' WHERE ICDDocId=:ICDDocId";
	@Override
	public int icdDocumentUserRevoke(String icdDocId) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(ICDDOCUMENTUSERREVOKE);
			query.setParameter("ICDDocId", icdDocId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public ICDDocumentConnections getICDDocumentConnectionsById(String icdConnectionId) throws Exception {
		try {
			return manager.find(ICDDocumentConnections.class, icdConnectionId!=null?Long.parseLong(icdConnectionId):0);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ICDPurpose> getAllICDPurposeList() throws Exception {
		try {
			Query query = manager.createQuery("FROM ICDPurpose WHERE IsActive=1");
			return (List<ICDPurpose>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ICDPurpose>();
		}
	}
	
	@Override
	public long addICDConnectionPurpose(ICDConnectionPurpose icdConnectionPurpose) throws Exception {
		try {
			manager.persist(icdConnectionPurpose);
			manager.flush();
			return icdConnectionPurpose.getConPurposeId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addICDConnectionPurpose " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	public static final String GETICDCONNECTIONSCOUNT = "SELECT IFNULL(COUNT(b.ConInterfaceId), 0) AS 'ConnectionCount' FROM pfms_icd_connections a, pfms_icd_connection_interfaces b WHERE a.ICDConnectionId=b.ICDConnectionId AND a.SubSystemMainIdOne=:SubSystemMainIdOne AND a.SubSystemMainIdTwo=:SubSystemMainIdTwo AND a.ICDDocId=:ICDDocId";
	@Override
	public int getICDConnectionsCount(Long subSystemMainIdOne, Long subSystemMainIdTwo, Long icdDocId) throws Exception {

		try {
			Query query =manager.createNativeQuery(GETICDCONNECTIONSCOUNT);
			query.setParameter("SubSystemMainIdOne", subSystemMainIdOne);
			query.setParameter("SubSystemMainIdTwo", subSystemMainIdTwo);
			query.setParameter("ICDDocId", icdDocId);
			Long maxCount = (Long)query.getSingleResult();
			return maxCount.intValue();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getICDConnectionsCount "+ e);
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String DELETEICDCONNECTIONPURPOSEBYICDCONNECTIONID = "UPDATE pfms_icd_connection_purpose SET IsActive=0 WHERE ICDConnectionId=:ICDConnectionId";
	@Override
	public int deleteICDConnectionPurposeByICDConnectionId(String icdConnectionId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONPURPOSEBYICDCONNECTIONID);
			query.setParameter("ICDConnectionId", icdConnectionId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public ICDPurpose getICDPurposeById(String purposeId) throws Exception {
		try {
		  return manager.find(ICDPurpose.class, Long.parseLong(purposeId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addICDPurpose(ICDPurpose purpose) throws Exception {
		try {
			manager.persist(purpose);
			manager.flush();
			return purpose.getPurposeId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDPurpose "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String ICDMECHINTERFACECONNECTIONLIST = "SELECT a.MechInterfaceId, a.ICDDocId, a.SubSystemMainIdOne, a.SubSystemMainIdTwo, a.InterfaceCode, a.InterfaceName, a.InterfaceSeqId, a.partOne, a.partTwo, a.DrawingOne, a.DrawingTwo, a.Description, a.Standards, a.Precautions, a.Instructions, a.Remarks, b.LevelName AS 'LevelNameS1', c.LevelName AS 'LevelNameS2', b.LevelCode AS 'LevelCodeS1', c.LevelCode AS 'LevelCodeS2' FROM pfms_icd_mech_interfaces a, pfms_product_tree b, pfms_product_tree c WHERE a.IsActive=1 AND a.SubSystemMainIdOne = b.MainId AND a.SubSystemMainIdTwo = c.MainId AND a.ICDDocId=:ICDDocId";
	@Override
	public List<Object[]> getICDMechInterfaceConnectionList(String icdDocId) throws Exception {
		try {
			Query query=manager.createNativeQuery(ICDMECHINTERFACECONNECTIONLIST);
			query.setParameter("ICDDocId", Long.parseLong(icdDocId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public long addICDConnectionSystems(ICDConnectionSystems subsystems) throws Exception {
		try {
			manager.persist(subsystems);
			manager.flush();
			return subsystems.getConSystemsId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectionSystems "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public long addICDConnectionInterfaces(ICDConnectionInterfaces interfaces) throws Exception {
		try {
			manager.persist(interfaces);
			manager.flush();
			return interfaces.getConInterfaceId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectionInterfaces "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String DELETEICDCONNECTIONSYSTEMSBYICDCONNECTIONID = "UPDATE pfms_icd_connection_systems SET IsActive=0 WHERE SubSystemType=:SubSystemType AND ICDConnectionId=:ICDConnectionId";
	@Override
	public int deleteICDConnectionSystemsByICDConnectionId(String icdConnectionId, String subSystemType) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONSYSTEMSBYICDCONNECTIONID);
			query.setParameter("ICDConnectionId", icdConnectionId);
			query.setParameter("SubSystemType", subSystemType);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String DELETEICDCONNECTIONINTERFACEBYICDCONNECTIONID = "UPDATE pfms_icd_connection_interfaces SET IsActive=0 WHERE ICDConnectionId=:ICDConnectionId";
	@Override
	public int deleteICDConnectionInterfaceByICDConnectionId(String icdConnectionId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONINTERFACEBYICDCONNECTIONID);
			query.setParameter("ICDConnectionId", icdConnectionId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public long addICDConnectionConnectors(ICDConnectionConnectors connectors) {
		try {
			manager.persist(connectors);
			manager.flush();
			return connectors.getICDConnectorId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectionConnectors "+ e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	@Override
	public long addICDConnectorPins(ICDConnectorPins pins) {
		try {
			manager.persist(pins);
			manager.flush();
			return pins.getConnectorPinId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectorPins "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	public static final String DUPLICATECONNECTORCOUNT = "SELECT IFNULL(COUNT(ICDConnectorId), 0) AS MaxCount FROM pfms_icd_connection_connectors WHERE ICDConnectionId=:ICDConnectionId AND ConnectorNo=:ConnectorNo AND SystemType=:SystemType AND SubSystemId=:SubSystemId AND ICDConnectorId<>:ICDConnectorId";
	@Override
	public Long getDuplicateConnectorCount(String icdConnectionId, String connectorNo, String systemType, String subSystemId, String icdConnectorId) throws Exception {

		try {
			
			Query query =manager.createNativeQuery(DUPLICATECONNECTORCOUNT);
			query.setParameter("ICDConnectionId", icdConnectionId);
			query.setParameter("ConnectorNo", connectorNo);
			query.setParameter("SystemType", systemType);
			query.setParameter("SubSystemId", subSystemId);
			query.setParameter("ICDConnectorId", icdConnectorId);
			
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getDuplicateConnectorCount "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String ICDCONNECTORLIST = "SELECT a.ICDConnectorId, a.ICDConnectionId, a.ConnectorNo, a.SystemType, a.SubSystemId, b.LevelName, b.LevelCode FROM pfms_icd_connection_connectors a, pfms_product_tree b WHERE a.IsActive=1 AND b.IsActive=1 AND a.SubSystemId=b.MainId AND a.ICDConnectionId=:ICDConnectionId";
	@Override
	public List<Object[]> getICDConnectorList(String icdConnectionId) throws Exception {
		try {
			Query query=manager.createNativeQuery(ICDCONNECTORLIST);
			query.setParameter("ICDConnectionId", icdConnectionId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public List<ICDConnectionConnectors> getAllICDConnectorList() {
		try {
			Query query = manager.createQuery("FROM ICDConnectionConnectors WHERE IsActive=1");
			return (List<ICDConnectionConnectors>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ICDConnectionConnectors>();
		}
	}
	
	private static final String ICDCONNECTORPINLIST = "SELECT a.ICDConnectorId, a.ICDConnectionId, a.ConnectorNo, a.SystemType, a.SubSystemId, c.LevelName, c.LevelCode, b.ConnectorPinId, b.PinNo, b.InterfaceId, b.Constraints, b.Periodicity, b.Description, d.InterfaceCode, d.InterfaceName, a.ConnectorId, e.PartNo, e.ConnectorMake, e.StandardName, e.Protection, e.RefInfo, e.Remarks, e.PinCount, b.GroupName \r\n"
			+ "FROM pfms_icd_connection_connectors a JOIN pfms_icd_connector_pins b ON a.ICDConnectorId=b.ICDConnectorId JOIN pfms_product_tree c ON a.SubSystemId=c.MainId LEFT JOIN pfms_igi_interfaces d ON b.InterfaceId=d.InterfaceId LEFT JOIN pfms_igi_connector e ON e.ConnectorId=a.ConnectorId \r\n"
			+ "WHERE a.IsActive=1 AND b.IsActive=1 AND a.ICDConnectionId=:ICDConnectionId";
	@Override
	public List<Object[]> getICDConnectorPinListByICDConnectionId(String icdConnectionId) throws Exception {
		try {
			Query query=manager.createNativeQuery(ICDCONNECTORPINLIST);
			query.setParameter("ICDConnectionId", icdConnectionId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}

	@Override
	public ICDConnectionConnectors getICDConnectionConnectorsById(String icdConnectorId) throws Exception {
		try {
			return manager.find(ICDConnectionConnectors.class, Long.parseLong(icdConnectorId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String DELETEICDCONNECTIONCONNECTORPINDETAILS = "UPDATE pfms_icd_connector_pins SET IsActive=0 WHERE ICDConnectorId=:ICDConnectorId";
	@Override
	public int deleteICDConnectionConnectorPinDetails(String icdConnectorId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONCONNECTORPINDETAILS);
			query.setParameter("ICDConnectorId", icdConnectorId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public ICDConnectorPinMapping getICDConnectorPinMappingById(String connectorPinMapId) throws Exception {
		try {
			return manager.find(ICDConnectorPinMapping.class, Long.parseLong(connectorPinMapId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addICDConnectorPinMapping(ICDConnectorPinMapping pinMap) {
		try {
			manager.persist(pinMap);
			manager.flush();
			return pinMap.getConnectorPinMapId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectorPinMapping "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String ICDCONNECTORPINMAPLIST = "CALL pfms_icd_con_map_list(:ICDDocId)";
	@Override
	public List<ICDPinMapDTO> getICDConnectorPinMapListByICDDocId(String icdDocId) throws Exception {
	    try {
	        Query query = manager.createNativeQuery(ICDCONNECTORPINMAPLIST);
			query.setParameter("ICDDocId", icdDocId);
	        List<Object[]> results = query.getResultList();

	        List<ICDPinMapDTO> dtoList = new ArrayList<>();
	        if(results!=null && results.size()>0 && results.get(0)[0]!=null) {
		        for (Object[] row : results) {
		        	ICDPinMapDTO dto = new ICDPinMapDTO();
	
		            int i = 0;
		            dto.setConnectorPinMapId(((Number) row[i++]).longValue());
		            dto.setICDConnectionId(((Number) row[i++]).longValue());
		            dto.setPinFunction((String) row[i++]);
		            dto.setSignalName((String) row[i++]);
		            dto.setConnectionCode((String) row[i++]);
		            dto.setCableMaxLength(((Number) row[i++]).intValue());
		            dto.setInterfaceLoss(((Number) row[i++]).intValue());
		            dto.setCableBendingRadius(((Number) row[i++]).doubleValue());
		            dto.setRemarks((String) row[i++]);
		            
		            dto.setConPinMappedIdE1s((String) row[i++]);
		            dto.setPinIdE1s((String) row[i++]);
		            dto.setConnectorIdE1s((String) row[i++]);
		            dto.setPinNoE1s((String) row[i++]);
		            dto.setConPinMappedIdE2s((String) row[i++]);
		            dto.setPinIdE2s((String) row[i++]);
		            dto.setConnectorIdE2s((String) row[i++]);
		            dto.setPinNoE2s((String) row[i++]);
		            
		            dto.setConnectorNoE1(((Number) row[i++]).intValue());
		            dto.setConnectorNoE2(((Number) row[i++]).intValue());
		            dto.setSubSystemIdE1(((Number) row[i++]).longValue());
		            dto.setSubSystemIdE2(((Number) row[i++]).longValue());
		            
		            dto.setLevelNameE1((String) row[i++]);
		            dto.setLevelCodeE1((String) row[i++]);
		            dto.setLevelNameE2((String) row[i++]);
		            dto.setLevelCodeE2((String) row[i++]);
		            
		            dto.setInterfaceId(((Number) row[i++]).longValue());
		            dto.setConstraints((String) row[i++]);
		            dto.setPeriodicity((String) row[i++]);
		            dto.setDescription((String) row[i++]);
		            dto.setInterfaceName((String) row[i++]);
		            dto.setInterfaceCode((String) row[i++]);
		            dto.setInterfaceType((String) row[i++]);
		            dto.setInterfaceContent((String) row[i++]);
	
		            dtoList.add(dto);
		        }
	        }
	        return dtoList;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}

	@Override
	public ICDConnectorPins getICDConnectorPinsById(String connectorPinId) throws Exception {
		try {
			return manager.find(ICDConnectorPins.class, Long.parseLong(connectorPinId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String DELETEICDCONNECTIONCONNECTORBYID = "UPDATE pfms_icd_connection_connectors SET IsActive=0 WHERE ICDConnectorId=:ICDConnectorId";
	@Override
	public int deleteICDConnectionConnectorById(String icdConnectorId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTIONCONNECTORBYID);
			query.setParameter("ICDConnectorId", icdConnectorId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<ICDSystemAttach> getICDSystemAttachListByICDDocId(String icdDocId) {
		try {
			Query query = manager.createQuery("FROM ICDSystemAttach WHERE IsActive=1 AND ICDDocId=:ICDDocId");
			query.setParameter("ICDDocId", Long.parseLong(icdDocId));
			return (List<ICDSystemAttach>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ICDSystemAttach>();
		}
	}

	@Override
	public ICDSystemAttach getICDSystemAttachById(String systemAttachId) throws Exception {
		try {
			return manager.find(ICDSystemAttach.class, Long.parseLong(systemAttachId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addICDSystemAttach(ICDSystemAttach systemAttach) {
		try {
			manager.persist(systemAttach);
			manager.flush();
			return systemAttach.getSystemAttachId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDSystemAttach "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public long addICDConnectorMappedPins(ICDConnectorMappedPins mappedPins) {
		try {
			manager.persist(mappedPins);
			manager.flush();
			return mappedPins.getConPinMappedId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addICDConnectorMappedPins "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String DELETEICDCONNECTORMAPPEDPINBYCONNECTORPINMAPID = "DELETE FROM pfms_icd_connector_mapped_pins WHERE ConnectorPinMapId=:ConnectorPinMapId";
	@Override
	public int deleteICDConnectorMappedPinByConnectorPinMapId(Long connectorPinMapId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEICDCONNECTORMAPPEDPINBYCONNECTORPINMAPID);
			query.setParameter("ConnectorPinMapId", connectorPinMapId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/* ************************************************ ICD Document End***************************************************** */
	
	/* ************************************************ IRS Document ***************************************************** */
	
	private static final String IRSDOCUMENTLIST = "SELECT a.IRSDocId, a.IRSVersion, a.LabCode, a.InitiatedBy, a.InitiatedDate, a.IRSStatusCode, a.IRSStatusCodeNext, a.CreatedBy, a.CreatedDate, a.Remarks, \r\n"
			+ "CONCAT(IFNULL(CONCAT(b.Title,' '),(IFNULL(CONCAT(b.Salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation, a.ProjectId, a.InitiationId, d.ReqStatus, d.ReqStatusColor, a.ProductTreeMainId \r\n"
			+ "FROM pfms_irs_document a JOIN employee b ON a.InitiatedBy=b.emp_id LEFT JOIN employee_desig c ON b.desig_id=c.desig_id LEFT JOIN pfms_req_approval_status d ON a.IRSStatusCode=d.ReqStatusCode WHERE a.IsActive=1 AND a.ProjectId=:ProjectId AND a.InitiationId=:InitiationId AND a.ProductTreeMainId=:ProductTreeMainId ORDER BY a.IRSDocId DESC";
	@Override
	public List<Object[]> getIRSDocumentList(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query=manager.createNativeQuery(IRSDOCUMENTLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addPfmsIRSDocument(PfmsIRSDocument  pfmsIRSDocument) throws Exception
	{
		try {
		    manager.persist(pfmsIRSDocument);
		    manager.flush();
			return pfmsIRSDocument.getIRSDocId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addPfmsIRSDocument " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	@Override
	public PfmsIRSDocument getPfmsIRSDocumentById(String irsDocId) throws Exception {
		try {
			return manager.find(PfmsIRSDocument.class, Long.parseLong(irsDocId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String GETFIRSTVERSIONIRSDOCID = "SELECT a.IRSDocId FROM pfms_irs_document a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.IRSVersion='1.0' AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionIRSDocId(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONIRSDOCID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (Long)query.getSingleResult();
			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DocumentsDAOImpl getFirstVersionIRSDocId " + e);
			e.printStackTrace();
			return 0L;
		}
	}

	@Override
	public long addIRSDocumentSpecifications(IRSDocumentSpecifications irsDocumentSpecifications) throws Exception
	{
		try {
		    manager.persist(irsDocumentSpecifications);
		    manager.flush();
			return irsDocumentSpecifications.getIRSSpecificationId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addIRSDocumentSpecifications " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	private static final String GETIRSDOCUMENTSPECSLIST = "SELECT a.IRSSpecificationId, a.IRSDocId, a.ConnectorPinMapId, a.LogicalInterfaceId, '' AS InfoName, a.ActionAtDest, b.ConnectionCode, c.MsgCode, c.MsgName,\r\n"
			+ "	GROUP_CONCAT(DISTINCT f.PinNo SEPARATOR ', ') AS PinsFrom, GROUP_CONCAT(DISTINCT g.PinNo SEPARATOR ', ') AS PinsTo\r\n"
			+ "FROM pfms_irs_document_specifications a\r\n"
			+ "LEFT JOIN pfms_icd_connector_pin_mapping b ON a.ConnectorPinMapId = b.ConnectorPinMapId\r\n"
			+ "LEFT JOIN pfms_igi_logical_interfaces c ON a.LogicalInterfaceId=c.LogicalInterfaceId\r\n"
			+ "LEFT JOIN pfms_icd_connector_mapped_pins d ON a.ConnectorPinMapId = d.ConnectorPinMapId AND d.ConnectorPinType='A' AND d.IsActive=1\r\n"
			+ "LEFT JOIN pfms_icd_connector_mapped_pins e ON a.ConnectorPinMapId = e.ConnectorPinMapId AND e.ConnectorPinType='B' AND e.IsActive=1\r\n"
			+ "LEFT JOIN pfms_icd_connector_pins f ON d.ConnectorPinId = f.ConnectorPinId LEFT JOIN pfms_icd_connector_pins g ON e.ConnectorPinId = g.ConnectorPinId\r\n"
			+ "WHERE a.IsActive=1 AND a.IRSDocId=:IRSDocId \r\n"
			+ "GROUP BY a.IRSSpecificationId, a.IRSDocId, a.ConnectorPinMapId, a.LogicalInterfaceId, a.ActionAtDest, b.ConnectionCode, c.MsgCode, c.MsgName";
	@Override
	public List<Object[]> getIRSDocumentSpecificationsList(String irsDocId)throws Exception {
		try {
			Query query=manager.createNativeQuery(GETIRSDOCUMENTSPECSLIST);
			query.setParameter("IRSDocId", irsDocId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
		
	}
	
	private static final String DELETEIRSSPECIFICATIONBYID = "UPDATE pfms_irs_document_specifications SET IsActive=0 WHERE IRSSpecificationId=:IRSSpecificationId";
	@Override
	public int deleteIRSSpecifiactionById(String irsSpecificationId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEIRSSPECIFICATIONBYID);
			query.setParameter("IRSSpecificationId", irsSpecificationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String IRSDOCUMENTUSERREVOKE = "UPDATE pfms_irs_document SET IRSStatusCode='REV', IRSStatusCodeNext='FWD' WHERE IRSDocId=:IRSDocId";
	@Override
	public int irsDocumentUserRevoke(String irsDocId) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(IRSDOCUMENTUSERREVOKE);
			query.setParameter("IRSDocId", irsDocId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String GETDATACARRYINGCONNECTIONS = "SELECT a.ConnectorPinMapId, a.ConnectionCode, a.ICDConnectionId, a.InterfaceId\r\n"
			+ "FROM pfms_icd_connector_pin_mapping a JOIN pfms_igi_interfaces b ON a.InterfaceId = b.InterfaceId JOIN pfms_icd_connections c ON a.ICDConnectionId = c.ICDConnectionId\r\n"
			+ "WHERE a.IsActive = 1 AND b.InterfaceContentId IN (SELECT d.InterfaceContentId FROM pfms_igi_interface_content d WHERE d.IsDataCarrying = 'Y' AND d.IsActive = 1)\r\n"
			+ "	AND EXISTS ( SELECT 1 FROM pfms_icd_connector_mapped_pins e LEFT JOIN pfms_icd_connector_pins f ON e.ConnectorPinId = f.ConnectorPinId\r\n"
			+ "		LEFT JOIN pfms_icd_connection_connectors g ON g.ICDConnectorId = f.ICDConnectorId LEFT JOIN pfms_product_tree h ON h.MainId = g.SubSystemId AND h.IsActive = 1\r\n"
			+ "		WHERE a.ConnectorPinMapId = e.ConnectorPinMapId AND b.IsActive=1 AND h.SystemMainId IN (:SystemMainIdOne, :SystemMainIdTwo) ) AND c.ICDDocId=:ICDDocId";
	@Override
	public List<Object[]> getDataCarryingConnectionList(String icdDocId, String systemMainIdOne, String systemMainIdTwo)throws Exception {
		try {
			Query query=manager.createNativeQuery(GETDATACARRYINGCONNECTIONS);
			query.setParameter("ICDDocId", icdDocId);
			query.setParameter("SystemMainIdOne", systemMainIdOne);
			query.setParameter("SystemMainIdTwo", systemMainIdTwo);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	@Override
	public IRSDocumentSpecifications getIRSDocumentSpecificationsById(String irsSpecificationId) throws Exception {
		try {
			return manager.find(IRSDocumentSpecifications.class, Long.parseLong(irsSpecificationId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addIRSFieldDescription(IRSFieldDescription irsFieldDescription) {
		try {
		    manager.persist(irsFieldDescription);
		    manager.flush();
			return irsFieldDescription.getIRSFieldDescId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addIRSFieldDescription " + e);
			e.printStackTrace();
			return 0 ;
		}
	}

	private static final String GETIRSFIELDDESCRIPTIONLIST = "SELECT a.IRSFieldDescId, a.LogicalInterfaceId, a.FieldMasterId, a.DataTypeMasterId, c.DataLength, a.FieldDesc, a.Quantum, a.Remarks, a.FieldName, a.FieldShortName, a.FieldCode, c.DataTypePrefix, a.FieldGroupId, a.TypicalValue, a.FieldMinValue, a.FieldMaxValue, a.InitValue, a.FieldOffSet, a.FieldUnit, a.IRSSpecificationId, a.ArrayMasterId, d.ArrayName, d.ArrayValue, e.GroupName, e.GroupCode, e.GroupType, a.GroupVariable, c.AliasName, c.DataStandardName, a.FieldSlNo\r\n"
			+ "FROM pfms_irs_field_desc a JOIN pfms_irs_document_specifications b ON a.IRSSpecificationId = b.IRSSpecificationId AND b.IsActive=1\r\n"
			+ "JOIN pfms_data_type_master c ON a.DataTypeMasterId = c.DataTypeMasterId\r\n"
			+ "LEFT JOIN pfms_irs_array_master d ON a.ArrayMasterId = d.ArrayMasterId\r\n"
			+ "LEFT JOIN pfms_field_group e ON a.FieldGroupId = e.FieldGroupId\r\n"
			+ "JOIN ( SELECT FieldGroupId, MIN(IRSFieldDescId) AS first_id FROM pfms_irs_field_desc WHERE IsActive = 1 GROUP BY FieldGroupId) g ON a.FieldGroupId = g.FieldGroupId\r\n"
			+ "WHERE a.IsActive = 1 AND b.IRSDocId =:IRSDocId ORDER BY CASE WHEN a.FieldGroupId = 0 THEN a.IRSFieldDescId ELSE g.first_id END, a.IRSFieldDescId";
	@Override
	public List<Object[]> getIRSFieldDescriptionList(String irsDocId)throws Exception {
		try {
			Query query=manager.createNativeQuery(GETIRSFIELDDESCRIPTIONLIST);
			query.setParameter("IRSDocId", Long.parseLong(irsDocId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String IRSFIELDDESCRIPTIONDELETE = "UPDATE pfms_irs_field_desc SET IsActive='0' WHERE IRSSpecificationId=:IRSSpecificationId AND FieldGroupId=:FieldGroupId";
	@Override
	public int removeIRSFieldDescription(String irsSpecificationId, String fieldGroupId) throws Exception {
		try {
			Query query =manager.createNativeQuery(IRSFIELDDESCRIPTIONDELETE);
			query.setParameter("IRSSpecificationId", Long.parseLong(irsSpecificationId));
			query.setParameter("FieldGroupId", Long.parseLong(fieldGroupId));
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static final String GETIRSFIELDDESCRIPTIONENTITYLIST = "SELECT a FROM IRSFieldDescription a, IRSDocumentSpecifications b WHERE a.IsActive=1 AND b.IsActive=1 AND a.IRSSpecificationId = b.IRSSpecificationId AND b.IRSDocId=:IRSDocId";
	@Override
	public List<IRSFieldDescription> getIRSFieldDescriptionEntityList(String irsDocId) {
		try {
			Query query = manager.createQuery(GETIRSFIELDDESCRIPTIONENTITYLIST);
			query.setParameter("IRSDocId", Long.parseLong(irsDocId));
			return (List<IRSFieldDescription>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IRSFieldDescription>();
		}
	}
	
	@Override
	public IRSFieldDescription getIRSFieldDescriptionById(String irsFieldDescId) {
		try {
			return manager.find(IRSFieldDescription.class, Long.parseLong(irsFieldDescId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String IRSFIELDDESCRIPTIONUPDATE = "UPDATE pfms_irs_field_desc SET GroupVariable=:GroupVariable, ArrayMasterId=:ArrayMasterId WHERE IRSSpecificationId=:IRSSpecificationId AND FieldGroupId=:FieldGroupId";
	@Override
	public int updateIRSFieldDescription(String groupVariable, String arrayMasterId, String irsSpecificationId, String fieldGroupId) throws Exception {
		try {
			Query query =manager.createNativeQuery(IRSFIELDDESCRIPTIONUPDATE);
			query.setParameter("GroupVariable", groupVariable);
			query.setParameter("ArrayMasterId", Long.parseLong(arrayMasterId));
			query.setParameter("IRSSpecificationId", Long.parseLong(irsSpecificationId));
			query.setParameter("FieldGroupId", Long.parseLong(fieldGroupId));
			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<IRSArrayMaster> getIRSArrayMasterListByIRSDocId(String irsDocId) {
		try {
			Query query = manager.createQuery("FROM IRSArrayMaster WHERE IsActive=1 AND IRSDocId=:IRSDocId");
			query.setParameter("IRSDocId", Long.parseLong(irsDocId));
			return (List<IRSArrayMaster>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IRSArrayMaster>();
		}
	}

	@Override
	public IRSArrayMaster getIRSArrayMasterById(String arrayMasterId) throws Exception {
		try {
			return manager.find(IRSArrayMaster.class, Long.parseLong(arrayMasterId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long addIRSArrayMaster(IRSArrayMaster arrayMaster) throws Exception {
		try {
			manager.persist(arrayMaster);
			manager.flush();
			return arrayMaster.getArrayMasterId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addIRSArrayMaster " + e);
			e.printStackTrace();
			return 0;
		}
	}
	
	/* ************************************************ IRS Document End***************************************************** */
	
	/* ************************************************ IDD Document ***************************************************** */
	
	private static final String IDDDOCUMENTLIST = "SELECT a.IDDDocId, a.IDDVersion, a.LabCode, a.InitiatedBy, a.InitiatedDate, a.IDDStatusCode, a.IDDStatusCodeNext, a.CreatedBy, a.CreatedDate, a.Remarks, \r\n"
			+ "CONCAT(IFNULL(CONCAT(b.Title,' '),(IFNULL(CONCAT(b.Salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation, a.ProjectId, a.InitiationId, d.ReqStatus, d.ReqStatusColor, a.ProductTreeMainId \r\n"
			+ "FROM pfms_idd_document a JOIN employee b ON a.InitiatedBy=b.emp_id LEFT JOIN employee_desig c ON b.desig_id=c.desig_id LEFT JOIN pfms_req_approval_status d ON a.IDDStatusCode=d.ReqStatusCode WHERE a.IsActive=1 AND a.ProjectId=:ProjectId AND a.InitiationId=:InitiationId AND a.ProductTreeMainId=:ProductTreeMainId ORDER BY a.IDDDocId DESC";
	@Override
	public List<Object[]> getIDDDocumentList(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query=manager.createNativeQuery(IDDDOCUMENTLIST);
			query.setParameter("ProjectId", projectId);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}
	
	@Override
	public long addPfmsIDDDocument(PfmsIDDDocument  pfmsIDDDocument) throws Exception
	{
		try {
			manager.persist(pfmsIDDDocument);
			manager.flush();
			return pfmsIDDDocument.getIDDDocId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addPfmsIDDDocument " + e);
			e.printStackTrace();
			return 0 ;
		}
	}
	
	
	@Override
	public PfmsIDDDocument getPfmsIDDDocumentById(String irsDocId) throws Exception {
		try {
			return manager.find(PfmsIDDDocument.class, Long.parseLong(irsDocId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String GETFIRSTVERSIONIDDDOCID = "SELECT a.IDDDocId FROM pfms_idd_document a WHERE a.InitiationId=:InitiationId AND a.ProjectId=:ProjectId AND a.ProductTreeMainId=:ProductTreeMainId AND a.IDDVersion='1.0' AND a.IsActive=1 LIMIT 1";
	@Override
	public Long getFirstVersionIDDDocId(String projectId, String initiationId, String productTreeMainId) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETFIRSTVERSIONIDDDOCID);
			query.setParameter("InitiationId", initiationId);
			query.setParameter("ProjectId", projectId);
			query.setParameter("ProductTreeMainId", productTreeMainId);
			return (Long)query.getSingleResult();
			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DocumentsDAOImpl getFirstVersionIDDDocId " + e);
			e.printStackTrace();
			return 0L;
		}
	}
	
	private static final String IDDDOCUMENTUSERREVOKE = "UPDATE pfms_idd_document SET IDDStatusCode='REV', IDDStatusCodeNext='FWD' WHERE IDDDocId=:IDDDocId";
	@Override
	public int iddDocumentUserRevoke(String iddDocId) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(IDDDOCUMENTUSERREVOKE);
			query.setParameter("IDDDocId", iddDocId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<IGIDocumentIntroduction> getIGIDocumentIntroductionList() throws Exception {
		try {
			Query query = manager.createQuery("FROM IGIDocumentIntroduction WHERE IsActive=1");
			return (List<IGIDocumentIntroduction>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IGIDocumentIntroduction>();
		}
	}
	
	@Override
	public IGIDocumentIntroduction getIGIDocumentIntroductionById(String introductionId) throws Exception {
		try {
			return manager.find(IGIDocumentIntroduction.class, Long.parseLong(introductionId));
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl getIGIDocumentIntroductionById "+ e);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Long addIGIDocumentIntroduction(IGIDocumentIntroduction introduction) throws Exception {
		try {
			manager.persist(introduction);
			manager.flush();
			return introduction.getIntroductionId();
		}catch (Exception e) {
			logger.error(new Date() +"Inside DocumentsDaoImpl addIGIDocumentIntroduction "+ e);
			e.printStackTrace();
			return 0L;
		}
	}

	private static final String DELETEIGIINTRODUCTIONBYID = "UPDATE pfms_igi_document_introduction SET IsActive=0 WHERE IntroductionId=:IntroductionId";
	@Override
	public int deleteIGIIntroduction(String introductionId) throws Exception {
		try {
			Query query=manager.createNativeQuery(DELETEIGIINTRODUCTIONBYID);
			query.setParameter("IntroductionId", introductionId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static final String INTERFACECOUNTBYPARENTID = "SELECT IFNULL(COUNT(InterfaceId),0) AS 'MaxCount' FROM pfms_igi_interfaces WHERE ParentId=:ParentId";
	@Override
	public Long getInterfaceCountByParentId(Long parentId) throws Exception {

		try {
			Query query =manager.createNativeQuery(INTERFACECOUNTBYPARENTID);
			query.setParameter("ParentId", parentId);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getInterfaceCountByParentId "+ e);
			e.printStackTrace();
			return null;
		}
	}

	private static final String SYSTEMPRODUCTTREELIST = "SELECT a.MainId, a.SubLevelId, a.LevelName, a.Stage, a.Module, a.RevisionNo, a.Sid, a.LevelCode, a.ParentLevelId, a.LevelId, a.LevelType FROM pfms_system_product_tree a WHERE a.MainId>0 AND a.IsActive='1' ORDER BY a.SubLevelId";
	@Override
	public List<Object[]> getSystemProductTreeAllList()throws Exception
	{
		try {
			Query query=manager.createNativeQuery(SYSTEMPRODUCTTREELIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}
	
	private static final String IGILOGICALCHANNELLIST = "SELECT a.LogicalChannelId, a.SourceId, a.DestinationId, a.LogicalChannel, a.ChannelCode, a.Description, b.LevelName AS 'SourceName', b.LevelCode AS 'SourceCode', c.LevelName AS 'DestName', c.LevelCode AS 'DestCode' FROM pfms_igi_logical_channel a, pfms_system_product_tree b, pfms_system_product_tree c WHERE a.SourceId=b.MainId AND a.DestinationId=c.MainId AND a.IsActive=1 ORDER BY a.LogicalChannelId DESC";
	@Override
	public List<Object[]> getLogicalChannelList() throws Exception {
		try {
			Query query = manager.createNativeQuery(IGILOGICALCHANNELLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	public static final String LOGICALCHANNELCOUNT = "SELECT IFNULL(COUNT(LogicalChannelId), 0) AS 'Count' FROM pfms_igi_logical_channel";
	@Override
	public int getLogicalChannelCount() throws Exception {

		try {
			Query query = manager.createNativeQuery(LOGICALCHANNELCOUNT);
			Long count = (Long)query.getSingleResult();
			return count.intValue();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getLogicalChannelCount "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	private static final String GETFIELDDESCRIPTIONLIST = "SELECT a.FieldDescId, a.LogicalInterfaceId, a.FieldMasterId, b.DataTypeMasterId, c.DataLength, b.FieldDesc, b.Quantum, b.Remarks, b.FieldName, b.FieldShortName, b.FieldCode, c.DataTypePrefix, a.FieldGroupId, b.TypicalValue, b.FieldMinValue, b.FieldMaxValue, b.InitValue, b.FieldOffSet, b.FieldUnit, c.AliasName, c.DataStandardName FROM pfms_igi_field_desc a, pfms_field_master b, pfms_data_type_master c WHERE a.IsActive=1 AND a.FieldMasterId = b.FieldMasterId AND b.DataTypeMasterId=c.DataTypeMasterId";
	@Override
	public List<Object[]> getIGIFieldDescriptionList()throws Exception {
		try {
			Query query=manager.createNativeQuery(GETFIELDDESCRIPTIONLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}
	
	public static final String MECHANICALINTERFACECOUNT = "SELECT IFNULL(COUNT(MechInterfaceId), 0) AS 'Count' FROM pfms_icd_mech_interfaces";
	@Override
	public long getMechanicalInterfaceCount() throws Exception {

		try {
			Query query = manager.createNativeQuery(MECHANICALINTERFACECOUNT);
			return (Long)query.getSingleResult();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl getMechanicalInterfaceCount "+ e);
			e.printStackTrace();
			return 0;
		}
	}

	/*********************************************** Naveen 4/9/25 **************************************************/
	
	@Override
	public UnitMaster getUnitMasterById(long unitMasterId) throws Exception {
		try{
			return manager.find(UnitMaster.class, unitMasterId);
		}catch (Exception e){
			logger.error(new Date()  + "Inside DAO getUnitMasterById " + e);
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public long addUnitMaster(UnitMaster unitmaster) throws Exception{
		try {
		    manager.persist(unitmaster);
		    manager.flush();
			return unitmaster.getUnitMasterId();
		}
		catch (Exception e) {
			logger.error(new Date()  + " Inside DocumentsDAOImpl addUnitMaster " + e);
			e.printStackTrace();
			return 0 ;
		}
	}


	@Override
	public List<Object[]> unitMasterList() throws Exception{
		try {
			Query query = manager.createNativeQuery("SELECT * FROM pfms_unit_master");
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date() +" Inside DocumentsDAOImpl unitMasterList "+ e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
}
