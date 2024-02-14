package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TeamRepository   extends JpaRepository<Team, UUID> {

}
