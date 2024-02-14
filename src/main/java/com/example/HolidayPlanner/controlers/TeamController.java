package com.example.HolidayPlanner.controlers;

import com.example.HolidayPlanner.models.*;
import com.example.HolidayPlanner.services.LogService;
import com.example.HolidayPlanner.services.TeamService;
import com.example.HolidayPlanner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/api/team")
@CrossOrigin("*")
public class TeamController {

    private final UserService userService;
    private final LogService logService;
    private final TeamService teamService;

    public TeamController(TeamService teamService, UserService userDao, LogService logService) {
        this.teamService = teamService;
        this.userService = userDao;
        this.logService = logService;
    }

    /**
     * Retrieves all teams.
     *
     * @return ApiResponse containing a status code and an ArrayList of Team objects
     */
    @GetMapping("")
    @ResponseBody
    @CrossOrigin("*")
    public ApiResponse<ArrayList<Team>> getTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");

        }
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        logService.saveLogToDatabase("User has asked for teams", user.getId());

        return new ApiResponse<>(HttpStatus.ACCEPTED, (ArrayList<Team>) teamService.getAllTeams());
    }

    /**
     * Retrieves all teams with their employees.
     *
     * @return ApiResponse containing a status code and an ArrayList of TeamWithEmployees objects
     */
    @GetMapping("/employee")
    @ResponseBody
    @CrossOrigin("*")
    public ApiResponse<ArrayList<TeamWithEmployees>> getTeamsWithEmployees() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");

        }
        String email = authentication.getName();
        User user = userService.findUserByEmail(email);

        if (user.getTeamId() == null && !"manager".equals(user.getRole())) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "You are not assigned to a team");
        }

        logService.saveLogToDatabase("User has asked for teams", user.getId());

        ArrayList<TeamWithEmployees> teams = teamService.getTeamsWithUsers(user);
        return new ApiResponse<>(HttpStatus.ACCEPTED, teams);
    }

    /**
     * Creates a new team.
     *
     * @param team The Team object containing the team details from the request body
     * @return ApiResponse containing a status code and a message indicating the outcome of the post operation
     */
    @PostMapping("/insert")
    @ResponseBody
    public ApiResponse<String> postTeam(@RequestBody Team team) {

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(email);

        if (!user.getRole().equals("manager")) {
            return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You need to use an admin account to access this resource!");
        }
        logService.saveLogToDatabase("User has created a new team", user.getId());


        teamService.saveTeam(team);
        return new ApiResponse<>(HttpStatus.ACCEPTED, "You posted some data!");
    }

    /**
     * Deletes a team by its ID.
     *
     * @param teamId The ID of the team to delete
     * @return ApiResponse containing a status code and a message indicating the outcome of the delete operation
     */
    @DeleteMapping("/delete/{teamId}")
    @ResponseBody
    public ApiResponse<String> deleteTeam(@PathVariable UUID teamId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(email);

        if (!user.getRole().equals("manager")) {
            return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You need to use an admin account to access this resource!");
        }

        try {
            this.teamService.deleteTeam(teamId);
            return new ApiResponse<>(HttpStatus.ACCEPTED, "Deleted Team!");

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Team does not exist!");
        }
    }
}
