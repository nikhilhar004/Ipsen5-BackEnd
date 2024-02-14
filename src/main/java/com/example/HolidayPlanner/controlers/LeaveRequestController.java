package com.example.HolidayPlanner.controlers;

import com.example.HolidayPlanner.DTO.LeaveRequestDTO;
import com.example.HolidayPlanner.models.ApiResponse;
import com.example.HolidayPlanner.models.LeaveRequest;
import com.example.HolidayPlanner.models.User;
import com.example.HolidayPlanner.services.LeaveRequestService;
import com.example.HolidayPlanner.services.LogService;
import com.example.HolidayPlanner.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping(value = "/api/leaverequest")
@CrossOrigin("*")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    private final LogService logService;
    private final UserService userService;

    public LeaveRequestController(LeaveRequestService leaveRequestService, LogService logService, UserService userService) {
        this.leaveRequestService = leaveRequestService;
        this.logService = logService;

        this.userService = userService;
    }

    /**
     * Posts a leave request for the currently authenticated user.
     *
     * @param leaveRequest The LeaveRequest object containing the leave request details from the request body
     * @return ApiResponse containing a status code and a message indicating the outcome of the post operation
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse<String> postLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        String email = userService.getCurrentUserEmail();
        User user = userService.findUserByEmail(email);

        logService.saveLogToDatabase("User has posted a leave request", user.getId());
        leaveRequestService.saveLeaveRequest(leaveRequest);

        return new ApiResponse<>(HttpStatus.ACCEPTED, "You posted a leave request!");
    }

    /**
     * Retrieves leave requests for the currently authenticated user.
     *
     * @return ApiResponse containing a status code and an ArrayList of LeaveRequest objects
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<ArrayList<LeaveRequest>> getLeaveRequest() {
        String email = userService.getCurrentUserEmail();
        if (email.equals("anonymousUser")) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");
        }

        User user = userService.findUserByEmail(email);
        logService.saveLogToDatabase("User has asked for leave requests", user.getId());

        if (user.getTeamId() == null && !user.getRole().equals("manager")) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "You are not assigned to a team");
        }
        ArrayList<LeaveRequest> leaveRequests = leaveRequestService.getAllLeaveRequests(user);
        return new ApiResponse<>(HttpStatus.ACCEPTED, leaveRequests);
    }

    /**
     * Declines a leave request.
     *
     * @param requestBody A Map containing the UUID of the leave request to decline
     * @return ApiResponse containing a status code and a message indicating the outcome of the decline operation
     */
    @RequestMapping(value = "/decline", method = RequestMethod.PATCH)
    @ResponseBody
    public ApiResponse<String> declineLeaveRequest(@RequestBody Map<String, String> requestBody) {
        try {
            String email = userService.getCurrentUserEmail();
            User user = userService.findUserByEmail(email);
            if (user.getRole().equals("employee")) {
                return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Only managers and team leaders Are Allowed to accept leave requests");
            }
            String uuidString = requestBody.get("uuid");
            UUID uuid = UUID.fromString(uuidString);

            leaveRequestService.declineLeaveRequest(uuid);


            logService.saveLogToDatabase("User has declined a leave request", user.getId());


            return new ApiResponse<>(HttpStatus.ACCEPTED, "The leave request has been declined!");
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NullPointerException | HttpMessageNotReadableException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing 'uuid' attribute in request body");
        }
    }

    /**
     * Accepts a leave request.
     *
     * @param requestBody A Map containing the UUID of the leave request to accept
     * @return ApiResponse containing a status code and a message indicating the outcome of the accept operation
     */
    @RequestMapping(value = "/accept", method = RequestMethod.PATCH)
    @ResponseBody
    public ApiResponse<String> acceptLeaveRequest(@RequestBody Map<String, String> requestBody) {
        try {
            String uuidString = requestBody.get("uuid");
            UUID uuid = UUID.fromString(uuidString);
            leaveRequestService.acceptLeaveRequest(uuid);

            String email = userService.getCurrentUserEmail();
            User user = userService.findUserByEmail(email);
            logService.saveLogToDatabase("User has accepted a leave request", user.getId());

            if (user.getRole().equals("employee")) {
                return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Only managers and team leaders Are Allowed to accept leave requests");
            }

            leaveRequestService.acceptLeaveRequest(uuid);

            logService.saveLogToDatabase("User has accepted a leave request", user.getId());


            return new ApiResponse<>(HttpStatus.ACCEPTED, "The leave request has been accepted!");
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NullPointerException | HttpMessageNotReadableException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing 'uuid' attribute in request body");
        }
    }

    /**
     * Retrieves the leave requests of the currently authenticated user.
     *
     * @return ApiResponse containing a status code and an ArrayList of LeaveRequest objects
     */
    @RequestMapping(value = "/view-requests", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<ArrayList<LeaveRequest>> getOwnLeaveRequests(){
        String email = userService.getCurrentUserEmail();
        if (email.equals("anonymousUser")) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");
        }
        User user = userService.findUserByEmail(email);

        logService.saveLogToDatabase("User has asked for leave requests", user.getId());

        ArrayList<LeaveRequest> leaveRequests = leaveRequestService.getOwnLeaveRequests(user.getId());
        return new ApiResponse<>(HttpStatus.ACCEPTED, leaveRequests);
    }

    /**
     * Retrieves the accepted leave request DTOs.
     *
     * @return ApiResponse containing a status code and a List of LeaveRequestDTO objects
     */
    @RequestMapping(value = "/get-DTO", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<LeaveRequestDTO>> getAcceptedLeaveRequestDTOs() {
        try {
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (email.equals("anonymousUser")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");
            }
            User user = userService.findUserByEmail(email);

            this.logService.saveLogToDatabase("User has asked for leave request DTO", user.getId());
            if (user.getRole().equals("manager")) {
                return new ApiResponse<>(HttpStatus.ACCEPTED,
                        leaveRequestService.getAcceptedLeaveRequestDTOs());
            } else {
                return new ApiResponse<>(HttpStatus.ACCEPTED,
                        leaveRequestService.getLeaveRequestFromOwnTeam(user));
            }
        } catch (EntityNotFoundException e){
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
