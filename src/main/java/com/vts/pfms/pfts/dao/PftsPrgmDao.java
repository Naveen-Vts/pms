package com.vts.pfms.pfts.dao;

import java.util.List;

import com.vts.pfms.pfts.dto.PmmgPmsDmdDetails;

public interface PftsPrgmDao {
	
	List<PmmgPmsDmdDetails> getPMMGProcurementData(String projectImmsCd);
	
}
