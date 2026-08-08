package com.vts.pfms.download.dao;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.vts.pfms.download.controller.TemplateAttributes;

@Transactional
@Repository
public class DownloadDaoImpl implements DownloadDao{

	@PersistenceContext
	EntityManager manager;
		
	private static final Logger logger=LogManager.getLogger(DownloadDaoImpl.class);
	
	
	
	private static final String AGENDADOCLINKDOWNLOAD  ="SELECT a.filerepid,b.filerepuploadid,b.filepath,b.filenameui,b.filename,b.filepass,b.ReleaseDoc,b.VersionDoc FROM file_rep_new a,file_rep_upload b WHERE a.filerepid=b.filerepid AND a.filerepid=:filerepid AND a.releasedoc=b.releasedoc AND a.versiondoc=b.versiondoc";
	private static final String PROJECTDATASPECSFILEDATA  ="SELECT projectdataid,projectid,filespath, systemspecsfilename,systemconfigimgname,producttreeimgname,pearlimgname FROM pfms_project_data WHERE projectdataid=:projectdataid";
	
	
	
	@Override
	public Object[] AgendaDocLinkDownload(String filerepid)throws Exception
	{
		logger.info(new java.util.Date() +"Inside DAO AgendaDocLinkDownload ");
		Query query=manager.createNativeQuery(AGENDADOCLINKDOWNLOAD);
		query.setParameter("filerepid", Long.parseLong(filerepid));
		List<Object[]> FileRepMasterListAll=(List<Object[]>)query.getResultList();
		if(FileRepMasterListAll.size()>0) {
		return FileRepMasterListAll.get(0);
		}else {
			return null;
		}
	}
	
	
	@Override
	public Object[] ProjectDataSpecsFileData(String projectdataid) throws Exception
	{
		logger.info(new java.util.Date() +"Inside DAO ProjectDataSpecsFileData ");
	
		Query query=manager.createNativeQuery(PROJECTDATASPECSFILEDATA);
		query.setParameter("projectdataid",Long.parseLong(projectdataid));
		Object[] ProjectDataSpecsFileData=(Object[])query.getSingleResult();
		return ProjectDataSpecsFileData;
		
	}

	private static final String REQATTACHDOWNLOAD="SELECT a.attachmentid,a.InitiationreqId,a.Filespath, a.AttachmentsName FROM pfms_initiation_req_attach a WHERE a.attachmentid=:attachmentid";
	@Override
	public Object[] reqAttachDownload(String attachmentid) throws Exception {
		// TODO Auto-generated method stub
		logger.info(new java.util.Date() +"Inside DAO reqAttachDownload ");
		Query query =manager.createNativeQuery(REQATTACHDOWNLOAD);
		
		query.setParameter("attachmentid", Long.parseLong(attachmentid));
		Object[]reqAttachDownload=(Object[])query.getSingleResult();
		return reqAttachDownload;
	}
	
	private static final String LABDETAILS=" SELECT LabMasterId,LabName,LabUnitCode,LabAddress,LabCity,LabPin,LabTelNo,LabFaxNo FROM lab_master WHERE labcode=:labCode";
	
	@Override
	public Object[] getLabDetails(String labCode) throws Exception {
		// TODO Auto-generated method stub
		
		Query query = manager.createNativeQuery(LABDETAILS);
		query.setParameter("labCode", labCode);
		Object[]labDetails=null;
		labDetails=(Object[])query.getSingleResult();
		return labDetails;
	}
	
	@Override
	public Long TemplateAttributesAdd(TemplateAttributes ta) throws Exception {
		manager.persist(ta);
		manager.flush();
		return ta.getAttributId();
	}
	
	
	@Override
	public TemplateAttributes TemplateAttributesEditById(long Attributid) throws Exception{
		try {
			return manager.find(TemplateAttributes.class, Attributid);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO getCARSInitiationById "+e);
			return new TemplateAttributes();
		}
	}
	@Override
	public long TemplateAttributesEdit(TemplateAttributes AttributId) throws Exception{
		try {
			manager.merge(AttributId);
			manager.flush();
			return AttributId.getAttributId();
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(new Date()+" Inside DAO TemplateAttributesEdit "+e);
			return 0;
		}
	}
	
	private static final String PROJECTTEMPATRR="SELECT a.HeaderFontSize,a.HeaderFontWeight,a.SubHeaderFontsize, a.SubHeaderFontweight,a.ParaFontSize,a.ParaFontWeight,a.MainTableWidth, a.subTableWidth,a.AttributId,a.SuperHeaderFontsize,a.SuperHeaderFontWeight,a.FontFamily,a.RestrictionOnUse FROM  pfms_doc_template_attributes a WHERE a.isActive = 1";

	@Override
	public Object[] ProjectDataTempAttr() throws Exception {
		
		try {
			Query query=manager.createNativeQuery(PROJECTTEMPATRR);
			List<Object[]> list =(List<Object[]>)query.getResultList();	
			if(list!=null && list.size()>0) {
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
