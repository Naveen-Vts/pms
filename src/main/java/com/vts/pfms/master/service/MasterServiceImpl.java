package com.vts.pfms.master.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.admin.dao.AdminDao;
import com.vts.pfms.admin.model.EmployeeDesig;
import com.vts.pfms.committee.model.PfmsEmpRoles;
import com.vts.pfms.committee.model.ProgrammeMaster;
import com.vts.pfms.committee.model.ProgrammeProjects;
import com.vts.pfms.master.dao.MasterDao;
import com.vts.pfms.master.dto.DivisionEmployeeDto;
import com.vts.pfms.master.dto.LabMasterAdd;
import com.vts.pfms.master.dto.OfficerMasterAdd;
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

@Service
public class MasterServiceImpl implements MasterService {

	
	private static final Logger logger=LogManager.getLogger(MasterServiceImpl.class);

	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	@Autowired
	MasterDao dao;
	
	@Autowired
	AdminDao admindao;
	
	@Value("${ApplicationFilesDrive}")
	String uploadpath;
	
	@Override
	public List<Object[]> OfficerList() throws Exception {
		
		return dao.OfficerList();
	}
	
	@Override
	public List<Object[]> DesignationList() throws Exception {
		
		return dao.DesignationList();
	}
	
	@Override
	public List<Object[]> OfficerDivisionList() throws Exception {
		
		return dao.OfficerDivisionList();
	}
	
	@Override
	public List<Object[]> OfficerEditData(String OfficerId) throws Exception {
		
		return dao.OfficerEditData(OfficerId);
	}
	
	@Override
	public int OfficerMasterDelete(String OfficerId, String UserId) throws Exception {

		logger.info(new Date() +" Inside SERVICE OfficerMasterDelete ");
		Employee employee = new Employee();
		employee.setSrNo((long) 0);
		employee.setModifiedBy(UserId);
		employee.setModifiedDate(sdf1.format(new Date()));
		employee.setIsActive(0);
		employee.setEmpId(Long.parseLong(OfficerId));
		
		return dao.OfficerMasterDelete(employee);
	}
	
	@Override
	public int OfficerExtDelete(String OfficerId, String UserId) throws Exception {

		logger.info(new Date() +" Inside SERVICE OfficerExtDelete ");
		Employee employee = new Employee();
		employee.setModifiedBy(UserId);
		employee.setModifiedDate(sdf1.format(new Date()));
		employee.setIsActive(0);
		employee.setEmpId(Long.parseLong(OfficerId));
		
		return dao.OfficerExtDelete(employee);
	}

	@Override
	public List<String> EmpNoCheck() throws Exception {

		return dao.EmpNoCheck();
	}
	
	@Override
	public List<String> EmpExtNoCheck() throws Exception {

		return dao.EmpExtNoCheck();
	}
	
	@Override
	public Long OfficerMasterInsert(OfficerMasterAdd officermasteradd, String UserId) throws Exception 
	{
		logger.info(new Date() +" Inside SERVICE OfficerMasterInsert ");
		Employee employee= new Employee();
		employee.setSalutation(officermasteradd.getSalutation());
		employee.setTitle(officermasteradd.getTitle());
		employee.setEmpNo(officermasteradd.getEmpNo());
		employee.setEmpName(officermasteradd.getEmpName());
		employee.setDesigId(Long.parseLong(officermasteradd.getDesignation()));
		employee.setExtNo(officermasteradd.getExtNo());
		employee.setEmail(officermasteradd.getEmail());
		employee.setDivisionId(Long.parseLong(officermasteradd.getDivision()));
		employee.setDronaEmail(officermasteradd.getDronaEmail());
		employee.setInternetEmail(officermasteradd.getInternalEmail());
		employee.setMobileNo(Long.parseLong(officermasteradd.getMobileNo()));
		employee.setCreatedBy(UserId);
		employee.setCreatedDate(sdf1.format(new Date()));
		employee.setIsActive(1);
		employee.setSrNo(0l);
		employee.setLabCode(officermasteradd.getLabCode());
		employee.setSuperiorOfficer(officermasteradd.getSuperiorOfficer());
		employee.setEmpStatus(officermasteradd.getEmpStatus());	//srikant

		return dao.OfficeMasterInsert(employee);
	}
	
	@Override
	public Long OfficerExtInsert(OfficerMasterAdd officermasteradd, String UserId) throws Exception 
	{
		logger.info(new Date() +" Inside SERVICE OfficerExtInsert ");
		Employee empExternal=new Employee();
		empExternal.setSrNo(Long.parseLong(officermasteradd.getSrNo()));
		empExternal.setEmpNo(officermasteradd.getEmpNo());
		empExternal.setLabCode( officermasteradd.getLabId());
		empExternal.setEmpName(officermasteradd.getEmpName());
		empExternal.setDesigId(Long.parseLong(officermasteradd.getDesignation()));
		empExternal.setExtNo(officermasteradd.getExtNo());
		empExternal.setEmail(officermasteradd.getEmail());
		empExternal.setDivisionId(Long.parseLong(officermasteradd.getDivision()));
		empExternal.setDronaEmail(officermasteradd.getDronaEmail());
		empExternal.setInternetEmail(officermasteradd.getInternalEmail());
		empExternal.setTitle(officermasteradd.getTitle());
		empExternal.setSalutation(officermasteradd.getSalutation());
		empExternal.setCreatedBy(UserId);
		empExternal.setCreatedDate(sdf1.format(new Date()));
		empExternal.setIsActive(1);
		empExternal.setMobileNo(Long.parseLong(officermasteradd.getMobileNo()));
		empExternal.setSuperiorOfficer(officermasteradd.getSuperiorOfficer());
		return dao.OfficeMasterExternalInsert(empExternal);
	}
	
	@Override
	public int OfficerMasterUpdate(OfficerMasterAdd officermasteradd, String UserId) throws Exception 
	{
		logger.info(new Date() +" Inside SERVICE OfficerMasterUpdate ");
			Employee empExternal=new Employee();
			empExternal.setTitle(officermasteradd.getTitle());
			empExternal.setSalutation(officermasteradd.getSalutation());
			empExternal.setEmpNo(officermasteradd.getEmpNo());
			empExternal.setEmpName(officermasteradd.getEmpName());
			empExternal.setDesigId(Long.parseLong(officermasteradd.getDesignation()));
			empExternal.setExtNo(officermasteradd.getExtNo());
			empExternal.setMobileNo(Long.parseLong(officermasteradd.getMobileNo()));
			empExternal.setEmail(officermasteradd.getEmail());
			empExternal.setDivisionId(Long.parseLong(officermasteradd.getDivision()));
			empExternal.setDronaEmail(officermasteradd.getDronaEmail());
			empExternal.setInternetEmail(officermasteradd.getInternalEmail());
			empExternal.setModifiedBy(UserId);
			empExternal.setModifiedDate(sdf1.format(new Date()));
			empExternal.setEmpId(Long.parseLong(officermasteradd.getEmpId()));
			empExternal.setSuperiorOfficer(officermasteradd.getSuperiorOfficer());
			empExternal.setEmpStatus(officermasteradd.getEmpStatus()); //srikant

			return dao.OfficerMasterUpdate(empExternal);
		
	}
	
	@Override
	public int OfficerExtUpdate(OfficerMasterAdd officermasteradd, String UserId) throws Exception 
	{		
		logger.info(new Date() +" Inside SERVICE OfficerExtUpdate ");
			Employee empExternal=new Employee();
			empExternal.setEmpNo(officermasteradd.getEmpNo());
			empExternal.setLabCode( officermasteradd.getLabId());
			empExternal.setEmpName(officermasteradd.getEmpName());
			empExternal.setDesigId(Long.parseLong(officermasteradd.getDesignation()));
			empExternal.setExtNo(officermasteradd.getExtNo());
			empExternal.setMobileNo(Long.parseLong(officermasteradd.getMobileNo()));
			empExternal.setEmail(officermasteradd.getEmail());
			empExternal.setDivisionId(Long.parseLong(officermasteradd.getDivision()));
			empExternal.setDronaEmail(officermasteradd.getDronaEmail());
			empExternal.setInternetEmail(officermasteradd.getInternalEmail());
			empExternal.setModifiedBy(UserId);
			empExternal.setModifiedDate(sdf1.format(new Date()));
			empExternal.setEmpId(Long.parseLong(officermasteradd.getEmpId()));
			empExternal.setTitle(officermasteradd.getTitle());
			empExternal.setSalutation(officermasteradd.getSalutation());
			return dao.OfficerExtUpdate(empExternal);
		
	}
    
	@Override
	public List<Object[]> LabList()throws Exception{
		
		return dao.LabList();
	}
	
	@Override
	public List<Object[]>  ExternalOfficerList(String LabCode)throws Exception{
		return dao.ExternalOfficerList(LabCode);
	}
	@Override
	public List<Object[]> ExternalOfficerEditData(String officerId)throws Exception{
		return dao.ExternalOfficerEditData(officerId);
	}
	
	@Override
	public Object[] getOfficerDetalis(String officerId)throws Exception{
		return dao.getOfficerDetalis(officerId);
	}
	
	@Override
	public int updateSeniorityNumber(String empid, String newSeniorityNumber)throws Exception{
		logger.info(new Date() +" Inside SERVICE updateSeniorityNumber ");
		Long empId=Long.parseLong(empid);
		Long SeniorityNumber=Long.parseLong(newSeniorityNumber);
		int result= 0;
		List<Integer> list = new ArrayList<Integer>();
		Long newSeniorityNumberL=SeniorityNumber;
		List<Object[]> EmpSenHaveUpdate=dao.updateAndGetList(empId,newSeniorityNumber);
		if(EmpSenHaveUpdate!=null) list.add(1);
		List<Object[]> result1=EmpSenHaveUpdate.stream().filter(srno-> Long.parseLong(srno[0].toString())>=SeniorityNumber && Long.parseLong(srno[1].toString())!=empId  ).collect(Collectors.toList());
		
		for(Object[] data:result1) { 
		  Long empIdL=Long.parseLong(data[1].toString()); 
		  result = dao.updateAllSeniority(empIdL, ++newSeniorityNumberL);
		  if(result>0)list.add(result);		  
		}

		return list.size();

	}
	
	
	@Override
	public List<Object[]> DivisionList()throws Exception
	{	
		return dao.DivisionList();
	}
	@Override
	public List<Object[]> DivisionEmpList(String divisionid)throws Exception
	{
		return dao.DivisionEmpList(divisionid);
	}	
	@Override
	public List<Object[]> DivisionNonEmpList(String divisionid)throws Exception
	{	
		return dao.DivisionNonEmpList(divisionid);
	}
	
	@Override
	public Object[] DivisionData(String divisionid)throws Exception
	{
		return dao.DivisionData(divisionid);
	}
	@Override
	public int DivsionEmployeeRevoke(DivisionEmployeeDto dto)throws Exception
	{
		dto.setModifiedDate(sdf1.format(new Date()));
		return dao.DivsionEmployeeRevoke(dto);
	}
	@Override
	public long DivisionAssignSubmit(DivisionEmployeeDto dto)throws Exception
	{
		logger.info(new Date() +" Inside SERVICE DivisionAssignSubmit ");
		int count=0;
		for(int i=0;i<dto.getEmpId().length;i++)  //
		{
			DivisionEmployee model= new DivisionEmployee();
			model.setDivisionId(Long.parseLong(dto.getDivisionId()));
			model.setEmpId(Long.parseLong(dto.getEmpId()[i]));
			model.setCreatedBy(dto.getCreatedBy());
			model.setCreatedDate(sdf1.format(new Date()));	
			model.setIsActive(1);
			if(dao.DivisionAssignSubmit(model)>0) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public List<Object[]> ActivityList()throws Exception
	{
		return dao.ActivityList();
	}
	
	@Override
	public Object[] ActivityNameCheck(String activityTypeId, String activityType)throws Exception
	{	
		return dao.ActivityNameCheck(activityTypeId, activityType);
	} 
	@Override
	public long ActivityAddSubmit(MilestoneActivityType model)throws Exception
	{	
		model.setCreatedDate(sdf1.format(new Date()));
		model.setIsActive(1);
		return dao.ActivityAddSubmit(model);
	} 
	
	@Override
	public List<Object[]> GroupsList(String LabCode)throws Exception
	{	
		return dao.GroupsList(LabCode);
	}
	
	@Override
	public List<Object[]> GroupHeadList(String LabCode) throws Exception 
	{
		return dao.GroupHeadList(LabCode);
	}
	
	@Override
	public long GroupAddSubmit(DivisionGroup model) throws Exception 
	{
		model.setCreatedDate(sdf1.format(new Date()));
		model.setIsActive(1);
		return dao.GroupAddSubmit(model);
	}
	
	@Override
	public Object[] GroupAddCheck(String gcode) throws Exception 
	{		
		return dao.GroupAddCheck(gcode); 
	}
	
	@Override
	public Object[] GroupsData(String groupid)throws Exception
	{	
		return dao.GroupsData(groupid); 
	}
	
	
	@Override
	public int GroupMasterUpdate(DivisionGroup model) throws Exception
	{
		model.setModifiedDate(sdf1.format(new Date()));		
		return dao.GroupMasterUpdate(model); 
	}
	
	@Override
	public List<Object[]> LabMasterList() throws Exception
	{
		return dao. LabMasterList();
	}
	@Override
	public List<Object[]> EmployeeList() throws Exception {
		
		return dao.EmployeeList();
	}
	@Override
	public List<Object[]> LabMasterEditData(String LabId) throws Exception {

		
		return dao.LabMasterEditData(LabId);
	}
	
	
	@Override
	public int LabMasterUpdate(LabMasterAdd labmasteredit, String Userid) throws Exception {
		logger.info(new Date() +" Inside SERVICE LabMasterUpdate ");
		LabMaster labmaster= new LabMaster();
		labmaster.setLabCode(labmasteredit.getLabCode());
		labmaster.setLabName(labmasteredit.getLabName());
		labmaster.setLabUnitCode(labmasteredit.getLabUnitCode());
		labmaster.setLabAddress(labmasteredit.getLabAddress());
		labmaster.setLabCity(labmasteredit.getLabCity());
		labmaster.setLabPin(labmasteredit.getLabPin());
		labmaster.setLabMasterId(Integer.parseInt(labmasteredit.getLabMasterId()));
		labmaster.setLabTelNo(labmasteredit.getLabTelephone());
		labmaster.setLabFaxNo(labmasteredit.getLabFaxNo());
		labmaster.setLabEmail(labmasteredit.getLabEmail());
		labmaster.setLabAuthority(labmasteredit.getLabAuthority());
		labmaster.setLabAuthorityId(Long.parseLong(labmasteredit.getLabAuthorityId()));
		labmaster.setLabRfpEmail(labmasteredit.getLabRfpEmail());
		labmaster.setLabId(Long.parseLong(labmasteredit.getLabId()));
		labmaster.setClusterId(Long.parseLong(labmasteredit.getClusterId()));
		labmaster.setModifiedBy(Userid);
		labmaster.setModifiedDate(sdf1.format(new Date()));
//		labmaster.setLabLogo(labmasteredit.getLabLogo());
		
		return dao.LabMasterUpdate(labmaster);
	}
	
	@Override
	public List<Object[]> LabsList() throws Exception 
	{		
		return dao.LabsList();
	}
	
	@Override
	public List<Object[]> empNoCheckAjax(String empno)throws Exception
	{	
		return dao.empNoCheckAjax(empno);
	}
	
	@Override
	public List<Object[]> extEmpNoCheckAjax(String empno)throws Exception
	{	
		return dao.extEmpNoCheckAjax(empno);
	}
	
	@Override
	public Long FeedbackInsert(PfmsFeedback feedback ,MultipartFile FileAttach , String LabCode) throws Exception {
		logger.info(new Date() +" Inside SERVICE FeedbackInsert ");
		
		Long feedbackid = dao.FeedbackInsert(feedback);
		if(!FileAttach.isEmpty()) {
			Timestamp instant= Timestamp.from(Instant.now());
			String timestampstr = instant.toString().replace(" ","").replace(":", "").replace("-", "").replace(".","");
			String Path = LabCode+"\\Feedback\\";
			PfmsFeedbackAttach attach = new PfmsFeedbackAttach();
			attach.setFeedbackId(feedbackid);
			attach.setPath(Path);
			attach.setCreatedBy(feedback.getCreatedBy());
			attach.setCreatedDate(feedback.getCreatedDate());
			attach.setIsActive(1);
			attach.setFileName("Feedback"+timestampstr+"."+FilenameUtils.getExtension(FileAttach.getOriginalFilename()));
			dao.FeedbackAttachInsert(attach);
			saveFile(uploadpath+Path, attach.getFileName(), FileAttach);
		}
		return feedbackid;
	}
	  public static void saveFile(String uploadpath, String fileName, MultipartFile multipartFile) throws IOException 
	    {
	    	logger.info(new Date() +"Inside SERVICE saveFile ");
	        Path uploadPath = Paths.get(uploadpath);
	          
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        
	        try (InputStream inputStream = multipartFile.getInputStream()) {
	            Path filePath = uploadPath.resolve(fileName);
	            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	        } catch (IOException ioe) {       
	            throw new IOException("Could not save image file: " + fileName, ioe);
	        }     
	    }
	@Override
	public List<Object[]> FeedbackList(String fromdate , String todate , String empid ,String LabCode,String feedbacktype) throws Exception{
		
		String FromDate = new FormatConverter().RegularToSqlDate(fromdate);
		String ToDate = new FormatConverter().RegularToSqlDate(todate);
		return dao.FeedbackList(FromDate,ToDate,empid ,LabCode,feedbacktype);
	}
	@Override
	public List<Object[]> GetfeedbackAttch()throws Exception
	{
		return dao.GetfeedbackAttch();
	}
	@Override
	public List<Object[]> GetfeedbackAttchForUser(String empid)throws Exception
	{
		return dao.GetfeedbackAttchForUser(empid);
	}
	@Override
	public List<Object[]> FeedbackListForUser(String LabCode , String empid) throws Exception
	{
		return dao.FeedbackListForUser(LabCode , empid);
	}
	@Override
	public Object[] FeedbackContent(String feedbackid)throws Exception
	{	
		return dao.FeedbackContent(feedbackid);
	}
	
	@Override
	public PfmsFeedback getPfmsFeedbackById(String feedbackId) {
		
		return dao.getPfmsFeedbackById(feedbackId);
	}
	
	@Override
	public Long addPfmsFeedback(PfmsFeedback feedback) throws Exception {

		return dao.FeedbackInsert(feedback);
	}
	
	@Override
	
	public PfmsFeedbackAttach FeedbackAttachmentDownload(String achmentid) throws Exception {
		return dao.FeedbackAttachmentDownload(achmentid);
	}

	@Override
	public List<Object[]> TDList(String LabCode) throws Exception {
		
		return dao.TDList(LabCode);
	}
	
	@Override
	public List<Object[]> TDHeadList(String LabCode) throws Exception 
	{
		return dao.TDHeadList(LabCode);
	}
	
	@Override
	public Object[]  TDsData(String tdid)throws Exception
	{	
		return dao.TDsData(tdid); 
	}

	@Override
	public long TDAddSubmit(DivisionTd dtd) throws Exception {
		
		dtd.setCreatedDate(sdf1.format(new Date()));
		dtd.setIsActive(1);
		return dao.TDAddSubmit(dtd);
	}

	@Override
	public Object[] TDAddCheck(String tCode) throws Exception {
		
		return dao.TDAddCheck(tCode);
	}
	
	@Override
	public List<Object[]> checkGroupMasterCode(String TdCode) throws Exception {
		
		return dao.CheckGroupMasterCode(TdCode);
	}
	
	
	@Override
	public int TDMasterUpdate(DivisionTd model) throws Exception {
		model.setModifiedDate(sdf1.format(new Date()));		
		return dao.TDMasterUpdate(model);
	}

	@Override
	public List<Object[]> TDListAdd() throws Exception {
		
		return dao.TDListAdd();
	}
	@Override
	public int UpdateActivityType(String activityType, String activityTypeId, String isTimeSheet, String activityCode) throws Exception {
		
		return dao.UpdateActivityType(activityType, activityTypeId, isTimeSheet, activityCode);
	}

	@Override
	public int DeleteActivityType(String ActivityId) throws Exception {
		
		return dao.DeleteActivityType(ActivityId);
	}

	@Override
	public List<Object[]> industryPartnerList() throws Exception {
		
		return dao.industryPartnerList();
	}
	
	@Override
	public Object[] industryPartnerDetailsByIndustryPartnerRepId(String industryPartnerRepId) throws Exception {
		
		return dao.industryPartnerDetailsByIndustryPartnerRepId(industryPartnerRepId);
	}

	@Override
	public List<IndustryPartner> getIndustryPartnerList() throws Exception {
		
		return dao.getIndustryPartnerList();
	}

	@Override
	public IndustryPartner getIndustryPartnerById(String industryPartnerId) throws Exception {
		
		return dao.getIndustryPartnerById(industryPartnerId);
	}

	@Override
	public IndustryPartnerRep getIndustryPartnerRepById(String industryPartnerRepId) throws Exception {
		
		return dao.getIndustryPartnerRepById(industryPartnerRepId);
	}

	@Override
	public long addIndustryPartner(IndustryPartner partner) throws Exception {
		
		return dao.addIndustryPartner(partner);
	}

	@Override
	public int revokeIndustryPartnerRep(String industryPartnerRepId) throws Exception {
		
		return dao.revokeIndustryPartnerRep(industryPartnerRepId);
	}
	@Override
	public List<Object[]> industryPartnerRepDetails(String industryPartnerId, String industryPartnerRepId) throws Exception {
		
		return dao.industryPartnerRepDetails(industryPartnerId, industryPartnerRepId);
	}
	
	//-----------------------------------------------------------------------------------------
	 //holiday lsit----
		
		@Override
		public List<Object[]> HolidayList( String yr) throws Exception {
			// TODO Auto-generated method stub
			return dao.HolidayList(yr);
		}
		
		@Override
		public long HolidayDelete(HolidayMaster holiday, String userId) throws Exception {
			
			HolidayMaster hw =dao.getHolidayData(holiday.getHolidayId());
			hw.setIsActive(0);
			return dao.HolidayEditSubmit(hw);
		}
		
		@Override
		public long HolidayAddSubmit(HolidayMaster holiday) throws Exception {
			
			return dao.HolidayAddSubmit(holiday);
		}

		@Override
		public long HolidayEditSubmit(HolidayMaster holiday, String userId) throws Exception {
			HolidayMaster hw =dao.getHolidayData(holiday.getHolidayId());
			hw.setHolidayDate(holiday.getHolidayDate());
			hw.setHolidayName(holiday.getHolidayName());
			hw.setHolidayType(holiday.getHolidayType());
			hw.setModifiedBy(userId);
			hw.setModifiedDate(fc.getSqlDateAndTimeFormat().format(new Date()));
			hw.setIsActive(1);
			
			return dao.HolidayEditSubmit(hw);
		}
		

		@Override
		public HolidayMaster getHolidayData(Long holidayid) throws Exception {
			
			return dao.getHolidayData(holidayid);  
		}
		
		
		@Override
		public List<Object[]> labPmsEmployeeList(String LabCode) throws Exception {
			return dao.labPmsEmployeeList(LabCode);
		}
		
		
		@Override
		public long LabPmsEmployeeUpdate(String[] labPmsEmpId, String userName,String LabCode) throws Exception {
			long status=0;
			try
			{
				for(int i=0;i<labPmsEmpId.length;i++)
				{
					if(labPmsEmpId[i]!=null && labPmsEmpId[i].split("#")!=null)
					{
						long DesignationId=0;
						System.out.println("labPmsEmpId["+i+"]*****"+labPmsEmpId[i]);
						LabPmsEmployee labemployee=dao.labemployeefindbyEmpId(Long.parseLong(labPmsEmpId[i].split("#")[0].toString()));
						Object[] getDesigId=dao.getDesigId(labemployee.getDesignation());
						System.out.println("getDesigId:"+getDesigId);
						if(getDesigId==null) {
							EmployeeDesig model=new EmployeeDesig();
							model.setDesigCode("-");
							model.setDesignation(labemployee.getDesignation());
							model.setDesigLimit(100000);
							model.setDesigCadre("Others");
							
							DesignationId=admindao.DesignationAddSubmit(model);
						}else {
							DesignationId=Long.parseLong(getDesigId[0].toString());
						}
						
						if(labPmsEmpId[i].split("#")[0]!=null && labPmsEmpId[i].split("#")[1].equalsIgnoreCase(" ") && labPmsEmpId[i].split("#")[2].equalsIgnoreCase(" ")) {
							Employee employee= new Employee();
							employee.setEmpNo(labemployee.getPcNo().toString());
							employee.setEmpName(labemployee.getName());
							employee.setDesigId(DesignationId);
							if(labemployee.getInternalPhoneNo()!=null) {
							employee.setExtNo(labemployee.getInternalPhoneNo());
							}
							if(labemployee.getLrdeMail()!=null) {
							employee.setEmail(labemployee.getLrdeMail());
							}
							if(labemployee.getDronaMail()!=null) {
							employee.setDronaEmail(labemployee.getDronaMail());
							}
							if(labemployee.getInternetMail()!=null) {
							employee.setInternetEmail(labemployee.getInternetMail());
							}
							if(labemployee.getMobileNo()!=null) {
							employee.setMobileNo(Long.parseLong(labemployee.getMobileNo()));
							}
							employee.setCreatedBy(userName);
							employee.setCreatedDate(sdf1.format(new Date()));
							employee.setIsActive(1);
							employee.setSrNo(0l);
							employee.setLabCode(LabCode);
							status= dao.EmployeeMasterInsert(employee);
						}else if(labPmsEmpId[i].split("#")[0]!=null && labPmsEmpId[i].split("#")[1]!=null && labPmsEmpId[i].split("#")[2]!=null) {
							if(labemployee.getPcNo()!=null && labemployee.getPcNo().toString().equalsIgnoreCase(labPmsEmpId[i].split("#")[2].toString())) {
								Employee employee=dao.getEmployeeById(labPmsEmpId[i].split("#")[1].toString());
								employee.setDesigId(DesignationId);
								if(labemployee.getInternalPhoneNo()!=null) {
								employee.setExtNo(labemployee.getInternalPhoneNo());
								}
								if(labemployee.getMobileNo()!=null) {
								employee.setMobileNo(Long.parseLong(labemployee.getMobileNo()));
								}
								if(labemployee.getLrdeMail()!=null) {
								employee.setEmail(labemployee.getLrdeMail());
								}
								if(labemployee.getInternetMail()!=null) {
								employee.setInternetEmail(labemployee.getInternetMail());
								}
								if(labemployee.getDronaMail()!=null) {
								employee.setDronaEmail(labemployee.getDronaMail());
								}
								employee.setModifiedBy(userName);
								employee.setModifiedDate(sdf1.format(new Date()));
								
								status=dao.updateEmployee(employee) ;
							}
						}
					}
				}
			}catch(Exception e)
			{
				logger.error(new Date() +"Inside DAO LabPmsEmployeeUpdate() "+ e);
				e.printStackTrace();
				
			}
			return status;
		}
		
		@Override
		public List<Object[]> getEmployees() throws Exception {
			return dao.getEmployees();
		}
		
		@Override
		public PfmsEmpRoles getPfmsEmpRolesById(String roleid) throws Exception {
			// TODO Auto-generated method stub
			return dao.getPfmsEmpRolesById(roleid);
		}
		
		@Override
		public long addPfmsEmpRoles(PfmsEmpRoles pf) throws Exception {
			return dao.addPfmsEmpRoles(pf);
		}
		
		@Override
		public List<Object[]> checkDivisionMasterId(String groupId) throws Exception {
			return dao.checkDivisionMasterId(groupId);
		}
		
		@Override
		public List<Object[]> getFeedbackTransByFeedbackId(String feedbackId) throws Exception {
			
			return dao.getFeedbackTransByFeedbackId(feedbackId);
		}
		
		@Override
		public long addPfmsFeedbackTrans(PfmsFeedbackTrans transaction) throws Exception {
			
			return dao.addPfmsFeedbackTrans(transaction);
		}
		
		/* **************************** Programme Master - Naveen R  - 16/07/2025 **************************************** */
		@Override
		public List<Object[]> getProgramMasterList() throws Exception {
			
			return dao.getProgramMasterList();
		}

		@Override
		public long addProgrammeMaster(ProgrammeMaster master) throws Exception {
			
			return dao.addProgrammeMaster(master);
		}

		@Override
		public int removeProjectsLinked(String programmeId) throws Exception {
			
			return dao.removeProjectLinked(programmeId);
		}

		@Override
		public long addProgrammeProjects(ProgrammeProjects linked) throws Exception {
			
			return dao.addProgrammeProjects(linked);		
		}

		@Override
		public Long ProgramCodeCheck(String programmeCode, String prgrammeId) throws Exception {
			
			return dao.ProgramCodeCheck(programmeCode,prgrammeId);
		}
		
		@Override
		public List<Object[]> getProjectList(String labcode) throws Exception {
			return dao.getProjectList(labcode);
		}
		
		@Override
		public List<Object[]> getProjectList(String labcode, String programmeId) throws Exception {
			return dao.getProjectList(labcode,programmeId);
		}
		/* **************************** Programme Master - Naveen R  - 16/07/2025 End**************************************** */

		@Override
		public Long addRoleMaster(RoleMaster roleMaster) throws Exception {
			
			return dao.addRoleMaster(roleMaster);
		}

		// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check start
		
		@Override
		public Long getRoleNameDuplicateCount(String roleName) throws Exception {
			
			return dao.getRoleNameDulicateCount(roleName);
		}

		@Override
		public Long getRoleCodeDuplicateCount(String roleCode) throws Exception {
			
			return dao.getRoleCodeDuplicateCount(roleCode);
		}

		@Override
		public List<LabMaster> getAllLabMaster() throws Exception {
			return dao.getAllLabMaster();
		}

		

		
		
		// 22/8/2025  Naveen R RoleName and RoleCode Duplicate Check End
}

