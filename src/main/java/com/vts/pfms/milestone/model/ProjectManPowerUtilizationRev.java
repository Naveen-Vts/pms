package com.vts.pfms.milestone.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "project_manpower_utilization_rev")
@Entity
@Getter
@Setter
public class ProjectManPowerUtilizationRev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manpower_utilization_rev_id")
    private Long manPowerUtilizationRevId;
    
    @Column(name = "resource_utilization_id")
    private Long resourceUtilizationId;

    @Column(name = "man_power_days")
    private Long manPowerDays;

    @Column(name = "man_power_count")
    private Long manPowerCount;

    @Column(name = "desig_cadre", length = 50)
    private String desigCadre;

    @Column(name = "revision_no")
    private Long revisionNo;

    @Column(name = "revision_date")
    private LocalDateTime revisionDate;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "is_active")
    private Integer isActive;

	
	
}
