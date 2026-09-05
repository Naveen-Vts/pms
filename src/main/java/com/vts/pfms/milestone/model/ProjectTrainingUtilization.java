package com.vts.pfms.milestone.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_training_utilization")
@Getter
@Setter
public class ProjectTrainingUtilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_utilization_id")
    private Long trainingUtilizationId;

    @Column(name = "resource_utilization_id")
    private Long resourceUtilizationId;

    @Column(name = "name_of_training", length = 500)
    private String nameOfTraining;

    @Column(name = "cost", precision = 10, scale = 0)
    private BigDecimal cost;

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