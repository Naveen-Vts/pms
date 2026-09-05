package com.vts.pfms.milestone.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProjectTrainingUtilizationDto {
    private String resourceUtilizationId;
    private String projectId;
    private String finYear;
    private String quarter;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
    private String isActive;
    private List<TrainingItemDto> items = new ArrayList<>();
}

