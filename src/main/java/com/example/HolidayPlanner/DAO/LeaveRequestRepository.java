package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.DTO.LeaveRequestDTO;
import com.example.HolidayPlanner.models.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    /**
     * Retrieves the accepted leave request DTOs.
     *
     * @return List of LeaveRequestDTO objects containing the ID, start date, end date, type of request, and user ID
     */
    @Query("SELECT new com.example.HolidayPlanner.DTO.LeaveRequestDTO (lr.id , lr.startDate, lr.endDate, lr.typeRequest, u.id) " +
            "FROM LeaveRequest AS lr " +
            "INNER JOIN User u ON u.id = lr.userId " +
            "WHERE lr.status = 'accepted'")
    <T> List<LeaveRequestDTO> getAcceptedLeaverequestDTOs();
}
