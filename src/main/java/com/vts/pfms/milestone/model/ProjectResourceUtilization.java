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
@Table(name = "project_resource_utilization")
@Getter
@Setter
public class ProjectResourceUtilization {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resource_utilization_id")
	private Long resourceUtilizationId;
	
	@Column(name = "project_id")
	private Long projectId;
		
	@Column(name = "financial_year",length = 10)
	private String financialYear;
		
	@Column(name = "quarter",length = 2)
	private String quarter;
	
	@Column(name = "status",length = 50)
	private String status;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "is_active")
    private Integer isActive;

    
    
}
