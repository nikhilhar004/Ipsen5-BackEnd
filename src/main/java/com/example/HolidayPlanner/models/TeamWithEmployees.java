package com.example.HolidayPlanner.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class TeamWithEmployees extends Team{
    private List<User> employees;

    public TeamWithEmployees(UUID id, String name, String description, UUID teamleaderId, List<User> employees) {
        super(id, name, description, teamleaderId);
        this.employees = employees;
    }


}
