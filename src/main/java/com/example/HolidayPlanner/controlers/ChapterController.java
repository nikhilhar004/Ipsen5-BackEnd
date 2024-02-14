package com.example.HolidayPlanner.controlers;

import com.example.HolidayPlanner.models.*;
import com.example.HolidayPlanner.services.ChapterService;
import com.example.HolidayPlanner.services.LogService;
import com.example.HolidayPlanner.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/chapter")
@CrossOrigin("*")
public class ChapterController {
    private final ChapterService chapterService;
    private final UserService userService;
    private final LogService logService;

    public ChapterController(ChapterService chapterService, UserService userService, LogService logService) {
        this.chapterService = chapterService;
        this.userService = userService;
        this.logService = logService;
    }

    /**
     * Retrieves all chapters.
     *
     * @return ApiResponse containing a status code and a List of Chapter objects
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<Chapter>> getAllChapters() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email.equals("anonymousUser")) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Please login to use this function");
        }
        User user = userService.findUserByEmail(email);

        logService.saveLogToDatabase("User has asked for chapters", user.getId());

        List<Chapter> chapters = this.chapterService.getAllChapters();
        return new ApiResponse<>(HttpStatus.ACCEPTED, chapters);
    }
}
