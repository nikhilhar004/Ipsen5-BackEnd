package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.models.Log;
import com.example.HolidayPlanner.models.Team;
import org.springframework.stereotype.Component;

@Component
public class LogDao {

    private LogRepository logRepository;

    public LogDao(LogRepository logRepository) {
        this.logRepository = logRepository;
    }
    public void saveToDatabase(Log log) {
        this.logRepository.save(log);
    }


}
