package com.example.HolidayPlanner.controlers;

import com.example.HolidayPlanner.DTO.UserDTO;
import com.example.HolidayPlanner.models.ApiResponse;
import com.example.HolidayPlanner.models.User;
import com.example.HolidayPlanner.services.LogService;
import com.example.HolidayPlanner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private final LogService logService;

    public UserController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }


    /**
     * Retrieves all users based on the HTTP GET request.
     *
     * @return ApiResponse containing an ArrayList of UserDTO objects
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<ArrayList<UserDTO>> getAllUsers() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByEmail(email);
        logService.saveLogToDatabase("User has asked for all usernames", user.getId());
        if (user.getRole().equals("manager")) {
            return new ApiResponse<>(HttpStatus.ACCEPTED, userService.getAllUsersToApiRequest());
        } else {
            ArrayList<UserDTO> users = userService.getAllUsersToApiRequest()
                    .stream()
                    .filter(request -> request.getTeamId() != null)
                    .filter(userDTO -> userDTO.getTeamId().equals(user.getTeamId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<UserDTO> users2 = userService.getAllUsersToApiRequest();
            return new ApiResponse<>(HttpStatus.ACCEPTED, users);
        }
    }

    /**
     * This method updates the following user details for all send users: teamId, chapterId, role.
     * Only managers are allowed to use this endpoint
     *
     * @param users These parameters represent the users who need to change.
     * @return returns a API response message
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ResponseBody
    public ApiResponse<ArrayList<String>> saveAllUsers(@RequestBody ArrayList<User> users) {
        try {


            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findUserByEmail(email);
            if (!user.getRole().equals("manager")) {
                return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You need to use an admin account to access this resource!");
            }
            ArrayList<User> allUsers = userService.getAllUsers();

            for (User item : users) {
                for (int j = 0; j < allUsers.size(); j++) {
                    if (item.getId().equals(users.get(j).getId())) {
                        item.setPassword(allUsers.get(j).getPassword());
                        item.setEmail(allUsers.get(j).getEmail());
                    }
                }
            }
            for (User value : users) {

                this.userService.saveUserToDatabase(value);
            }
            this.logService.saveLogToDatabase("User has updated user data", user.getId());

            return new ApiResponse<>(HttpStatus.ACCEPTED, "You have updated users!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ApiResponse<>(HttpStatus.ACCEPTED, "You have updated users!");

    }


}

