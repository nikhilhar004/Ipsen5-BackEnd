package com.example.HolidayPlanner.services;

import com.example.HolidayPlanner.DAO.LogDao;
import com.example.HolidayPlanner.models.Log;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogService {

    private final LogDao logDao;

    public LogService(LogDao logDao) {
        this.logDao = logDao;
    }

    public void saveLogToDatabase(String logMessage, UUID userId) {
        logDao.saveToDatabase(new Log(logMessage, userId));
    }

}
