package com.example.HolidayPlanner.controlers;

import com.example.HolidayPlanner.models.*;
import com.example.HolidayPlanner.security.JWTUtil;
import com.example.HolidayPlanner.services.InvalidMailService;
import com.example.HolidayPlanner.services.LogService;
import com.example.HolidayPlanner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    private final InvalidMailService invalidMailService;
    private final LogService logService;

    public AuthController(UserService userService, JWTUtil jwtUtil, AuthenticationManager authManager, PasswordEncoder passwordEncoder, InvalidMailService invalidMailService, LogService logService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.invalidMailService = invalidMailService;
        this.logService = logService;
    }

    /**
     * Handles the registration of a new user. Only managers are allowed to use this endpoint
     *
     * @param user The User object containing the user information from the request body
     * @return ApiResponse containing a status code and a message indicating the outcome of the registration
     */
    @PostMapping("/register")
    public ApiResponse<String> registerHandler(@RequestBody User user) {
        try {
            if (invalidMailService.patternMatches(user.getEmail())) {
                if (user.getPassword().length() < 8) {
                    return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid password: length must be > 8");
                }
                String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Optional<User> admin = Optional.ofNullable(userService.findUserByEmail(email));

                if (admin.isPresent()) {
                    this.logService.saveLogToDatabase("User has successfully been created", admin.get().getId());
                    if (!admin.get().getRole().equals("manager")){
                        return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You need to use an admin account to access this resource!");
                    }
                } else {
                    return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You need to use an admin account to access this resource!");
                }

                String encodedPass = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPass);
                userService.saveUserToDatabase(user);
                return new ApiResponse<>(HttpStatus.ACCEPTED, "User has been created");
            } else {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid email");
            }
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Email already in use");
        }
    }

    /**
     * Handles the login process for a user.
     *
     * @param body The LoginCredentials object containing the email and password from the request body
     * @return ApiResponse containing a status code and a message indicating the outcome of the login process
     */
    @PostMapping("/login")
    public ApiResponse<String> loginHandler(@RequestBody LoginCredentials body) {
        try {
            String email = body.getEmail();

            if (invalidMailService.patternMatches(email)) {
                UsernamePasswordAuthenticationToken authInputToken =
                        new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
                authManager.authenticate(authInputToken);

                Optional<User> user = Optional.ofNullable(userService.findUserByEmail(email));
                logService.saveLogToDatabase("User has successfully logged in", user.get().getId());
                return new ApiResponse<>(HttpStatus.ACCEPTED, jwtUtil.generateToken(body.getEmail()));
            } else {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid email");
            }
        } catch (AuthenticationException authExc) {
            return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Invalid email/password");
        }
    }

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return ApiResponse containing the user details if available, or an error message if the email is not found
     */
    @GetMapping("/info")
    public ApiResponse<User> getUserDetails() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Optional.ofNullable(userService.findUserByEmail(email)).isPresent()) {
            User user = userService.findUserByEmail(email);
            logService.saveLogToDatabase("User has asked for their personal data", user.getId());
            return new ApiResponse<>(HttpStatus.ACCEPTED, user);
        } else {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Email not found");
        }
    }

    /**
     * Updates the details of the currently authenticated user.
     *
     * @param userInfo The UserWIthOldPassword object containing the user information and old password from the request body
     * @return ApiResponse containing a status code and a message indicating the outcome of the update process
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ResponseBody
    public ApiResponse<String> updateUser(@RequestBody UserWIthOldPassword userInfo) {

        try {
            String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (email.equals("anonymousUser")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid credentials");
            }

            User user = userInfo.getUser();
            String oldPassword = userInfo.getOldPassword();
            if (invalidMailService.patternMatches(user.getEmail())) {
                if (user.getPassword().length() < 8) {
                    return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid password: length must be > 8");
                }
                Optional<User> loggedInUser = Optional.ofNullable(this.userService.findUserByEmail(email));

                if (loggedInUser.isPresent()) {
                    logService.saveLogToDatabase("User has successfully been updated", loggedInUser.get().getId());
                } else {
                    return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Invalid user has been logged in");
                }

                UsernamePasswordAuthenticationToken authInputToken =
                        new UsernamePasswordAuthenticationToken(loggedInUser.get().getEmail(), oldPassword);
                authManager.authenticate(authInputToken);

                String encodedPass = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPass);

                User newUser = loggedInUser.get();

                newUser.setFullName(user.getFullName());
                newUser.setEmail(user.getEmail());
                newUser.setPassword(encodedPass);

                userService.saveUserToDatabase(newUser);
                return new ApiResponse<>(HttpStatus.ACCEPTED, jwtUtil.generateToken(user.getEmail()));
            } else {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Invalid email");
            }
        } catch (AuthenticationException authExc) {
            return new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Invalid email/password");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Email already in use");
        }
    }

    /**
     * Checks if the currently authenticated user is a manager.
     *
     * @return ApiResponse containing a status code and a boolean indicating if the user is a manager
     */
    @RequestMapping(value = "/ismanager", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<Boolean> IsUserManager() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> loggedInUser = Optional.ofNullable(this.userService.findUserByEmail(email));

        return loggedInUser.map(user -> new ApiResponse<>(HttpStatus.ACCEPTED,
                user.getRole().equals("manager")
        )).orElseGet(() -> new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You are not logged in"));
    }

    /**
     * Checks if the currently authenticated user is a team leader.
     *
     * @return ApiResponse containing a status code and a boolean indicating if the user is a manager
     */
    @RequestMapping(value = "/isteamlead", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<Boolean> IsUserTeamLead() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> loggedInUser = Optional.ofNullable(this.userService.findUserByEmail(email));

        return loggedInUser.map(user -> new ApiResponse<>(HttpStatus.ACCEPTED,
                user.getRole().equals("manager") ||
                        user.getRole().equals("team_leader")
        )).orElseGet(() -> new ApiResponse<>(HttpStatus.UNAUTHORIZED, "You are not logged in"));
    }
}
