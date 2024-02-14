package com.example.HolidayPlanner.services;

import com.example.HolidayPlanner.DAO.LeaveRequestDao;
import com.example.HolidayPlanner.DAO.TeamDao;
import com.example.HolidayPlanner.DAO.UserDao;
import com.example.HolidayPlanner.DTO.LeaveRequestDTO;
import com.example.HolidayPlanner.models.LeaveRequest;
import com.example.HolidayPlanner.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {
    private final LeaveRequestDao leaveRequestDao;
    private final UserDao userDao;
    private final TeamDao teamDao;

    public LeaveRequestService(LeaveRequestDao leaveRequestDao, UserDao userDao, TeamDao teamDao) {
        this.leaveRequestDao = leaveRequestDao;
        this.userDao = userDao;
        this.teamDao = teamDao;
    }

    public void saveLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequestDao.saveToDatabase(leaveRequest);
    }

    public void declineLeaveRequest(UUID uuid) {
        leaveRequestDao.declineLeaveRequest(uuid);
    }

    public void acceptLeaveRequest(UUID uuid) {
        leaveRequestDao.acceptLeaveRequest(uuid);
    }

    public ArrayList<LeaveRequest> getAllLeaveRequests(User user) {
        return leaveRequestDao.getAllLeaveRequests(user);
    }

    public ArrayList<LeaveRequest> getOwnLeaveRequests(UUID userId) {
        return leaveRequestDao.getOwnLeaveRequests(userId);
    }

    public List<LeaveRequestDTO> getAcceptedLeaveRequestDTOs() {
        return leaveRequestDao.getAcceptedLeaveRequestDTOs();
    }

    /**
     * Retrieves leave requests from the user's own team.
     *
     * @param requestUser The user for whom leave requests are retrieved
     * @return An ArrayList of LeaveRequestDTO objects representing leave requests from the user's own team
     */
    public ArrayList<LeaveRequestDTO> getLeaveRequestFromOwnTeam(User requestUser) {
        if (requestUser.getTeamId() == null) {
            ArrayList<LeaveRequest> ownRequests = this.leaveRequestDao.getOwnLeaveRequests(requestUser.getId());
            ArrayList<LeaveRequestDTO> leaveRequestDTOS = new ArrayList<>();
            for (LeaveRequest ownRequest : ownRequests) {
                leaveRequestDTOS.add(new LeaveRequestDTO(
                        ownRequest.getId(),
                        ownRequest.getStartDate(),
                        ownRequest.getEndDate(),
                        ownRequest.getTypeRequest(),
                        requestUser.getId()
                ));
            }
            return leaveRequestDTOS;
        } else {

            ArrayList<LeaveRequestDTO> leaveRequestDTOS = (ArrayList<LeaveRequestDTO>) this.getAcceptedLeaveRequestDTOs();
            List<User> users = this.userDao.getAllUsers().stream()
                    .filter(request -> request.getTeamId() != null)
                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<LeaveRequestDTO> newLeaveRequest = new ArrayList<>();
            for (LeaveRequestDTO leaveRequestDTO : leaveRequestDTOS) {
                for (User user : users) {
                    if (leaveRequestDTO.getUserId().equals(user.getId())) {
                        if (user.getTeamId().equals(requestUser.getTeamId())) {
                            newLeaveRequest.add(leaveRequestDTO);
                        }
                    }
                }
            }
            return newLeaveRequest;
        }
    }
}