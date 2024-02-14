package com.example.HolidayPlanner.services;

import com.example.HolidayPlanner.DAO.TeamDao;
import com.example.HolidayPlanner.DAO.UserDao;
import com.example.HolidayPlanner.models.Team;
import com.example.HolidayPlanner.models.TeamWithEmployees;
import com.example.HolidayPlanner.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamDao teamDao;
    private final UserDao userDao;

    public TeamService(TeamDao teamDao, UserDao userDao) {
        this.teamDao = teamDao;
        this.userDao = userDao;
    }

    public void saveTeam(Team team) {
        teamDao.saveToDatabase(team);
    }

    public ArrayList<TeamWithEmployees> getTeamsWithUsers(User user) {
        return teamDao.getTeams(user);
    }

    public void deleteTeam(UUID teamId) throws Exception {
        ArrayList<User> users = this.userDao.getAllUsers().stream()
                .filter(request -> request.getTeamId() != null)
                .collect(Collectors.toCollection(ArrayList::new));

        for (User user : users) {
            if (user.getTeamId().equals(teamId)) {
                user.setTeamId(null);
                this.userDao.saveToDatabase(user);
            }
        }
        this.teamDao.deleteTeam(teamId);
    }

    public List<Team> getAllTeams() {
        return this.teamDao.getAllTeams();
    }

}
