package com.vts.pfms.mail;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;
@Transactional
@Repository
public class MailDaoImpl implements MailDao {

	@PersistenceContext
	EntityManager manager;
	
	private static final String MEETINGS="SELECT cs.scheduleid,cs.projectid,cs.InitiationId,\r\n"
			+ "c.CommitteeShortName,c.CommitteeName,cs.MeetingVenue,\r\n"
			+ "cs.ScheduleStartTime,pm.project_code,pm.project_short_name FROM committee_schedule cs,committee c ,project_master pm WHERE \r\n"
			+ "c.CommitteeId=cs.CommitteeId AND pm.project_id=cs.projectid AND  cs.ScheduleDate=:DATE AND cs.isactive='1'\r\n"
			+ "UNION \r\n"
			+ "SELECT cs.scheduleid,cs.projectid,cs.InitiationId,\r\n"
			+ "c.CommitteeShortName,c.CommitteeName,cs.MeetingVenue,\r\n"
			+ "cs.ScheduleStartTime,'','-'FROM committee_schedule cs,committee c WHERE \r\n"
			+ "c.CommitteeId=cs.CommitteeId AND cs.projectid='0' AND  cs.ScheduleDate=:DATE AND cs.isactive='1'";
	@Override
	public List<Object[]> getTodaysMeetings(String date) throws Exception {
		Query query = manager.createNativeQuery(MEETINGS);
		query.setParameter("DATE", date);
		
		return (List<Object[]>)query.getResultList();
	}
	private static final String MAILPROPERTIESCONFIGURATION = "SELECT a.MailConfigurationId,a.TypeOfHost,a.Host,a.Port,a.Username,a.Password,a.CreatedBy,a.CreatedDate FROM mail_configuration a WHERE a.TypeOfHost=:typeOfHost LIMIT 1";
	@Override
	public List<Object[]> GetMailPropertiesByTypeOfHost(String typeOfHost)throws Exception{
		Query query = manager.createNativeQuery(MAILPROPERTIESCONFIGURATION);
		query.setParameter("typeOfHost", typeOfHost);
		List<Object[]> GetMailPropertiesByTypeOfHost = query.getResultList();
		return GetMailPropertiesByTypeOfHost;

		
	}

}
