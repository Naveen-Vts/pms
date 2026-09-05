package com.vts.pfms.master.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Transactional
@Repository
public class MasterDaoImpl implements MasterDao {

	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String OFFICERLIST="SELECT a.emp_id, a.emp_no, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'empname' , b.designation, a.ext_no, a.email, (SELECT c.division_name FROM division_master c WHERE a.division_id= c.division_id LIMIT 1) AS 'divisionname', a.desig_id, a.division_id, a.sr_no, a.is_active,a.lab_code FROM employee a,employee_desig b WHERE a.desig_id= b.desig_id AND a.emp_status IN ('P')  ORDER BY a.sr_no=0,a.sr_no";
	private static final String DESIGNATIONLIST="SELECT desig_id, desig_code, designation, desig_limit FROM employee_desig ORDER BY desig_sr";
	private static final String OFFICERDIVISIONLIST="SELECT division_id, division_name FROM division_master where is_active='1'";
	private static final String OFFICEREDITDATA="select emp_id,emp_no,emp_name,desig_id,ext_no,email,division_id, drona_email, internet_email,mobile_no , title , salutation, superior_officer, emp_status from employee  where emp_id=:empid"; 
	private static final String EMPNOCHECK="SELECT emp_no FROM employee";
	private static final String EMPEXTNOCHECK="SELECT empno FROM employee_external";
	private static final String CLUSTERLAB="SELECT lab_id, cluster_id, lab_code FROM cluster_lab";
	private static final String EXTERNALOFFICERLIST="SELECT a.emp_id, a.emp_no, CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'EmpName', b.designation, a.ext_no, a.email, c.division_name, a.desig_id, a.division_id, 'active', a.is_active, a.lab_code FROM employee a JOIN employee_desig b ON a.desig_id = b.desig_id LEFT JOIN division_master c ON a.division_id = c.division_id WHERE a.lab_code <>:labcode ORDER BY a.emp_id DESC";
	private static final String EXTERNALOFFICEREDITDATA="select emp_id,emp_no,emp_name,desig_id,ext_no,email,division_id,drona_email,internet_email,mobile_no,lab_code,title , salutation from employee  where emp_id=:empid";
	private static final String EXTERNALOFFICERMASTERUPDATE="UPDATE employee SET salutation=:salutation ,title=:title, labcode=:labcode,empno=:empno, empname=:empname, desigid=:desigid, extno=:extno, MobileNo=:mobileno, email=:email,DronaEmail=:dronaemail, InternetEmail=:internalemail , divisionid=:divisionid, modifiedby=:modifiedby, modifieddate=:modifieddate WHERE empid=:empid" ;



	private static final String DIVISIONLIST="SELECT division_id,division_code,division_name FROM division_master WHERE isactive=1";
	private static final String DIVISIONEMPLIST="SELECT de.divisionemployeeid,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation,de.divisionid,e.lab_code  FROM division_employee de,employee e, employee_desig ed WHERE de.isactive=1 AND e.is_active=1 AND e.emp_status IN ('P') AND  de.empid=e.emp_id AND e.desig_id=ed.desig_id AND de.divisionid=:divisionid";
	private static final String DIVISIONNONEMPLIST ="SELECT e.emp_id, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation,e.lab_code  FROM employee e,employee_desig ed  WHERE e.is_active=1 AND e.emp_status IN ('P') AND e.desig_id=ed.desig_id AND e.emp_id NOT IN  (SELECT de.empid FROM division_employee de WHERE de.isactive=1 AND divisionid=:divisionid) AND e.emp_status NOT IN ('N') ORDER BY e.sr_no ASC ,ed.desig_sr ASC";
	private static final String DIVISIONDATA ="SELECT division_id, division_code,division_name FROM division_master WHERE division_id=:divisionid";


	private final static String OFFICERDETALIS="SELECT a.emp_id, a.emp_no,CONCAT(IFNULL(CONCAT(a.title,' '),(IFNULL(CONCAT(a.salutation, ' '), ''))), a.emp_name) AS 'empname' , b.designation, a.ext_no, a.email, (SELECT c.division_name FROM division_master c WHERE a.division_id= c.division_id LIMIT 1) AS 'divisionname', a.desig_id, a.division_id, a.sr_no FROM employee a,employee_desig b WHERE a.desig_id= b.desig_id AND a.is_active='1' AND a.emp_id=:officerid"; 
	private final static String LISTOFSENIORITYNUMBER="SELECT sr_no, emp_id FROM employee WHERE sr_no !=0 ORDER BY sr_no ASC ";

	private static final String ACTIVITYLIST="SELECT activitytypeid, activitytype, IsTimeSheet, ActivityCode FROM milestone_activity_type WHERE isactive=1";
	private static final String ACTIVITYNAMECHECK="SELECT COUNT(ActivityTypeId) AS 'count','ActivityType' FROM milestone_activity_type WHERE CASE WHEN ActivityTypeId<>0 THEN ActivityTypeId!=:ActivityTypeId END AND ActivityType=:ActivityType AND IsActive=1";
	private static final String GROUPLIST = "SELECT dg.group_id,dg.group_code,dg.group_name,dg.group_head_id,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation ,dg.lab_code,dt.tdcode FROM division_group dg,employee e, employee_desig ed, division_td dt WHERE e.is_active=1 AND dg.group_head_id=e.emp_id AND e.desig_id=ed.desig_id AND dg.td_id=dt.tdid  AND dg.is_active=1 AND dg.lab_code=:labcode ORDER BY dg.group_id DESC";
	private static final String GROUPHEADLIST ="SELECT e.emp_id,CONCAT(IFNULL(e.title,''), e.emp_name)AS 'empname',ed.designation FROM employee e, employee_desig ed WHERE  e.desig_id=ed.desig_id AND e.is_active=1 AND e.lab_code=:labcode AND e.emp_status NOT IN ('N') ORDER BY e.sr_no=0, e.sr_no";
	private static final String GROUPADDCHECK ="SELECT SUM(IF(group_code =:gcode,1,0))   AS 'dCode','0' AS 'codecount'FROM division_group WHERE is_active=1 ";
	private static final String GROUPDATA = "SELECT dg.group_id,dg.group_code,dg.group_name,dg.group_head_id,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation,dg.is_active,dg.td_id,e.lab_code AS 'Group Head Labcode' FROM division_group dg,employee e, employee_desig ed WHERE e.is_active=1 AND dg.group_head_id=e.emp_id AND e.desig_id=ed.desig_id AND  dg.group_id=:groupid";

	private static final String LABLIST="select labmasterid,labcode,labname,labunitcode,labaddress,labcity,labpin FROM lab_master";
	private static final String EMPLOYEELIST="SELECT emp_id, CONCAT(IFNULL(CONCAT(title,' '),''), emp_name) AS 'empname', lab_code, desig_id FROM employee WHERE is_active=1 ORDER BY sr_no ";
	private static final String LABMASTEREDITDATA="select labmasterid,labcode,labname,labunitcode,labaddress,labcity,labpin,labtelno,labfaxno,labemail,labauthority,labauthorityid,labrfpemail,lablogo,labid from lab_master where labmasterid= :labmasterid";
	private static final String LABSLIST="SELECT lab_id,cluster_id,lab_name,lab_code FROM cluster_lab";
	private static final String EMPNOCHECKAJAX="SELECT emp_id, CONCAT(IFNULL(CONCAT(title,' '),''), emp_name) AS 'empname' , emp_no FROM employee WHERE emp_no=:empno"; 
	private static final String EXTEMPNOCHECKAJAX="SELECT empid, empname , empno FROM employee_external WHERE empno=:empno";


	@PersistenceContext
	EntityManager manager;

	@Override
	public List<Object[]> OfficerList() throws Exception {
		Query query=manager.createNativeQuery(OFFICERLIST);
		List<Object[]> OfficerList=(List<Object[]>)query.getResultList();
		return OfficerList;
	}

	@Override
	public List<Object[]> DesignationList() throws Exception {

		Query query=manager.createNativeQuery(DESIGNATIONLIST);
		List<Object[]> DesignationList=(List<Object[]>)query.getResultList();
		return DesignationList;
	}

	@Override
	public List<Object[]> OfficerDivisionList() throws Exception {

		Query query=manager.createNativeQuery(OFFICERDIVISIONLIST);
		List<Object[]> DivisionList=(List<Object[]>)query.getResultList();
		return DivisionList;
	}

	@Override
	public List<Object[]> OfficerEditData(String OfficerId) throws Exception {

		Query query=manager.createNativeQuery(OFFICEREDITDATA);
		query.setParameter("empid", Long.parseLong(OfficerId));
		List<Object[]> OfficerEditData= (List<Object[]>) query.getResultList();

		return OfficerEditData;
	}

	@Override
	public int OfficerMasterDelete(Employee employee) throws Exception {
		Employee emp = manager.find(Employee.class, employee.getEmpId());
		if (emp == null) {
			return 0;
		}
		try {
			emp.setSrNo(employee.getSrNo());
			emp.setIsActive(employee.getIsActive());
			emp.setModifiedBy(employee.getModifiedBy());
			emp.setModifiedDate(employee.getModifiedDate());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int OfficerExtDelete(Employee employee) throws Exception {
		Employee emp = manager.find(Employee.class, employee.getEmpId());
		if (emp == null) {
			return 0;
		}
		try {
			emp.setIsActive(employee.getIsActive());
			emp.setModifiedBy(employee.getModifiedBy());
			emp.setModifiedDate(employee.getModifiedDate());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Override
	public List<String> EmpNoCheck() throws Exception {

		Query query=manager.createNativeQuery(EMPNOCHECK);
		List<String> EmpNoCheck=(List<String>)query.getResultList();
		return EmpNoCheck;
	}

	@Override
	public List<String> EmpExtNoCheck() throws Exception {

		Query query=manager.createNativeQuery(EMPEXTNOCHECK);
		List<String> EmpNoCheck=(List<String>)query.getResultList();
		return EmpNoCheck;
	}

	@Override
	public Long OfficeMasterInsert(Employee employee) throws Exception {
		if(employee.getSalutation() == null || employee.getSalutation().isBlank()) { 
			employee.setSalutation(null);
		}
		if(employee.getTitle() == null || employee.getTitle().isBlank()) { 
			employee.setTitle(null);
		}
		manager.persist(employee);
		manager.flush();
		return employee.getEmpId();
	}

	@Override
	public int OfficerMasterUpdate(Employee employee) throws Exception {
		Employee emp = manager.find(Employee.class, employee.getEmpId());
		if (emp == null) {
			return 0;
		}
		try {
			emp.setSalutation(employee.getSalutation() == null || employee.getSalutation().isBlank() ? null : employee.getSalutation());
			emp.setTitle(employee.getTitle() == null || employee.getTitle().isBlank() ? null : employee.getTitle());
			emp.setEmpNo(employee.getEmpNo());
			emp.setEmpName(employee.getEmpName());
			emp.setDesigId(employee.getDesigId());
			emp.setExtNo(employee.getExtNo());			
			emp.setEmail(employee.getEmail());			
			emp.setMobileNo(employee.getMobileNo());			
			emp.setDronaEmail(employee.getDronaEmail());			
			emp.setInternetEmail(employee.getInternetEmail());
			emp.setDivisionId(employee.getDivisionId());
			emp.setModifiedBy(employee.getModifiedBy());
			emp.setModifiedDate(employee.getModifiedDate());
			emp.setSuperiorOfficer(employee.getSuperiorOfficer());
			emp.setEmpStatus(employee.getEmpStatus());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	@Override
	public List<Object[]> LabList()throws Exception{

		Query query=manager.createNativeQuery(CLUSTERLAB);
		List<Object[]> OfficerEditData= (List<Object[]>) query.getResultList();

		return OfficerEditData;
	}

	@Override
	public Long OfficeMasterExternalInsert(Employee empExternal)throws Exception{

		manager.persist(empExternal);
		manager.flush();

		return empExternal.getEmpId();

	}
	@Override
	public List<Object[]> ExternalOfficerList(String LabCode)throws Exception{
		Query query=manager.createNativeQuery(EXTERNALOFFICERLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> OfficerList=(List<Object[]>)query.getResultList();
		return OfficerList;
	}
	@Override
	public List<Object[]> ExternalOfficerEditData(String officerId) throws Exception{

		Query query=manager.createNativeQuery(EXTERNALOFFICEREDITDATA);
		query.setParameter("empid", Long.parseLong(officerId));
		List<Object[]> OfficerEditData= (List<Object[]>) query.getResultList();

		return OfficerEditData;
	}

	@Override  
	public int OfficerExtUpdate(Employee employee) throws Exception{
		Employee emp = manager.find(Employee.class, employee.getEmpId());
		if (emp == null) {
			return 0;
		}
		try {
			emp.setSalutation(employee.getSalutation() == null ||employee.getSalutation().isBlank() ? null : employee.getSalutation());
			emp.setTitle(employee.getTitle() == null || employee.getTitle().isBlank() ? null : employee.getTitle());
			emp.setEmpNo(employee.getEmpNo());
			emp.setEmpName(employee.getEmpName());
			emp.setDesigId(employee.getDesigId());
			emp.setExtNo(employee.getExtNo());			
			emp.setEmail(employee.getEmail());			
			emp.setMobileNo(employee.getMobileNo());			
			emp.setDronaEmail(employee.getDronaEmail());			
			emp.setInternetEmail(employee.getInternetEmail());
			emp.setDivisionId(employee.getDivisionId());
			emp.setModifiedBy(employee.getModifiedBy());
			emp.setModifiedDate(employee.getModifiedDate());
			emp.setLabCode(employee.getLabCode());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Override
	public Object[] getOfficerDetalis(String officerId)throws Exception{
		Query query=manager.createNativeQuery(OFFICERDETALIS);
		query.setParameter("officerid", Long.parseLong(officerId));
		return (Object[])query.getSingleResult();
	}

	@Override
	public List<Object[]> updateAndGetList(Long empId, String newSeniorityNumber)throws Exception{

		Query query=manager.createNativeQuery(LISTOFSENIORITYNUMBER);
		List<Object[]> listSeni=(List<Object[]>)query.getResultList();
		
		Employee emp = manager.find(Employee.class, empId);
		if (emp != null) {
			emp.setSrNo(Long.parseLong(newSeniorityNumber));
		}
		return listSeni;
	}

	@Override
	public int updateAllSeniority(Long empIdL, Long long1)throws Exception{
		Employee emp = manager.find(Employee.class, empIdL);
		
		System.out.println(emp.toString());
		
		if (emp != null) {
			emp.setSrNo(long1);
			return 1;
		}
		return 0;
	}


	@Override
	public List<Object[]> DivisionList()throws Exception
	{		
		Query query=manager.createNativeQuery(DIVISIONLIST);   
		return  (List<Object[]>)query.getResultList();
	}


	@Override
	public List<Object[]> DivisionEmpList(String divisionid)throws Exception
	{		
		Query query=manager.createNativeQuery(DIVISIONEMPLIST); 
		query.setParameter("divisionid", Long.parseLong(divisionid));  	 
		return  (List<Object[]>)query.getResultList();
	}

	@Override
	public List<Object[]> DivisionNonEmpList(String divisionid)throws Exception
	{		
		Query query=manager.createNativeQuery(DIVISIONNONEMPLIST); 
		query.setParameter("divisionid", Long.parseLong(divisionid));  	 
		return  (List<Object[]>)query.getResultList();
	}

	@Override
	public Object[] DivisionData(String divisionid)throws Exception
	{
		Query query=manager.createNativeQuery(DIVISIONDATA); 
		query.setParameter("divisionid", Long.parseLong(divisionid));  
		return  (Object[])query.getSingleResult();
	}


	@Override
	public int DivsionEmployeeRevoke(DivisionEmployeeDto dto)throws Exception
	{
		DivisionEmployee dvEmp = manager.find(DivisionEmployee.class, dto.getDivisionEmployeeId());
		if (dvEmp == null) {
			return 0;
		}
		try {
			dvEmp.setModifiedBy(dto.getModifiedBy());
			dvEmp.setModifiedDate(dto.getModifiedDate());
			dvEmp.setIsActive(0);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	@Override
	public long DivisionAssignSubmit(DivisionEmployee model)throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getDivisionEmployeeId();
	}


	@Override
	public List<Object[]> ActivityList()throws Exception
	{		
		Query query=manager.createNativeQuery(ACTIVITYLIST);   
		return  (List<Object[]>)query.getResultList();
	}


	@Override
	public Object[] ActivityNameCheck(String activityTypeId, String activityType)throws Exception
	{		
		Query query=manager.createNativeQuery(ACTIVITYNAMECHECK);   
		query.setParameter("ActivityTypeId", Long.parseLong(activityTypeId));
		query.setParameter("ActivityType", activityType);
		return  (Object[])query.getSingleResult();
	}

	@Override
	public long ActivityAddSubmit(MilestoneActivityType model)throws Exception
	{	
		manager.persist(model);
		manager.flush();
		return model.getActivityTypeId();
	}


	@Override
	public List<Object[]> GroupsList(String LabCode)throws Exception
	{	
		Query query=manager.createNativeQuery(GROUPLIST);
		query.setParameter("labcode", LabCode);
		return (List<Object[]> )query.getResultList();
	}


	@Override
	public List<Object[]> GroupHeadList(String LabCode) throws Exception 
	{		
		Query query=manager.createNativeQuery(GROUPHEADLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> GroupHeadList=(List<Object[]>)query.getResultList();

		return GroupHeadList;
	}

	@Override
	public long GroupAddSubmit(DivisionGroup model) throws Exception 
	{	
		manager.persist(model);
		manager.flush();
		return model.getGroupId();
	}


	@Override
	public Object[] GroupAddCheck(String gcode) throws Exception 
	{
		Query query=manager.createNativeQuery(GROUPADDCHECK);   
		query.setParameter("gcode", gcode);
		return  (Object[])query.getSingleResult();
	}



	@Override
	public Object[] GroupsData(String groupid)throws Exception
	{	
		Query query=manager.createNativeQuery(GROUPDATA);
		query.setParameter("groupid", Long.parseLong(groupid));				
		return( Object[] )query.getSingleResult();
	}


	@Override
	public int GroupMasterUpdate(DivisionGroup model) throws Exception
	{		
		DivisionGroup divisionGroup = manager.find(DivisionGroup.class, model.getGroupId());
		if (divisionGroup == null) {
			return 0;
		}
		try { 
			divisionGroup.setGroupCode(model.getGroupCode());
			divisionGroup.setGroupName(model.getGroupName());
			divisionGroup.setGroupHeadId(model.getGroupHeadId());
			divisionGroup.setTDId(model.getTDId());
			divisionGroup.setModifiedBy(model.getModifiedBy());
			divisionGroup.setModifiedDate(model.getModifiedDate());
			divisionGroup.setIsActive(model.getIsActive());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Object[]> LabMasterList() throws Exception {
		Query query = manager.createNativeQuery(LABLIST);
		List<Object[]> LabList =(List<Object[]>) query.getResultList();
		return LabList;
	}

	@Override
	public List<Object[]> EmployeeList() throws Exception {

		Query query= manager.createNativeQuery(EMPLOYEELIST);
		List < Object[]>  EmployeeList =(List <Object[]>)query.getResultList();
		return EmployeeList;
	}

	@Override
	public List<Object[]> LabMasterEditData(String LabId) throws Exception {

		Query query = manager.createNativeQuery(LABMASTEREDITDATA);
		query.setParameter("labmasterid", Long.parseLong(LabId));
		List<Object[]> LabMasterEditData=(List<Object[]>) query.getResultList();
		return LabMasterEditData;
	}


	@Override
	public int LabMasterUpdate(LabMaster labmaster) throws Exception {
		
		LabMaster dbLabMaster = manager.find(LabMaster.class, labmaster.getLabMasterId());
		if (dbLabMaster == null) {
			return 0;
		}
		try {
			dbLabMaster.setLabCode(labmaster.getLabCode());
			dbLabMaster.setLabName(labmaster.getLabName());
			dbLabMaster.setLabUnitCode(labmaster.getLabUnitCode());
			dbLabMaster.setLabAddress(labmaster.getLabAddress());
			dbLabMaster.setLabCity(labmaster.getLabCity());
			dbLabMaster.setLabPin(labmaster.getLabPin());
			dbLabMaster.setLabTelNo(labmaster.getLabTelNo());
			dbLabMaster.setLabFaxNo(labmaster.getLabFaxNo());
			dbLabMaster.setLabEmail(labmaster.getLabEmail());
			dbLabMaster.setLabAuthority(labmaster.getLabAuthority());
			dbLabMaster.setLabAuthorityId(labmaster.getLabAuthorityId());
			dbLabMaster.setLabRfpEmail(labmaster.getLabRfpEmail());
			dbLabMaster.setLabId(labmaster.getLabId());
			dbLabMaster.setClusterId(labmaster.getClusterId());
			dbLabMaster.setModifiedBy(labmaster.getModifiedBy());
			dbLabMaster.setModifiedDate(labmaster.getModifiedDate());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public List<Object[]> LabsList() throws Exception 
	{		
		Query query= manager.createNativeQuery(LABSLIST);
		List < Object[]>  LabsList =(List <Object[]>)query.getResultList();
		return LabsList;
	}


	@Override
	public List<Object[]> empNoCheckAjax(String empno)throws Exception
	{		
		Query query=manager.createNativeQuery(EMPNOCHECKAJAX); 
		query.setParameter("empno", empno);  	 
		return  (List<Object[]>)query.getResultList();
	}


	@Override
	public List<Object[]> extEmpNoCheckAjax(String empno)throws Exception
	{		
		Query query=manager.createNativeQuery(EXTEMPNOCHECKAJAX); 
		query.setParameter("empno", empno);  	 
		return  (List<Object[]>)query.getResultList();
	}

	@Override
	public Long FeedbackInsert(PfmsFeedback feedback) throws Exception {
		manager.persist(feedback);
		manager.flush();
		return feedback.getFeedbackId();
	}

	@Override
	public long FeedbackAttachInsert(PfmsFeedbackAttach main) throws Exception {

		manager.persist(main);
		manager.flush();
		return main.getFeedbackAttachId();
	}

	private static final String FEEDBACKLIST = "SELECT a.feedbackid,b.empname,a.createddate , a.feedback , a.feedbacktype , a.status , a.remarks FROM pfms_feedback a,employee b WHERE a.isactive='1' AND a.empid=b.empid  AND CASE WHEN 'A'<>:feedbacktype THEN a.feedbacktype=:feedbacktype ELSE 1=1 END and CASE WHEN 'A'<>:empid THEN a.empid=:empid ELSE 1=1 END AND CAST(a.createddate AS DATE) BETWEEN :fromdate AND :todate AND b.labcode=:labcode  ORDER BY feedbackid DESC";

	@Override
	public List<Object[]> FeedbackList(String fromdate , String todate , String empid ,String LabCode,String feedbacktype) throws Exception{
		Query query = manager.createNativeQuery(FEEDBACKLIST);
		query.setParameter("empid", empid);
		query.setParameter("fromdate", fromdate);
		query.setParameter("todate", todate);
		query.setParameter("labcode", LabCode);
		query.setParameter("feedbacktype", feedbacktype);
		List<Object[]> FeedbackList = (List<Object[]>) query.getResultList();
		return FeedbackList;
	}
	private static final String FEEDBACKLISTFORUSER="SELECT a.feedbackid,b.emp_name,a.createddate , a.feedback , a.feedbacktype , a.status , a.remarks FROM pfms_feedback a,employee b WHERE a.isactive='1' AND a.empid=b.emp_id  AND CASE WHEN :empid<>'A' THEN a.empid=:empid ELSE 1=1 END AND MONTH(a.createddate)=MONTH(NOW())-1 AND b.lab_code=:labcode UNION SELECT a.feedbackid,b.emp_name,a.createddate , a.feedback , a.feedbacktype , a.status , a.remarks FROM pfms_feedback a,employee b WHERE a.isactive='1' AND a.empid=b.emp_id AND CASE WHEN :empid<>'A' THEN a.empid=:empid ELSE 1=1 END  AND b.lab_code=:labcode ORDER BY feedbackid DESC";
	@Override
	public List<Object[]> FeedbackListForUser(String LabCode , String empid) throws Exception
	{
		Query query = manager.createNativeQuery(FEEDBACKLISTFORUSER);
		query.setParameter("labcode", LabCode);
		query.setParameter("empid", empid);
		List<Object[]> FeedbackList = (List<Object[]>) query.getResultList();
		return FeedbackList;
	}

	private static final String FEEDBACKCONTENT = "SELECT a.feedbackid,b.emp_name,a.createddate,a.feedback,a.EmpId, a.Status FROM pfms_feedback a,employee b WHERE a.isactive='1' AND a.empid=b.emp_id AND feedbackid=:feedbackid";
	@Override
	public Object[] FeedbackContent(String feedbackid)throws Exception
	{		
		Query query=manager.createNativeQuery(FEEDBACKCONTENT);   
		query.setParameter("feedbackid", Long.parseLong(feedbackid));
		return  (Object[])query.getSingleResult();
	}
	
	private static final String ATTACHLIST="SELECT a.feedbackid, a.FeedbackAttachId ,a.path , a.filename  FROM pfms_feedback_attach a , pfms_feedback b WHERE a.feedbackid=b.feedbackid AND a.isactive=1";
	@Override
	public List<Object[]> GetfeedbackAttch()throws Exception
	{
		Query query = manager.createNativeQuery(ATTACHLIST);
		List<Object[]> FeedbackList = (List<Object[]>) query.getResultList();
		return FeedbackList;
	}
	
	@Override
	public PfmsFeedback getPfmsFeedbackById(String feedbackId) {
		try {
			return manager.find(PfmsFeedback.class, Long.parseLong(feedbackId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String USERATTCHFEEDBACK="SELECT a.feedbackid,a.FeedbackAttachId ,a.path , a.filename  FROM pfms_feedback_attach a , pfms_feedback b WHERE a.feedbackid=b.feedbackid AND b.empid=:empid AND a.isactive=1";
	@Override
	public List<Object[]> GetfeedbackAttchForUser(String empid)throws Exception
	{
		Query query = manager.createNativeQuery(USERATTCHFEEDBACK);
		query.setParameter("empid", Long.parseLong(empid));
		List<Object[]> FeedbackList = (List<Object[]>) query.getResultList();
		return FeedbackList;
	}

	@Override
	public PfmsFeedbackAttach FeedbackAttachmentDownload(String achmentid) throws Exception
	{
		PfmsFeedbackAttach Attachment= manager.find(PfmsFeedbackAttach.class,Long.parseLong(achmentid));
		return Attachment;
	}

	private static final String TDLIST="SELECT a.tdid ,a.tdcode, a.tdname , a.tdheadid ,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation ,a.labcode FROM  division_td  a,employee e, employee_desig ed WHERE a.tdheadid=e.emp_id AND e.desig_id=ed.desig_id AND a.isactive=1 AND e.is_active=1 AND a.labcode=:labcode ORDER BY a.tdid  DESC";
	@Override
	public List<Object[]> TDList(String LabCode) throws Exception {

		Query query = manager.createNativeQuery(TDLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> TDList = (List<Object[]>) query.getResultList();
		return TDList;
	}

	private static final String TDHEADLIST="SELECT e.emp_id,CONCAT(IFNULL(e.title,''), e.emp_name)AS 'empname',ed.designation FROM employee e, employee_desig ed WHERE  e.desig_id=ed.desig_id AND e.is_active=1 AND e.lab_code=:labcode AND e.emp_status NOT IN ('N') ORDER BY e.sr_no=0, e.sr_no";
	@Override
	public List<Object[]> TDHeadList(String LabCode) throws Exception 
	{		
		Query query=manager.createNativeQuery(TDHEADLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> TDHeadList=(List<Object[]>)query.getResultList();

		return TDHeadList;
	}

	public static final String TDDATA="SELECT dt.TDId,dt.TDCode,dt.TDName,dt.TDHeadId,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',ed.designation,dt.isactive FROM division_td dt,employee e, employee_desig ed WHERE e.is_active=1 AND dt.TDHeadId=e.emp_id AND e.desig_id=ed.desig_id AND  dt.tdid=:tdid";
	@Override
	public Object[] TDsData(String tdid)throws Exception
	{	
		Query query=manager.createNativeQuery(TDDATA);
		query.setParameter("tdid", Long.parseLong(tdid));				
		return( Object[] )query.getSingleResult();
	}

	@Override
	public long TDAddSubmit(DivisionTd dtd) throws Exception {

		manager.persist(dtd);
		manager.flush();
		return dtd.getTdId();
	}

	public static final String TDCHECK="SELECT SUM(IF(TDCode =:tcode,1,0))   AS 'tCode','0' AS 'codecount'FROM division_td WHERE isactive=1 ";
	@Override
	public Object[] TDAddCheck(String tCode) throws Exception {

		Query query = manager.createNativeQuery(TDCHECK);
		query.setParameter("tcode", tCode);
		return(Object[])query.getSingleResult();
	}
	
	private static final String ALERTTDMASTER="SELECT dt.tdcode,dg.is_active,dg.group_name  FROM division_group dg,employee e, employee_desig ed, division_td dt \r\n"
			+ "WHERE dg.group_head_id=e.emp_id AND e.desig_id=ed.desig_id AND dg.td_id=dt.tdid AND dg.is_active=1 AND dt.tdcode=:tdCode ORDER BY dg.group_id DESC";
	@Override
	public List<Object[]> CheckGroupMasterCode(String TdCode) throws Exception {
		
		Query query=manager.createNativeQuery(ALERTTDMASTER);
		query.setParameter("tdCode", TdCode);
		return (List<Object[]>)query.getResultList();
	}

	@Override
	public int TDMasterUpdate(DivisionTd model) throws Exception {
		DivisionTd divisionTd = manager.find(DivisionTd.class, model.getTdId());
		if(divisionTd == null) {
			return 0;
		}
		try {
			divisionTd.setTdCode(model.getTdCode());
			divisionTd.setTdName(model.getTdName());
			divisionTd.setTdHeadId(model.getTdHeadId());
			divisionTd.setModifiedBy(model.getModifiedBy());
			divisionTd.setModifiedDate(model.getModifiedDate());
			divisionTd.setIsActive(model.getIsActive());
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static final String TDADDLIST="SELECT a.tdid,a.tdname,a.tdcode,a.labcode FROM division_td a WHERE a.isactive=1";
	@Override
	public List<Object[]> TDListAdd() throws Exception {

		Query query = manager.createNativeQuery(TDADDLIST);
		List<Object[]> TDListAdd = (List<Object[]>)query.getResultList();
		return TDListAdd;
	}

	@Override
	public int UpdateActivityType(String activityType, String activityTypeId, String isTimeSheet, String activityCode) throws Exception {
		MilestoneActivityType milestoneActivityType = manager.find(MilestoneActivityType.class, Long.parseLong(activityTypeId));
		if(milestoneActivityType == null) {
			return 0;
		}
		try {
			milestoneActivityType.setActivityCode(activityCode);
			milestoneActivityType.setActivityType(activityType);
			milestoneActivityType.setIsTimeSheet(isTimeSheet);
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}

	@Override
	public int DeleteActivityType(String ActivityId) throws Exception {
		MilestoneActivityType milestoneActivityType = manager.find(MilestoneActivityType.class, Long.parseLong(ActivityId));
		if(milestoneActivityType == null) {
			return 0;
		}
		try {
			milestoneActivityType.setIsActive(0);
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static final String INDUSTRYPARTNERLIST="SELECT a.IndustryPartnerId,a.IndustryName,a.IndustryAddress,b.IndustryPartnerRepId,b.RepName,b.RepDesignation,b.RepMobileNo,b.RepEmail,b.IsActive,a.IndustryCity,a.IndustryPinCode FROM pfms_industry_partner a, pfms_industry_partner_rep b WHERE a.IndustryPartnerId=b.IndustryPartnerId AND a.IsActive=1 ORDER BY a.IndustryPartnerId DESC, b.IndustryPartnerRepId ASC";
	@Override
	public List<Object[]> industryPartnerList() throws Exception{
		try {
			Query query = manager.createNativeQuery(INDUSTRYPARTNERLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static final String INDUSTRYPARTNERDETAILSBYINDUSTRYPARTNERREPID="SELECT a.IndustryPartnerId,a.IndustryName,a.IndustryAddress,b.IndustryPartnerRepId,b.RepName,b.RepDesignation,b.RepMobileNo,b.RepEmail,b.IsActive,a.IndustryCity,a.IndustryPinCode FROM pfms_industry_partner a, pfms_industry_partner_rep b WHERE a.IndustryPartnerId=b.IndustryPartnerId AND a.IsActive=1 AND b.IsActive=1 AND b.IndustryPartnerRepId=:IndustryPartnerRepId";
	@Override
	public Object[] industryPartnerDetailsByIndustryPartnerRepId(String industryPartnerRepId) throws Exception{
		try {
			Query query = manager.createNativeQuery(INDUSTRYPARTNERDETAILSBYINDUSTRYPARTNERREPID);
			query.setParameter("IndustryPartnerRepId", Long.parseLong(industryPartnerRepId));
			List<Object[]> list = (List<Object[]>)query.getResultList();
			return list.size()>0? list.get(0) : null;
			
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<IndustryPartner> getIndustryPartnerList() throws Exception{
		try {
			Query query = manager.createQuery("FROM IndustryPartner WHERE IsActive=1");
			return (List<IndustryPartner>)query.getResultList();		
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public IndustryPartner getIndustryPartnerById(String industryPartnerId) throws Exception{
		try {
			return manager.find(IndustryPartner.class, Long.parseLong(industryPartnerId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public IndustryPartnerRep getIndustryPartnerRepById(String industryPartnerRepId) throws Exception{
		try {
			return manager.find(IndustryPartnerRep.class, Long.parseLong(industryPartnerRepId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public long addIndustryPartner(IndustryPartner partner) throws Exception{
		try {
			manager.persist(partner);
			manager.flush();
			return partner.getIndustryPartnerId();
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public int revokeIndustryPartnerRep(String industryPartnerRepId) throws Exception{
		IndustryPartnerRep industryPartnerRep = manager.find(IndustryPartnerRep.class, Long.parseLong(industryPartnerRepId));
		if(industryPartnerRep == null) {
			return 0;
		}
		try {
		industryPartnerRep.setIsActive(0);
			return 1;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static final String INDUSTRYPARTNERREPDETAILS="SELECT a.IndustryPartnerRepId,a.IndustryPartnerId,a.RepName,a.RepDesignation,a.RepMobileNo,a.RepEmail FROM pfms_industry_partner_rep a,pfms_industry_partner b WHERE a.IndustryPartnerId=b.IndustryPartnerId AND b.IndustryPartnerId=:IndustryPartnerId AND a.IsActive=1 AND b.IsActive=1 AND a.IndustryPartnerRepId<>:IndustryPartnerRepId";
	@Override
	public List<Object[]> industryPartnerRepDetails(String industryPartnerId, String industryPartnerRepId) throws Exception {
		try {
			Query query = manager.createNativeQuery(INDUSTRYPARTNERREPDETAILS);
			query.setParameter("IndustryPartnerId", Long.parseLong(industryPartnerId));
			query.setParameter("IndustryPartnerRepId", Long.parseLong(industryPartnerRepId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	@Override
	public Employee getEmployeeById(String empId) throws Exception {
		try {
			
			return manager.find(Employee.class, Long.parseLong(empId));
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//---------------------------------------------------------------------------------------------
	  private String HolidayList=" select a.HolidayId,a.HolidayDate,a.HolidayName,a.HolidayType from pfms_holiday_master a where  DATE_FORMAT(HolidayDate,'%Y')=:holiyear AND IsActive='1' ORDER BY HolidayDate DESC";
		@Override
		public List<Object[]> HolidayList(String yr) throws Exception {
			try {
				Query query = manager.createNativeQuery(HolidayList);
				query.setParameter("holiyear", yr);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		@Override
		public HolidayMaster getHolidayData(Long holidayid) {
			
			try {
				HolidayMaster holiday = manager.find(HolidayMaster.class,(holidayid));
				return holiday ;
			} catch (Exception e) {
				
				e.printStackTrace();
				return null;
			}
		}
		

		@Override
		public long HolidayEditSubmit(HolidayMaster hw) {
			
			manager.merge(hw);
			manager.flush();
			return hw.getHolidayId();
	   }
		
		@Override
	    public long HolidayAddSubmit(HolidayMaster holiday) {
			
			try {
				manager.persist(holiday);
				manager.flush();

			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return holiday.getHolidayId();
		}
		
		
		private static final String LABPMSEMPLOYEELIST="SELECT im.PcNo,im.Name,im.Designation AS 'immsDesignation',im.DivName,e.EmpNo,e.EmpName,d.Designation AS 'pmsDesignation',dv.DivisionName,im.EmpId AS 'immsEmpId',e.EmpId AS 'PmsEmpId' FROM lab_employee im LEFT JOIN employee e ON e.EmpNo=im.PcNo AND e.LabCode=:LabCode LEFT JOIN employee_desig d ON d.DesigId=e.DesigId LEFT JOIN division_master dv ON dv.DivisionId=e.DivisionId ORDER BY im.EmpId DESC";
		@Override
		public List<Object[]> labPmsEmployeeList(String LabCode) throws Exception {
			try {
				Query query =manager.createNativeQuery(LABPMSEMPLOYEELIST);
				query.setParameter("LabCode", LabCode);
				return (List<Object[]>)query.getResultList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null; 
		}
		
		
		@Override
		public LabPmsEmployee labemployeefindbyEmpId(long EmpId) throws Exception {
			try {
				return manager.find(LabPmsEmployee.class, EmpId);
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
		
		private static final String GETDESIGID="SELECT DesigId,DesigCode FROM employee_desig WHERE Designation=:designation";
		@Override
		public Object[] getDesigId(String designation) throws Exception {
			try {
				Query query =manager.createNativeQuery(GETDESIGID);
				query.setParameter("designation", designation);
				return (Object[])query.getSingleResult();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		
		@Override
		public long updateEmployee(Employee employee) throws Exception {
			try {
				manager.persist(employee);
				manager.flush();

			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return employee.getEmpId();
		}
		
		
		@Override
		public long EmployeeMasterInsert(Employee employee) throws Exception {
			try {
				manager.persist(employee);
				manager.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return employee.getEmpId();
		}

		private static final String EMPWITHROLES ="SELECT b.emp_id AS'Empid',a.Roleid,b.lab_code AS 'Organization', CONCAT(IFNULL(CONCAT(b.title,' '),IFNULL(CONCAT(b.salutation,' '),'')), b.emp_name) AS 'empname' ,a.EmpRole,b.emp_no AS 'empno'  ,'I' AS 'emptype', b.desig_id \r\n"
				+ "FROM  employee b  LEFT JOIN pfms_emp_roles a ON b.emp_no = a.empno\r\n"
				+ "UNION \r\n"
				+ "SELECT b.ExpertId AS'Empid',a.Roleid,b.Organization AS 'Organization', CONCAT(IFNULL(CONCAT(b.title,' '),IFNULL(CONCAT(b.salutation,' '),'')), b.expertname) AS 'empname' ,a.EmpRole,b.ExpertNo AS 'empno'  ,'E' AS 'emptype', b.desigId "
				+ "FROM  expert b  LEFT JOIN pfms_emp_roles a ON b.ExpertNo = a.empno ORDER BY Empid;";
		@Override
		public List<Object[]> getEmployees() throws Exception {
			Query query = manager.createNativeQuery(EMPWITHROLES);
			return (List<Object[]>)query.getResultList();
		}
		
		@Override
		public PfmsEmpRoles getPfmsEmpRolesById(String roleid) throws Exception {
			try {
				return manager.find(PfmsEmpRoles.class, Long.parseLong(roleid));
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public long addPfmsEmpRoles(PfmsEmpRoles pf) throws Exception {
			try {
				manager.persist(pf);
				manager.flush();
				return pf.getRoleId();
				
				
			}catch (Exception e) {
			e.printStackTrace();
				return 0;
			}
		}
		
		public List<PfmsEmpRoles>getAllEmpRoles() throws Exception {
			
			String queryStr = "SELECT i FROM PfmsEmpRoles i ";
	        TypedQuery<PfmsEmpRoles> query = manager.createQuery(queryStr, PfmsEmpRoles.class);
	        return query.getResultList();
			
			
		}

		private static final String DIVISIONMASTERCHECK="SELECT dm.group_id,dm.is_active,dm.division_name FROM division_master dm, division_group dg WHERE dg.group_id =dm.group_id  AND dm.is_active=1 AND dm.group_id=:GrpId ORDER BY dg.group_id DESC";
		@Override
		public List<Object[]> checkDivisionMasterId(String groupId) {
			Query query=manager.createNativeQuery(DIVISIONMASTERCHECK);
			query.setParameter("GrpId", Long.parseLong(groupId));
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String GETFEEDBACKTRANSACTIONBYFEEDBACKID = "SELECT a.FeedbackTransId, a.FeedbackId, a.Comments, a.ActionBy, a.ActionDate, CONCAT(IFNULL(CONCAT(b.Title,' '),(IFNULL(CONCAT(b.Salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.Designation FROM pfms_feedback_trans a LEFT JOIN employee b ON a.ActionBy = b.emp_id LEFT JOIN employee_desig c ON b.desig_id = c.desig_id WHERE a.FeedbackId=:FeedbackId";
		@Override
		public List<Object[]> getFeedbackTransByFeedbackId(String feedbackId)throws Exception
		{
			try {
				Query query=manager.createNativeQuery(GETFEEDBACKTRANSACTIONBYFEEDBACKID);
				query.setParameter("FeedbackId", Long.parseLong(feedbackId));
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();
			}

		}
		
		@Override
		public long addPfmsFeedbackTrans(PfmsFeedbackTrans transaction) throws Exception {
			try {
				manager.persist(transaction);
				manager.flush();
				return transaction.getFeedbackTransId();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		/* **************************** Programme Master - Naveen R  - 16/07/2025 **************************************** */
		private static final String PROGRAMMASTERLIST = "SELECT a.ProgrammeId, a.PrgmCode, a.PrgmName, a.PrgmDirector, a.SanctionedOn, CONCAT(IFNULL(CONCAT(b.title,' '),(IFNULL(CONCAT(b.salutation, ' '), ''))), b.emp_name) AS 'EmpName', c.designation FROM pfms_programme_master a, employee b, employee_desig c WHERE a.IsActive = 1 AND a.PrgmDirector = b.emp_id AND b.desig_id= c.desig_id ORDER BY a.ProgrammeId DESC";
		@Override
		public List<Object[]> getProgramMasterList() throws Exception {
			try {
				Query query = manager.createNativeQuery(PROGRAMMASTERLIST);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();	
			}
				
		}

		@Override
		public long addProgrammeMaster(ProgrammeMaster master) throws Exception {
			try {
				manager.persist(master);
				manager.flush();
				return master.getProgrammeId();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		private static final String PPROGRAMPROJECTLINKEDDELETE = "UPDATE pfms_programme_projects SET IsActive='0' WHERE ProgrammeId=:ProgrammeId ";
		@Override
		public int removeProjectLinked(String programmeId) throws Exception {
			try {
				Query query =manager.createNativeQuery(PPROGRAMPROJECTLINKEDDELETE);
				query.setParameter("ProgrammeId", Long.parseLong(programmeId));
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public long addProgrammeProjects(ProgrammeProjects prgmprojects) throws Exception {
			try {
				manager.persist(prgmprojects);
				manager.flush();
				return prgmprojects.getPrgmProjLinkedId();
			}catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		private static final String PROGRAMCODECHECK = "SELECT COUNT(PrgmCode) FROM pfms_programme_master WHERE IsActive= '1' AND ProgrammeId!=:ProgrammeId AND PrgmCode=:PrgmCode";
		@Override
		public Long ProgramCodeCheck(String PrgmCode, String ProgrammeId) throws Exception {
			try {
				Query query = manager.createNativeQuery(PROGRAMCODECHECK);
				query.setParameter("PrgmCode", PrgmCode);
				query.setParameter("ProgrammeId", ProgrammeId);
				return (Long) query.getSingleResult();
			} catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		}
		
		private static final String PROJECTLIST = "SELECT DISTINCT project_id, project_main_id, project_code, project_name, project_short_name FROM project_master WHERE is_active=1 AND lab_code=:labCode AND project_id NOT IN (SELECT projectid FROM pfms_programme_projects WHERE isActive=1)";
		@Override
		public List<Object[]> getProjectList(String labcode) throws Exception {
			try {
				Query query = manager.createNativeQuery(PROJECTLIST);
				query.setParameter("labCode", labcode);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace(); 
				return new ArrayList<Object[]>();	
			}
		} 
		
		private static final String PROJECTLISTFORPROGRAMMEID = "SELECT a.projectid, a.projectmainid, a.projectcode, a.projectname, a.ProjectShortName FROM project_master a LEFT JOIN pfms_programme_projects b ON a.projectid = b.projectId AND b.isActive = 1 LEFT JOIN pfms_programme_master c ON b.programmeId = c.programmeId AND c.isActive = 1 WHERE a.isActive = 1 AND a.labcode = :labCode AND ( b.projectId IS NULL OR ( c.programmeId =:programmeId AND a.projectid IN ( SELECT projectId FROM pfms_programme_projects WHERE isActive = 1 GROUP BY projectId HAVING COUNT(DISTINCT programmeId) = 1 ))) ORDER BY a.projectid ASC;";
		@Override
		public List<Object[]> getProjectList(String labcode, String programmeId) throws Exception {
			try {
				Query query = manager.createNativeQuery(PROJECTLISTFORPROGRAMMEID);
				query.setParameter("labCode", labcode);
				query.setParameter("programmeId", programmeId);
				return (List<Object[]>)query.getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Object[]>();	
			}
		}
				
		/* **************************** Programme Master - Naveen R  - 16/07/2025 End**************************************** */

		@Override
		public Long addRoleMaster(RoleMaster roleMaster)throws Exception {
			try {
				manager.persist(roleMaster);
				manager.flush();
				return roleMaster.getRoleMasterId();
			}catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		}

		// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check start
		private static final String DUPLICATEROLENAMECOUNT = "SELECT COUNT(RoleName) FROM pfms_role_master WHERE RoleName=:RoleName AND IsActive=1";
		@Override
		public Long getRoleNameDulicateCount(String roleName) throws Exception {
			try {
				Query query = manager.createNativeQuery(DUPLICATEROLENAMECOUNT);
				query.setParameter("RoleName", roleName);
				return (Long)query.getSingleResult();
			}catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		}

		private static final String DUPLICATEROLECODECOUNT = "SELECT COUNT(RoleCode) FROM pfms_role_master WHERE RoleCode=:RoleCode AND IsActive=1";
		@Override
		public Long getRoleCodeDuplicateCount(String roleCode) throws Exception {
			try {
				Query query = manager.createNativeQuery(DUPLICATEROLECODECOUNT);
				query.setParameter("RoleCode", roleCode);
				return (Long)query.getSingleResult();
			}catch (Exception e) {
				e.printStackTrace();
				return 0L;
			}
		}

		// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check End
		
		@Override
		public List<LabMaster> getAllLabMaster() throws Exception {
			try {
				String jpql = "FROM LabMaster";

			    return manager
			            .createQuery(jpql, LabMaster.class)
			            .getResultList();
			}catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}

		
}
