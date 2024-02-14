package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
}
