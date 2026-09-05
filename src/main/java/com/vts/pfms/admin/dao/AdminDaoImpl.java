package com.vts.pfms.admin.dao;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.admin.dto.EmployeeDesigDto;
import com.vts.pfms.admin.model.AuditPatches;
import com.vts.pfms.admin.model.DivisionMaster;
import com.vts.pfms.admin.model.EmployeeDesig;
import com.vts.pfms.admin.model.Expert;
import com.vts.pfms.admin.model.PfmsFormRoleAccess;
import com.vts.pfms.admin.model.PfmsLoginRoleSecurity;
import com.vts.pfms.admin.model.PfmsRtmddo;
import com.vts.pfms.admin.model.PfmsStatistics;
import com.vts.pfms.admin.model.WeekUpdate;
import com.vts.pfms.login.Login;
import com.vts.pfms.login.PfmsLoginRole;
import com.vts.pfms.mail.MailConfiguration;
import com.vts.pfms.master.model.DivisionEmployee;

@Transactional
@Repository
public class AdminDaoImpl implements AdminDao{

	private static final Logger logger=LogManager.getLogger(AdminDaoImpl.class);
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String LOGINTYPELIST ="SELECT a.username,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname',c.division_name,d.rolename,a.loginid FROM login a,employee b,division_master c,pfms_role_security d,pfms_login_role_security e WHERE a.empid=b.emp_id AND a.divisionid=c.division_id AND a.loginid=e.loginid AND e.roleid=d.roleid AND a.isactive='1' AND a.pfms='Y' AND b.is_active=1 ORDER BY b.srno";
	private static final String EMPLOYEELIST ="SELECT a.loginid,a.username,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname' FROM login a, employee b WHERE a.empid=b.emp_id AND b.is_active=1 AND a.isactive='1' AND a.pfms='N' ORDER BY b.srno ";
	private static final String ROLELIST="SELECT RoleId, RoleName FROM  pfms_role_security ";
	private static final String LOGINTYPEEDITDATA="SELECT a.username,CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname' ,c.division_name,d.rolename,a.loginid FROM login a,employee b,division_master c,pfms_role_security d,pfms_login_role_security e WHERE a.empid=b.emp_id AND a.divisionid=c.division_id AND a.loginid=e.loginid AND e.roleid=d.roleid AND a.isactive='1' AND a.pfms='Y' and a.loginid=:loginid ";
	private static final String PFMSLOGINREVOKE="DELETE from pfms_login_role_security WHERE loginid=:loginid";
	private static final String EMPLOYEELISTALL="select a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname',b.designation,a.lab_code FROM employee a,employee_desig b WHERE a.is_active='1' AND a.desig_id=b.desig_id";
	private static final String RTMDDO="SELECT 'empid1',a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname',b.designation,c.validfrom,c.validto  FROM employee a,employee_desig b,pfms_initiation_approver c WHERE a.emp_id=c.empid AND c.isactive='1' AND a.is_active='1' AND a.desig_id=b.DesigId AND c.type='DO-RTMD'";
	// new code
	private static final String NOTIFICATIONLIST="SELECT notificationdate, notificationmessage, notificationurl, notificationid FROM pfms_notification WHERE empid =:empid AND isactive = 1 ORDER BY CreatedDate DESC LIMIT 0, 1000";
	private static final String RTMDDOUPDATE="update pfms_initiation_approver set isactive='0' WHERE Type=:type";
	private static final String DIVISIONLIST ="select division_id,division_code from division_master where is_active='1'";
	private static final String AUDITSTAMPING="SELECT a.username,a.logindate, a.logindatetime,a.ipaddress, a.macaddress, ( CASE WHEN a.logouttype='L' THEN 'Logout' ELSE 'Session Expired' END ) AS logouttype, 	a.logoutdatetime FROM auditstamping a WHERE a.`LoginDate` BETWEEN :fromdate AND :todate AND a.loginid=:loginid ORDER BY a.`LoginDateTime` DESC ";
	private static final String USERNAMELIST="SELECT l.loginid, l.empid,l.username, CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname',e.lab_code FROM login l , employee e WHERE e.is_active=1 AND l.isactive=1 AND l.EmpId=e.emp_id ORDER BY e.sr_no=0,e.sr_no"; 
	private static final String LOGINEDITDATA="FROM Login WHERE LoginId=:LoginId";
	private static final String USERMANAGELIST = "SELECT a.loginid, a.username, b.division_name,c.formrolename, a.Pfms , CONCAT(IFNULL(CONCAT(e.title,' '),''), e.emp_name) AS 'empname', d.designation ,lt.logindesc ,e.emp_no ,e.lab_code FROM login a , division_master b , form_role c , employee e, employee_desig d,  login_type lt WHERE a.divisionid=b.division_id AND a.formroleid=c.formroleid AND a.isactive=1 AND a.empid=e.emp_id AND e.desig_id=d.desig_id AND a.logintype=lt.logintype ";
	private static final String USERNAMEPRESENTCOUNT ="select count(*) from login where username=:username and isactive IN ('1','0')";
	private static final String EMPLOYEELIST1="SELECT emp_id,CONCAT(IFNULL(CONCAT(title,' '),''), emp_name) AS 'empname' FROM employee e WHERE e.is_active='1' AND lab_code=:labcode AND emp_id NOT IN (SELECT empid FROM login WHERE isactive=1) ORDER BY sr_no ";
	private final static String CHECKUSER = "SELECT COUNT(LoginId) FROM pfms_login_role_security WHERE LoginId=:loginid";
	private final static String UPDATEPFMSLOGINROLE="UPDATE pfms_login_role_security SET RoleId=:roleid WHERE LoginId=:loginid";
	private static final String CURRENTADDORTMT="SELECT r.RtmddoId, r.EmpId, r.ValidFrom, r.ValidTo, r.Type,r.labcode FROM pfms_initiation_approver r WHERE r.IsActive=1 ORDER BY r.Type DESC";

	private static final String DIVISIONLIST1 ="SELECT a.division_id,a.division_code,a.division_name, CONCAT(IFNULL(CONCAT(b.title,' '),''), b.emp_name) AS 'empname' ,c.group_name ,a.division_name, d.Designation, a.division_short_name FROM division_master a,employee b,division_group c, employee_desig d WHERE a.is_active='1' AND b.is_active='1' AND b.desig_id = d.desig_id and a.division_head_id=b.emp_id AND a.group_id=c.group_id AND a.lab_code=:labcode ORDER BY a.division_id desc"; //srikant
	private static final String DIVISIONADDCHECK="SELECT SUM(IF(division_code =:divisionCode,1,0))   AS 'dCode',SUM(IF(division_name = :divisionName,1,0)) AS 'dName' FROM division_master where is_active=1 ";
	private static final String DIVISIONGROUPLIST="SELECT a.group_id,a.group_name,a.lab_code FROM division_group a WHERE a.is_active=1";
	private static final String DIVISIONHEADLIST="SELECT a.emp_id,CONCAT(IFNULL(CONCAT(a.title,' '),''), a.emp_name) AS 'empname',a.lab_code,b.designation FROM employee a , employee_desig b WHERE a.is_active=1 AND a.desig_id=b.desig_id AND a.emp_status NOT IN ('N') ORDER BY a.sr_no = 0, a.sr_no";
	private static final String DIVISIONEDITDATA="SELECT d.division_id,d.division_code, d.division_name, d.division_head_id, d.group_id, d.is_active, d.division_short_name , e.lab_code AS 'Division Head Labcode' FROM division_master d LEFT JOIN employee e ON e.emp_id = d.division_head_id WHERE d.division_id=:divisionid ";	//srikant
	private static final String DESIGNATIONDATA="SELECT desig_id,desig_code,designation,desig_limit,desig_sr,desig_cadre FROM employee_desig WHERE desig_id=:desigid";
	private static final String DESIGNATIONLIST="SELECT desig_id,desig_code,designation,desig_limit,desig_sr,desig_cadre FROM employee_desig ORDER BY desig_sr";
	private static final String DESIGNATIONCODECHECK="SELECT COUNT(desig_code),'desigcode' FROM employee_desig WHERE desig_code=:desigcode";

	private static final String DESIGNATIONCHECK="SELECT COUNT(designation),'designation' FROM employee_desig WHERE designation=:designation";
	private static final String DESIGNATIONCODEEDITCHECK="SELECT COUNT(desig_code),'desigcode' FROM employee_desig WHERE desig_code=:desigcode AND desig_id<>:desigid";
	private static final String DESIGNATIONEDITCHECK="SELECT COUNT(designation),'designation' FROM employee_desig WHERE designation=:designation AND desig_id<>:desigid";
	private static final String LISTOFDESIGSENIORITYNUMBER ="SELECT desig_sr,desig_id FROM employee_desig WHERE desig_sr!=0 ORDER BY desig_sr ASC ";
	private static final String LOGINTYPEROLES="SELECT LoginTypeId,LoginType,LoginDesc FROM login_type";
	private static final String FORMDETAILSLIST="SELECT b.formroleaccessid,b.logintype,a.formname,b.isactive ,b.labhq ,a.formdetailid FROM  (SELECT fd.formdetailid,fd.formmoduleid,fd.formname FROM pfms_form_detail fd WHERE  fd.isactive=1 AND CASE WHEN :moduleid <> 'A' THEN fd.formmoduleid =:moduleid ELSE 1=1 END) AS a LEFT JOIN  (SELECT b.formroleaccessid,b.logintype,a.formname,b.isactive ,b.labhq , b.formdetailid FROM pfms_form_detail a ,pfms_form_role_access b  WHERE a.formdetailid=b.formdetailid AND a.isactive=1 AND b.logintype=:logintype AND  CASE WHEN :moduleid <> 'A' THEN a.formmoduleid =:moduleid ELSE 1=1 END ) AS b ON a.formdetailid = b.formdetailid";
	private static final String FORMMODULELIST="SELECT FormModuleId,FormModuleName,ModuleUrl,IsNav,IsActive FROM pfms_form_module WHERE isactive=1";
	private static final String FORMROLEACTIVELIST="SELECT isactive FROM pfms_form_role_access WHERE formroleaccessid=:formroleaccessid";
	private static final String EMPLOYEEDATA ="SELECT a.emp_id, a.sr_no,a.emp_no,a.emp_name,a.desig_id,a.division_id ,b.group_id  FROM employee  a , division_master b WHERE a.division_id =b.division_id  AND a.emp_id=:empid";
	private static final String LOGINTYPELIST1="select logintype,logindesc,logintypeid from login_type";	
	private static final String LOGINEDITEMPLIST = "SELECT emp_id,CONCAT(IFNULL(CONCAT(title,' '),''), emp_name) AS 'empname' FROM employee WHERE lab_code=:labcode ORDER BY sr_no ";
	private static final String GETEXPERTLIST= "SELECT e.ExpertId, e.ExpertNo,CONCAT(IFNULL(CONCAT(e.title,' '),''), e.ExpertName)AS 'ExpertName', d.Designation , e.MobileNo, e.ExtNo, e.Email, e.Organization, e.IsActive FROM expert e, employee_desig d WHERE  e.DesigId=d.desig_id ";
	private static final String GETDESIGNATION = "SELECT desig_id, desig_code, designation FROM employee_desig";
	private static final String  ABILITYOFEXPERTNO ="SELECT COUNT(*)FROM expert WHERE ExpertNo=:EXPERTNO ";
	private static final String ABILITYOFEXTENSIONNO = "SELECT COUNT(*)FROM expert WHERE ExtNo=:EXTNO ";
	private static final String  GETEDITDETAILS = "SELECT expertid , title , salutation , expertname, desigid , mobileno , email , organization , expertno FROM expert WHERE ExpertId=:EXPERTID";
	private static final String CHECKABILITY2 ="SELECT COUNT(*)FROM expert WHERE ExtNo=:EXTNO AND  ExpertId NOT IN (:ExpertId)";
	private static final String CLUSTERLABLIST="SELECT lab_id,cluster_id,lab_name,lab_code FROM cluster_lab";



	@PersistenceContext
	EntityManager manager;

	@Override
	public List<Object[]> AllLabList() throws Exception 
	{
		Query query=manager.createNativeQuery(CLUSTERLABLIST);
		List<Object[]> ClusterLabList=(List<Object[]>)query.getResultList();
		return ClusterLabList;
	}


	@Override
	public List<Object[]> LoginTypeList() throws Exception {
		Query query=manager.createNativeQuery(LOGINTYPELIST);

		List<Object[]> LoginTypeList=(List<Object[]>)query.getResultList();		

		return LoginTypeList;
	}

	@Override
	public List<Object[]> EmployeeList() throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELIST);

		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();		

		return EmployeeList;
	}


	@Override
	public List<Object[]> LoginEditEmpList(String LabCode) throws Exception {
		Query query=manager.createNativeQuery(LOGINEDITEMPLIST);
		query.setParameter("labcode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();		

		return EmployeeList;
	}

	@Override
	public Object[] EmployeeData(String empid) throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEEDATA);
		query.setParameter("empid", Long.parseLong(empid));
		List<Object[]> EmployeeData=(List<Object[]>)query.getResultList();		

		return EmployeeData.get(0);
	}

	@Override
	public List<Object[]> RoleList() throws Exception {
		Query query=manager.createNativeQuery(ROLELIST);
		List<Object[]> RoleList=(List<Object[]>)query.getResultList();		
		return RoleList;
	}

	@Override
	public List<Object[]> LoginTypeList1() throws Exception {
		Query query=manager.createNativeQuery(LOGINTYPELIST1);
		List<Object[]> LoginTypeList=(List<Object[]>)query.getResultList();		
		return LoginTypeList;
	}

	@Override
	public Long LoginTypeAddSubmit(PfmsLoginRoleSecurity loginrole,Login login) throws Exception {
		manager.persist(loginrole);
		
		Login ExistingLogin=manager.find(Login.class, loginrole.getLoginId());
		if(ExistingLogin!=null) {
			ExistingLogin.setPfms(login.getPfms());
			ExistingLogin.setModifiedBy(login.getModifiedBy());
			ExistingLogin.setModifiedDate(login.getModifiedDate());
			return 1L;
		}
		else
		{
			return 0L;
		}
		

		
	}

	@Override
	public Long LoginTypeRevoke(Login login) throws Exception {
		Login ExistingLogin=manager.find(Login.class, login.getLoginId());
		if(ExistingLogin !=null) {
			ExistingLogin.setPfms("N");
			ExistingLogin.setModifiedBy(login.getModifiedBy());
			ExistingLogin.setModifiedDate(login.getModifiedDate());	
		}
		else {
			return 0L;
		}


		Query query1=manager.createNativeQuery(PFMSLOGINREVOKE);
		query1.setParameter("loginid", login.getLoginId());
		query1.executeUpdate();	

		return login.getLoginId();
	}

	@Override
	public List<Object[]> LoginTypeEditData(String LoginId) throws Exception {
		Query query=manager.createNativeQuery(LOGINTYPEEDITDATA);
		query.setParameter("loginid", Long.parseLong(LoginId));
		List<Object[]> LoginTypeEditData=(List<Object[]>)query.getResultList();		

		return LoginTypeEditData;
	}

	@Override
	public Long LoginTypeEditSubmit(PfmsLoginRoleSecurity loginrole,Login login) throws Exception {
		
		PfmsLoginRoleSecurity ExistingPfmsLoginRoleSecurity = manager.find(PfmsLoginRoleSecurity.class, loginrole.getLoginId());
		Login ExistingLogin=manager.find(Login.class, login.getLoginId());
		
		if(ExistingPfmsLoginRoleSecurity !=null && ExistingLogin != null) {
			ExistingPfmsLoginRoleSecurity.setRoleId(loginrole.getRoleId());

			
			ExistingLogin.setModifiedBy(login.getModifiedBy());
			ExistingLogin.setModifiedDate(login.getModifiedDate());


			return 1L;
		}
		else {
			return 0L;
		}
		
		

		
	}

	@Override
	public List<Object[]> NotificationList(String EmpId) throws Exception {
		Query query=manager.createNativeQuery(NOTIFICATIONLIST);
		query.setParameter("empid", Long.parseLong(EmpId));
		List<Object[]> NotificationList=(List<Object[]>)query.getResultList();		

		return NotificationList;
	}

	@Override
	public List<Object[]> EmployeeListAll() throws Exception {
		Query query=manager.createNativeQuery(EMPLOYEELISTALL);

		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}

	@Override
	public List<Object[]> Rtmddo() throws Exception {
		Query query=manager.createNativeQuery(RTMDDO);

		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();	
		return EmployeeList;
	}

	@Override
	public long RtmddoInsert(PfmsRtmddo rtmddo) throws Exception {
		manager.persist(rtmddo);
		manager.flush();
		return rtmddo.getRtmddoId();
	}

	@Override
	public int RtmddoUpdate(String type) throws Exception {
		Query query=manager.createNativeQuery(RTMDDOUPDATE);
		query.setParameter("type", type);
		int count=query.executeUpdate();
		return count;
	}



	@Override
	public List<Object[]> GetExpertList() throws Exception {
		final Query query = this.manager.createNativeQuery(GETEXPERTLIST);
		final List<Object[]> ExpertList = (List<Object[]>)query.getResultList();
		return ExpertList;
	}

	private static final String GETEXPERTSCOUNT ="SELECT COUNT(*) FROM expert ";

	@Override
	public long GetExpertsCount() throws Exception {
		final Query query = manager.createNativeQuery(GETEXPERTSCOUNT);
		Long Expertcount = (Long)query.getSingleResult();
		return Expertcount.longValue()+1;
	}


	@Override
	public List<Object[]> GetDesignation() throws Exception {
		final Query query = this.manager.createNativeQuery(GETDESIGNATION);
		final List<Object[]> DesigList = (List<Object[]>)query.getResultList();
		return DesigList;
	}


	@Override
	public int abilityOfexpertNo( String expertNo) throws Exception {
		final Query query = this.manager.createNativeQuery(ABILITYOFEXPERTNO);
		query.setParameter("EXPERTNO", (Object)expertNo);
		final Object count = query.getSingleResult();
		final int countR = Integer.parseInt(count.toString());
		return countR;
	}


	@Override
	public int abilityOfextensionNo( String extensionNo) throws Exception {
		final Query query = this.manager.createNativeQuery(ABILITYOFEXTENSIONNO);
		query.setParameter("EXTNO", (Object)extensionNo);
		final Object count = query.getSingleResult();
		final int countR = Integer.parseInt(count.toString());
		return countR;
	}

	@Override
	public Long addExpert( Expert newExpert) throws Exception {
		manager.persist(newExpert);
		this.manager.flush();
		return newExpert.getExpertId();

	}


	@Override
	public Long ExpertRevoke( Expert expert) throws Exception {
		
		Expert ExistingExpert= manager.find(Expert.class, expert.getExpertId());
		if(ExistingExpert !=null) {
			ExistingExpert.setIsActive(0);
			ExistingExpert.setModifiedBy(expert.getModifiedBy());
			ExistingExpert.setModifiedDate(expert.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
	
	}



	@Override
	public List<Object[]> getEditDetails( String expertId) throws Exception {
		final Query query = this.manager.createNativeQuery(GETEDITDETAILS);
		query.setParameter("EXPERTID", Long.parseLong(expertId));

		final List<Object[]> details = (List<Object[]>)query.getResultList();
		return details;
	}



	@Override
	public int checkAbility2( String extensionNo, final String expertId) throws Exception {
		final Query query = this.manager.createNativeQuery(CHECKABILITY2);
		query.setParameter("EXTNO", (Object)extensionNo);
		query.setParameter("ExpertId", Long.parseLong(expertId));
		final Object count = query.getSingleResult();
		final int countR = Integer.parseInt(count.toString());
		return countR;
	}


	@Override
	public Long editExpert( Expert newExpert) throws Exception {
		
		Expert ExistingExpert = manager.find(Expert.class, newExpert.getExpertId());
		if(ExistingExpert != null) {
			ExistingExpert.setTitle(newExpert.getTitle());
			ExistingExpert.setSalutation(newExpert.getSalutation());
			ExistingExpert.setExpertName(newExpert.getExpertName());
			ExistingExpert.setDesigId(newExpert.getDesigId());
			ExistingExpert.setExtNo(newExpert.getExtNo());
			ExistingExpert.setMobileNo(newExpert.getMobileNo());
			ExistingExpert.setEmail(newExpert.getEmail());
			ExistingExpert.setOrganization(newExpert.getOrganization());
			ExistingExpert.setModifiedBy(newExpert.getModifiedBy());
			ExistingExpert.setModifiedDate(newExpert.getModifiedDate());
			return 1L;
		}
		else {
			return 0L;
		}
		
	}


	@Override
	public List<Object[]> AuditStampingList(String loginid,LocalDate Fromdate,LocalDate Todate) throws Exception {

		Query query = manager.createNativeQuery(AUDITSTAMPING);
		query.setParameter("loginid", loginid);
		query.setParameter("fromdate", Fromdate);
		query.setParameter("todate", Todate);

		List<Object[]> AuditStampingList=(List<Object[]>) query.getResultList();

		return AuditStampingList;
	}

	@Override
	public List<Object[]> UsernameList() throws Exception {

		Query query = manager.createNativeQuery(USERNAMELIST);

		List<Object[]> UsernameList=(List<Object[]>) query.getResultList();

		return UsernameList;

	}

	@Override
	public List<Object[]> DivisionList() throws Exception {
		Query query = manager.createNativeQuery(DIVISIONLIST);

		List<Object[]> DivisionList = query.getResultList();
		return DivisionList;
	}


	@Override
	public Login UserManagerEditData(Long LoginId) throws Exception {
		Query query = manager.createQuery(LOGINEDITDATA);
		query.setParameter("LoginId", LoginId);
		Login UserManagerEditData = (Login) query.getSingleResult();

		return UserManagerEditData;
	}

	@Override
	public int UserManagerDelete(Login login) throws Exception {
		
		Login ExistingLogin=manager.find(Login.class, login.getLoginId());
		if(ExistingLogin!=null) {
			ExistingLogin.setIsActive(0);
			ExistingLogin.setModifiedBy(login.getModifiedBy());
			ExistingLogin.setModifiedDate(login.getModifiedDate());
			return 1;
		}
		else {
			return 0;
		}

	}

	@Override
	public List<Object[]> UserManagerList( ) throws Exception {
		Query query = manager.createNativeQuery(USERMANAGELIST);

		List<Object[]> UserManagerList = query.getResultList();
		return UserManagerList;
	}

	@Override
	public int UserNamePresentCount(String UserName) throws Exception {
		Query query = manager.createNativeQuery(USERNAMEPRESENTCOUNT);
		query.setParameter("username", UserName);

		Long UserNamePresentCount = (Long) query.getSingleResult();
		return   UserNamePresentCount.intValue();
	}

	@Override
	public List<Object[]> EmployeeList1(String LabCode) throws Exception {

		Query query=manager.createNativeQuery(EMPLOYEELIST1);
		query.setParameter("labcode", LabCode);
		List<Object[]> EmployeeList=(List<Object[]>)query.getResultList();		
		return EmployeeList;
	}
	@Override
	public Long UserManagerInsert(Login login,DivisionEmployee logindivision) throws Exception {

		manager.persist(login);
		manager.persist(logindivision);
		manager.flush();

		return login.getLoginId();
	}

	@Override
	public int UserManagerUpdate(Login login) throws Exception {
		
		Login ExistingLogin=manager.find(Login.class, login.getLoginId());
		if(ExistingLogin !=null) {
			ExistingLogin.setDivisionId(login.getDivisionId());
			ExistingLogin.setFormRoleId(login.getFormRoleId());
			ExistingLogin.setLoginType(login.getLoginType());
			ExistingLogin.setEmpId(login.getEmpId());
			ExistingLogin.setPfms(login.getPfms());
			ExistingLogin.setModifiedBy(login.getModifiedBy());
			ExistingLogin.setModifiedDate(login.getModifiedDate());		
			return 1;
		}
		else {
			return 0;
		}

		
	}


	@Override
	public Long pfmsRoleInsert(PfmsLoginRole pfmsrole) throws Exception {
		manager.persist(pfmsrole);
		manager.flush();
		return pfmsrole.getLoginRoleSecurityId();
	}



	@Override
	public Object checkUser(Long loginId) throws Exception {
		Query query = manager.createNativeQuery(CHECKUSER);
		query.setParameter("loginid", loginId);
		Object flag = (Object) query.getSingleResult();
		return flag;
	}


	@Override
	public Long updatePfmsLoginRole(Long role, Long loginId) throws Exception {
		Query query = manager.createNativeQuery(UPDATEPFMSLOGINROLE);
		query.setParameter("roleid", role);
		query.setParameter("loginid", loginId);
		query.executeUpdate();
		return role;
	}

	@Override
	public List<Object[]> presentEmpList()throws Exception{
		Query query =manager.createNativeQuery(CURRENTADDORTMT);
		return query.getResultList();
	}



	@Override
	public List<Object[]> DesignationList()throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONLIST);
		return (List<Object[]>)query.getResultList();
	}


	@Override
	public Object[] DesignationData(String desigid)throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONDATA);
		query.setParameter("desigid", Long.parseLong(desigid));
		return (Object[])query.getResultList().get(0);
	}


	@Override
	public long DesignationEditSubmit(EmployeeDesigDto dto)throws Exception
	{
		EmployeeDesig ExistingEmployeeDesig=manager.find(EmployeeDesig.class, dto.getDesigId() );
		if(ExistingEmployeeDesig != null) {
			ExistingEmployeeDesig.setDesigCode(dto.getDesigCode());
			ExistingEmployeeDesig.setDesignation(dto.getDesignation());
			ExistingEmployeeDesig.setDesigLimit(Long.parseLong(dto.getDesigLimit()));
			ExistingEmployeeDesig.setDesigCadre(dto.getDesigCadre());
			return 1L;
		}
		else {
			return 0L;
		}

	}

	@Override
	public long DesignationAddSubmit(EmployeeDesig model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getDesigId();
	}

	@Override
	public Object[] DesignationCodeCheck(String desigcode)throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONCODECHECK);
		query.setParameter("desigcode", desigcode);
		return (Object[])query.getSingleResult();
	}

	@Override
	public Object[] DesignationCheck(String designation)throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONCHECK);
		query.setParameter("designation", designation);
		return (Object[])query.getSingleResult();
	}


	@Override
	public Object[] DesignationCodeEditCheck(String desigcode,String desigid )throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONCODEEDITCHECK);
		query.setParameter("desigcode", desigcode);
		query.setParameter("desigid", Long.parseLong(desigid));
		return (Object[])query.getSingleResult();
	}

	@Override
	public Object[] DesignationEditCheck(String designation,String desigid)throws Exception
	{
		Query query =manager.createNativeQuery(DESIGNATIONEDITCHECK);
		query.setParameter("designation", designation);
		query.setParameter("desigid", Long.parseLong(desigid));
		return (Object[])query.getSingleResult();
	}



	@Override
	public List<Object[]> DivisionMasterList(String LabCode) throws Exception {
		Query query = manager.createNativeQuery(DIVISIONLIST1);
		query.setParameter("labcode", LabCode);
		List<Object[]> DivisionList = query.getResultList();
		return DivisionList;
	}

	@Override
	public List<Object[]> DivisionGroupList() throws Exception{

		Query query=manager.createNativeQuery(DIVISIONGROUPLIST);
		List<Object[]> DivisionGroupList=(List<Object[]>)query.getResultList();

		return DivisionGroupList;
	}

	@Override
	public List<Object[]> DivisionHeadList() throws Exception {

		Query query=manager.createNativeQuery(DIVISIONHEADLIST);
		List<Object[]> DivisionHeadList=(List<Object[]>)query.getResultList();

		return DivisionHeadList;
	}


	@Override
	public List<Object[]> DivisionMasterEditData(String DivisionId) throws Exception {

		Query query= manager.createNativeQuery(DIVISIONEDITDATA);
		query.setParameter("divisionid", Long.parseLong(DivisionId));
		List<Object[]> DivisionMasterEditData=(List<Object[]>) query.getResultList();

		return DivisionMasterEditData;
	}


	@Override
	public List<Object[]> DivisionAddCheck(String dCode,String dName) throws Exception
	{

		Query query=manager.createNativeQuery(DIVISIONADDCHECK);
		query.setParameter("divisionCode", dCode);
		query.setParameter("divisionName", dName);

		List<Object[]> DivisionAddCheck=(List<Object[]>)query.getResultList();

		return DivisionAddCheck;
	}


	@Override
	public long DivisionAddSubmit(DivisionMaster model) throws Exception
	{
		manager.persist(model);
		manager.flush();
		return model.getGroupId();
	}


	@Override
	public int DivisionMasterUpdate(DivisionMaster divisionmaster) throws Exception {
		DivisionMaster ExistingDivisionMaster=manager.find(DivisionMaster.class, divisionmaster.getDivisionId());
		if(ExistingDivisionMaster != null) {
			ExistingDivisionMaster.setDivisionCode(divisionmaster.getDivisionCode());
			ExistingDivisionMaster.setDivisionName(divisionmaster.getDivisionName());
			ExistingDivisionMaster.setDivisionHeadId(divisionmaster.getDivisionHeadId());
			ExistingDivisionMaster.setGroupId(divisionmaster.getGroupId());
			ExistingDivisionMaster.setModifiedBy(divisionmaster.getModifiedBy());
			ExistingDivisionMaster.setModifiedDate(divisionmaster.getModifiedDate());
			ExistingDivisionMaster.setIsActive(divisionmaster.getIsActive());
			ExistingDivisionMaster.setDivisionShortName(divisionmaster.getDivisionShortName());
			return 1;
		}
		else {
			return 0;
		}
		
	}


	@Override
	public List<Object[]> DesigupdateAndGetList(Long desigid, String newSeniorityNumber)throws Exception
	{			
		Query query=manager.createNativeQuery(LISTOFDESIGSENIORITYNUMBER);
		List<Object[]> listSeni=(List<Object[]>)query.getResultList();
		
		EmployeeDesig ExistingEmployeeDesig = manager.find(EmployeeDesig.class, desigid);
		if(ExistingEmployeeDesig != null) {
			ExistingEmployeeDesig.setDesigSr(Integer.parseInt(newSeniorityNumber));
			manager.merge(ExistingEmployeeDesig);
		}
		return listSeni;
		
		
	}

	@Override
	public int updateAllDesigSeniority(Long desigid, Long srno)throws Exception{
		
		EmployeeDesig ExistingEmployeeDesig = manager.find(EmployeeDesig.class, desigid);
		if(ExistingEmployeeDesig != null) {
			ExistingEmployeeDesig.setDesigSr(srno.intValue());
			manager.merge(ExistingEmployeeDesig);
			return 1;
		}
		else {
			return 0;
		}
		
	}

	@Override
	public List<Object[]> LoginTypeRoles() throws Exception {

		Query query=manager.createNativeQuery(LOGINTYPEROLES);
		List<Object[]> LoginTypeRoles=(List<Object[]>)query.getResultList();

		return LoginTypeRoles;
	}

	@Override
	public List<Object[]> FormDetailsList(String LoginType,String ModuleId) throws Exception {

		Query query=manager.createNativeQuery(FORMDETAILSLIST);
		query.setParameter("logintype", LoginType);
		query.setParameter("moduleid", ModuleId);
		List<Object[]> FormDetailsList=(List<Object[]>)query.getResultList();

		return FormDetailsList;
	}

	@Override
	public List<Object[]> FormModulesList() throws Exception {

		Query query=manager.createNativeQuery(FORMMODULELIST);
		List<Object[]> FormModulesList=(List<Object[]>)query.getResultList();

		return FormModulesList;
	}

	@Override
	public List<BigInteger> FormRoleActiveList(String formroleaccessid) throws Exception {

		Query query=manager.createNativeQuery(FORMROLEACTIVELIST);
		query.setParameter("formroleaccessid", Long.parseLong(formroleaccessid));
		List<BigInteger> FormRoleActiveList=(List<BigInteger>)query.getResultList();

		return FormRoleActiveList;
	}

	@Override
	public Long FormRoleActive(String formroleaccessid, Long Value) throws Exception {

		int count=0;


		if(Value.equals(1L)) {
			PfmsFormRoleAccess ExistingRoleAccess = manager.find(PfmsFormRoleAccess.class, Long.parseLong(formroleaccessid));
			if(ExistingRoleAccess != null) {
				ExistingRoleAccess.setIsActive(0);
			
				return 1L;
			}
			else {
				return 0L;
			}
		
		}
		if(Value.equals(0L)) {
			PfmsFormRoleAccess ExistingRoleAccess = manager.find(PfmsFormRoleAccess.class, Long.parseLong(formroleaccessid));
			if(ExistingRoleAccess != null) {
				ExistingRoleAccess.setIsActive(1);
				
				return 1L;
			}
			else {
				return 0L;
			}
		
		}

		return (long) count;
	}



	@Override
	public Long LabHqChange(String formroleaccessid, String Value) throws Exception{
		
		
		PfmsFormRoleAccess ExistingPfmsFormRoleAccess =manager.find(PfmsFormRoleAccess.class, Long.parseLong(formroleaccessid));
		if(ExistingPfmsFormRoleAccess !=null) {
			ExistingPfmsFormRoleAccess.setLabHQ(Value);
			
			return 1L;
		}
		else {
			return 0L;
		}
		

	}

	@Override
	public int checkavaibility(String logintype,String detailsid)throws Exception{
		Query query = manager.createNativeQuery("SELECT COUNT(formroleaccessid)  FROM `pfms_form_role_access` WHERE logintype=:logintype  AND  formdetailid=:detailsid");
		query.setParameter("logintype", logintype);
		query.setParameter("detailsid", Long.parseLong(detailsid) );

		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}
	@Override
	public Long insertformroleaccess(PfmsFormRoleAccess main) throws Exception {
		logger.info(new Date() + "Inside insertformroleaccess()");
		try {
			manager.persist(main);
			manager.flush();
			return (long)main.getFormRoleAccessId();
		} catch (Exception e) {
			e.printStackTrace();
			return 0l;
		}

	}
	@Override
	public int updateformroleaccess(String formroleid,String active,String auth)throws Exception{
		Query query = manager.createNativeQuery("UPDATE pfms_form_role_access SET isactive=:isactive , modifieddate=:modifieddate , modifiedby=:modifiedby WHERE formroleaccessid=:formroleaccessid");
		query.setParameter("formroleaccessid", Long.parseLong(formroleid));
		query.setParameter("isactive", active);
		query.setParameter("modifieddate",sdf1.format(new Date()));
		query.setParameter("modifiedby", auth);

		return query.executeUpdate();
	}


	@Override
	public int resetPassword(String lid, String userId, String password, String modifieddate) throws Exception {
		
		Login ExistingLogin=manager.find(Login.class, Long.parseLong(lid));
		if(ExistingLogin != null) {
			ExistingLogin.setPassword(password);
			ExistingLogin.setModifiedBy(userId);
			ExistingLogin.setModifiedDate(modifieddate);
			
			return 1;
		}
		else {
			return 0;
		}
		
		
	}

	private static final String FIRSTDAY="SELECT MIN(logindate) AS 'MINDATE'  FROM auditstamping";
	@Override
	public String firstDateOfAudit() throws Exception {
		Query query = manager.createNativeQuery(FIRSTDAY);
		Object result = query.getSingleResult();
		if(result != null) {
			// Assuming logindate is of type java.sql.Date
			java.sql.Date minDate = (java.sql.Date) result;
			// You can convert the date to a String using a SimpleDateFormat or another method
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return result.toString();
		} else {
			return "No records found";
		}
	}
	// newlyCreated

	private static final String EMPLIST="SELECT a.empid,a.username,COUNT(b.loginid)AS 'TotalLogInCounts' FROM login a, auditstamping b WHERE a.loginid=b.loginid AND b.logindate=:logindate GROUP BY b.loginid";
	@Override
	public List<Object[]> getAllEmployeesOfDate(String date) throws Exception {
		Query query = manager.createNativeQuery(EMPLIST);
		query.setParameter("logindate", date);
		List<Object[]>emplist= new ArrayList<>();
		emplist=(List<Object[]>)query.getResultList();
		return emplist;
	}
	private static final String WORKCOUNTS="CALL pfms_statistics_data(:username,:date)";
	@Override
	public Object[] ListOfWorkCounts(String username, String date) throws Exception {
		Query query=manager.createNativeQuery(WORKCOUNTS);
		query.setParameter("username", username);
		query.setParameter("date", date);
		Object[] workCounts=(Object[])query.getSingleResult();
		return workCounts;
	}

	@Override
	public int  DataInsetrtIntoPfmsStatistics(List<PfmsStatistics> pfmsStatistics) throws Exception {
		// TODO Auto-generated method stub
		for(PfmsStatistics p:pfmsStatistics) {
			manager.persist(p);
		}
		return 1;
	}

	private static final String PMSSTATTABLEDATA="select * from pfms_statistics";
	@Override
	public List<Object[]> getpfmsStatiscticsTableData() throws Exception {
		Query query = manager.createNativeQuery(PMSSTATTABLEDATA);
		List<Object[]>totalData=(List<Object[]>)query.getResultList();
		return totalData;
	}

	private static final String EMPLOYEELISTDH="SELECT a.EmpId,a.EmpName,b.Designation FROM employee a,employee_desig b WHERE a.labcode=:labCode AND a.IsActive=1 AND a.DesigId=b.DesigId AND a.DivisionId=:division";
	private static final String EMPLOYEELISTGH="SELECT a.EmpId,a.EmpName,b.Designation FROM employee a,employee_desig b WHERE a.labcode=:labCode AND a.IsActive=1 AND a.DesigId=b.DesigId AND a.DivisionId=:division";
	private static final String ALLEMPLOYEELIST="SELECT a.EmpId,a.EmpName,b.Designation FROM employee a,employee_desig b WHERE a.labcode=:labCode AND a.IsActive=1 AND a.DesigId=b.DesigId";

	@Override
	public List<Object[]> StatsEmployeeList(String logintype, String division, String labCode) throws Exception {
		// TODO Auto-generated method stub
		if(logintype.equalsIgnoreCase("D")) {
			Query query=manager.createNativeQuery(EMPLOYEELISTDH);
			query.setParameter("division", division);
			query.setParameter("labCode", labCode);

			return (List<Object[]>)query.getResultList();
		}else if(logintype.equalsIgnoreCase("G")) {
			Query query=manager.createNativeQuery(EMPLOYEELISTGH);
			query.setParameter("division", division);
			query.setParameter("labCode", labCode);

			return (List<Object[]>)query.getResultList();
		}else if(logintype.equalsIgnoreCase("A")|| logintype.equalsIgnoreCase("X")|| logintype.equalsIgnoreCase("Z")||logintype.equalsIgnoreCase("E") || logintype.equalsIgnoreCase("L")){
			Query query=manager.createNativeQuery(ALLEMPLOYEELIST);
			query.setParameter("labCode", labCode);

			return (List<Object[]>)query.getResultList();
		}else {
			return null;
		}
	}

	private static final String COUNT="SELECT a.EmpName,c.Designation, b.* FROM employee a JOIN pfms_statistics b ON a.EmpId = b.EmpId JOIN employee_desig c ON a.DesigId = c.DesigId WHERE b.EmpId =:employeeId AND b.LogDate  BETWEEN :fromDate AND :toDate ORDER BY b.Logdate DESC";
	@Override
	public List<Object[]> getEmployeeWiseCount(long employeeId, String fromDate, String toDate) throws Exception {
		Query query = manager.createNativeQuery(COUNT);
		query.setParameter("employeeId", employeeId);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		return (List<Object[]>)query.getResultList();
	}

	private static final String INITIATINAPPROVALAUTH="SELECT a.RtmddoId,a.LabCode,a.EmpId,a.ValidFrom,a.ValidTo,a.Type,b.emp_name,b.emp_no,c.Designation FROM pfms_initiation_approver a,employee b,employee_desig c WHERE a.IsActive=1 AND a.InitiationId=0 AND b.is_active=1 AND a.EmpId=b.emp_id AND b.desig_id=c.desig_id AND a.LabCode=:labcode";
	@Override
	public List<Object[]> initiationApprovalAuthority(String labcode) throws Exception {
		try {
			Query query = manager.createNativeQuery(INITIATINAPPROVALAUTH);
			query.setParameter("labcode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public PfmsRtmddo getApprovalAuthById(String RtmddoId) throws Exception{
		try {
			return manager.find(PfmsRtmddo.class, Long.parseLong(RtmddoId));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}

	private static final String APPROVALAUTHREVOKE = "UPDATE pfms_initiation_approver SET IsActive='0' WHERE RtmddoId=:RtmddoId";
	@Override
	public int approvalAuthRevoke(String RtmddoId) throws Exception {
		Query query=manager.createNativeQuery(APPROVALAUTHREVOKE);
		query.setParameter("RtmddoId", Long.parseLong(RtmddoId));
		int count=query.executeUpdate();
		return count;
	}
	private static final String MAILCONFIGURATIONLIST = "SELECT a.MailConfigurationId,a.Username,a.Host,a.TypeOfHost,a.Port,a.Password,a.CreatedBy,a.CreatedDate FROM mail_configuration a  WHERE a.IsActive='1' ORDER BY MailConfigurationId DESC";
	@Override
	public List<Object[]> MailConfigurationList()throws Exception{
		Query query = manager.createNativeQuery(MAILCONFIGURATIONLIST);
		List<Object[]> MailConfigurationList = query.getResultList();
		return MailConfigurationList;
	}
	private static final String DELETEMAILCONFIGURATION = "UPDATE mail_configuration SET IsActive=0 AND ModifiedBy=:modifiedBy AND ModifiedDate=:modifiedDate WHERE MailConfigurationId=:mailConfigurationId";
	public long DeleteMailConfiguration(long MailConfigurationId, String ModifiedBy)throws Exception{
		logger.info(new Date() + "Inside DaoImpl DeleteMailConfiguration");
		try {
			Query query = manager.createNativeQuery(DELETEMAILCONFIGURATION);
			query.setParameter("mailConfigurationId", MailConfigurationId);
			query.setParameter("modifiedBy", ModifiedBy);
			query.setParameter("modifiedDate", sdf1.format(new Date()));		
			int DeleteMailConfiguration = (int) query.executeUpdate();
			return  DeleteMailConfiguration;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl DeleteMailConfiguration", e);
			return 0;
		}
	}
	@Override
	public long AddMailConfiguration( MailConfiguration mailConfigAdd)throws Exception{
		logger.info(new Date() + "Inside DaoImpl AddMailConfiguration");
		try {
			manager.persist(mailConfigAdd);
			manager.flush();
			return mailConfigAdd.getMailConfigurationId();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl AddMailConfiguration", e);
			return 0;
		}
	}
	private static final String MAILCONFIGURATIONEDITLIST ="SELECT a.MailConfigurationId,a.Username,a.Host,a.TypeOfHost,a.Port,a.Password,a.CreatedBy,a.CreatedDate FROM mail_configuration a  WHERE a.MailConfigurationId=:mailConfigurationId";
	@Override
	public Object[] MailConfigurationEditList(long MailConfigurationId)throws Exception{
		logger.info(new Date() + "Inside DaoImpl MailConfigurationEditList");
		try {
			Query query = manager.createNativeQuery(MAILCONFIGURATIONEDITLIST);
			query.setParameter("mailConfigurationId", MailConfigurationId);
			return (Object[])query.getSingleResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl MailConfigurationEditList", e);
			return null;
		}
	}

	private static final String UPDATEMAILCONFIGURATION = "UPDATE mail_configuration a SET a.Username=:userName ,a.TypeOfHost=:hostType, a.ModifiedBy=:modifiedBy ,a.ModifiedDate=:modifiedDate,a.Host=:Host,a.Port=:Port,a.Password=:pass WHERE a.MailConfigurationId=:mailConfigurationId";

	@Override
	public long UpdateMailConfiguration(long MailConfigurationId,String userName,String hostType, String modifiedBy,String Host,String Port,String pass)throws Exception{
		logger.info(new Date() + "Inside DaoImpl MailConfigurationEditList");
		try {
			Query query = manager.createNativeQuery(UPDATEMAILCONFIGURATION);
			query.setParameter("mailConfigurationId", MailConfigurationId);
			query.setParameter("userName", userName);
			query.setParameter("hostType", hostType);
			query.setParameter("Host", Host);
			query.setParameter("Port", Port);
			query.setParameter("pass", pass);
			query.setParameter("modifiedBy", modifiedBy);
			query.setParameter("modifiedDate", sdf1.format(new Date()));		
			int DeleteMailConfiguration = (int) query.executeUpdate();
			return  DeleteMailConfiguration;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl MailConfigurationEditList", e);
			return 0;
		}

	}

	private static final String LastUpdate = "SELECT pwu.UpdatedDate, pwu.projectId  FROM pfms_weekly_update AS pwu  JOIN project_master pm ON pwu.projectid=pm.project_id  WHERE DATEDIFF(NOW(),pwu.updateddate)<6 ORDER BY pwu.UpdatedDate DESC";

	@Override
	public List<Object[]> lastUpdate() {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery(LastUpdate);
		try {
			return query.getResultList();}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}


	@Override
	public Object weeklyupdate(int empid, String username, String dateofupdate, String procurement,
			String actionpoints, String riskdetails, String meeting, String mile,int projectid) {
		// TODO Auto-generated method stub
		WeekUpdate w = new  WeekUpdate(procurement, username, dateofupdate, empid, actionpoints, riskdetails, meeting, mile, projectid);
		manager.persist(w);
		manager.flush();
		return null;
	}

	@Override
	public List<Object[]> ProjectListPD(String empId) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT * FROM project_master WHERE project_director=:empId");
		query.setParameter("empId", Long.parseLong(empId));
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Override
	public List<Object[]> ProjectListIC(String empId) throws Exception {
		// TODO Auto-generated method stub
		Query query = manager.createNativeQuery("SELECT pe.projectid,pm.lab_code, pm.project_main_id, pm.project_type, pm.project_type,pm.unit_code , pm.project_name FROM project_employee pe JOIN project_master pm ON pe.projectid=pm.project_id WHERE pe.empid=:empId");
		query.setParameter("empId", Long.parseLong(empId));
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	private static final String GETTYPEOFHOSTCOUNT="SELECT COUNT(*) FROM mail_configuration WHERE TypeOfHost=:hostType";

	@Override
	public long getTypeOfHostCount(String hostType) throws Exception {
		logger.info(new Date() + "Inside getTypeOfHostCount");
		try {
			Query query = manager.createNativeQuery(GETTYPEOFHOSTCOUNT);
			query.setParameter("hostType", hostType);
			Long getTypeOfHostCount = (Long) query.getSingleResult();
			return getTypeOfHostCount.longValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date() + "Inside DaoImpl getTypeOfHostCount", e);
			return 0;
		}
	}
	private static final String ROLEACCESS="SELECT a.FormDetailId,a.FormName,a.isactive,b.FormRoleAccessId FROM pfms_form_detail a, pfms_form_role_access b WHERE a.FormDetailId=b.FormDetailId AND b.LoginType=:logintype AND a.FormUrl=:FormUrl AND b.isactive='1'";
	@Override
	public List<Object[]> hasroleAccess(String FormUrl, String logintype) throws Exception {

		Query query = manager.createNativeQuery(ROLEACCESS);

		System.out.println("FormUrl "+FormUrl +" "+logintype);
		query.setParameter("logintype", logintype);
		query.setParameter("FormUrl", FormUrl);

		List<Object[]>hasroleAccess = (List<Object[]>)query.getResultList();

		return hasroleAccess;
	}

	private static final String GETFORMURLLIST="SELECT a.FormDetailId,a.FormName,a.FormUrl,a.isactive,b.FormRoleAccessId FROM pfms_form_detail a, pfms_form_role_access b WHERE a.FormDetailId=b.FormDetailId AND b.LoginType=:loginType AND b.isactive='1' AND a.FormDetailId=:formdetaild ";
	@Override
	public List<Object[]> getFormUrlList(String loginType,String formdetaild)  {
		try {

			Query query = manager.createNativeQuery(GETFORMURLLIST);
			query.setParameter("loginType", loginType);
			query.setParameter("formdetaild", Long.parseLong(formdetaild));
			List<Object[]>hasroleAccess = (List<Object[]>)query.getResultList();
			return hasroleAccess;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static final String GETALLURLLIST="SELECT * FROM pfms_form_url WHERE Url=:Url and isactive='1'";
	@Override
	public List<Object[]> getAllUrlList(String Url)  {
		try {
			Query query = manager.createNativeQuery(GETALLURLLIST);
			query.setParameter("Url", Url);
			List<Object[]>allurllist = (List<Object[]>)query.getResultList();
			return allurllist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String AUDITPATCHESLIST="SELECT VersionNo,Description,CreatedDate,Attachment,AuditPatchesId,PatchDate FROM  pfms_audit_patches order by CreatedDate desc";
	@Override
	public List<Object[]> getAuditPatchesList() throws Exception{
		try {
			Query query = manager.createNativeQuery(AUDITPATCHESLIST);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	private static final String  UPDATEPATCHDETAILS="update pfms_audit_patches set Description=:description,Attachment=:attachment,ModifiedBy=:modifiedBy,ModifiedDate=:modifiedDate where AuditPatchesId=:auditPatchesId";
	@Override
	public int auditpatchAddSubmit(AuditPatches model) throws Exception
	{
		Query query = manager.createNativeQuery(UPDATEPATCHDETAILS);
		query.setParameter("description", model.getDescription());
		query.setParameter("attachment", model.getAttachment());
		query.setParameter("modifiedBy", model.getModifiedBy());
		query.setParameter("modifiedDate", model.getModifiedDate());
		query.setParameter("auditPatchesId", model.getAuditPatchesId());
		return query.executeUpdate();
	}
	@Override
	public AuditPatches getAuditPatchById(Long attachId) {
		return manager.find(AuditPatches.class, attachId); // Fetches the entity by its ID
	}

	private static final String FORMDETAILID="SELECT a.FormDetailId , a.FormUrl FROM pfms_form_detail a WHERE a.FormUrl =:url\r\n"
			+ "  UNION "
			+ "  SELECT a.FormDetailId , a.Url FROM pfms_form_url a WHERE a.Url =:url AND isactive='1'" ;
	@Override
	public List<Object[]> getFormId(String url)  {
		try {
			Query query = manager.createNativeQuery(FORMDETAILID);
			query.setParameter("url", url);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public static final String CHECKDIVISIONMASTER="SELECT de.EmpId,de.isactive FROM division_master dm, division_employee de WHERE dm.division_id =de.DivisionId  AND de.isactive=1 AND de.DivisionId=:divId ORDER BY de.empid ASC";
	@Override
	public List<Object[]> checkDivisionMasterId(String divisionId) {
		Query query=manager.createNativeQuery(CHECKDIVISIONMASTER);
		query.setParameter("divId", Long.parseLong(divisionId));
		return (List<Object[]>)query.getResultList();
	}
}
