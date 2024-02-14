package com.example.HolidayPlanner;

import com.example.HolidayPlanner.DAO.LeaveRequestDao;
import com.example.HolidayPlanner.DAO.LeaveRequestRepository;
import com.example.HolidayPlanner.DAO.UserDao;
import com.example.HolidayPlanner.models.LeaveRequest;
import com.example.HolidayPlanner.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class LeaveRequestDaoTest {
    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private LeaveRequestDao leaveRequestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllLeaveRequests_ManagerRole_ReturnsAllRequests() {
        // Arrange
        User manager = new User();
        manager.setRole("manager");

        List<LeaveRequest> allRequests = Arrays.asList(
                createLeaveRequest(UUID.randomUUID(), "pending", UUID.randomUUID()),
                createLeaveRequest(UUID.randomUUID(), "accepted", UUID.randomUUID())
        );

        when(leaveRequestRepository.findAll()).thenReturn(allRequests);

        // Act
        ArrayList<LeaveRequest> result = leaveRequestService.getAllLeaveRequests(manager);

        // Assert
        assertEquals(allRequests.size(), result.size());
        for (int i = 0; i < allRequests.size(); i++) {
            assertEquals(allRequests.get(i).getId(), result.get(i).getId());
            assertEquals(allRequests.get(i).getStatus(), result.get(i).getStatus());
            assertEquals(allRequests.get(i).getUserId(), result.get(i).getUserId());
        }
    }

    @Test
    public void testGetAllLeaveRequests_NonManagerRole_ReturnsFilteredRequests() {
        // Arrange
        User nonManager = new User();
        nonManager.setRole("employee");
        nonManager.setId(UUID.randomUUID());
        UUID teamId = UUID.randomUUID();
        nonManager.setTeamId(teamId);

        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.add(createUser(UUID.randomUUID(), "John", teamId));
        allUsers.add(createUser(UUID.randomUUID(), "Jane", teamId));
        allUsers.add(createUser(UUID.randomUUID(), "Mark", UUID.randomUUID()));
        allUsers.add(nonManager);

        ArrayList<LeaveRequest> allRequests = new ArrayList<>();
        allRequests.add(createLeaveRequest(UUID.randomUUID(), "pending",allUsers.get(0).getId()));
        allRequests.add(createLeaveRequest(UUID.randomUUID(), "accepted", allUsers.get(1).getId()));
        allRequests.add(createLeaveRequest(UUID.randomUUID(), "pending", UUID.randomUUID()));

        when(leaveRequestRepository.findAll()).thenReturn(allRequests);
        when(userDao.getAllUsers()).thenReturn(allUsers);

        // Act
        List<LeaveRequest> result = leaveRequestService.getAllLeaveRequests(nonManager);

        // Assert
        assertEquals(2, result.size());
    }
    private LeaveRequest createLeaveRequest(UUID requestId, String status, UUID userId) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(requestId);
        leaveRequest.setStatus(status);
        leaveRequest.setUserId(userId);
        return leaveRequest;
    }

    private User createUser(UUID userId, String name, UUID teamId) {
        User user = new User();
        user.setId(userId);
        user.setFullName(name);
        user.setTeamId(teamId);
        return user;
    }
}
