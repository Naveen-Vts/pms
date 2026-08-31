package com.vts.pfms.cars.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.master.model.Employee;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.cars.model.CARSAnnualReport;
import com.vts.pfms.cars.model.CARSContract;
import com.vts.pfms.cars.model.CARSContractConsultants;
import com.vts.pfms.cars.model.CARSContractEquipment;
import com.vts.pfms.cars.model.CARSInitiation;
import com.vts.pfms.cars.model.CARSInitiationTrans;
import com.vts.pfms.cars.model.CARSOtherDocDetails;
import com.vts.pfms.cars.model.CARSRSQR;
import com.vts.pfms.cars.model.CARSRSQRDeliverables;
import com.vts.pfms.cars.model.CARSRSQRMajorRequirements;
import com.vts.pfms.cars.model.CARSSoC;
import com.vts.pfms.cars.model.CARSSoCMilestones;
import com.vts.pfms.cars.model.CARSSoCMilestonesProgress;
import com.vts.pfms.committee.model.PfmsNotification;

@Repository
@Transactional
public class CARSDaoImpl implements CARSDao{

	private static final Logger logger = LogManager.getLogger(CARSDaoImpl.class);

	@PersistenceContext
	EntityManager manager;

	private static final String CARSINITIATIONLIST = "SELECT a.CARSInitiationId,a.EmpId,a.CARSNo,a.InitiationDate,a.InitiationTitle,a.InitiationAim,a.Justification,a.FundsFrom,a.Duration,b.emp_name,c.CARSStatus,c.CARSStatusColor,c.CARSStatusCode,a.Amount,a.DPCSoCStatus,\r\n"
			+ "(SELECT GROUP_CONCAT(d.CARSStatusCode SEPARATOR ',') FROM pfms_cars_initiation_trans d WHERE a.CARSInitiationId=d.CARSInitiationId) AS 'Transaction', e.MoMUpload,\r\n"
			+ "(SELECT COUNT(f.CARSInitiationId) FROM pfms_cars_soc_milestones f WHERE a.CARSInitiationId=f.CARSInitiationId AND f.IsActive LIMIT 1) AS 'MilestoneCount',\r\n"
			+ "(CASE WHEN a.FundsFrom='0' THEN 'Build up' ELSE (SELECT g.project_code FROM project_master g WHERE a.FundsFrom=g.project_id AND g.is_active=1 LIMIT 1 ) END) AS 'FundsSanction',\r\n"
			+ "IFNULL((SELECT h.CommitteeMainId FROM committee_main h WHERE a.CARSInitiationId=h.CARSInitiationId AND h.IsActive=1 LIMIT 1), 0) AS 'CommitteeMainId', e.SoCAmount, \r\n"
			+ "a.RSPInstitute, a.RSPAddress, a.RSPCity, a.RSPState, a.RSPPinCode, a.PITitle, a.PIName, a.PIDesig, a.PIDept, a.PIMobileNo, a.PIEmail, a.PIFaxNo, e.SoCDuration, a.CurrentStatus\r\n"
			+ "FROM pfms_cars_initiation a JOIN employee b ON a.EmpId=b.emp_id  JOIN pfms_cars_approval_status c ON a.CARSStatusCode=c.CARSStatusCode LEFT JOIN pfms_cars_soc e ON a.CARSInitiationId=e.CARSInitiationId AND e.IsActive\r\n"
			+ "WHERE a.IsActive=1 AND (CASE WHEN :LoginType IN ('A','Z','E','L') THEN 1=1 ELSE a.EmpId=:EmpId END)\r\n"
			+ "ORDER BY a.CARSInitiationId DESC";
	@Override
	public List<Object[]> carsInitiationList(String LoginType, String EmpId) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSINITIATIONLIST);
			query.setParameter("EmpId", Long.parseLong(EmpId));
			query.setParameter("LoginType", LoginType);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsInitiationList "+e);
			return new ArrayList<>();
		}

	}

	@Override
	public CARSInitiation getCARSInitiationById(long carsInitiationId) throws Exception{
		try {
			return manager.find(CARSInitiation.class, carsInitiationId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSInitiationById "+e);
			return new CARSInitiation();
		}
	}

	@Override
	public long addCARSInitiation(CARSInitiation initiation) throws Exception{
		try {
			manager.persist(initiation);
			manager.flush();
			return initiation.getCARSInitiationId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSInitiation "+e);
			return 0;
		}
	}

	@Override
	public long editCARSInitiation(CARSInitiation initiation) throws Exception{
		try {
			manager.merge(initiation);
			manager.flush();
			return initiation.getCARSInitiationId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editCARSInitiation "+e);
			return 0;
		}
	}

	private static final String MAXCARSINITIATIONID="SELECT IFNULL(MAX(CARSInitiationId),0) AS 'MAX' FROM pfms_cars_initiation WHERE IsActive=1";
	@Override
	public long getMaxCARSInitiationId() throws Exception {

		try {
			Query query =  manager.createNativeQuery(MAXCARSINITIATIONID);
			return (Long)query.getSingleResult();
		}catch ( NoResultException e ) {
			logger.error(new Date() +"Inside DAO getMaxCARSInitiationId "+ e);
			return 0;
		}
	}

	private static final String CARSRSQRDATA = "SELECT a.CARSRSQRId,a.CARSInitiationId,a.Introduction,a.ResearchOverview,a.Objectives,a.ProposedMandT,a.RSPScope,a.LRDEScope,a.Criterion,a.LiteratureRef,a.RSQRFreeze,a.CARSRSQRNo FROM pfms_cars_rsqr a WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public Object[] carsRSQRDetails(String carsinitiationid) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSRSQRDATA);
			query.setParameter("CARSInitiationId", Long.parseLong(carsinitiationid));
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsRSQRDetails "+e);
			return null;
		}

	}

	@Override
	public long addCARSRSQRDetails(CARSRSQR carsRSQR) throws Exception{
		try {
			manager.persist(carsRSQR);
			manager.flush();
			return carsRSQR.getCARSRSQRId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSRSQRDetails "+e);
			return 0;
		}
	}

	@Override
	public long editCARSRSQRDetails(CARSRSQR carsRSQR) throws Exception{
		try {
			manager.merge(carsRSQR);
			manager.flush();
			return carsRSQR.getCARSRSQRId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editCARSRSQRDetails "+e);
			return 0;
		}
	}

	private static final String CARSRSQRBYCARSINITIATIONID = "FROM CARSRSQR WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public CARSRSQR getCARSRSQRByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSRSQRBYCARSINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (CARSRSQR)query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSRSQRByCARSInitiationId "+e);
			return null;
		}
	}

	private static final String RSQRMAJORREQUIREMENTSLIST = "FROM CARSRSQRMajorRequirements WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSRSQRMajorRequirements> getCARSRSQRMajorReqrByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(RSQRMAJORREQUIREMENTSLIST);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<CARSRSQRMajorRequirements>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSRSQRMajorReqrByCARSInitiationId "+e);
			return null;
		}
	}

	private static final String RSQRDELIVERABLESLIST = "FROM CARSRSQRDeliverables WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSRSQRDeliverables> getCARSRSQRDeliverablesByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(RSQRDELIVERABLESLIST);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<CARSRSQRDeliverables>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSRSQRDeliverablesByCARSInitiationId "+e);
			return null;
		}
	}

	@Override
	public long addCARSRSQRMajorReqrDetails(CARSRSQRMajorRequirements major) throws Exception {
		try {
			manager.persist(major);
			manager.flush();
			return major.getMajorReqrId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSRSQRMajorReqrDetails "+e);
			return 0;
		}
	}

	private static final String REMOVECARSRSQRMAJORREQRDETAILS = "UPDATE pfms_cars_rsqr_major_reqr SET IsActive=:IsActive WHERE CARSInitiationId=:CARSInitiationId";
	@Override
	public int removeCARSRSQRMajorReqrDetails(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createNativeQuery(REMOVECARSRSQRMAJORREQRDETAILS);
			query.setParameter("IsActive", "0");
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeCARSRSQRMajorReqrDetails "+e);
			return 0;
		}

	}

	@Override
	public long addCARSRSQRDeliverableDetails(CARSRSQRDeliverables deliverable) throws Exception {
		try {
			manager.persist(deliverable);
			manager.flush();
			return deliverable.getDeliverablesId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSRSQRDeliverableDetails "+e);
			return 0;
		}
	}

	private static final String REMOVECARSRSQRDELIVERABLEDETAILS = "UPDATE pfms_cars_rsqr_deliverables SET IsActive=:IsActive WHERE CARSInitiationId=:CARSInitiationId";
	@Override
	public int removeCARSRSQRDeliverableDetails(long carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVECARSRSQRDELIVERABLEDETAILS);
			query.setParameter("IsActive", "0");
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeCARSRSQRDeliverableDetails "+e);
			return 0;
		}

	}

	@Override
	public long addCARSInitiationTransaction(CARSInitiationTrans transaction) throws Exception {
		try {
			manager.persist(transaction);
			manager.flush();
			return transaction.getCARSInitiationTransId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSInitiationTransaction "+e);
			return 0;
		}
	}

	private static final String GETEMPGDEMPID  ="SELECT emp.emp_no,emp.emp_id,emp.emp_name FROM employee emp WHERE emp.emp_id=(SELECT c.group_head_id FROM employee a,division_master b,division_group c WHERE a.emp_id=:EmpId AND a.division_id=b.division_id AND b.group_id=c.group_id)";
	@Override
	public Object[] getEmpGDEmpId(String empId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(GETEMPGDEMPID);
			query.setParameter("EmpId", Long.parseLong(empId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getEmpGDEmpId " + e);
			e.printStackTrace();
			return null;
		}		
	}

	private static final String GETEMPPDEMPID  ="SELECT a.project_code,a.project_director,b.emp_name,a.project_short_name,a.project_name FROM project_master a,employee b WHERE a.project_id=:ProjectId AND a.project_director=b.emp_id";
	@Override
	public Object[] getEmpPDEmpId(String projectId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(GETEMPPDEMPID);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getEmpPDEmpId " + e);
			e.printStackTrace();
			return null;
		}		
	}

	private static final String GETEMPDATA="FROM Employee WHERE empId=:EmpId AND empStatus IN ('P','R','T')";
	@Override
	public Employee getEmpData(String EmpId)throws Exception
	{
		Employee emp=null;
		try {
			Query query = manager.createQuery(GETEMPDATA);
			query.setParameter("EmpId", Long.parseLong(EmpId));
			emp = (Employee) query.getSingleResult();
			return emp;
		} catch (Exception e) {
			logger.error(new Date() + "Inside DAO getEmpData "+e);
			e.printStackTrace();
			return null;
		}				
	}

	@Override
	public long addNotifications(PfmsNotification notification) throws Exception {
		try {
			manager.persist(notification);
			manager.flush();
			return notification.getNotificationId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addNotifications "+e);
			return 0;
		}
	}

	private static final String GETEMPDETAILS  ="SELECT a.emp_id,a.emp_name,b.designation,c.division_name,a.title,a.salutation,c.division_code FROM employee a,employee_desig b,division_master c WHERE a.emp_id=:EmpId AND a.emp_status IN ('P','R','T') AND a.desig_id=b.desig_id AND a.division_id=c.division_id";
	@Override
	public Object[] getEmpDetailsByEmpId(String empId) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(GETEMPDETAILS);
			query.setParameter("EmpId", Long.parseLong(empId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getEmpDetailsByEmpId " + e);
			e.printStackTrace();
			return null;
		}		
	}

	private static final String CARSTRANSAPPROVALDATA = "SELECT tra.CARSInitiationTransId,\r\n"
			+ "	(SELECT emp_no FROM pfms_cars_initiation_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.LabCode=e.lab_code AND t.CARSStatusCode =  sta.CARSStatusCode AND t.CARSInitiationId=par.CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'empno',\r\n"
			+ "	(SELECT emp_name FROM pfms_cars_initiation_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.LabCode=e.lab_code AND t.CARSStatusCode =  sta.CARSStatusCode AND t.CARSInitiationId=par.CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ "	(SELECT des.designation FROM pfms_cars_initiation_trans t , employee e,employee_desig des WHERE e.emp_id = t.ActionBy AND t.LabCode=e.lab_code AND t.CARSStatusCode =  sta.CARSStatusCode AND e.desig_id=des.desig_id AND t.CARSInitiationId=par.CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'Designation',\r\n"
			+ "	MAX(tra.ActionDate) AS ActionDate,tra.Remarks,sta.CARSStatus,sta.CARSStatusColor,sta.CARSStatusCode,\r\n"
			+ "	(SELECT ExpertNo FROM pfms_cars_initiation_trans t, expert e WHERE e.ExpertId=t.ActionBy AND t.LabCode='@EXP' AND t.CARSStatusCode =  sta.CARSStatusCode AND t.CARSInitiationId=par.CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'expempno',\r\n"
			+ "	(SELECT ExpertName FROM pfms_cars_initiation_trans t, expert e WHERE e.ExpertId=t.ActionBy AND t.LabCode='@EXP' AND t.CARSStatusCode =  sta.CARSStatusCode AND t.CARSInitiationId=par.CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'expempname'\r\n"
			+ "	FROM pfms_cars_initiation_trans tra,pfms_cars_approval_status sta,pfms_cars_initiation par\r\n"
			+ "	WHERE par.CARSInitiationId=tra.CARSInitiationId AND tra.CARSStatusCode =sta.CARSStatusCode\r\n"
			+ "	AND CASE WHEN 'AF'=:ApprFor THEN sta.CARSForward IN ('RF','SF','DF','CF') ELSE sta.CARSForward=:ApprFor END AND par.CARSInitiationId=:CARSInitiationId GROUP BY sta.CARSStatusCode,sta.CARSStatus,sta.CARSStatusColor,tra.CARSInitiationTransId,tra.Remarks ORDER BY tra.CARSInitiationTransId ASC";
	@Override
	public List<Object[]> carsTransApprovalData(String carsInitiationId, String apprFor) {

		try {
			Query query = manager.createNativeQuery(CARSTRANSAPPROVALDATA);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("ApprFor", apprFor);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO carsTransApprovalData "+e);
			e.printStackTrace();
			return null;
		}

	}

	private static final String CARSRSQRPENDINGLIST  ="CALL pfms_cars_rsqr_pending(:EmpId);";
	@Override
	public List<Object[]> carsRSQRPendingList(String empId) throws Exception {
		try {			
			Query query= manager.createNativeQuery(CARSRSQRPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO rsqrPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String CARSRSQRAPPROVEDLIST  ="CALL pfms_cars_rsqr_approved(:EmpId,:FromDate,:ToDate,:Type);";
	@Override
	public List<Object[]> carsRSQRApprovedList(String empId, String FromDate, String ToDate, String type) throws Exception {

		try {			
			Query query= manager.createNativeQuery(CARSRSQRAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			query.setParameter("Type", type);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsRSQRApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String GETDPANDCEMPID  ="SELECT a.emp_id,a.emp_no,a.emp_name,a.title,c.Designation FROM employee a,login b,employee_desig c WHERE a.emp_id=b.EmpId AND a.emp_status IN ('P','R','T') AND a.desig_id=c.desig_id AND b.LoginType=:LoginType LIMIT 1";
	@Override
	public Object[] getEmpDataByLoginType(String loginType) throws Exception
	{
		try {			
			Query query= manager.createNativeQuery(GETDPANDCEMPID);
			query.setParameter("LoginType", loginType);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}			
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getEmpDataByLoginType " + e);
			e.printStackTrace();
			return null;
		}		
	}

	@Override
	public CARSSoC getCARSSoCById(long carsSoCId) throws Exception{
		try {
			return manager.find(CARSSoC.class, carsSoCId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSSoCById "+e);
			return new CARSSoC();
		}
	}

	@Override
	public long addCARSSoC(CARSSoC soc) throws Exception{
		try {
			manager.persist(soc);
			manager.flush();
			return soc.getCARSSoCId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSSoC "+e);
			return 0;
		}
	}

	@Override
	public long editCARSSoC(CARSSoC soc) throws Exception{
		try {
			manager.merge(soc);
			manager.flush();
			return soc.getCARSSoCId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editCARSSoC "+e);
			return 0;
		}
	}

//	private static final String UPDATEINVFORSOODATE = "UPDATE pfms_cars_initiation SET InvForSoODate=:InvForSoODate WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int invForSoODateSubmit(String carsInitiationId, String sooDate) throws Exception{
		try {
			System.err.println(sooDate);
			CARSInitiation ExistingCARSInitiation = manager.find(CARSInitiation.class, Long.parseLong(carsInitiationId));
			if(ExistingCARSInitiation != null) {
				ExistingCARSInitiation.setInvForSoODate(sooDate);
				return 1;
			}
			else {
				return 0;
			}
			
			/*
			 * Query query = manager.createNativeQuery(UPDATEINVFORSOODATE);
			 * query.setParameter("CARSInitiationId", carsInitiationId);
			 * query.setParameter("InvForSoODate", sooDate); return query.executeUpdate();
			 */
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO invForSoODateSubmit "+e);
			return 0;
		}
	}

	private static final String CARSSOCBYINITIATIONID = "FROM CARSSoC WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public CARSSoC getCARSSoCByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSSOCBYINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			List<CARSSoC> list =(List<CARSSoC>) query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSSoCByCARSInitiationId "+e);
			return null;
		}
	}

	private static final String CARSRSQRFREEZE = "UPDATE pfms_cars_rsqr SET RSQRFreeze=:RSQRFreeze WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int carsRSQRFreeze(long carsInitiationId, String filepath) throws Exception{
		try {
			Query query = manager.createNativeQuery(CARSRSQRFREEZE);
			query.setParameter("CARSInitiationId", carsInitiationId);
			query.setParameter("RSQRFreeze", filepath);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsRSQRFreeze "+e);
			return 0;
		}
	}

	private static final String CARSSOCMILESTONESLIST = "FROM CARSSoCMilestones WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSSoCMilestones> getCARSSoCMilestonesByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSSOCMILESTONESLIST);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<CARSSoCMilestones>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSSoCMilestonesByCARSInitiationId "+e);
			return null;
		}
	}

	@Override
	public long addCARSSoCMilestoneDetails(CARSSoCMilestones milestone) throws Exception {
		try {
			manager.persist(milestone);
			manager.flush();
			return milestone.getCARSSoCMilestoneId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSSoCMilestoneDetails "+e);
			return 0;
		}
	}

	private static final String REMOVECARSSOCMILESTONEDETAILS = "UPDATE pfms_cars_soc_milestones SET IsActive=:IsActive WHERE CARSInitiationId=:CARSInitiationId";
	@Override
	public int removeCARSSoCMilestonesDetails(long carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVECARSSOCMILESTONEDETAILS);
			query.setParameter("IsActive", "0");
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeCARSSoCMilestonesDetails "+e);
			return 0;
		}

	}

	private static final String CARSSOCMOMUPLOADEDLIST = "SELECT a.CARSInitiationId,a.EmpId,a.CARSNo,a.InitiationDate,a.InitiationTitle,a.InitiationAim,a.Justification,a.FundsFrom,a.Duration,b.emp_name,c.CARSStatus,c.CARSStatusColor,c.CARSStatusCode,b.emp_no,e.Designation,d.SoCAmount FROM pfms_cars_initiation a,employee b,pfms_cars_approval_status c,pfms_cars_soc d,employee_desig e  WHERE a.EmpId=b.emp_id AND a.CARSStatusCode=c.CARSStatusCode AND a.IsActive=1 AND d.IsActive=1 AND a.CARSInitiationId=d.CARSInitiationId AND d.MoMUpload IS NOT NULL AND b.desig_id=e.desig_id AND a.InitiationDate BETWEEN :FromDate AND :ToDate ORDER BY a.CARSInitiationId DESC";
	@Override
	public List<Object[]> carsSoCMoMUploadedList(String fromdate, String todate) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSSOCMOMUPLOADEDLIST);
			query.setParameter("FromDate", fromdate);
			query.setParameter("ToDate", todate);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsSoCMoMUploadedList "+e);
			return new ArrayList<>();
		}

	}

	private static final String APPRAUTHDATABYTYPE = "SELECT b.emp_id,b.emp_name FROM pfms_initiation_approver a,employee b WHERE a.IsActive=1 AND a.InitiationId=0 AND a.EmpId=b.emp_id AND a.Type=:Type AND DATE(NOW()) >=a.ValidFrom AND DATE(NOW()) <= a.ValidTo AND a.LabCode=:LabCode LIMIT 1";
	@Override
	public Object[] getApprAuthorityDataByType(String labcode, String type) throws Exception {
		try {
			Query query = manager.createNativeQuery(APPRAUTHDATABYTYPE);
			query.setParameter("LabCode", labcode);
			query.setParameter("Type", type);
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getApprAuthorityDataByType "+e);
			return null;
		}

	}

	private static final String LABDIRECTORDATA = "SELECT a.emp_id,a.emp_name,b.Designation FROM employee a,employee_desig b WHERE a.desig_id=b.desig_id AND a.emp_status IN ('P','R','T') AND a.emp_id =(SELECT lab_authority_id FROM lab_master c WHERE c.lab_code=:LabCode LIMIT 1) AND a.is_active=1";
	@Override
	public Object[] getLabDirectorData(String labcode) throws Exception {
		try {
			Query query = manager.createNativeQuery(LABDIRECTORDATA);
			query.setParameter("LabCode", labcode);
			List<Object[]> list = (List<Object[]>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getLabDirectorData "+e);
			return null;
		}

	}

	private static final String CARSDPCSOCPENDINGLIST  ="CALL pfms_cars_dpcsoc_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> carsDPandCSoCPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(CARSDPCSOCPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsDPandCSoCPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String CARSDPCSOCAPPROVEDLIST  ="CALL pfms_cars_dpcsoc_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> carsDPCSoCApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(CARSDPCSOCAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsDPCSoCApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	private static final String CARSTRANSLISTBYTYPE = "SELECT tra.CARSInitiationTransId,\r\n"
			+ "(SELECT emp.emp_id FROM employee emp WHERE tra.ActionBy=emp.emp_id AND tra.LabCode=emp.lab_code LIMIT 1) AS 'EmpId',\r\n"
			+ "(SELECT emp.emp_name FROM employee emp WHERE tra.ActionBy=emp.emp_id AND tra.LabCode=emp.lab_code LIMIT 1) AS 'EmpName',\r\n"
			+ "(SELECT des.Designation FROM employee emp, employee_desig des WHERE tra.ActionBy=emp.emp_id AND emp.desig_id = des.DesigId AND tra.LabCode=emp.lab_code LIMIT 1) AS 'Designation',\r\n"
			+ "tra.ActionDate,tra.Remarks,sta.CARSStatus,sta.CARSStatusColor,\r\n"
			+ "(SELECT ex.ExpertId FROM expert ex WHERE tra.ActionBy=ex.ExpertId AND tra.LabCode='@EXP' LIMIT 1) AS 'ExpertId',\r\n"
			+ "(SELECT ex.ExpertName FROM expert ex WHERE tra.ActionBy=ex.ExpertId AND tra.LabCode='@EXP' LIMIT 1) AS 'ExpName'\r\n"
			+ "FROM pfms_cars_initiation_trans tra,pfms_cars_approval_status sta,pfms_cars_initiation par \r\n"
			+ "WHERE par.CARSInitiationId = tra.CARSInitiationId AND tra.CARSStatusCode = sta.CARSStatusCode AND CASE WHEN 'A'=:CARSStatusFor THEN 1=1 ELSE sta.CARSStatusFor =:CARSStatusFor END \r\n"
			+ "AND par.CARSInitiationId=:CARSInitiationId ORDER BY tra.CARSInitiationTransId DESC";
	@Override
	public List<Object[]> carsTransListByType(String carsInitiationId, String statusFor) throws Exception {

		try {
			Query query = manager.createNativeQuery(CARSTRANSLISTBYTYPE);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("CARSStatusFor", statusFor);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO carsTransListByType "+e);
			e.printStackTrace();
			return null;
		}

	}

	private static final String CARSREMARKSHISTORYBYTYPE  ="SELECT cat.CARSInitiationId,cat.Remarks,cs.CARSStatusCode,\r\n"
			+ "(SELECT emp.emp_name FROM employee emp WHERE cat.ActionBy=emp.emp_id AND cat.LabCode=emp.lab_code LIMIT 1) AS 'EmpId',\r\n"
			+ "(SELECT des.Designation FROM employee emp, employee_desig des WHERE cat.ActionBy=emp.emp_id AND emp.desig_id = des.desig_id AND cat.LabCode=emp.lab_code LIMIT 1) AS 'Designation',\r\n"
			+ "(SELECT ex.ExpertName FROM expert ex WHERE cat.ActionBy=ex.ExpertId AND cat.LabCode='@EXP' LIMIT 1) AS 'ExpName'\r\n"
			+ "FROM pfms_cars_approval_status cs,pfms_cars_initiation_trans cat,pfms_cars_initiation ca\r\n"
			+ "WHERE cs.CARSStatusCode = cat.CARSStatusCode AND CASE WHEN 'AF'=:RemarksFor THEN cs.CARSForward IN('RF','SF','DF','CF','MF') ELSE cs.CARSForward=:RemarksFor END AND ca.CARSInitiationId = cat.CARSInitiationId AND TRIM(cat.Remarks)<>'' AND ca.CARSInitiationId=:CARSInitiationId ORDER BY cat.CARSInitiationTransId ASC";
	@Override
	public List<Object[]> carsRemarksHistoryByType(String carsInitiationId, String remarksFor) throws Exception
	{
		List<Object[]> list =new ArrayList<Object[]>();
		try {
			Query query= manager.createNativeQuery(CARSREMARKSHISTORYBYTYPE);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("RemarksFor", remarksFor);
			list= (List<Object[]>)query.getResultList();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsRSQRRemarksHistory " + e);
			e.printStackTrace();
		}
		return list;
	}

	private static final String SOCMOMUPLOAD = "UPDATE pfms_cars_soc SET MoMUpload=:MoMUpload WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public long carsSoCUploadMoM(String momFile, String carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(SOCMOMUPLOAD);
			query.setParameter("MoMUpload", momFile);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsSoCUploadMoM "+e);
			return 0;
		}

	}

	private static final String UPDATEDPCAPPROVALSOUGHT = "UPDATE pfms_cars_soc SET DPCApprovalSought=:DPCApprovalSought WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int updateDPCApprovalSought(long carsInitiationId,String approvalSought) throws Exception{
		try {
			Query query = manager.createNativeQuery(UPDATEDPCAPPROVALSOUGHT);
			query.setParameter("DPCApprovalSought", approvalSought);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO updateDPCApprovalSought "+e);
			return 0;
		}

	}

	private static final String LABLIST="SELECT lab_id, cluster_id, lab_code FROM cluster_lab WHERE lab_code NOT IN (:lab)";
	@Override
	public List<Object[]> getLabList(String lab) throws Exception {
		try {
			Query query=manager.createNativeQuery(LABLIST);
			query.setParameter("lab", lab);
			List<Object[]> lablist=(List<Object[]>)query.getResultList();
			return lablist;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO LabList "+e);
			return null;
		}
	}

	private static final String EMPLOYEELISTBYLABCODE="SELECT e.emp_id, CONCAT(IFNULL(CONCAT(e.title,' '),(IFNULL(CONCAT(e.salutation, ' '), ''))), e.emp_name) AS 'EmpName',e.emp_no,d.Designation FROM employee e,employee_desig d WHERE e.desig_id=d.desig_id AND e.emp_status IN ('P','R','T') AND e.is_active='1' AND e.lab_code=:LabCode ORDER BY sr_no=0, sr_no";
	@Override
	public List<Object[]> getEmployeeListByLabCode(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(EMPLOYEELISTBYLABCODE);
			query.setParameter("LabCode", labCode);
			List<Object[]> EmployeeListForEnoteRoSo = (List<Object[]>) query.getResultList();
			return EmployeeListForEnoteRoSo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DAO getEmployeeListByLabCode", e);
			return null;
		}
	}
	
	private static final String LABDETAILSBYLABCODE = "FROM LabMaster WHERE LabCode=:LabCode";
	@Override
	public LabMaster getLabDetailsByLabCode(String labcode) throws Exception{
		try {
			Query query = manager.createQuery(LABDETAILSBYLABCODE);
			query.setParameter("LabCode", labcode);
			List<LabMaster> list = (List<LabMaster>)query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getLabDetailsByLabCode "+e);
			return null;
		}
	}
	
	private static final String CARSDPCSOCFINALAPPROVEDLIST = "SELECT a.CARSInitiationId,a.EmpId,a.CARSNo,a.InitiationDate,a.InitiationTitle,a.InitiationAim,a.Justification,a.FundsFrom,a.Duration,b.emp_name,c.CARSStatus,c.CARSStatusColor,c.CARSStatusCode,b.emp_no,e.Designation,d.SoCAmount,(SELECT IFNULL(MAX(con.CARSContractId),0) FROM pfms_cars_contract con WHERE con.CARSInitiationId=a.CARSInitiationId AND con.IsActive=1) AS 'ContractId' FROM pfms_cars_initiation a,employee b,pfms_cars_approval_status c,pfms_cars_soc d,employee_desig e  WHERE a.EmpId=b.emp_id AND a.CARSStatusCode=c.CARSStatusCode AND a.IsActive=1 AND d.IsActive=1 AND a.CARSInitiationId=d.CARSInitiationId AND DPCSoCStatus='A' AND b.desig_id=e.desig_id AND a.InitiationDate BETWEEN :FromDate AND :ToDate ORDER BY a.CARSInitiationId DESC";
	@Override
	public List<Object[]> carsDPCSoCFinalApprovedList(String fromdate, String todate) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSDPCSOCFINALAPPROVEDLIST);
			query.setParameter("FromDate", fromdate);
			query.setParameter("ToDate", todate);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsDPCSoCFinalApprovedList "+e);
			return new ArrayList<>();
		}

	}
	
	private static final String CARSCONTRACTBYINITIATIONID = "FROM CARSContract WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public CARSContract getCARSContractByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSCONTRACTBYINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			List<CARSContract> list =(List<CARSContract>) query.getResultList();
			if(list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSContractByCARSInitiationId "+e);
			return null;
		}
	}
	
	private static final String CARSCONTRACTCONSULTANTSLIST = "FROM CARSContractConsultants WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSContractConsultants> getCARSContractConsultantsByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSCONTRACTCONSULTANTSLIST);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<CARSContractConsultants>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSContractConsultantsByCARSInitiationId "+e);
			return null;
		}
	}
	
	private static final String CARSCONTRACTEQUIPMENTLIST = "FROM CARSContractEquipment WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSContractEquipment> getCARSContractEquipmentByCARSInitiationId(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createQuery(CARSCONTRACTEQUIPMENTLIST);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<CARSContractEquipment>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSContractEquipmentByCARSInitiationId "+e);
			return null;
		}
	}
	
	@Override
	public long addCARSContractDetails(CARSContract contract) throws Exception{
		try {
			manager.persist(contract);
			manager.flush();
			return contract.getCARSContractId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSContractDetails "+e);
			return 0;
		}
	}

	@Override
	public long editCARSContractDetails(CARSContract contract) throws Exception{
		try {
			manager.merge(contract);
			manager.flush();
			return contract.getCARSContractId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editCARSContractDetails "+e);
			return 0;
		}
	}
	
	@Override
	public long addCARSContractConsultantsDetails(CARSContractConsultants consultants) throws Exception{
		try {
			manager.persist(consultants);
			manager.flush();
			return consultants.getConsultantId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSContractConsultantsDetails "+e);
			return 0;
		}
	}
	
	@Override
	public long addCARSContractEquipmentDetails(CARSContractEquipment equipment) throws Exception{
		try {
			manager.persist(equipment);
			manager.flush();
			return equipment.getEquipmentId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSContractEquipmentDetails "+e);
			return 0;
		}
	}
	
	private static final String REMOVECARSCONTRACTCONSULTANTSDETAILS = "UPDATE pfms_cars_contract_consultants SET IsActive=:IsActive WHERE CARSInitiationId=:CARSInitiationId";
	@Override
	public int removeCARSContractConsultantsDetails(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createNativeQuery(REMOVECARSCONTRACTCONSULTANTSDETAILS);
			query.setParameter("IsActive", "0");
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeCARSContractConsultantsDetails "+e);
			return 0;
		}

	}
	
	private static final String REMOVECARSCONTRACTEQUIPMENTDETAILS = "UPDATE pfms_cars_contract_equipment SET IsActive=:IsActive WHERE CARSInitiationId=:CARSInitiationId";
	@Override
	public int removeCARSContractEquipmentDetails(long carsInitiationId) throws Exception{
		try {
			Query query = manager.createNativeQuery(REMOVECARSCONTRACTEQUIPMENTDETAILS);
			query.setParameter("IsActive", "0");
			query.setParameter("CARSInitiationId", carsInitiationId);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO removeCARSContractEquipmentDetails "+e);
			return 0;
		}

	}
	
	private static final String MAXCARSFINALCONTRACTNO="SELECT IFNULL(SUBSTR(a.ContractNo,11),0) AS ContractNo FROM pfms_cars_contract a WHERE a.CARSContractId IN (SELECT IFNULL(MAX(con.CARSContractId),0) FROM pfms_cars_contract con WHERE ContractNo IS NOT NULL)";
	@Override
	public String getMaxCARSContractNo() throws Exception {

		try {
			Query query =  manager.createNativeQuery(MAXCARSFINALCONTRACTNO);
			List<String> list = (List<String>)query.getResultList();
			if(list!=null && list.size()>0) {
				list.get(0);
			}else {
				return null;
			}
		}catch ( Exception e ) {
			logger.error(new Date() +"Inside DAO getMaxCARSContractNo "+ e);
			return null;
		}
		return null;
	}

	private static final String CARSOTHERDOCDETAILSBYINITIATIONID = "FROM CARSOtherDocDetails WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public List<CARSOtherDocDetails> getCARSOtherDocDetailsByCARSInitiationId(long carsInitiationId) throws Exception{
		List<CARSOtherDocDetails> list = new ArrayList<>();
		try {
			Query query = manager.createQuery(CARSOTHERDOCDETAILSBYINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			list =(List<CARSOtherDocDetails>) query.getResultList();
			
			return list;

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSOtherDocDetailsByCARSInitiationId "+e);
			return list;
		}
	}
	
	@Override
	public long addCARSOtherDocDetails(CARSOtherDocDetails doc) throws Exception{
		try {
			manager.persist(doc);
			manager.flush();
			return doc.getOtherDocDetailsId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSCSDetails "+e);
			return 0;
		}
	}

	@Override
	public long editCARSOtherDocDetails(CARSOtherDocDetails doc) throws Exception{
		try {
			manager.merge(doc);
			manager.flush();
			return doc.getOtherDocDetailsId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editCARSCSDetails "+e);
			return 0;
		}
	}

	//private static final String UPDATECARSINITIATIONSTATUSCODES = "UPDATE pfms_cars_initiation SET CARSStatusCode=:CARSStatusCode,CARSStatusCodeNext=:CARSStatusCodeNext WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int updateCARSInitiationStatusCodes(long carsInitiationId, String CARSStatusCode, String CARSStatusCodeNext) throws Exception{
		try {
			
			CARSInitiation ExistingCARSInitiation=manager.find(CARSInitiation.class, carsInitiationId);
			if(ExistingCARSInitiation !=null && ExistingCARSInitiation.getIsActive()==1) {
				ExistingCARSInitiation.setCARSStatusCode(CARSStatusCode);
				ExistingCARSInitiation.setCARSStatusCodeNext(CARSStatusCodeNext);
				return 1;
			}
			else {
				return 0;
			}
			/*
			 * Query query = manager.createNativeQuery(UPDATECARSINITIATIONSTATUSCODES);
			 * query.setParameter("CARSInitiationId", carsInitiationId);
			 * query.setParameter("CARSStatusCode", CARSStatusCode);
			 * query.setParameter("CARSStatusCodeNext", CARSStatusCodeNext); return
			 * query.executeUpdate();
			 */
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO updateCARSInitiationStatusCodes "+e);
			return 0;
		}

	}
	
	private static final String CARSSTATUSDETAILSBYCARSINITIATIONID = "SELECT a.CARSInitiationId,b.CARSStatusCode,b.CARSStatus,b.CARSStatusColor FROM pfms_cars_initiation a, pfms_cars_approval_status b WHERE a.CARSInitiationId=:CARSInitiationId AND a.IsActive=1 AND a.CARSStatusCode=b.CARSStatusCode";
	@Override
	public Object[] carsStatusDetailsByCARSInitiationId(long carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSSTATUSDETAILSBYCARSINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			List<Object[]> list = (List<Object[]>) query.getResultList();
			if(list!=null && list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsStatusDetailsByCARSInitiationId "+e);
			return null;
		}
	}
	
	@Override
	public CARSOtherDocDetails getCARSOtherDocDetailsById(long otherDocDetailsId) throws Exception{
		try {
			return manager.find(CARSOtherDocDetails.class, otherDocDetailsId);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSOtherDocDetailsById "+e);
			return new CARSOtherDocDetails();
		}
	}

	private static final String CARSCSPENDINGLIST  ="CALL pfms_cars_cs_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> carsCSPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(CARSCSPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsCSPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String CARSCSAPPROVEDLIST  ="CALL pfms_cars_cs_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> carsCSApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(CARSCSAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsCSApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public long carsOtherDocUpload(String uploadOtherDoc, String otherDocDetailsId) throws Exception {
		try {
			
			CARSOtherDocDetails ExistingCARSOtherDocDetails=manager.find(CARSOtherDocDetails.class, Long.parseLong(otherDocDetailsId));
			if(ExistingCARSOtherDocDetails !=null && ExistingCARSOtherDocDetails.getIsActive()==1)
			{
				ExistingCARSOtherDocDetails.setUploadOtherDoc(uploadOtherDoc);
				return 1L;
			}
			else {
				return 0L;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsOtherDocUpload "+e);
			return 0;
		}

	}
	
	private static final String CARSREMARKSHISTORYBYMILESTONENO  ="SELECT cat.CARSInitiationId,cat.Remarks,cs.CARSStatusCode,e.emp_name,ed.Designation FROM pfms_cars_approval_status cs,pfms_cars_initiation_trans cat,pfms_cars_initiation ca,employee e,employee_desig ed WHERE cat.ActionBy = e.emp_id AND e.desig_id = ed.desig_id AND cs.CARSStatusCode = cat.CARSStatusCode AND cs.CARSForward='MF' AND ca.CARSInitiationId = cat.CARSInitiationId AND TRIM(cat.Remarks)<>'' AND cat.MilestoneNo=:MilestoneNo AND ca.CARSInitiationId=:CARSInitiationId ORDER BY cat.CARSInitiationTransId ASC";
	@Override
	public List<Object[]> carsRemarksHistoryByMilestoneNo(String carsInitiationId, String milestoneNo) throws Exception
	{
		List<Object[]> list =new ArrayList<Object[]>();
		try {
			Query query= manager.createNativeQuery(CARSREMARKSHISTORYBYMILESTONENO);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("MilestoneNo", milestoneNo);
			list= (List<Object[]>)query.getResultList();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsRemarksHistoryByMilestoneNo " + e);
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String CARSTRANSAPPROVALDATABYMILESTONENO = "SELECT tra.CARSInitiationTransId,\r\n"
			+ "(SELECT emp_no FROM pfms_cars_initiation_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.CARSStatusCode =  sta.CARSStatusCode  AND t.MilestoneNo=:MilestoneNo AND t.CARSInitiationId=:CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'empno',\r\n"
			+ "(SELECT emp_name FROM pfms_cars_initiation_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.CARSStatusCode =  sta.CARSStatusCode  AND t.MilestoneNo=:MilestoneNo AND t.CARSInitiationId=:CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ "(SELECT des.designation FROM pfms_cars_initiation_trans t , employee e,employee_desig des WHERE e.emp_id = t.ActionBy AND e.desig_id=des.desig_id AND t.CARSStatusCode =  sta.CARSStatusCode  AND t.MilestoneNo=:MilestoneNo AND t.CARSInitiationId=:CARSInitiationId ORDER BY t.CARSInitiationTransId DESC LIMIT 1) AS 'Designation',\r\n"
			+ "MAX(tra.ActionDate) AS ActionDate,tra.Remarks,sta.CARSStatus,sta.CARSStatusColor,sta.CARSStatusCode\r\n"
			+ "FROM pfms_cars_initiation_trans tra,pfms_cars_approval_status sta,employee emp,pfms_cars_initiation par\r\n"
			+ "WHERE par.CARSInitiationId=tra.CARSInitiationId AND tra.CARSStatusCode =sta.CARSStatusCode AND tra.Actionby=emp.emp_id AND sta.CARSForward='MF' AND tra.MilestoneNo=:MilestoneNo AND par.CARSInitiationId=:CARSInitiationId GROUP BY sta.CARSStatusCode,tra.CARSInitiationTransId,sta.CARSStatus,sta.CARSStatusColor ORDER BY tra.CARSInitiationTransId ASC";
	@Override
	public List<Object[]> carsTransApprovalDataByMilestoneNo(String carsInitiationId, String milestoneNo) {

		try {
			Query query = manager.createNativeQuery(CARSTRANSAPPROVALDATABYMILESTONENO);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("MilestoneNo", milestoneNo);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO carsTransApprovalDataByMilestoneNo "+e);
			e.printStackTrace();
			return null;
		}

	}
	
	@Override
	public long updateOtherDocForwardDetails(String forwardedBy, String forwardedDate, String otherDocDetailsId) throws Exception {
		try {
			
			CARSOtherDocDetails ExistingCARSOtherDocDetails= manager.find(CARSOtherDocDetails.class, Long.parseLong(otherDocDetailsId));
			if(ExistingCARSOtherDocDetails != null && ExistingCARSOtherDocDetails.getIsActive() ==1)
			{
				ExistingCARSOtherDocDetails.setForwardedBy(Long.parseLong(forwardedBy));
				ExistingCARSOtherDocDetails.setForwardedDate(forwardedDate);
				return 1L;
			}
			else {
				return 0L;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO updateOtherDocForwardDetails "+e);
			return 0;
		}

	}
	
	private static final String CARSMPPENDINGLIST  ="CALL pfms_cars_mp_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> carsMPPendingList(String empId,String labcode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(CARSMPPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labcode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsMPPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String CARSMPAPPROVEDLIST  ="CALL pfms_cars_mp_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> carsMPApprovedList(String empId, String FromDate, String ToDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(CARSMPAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", FromDate);
			query.setParameter("ToDate", ToDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO carsMPApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	private static final String CARSMPSTATUSDETAILSBYCARSINITIATIONID = "SELECT a.CARSInitiationId,b.CARSStatusCode,b.CARSStatus,b.CARSStatusColor,a.MilestoneNo FROM pfms_cars_other_doc_details a, pfms_cars_approval_status b WHERE a.CARSInitiationId=:CARSInitiationId AND a.IsActive=1 AND a.OthersStatusCode=b.CARSStatusCode";
	@Override
	public List<Object[]> carsMPStatusDetailsByCARSInitiationId(long carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSMPSTATUSDETAILSBYCARSINITIATIONID);
			query.setParameter("CARSInitiationId", carsInitiationId);
			return (List<Object[]>) query.getResultList();
			 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsMPStatusDetailsByCARSInitiationId "+e);
			return new ArrayList<Object[]>();
		}
	}

	private static final String UPDATECARSOTHERDOCSTATUSCODES = "UPDATE pfms_cars_other_doc_details SET OthersStatusCode=:OthersStatusCode,OthersStatusCodeNext=:OthersStatusCodeNext WHERE CARSInitiationId=:CARSInitiationId AND MilestoneNo=:MilestoneNo AND IsActive=1";
	@Override
	public int updateCARSOtherDocStatusCodes(long carsInitiationId, String othersStatusCode, String othersStatusCodeNext, String milestoneNo) throws Exception{
		try {
			Query query = manager.createNativeQuery(UPDATECARSOTHERDOCSTATUSCODES);
			query.setParameter("CARSInitiationId", carsInitiationId);
			query.setParameter("OthersStatusCode", othersStatusCode);
			query.setParameter("OthersStatusCodeNext", othersStatusCodeNext);
			query.setParameter("MilestoneNo", milestoneNo);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO updateCARSOtherDocStatusCodes "+e);
			return 0;
		}

	}
	
	private static final String EXPERTEMPLOYEELIST="SELECT e.expertid,CONCAT(IFNULL(CONCAT(e.title,' '),''),e.expertname) AS 'expertname' ,e.expertno,'Expert' AS designation FROM expert e WHERE e.isactive=1";
	@Override
	public List<Object[]> ExpertEmployeeList() throws Exception {
		logger.info(new Date() + "Inside the DaoImpl ExpertEmployeeList");
		try {
			Query query=manager.createNativeQuery(EXPERTEMPLOYEELIST);
			List<Object[]> ExpertEmployeeList=(List<Object[]>)query.getResultList();
			return ExpertEmployeeList;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl ExpertEmployeeList", e);
			return null;
		}
	}

	private static final String UPDATESOOLETTERDATE = "UPDATE pfms_cars_contract SET FinalSoODate=:FinalSoODate WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int finalSoODateSubmit(String carsInitiationId, String date) throws Exception {
		try {
			Query query = manager.createNativeQuery(UPDATESOOLETTERDATE);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			query.setParameter("FinalSoODate", date);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO finalSoODateSubmit "+e);
			return 0;
		}
	}
	
	@Override
	public CARSSoCMilestones getCARSSoCMilestonesById(String carsSoCMilestoneId) throws Exception{
		try {
			
			return manager.find(CARSSoCMilestones.class, Long.parseLong(carsSoCMilestoneId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSSoCMilestonesById "+e);
			return null;
		}
	}
	
	private static final String ASSIGNEDLISTBYCARSSOCMILESTONEID="SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.emp_name) AS emp,dc.designation,a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.progress, aas.IsSeen , aas.actionassignid , \r\n"
			+ "(SELECT COUNT(am.actionmainid) FROM action_main am WHERE am.ParentActionid = a.actionmainid ) AS 'ChildActionCount',aas.actionno \r\n"
			+ "FROM action_main a,  employee ab ,employee_desig dc ,action_assign aas \r\n"
			+ "WHERE aas.actionmainid=a.actionmainid AND aas.assignee=ab.emp_id AND ab.is_active='1' AND dc.desigid=ab.desig_id  AND aas.actionstatus<>'C' AND aas.assigneelabcode <> '@EXP' AND a.CARSSoCMilestoneId=:CARSSoCMilestoneId \r\n"
			+ "\r\n"
			+ "UNION \r\n"
			+ "\r\n"
			+ "SELECT a.actionmainid,CONCAT(IFNULL(CONCAT(ab.title,' '),''), ab.expertname) AS emp,'Expert' AS 'designation',a.actiondate,aas.enddate,a.actionitem,aas.actionstatus,aas.progress, aas.IsSeen , aas.actionassignid , \r\n"
			+ "(SELECT COUNT(am.actionmainid) FROM action_main am WHERE am.ParentActionid = a.actionmainid ) AS 'ChildActionCount',aas.actionno \r\n"
			+ "FROM action_main a,  expert ab , action_assign aas \r\n"
			+ "WHERE aas.actionmainid=a.actionmainid AND aas.assignee=ab.expertid AND ab.isactive='1' AND aas.actionstatus<>'C' AND aas.assigneelabcode = '@EXP' AND a.CARSSoCMilestoneId=:CARSSoCMilestoneId\r\n"
			+ "\r\n"
			+ "ORDER BY actionassignid DESC";
	@Override
	public List<Object[]> assignedListByCARSSoCMilestoneId(String carsSoCMilestoneId) throws Exception {
		logger.info(new Date() + "Inside the DaoImpl assignedListByCARSSoCMilestoneId");
		try {
			Query query=manager.createNativeQuery(ASSIGNEDLISTBYCARSSOCMILESTONEID);
			query.setParameter("CARSSoCMilestoneId", Long.parseLong(carsSoCMilestoneId));
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl assignedListByCARSSoCMilestoneId", e);
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public long addCARSSoCMilestonesProgress(CARSSoCMilestonesProgress milestoneProgress) throws Exception{
		try {
			manager.persist(milestoneProgress);
			manager.flush();
			return milestoneProgress.getCARSMSProgressId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSSoCMilestonesProgress "+e);
			return 0;
		}
	}
	
	@Override
	public List<CARSSoCMilestonesProgress> getCARSSoCMilestonesProgressListByCARSSoCMilestoneId(String carsSoCMilestoneId) throws Exception {
		logger.info(new Date() + "Inside the DaoImpl getCARSSoCMilestonesProgressListByCARSSoCMilestoneId");
		try {
			Query query=manager.createQuery("FROM CARSSoCMilestonesProgress WHERE CARSSoCMilestoneId=:CARSSoCMilestoneId AND IsActive=1");
			query.setParameter("CARSSoCMilestoneId", Long.parseLong(carsSoCMilestoneId));
			return (List<CARSSoCMilestonesProgress>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl getCARSSoCMilestonesProgressListByCARSSoCMilestoneId ", e);
			return new ArrayList<CARSSoCMilestonesProgress>();
		}
	}
	
	private static final String CARSMILESTONESLISTBYCARSINITIATIONID = "SELECT a.CARSMSProgressId, a.CARSSoCMilestoneId, a.Progress, a.ProgressDate,a.Remarks, b.MilestoneNo, b.CARSInitiationId FROM pfms_cars_soc_ms_progress a, pfms_cars_soc_milestones b WHERE a.CARSSoCMilestoneId=b.CARSSoCMilestoneId AND a.ProgressDate = (SELECT MAX(b.ProgressDate) FROM pfms_cars_soc_ms_progress b WHERE b.CARSSoCMilestoneId = a.CARSSoCMilestoneId) ORDER BY b.CarsInitiationId, b.MilestoneNo";
	@Override
	public List<Object[]> getAllCARSSoCMilestonesProgressList() throws Exception {
		logger.info(new Date() + " Inside the DaoImpl getCARSSoCMilestonesProgressList");
		try {
			Query query=manager.createNativeQuery(CARSMILESTONESLISTBYCARSINITIATIONID);
			return (List<Object[]>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + " Inside DaoImpl getCARSSoCMilestonesProgressList ", e);
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public List<CARSSoCMilestones> getAllCARSSoCMilestonesList() throws Exception{
		try {
			Query query = manager.createQuery("FROM CARSSoCMilestones WHERE IsActive=1");
			return (List<CARSSoCMilestones>)query.getResultList();

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getAllCARSSoCMilestonesList "+e);
			return null;
		}
	}
	
	@Override
	public List<CARSContract> getAllCARSContractList() throws Exception{
		try {
			Query query = manager.createQuery("FROM CARSContract WHERE IsActive=1");
			return (List<CARSContract>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getAllCARSContractList "+e);
			return null;
		}
	}
	
	@Override
	public List<CARSOtherDocDetails> getCARSOtherDocDetailsList() throws Exception{
		List<CARSOtherDocDetails> list = new ArrayList<>();
		try {
			Query query = manager.createQuery("FROM CARSOtherDocDetails WHERE IsActive=1");
			list =(List<CARSOtherDocDetails>) query.getResultList();
			
			return list;

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSOtherDocDetailsList "+e);
			return list;
		}
	}

	private static final String CARSCURRENTSTATUSUPDATE = "UPDATE pfms_cars_initiation SET CurrentStatus=:CurrentStatus WHERE CARSInitiationId=:CARSInitiationId AND IsActive=1";
	@Override
	public int carsCurrentStatusUpdate(String currentStatus, String carsInitiationId) throws Exception {
		try {
			Query query = manager.createNativeQuery(CARSCURRENTSTATUSUPDATE);
			query.setParameter("CurrentStatus", currentStatus);
			query.setParameter("CARSInitiationId", Long.parseLong(carsInitiationId));
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO carsCurrentStatusUpdate "+e);
			return 0;
		}

	}
	
	@Override
	public List<CARSAnnualReport> getCARSAnnualReportListByYear(String annualYear) throws Exception {
		logger.info(new Date() + "Inside the DaoImpl getCARSAnnualReportListByYear");
		try {
			Query query=manager.createQuery("FROM CARSAnnualReport WHERE AnnualYear=:AnnualYear AND IsActive=1");
			query.setParameter("AnnualYear", annualYear!=null?Integer.parseInt(annualYear):0);
			return (List<CARSAnnualReport>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl getCARSAnnualReportListByYear ", e);
			return new ArrayList<CARSAnnualReport>();
		}
	}
	
	@Override
	public long addCARSAnnualReport(CARSAnnualReport carsAnnualReport) throws Exception{
		try {
			manager.persist(carsAnnualReport);
			manager.flush();
			return carsAnnualReport.getCARSAnnualReportId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addCARSAnnualReport "+e);
			return 0;
		}
	}
	
	private static final String DELETECARSANNUALREPORTRECORDS = "DELETE FROM pfms_cars_annual_report WHERE AnnualYear=:AnnualYear";
	@Override
	public int deleteCARSAnnualReportRecordsByYear(String annualYear) throws Exception {
		try {
			Query query = manager.createNativeQuery(DELETECARSANNUALREPORTRECORDS);
			query.setParameter("AnnualYear", annualYear!=null?Integer.parseInt(annualYear):0);
			return query.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO deleteCARSAnnualReportRecordsByYear "+e);
			return 0;
		}

	}
}
