package com.vts.pfms.login;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "audit_failed_stamping")
public class AuditFailedStamping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private String UserName;

    private LocalDate LoginDate;

    private LocalDateTime LoginDateTime;

    private String IpAddress;
    private Integer isactive;
    
}
