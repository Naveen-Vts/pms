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

@Entity
@Table(name = "project_economic_impact")
@Getter
@Setter
public class ProjectEconomicImpact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "economic_impact_id")
    private Long economicImpactId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "indigenous_content_and_indigenization", columnDefinition = "LONGTEXT")
    private String indigenousContentAndIndigenization;

    @Column(name = "international_collaborations_executed", columnDefinition = "LONGTEXT")
    private String internationalCollaborationsExecuted;

    @Column(name = "intellectual_property_rights", columnDefinition = "LONGTEXT")
    private String intellectualPropertyRights;

    @Column(name = "export_potential", columnDefinition = "LONGTEXT")
    private String exportPotential;

    @Column(name = "infrastructure_created", columnDefinition = "LONGTEXT")
    private String infrastructureCreated;

    @Column(name = "revision_no")
    private Long revisionNo;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "is_active")
    private Integer isActive;
}
