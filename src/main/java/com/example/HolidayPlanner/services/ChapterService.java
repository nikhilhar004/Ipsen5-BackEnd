package com.example.HolidayPlanner.services;

import com.example.HolidayPlanner.DAO.ChapterDao;
import com.example.HolidayPlanner.models.Chapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {
    private final ChapterDao chapterDao;

    public ChapterService(ChapterDao chapterDao) {
        this.chapterDao = chapterDao;
    }

    public List<Chapter> getAllChapters() {
        return chapterDao.getAllChapters();
    }
}