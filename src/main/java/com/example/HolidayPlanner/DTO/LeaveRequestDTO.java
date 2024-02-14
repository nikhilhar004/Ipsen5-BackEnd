package com.example.HolidayPlanner.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) class representing a leave request.
 */
@Data
@Getter
@AllArgsConstructor
public class LeaveRequestDTO {
    private UUID leaveRequestId;
    private Date startDate;
    private Date endDate;
    private String typeRequest;
    private UUID userId;
}
