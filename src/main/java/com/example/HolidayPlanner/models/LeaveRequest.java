package com.example.HolidayPlanner.models;


import com.example.HolidayPlanner.DTO.LeaveRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a Leave requests entity. this model will be used with almost every feature.
 * You can Accept and Decline a leave request as a team lead or manager.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "LeaveRequest")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private Date startDate;
    private Date endDate;
    private String typeRequest;
    /**
     * this attribute is the status of a leave request. There are three options:
     * pending, accepted and declined
     */
    private String status = "pending";
    private double balanceRequired;
    private String description;
    private UUID userId;
}
