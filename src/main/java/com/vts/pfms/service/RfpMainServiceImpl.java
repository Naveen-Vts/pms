package com.vts.pfms.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vts.pfms.dao.RfpMainDao;
import com.vts.pfms.login.CCMView;
import com.vts.pfms.login.PFMSCCMData;
import com.vts.pfms.login.ProjectHoa;
import com.vts.pfms.model.FinanceChanges;
import com.vts.pfms.model.IbasLabMaster;
import com.vts.pfms.model.LabMaster;
import com.vts.pfms.model.LoginStamping;
import com.vts.pfms.model.Notice;
import com.vts.pfms.model.PfmsCommitteSmsTracking;
import com.vts.pfms.model.PfmsCommitteSmsTrackingInsights;
import com.vts.pfms.model.PfmsSmsTracking;
import com.vts.pfms.model.PfmsSmsTrackingInsights;
import com.vts.pfms.model.ProjectHoaChanges;
import com.vts.pfms.project.model.ProjectHealth;
@Service
public class RfpMainServiceImpl implements RfpMainService {
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	RfpMainDao dao;
	
			private static final Logger logger=LogManager.getLogger(RfpMainServiceImpl.class);
	
	@Override
	public List<Object[]> DashBoardFormUrlList(String FormModuleId, String loginId) throws Exception {
		logger.info(new Date() +"Inside SERVICE DashBoardFormUrlList ");
		int FormModuleIdinput=Integer.parseInt(FormModuleId);
		int LoginIdinput=Integer.parseInt(loginId);
		List<Object[]> DashBoardFormUrlList=dao.DashBoardFormUrlList(FormModuleIdinput, LoginIdinput);
		
		return DashBoardFormUrlList;
	}


	@Override
	public List<Object[]> UserManagerList() throws Exception {
	
		return dao.UserManagerList();
	}


	


	@Override
	public int LoginStampingUpdate(String Logid ,String LogoutType) throws Exception {
		logger.info(new Date() +"Inside SERVICE LoginStampingUpdate ");
		LoginStamping stamping=new LoginStamping();
        stamping.setAuditStampingId(dao.LastLoginStampingId(Logid));
       
        stamping.setLogOutType(LogoutType);
        stamping.setLogOutDateTime(sdf1.format(new Date()));
		
		
		return dao.LoginStampingUpdate(stamping);
	}


	@Override
	public LabMaster LabDetailes() throws Exception {
	
		return dao.LabDetailes();
	}
	
	@Override
	public List<Object[]> LabDetails() throws Exception {
	
		return dao.LabDetails();
	}
 

	@Override
	public String DesgId(String Empid) throws Exception {
		
		return dao.DesgId(Empid);
	}
	@Override
	public List<Object[]> getIndividualNoticeList(String userId)throws Exception{
		
		return dao.getIndividualNoticeList(userId);
		
	}
	@Override
	public Long addNotice(Notice notice)throws Exception{
		
		return dao.addNotice(notice);
	}
	
	@Override

	public List<Object[]> AllActionsCount(String logintype, String empid,String LoginId,String LabCode) throws Exception 
	{		
		logger.info(new Date() +"Inside SERVICE AllActionsCount ");
		List<Object[]> al = new ArrayList<Object[]>();
		List<Object[]> ProjectList=dao.ProjectEmployeeList(empid,logintype,LabCode);
   
        if(ProjectList!=null) {
		for(Object[] obj : ProjectList) {
			
			al.addAll(dao.AllActionsCount(empid,obj[0].toString()));
		}
        }
		return al;
	}
	
	@Override
	public List<Object[]> GetNotice(String LabCode)throws Exception{
		
		return dao.GetNotice(LabCode);
		
	}

	
	@Override
	public List<Object[]> getAllNotice()throws Exception{
		
		return dao.getAllNotice();
	}
	
	@Override
	public int  GetNoticeEligibility(String empId)throws Exception{
		logger.info(new Date() +"Inside SERVICE GetNoticeEligibility ");
		int count =0; 
		List<Object> counts=(List<Object>) dao.GetNoticeEligibility(empId);
		for(Object data:counts) {
			count += Integer.parseInt(data.toString());
		}
		
		return count ;
	}
	
	@Override
    public List<Object> SelfActionsList(String empId)throws Exception 
	{
		return dao.SelfActionsList(empId);
	}
	
	@Override
	public List<Object[]> getIndividualNoticeList(String empId, String fdate, String tdate)throws Exception{
		
		return dao.getIndividualNoticeList(empId, new java.sql.Date(sdf.parse(fdate).getTime()), new java.sql.Date(sdf.parse(tdate).getTime()));
	}
	
	@Override
	public List<Object[]>  noticeEditData(String noticeId)throws Exception{
		
		return dao.getnoticeEditData(noticeId);
	}
	
	@Override
	public int noticeDelete(String noticeId)throws Exception{
		return dao.noticeDelete(noticeId);
	}
	
	@Override
	public int editNotice(Notice notice)throws Exception{
		
		return dao.editNotice(notice);
	}
	
	@Override
	public List<Object[]> ProjectBudgets() throws Exception {
		
		return dao. ProjectBudgets();
	}
	
	@Override
	public Object[] AllSchedulesCount(String LoginType, String loginid) throws Exception {
		logger.info(new Date() +"Inside SERVICE AllSchedulesCount ");
		Object[] allschedulescount = null;
		if(LoginType.equalsIgnoreCase("Z") || LoginType.equalsIgnoreCase("Y") || LoginType.equalsIgnoreCase("A") || LoginType.equalsIgnoreCase("E") || LoginType.equalsIgnoreCase("L")|| LoginType.equalsIgnoreCase("C")|| LoginType.equalsIgnoreCase("I") )
	     {
			allschedulescount = dao. AllSchedulesCount("0");
	     }
		else  if(!LoginType.equalsIgnoreCase("U") )
		{
			allschedulescount = dao. AllSchedulesCount(loginid);
		}
			return allschedulescount;
			
	}
	
	@Override
	public String getEmpNo(long empId) throws Exception{
		logger.info(new Date() +"Inside SERVICE getEmpNo ");
		List<String> list=dao.getEmpNo(empId);
		String empno=null;
		if(list!=null && list.size()>0) {
			empno=list.get(0);
		}
		return empno;
	}

	
	
	@Override
	public List<Object[]> ProjectMeetingCount(String LoginType,String empid,String labcode) throws Exception {
		logger.info(new Date() +"Inside SERVICE ProjectMeetingCount ");
	
		List<Object[]> ProjectList=dao.ProjectEmployeeList(empid,LoginType,labcode);
		
		List<Object[]> al= new ArrayList<Object[]>();
    	
    	for(Object[] obj : ProjectList ) {
    		
    		al.addAll(dao.ProjectMeetingCount(obj[0].toString()));
    	}
		
		return al;
		
		
	}
	
	@Override
	public List<Object[]> ProjectList(String LoginType,String empid,String labcode) throws Exception {

		logger.info(new Date() +"Inside SERVICE ProjectList");
		List<Object[]> ProjectList=dao.ProjectEmployeeList(empid,LoginType,labcode);
		
		return ProjectList;
	}
	
	@Override
	public ArrayList<String> ProjectQuaters(String ProjectId) throws Exception {
		logger.info(new Date() +"Inside SERVICE ProjectQuaters ");
		ArrayList<String> Qualters = new ArrayList<String>();
		if(!"0".equalsIgnoreCase(ProjectId)) {
		Object[] Quater=dao.ProjectQuaters(ProjectId).get(0);
		
		Date dt=sdf2.parse(Quater[0].toString());
		Calendar c=Calendar.getInstance();
		c.setTime(dt);
		int month = c.get(Calendar.MONTH);
		int year=c.get(Calendar.YEAR);

		String qtr= (month >= Calendar.JANUARY && month <= Calendar.MARCH)     ? "Q1" :
		            (month >= Calendar.APRIL && month <= Calendar.JUNE)        ? "Q2" :
		            (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)    ? "Q3" :
		                                                                    "Q4";
		if("Q1".equals(qtr)) {
			Object[] mile=dao.MileQuaters(ProjectId, 1, year).get(0);
			Qualters.add("Q1-"+year+":"+mile[0]+":"+mile[1]);
			Object[] mile2=dao.MileQuaters(ProjectId, 2, year).get(0);
			Qualters.add("Q2-"+year+":"+mile2[0]+":"+mile2[1]);
			Object[] mile3=dao.MileQuaters(ProjectId, 3, year).get(0);
			Qualters.add("Q3-"+year+":"+mile3[0]+":"+mile3[1]);
			Object[] mile4=dao.MileQuaters(ProjectId, 4, year).get(0);
			Qualters.add("Q4-"+year+":"+mile4[0]+":"+mile4[1]);
		}else if("Q2".equals(qtr)) {
			Object[] mile2=dao.MileQuaters(ProjectId, 2, year).get(0);
			Qualters.add("Q2-"+year+":"+mile2[0]+":"+mile2[1]);
			Object[] mile3=dao.MileQuaters(ProjectId, 3, year).get(0);
			Qualters.add("Q3-"+year+":"+mile3[0]+":"+mile3[1]);
			Object[] mile4=dao.MileQuaters(ProjectId, 4, year).get(0);
			Qualters.add("Q4-"+year+":"+mile4[0]+":"+mile4[1]);
		}else if("Q3".equals(qtr)) {
			Object[] mile3=dao.MileQuaters(ProjectId, 3, year).get(0);
			Qualters.add("Q3-"+year+":"+mile3[0]+":"+mile3[1]);
			Object[] mile4=dao.MileQuaters(ProjectId, 4, year).get(0);
			Qualters.add("Q4-"+year+":"+mile4[0]+":"+mile4[1]);
		}else {
			Object[] mile4=dao.MileQuaters(ProjectId, 4, year).get(0);
			Qualters.add("Q4-"+year+":"+mile4[0]+":"+mile4[1]);
		}
		for(int i=3;i<=Integer.parseInt(Quater[2].toString());i++) {
			Object[] mile=dao.MileQuaters(ProjectId, 1, (year+i-2)).get(0);
			Qualters.add("Q1-"+(year+i-2)+":"+mile[0]+":"+mile[1]);
			Object[] mile2=dao.MileQuaters(ProjectId, 2, (year+i-2)).get(0);
			Qualters.add("Q2-"+(year+i-2)+":"+mile2[0]+":"+mile2[1]);
			Object[] mile3=dao.MileQuaters(ProjectId, 3, (year+i-2)).get(0);
			Qualters.add("Q3-"+(year+i-2)+":"+mile3[0]+":"+mile3[1]);
			Object[] mile4=dao.MileQuaters(ProjectId, 4, (year+i-2)).get(0);
			Qualters.add("Q4-"+(year+i-2)+":"+mile4[0]+":"+mile4[1]);
		}
		Date dt1=sdf2.parse(Quater[1].toString());
		Calendar c1=Calendar.getInstance();
		c1.setTime(dt1);
		int month1 = c1.get(Calendar.MONTH);
		int year1=c1.get(Calendar.YEAR);
		String qtr1= (month1 >= Calendar.JANUARY && month1 <= Calendar.MARCH)     ? "Q1" :
		            (month1 >= Calendar.APRIL && month1 <= Calendar.JUNE)        ? "Q2" :
		            (month1 >= Calendar.JULY && month1 <= Calendar.SEPTEMBER)    ? "Q3" :
		                                                                   "Q4";
		if(year!=year1) { 
		if("Q4".equals(qtr1)) {
			Object[] mile=dao.MileQuaters(ProjectId, 1, year1).get(0);
			Qualters.add("Q1-"+year1+":"+mile[0]+":"+mile[1]);
			Object[] mile2=dao.MileQuaters(ProjectId, 2, year1).get(0);
			Qualters.add("Q2-"+year1+":"+mile2[0]+":"+mile2[1]);
			Object[] mile3=dao.MileQuaters(ProjectId, 3, year1).get(0);
			Qualters.add("Q3-"+year1+":"+mile3[0]+":"+mile3[1]);
			Object[] mile4=dao.MileQuaters(ProjectId, 4, year1).get(0);
			Qualters.add("Q4-"+year1+":"+mile4[0]+":"+mile4[1]);
		}else if("Q3".equals(qtr1)) {
			Object[] mile=dao.MileQuaters(ProjectId, 1, year1).get(0);
			Qualters.add("Q1-"+year1+":"+mile[0]+":"+mile[1]);
			Object[] mile2=dao.MileQuaters(ProjectId, 2, year1).get(0);
			Qualters.add("Q2-"+year1+":"+mile2[0]+":"+mile2[1]);
			Object[] mile3=dao.MileQuaters(ProjectId, 3, year1).get(0);
			Qualters.add("Q3-"+year1+":"+mile3[0]+":"+mile3[1]);
		}else if("Q2".equals(qtr1)) {
			Object[] mile=dao.MileQuaters(ProjectId, 1, year1).get(0);
			Qualters.add("Q1-"+year1+":"+mile[0]+":"+mile[1]);
			Object[] mile2=dao.MileQuaters(ProjectId, 2, year1).get(0);
			Qualters.add("Q2-"+year1+":"+mile2[0]+":"+mile2[1]);

		}else {
			Object[] mile=dao.MileQuaters(ProjectId, 1, year1).get(0);
			Qualters.add("Q1-"+year1+":"+mile[0]+":"+mile[1]);
			
		}
		}
		}
		return Qualters;
	}
	
	
	@Override
	public List<Object[]> GanttChartList() throws Exception {
	
		return dao.GanttChartList();
	}
	
	@Override
	public List<Object[]> ProjectHealthData(String LabCode) throws Exception{
		
		return dao.ProjectHealthData(LabCode);
	}

	@Override
	public Object[] ProjectHealthTotalData(String ProjectId,String EmpId, String LoginType,String LabCode,String IsAll) throws Exception
	{
		return dao.ProjectHealthTotalData(ProjectId,EmpId,LoginType,LabCode,IsAll);
	}


	@Override
	public long ProjectHealthUpdate(String EmpId, String UserName) throws Exception {
		List<Object[]> proList=dao.ProjectList(EmpId).stream().filter(e-> !"0".equalsIgnoreCase(e[0].toString())).collect(Collectors.toList());
		long result=0;
		for(Object[] obj:proList) {
			try {
		        dao.ProjectHealthDelete(obj[0].toString());
				Object[] data=dao.ProjectHealthInsertData(obj[0].toString());
				ProjectHealth health=new ProjectHealth();
				health.setLabCode(data[0].toString());
				health.setProjectId(Long.parseLong(data[1].toString()));
				health.setProjectShortName(data[2].toString());
				health.setPMRCHeld(Long.parseLong(data[3].toString()));
				health.setPMRCPending( Long.parseLong(data[4].toString())>=0 ?  Long.parseLong(data[4].toString()) : 0 );
				health.setEBHeld(Long.parseLong(data[5].toString()));
				health.setEBPending(  Long.parseLong(data[6].toString())>=0 ? Long.parseLong(data[6].toString()) : 0 );
				health.setMilPending(Long.parseLong(data[7].toString()));
				health.setMilDelayed(Long.parseLong(data[8].toString()));
				health.setMilCompleted(Long.parseLong(data[9].toString()));
				health.setActionPending(Long.parseLong(data[10].toString()));
				health.setActionForwarded(Long.parseLong(data[11].toString()));
				health.setActionDelayed(Long.parseLong(data[12].toString()));
				health.setActionCompleted(Long.parseLong(data[13].toString()));
				health.setRiskPending(Long.parseLong(data[14].toString()));
				health.setRiskCompleted(Long.parseLong(data[15].toString()));
				health.setProjectType(data[20].toString());
				health.setEndUser(data[21].toString());
				health.setProjectCode(data[22].toString());
				health.setPMRCTotal(Long.parseLong(data[23].toString()));
				health.setEBTotal(Long.parseLong(data[24].toString()));
				
				if(data[16]!=null) {
					health.setExpenditure(Double.parseDouble(data[16].toString()));
					health.setDipl(Double.parseDouble(data[18].toString()));
					health.setOutCommitment(Double.parseDouble(data[17].toString()));
					health.setBalance(Double.parseDouble(data[19].toString()));
				}else {
					health.setExpenditure(Double.parseDouble("0.00"));
					health.setDipl(Double.parseDouble("0.00"));
					health.setOutCommitment(Double.parseDouble("0.00"));
					health.setBalance(Double.parseDouble("0.00"));
				}
				
				health.setPJBHeld(Long.parseLong(data[32].toString()));
				health.setPJBPending(Long.parseLong(data[33].toString()));
				health.setPJBTotal(Long.parseLong(data[34].toString()));
				health.setPJBTotalToBeHeld(Long.parseLong(data[35].toString()));
				
				health.setPMBHeld(Long.parseLong(data[36].toString()));
				health.setPMBPending(Long.parseLong(data[37].toString()));
				health.setPMBTotal(Long.parseLong(data[38].toString()));
				health.setPMBTotalToBeHeld(Long.parseLong(data[39].toString()));
				
				health.setABHeld(Long.parseLong(data[40].toString()));
				health.setABPending(Long.parseLong(data[41].toString()));
				health.setABTotal(Long.parseLong(data[42].toString()));
				health.setABTotalToBeHeld(Long.parseLong(data[43].toString()));
				
				health.setCreatedBy(UserName);
				health.setCreatedDate(sdf1.format(new Date()));
				health.setTodayChanges(Long.parseLong(data[25].toString()));
				health.setWeeklyChanges(Long.parseLong(data[26].toString()));
				health.setMonthlyChanges(Long.parseLong(data[27].toString()));
				health.setPDC(data[28].toString());		
				health.setPMRCTotalToBeHeld(Long.parseLong(data[29].toString()));
				health.setEBTotalToBeHeld(Long.parseLong(data[30].toString()));
				health.setSanctionDate(data[31].toString());
				result=dao.ProjectHealthInsert(health);
			}catch (Exception e) {
				e.printStackTrace();
			}		
			}
		return result;
	}
	
	@Override
	public long ProjectHoaUpdate(List<ProjectHoa> hoa,String Username,List<IbasLabMaster> LabDetails) throws Exception{
		logger.info(new Date() +"Inside SERVICE ProjectHoaUpdate ");
		
		long count1 =0 ;
		try {
		long count = dao.ProjectHoaDelete(LabDetails.get(0).getLabCode());
		for(ProjectHoa obj : hoa) {
			obj.setCreatedBy(Username);
			obj.setCreatedDate(sdf1.format(new Date()));
			obj.setLabCode(LabDetails.get(0).getLabCode());
			count1=dao.ProjectHoaUpdate(obj);
		}
		}catch (Exception e) {
		e.printStackTrace();
		}
		return count1;
	}
	
	@Override
	public Object[]  ChangesTotalCountData(String ProjectId) throws Exception{
		
		return dao.ChangesTotalCountData(ProjectId);
	}

	@Override
	public List<Object[]> MeetingChanges(String ProjectId,String Interval,String LabCode) throws Exception {
	
		return dao.MeetingChanges(ProjectId,Interval,LabCode);
	}
	
	@Override
	public List<Object[]> MilestoneChanges(String ProjectId,String Interval,String LabCode) throws Exception {
		
		return dao.MilestoneChanges(ProjectId, Interval,LabCode);
	}
	
	@Override
	public List<Object[]> ActionChanges(String ProjectId,String Interval,String LabCode) throws Exception{
		
		return dao.ActionChanges(ProjectId,Interval,LabCode);
	}
	
	@Override
	public List<Object[]> RiskChanges(String ProjectId,String Interval,String LabCode)throws Exception{
		
		return dao.RiskChanges(ProjectId,Interval,LabCode);
	}
	
	@Override
	public List<Object[]> FinanceDataPartA(String ProjectId, String Interval) throws Exception{
		
		return dao.FinanceDataPartA(ProjectId, Interval);
	}
	
	@Override
	public long ProjectFinanceChangesUpdate(List<FinanceChanges> Monthly, List<FinanceChanges> Weekly, List<FinanceChanges> Today, String UserId,String EmpId) throws Exception {
		logger.info(new Date() +"Inside SERVICE ProjectFinanceChangesUpdate ");
		List<Object[]> proList=dao.ProjectList(EmpId);
		long result=0;
		for(Object[] obj:proList) {
			try {
		        dao.ProjectHoaChangesDelete(obj[0].toString());
		        ProjectHoaChanges changes = new ProjectHoaChanges();
		        changes.setProjectId(Long.parseLong(obj[0].toString()));
		        changes.setProjectCode(obj[1].toString());
		        changes.setMonthlyChanges(Long.valueOf(Monthly.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setWeeklyChanges(Long.valueOf(Weekly.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setTodayChanges(Long.valueOf(Today.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setCreatedBy(UserId);
		        changes.setCreatedDate(sdf1.format(new Date()));
		        changes.setIsActive(1);
		        
		        result= dao.ProjectHoaChangesInsert(changes);

			}catch (Exception e) {
				e.printStackTrace();
			}		
			}
		return result;
	}
	
	
	@Override
	public Object[] ProjectData(String projectid) throws Exception 
	{
		return dao.ProjectData(projectid);
	}
	
	
	@Override
	public long CCMViewDataUpdate(List<CCMView> CCMViewData,String LabCode,String ClusterId, String UserId,String EmpId) throws Exception
	{
		logger.info(new Date() +"Inside SERVICE CCMViewDataUpdate ");
		List<Object[]> proList=dao.ProjectList(EmpId);
		
		
		
		long result=0;
		
		if(CCMViewData.size()>0) 
		{
			dao.CCMDataDelete(LabCode);
		
		
			for(CCMView ccmdata:CCMViewData)
			{
				PFMSCCMData pfmsccm = PFMSCCMData.builder()
								
								.ClusterId(Long.parseLong(ClusterId))
								.LabCode(LabCode)
								.ProjectId(ccmdata.getProjectId())
								.ProjectCode(ccmdata.getProjectCode().trim())
								.BudgetHeadId(ccmdata.getBudgetHeadId())
								.BudgetHeadDescription(ccmdata.getBudgetHeadDescription())
								.AllotmentCost(ccmdata.getAllotmentCost())
								.Expenditure(ccmdata.getExpenditure())
								.Balance(ccmdata.getBalance())
								.Q1CashOutGo(ccmdata.getQ1CashOutGo())
								.Q2CashOutGo(ccmdata.getQ2CashOutGo())
								.Q3CashOutGo(ccmdata.getQ3CashOutGo())
								.Q4CashOutGo(ccmdata.getQ4CashOutGo())
								.Required(ccmdata.getRequired())
								.CreatedDate(sdf1.format(new Date()))
								.build();
				result= dao.CCMDataInsert(pfmsccm);
				
			}
		}
		return result;
	}
	
	@Override
	public List<Object[]> getCCMData(String EmpId,String LoginType,String LabCode)throws Exception
	{
		return dao.getCCMData(EmpId, LoginType, LabCode);
	}
	@Override
	public List<Object[]> DashboardFinanceCashOutGo(String LoginType,String EmpId,String LabCode,String ClusterId)throws Exception
	{
		List<Object[]> CashOutGo = dao.DashboardFinanceCashOutGo(LoginType, EmpId, LabCode, ClusterId);
		BigDecimal onecrore = new BigDecimal(10000000);
		for(Object[] OutGo : CashOutGo)
		{ 
			OutGo[3] = Double.parseDouble(OutGo[3].toString())!=0 ?  new BigDecimal(OutGo[3].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[3].toString()).toString() ;
			OutGo[4] = Double.parseDouble(OutGo[4].toString())!=0 ?  new BigDecimal(OutGo[4].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[4].toString()).toString() ;
			OutGo[5] = Double.parseDouble(OutGo[5].toString())!=0 ?  new BigDecimal(OutGo[5].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[5].toString()).toString() ;
			OutGo[6] = Double.parseDouble(OutGo[6].toString())!=0 ?  new BigDecimal(OutGo[6].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[6].toString()).toString();
			OutGo[7] = Double.parseDouble(OutGo[7].toString())!=0 ?  new BigDecimal(OutGo[7].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[7].toString()).toString() ;
			OutGo[8] = Double.parseDouble(OutGo[8].toString())!=0 ?  new BigDecimal(OutGo[8].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[8].toString()).toString() ;
			OutGo[9] = Double.parseDouble(OutGo[9].toString())!=0 ?  new BigDecimal(OutGo[9].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[9].toString()).toString() ;
			OutGo[10] = Double.parseDouble(OutGo[10].toString())!=0 ?  new BigDecimal(OutGo[10].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[10].toString()).toString() ;
		}
		return CashOutGo;
	}
	
	
	@Override
	public List<Object[]> DashboardFinance(String LoginType,String EmpId,String LabCode,String ClusterId)throws Exception
	{
		List<Object[]> DashboardFinance = dao.DashboardFinance(LoginType, EmpId, LabCode, ClusterId);
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
	public List<Object[]> DashboardProjectFinanceCashOutGo(String projectcode)throws Exception
	{
		List<Object[]> CashOutGo = dao.DashboardProjectFinanceCashOutGo(projectcode);
		BigDecimal onecrore = new BigDecimal(10000000);
		for(Object[] OutGo : CashOutGo)
		{ 
			OutGo[3] = Double.parseDouble(OutGo[3].toString())!=0 ?  new BigDecimal(OutGo[3].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[3].toString()).toString() ;
			OutGo[4] = Double.parseDouble(OutGo[4].toString())!=0 ?  new BigDecimal(OutGo[4].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[4].toString()).toString() ;
			OutGo[5] = Double.parseDouble(OutGo[5].toString())!=0 ?  new BigDecimal(OutGo[5].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[5].toString()).toString() ;
			OutGo[6] = Double.parseDouble(OutGo[6].toString())!=0 ?  new BigDecimal(OutGo[6].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[6].toString()).toString() ;
			OutGo[7] = Double.parseDouble(OutGo[7].toString())!=0 ?  new BigDecimal(OutGo[7].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[7].toString()).toString() ;
			OutGo[8] = Double.parseDouble(OutGo[8].toString())!=0 ?  new BigDecimal(OutGo[8].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[8].toString()).toString() ;
			OutGo[9] = Double.parseDouble(OutGo[9].toString())!=0 ?  new BigDecimal(OutGo[9].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[9].toString()).toString() ;
			OutGo[10] = Double.parseDouble(OutGo[10].toString())!=0 ?  new BigDecimal(OutGo[10].toString()).divide(onecrore).setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : new BigDecimal(OutGo[10].toString()).toString() ;
		}
		return CashOutGo;
	}
	
	@Override
	public Object[] ProjectAttributes(String projectcode)throws Exception
	{
		return dao.ProjectAttributes(projectcode);
	}
	

	@Override
	public long GetSMSInitiatedCount(String SmsTrackingType) throws Exception {
		return dao.GetSMSInitiatedCount(SmsTrackingType);
	}
	
	@Override
	public long InsertSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsSmsTracking Model  = new PfmsSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    long dailyPendingCount = dao.GetDailyExpectedPendingReplyCount();
	    Model.setSmsExpectedCount(dailyPendingCount);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertSmsTrackRow(Model);
		
		return rowAddResult;
	}
	
	
	@Override
	public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception {
		return dao.GetDailyPendingAssigneeEmpData();
	}
	
	@Override
	public long InsertDailySmsPendingInsights(long smsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		    List<Object[]> PendingAssingEmpsDetailstoSendSms = dao.GetDailyPendingAssigneeEmpData();
		    if (PendingAssingEmpsDetailstoSendSms != null && PendingAssingEmpsDetailstoSendSms.size() > 0) {
		        Map<Integer, Set<String>> empActionItemMap = new HashMap();

		        for (Object[] rowData : PendingAssingEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[1].toString());
		            String Mobileno = rowData[4].toString();
		            
		            if (!empActionItemMap.containsKey(empId)) {
		            	empActionItemMap.put(empId, new HashSet<>());
		            }

		            empActionItemMap.get(empId).add(Mobileno);
		        }

		        for (Map.Entry<Integer, Set<String>> entry : empActionItemMap.entrySet()) {
		            int empId = entry.getKey();
		            Set<String> Mobileno = entry.getValue();
		            
		            String mobileNumber = Mobileno.isEmpty() ? null : Mobileno.iterator().next();
		           // Object[] ActionAssignCounts =dao.ActionAssignCounts(empId,LocalDate.now().toString());
		            if(mobileNumber != null && !mobileNumber.equalsIgnoreCase("0") && mobileNumber.trim().length()>0 && mobileNumber.trim().length()==10) {
		            List<Object[]> ActionAssignCount=dao.ActionAssignedCounts(empId);
		            // Create a new instance of DakMailTrackingInsights for each entry
		            PfmsSmsTrackingInsights Insights = new PfmsSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            String message=null;
		            String Action=null;
		            String Meeting=null;
		            String Milestone=null;
		            
		            System.out.println("Mobileno:"+mobileNumber);
		            for(Object[] obj:ActionAssignCount) {
		            	if(obj[0].toString().equalsIgnoreCase("ActionItems")) {
				            Insights.setActionItemP(Long.parseLong(obj[2].toString()));
				            Insights.setActionItemTP(Long.parseLong(obj[3].toString()));
				            Insights.setActionItemDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Action="Action Pending - "+Long.parseLong(obj[2].toString()) +" / Action Delay - "+ Long.parseLong(obj[4].toString()) +" / Action Today - "+ Long.parseLong(obj[3].toString())+" / ";
				            }
		            	}else if(obj[0].toString().equalsIgnoreCase("MeetingActions")) {
		            		Insights.setMeetingActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMeetingActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMeetingActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Meeting="Meeting Pending - "+Long.parseLong(obj[2].toString()) +" /  Meeting Delay - "+ Long.parseLong(obj[4].toString())+ " /  Meeting Today - "+Long.parseLong(obj[3].toString())+" ";
				            }
		            	}else {
		            		Insights.setMilestoneActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMilestoneActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMilestoneActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Milestone="MileStone Pending - "+Long.parseLong(obj[2].toString()) +" / MileStone Delay - "+ Long.parseLong(obj[4].toString())+ " / Milestone Today - "+Long.parseLong(obj[3].toString())+" / ";
				            }
		            	}
		            //	message="Good Morning,\nPMS P / D / T  \n" +(Action != null ? "AI  "+ Action : "") + "\n"+ (Meeting != null ?"MS  "+ Meeting : "") +"\n"+(Milestone != null ?"MT  "+ Milestone : "")+"\n-PMS Team.";
		            	message = "Good Morning PMS  " ;
		            	if(Action!=null) {
		            		 message += Action+" " ;
		            	}
		            	if (Milestone != null) {
		            	    message +=  Milestone+" " ;
		            	}
		            	if (Meeting != null) {
		            	    message += Meeting+" " ;
		            	}
		            	message += " - PMS Team.";

		            	Insights.setMessage(message);
		            	System.out.println("message:12343524"+message);
		            }
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		        }
		        }
		    }

		    return count;
	}
	
	@Override
	public Object[] ActionAssignCounts(long EmpId, String actionDate) throws Exception {
		return dao.ActionAssignCounts(EmpId, actionDate);
	}
	
	@Override
	public long UpdateParticularEmpSmsStatus(String SmsPurpose, String SmsStatus, long EmpId,long effectivelyFinalSmsTrackingId, String message) throws Exception {
		return dao.UpdateParticularEmpSmsStatus(SmsPurpose,SmsStatus,EmpId,effectivelyFinalSmsTrackingId,message);
	}
	
	@Override
	public long updateSmsSuccessCount(long smsTrackingId, long SuccessCount, String TrackingType) throws Exception {
		long rowUpdateResult = dao.UpdateDakActionTrackRow(smsTrackingId,SuccessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateNoSmsPendingReply(String TrackingType) throws Exception {
		return dao.UpdateNoSmsPendingReply(TrackingType);
	}
	
	@Override
	public long DirectorInsertSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsSmsTracking Model  = new PfmsSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    Model.setSmsExpectedCount(1);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertSmsTrackRow(Model);
		
		return rowAddResult;
	}


	@Override
	public List<Object[]> GetDirectorDailyPendingAssignEmpData(String Lab) throws Exception {
		return dao.GetDirectorDailyPendingAssignEmpData(Lab);
	}
	
	@Override
	public long DirectorInsertDailySmsPendingInsights(long smsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		 List<Object[]> DirectorPendingAssignEmpsDetailstoSendSms = dao.GetDirectorDailyPendingAssignEmpData("LRDE");
		    if (DirectorPendingAssignEmpsDetailstoSendSms != null && DirectorPendingAssignEmpsDetailstoSendSms.size() > 0) {

		        for (Object[] rowData : DirectorPendingAssignEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[0].toString());
		           // Object[] DirectorActionAsssignCounts =dao.DirectorActionAssignCounts(LocalDate.now().toString());
		            // Create a new instance of DakMailTrackingInsights for each entry
		            String Mobileno=rowData[1].toString();
		            if( Mobileno != null && !Mobileno.toString().equalsIgnoreCase("0") && Mobileno.toString().trim().length()>0 && Mobileno.toString().trim().length()==10) {
		            List<Object[]> DirectorActionAssignCounts=dao.DirectorActionAssignedCounts();
		            PfmsSmsTrackingInsights Insights = new PfmsSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            String message=null;
		            String Action=null;
		            String Meeting=null;
		            String Milestone=null;
		            for(Object[] obj:DirectorActionAssignCounts) {
		            	if(obj[0].toString().equalsIgnoreCase("ActionItems")) {
				            Insights.setActionItemP(Long.parseLong(obj[2].toString()));
				            Insights.setActionItemTP(Long.parseLong(obj[3].toString()));
				            Insights.setActionItemDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	 Action="Action Pending - "+Long.parseLong(obj[2].toString()) +" / Action Delay - "+ Long.parseLong(obj[4].toString()) +" / Action Today - "+ Long.parseLong(obj[3].toString())+" / ";
					        }
		            	}else if(obj[0].toString().equalsIgnoreCase("MeetingActions")) {
		            		Insights.setMeetingActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMeetingActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMeetingActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	Meeting="Meeting Pending - "+Long.parseLong(obj[2].toString()) +" /  Meeting Delay - "+ Long.parseLong(obj[4].toString())+ " /  Meeting Today - "+Long.parseLong(obj[3].toString())+" ";
					        }
		            	}else {
		            		Insights.setMilestoneActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMilestoneActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMilestoneActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	Milestone="MileStone Pending - "+Long.parseLong(obj[2].toString()) +" / MileStone Delay - "+ Long.parseLong(obj[4].toString())+ " / Milestone Today - "+Long.parseLong(obj[3].toString())+" / ";
					        }
		            	}
		            	message = "Good Morning PMS " ;
		            	if(Action!=null) {
		            		 message += Action+" " ;
		            	}
		            	if (Milestone != null) {
		            	    message += Milestone+" ";
		            	}
		            	if (Meeting != null) {
		            	    message += Meeting+" " ;
		            	}
		            	message += " - PMS Team.";

		            	Insights.setMessage(message);
		            	System.out.println("message:12343524"+message);
		            }
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		            }
		        }
		    }

		    return count;
	}
	
	@Override
	public Object[] DirectorActionAssignCounts(String PdcDate) throws Exception {
		return dao.DirectorActionAssignCounts(PdcDate);
	}
	
	@Override
	public long GetCommitteSMSInitiatedCount(String SmsTrackingType) throws Exception {
		return dao.GetCommitteSMSInitiatedCount(SmsTrackingType);
	}
	
	@Override
	public long InsertCommitteSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsCommitteSmsTracking Model  = new PfmsCommitteSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    long dailyCommitteCount = dao.dailyCommitteCount(LocalDate.now().toString());
	    Model.setSmsExpectedCount(dailyCommitteCount);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertCommitteSmsTrackRow(Model);
		
		return rowAddResult;
	}


	@Override
	public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception {
		return dao.GetCommitteEmpsDetailstoSendSms();
	}
	
	@Override
	public long InsertDailyCommitteSmsInsights(long committeSmsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		    List<Object[]> CommitteEmpsDetailstoSendSms = dao.GetCommitteEmpsDetailstoSendSms();
		    if (CommitteEmpsDetailstoSendSms != null && CommitteEmpsDetailstoSendSms.size() > 0) {

		        for (Object[] rowData : CommitteEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[0].toString());
		            String Mobileno=rowData[1].toString();
		            String message="";
		            if(Mobileno != null && !Mobileno.toString().equalsIgnoreCase("0") && Mobileno.toString().trim().length()>0 && Mobileno.toString().trim().length()==10) {
               	    List<Object[]> committedata=dao.getCommittedata(empId);
		            PfmsCommitteSmsTrackingInsights Insights = new PfmsCommitteSmsTrackingInsights();
		            Insights.setCommitteSmsTrackingId(committeSmsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            for(Object[] str:committedata) {
						LocalTime time = LocalTime.parse(str[6].toString());
				        
				        // Format the time as "HH:mm Hrs"
				        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'Hrs'");
				        String formattedTime = time.format(formatter);
				        message="Good Morning ";
						if(str[7]!=null && str[7].toString().equalsIgnoreCase("P")) {
							message+=" Today Status: P"+str[1].toString()+" - "+str[2].toString() +" @ " +formattedTime+". ";
						}else if(str[7]!=null && str[7].toString().equalsIgnoreCase("N")){
							message+=" Today Status: NP - "+str[2].toString() +" @ " +formattedTime+". ";
						}else{ 
							message+=" Today Status: GEN - "+str[2].toString() +" @ " +formattedTime+". ";
						}
						
					}
					message+=" - PMS Team.";
					Insights.setMessage(message);
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertCommitteSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		            }
		        }
		  }
		    return count;
	}
	
	public List<Object[]> getCommittedata(long EmpId) throws Exception{
		return dao.getCommittedata(EmpId);
	}
	
	@Override
	public long UpdateParticularCommitteEmpSmsStatus(String SmsPurpose, String SmsStatus, long empId,long effectivelyFinalSmsTrackingId, String message) throws Exception {
		return dao.UpdateParticularCommitteEmpSmsStatus(SmsPurpose,SmsStatus,empId,effectivelyFinalSmsTrackingId,message);
	}
	
	@Override
	public long updateCommitteSmsSuccessCount(long committeSmsTrackingId, long SuccessCount, String TrackingType)throws Exception {
		long rowUpdateResult = dao.UpdateCommitteSmsTrackRow(committeSmsTrackingId,SuccessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateCommitteNoSmsPending(String TrackingType) throws Exception {
		return dao.UpdateCommitteNoSmsPending(TrackingType);
	}
	
	@Override
	public List<Object[]> ActionAssignedCounts(long empId) throws Exception {
		return dao.ActionAssignedCounts(empId);
	}
	
	@Override
	public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception {
		return dao.SmsReportList(fromDate,toDate);
	}
	
@Override
	public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception {
		return dao.SmsCommitteReportList(fromDate,toDate);
	}
}
