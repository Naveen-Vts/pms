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
@Table(name = "project_infrastructure_utilization")
@Setter
@Getter
public class ProjectInfrastructureUtilization {
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "infrastructure_utilization_id")
	private Long infrastructureUtilizationId;

	@Column(name = "resource_utilization_id")
	private Long resourceUtilizationId;

	@Column(name = "name_of_infrastructure", length = 500)
	private String nameOfInfrastructure;

	@Column(name = "days_utilized")
	private Long daysUtilized;
	
	@Column(name = "revision_no")
	private Long revisionNo;

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
