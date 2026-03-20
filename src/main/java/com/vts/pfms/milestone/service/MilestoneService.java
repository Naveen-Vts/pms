package com.vts.pfms.milestone.service;

import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import com.vts.pfms.committee.model.ActionAssign;
import com.vts.pfms.milestone.dto.FileDocAmendmentDto;
import com.vts.pfms.milestone.dto.FileProjectDocDto;
import com.vts.pfms.milestone.dto.FileUploadDto;
import com.vts.pfms.milestone.dto.MileEditDto;
import com.vts.pfms.milestone.dto.MilestoneActivityDto;
import com.vts.pfms.milestone.dto.MilestoneScheduleDto;
import com.vts.pfms.milestone.model.FileDocMaster;
import com.vts.pfms.milestone.model.FileRepMasterPreProject;
import com.vts.pfms.milestone.model.FileRepNew;
import com.vts.pfms.milestone.model.FileRepUploadNew;
import com.vts.pfms.milestone.model.FileRepUploadPreProject;
import com.vts.pfms.milestone.model.MilestoneActivity;
import com.vts.pfms.milestone.model.MilestoneActivityLevel;
import com.vts.pfms.milestone.model.MilestoneActivityLevelRemarks;
import com.vts.pfms.milestone.model.MilestoneActivityPredecessor;
import com.vts.pfms.milestone.model.MilestoneActivitySub;

public interface MilestoneService {
	public List<Object[]> MilestoneActivityList(String ProjectId) throws Exception;
	public List<Object[]> ProjectList() throws Exception ;
	public List<Object[]> EmployeeList() throws Exception;
	public long MilestoneActivityInsert(MilestoneActivityDto dto)throws Exception;
	public List<Object[]> MilestoneActivity(String MilestoneActivityId) throws Exception;
	public long MilestoneActivityLevelInsert(MilestoneActivityDto dto)throws Exception;
	public List<Object[]> MilestoneActivityLevel(String MilestoneActivityId,String LevelId) throws Exception;
	public int MilestoneRevisionCount(String MileActivityId) throws Exception;
	public int MilestoneActivityAssign(MilestoneActivityDto dto)throws Exception;
	public long MilestoneActivityRevision(MilestoneActivityDto dto)throws Exception;
	public List<Object[]> MilestoneActivityEdit(MileEditDto dto) throws Exception;
	public List<Object[]> ActivityTypeList() throws Exception ;
	public int MilestoneActivityUpdate(MileEditDto dto)throws Exception;
	public List<Object[]> ActivityCompareMAin(String ActivityId,String Rev,String Rev1) throws Exception;
	public List<Object[]> ActivityLevelCompare(String ActivityId,String Rev,String Rev1,String LevelId) throws Exception;
	public List<Object[]> MilestoneActivityEmpIdList(String EmpId) throws Exception;
	public List<Object[]> StatusList() throws Exception;
	public int ActivityProgressUpdate(MileEditDto dto) throws Exception;
	public List<Object[]> MilestoneReportsList(String ProjectId) throws Exception;
	public int MilestoneTotalWeightage(String MilestoneActivityId) throws Exception;
	public List<Object[]> MilestoneActivitySub(MileEditDto dto)throws Exception;
    public com.vts.pfms.milestone.model.MilestoneActivitySub ActivityAttachmentDownload(String ActivitySubId)throws Exception;
    public List<Object[]> ProjectDetails(String ProjectId)throws Exception;
    public List<Object[]> MilestoneActivityAssigneeList(String ProjectId,String EmpId) throws Exception;
	public List<Object[]> ProjectAssigneeList(String EmpId) throws Exception ;
	public List<Object[]> ActionList(String actiontype,String activityid) throws Exception;
	public int WeightageSum(String Id,String ActivityId,String ActivityType,String LevelId)throws Exception;
	public List<Object[]> FileDeatils(String FileId) throws Exception;
	public List<Object[]> MilestoneScheduleList(String ProjectId) throws Exception;
	public long MilestoneScheduleInsert(MilestoneScheduleDto maindto) throws Exception;
	public List<Object[]> MilestoneExcel(String ProjectId)throws Exception;
    public List<Object[]> MainSystem(String projectid)throws Exception;
	public long RepMasterInsert(com.vts.pfms.milestone.model.FileRepMaster fileRepo)throws Exception;
    public List<Object[]> MainSystemLevel(String ParentId)throws Exception;
	public long FileRepMasterSubInsert(com.vts.pfms.milestone.model.FileRepMaster fileRepo)throws Exception;
	public List<Object[]> LoginProjectDetailsList(String empid,String Logintype ,String LabCode) throws Exception;
	public List<Object[]> ProjectEmpList(String projectid ,String Labcode) throws Exception;
	public List<Object[]> AllEmpNameDesigList(String labcode) throws Exception;
	public List<Object[]> ProjectEmpListEdit(String projectid,String id)throws Exception;
	public List<Object[]> DocumentTypeList(String ProjectId,String LabCode) throws Exception ;
	public List<Object[]> DocumentTitleList(String ProjectId,String Sub,String LabCode) throws Exception;
	public List<Object[]> DocumentStageList(String documenttype,String levelid) throws Exception;
	public long FileSubInsertNew(FileRepNew fileRepo)throws Exception;
	public long FileUploadNew(FileUploadDto dto)throws Exception;
	public List<Object[]> VersionCheckList(String ProjectId, String SubsystemL1,String documenttitle) throws Exception;
	public List<Object[]> FileHistoryList(String filerepid) throws Exception;
	public List<Object[]> FileRepMasterListAll(String projectid,String LabCode ) throws Exception;
	public List<Object[]> FileDocMasterListAll(String projectid,String LabCode) throws Exception;
	public long ProjectDocumetsAdd(FileProjectDocDto dto) throws Exception;
	public long FileAmmendUploadNew(FileDocAmendmentDto amenddoc) throws Exception;
	public List<Object[]> DocumentAmendment(String FileRepUploadId) throws Exception;
	public Object[] DocumentAmendmentData(String docammendmentid) throws Exception;
	public Object[] RepMasterData(String filerepmasterid) throws Exception;
	public List<Object[]> RepMasterAllDocLists(String filerepmasterid) throws Exception;
	public List<Object[]> MainSystem1(String filerepmasterid) throws Exception;
	public int fileRepMasterEditSubmit(String filerepmasterid, String levelname, String levelType) throws Exception;
	public List<FileDocMaster> fileDocMasterList(String LabCode) throws Exception;
	public long FileDocMasterAdd(FileDocMaster model) throws Exception;
	public List<FileDocMaster> FileLevelSublevelNameCheck(String levelname,String LabCode) throws Exception;
	public Object[] fileNameCheck(String levelname, String shortname, String docid, String parentlevelid,String LabCode) throws Exception;
	public void excelCellValuesSet(Sheet sheet, Object[] hlo, CellStyle wrapname, int rowcount) throws Exception;
	public int MilestoneRemarkUpdate(MilestoneActivityDto dto)throws Exception;
	public int MileActivityAssignCheck(MilestoneActivityDto dto)throws Exception;
	public long MainMilestoneDOCUpdate(String MainId, String DateOfCompletion, String UserId) throws Exception;


	//prakarsh 
	public void  IsActive(String project, String FileParentId);
	public List<Object[]> FileRepUploadId(String project,String FileParentId);
	public int IsFileInActive(String project, String fileParentId);
	public int DocumentListNameEdit(String filerepmasterid, String levelname);
	//Ms project
	public List<Object[]> getMsprojectTaskList(String projectId)throws Exception;
	public long DocFileUploadAjax(FileUploadDto uploadDto)throws Exception;
	public List<Object[]> getAttachmentId(String projectid)throws Exception;
	public long submitCheckboxFile(String userId, String techDataId, String attachid, String projectid)throws Exception;
	
	public List<Object[]> getMsprojectProcurementStatusList(String projectId) throws Exception;
	public int mileStoneSerialNoUpdate(String[] newslno, String[] milestoneActivityId);
	public List<Object[]> getAllMilestoneActivityList() throws Exception;
	public List<Object[]> getAllMilestoneActivityLevelList() throws Exception;
	
	
	public List<Object[]> getOldFileDocNames(String projectId, String fileType, String fileId)throws Exception;
	public long uploadFileData(FileUploadDto upload, String fileType)throws Exception;
	public List<Object[]> FileRepDocsList(String projectId)throws Exception;
	public Optional<FileRepUploadNew> getFileById(Long id)throws Exception;
	public int getFileRepMasterNames(String projectId, String fileType, String fileId, String fileName) throws Exception;
	public long removeFileAttachment(String projectId, String techDataId, String techAttachId, String userId, String relativePoints) throws Exception;
	public MilestoneActivityLevel getMilestoneActivityLevelById(String milesMainId);
	public long MilestoneActivityLevelSave(com.vts.pfms.milestone.model.MilestoneActivityLevel level1);
	public String getMainLevelId(Long getActivityId)throws Exception;
	
	public void updateMilestoneLevelProgress(MileEditDto dto);
	public String getProjectIdByMainLevelId(String id)throws Exception;
	public MilestoneActivity getMilestoneActivityById(String id);
	public long MilestoneActivitySave(MilestoneActivity activity) throws Exception;
	public List<Object[]> actionAssigneeList(String EmpId) throws Exception;
	public long ActionAssignInsert(ActionAssign assign) throws Exception;

	public int deleteMilsetone(String activityId)throws Exception;


	public List<Object[]> getMilestoneActivityProgressList() throws Exception;
	public List<Object[]> getPreProjectFolderList(String initiationId, String labcode) throws Exception;
	public List<Object[]> getPreProjectSubFolderList(String initiationId, String mainLevelId, String labcode) throws Exception;
	public int getPreProjectFileRepMasterNames(String initiationId, String fileType, String fileId, String fileName) throws Exception;
	public long preProjectRepMasterInsert(FileRepMasterPreProject fileRepo) throws Exception;
	public long preProjectFileRepMasterSubInsert(FileRepMasterPreProject fileRepo) throws Exception;
	public long preProjectfileEditSubmit(String filerepmasterid, String levelname, String levelType) throws Exception;
	public List<Object[]> getPreProjectOldFileNames(String initiationId, String fileType, String fileId)throws Exception;
	public long uploadPreProjectFile(FileUploadDto upload, String fileType)throws Exception;
	public Optional<FileRepUploadPreProject> getPreProjectFileById(Long id)throws Exception;
	public List<Object[]> preProjectFileRepDocsList(String fileRepId)throws Exception;
	public List<Object[]> getPreProjectFileRepMasterListAll(String initiationId, String labCode)throws Exception;

	
	public int saveMilestoneActivityLevelRemarks(MilestoneActivityLevelRemarks cmd)throws Exception;
	public List<Object[]> getMilestoneDraftRemarks(Long activityId)throws Exception;
	
	public Long saveMilestoneActivityPredecessor(MilestoneActivityPredecessor mp)throws Exception;
	public List<Object[]> predecessorList(String successor)throws Exception;
	public List<Object[]> getsuccessorList(String activityId)throws Exception;
	public int deleteMilestoneActivityPredecessor(String successor)throws Exception;
	public long saveMilestoneSub(MilestoneActivitySub attach)throws Exception;
	public Object[] getprojectDetails(String projectId)throws Exception;
	public Object[] getFileRepUploadDetails(String projectId)throws Exception;
}
