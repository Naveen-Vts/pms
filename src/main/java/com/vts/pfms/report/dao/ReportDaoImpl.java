package com.vts.pfms.report.dao;

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
import org.springframework.stereotype.Repository;

import com.vts.pfms.FormatConverter;
import com.vts.pfms.report.model.LabReport;
import com.vts.pfms.report.model.PfmsLabReportMilestone;
import com.vts.pfms.roadmap.dao.RoadMapDaoImpl;

@Repository
@Transactional
public class ReportDaoImpl implements ReportDao {
	
	private static final Logger logger = LogManager.getLogger(ReportDaoImpl.class);
	
	FormatConverter fc = new FormatConverter();
	private SimpleDateFormat sdtf = fc.getSqlDateAndTimeFormat();
	private SimpleDateFormat sdf = fc.getSqlDateFormat();
	private SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdf2=new SimpleDateFormat("dd-MMM-yyyy");

	@PersistenceContext
	EntityManager manager;
	
	private static final String prjDetails="SELECT  m.ProjectCategory, m.ProjectShortName,  m.ProjectName, m.ProjectDescription, m.SanctionNo,  m.SanctionDate, ROUND(m.TotalSanctionCost / 100000, 2) AS 'TotalSanctionCost',   ROUND(m.SanctionCostRE / 100000, 2) AS 'SanctionCostRE',   ROUND(m.SanctionCostFE / 100000, 2) AS 'SanctionCostFE', m.Objective,  m.LabParticipating, m.Scope,  c.Category, s.ProjectStageCode,s.ProjectStage, (SELECT COUNT(ScheduleId) FROM committee_schedule WHERE CommitteeId='1' AND ProjectId=:projectid AND  ScheduleFlag IN ('MKV','MMR','MMF','MMS','MMA')) AS PMRB,  (SELECT COUNT(ScheduleId) FROM committee_schedule WHERE CommitteeId='2' AND ProjectId=:projectid AND  ScheduleFlag IN ('MKV','MMR','MMF','MMS','MMA')) AS EB ,p.Brief,p.ImageName,t.ProjectType,CONCAT(p.Path,p.ImageName)AS imgpath FROM  project_master m LEFT JOIN  pfms_category c ON c.CategoryId = m.ProjectCategory LEFT JOIN  pfms_project_data d ON d.ProjectId = m.ProjectId LEFT JOIN  pfms_project_stage s ON s.ProjectStageId = d.CurrentStageId LEFT JOIN pfms_project_slides p ON p.ProjectId = m.ProjectId LEFT JOIN  project_type t ON t.ProjectTypeId = m.ProjectType  WHERE m.ProjectId = :projectid";
	@Override
	public Object[] prjDetails(String projectid) throws Exception {
		try {
			Query query = manager.createNativeQuery(prjDetails);
			query.setParameter("projectid", Long.parseLong(projectid));
			return (Object[]) query.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO prjDetails "+e);
			return null;
		}
	}
	
	private static final String countPrjEntries="SELECT LabReportId,ProjectId FROM pfms_labreport  WHERE ProjectId=:prjId";
	@Override
	public List<Object[]> countPrjEntries(long prjId) throws Exception {
		try {
			Query query = manager.createNativeQuery(countPrjEntries);
			query.setParameter("prjId", prjId);
			// Get single result and cast to long
	        return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO prjDetails "+e);
			return null;
		}
	}
	
	@Override
	public long addData(LabReport lr) throws Exception {
		manager.persist(lr);
		manager.flush();
		return lr.getLabReportId();
	}

	@Override
	public long updateData(LabReport lr) throws Exception {
		manager.merge(lr);
		manager.flush();
		return lr.getLabReportId();
	}

	@Override
	public LabReport getLabReportDetails(String labReportId) throws Exception {
		try
		{
			return manager.find(LabReport.class,Long.parseLong(labReportId));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getLabReportDetails "+e);
			return null;
		}
	}
	private static final String editorData= "SELECT LabReportId,ProjectId,SpinOffData,DetailsofNomination,CurrentYrAchievement,Introduction from pfms_labreport WHERE ProjectId=:ProjectId";
	@Override
	public Object[] editorData(String projectid) throws Exception {
		try {
			Query query = manager.createNativeQuery(editorData);
			query.setParameter("ProjectId", Long.parseLong(projectid));
			
			//return (Object[]) query.getSingleResult();
			// Use getResultList instead of getSingleResult to avoid NoResultException
	        List<Object[]> result = query.getResultList();
	        
	        // Check if the result is not empty and return the first entry
	        if (!result.isEmpty()) {
	            return result.get(0);
	        } else {
	            
	            return null;
	        }
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO editorData "+e);
			return null;
		}
	}

	private static final String mileStoneData="SELECT a.ProjectId,b.ActivityName,b.StartDate,b.EndDate ,'2024' AS PlanActivityYear,b.DateOfCompletion,YEAR(b.DateOfCompletion) AS 'completedYear',YEAR(b.StartDate) AS 'StartYear',YEAR(b.EndDate) AS 'EndYear',b.MilestoneActivityId,b.MilestoneNo,b.ProgressStatus  FROM project_master a,milestone_activity b WHERE a.ProjectId=b.ProjectId AND a.IsActive=1 AND b.IsActive=1 AND a.ProjectId=:prjid ";
	@Override
	public List<Object[]> mileStoneData(int currentYear,String projectid) throws Exception {
		try {
			Query query = manager.createNativeQuery(mileStoneData);
			query.setParameter("prjid", Long.parseLong(projectid));
			/*
			 * query.setParameter("currentYear", currentYear); query.setParameter("nextyr",
			 * (currentYear+1));
			 */
			return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO mileStoneData "+e);
			return null;
		}
		
		
	}	
		
@Override
public long LabReportMilestone(PfmsLabReportMilestone pm) throws Exception {
try {
	if(pm.getIsChecked().equalsIgnoreCase("1")) {
		
		String sql ="DELETE FROM  pfms_labreport_milestone WHERE MilestoneActivityId=:MilestoneActivityId AND ProjectId=:ProjectId AND ActivityFor=:ActivityFor";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("MilestoneActivityId", pm.getMilestoneActivityId());
		query.setParameter("ProjectId", pm.getProjectId());
		query.setParameter("ActivityFor", pm.getActivityFor());
			query.executeUpdate();
		
		manager.persist(pm);
		manager.flush();
		return pm.getMilestoneActivityId();
	}else {
		String sql ="DELETE FROM  pfms_labreport_milestone WHERE MilestoneActivityId=:MilestoneActivityId AND ProjectId=:ProjectId AND ActivityFor=:ActivityFor";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("MilestoneActivityId", pm.getMilestoneActivityId());
		query.setParameter("ProjectId", pm.getProjectId());
		query.setParameter("ActivityFor", pm.getActivityFor());
		return query.executeUpdate();
		
	}
	}
	catch (Exception e) {
	e.printStackTrace();
	return 0;
		}
	
}

@Override
public List<PfmsLabReportMilestone> getPfmsLabReportMilestoneData(String projectid) throws Exception {

	 try {
         // Creating a dynamic JPQL query to fetch records based on ProjectId
         String jpql = "SELECT p FROM PfmsLabReportMilestone p WHERE p.ProjectId = :projectId ORDER BY p.MilestoneActivityId ASC";
         Query query = manager.createQuery(jpql);
         
         // Setting the projectId parameter
         query.setParameter("projectId", Long.parseLong(projectid));

         // Execute the query and return the list of results
         return query.getResultList();
     } catch (Exception e) {
         e.printStackTrace();
         throw new Exception("Error fetching milestones for project ID: " + projectid, e);
     }
 }


}
