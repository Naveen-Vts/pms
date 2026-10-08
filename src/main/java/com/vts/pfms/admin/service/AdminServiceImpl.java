
package com.vts.pfms.admin.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.dao.AdminDao;
import com.vts.pfms.admin.dto.EmployeeDesigDto;
import com.vts.pfms.admin.dto.PfmsLoginRoleSecurityDto;
import com.vts.pfms.admin.dto.PfmsRtmddoDto;
import com.vts.pfms.admin.dto.UserManageAdd;
import com.vts.pfms.admin.model.AuditPatches;
import com.vts.pfms.admin.model.DivisionMaster;
import com.vts.pfms.admin.model.EmployeeDesig;
import com.vts.pfms.admin.model.Expert;
import com.vts.pfms.admin.model.PfmsFormRoleAccess;
import com.vts.pfms.admin.model.PfmsLoginRoleSecurity;
import com.vts.pfms.admin.model.PfmsRtmddo;
import com.vts.pfms.admin.model.PfmsStatistics;
import com.vts.pfms.login.Login;
import com.vts.pfms.mail.MailConfiguration;
import com.vts.pfms.mail.ReversibleEncryptionAlg;
import com.vts.pfms.master.model.DivisionEmployee;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	AdminDao dao;
	
	//@Autowired
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	ReversibleEncryptionAlg rea;
	
	
	private static final Logger logger=LogManager.getLogger(AdminServiceImpl.class);
	
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    FormatConverter fc=new FormatConverter();
	@Override
	public List<Object[]> LoginTypeList() throws Exception 
	{
		return dao.LoginTypeList();
	}

	@Override
	public List<Object[]> EmployeeList() throws Exception {
		 
		return dao.EmployeeList();
	}
	
	@Override
	public List<Object[]> LoginEditEmpList(String LabCode) throws Exception {
		return dao.LoginEditEmpList(LabCode);
	}

	@Override
	public Object[] EmployeeData(String empid ) throws Exception {
		 
		return dao.EmployeeData(empid);
	}
	
	@Override
	public List<Object[]> RoleList() throws Exception {
		return dao.RoleList();
	}
	@Override
	public List<Object[]> EmployeeList1(String LabCode) throws Exception {
		
		return dao.EmployeeList1(LabCode);
	}


	@Override
	public List<Object[]> LoginTypeList1() throws Exception 
	{		
		return dao.LoginTypeList1();
	}
	
	@Override
	public Long UserManagerInsert(UserManageAdd UserManageAdd, String Userid) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE UserManagerInsert ");
		if(dao.UserNamePresentCount(UserManageAdd.getUserName())==0) {
		Login login=new Login();
		DivisionEmployee logindivision=new DivisionEmployee();
		login.setUsername(UserManageAdd.getUserName());
		login.setPassword("$2y$12$QTTMcjGKiCVKNvNa242tVu8SPi0SytTAMpT3XRscxNXHHu1nY4Kui");
		login.setPfms("Y");
		login.setDivisionId(Long.parseLong(UserManageAdd.getDivision()));
		login.setFormRoleId(Long.parseLong(UserManageAdd.getRole()));
		login.setCreatedBy(Userid);
		login.setCreatedDate(sdf1.format(new Date()));
		login.setIsActive(1);
		login.setLoginType(UserManageAdd.getLoginType());
		login.setLoginTypeDms("U");
		if(UserManageAdd.getEmployee()!=null) {
		login.setEmpId(Long.parseLong(UserManageAdd.getEmployee()));
		}else {
			login.setEmpId(Long.parseLong("0"));
		}
		
		//HashSet< Role> Roles=new HashSet<Role>();
		//Roles.add(roleRepository.findAll().get(Integer.parseInt(UserManageAdd.getRole())-1));
		//login.setRoles(Roles);
		
	    logindivision.setDivisionId(Long.parseLong(UserManageAdd.getDivision()));
	    logindivision.setEmpId(Long.parseLong(UserManageAdd.getEmployee()));
	    logindivision.setCreatedBy(Userid);
	    logindivision.setCreatedDate(sdf1.format(new Date()));
	    logindivision.setIsActive(1);
		return dao.UserManagerInsert(login,logindivision) ;
		}else {
			throw new Exception();
		}
	}
	
	
	@Override
	public Long UserManagerInsertFromExcel(UserManageAdd UserManageAdd, String Userid) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE UserManagerInsert ");
		if(dao.UserNamePresentCount(UserManageAdd.getUserName())==0) {
		Login login=new Login();
		DivisionEmployee logindivision=new DivisionEmployee();
		login.setUsername(UserManageAdd.getUserName());
		login.setPassword("$2y$12$QTTMcjGKiCVKNvNa242tVu8SPi0SytTAMpT3XRscxNXHHu1nY4Kui");
		login.setPfms("Y");
		login.setDivisionId(Long.parseLong(UserManageAdd.getDivision()));
		login.setFormRoleId(Long.parseLong(UserManageAdd.getRole()));
		login.setCreatedBy(Userid);
		login.setCreatedDate(sdf1.format(new Date()));
		login.setIsActive(1);
		login.setLoginType(UserManageAdd.getLoginType());
		if(UserManageAdd.getEmployee()!=null) {
		login.setEmpId(Long.parseLong(UserManageAdd.getEmployee()));
		}else {
			login.setEmpId(Long.parseLong("0"));
		}
		
		//HashSet< Role> Roles=new HashSet<Role>();
		//Roles.add(roleRepository.findAll().get(Integer.parseInt(UserManageAdd.getRole())-1));
		//login.setRoles(Roles);
		
	    logindivision.setDivisionId(Long.parseLong(UserManageAdd.getDivision()));
	    logindivision.setEmpId(Long.parseLong(UserManageAdd.getEmployee()));
	    logindivision.setCreatedBy(Userid);
	    logindivision.setCreatedDate(sdf1.format(new Date()));
	    logindivision.setIsActive(1);
		return dao.UserManagerInsert(login,logindivision) ;
		}else {
			return 0l;
		}
	}


	@Override
	public Long LoginTypeAddSubmit(PfmsLoginRoleSecurityDto loginrolesecurity,String UserId) throws Exception {
		logger.info(new Date() +"Inside SERVICE LoginTypeAddSubmit ");
		PfmsLoginRoleSecurity loginrole =new PfmsLoginRoleSecurity();
		loginrole.setLoginId(Long.parseLong(loginrolesecurity.getLoginId()));
		loginrole.setRoleId(Long.parseLong(loginrolesecurity.getRoleId()));
		
		Login login = new Login();
		login.setPfms("Y");
		login.setModifiedBy(UserId);
		login.setModifiedDate(sdf1.format(new Date()));
		
		return dao.LoginTypeAddSubmit(loginrole,login);
	}

	@Override
	public Long LoginTypeRevoke(String LoginId,String UserId) throws Exception {
		logger.info(new Date() +"Inside SERVICE LoginTypeRevoke ");
		Login login=new Login();
		login.setModifiedBy(UserId);
		login.setModifiedDate(sdf1.format(new Date()));
		login.setLoginId(Long.parseLong(LoginId));
	
		return dao.LoginTypeRevoke(login);
	}

	@Override
	public List<Object[]> LoginTypeEditData(String LoginId) throws Exception {
		return dao.LoginTypeEditData(LoginId);
	}
	
	@Override
	public Long LoginTypeEditSubmit(PfmsLoginRoleSecurityDto loginrolesecurity,String LoginId,String UserId) throws Exception {
		logger.info(new Date() +"Inside SERVICE LoginTypeEditSubmit ");
		PfmsLoginRoleSecurity loginrole =new PfmsLoginRoleSecurity();
		
		loginrole.setRoleId(Long.parseLong(loginrolesecurity.getRoleId()));
		loginrole.setLoginId(Long.parseLong(LoginId));
		
		Login login=new Login();
		login.setModifiedBy(UserId);
		login.setModifiedDate(sdf1.format(new Date()));

		return dao.LoginTypeEditSubmit(loginrole,login);
	}

	@Override
	public List<Object[]> NotificationList(String EmpId) throws Exception {

		return dao.NotificationList(EmpId);
	}

	@Override
	public List<Object[]> EmployeeListAll() throws Exception {
		
		return dao.EmployeeListAll();
	}

	@Override
	public List<Object[]> Rtmddo() throws Exception {
		
		return dao.Rtmddo();
	}

	@Override
	public long RtmddoInsert(PfmsRtmddoDto dto) throws Exception {
		try {
			dao.RtmddoUpdate(dto.getType());
		}catch (Exception e) {
			logger.error(new Date() +"Inside SERVICE RtmddoInsert "+e);
		}
		PfmsRtmddo rtmddo=new PfmsRtmddo();
		rtmddo.setEmpId(Long.parseLong(dto.getEmpId()));
		rtmddo.setValidFrom(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getValidFrom()).getTime()));
		rtmddo.setValidTo(new java.sql.Date(fc.getRegularDateFormat().parse(dto.getValidTo()).getTime()));
		rtmddo.setType(dto.getType());
		rtmddo.setCreatedBy(dto.getCreatedBy());
		rtmddo.setCreatedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
		rtmddo.setInitiationId(0l);
		rtmddo.setIsActive(1);
		rtmddo.setLabCode(dto.getLabCode()); 
		return dao.RtmddoInsert(rtmddo);
	}
	
	@Override
    public List<Object[]> GetExpertList() throws Exception {
        return (List<Object[]>)this.dao.GetExpertList();
    }
    
    
    @Override
    public String GenExpertNo() throws Exception {
    	logger.info(new Date() +"Inside SERVICE GenExpertNo ");
    	long count=dao.GetExpertsCount();
    	String expno="N"+String.format("%04d", count);
		return expno;
    }
    
    @Override
    public List<Object[]> GetDesignation() throws Exception {
        return (List<Object[]>)this.dao.GetDesignation();
    }
    
    @Override
    public int[] checkAbility( String expertNo,  String extensionNo) throws Exception {
        final int[] result = { this.dao.abilityOfexpertNo(expertNo), this.dao.abilityOfextensionNo(extensionNo) };
        return result;
    }
    
    @Override
    public int checkAbility2( String extensionNo, final String ExpertId) throws Exception {
        final int result = this.dao.checkAbility2(extensionNo, ExpertId);
        return result;
    }
    
    @Override
    public Long addExpert( Expert newExpert) throws Exception {
        newExpert.setCreatedDate(this.fc.getSqlDateAndTimeFormat().format(new Date()));
        newExpert.setIsActive(1);
        return this.dao.addExpert(newExpert);
    }
    
    @Override
    public Long ExpertRevoke(final String expertId, final String userId) throws Exception {
    	logger.info(new Date() +"Inside SERVICE ExpertRevoke ");
    	final Expert expert = new Expert();
        expert.setExpertId(Long.parseLong(expertId));
        expert.setModifiedBy(userId);
        expert.setModifiedDate(this.sdf1.format(new Date()));
        return this.dao.ExpertRevoke(expert);
    }
    
    @Override
    public List<Object[]> getEditDetails( String expertId) throws Exception {
        return (List<Object[]>)this.dao.getEditDetails(expertId);
    }
    
    @Override
    public Long editExpert( Expert newExpert) throws Exception {
        newExpert.setModifiedDate(this.sdf1.format(new Date()));
        return this.dao.editExpert(newExpert);
    }
    
    @Override
	public List<Object[]> AuditStampingList(String loginid,String Fromdateparam,String Todateparam)  throws Exception {
		logger.info(new Date() +"Inside SERVICE AuditStampingList ");
		 LocalDate Fromdate = null;
		 LocalDate Todate = null;
		 
		 if(Fromdateparam == null || Todateparam == null) 
		 { 
			 Todate = LocalDate.now();
			 Fromdate= Todate.minusMonths(1); 
		 }
		  
		 else { 
			 
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		 Fromdate = LocalDate.parse(Fromdateparam,formatter);
		 Todate = LocalDate.parse(Todateparam,formatter);
		 
		 }
		return dao.AuditStampingList(loginid,Fromdate,Todate);
		
	}
	

	@Override
	public List<Object[]> UsernameList() throws Exception {
		return dao.UsernameList();
	}
	
	@Override
	public List<Object[]> UserManagerList( ) throws Exception {
		
			  return dao.UserManagerList();
	}

	
	@Override
	public int UserNamePresentCount(String UserName) throws Exception {
	
		return dao.UserNamePresentCount(UserName);
	}
	@Override
	public List<Object[]> DivisionList() throws Exception {
	
		return dao.DivisionList();
	}
	
	@Override
	public Login UserManagerEditData(String LoginId) throws Exception {
	
		return dao.UserManagerEditData(Long.parseLong(LoginId));
	}
	
	@Override
	public int UserManagerDelete(String LoginId, String Userid) throws Exception {
		logger.info(new Date() +"Inside SERVICE UserManagerDelete ");
      Login login=new Login();
		login.setLoginId(Long.parseLong(LoginId));
		login.setModifiedBy(Userid);
		login.setModifiedDate(sdf1.format(new Date()));
		
		return dao.UserManagerDelete(login);
	}
	
	@Override
	public int UserManagerUpdate(UserManageAdd UserManageAdd,String Userid) throws Exception {
		logger.info(new Date() +"Inside SERVICE UserManagerUpdate ");
		Login login=new Login();
		
		login.setDivisionId(Long.parseLong(UserManageAdd.getDivision()));
		login.setFormRoleId(Long.parseLong(UserManageAdd.getRole()));
		login.setLoginId(Long.parseLong(UserManageAdd.getLoginId()));
		login.setLoginType(UserManageAdd.getLoginType());
		login.setPfms(UserManageAdd.getPfms());
		if(UserManageAdd.getEmployee()!=null) {
		login.setEmpId(Long.parseLong(UserManageAdd.getEmployee()));
		}else {
			login.setEmpId(Long.parseLong("0"));
		}
		
		login.setModifiedBy(Userid);
		login.setModifiedDate(sdf1.format(new Date()));
//           if(UserManageAdd.getPfms().equalsIgnoreCase("Y")) {
//			
//	     	Object flag=dao.checkUser(login.getLoginId());
//	     	if(Integer.parseInt(flag.toString())>0){
//	     		dao.updatePfmsLoginRole(Long.parseLong(UserManageAdd.getRole()),Long.parseLong(UserManageAdd.getLoginId()));  	     		
//	     	}else {
//	     		PfmsLoginRole pfmsrole= new PfmsLoginRole();
//				pfmsrole.setLoginId(login.getLoginId());
//				pfmsrole.setRoleId(Long.parseLong(UserManageAdd.getRole()));
//				dao.pfmsRoleInsert(pfmsrole);
//	     	}
//		}
		
		return dao.UserManagerUpdate(login);
	}

	
	@Override
	public List<Object[]> presentEmpList()throws Exception
	{
	  return dao.presentEmpList();
	}
	
	@Override
	public List<Object[]> DesignationList()throws Exception
	{
		return dao.DesignationList();
	}
	
	@Override
	public Object[] DesignationData(String desigid)throws Exception
	{
		return dao.DesignationData(desigid);
	}
	
	@Override
	public long DesignationEditSubmit(EmployeeDesigDto dto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE DesignationEditSubmit ");
		long ret=dao.DesignationEditSubmit(dto);
		if(!dto.getDesigSr().equalsIgnoreCase(dto.getOldDesigSr()))
		{		
			Long desigid=Long.parseLong(dto.getDesigId());
			Long SeniorityNumber=Long.parseLong(dto.getDesigSr());
			int result= 0;
			Long newSeniorityNumberL=SeniorityNumber;
			List<Object[]> EmpSenHaveUpdate=dao.DesigupdateAndGetList(desigid,dto.getDesigSr());
			List<Object[]> result1=EmpSenHaveUpdate.stream().filter(srno-> Long.parseLong(srno[0].toString())>=SeniorityNumber && Long.parseLong(srno[1].toString())!=desigid  ).collect(Collectors.toList());
			
			for(Object[] data:result1) { 
			  Long empIdL=Long.parseLong(data[1].toString()); 
			  result= dao.updateAllDesigSeniority(empIdL, ++newSeniorityNumberL);
			}
			
			
			
			
		}	
		return ret;
	}
	
	@Override
	public long DesignationAddSubmit(EmployeeDesigDto dto) throws Exception 
	{
		logger.info(new Date() +"Inside SERVICE DesignationAddSubmit ");
		EmployeeDesig model=new EmployeeDesig();
		model.setDesigCode(dto.getDesigCode());
		model.setDesignation(dto.getDesignation());
		model.setDesigLimit(Long.parseLong(dto.getDesigLimit()));
		model.setDesigCadre(dto.getDesigCadre());
		
		return dao.DesignationAddSubmit(model);
	}
	
	
	@Override
	public Object[] DesignationAddCheck(String desigcode,String designation) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE DesignationAddSubmit ");
		Object[] returnobj=new Object[2];
		returnobj[0]=dao.DesignationCodeCheck(desigcode)[0].toString();
		returnobj[1]=dao.DesignationCheck(designation)[0].toString();
		return returnobj;
	}
	

	@Override
	public Object[] DesignationEditCheck(String desigcode,String designation,String desigid) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE DesignationEditCheck ");
		Object[] returnobj=new Object[2];
		returnobj[0]=dao.DesignationCodeEditCheck(desigcode,desigid)[0].toString();
		returnobj[1]=dao.DesignationEditCheck(designation,desigid)[0].toString();
		return returnobj;
	}
	
	
	 @Override
		public List<Object[]> DivisionMasterList(String LabCode) throws Exception {
		
			return dao.DivisionMasterList(LabCode);
		}

	        
		 @Override
		public List<Object[]> DivisionGroupList() throws Exception{
			
			
			return dao.DivisionGroupList();
		}
		

		@Override
		public List<Object[]> DivisionHeadList() throws Exception {
			
			return dao.DivisionHeadList();
		}
		
		@Override
		public List<Object[]> DivisionMasterEditData(String DivisionId) throws Exception {
			 
			// List<Object[]> kl=dao.DivisionMasterEditData(DivisionId);

			return dao.DivisionMasterEditData(DivisionId);
		}
		
		
		@Override
		public Object[] DivisionAddCheck(String dCode,String dName) throws Exception
		{
			return dao.DivisionAddCheck(dCode,dName).get(0);
		}
		
		@Override
		public long DivisionAddSubmit(DivisionMaster model) throws Exception 
		{
			logger.info(new Date() +"Inside SERVICE DivisionAddSubmit ");
			
			model.setIsActive(1);
			model.setCreatedDate(sdf1.format(new Date()));
			return dao.DivisionAddSubmit(model);
		}
		
		
		
		@Override
		public int DivisionMasterUpdate(DivisionMaster add, String Userid) throws Exception {
			
			logger.info(new Date() +"Inside SERVICE DivisionMasterUpdate ");
			DivisionMaster divisionmaster = new DivisionMaster();
			
			divisionmaster.setDivisionCode(add.getDivisionCode());
			divisionmaster.setDivisionName(add.getDivisionName());
			divisionmaster.setDivisionHeadId(add.getDivisionHeadId());
			divisionmaster.setGroupId(add.getGroupId());
			divisionmaster.setModifiedBy(Userid);
			divisionmaster.setModifiedDate(sdf1.format(new Date()));
			divisionmaster.setDivisionId(add.getDivisionId());
	        divisionmaster.setIsActive(add.getIsActive());
	        divisionmaster.setDivisionShortName(add.getDivisionShortName());	//srikant
			
			return dao.DivisionMasterUpdate(divisionmaster);
		}

		@Override
		public List<Object[]> LoginTypeRoles() throws Exception {

			return dao.LoginTypeRoles();
		}

		@Override
		public List<Object[]> FormDetailsList(String LoginType,String ModuleId) throws Exception {
			logger.info(new Date() +"Inside SERVICE FormDetailsList ");
			String logintype="A";
			if(LoginType!=null) {
				logintype=LoginType;
			}
			String moduleid="A";
			if(ModuleId!=null) {
				moduleid=ModuleId;
			}
			
			
			return dao.FormDetailsList(logintype,moduleid);
		}

		@Override
		public List<Object[]> FormModulesList() throws Exception {

			return dao.FormModulesList();
		}

		@Override
		public Long FormRoleActive(String formroleaccessid) throws Exception {
			
			logger.info(new Date() +"Inside SERVICE FormRoleActive ");			
			List<BigInteger> FormRoleActiveList=dao.FormRoleActiveList(formroleaccessid);
			
			Long Value=null;
			
			for(int i=0; i<FormRoleActiveList.size();i++ ) {
				 Value=FormRoleActiveList.get(i).longValue();	
			}

			long ret=dao.FormRoleActive(formroleaccessid,Value);
				
			return ret;
		}
	
		@Override
		public List<Object[]> AllLabList() throws Exception {
			
			return dao.AllLabList();
		}

		@Override
		public Long LabHqChange(String FormRoleAccessid, String Value) throws Exception {
			
			return dao.LabHqChange(FormRoleAccessid, Value);
		}

		@Override
		public int updateformroleaccess(String formroleaccessid,String detailsid,String isactive,String logintype, String UserId)throws Exception{
			
			if(isactive!=null && isactive.equals("0")){
				isactive="1";
			}else {
				isactive="0";
			}
			int result = dao.checkavaibility(logintype,detailsid);
			
			if(result == 0) {
				PfmsFormRoleAccess formrole = new PfmsFormRoleAccess();
				formrole.setLoginType(logintype);
				formrole.setFormDetailId(Long.parseLong(detailsid));
				formrole.setLabHQ("B");
				formrole.setIsActive(1);
				formrole.setCreatedBy(UserId);
				formrole.setCreatedDate(sdf1.format(new Date()));	
				Long value=dao.insertformroleaccess(formrole);
				return value.intValue();
			}else {
			
				return dao.updateformroleaccess(formroleaccessid,isactive,UserId);
			}
			
		} 
		@Override
		public int resetPassword(String lid,String UserId, String labcode) throws Exception {
			String finalLabCode = labcode.substring(0,1).toUpperCase() + labcode.substring(1).toLowerCase();
			String password= finalLabCode+"@123";
			String newPasword=encoder.encode(password);
			System.out.println(newPasword);
			return dao.resetPassword(lid,UserId,newPasword,sdf1.format(new Date()));
		}
	
		@Override
		public int insertEmployeeData() throws Exception {
			long startTime = System.nanoTime();
			List<Object[]>EmployeeList=new ArrayList<>();
			List<PfmsStatistics>pfmsStatistics= new ArrayList<>();
			List<Object[]>pfmsStatisticsTableData=new ArrayList<>();
			pfmsStatisticsTableData= dao.getpfmsStatiscticsTableData();
			LocalDate startDate=LocalDate.now().minusDays(1);
			String date = "";
				if(pfmsStatisticsTableData.size()>0) {
				date=pfmsStatisticsTableData.stream().max((arr1,arr2) ->
				{
					Date date1=(Date)arr1[4];
					Date date2=(Date)arr2[4];
					return date1.compareTo(date2);
				}). orElse(null)[4].toString();
				// here startdate shouldbe the next day after max date
				 startDate=LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1);
				
				}
				else {
				date = dao.firstDateOfAudit();
					// here the startdate is the date from the date we have information
				 startDate=LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
				
				}
				//currdate shoulde be the yesterday
				LocalDate curDate=LocalDate.now();
				
				if(!startDate.equals(curDate)) {
				for(LocalDate d = startDate;!d.isEqual(curDate);d = d.plusDays(1)) {
				EmployeeList=dao.getAllEmployeesOfDate(d.toString()); //checking if that day has anylogin or not
				if(EmployeeList.size()>0){ 
				for(Object[]obj:EmployeeList){ 
				  Object[]ListOfWorkCounts=dao.ListOfWorkCounts(obj[1].toString(),d.toString());//calling procedure for counts and storing in object array
				  PfmsStatistics ps= new PfmsStatistics();
				  ps.setEmpId(Long.parseLong(obj[0].toString()));
				  ps.setUserName(obj[1].toString());
				  ps.setActionCount(Long.parseLong(ListOfWorkCounts[0].toString()));
				  ps.setActionAssignedCount(Long.parseLong(ListOfWorkCounts[1].toString()));
				  ps.setMilestoneCount(Long.parseLong(ListOfWorkCounts[2].toString()));
				  ps.setMeetingScheduled(Long.parseLong(ListOfWorkCounts[3].toString()));
				  ps.setLogDate(java.sql.Date.valueOf(d));
				  ps.setLogInCount(Long.parseLong(obj[2].toString())); pfmsStatistics.add(ps);
				  } 
				}
				  } 
				}	
				  int i=dao.DataInsetrtIntoPfmsStatistics(pfmsStatistics);
				   // Record the end time after calling method1
			        long endTime = System.nanoTime();
			        // Calculate the time difference
			        long elapsedTime = endTime - startTime;
			        System.out.println("Time taken: " + elapsedTime + " nanoseconds");
			        return i;
		}
		
		@Override
		public List<Object[]> StatsEmployeeList(String logintype, String division, String labCode) throws Exception {
			return dao.StatsEmployeeList(logintype,division,labCode);
		}
		@Override
		public List<Object[]> getEmployeeWiseCount(long employeeId, String fromDate, String toDate) throws Exception {
			return dao.getEmployeeWiseCount(employeeId,fromDate,toDate);
		}

		@Override
		public List<Object[]> initiationApprovalAuthority(String labcode) throws Exception {
			
			return dao.initiationApprovalAuthority(labcode);
		}

		@Override
		public PfmsRtmddo getApprovalAuthById(String RtmddoId) throws Exception {
			
			return dao.getApprovalAuthById(RtmddoId);
		}

		@Override
		public int approvalAuthRevoke(String RtmddoId) throws Exception {
			
			return dao.approvalAuthRevoke(RtmddoId);
		}
		@Override
		public List<Object[]> MailConfigurationList()throws Exception{
			return  dao.MailConfigurationList();
		}
		@Override
		public long DeleteMailConfiguration(long MailConfigurationId, String ModifiedBy) throws Exception {
			// TODO Auto-generated method stub
			return dao.DeleteMailConfiguration(MailConfigurationId,ModifiedBy);
		}
		
		@Override
		public long AddMailConfiguration(String userName, String password, String hostType, String createdBy,String Host,String port)throws Exception{
			
			long finalResult = 0;
			try {
	          System.out.println("Encrypted Password By AesAlgorithm: " + rea.encryptByAesAlg(password));
		    } catch (Exception e) {
			    e.printStackTrace();
			    System.out.println("Password:errror encrpting ");
			}
			
			MailConfiguration mailConfigAdd =  new MailConfiguration();
			mailConfigAdd.setTypeOfHost(hostType);
	        mailConfigAdd.setUsername(userName);
	        mailConfigAdd.setCreatedBy(createdBy);
		    mailConfigAdd.setCreatedDate(sdf1.format(new Date()));
		    mailConfigAdd.setIsActive(1);
		
	        mailConfigAdd.setHost(Host);
		    mailConfigAdd.setPort(port);
			
			
			////////////////////////////////////////////////////////
	//Modern authentication systems typically use one-way hash functions like bcrypt which cannot be decryptes
	// (unlike bcrypt, which is designed to be irreversible)that means You can't really "decrypt" a bcrypt-hashed password.
	//If you need to support both encryption and decryption of passwords for mail,you  want to use a reversible encryption algorithm
		    mailConfigAdd.setPassword(rea.encryptByAesAlg(password));
		    long count=dao.getTypeOfHostCount(hostType);
		    if(count==0) {
		    finalResult = dao.AddMailConfiguration(mailConfigAdd);
		    }else {
		    	finalResult=-1;
		    }
			return finalResult;
			
		}
		

		@Override
		public Object[] MailConfigurationEditList(long MailConfigurationId)throws Exception{
			return dao.MailConfigurationEditList(MailConfigurationId);
		}
		@Override
		public long UpdateMailConfiguration(long MailConfigurationId,String userName,String hostType, String modifiedBy,String Host,String Port,String Password)throws Exception{
		    logger.info(new Date() + " ServiceImpl  UpdateMailConfiguration");
		    long finalResult = 0;
			try {
				String pass=rea.encryptByAesAlg(Password);
				finalResult = dao.UpdateMailConfiguration(MailConfigurationId,userName,hostType,modifiedBy,Host,Port,pass);
			  }catch (Exception e) {
			    	 logger.error(new Date() +" Login Problem Occures When MailConfiguration.htm was clicked ", e);
			    	 finalResult = 0;
			     }
			return finalResult;
		}

		@Override
		public List<Object[]> lastUpdate() {
			// TODO Auto-generated method stub
			return dao.lastUpdate();
		}

		@Override
		public Object weeklyupdate(int empid, String username, String dateofupdate, String procurement,
				String actionpoints, String riskdetails, String meeting, String mile,int projectid) {
			// TODO Auto-generated method stub
			return dao.weeklyupdate( empid, username, dateofupdate, procurement,   
					 actionpoints,  riskdetails,  meeting,  mile, projectid);
		}
		
		@Override
		public List<Object[]> ProjectListPD(String empId) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProjectListPD(empId);
		}

		@Override
		public List<Object[]> ProjectListIC(String empId) throws Exception {
			// TODO Auto-generated method stub
			return dao.ProjectListIC(empId);
		}
		@Override
		public List<Object[]> hasroleAccess(String url, String logintype) throws Exception {
			// TODO Auto-generated method stub
			return dao.hasroleAccess(url,logintype);
		}
		@Override
		public List<Object[]> getFormUrlList(String loginType,String formdetaild)  {
			
			return dao.getFormUrlList(loginType,formdetaild);
		}
		@Override
		public List<Object[]> getAllUrlList(String loginType)  {
			return dao.getAllUrlList(loginType);
		}
		
		@Override
		public List<Object[]> AuditPatchesList() throws Exception
		{
			return dao.getAuditPatchesList();
		}
		@Override
		public int AuditPatchAddSubmit(AuditPatches dto) throws Exception 
		{
			logger.info(new Date() +"Inside SERVICE DesignationAddSubmit ");
			  LocalDateTime currentDateTime = LocalDateTime.now();
		      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		      String formattedDateTime = currentDateTime.format(formatter);
			  dto.setModifiedDate(formattedDateTime);

			
			return dao.auditpatchAddSubmit(dto);
		}
		@Override
	    public AuditPatches getAuditPatchById(Long attachId) {
	        return dao.getAuditPatchById(attachId);
	    }

		@Override
		public List<Object[]> getFormId(String requestUrlSegment)  {
			return dao.getFormId(requestUrlSegment);
		}
		
		@Override
		public List<Object[]> checkDivisionMasterId(String divisionId) throws Exception {
			
			return dao.checkDivisionMasterId(divisionId);
		}
}
