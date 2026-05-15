package com.vts.pfms.header.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vts.pfms.header.dao.HeaderDao;
import com.vts.pfms.header.model.ProjectDashBoardFavourite;
import com.vts.pfms.header.model.ProjectDashBoardFavouriteProjetcts;
import com.vts.pfms.login.LoginPasswordHistory;
@Service
public class HeaderServiceImpl implements HeaderService {
	@Autowired
	HeaderDao dao;
	 
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	private static final Logger logger=LogManager.getLogger(HeaderServiceImpl.class);
	
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public List<Object[]> FormModuleList(String LoginType,String LabCode)throws Exception {
		
		return dao.FormModuleList(LoginType,LabCode);
	}

	@Override
	public List<Object[]> loginTypeList(String LoginType) throws Exception {
		logger.info(new Date() +"Inside SERVICE loginTypeList ");
	List<Object[]>loginTypeList=dao.loginTypeList();
	List<Object[]>loginTypeListnew=new ArrayList<Object[]>();
	
	if(LoginType.equalsIgnoreCase("D")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("G")||obj[0].toString().equalsIgnoreCase("T")||obj[0].toString().equalsIgnoreCase("D")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	if(LoginType.equalsIgnoreCase("G")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("D")||obj[0].toString().equalsIgnoreCase("T")||obj[0].toString().equalsIgnoreCase("G")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	
	if(LoginType.equalsIgnoreCase("P")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("D")||obj[0].toString().equalsIgnoreCase("P")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	if(LoginType.equalsIgnoreCase("B")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("D")||obj[0].toString().equalsIgnoreCase("B")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}	
	
	
	if(LoginType.equalsIgnoreCase("O")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("D")||obj[0].toString().equalsIgnoreCase("O")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	
	
	if(LoginType.equalsIgnoreCase("S")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("D")||obj[0].toString().equalsIgnoreCase("G")||obj[0].toString().equalsIgnoreCase("T")||obj[0].toString().equalsIgnoreCase("S")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	

	
	
	if(LoginType.equalsIgnoreCase("M")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("M")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	if(LoginType.equalsIgnoreCase("R")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("R")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}	
	
	
		
	if(LoginType.equalsIgnoreCase("U")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("U")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	if(LoginType.equalsIgnoreCase("P")) {
		for(Object[] obj:loginTypeList) {
			if(obj[0].toString().equalsIgnoreCase("P")||obj[0].toString().equalsIgnoreCase("D")) {
				loginTypeListnew.add(obj);
			}
		}
	
	}
	
	
	
		if(LoginType.equalsIgnoreCase("A")) 
		{
			loginTypeListnew=loginTypeList;
		}
		
		return loginTypeListnew;
	}

	@Override
	public List<Object[]> DashboardDemandCount() throws Exception {
		
		return dao.DashboardDemandCount();
	}

	@Override
	public List<Object[]> NotificationList(String Empid) throws Exception {
	
		return dao.NotificationList(Empid);
	}

	@Override
	public int NotificationUpdate(String NotificationId) throws Exception {
		
		return dao.NotificationUpdate(NotificationId);
	}

	@Override
	public List<Object[]> NotificationAllList(String Empid) throws Exception {
		
		return dao.NotificationAllList(Empid);
	}

	@Override
	public List<Object[]> EmployeeDetailes(String LoginId) throws Exception {
	
		return dao.EmployeeDetailes(LoginId);
	}

	@Override
	public String DivisionName(String DivisionId) throws Exception {
	
		return dao.DivisionName(DivisionId);
	}
	
	@Override
//	@Cacheable(value = "todaySchedulesList", key = "#EmpId")
	public List<Object[]> TodaySchedulesList(String EmpId, String TodayDate) throws Exception {


		return dao.TodaySchedulesList(EmpId,TodayDate);
	}

	@Override
	public List<Object[]> TodayActionList(String EmpId) throws Exception {
		
		return dao.TodayActionList(EmpId);
	}

	@Override
	public int PasswordChange(String OldPassword, String NewPassword, String UserId, Long loginId, Long EmpId) throws Exception {
		int count = 0;
		try {
			String actualoldpassword=dao.OldPassword(UserId);

			if(encoder.matches(OldPassword, actualoldpassword)) {

				String oldpassword=encoder.encode(OldPassword);
				String newpassword=encoder.encode(NewPassword);

				count = dao.PasswordChange(oldpassword, newpassword, UserId, sdf1.format(new Date()));
				
				if(count>0) {
					LoginPasswordHistory passHis = new LoginPasswordHistory();
					passHis.setLoginId(loginId);
					passHis.setPassword(newpassword);
					passHis.setActionBy(EmpId);
					passHis.setActionDate(sdf1.format(new Date()));
					passHis.setActionType("CP");

					dao.addLoginPasswordHistory(passHis);
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public String FormRoleName(String LoginType) throws Exception {

		return dao.FormRoleName(LoginType);
	}

	@Override
	public List<Object[]> GanttChartList(String ProjectId) throws Exception {
		
		return dao.GanttChartList(ProjectId);
	}

	@Override
	public List<Object[]> ProjectList() throws Exception {
		
		return dao.ProjectList();
	}
	
	@Override
	public List<Object[]> ProjectDetails(String ProjectId) throws Exception {
		
		return dao.ProjectDetails(ProjectId);
	}
	

	@Override
	public Object[] LabDetails(String labcode) throws Exception {
		
		return dao.LabDetails(labcode);
	}

	@Override
	public List<Object[]> FullGanttChartList(String ProjectId) throws Exception 
	{	
		return dao.FullGanttChartList(ProjectId);
	}

	@Override
	public List<Object[]> HeaderSchedulesList(String Logintype,String FormModuleId,String LabCode) throws Exception {
		
		return dao.HeaderSchedulesList(Logintype,FormModuleId,LabCode);
	}

	@Override
	public List<Object[]> ProjectIntiationList(String EmpId, String LoginType) throws Exception {
		
		return dao.ProjectIntiationList(EmpId,LoginType);
	}

	@Override
	public List<Object[]> MyTaskList(String EmpId) throws Exception {
		
		return dao.MyTaskList(EmpId);
	}
	
	@Override
	public List<Object[]> ApprovalList(String EmpId,String LoginType) throws Exception {
		
		return dao.ApprovalList(EmpId,LoginType);
	}
	
	@Override
	public List<Object[]> MyTaskDetails(String EmpId) throws Exception {
		
		return dao.MyTaskDetails(EmpId);
	}
	
	@Override
	public List<Object[]> DashboardActionPdc(String EmpId,String Logintype) throws Exception {
		
		return dao.DashboardActionPdc(EmpId,Logintype);
	}
	
	@Override
	public List<Object[]> MilestoneActivityList(String ProjectId) throws Exception {
		return dao.MilestoneActivityList(ProjectId);
	}
	
	@Override
	public List<Object[]> MilestoneActivityLevel(String MilestoneActivityId,String LevelId) throws Exception {
		return dao.MilestoneActivityLevel(MilestoneActivityId, LevelId);
	}

	@Override
	public List<Object[]> QuickLinksList(String LoginType) throws Exception {

		return dao.QuickLinksList( LoginType);
	}

	@Override
	public String getLabCode(String Empid) throws Exception {
		
		return dao.getLabCode( Empid);
	}
	
	@Override
	public List<Object[]>  LabMasterList(String Clusterid) throws Exception {
		
		return dao.LabMasterList(Clusterid);
	}
	@Override
	public List<Object[]> getNotificationId(String Empid) throws Exception {
	
		return dao.getNotificationId(Empid);
	}
	
	@Override
	public List<Object[]> getFormNameByName(String Search)  throws Exception{
		// TODO Auto-generated method stub
		return dao.getFormNameByName(Search);
	}

	@Override
	public Boolean getRoleAccess(String formModuleId, String logintype) throws Exception {
		return dao.getRoleAccess( formModuleId,  logintype);
	}
	
	@Override
	public long addDashBoardFav(String projects, String favName, String empId,String UserId,String LoginType) throws Exception {
		ProjectDashBoardFavourite pd = new ProjectDashBoardFavourite();
		pd.setEmpId(Long.parseLong(empId));
		pd.setDashBoardName(favName);
		pd.setIsActive(1);
		pd.setLoginType(LoginType);
		List<ProjectDashBoardFavouriteProjetcts> list = new ArrayList<>();
		if(projects!=null && projects.length()>0) {
		String[] project = projects.split(",");	
		
		for(int i=0;i<project.length;i++) {
			ProjectDashBoardFavouriteProjetcts pf = new ProjectDashBoardFavouriteProjetcts();
			pf.setProjectdbfav(pd);
			pf.setProjectId(Long.parseLong(project[i]));
			pf.setCreatedBy(UserId);
			pf.setCreatedDate(sdf1.format(new Date()));
			list.add(pf);
		}
		
		}
		pd.setProjects(list);
		
		
		return dao.addDashBoardFav(pd);
	}
	
	@Override
	public ProjectDashBoardFavourite findProjectDashBoardFavourite(long DashBoardId) throws Exception {
		return dao.findProjectDashBoardFavourite(DashBoardId);
	}
	
		@Override
		public List<Object[]> getDashBoardId(Long empId, String LoginType) throws Exception {
			List<Object[]> getDashBoardId =	dao.getDashBoardId(empId,LoginType);
			
			if(getDashBoardId!=null && getDashBoardId.size()>0) {
				getDashBoardId = getDashBoardId.stream().filter(e->e[4].equals(true)).collect(Collectors.toList());
			}
			return getDashBoardId;
			
		}
		
		@Override
		public Object[] projecthealthtotalDashBoardwise(String dashBoardId, String labCode) throws Exception {
		
			return dao.projecthealthtotalDashBoardwise(dashBoardId,labCode);
		}
		
		@Override
		public long isActiveDashBoard(String empId, String loginType) throws Exception {
			
			return dao.isActiveDashBoard(empId,loginType);
		}
		
		@Override
		public List<Object[]> DashboardFinanceProjectWise(String dashBoardId, String labCode) throws Exception {
			List<Object[]> DashboardFinance = dao.DashboardFinanceProjectWise(dashBoardId,labCode);
			
			BigDecimal thousandcrore = new BigDecimal("10000000000");
			for(Object[] OutGo : DashboardFinance)
			{ 
				OutGo[3] = Double.parseDouble(OutGo[3].toString())!=0 ?  new BigDecimal(OutGo[3].toString()).divide(thousandcrore).setScale(2, BigDecimal.ROUND_HALF_EVEN ) : new BigDecimal(OutGo[3].toString()) ;
				OutGo[4] = Double.parseDouble(OutGo[4].toString())!=0 ?  new BigDecimal(OutGo[4].toString()).divide(thousandcrore).setScale(2, BigDecimal.ROUND_HALF_EVEN) : new BigDecimal(OutGo[4].toString()) ;
				OutGo[5] = Double.parseDouble(OutGo[5].toString())!=0 ?  new BigDecimal(OutGo[5].toString()).divide(thousandcrore).setScale(2, BigDecimal.ROUND_HALF_EVEN) : new BigDecimal(OutGo[5].toString()) ;
			}
			return DashboardFinance;
		}
		
		@Override
		public List<Object[]> getDashBoards(Long empId, String LoginType) throws Exception {

			return dao.getDashBoardId(empId,LoginType);
		}
		
		@Override
		public long updateDashBoard(String dashboardId,String projects,String UserId) throws Exception {
			dao.updateDashBoard(dashboardId);
			ProjectDashBoardFavourite pd = dao.findProjectDashBoardFavourite(Long.parseLong(dashboardId));
			long count =0l;
			if(projects!=null && projects.length()>0) {
				String [] project= projects.split(",");
				for(int i=0;i<project.length;i++) {
					ProjectDashBoardFavouriteProjetcts pf = new ProjectDashBoardFavouriteProjetcts();
					pf.setProjectdbfav(pd);
					pf.setProjectId(Long.parseLong(project[i]));
					pf.setCreatedBy(UserId);
					pf.setCreatedDate(sdf1.format(new Date()));
				
					count=dao.addProjectDashBoardFavouriteProjetcts(pf);
				}
			}
			
			return count;
			
			
		}
		
		@Override
		public List<Object[]> getProjectsBasedOnDashBoard(String dashBoardId) throws Exception {
			return dao.getProjectsBasedOnDashBoard(dashBoardId);
		}

		@Override
		public long PasswordChangeHystoryCount(Long loginId) throws Exception {
		
			return dao.PasswordChangeHystoryCount(loginId);
		}
}
