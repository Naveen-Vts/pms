package com.vts.pfms.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisionSummaryDto {
    private Long revisionNo;
    private String revisedDate;
    private String revisedBy;
    private boolean current;
}