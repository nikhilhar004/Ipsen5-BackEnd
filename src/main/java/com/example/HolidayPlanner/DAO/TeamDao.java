package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.models.Team;
import com.example.HolidayPlanner.models.TeamWithEmployees;
import com.example.HolidayPlanner.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TeamDao {

    private final TeamRepository teamRepository;
    private final UserDao userDao;


    public TeamDao(TeamRepository teamRepository, UserDao userDao) {
        this.teamRepository = teamRepository;
        this.userDao = userDao;
    }

    public void saveToDatabase(Team team) {
        this.teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return this.teamRepository.findAll();
    }

    /**
     * Retrieves teams with their associated employees.
     *
     * @param user The user for whom teams are retrieved
     * @return An ArrayList of TeamWithEmployees objects representing teams with their associated employees
     */
    public ArrayList<TeamWithEmployees> getTeams(User user) {
        ArrayList<TeamWithEmployees> teamsWithEmployees = new ArrayList<>();
        ArrayList<User> users = this.userDao.getAllUsers().stream()
                .filter(request -> request.getTeamId() != null)
                .collect(Collectors.toCollection(ArrayList::new));

        if (user.getRole().equals("manager")) {
            ArrayList<Team> teams = new ArrayList<>(this.teamRepository.findAll());

            for (Team team : teams) {
                ArrayList<User> filteredUsers = users.stream()
                        .filter(request -> request.getTeamId().equals(team.getId()))
                        .collect(Collectors.toCollection(ArrayList::new));

                teamsWithEmployees.add(new TeamWithEmployees(
                        team.getId(),
                        team.getName(),
                        team.getDescription(),
                        team.getTeamleaderId(),
                        filteredUsers
                ));
            }
        } else {
            UUID userTeamId = user.getTeamId();
            ArrayList<Team> teams = (ArrayList<Team>) this.teamRepository.findAllById(Collections.singleton(userTeamId));

            if (!teams.isEmpty()) {
                ArrayList<User> filteredUsers = users.stream()
                        .filter(request -> request.getTeamId().equals(userTeamId))
                        .collect(Collectors.toCollection(ArrayList::new));

                teamsWithEmployees.add(new TeamWithEmployees(
                        teams.get(0).getId(),
                        teams.get(0).getName(),
                        teams.get(0).getDescription(),
                        teams.get(0).getTeamleaderId(),
                        filteredUsers
                ));
            }
        }
        return teamsWithEmployees;
    }

    public void deleteTeam(UUID teamId){
        this.teamRepository.deleteById(teamId);
    }
}
