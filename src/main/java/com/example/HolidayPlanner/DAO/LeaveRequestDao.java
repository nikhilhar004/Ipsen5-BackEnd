package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.DTO.LeaveRequestDTO;
import com.example.HolidayPlanner.models.LeaveRequest;
import com.example.HolidayPlanner.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LeaveRequestDao {
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserDao userDao;
    private final EntityManager entityManager;

    public LeaveRequestDao(LeaveRequestRepository leaveRequestRepository, UserDao userDao, EntityManager entityManager) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userDao = userDao;
        this.entityManager = entityManager;
    }

    public void saveToDatabase(LeaveRequest leaveRequest) {
        this.leaveRequestRepository.save(leaveRequest);
    }

    public ArrayList<LeaveRequest> getAllLeaveRequests(User user) {
        List<LeaveRequest> leaveRequests = this.leaveRequestRepository.findAll()
                .stream()
                .filter(request -> request.getStatus().equals("pending") || request.getStatus().equals("accepted"))
                .collect(Collectors.toList());

        if (user.getRole().equals("manager")) {
            return (ArrayList<LeaveRequest>) leaveRequests;
        } else {
            ArrayList<User> users = this.userDao.getAllUsers();
            UUID teamIdFromUser = user.getTeamId();
            ArrayList<LeaveRequest> filteredLeaveRequest = new ArrayList<>();
            ArrayList<UUID> userIDSFromSameTeam = new ArrayList<>();

            for (User value : users) {
                if (value.getTeamId() == null) {
                    continue;
                }
                if (value.getTeamId().equals(teamIdFromUser)) {
                    userIDSFromSameTeam.add(value.getId());
                }
            }

            for (UUID uuid : userIDSFromSameTeam) {
                for (LeaveRequest leaveRequest : leaveRequests) {
                    if (uuid.equals(leaveRequest.getUserId())) {
                        filteredLeaveRequest.add(leaveRequest);
                    }
                }

            }
            return filteredLeaveRequest;
        }
    }


    public void declineLeaveRequest(UUID uuid){
        LeaveRequest leaveRequest = this.entityManager.find(LeaveRequest.class, uuid);
        if (leaveRequest == null){
            throw new EntityNotFoundException("Entity not found with id: " + uuid);
        }
        leaveRequest.setStatus("declined");
        this.saveToDatabase(leaveRequest);
    }

    public void acceptLeaveRequest(UUID uuid){
        LeaveRequest leaveRequest = this.entityManager.find(LeaveRequest.class, uuid);
        if (leaveRequest == null){
            throw new EntityNotFoundException("Entity not found with id: " + uuid);
        }
        leaveRequest.setStatus("accepted");
        this.saveToDatabase(leaveRequest);
    }

    public ArrayList<LeaveRequest>  getOwnLeaveRequests(UUID uuid) {
        return this.leaveRequestRepository.findAll()
                .stream()
                .filter(request -> (request.getStatus().equals("pending") || request.getStatus().equals("accepted"))
                        && request.getUserId().equals(uuid))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<LeaveRequestDTO> getAcceptedLeaveRequestDTOs(){
        List<LeaveRequestDTO> leaveRequestDTOList = this.leaveRequestRepository.getAcceptedLeaverequestDTOs();
        if (leaveRequestDTOList == null){
            throw new EntityNotFoundException("No Accepted LeaveRequest have been found.");
        }
        return leaveRequestDTOList;
    }
}
