package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.models.Chapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChapterDao {
    private final ChapterRepository chapterRepository;

    public ChapterDao(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public void saveToDatabase(Chapter chapter) {
        this.chapterRepository.save(chapter);
    }

    public List<Chapter> getAllChapters(){
        return this.chapterRepository.findAll();
    }
}
