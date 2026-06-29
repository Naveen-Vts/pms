package com.vts.pfms.roadmap.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.project.model.ProjectMaster;
import com.vts.pfms.roadmap.model.AnnualTargets;
import com.vts.pfms.roadmap.model.RoadMap;
import com.vts.pfms.roadmap.model.RoadMapAnnualTargets;

@Service
@Transactional
public class RoadMapDaoImpl implements RoadMapDao{

	private static final Logger logger = LogManager.getLogger(RoadMapDaoImpl.class);
	
	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf = fc.getSqlDateFormat();
	private SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdf2=new SimpleDateFormat("dd-MMM-yyyy");
	
	@PersistenceContext
	EntityManager manager;
	
	
	private static final String ROADMAPLIST = "SELECT a.RoadMapId,a.InitiationDate,a.RoadMapType,a.ProjectId,a.InitiationId,a.DivisionId,a.ProjectTitle,a.AimObjectives,a.StartDate,a.EndDate,a.Duration,a.Reference,a.Scope,'' AS DivisionName,a.MovedToASP,c.RoadMapStatus,c.RoadMapStatusColor,c.RoadMapStatusCode FROM pfms_road_map a,pfms_road_map_status c WHERE a.IsActive=1 AND a.RoadMapStatusCode=c.RoadMapStatusCode ORDER BY a.RoadMapId DESC";
	@Override
	public List<Object[]> roadMapList() throws Exception {
		try {
			Query query = manager.createNativeQuery(ROADMAPLIST);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO roadMapList "+e);
			return new ArrayList<>();
		}

	}
	
	private static final String DIVISIONLIST = "SELECT division_id,division_code,division_name FROM division_master WHERE is_active=1 AND lab_code=:LabCode";
	@Override
	public List<Object[]> divisionList(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(DIVISIONLIST);
			query.setParameter("LabCode", labCode);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO divisionList "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String PROJECTLIST = "SELECT project_id,project_code,project_short_name,project_name,objective,TIMESTAMPDIFF(MONTH,sanction_date,pdc) AS Duration FROM project_master WHERE is_active=1 AND lab_code=:LabCode";
	@Override
	public List<Object[]> getProjectList(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTLIST);
			query.setParameter("LabCode", labCode);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO projectList "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String GETPREPROJECTLIST = "SELECT a.InitiationId,a.ProjectProgramme,a.ProjectShortName,a.ProjectTitle,'' AS Objective,ProjectDuration FROM pfms_initiation a WHERE a.IsActive AND a.LabCode=:LabCode";
	@Override
	public List<Object[]> getPreProjectList(String labcode) throws Exception {
		try {
			Query query = manager.createNativeQuery(GETPREPROJECTLIST);
			query.setParameter("LabCode", labcode);
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getPreProjectList "+e);
			return new ArrayList<>();
		}
	}
	
	private static final String GETPROJECTDETAILS= "SELECT ProjectId,ProjectCode,ProjectShortName,ProjectName,DivisionId,SanctionDate,PDC,Objective,Scope,BoardReference,TotalSanctionCost FROM project_master WHERE IsActive=1 AND LabCode=:LabCode AND ProjectId=:ProjectId";
	private static final String GETPREPROJECTDETAILS= "SELECT InitiationId,ProjectProgramme,ProjectShortName,ProjectTitle,DivisionId,'Sanction Date','PDC','Objective','Scope','BoardReference',ProjectCost FROM pfms_initiation WHERE IsActive=1 AND LabCode=:LabCode AND InitiationId=:ProjectId";
	@Override
	public Object[] getProjectDetails(String labcode,String projectId,String roadMapType) throws Exception {
		try {
			
			Query query = manager.createNativeQuery(roadMapType.equalsIgnoreCase("E")?GETPROJECTDETAILS:GETPREPROJECTDETAILS);
			query.setParameter("LabCode", labcode);
			query.setParameter("ProjectId", Long.parseLong(projectId));
			return (Object[])query.getSingleResult();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectDetails "+e);
			return null;
		}
	}
	
	@Override
	public RoadMap getRoadMapDetailsById(String roadMapId) throws Exception {
		try {
			
			return manager.find(RoadMap.class, Long.parseLong(roadMapId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRoadMapDetails "+e);
			return null;
		}
	}
	
	@Override
	public List<RoadMapAnnualTargets> getRoadMapAnnualTargetDetails(String roadMapId) throws Exception {
		try {
			Query query = manager.createQuery("FROM RoadMapAnnualTargets WHERE IsActive=1 AND RoadMapId=:RoadMapId");
			query.setParameter("RoadMapId", Long.parseLong(roadMapId));
			return (List<RoadMapAnnualTargets>)query.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRoadMapAnnualTargetDetails "+e);
			return null;
		}
	}
	
	@Override
	public long addRoadMapDetails(RoadMap roadMap) throws Exception {
		try {
			manager.persist(roadMap);
			manager.flush();
			return roadMap.getRoadMapId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getRoadMapAnnualTargetDetails "+e);
			return 0L;
		}
	}
	
	private static final String REMOVEROADMAPANNUALTARGETS ="UPDATE pfms_road_map_annual_targets SET IsActive=0 WHERE RoadMapId=:RoadMapId";
	@Override
	public int removeRoadMapAnnualTargets(String roadMapId) throws Exception {
		try {
			Query query = manager.createNativeQuery(REMOVEROADMAPANNUALTARGETS);
			query.setParameter("RoadMapId", Long.parseLong(roadMapId));
			return query.executeUpdate();
		}catch (Exception e) {
			logger.error(new Date()+" Inside RoadMapDaoImpl removeRoadMapAnnualTargets()"+e);
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeRoadMapDetails(String roadMapId, String userId) throws Exception {
		RoadMap roadMap = manager.find(RoadMap.class, Long.parseLong(roadMapId));
		if (roadMap == null) {
			return 0;
		}
		try {
			roadMap.setIsActive(0);
			roadMap.setModifiedBy(userId);
			roadMap.setModifiedDate(sdtf.format(new Date()));
			return 1;
		}catch (Exception e) {
			logger.error(new Date()+" Inside RoadMapDaoImpl removeRoadMapDetails()"+e);
			e.printStackTrace();
			return 0;
		}
	}
	
//	private static final String ROADMAPDETAILSMOVETOASP="UPDATE pfms_road_map SET MovedToASP='Y',MovedToASPDate=:MovedToASPDate,ModifiedBy=:ModifiedBy,ModifiedDate=:ModifiedDate WHERE RoadMapId=:RoadMapId";
//	@Override
//	public int roadMapDetailsMoveToASP(String roadMapId, String userId) throws Exception {
//		try {
//			Query query = manager.createNativeQuery(ROADMAPDETAILSMOVETOASP);
//			query.setParameter("RoadMapId", roadMapId);
//			query.setParameter("MovedToASPDate", sdf.format(new Date()));
//			query.setParameter("ModifiedBy", userId);
//			query.setParameter("ModifiedDate", sdtf.format(new Date()));
//			return query.executeUpdate();
//		}catch (Exception e) {
//			logger.error(new Date()+" Inside RoadMapDaoImpl roadMapDetailsMoveToASP()"+e);
//			e.printStackTrace();
//			return 0;
//		}
//	}
	
	private static final String ROADMAPAPPROVALDATA = "SELECT a.RoadMapTransId,\r\n"
			+ "	(SELECT emp_no FROM pfms_road_map_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.RoadMapStatusCode =  b.RoadMapStatusCode AND t.RoadMapId=d.RoadMapId ORDER BY t.RoadMapTransId DESC LIMIT 1) AS 'empno',\r\n"
			+ "	(SELECT emp_name FROM pfms_road_map_trans t , employee e  WHERE e.emp_id = t.ActionBy AND t.RoadMapStatusCode =  b.RoadMapStatusCode AND t.RoadMapId=d.RoadMapId ORDER BY t.RoadMapTransId DESC LIMIT 1) AS 'empname',\r\n"
			+ "	(SELECT des.designation FROM pfms_road_map_trans t , employee e,employee_desig des WHERE e.emp_id = t.ActionBy AND e.desig_id=des.desig_id AND t.RoadMapStatusCode =  b.RoadMapStatusCode AND t.RoadMapId=d.RoadMapId ORDER BY t.RoadMapTransId DESC LIMIT 1) AS 'Designation',\r\n"
			+ "	MAX(a.ActionDate) AS ActionDate,a.Remarks,b.RoadMapStatus,b.RoadMapStatusColor,b.RoadMapStatusCode \r\n"
			+ "	FROM pfms_road_map_trans a,pfms_road_map_status b,employee c,pfms_road_map d\r\n"
			+ "	WHERE d.RoadMapId=a.RoadMapId AND a.RoadMapStatusCode =b.RoadMapStatusCode AND a.Actionby=c.emp_id AND d.RoadMapId=:RoadMapId GROUP BY b.RoadMapStatusCode,b.RoadMapStatus,b.RoadMapStatusColor,a.RoadMapTransId,a.Remarks ORDER BY a.RoadMapTransId ASC";
	@Override
	public List<Object[]> roadMapTransApprovalData(String roadMapId) {

		try {
			Query query = manager.createNativeQuery(ROADMAPAPPROVALDATA);
			query.setParameter("RoadMapId", Long.parseLong(roadMapId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO roadMapTransApprovalData "+e);
			e.printStackTrace();
			return null;
		}

	}
	
	private static final String ROADMAPTRANSLIST = "SELECT a.RoadMapTransId,c.EmpNo,c.EmpName,d.Designation,a.ActionDate,a.Remarks,b.RoadMapStatus,b.RoadMapStatusColor FROM pfms_road_map_trans a,pfms_road_map_status b,employee c,employee_desig d,pfms_road_map e WHERE e.RoadMapId = a.RoadMapId AND a.RoadMapStatusCode = b.RoadMapStatusCode AND a.ActionBy=c.EmpId AND c.DesigId = d.DesigId AND e.RoadMapId=:RoadMapId ORDER BY a.RoadMapTransId DESC";
	@Override
	public List<Object[]> roadMapTransList(String roadMapId) throws Exception {

		try {
			Query query = manager.createNativeQuery(ROADMAPTRANSLIST);
			query.setParameter("RoadMapId", Long.parseLong(roadMapId));
			return (List<Object[]>)query.getResultList();
		}catch (Exception e) {
			logger.info(new Date()+"Inside DAO roadMapTransList "+e);
			e.printStackTrace();
			return null;
		}

	}
	
	private static final String ROADMAPREMARKSHISTORY = "SELECT a.RoadMapId,a.Remarks,b.RoadMapStatusCode,e.emp_name,d.Designation FROM pfms_road_map_trans a,pfms_road_map_status b,pfms_road_map c,employee_desig d,employee e WHERE a.ActionBy = e.emp_id AND e.desig_id = d.desig_id AND b.RoadMapStatusCode = a.RoadMapStatusCode AND c.RoadMapId = a.RoadMapId AND TRIM(a.Remarks)<>'' AND c.RoadMapId=:RoadMapId ORDER BY a.ActionDate ASC";
	@Override
	public List<Object[]> roadMapRemarksHistory(String roadMapId) throws Exception
	{
		List<Object[]> list =new ArrayList<Object[]>();
		try {
			Query query= manager.createNativeQuery(ROADMAPREMARKSHISTORY);
			query.setParameter("RoadMapId", Long.parseLong(roadMapId));
			list= (List<Object[]>)query.getResultList();

		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO roadMapRemarksHistory " + e);
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String ROADMAPPENDINGLIST  ="CALL pfms_road_map_pending(:EmpId,:LabCode);";
	@Override
	public List<Object[]> roadMapPendingList(String empId, String labCode) throws Exception {
		try {			
			Query query= manager.createNativeQuery(ROADMAPPENDINGLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("LabCode", labCode);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO roadMapPendingList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}

	}

	private static final String ROADMAPAPPROVEDLIST  ="CALL pfms_road_map_approved(:EmpId,:FromDate,:ToDate);";
	@Override
	public List<Object[]> roadMapApprovedList(String empId, String fromDate, String toDate) throws Exception {

		try {			
			Query query= manager.createNativeQuery(ROADMAPAPPROVEDLIST);
			query.setParameter("EmpId", Long.parseLong(empId));
			query.setParameter("FromDate", fromDate);
			query.setParameter("ToDate", toDate);
			List<Object[]> list =  (List<Object[]>)query.getResultList();
			return list;
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO roadMapApprovedList " + e);
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}
	
	@Override
	public List<RoadMap> getRoadMapList() throws Exception {
		try {
			Query query = manager.createQuery("FROM RoadMap WHERE IsActive=1 ORDER BY FIELD(RoadMapType, 'E','P','N') ASC");
			return (List<RoadMap>)query.getResultList();
		}catch (Exception e) {
			logger.error(new Date()  + "Inside DAO getRoadMapList " + e);
			e.printStackTrace();
			return new ArrayList<RoadMap>();
		}
	}
	
	private static final String ROADMAPASPLIST = "SELECT a.RoadMapId,a.InitiationDate,a.RoadMapType,a.ProjectId,a.InitiationId,a.DivisionId,a.ProjectTitle,a.AimObjectives,a.StartDate,a.EndDate,a.Duration,a.Reference,a.Scope,b.division_name,a.MovedToASP,c.RoadMapStatus,c.RoadMapStatusColor,c.RoadMapStatusCode FROM pfms_road_map a,division_master b,pfms_road_map_status c WHERE a.IsActive=1 AND a.DivisionId=b.division_id AND a.RoadMapStatusCode=c.RoadMapStatusCode AND a.MovedToASP='Y' ORDER BY a.RoadMapId DESC";
	@Override
	public List<Object[]> roadMapASPList() throws Exception {
		try {
			Query query = manager.createNativeQuery(ROADMAPASPLIST);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO roadMapASPList "+e);
			return new ArrayList<>();
		}

	}
	
	@Override
	public List<AnnualTargets> getAnnualTargetsFromMaster() throws Exception {
		try {
			Query query = manager.createQuery("FROM AnnualTargets WHERE IsActive=1");
			return (List<AnnualTargets>)query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getAnnualTargetsFromMaster "+e);
			return new ArrayList<AnnualTargets>();
		}
	}
	
	@Override
	public AnnualTargets getAnnualTargetsById(String annualTargetId) throws Exception {
		try {
			return manager.find(AnnualTargets.class, Long.parseLong(annualTargetId));
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getAnnualTargetsById "+e);
			return null;
		}
	}
	
	@Override
	public Long addAnnualTargets(AnnualTargets targets) throws Exception {
		try {
			manager.persist(targets);
			manager.flush();
			return targets.getAnnualTargetId();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO addAnnualTargets "+e);
			return 0L;
		}
	}
	
	private static final String PROJECTMILESTONEACTIVITYLIST = "SELECT a.project_id,b.ActivityName,b.StartDate,b.EndDate FROM project_master a,milestone_activity b WHERE a.project_id=b.ProjectId AND a.is_active=1 AND b.IsActive=1 AND a.lab_code=:LabCode ORDER BY b.StartDate";
	@Override
	public List<Object[]> getProjectMilestoneActivityList(String labCode) throws Exception {
		try {
			Query query = manager.createNativeQuery(PROJECTMILESTONEACTIVITYLIST);
			query.setParameter("LabCode", labCode);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getProjectMilestoneActivityList "+e);
			return new ArrayList<>();
		}
		
	}
	
	private static final String PREPROJECTMILESTONEACTIVITYLIST = "SELECT InitiationId,MilestoneActivity,StartDate,EndDate FROM pfms_initiation_schedule WHERE IsActive=1 ORDER BY StartDate";
	@Override
	public List<Object[]> getPreProjectMilestoneActivityList() throws Exception {
		try {
			Query query = manager.createNativeQuery(PREPROJECTMILESTONEACTIVITYLIST);
			return (List<Object[]>) query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getPreProjectMilestoneActivityList "+e);
			return new ArrayList<>();
		}
		
	}
}
