package com.vts.pfms.pfts.dao;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vts.pfms.pfts.dto.PmmgPmsDmdDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@ConditionalOnProperty(
    name = "app.lab-code",
    havingValue = "PGAD"
)
public class PftsPrgmDaoImpl implements PftsPrgmDao{

	@PersistenceContext(unitName = "secondary")
    private EntityManager procurementManager;
	
	private static final String PMMGPROCUREMENTDATA = """
	        SELECT DemandNo, DemandDate, ProjectCode, ItemName, SONo, SODate, DPDate, FirmName, ProcurementStage
	        FROM imapmmgpms_dmddetails
	        WHERE ProjectCode = :projectCode
	        ORDER BY DemandDate DESC
	        """;

//	private static final String PMMGPROCUREMENTDATA = "SELECT * FROM imapmmgpms_dmddetails";
	
	@Override
	@Transactional(transactionManager = "secondaryTransactionManager", readOnly = true)
	public List<PmmgPmsDmdDetails> getPMMGProcurementData(String projectImmsCd) {

	    Query query = procurementManager.createNativeQuery(PMMGPROCUREMENTDATA);
	    query.setParameter("projectCode", projectImmsCd);

	    List<Object[]> results = (List<Object[]>)query.getResultList();

	    return results.stream()
	            .map(rs -> {
	            	PmmgPmsDmdDetails dto = new PmmgPmsDmdDetails();

	                dto.setDemandNo(Objects.toString(rs[0], null));
	                dto.setDemandDate(Objects.toString(rs[1], null));
	                dto.setProjectCode(Objects.toString(rs[2], null));
	                dto.setItemName(Objects.toString(rs[3], null));
	                dto.setSoNo(Objects.toString(rs[4], null));
	                dto.setSoDate(Objects.toString(rs[5], null));
	                dto.setDpDate(Objects.toString(rs[6], null));
	                dto.setFirmName(Objects.toString(rs[7], null));
	                dto.setProcurementStage(Objects.toString(rs[8], null));

	                return dto;
	            })
	            .toList();
	}
}
