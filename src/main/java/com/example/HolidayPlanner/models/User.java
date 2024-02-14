package com.example.HolidayPlanner.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a User entity. When a new user gets registers, this model will be saved in the database.
 * If this user logs in, then this model will be retrieved from the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String fullName;

    private String email;
    private String password;

    private UUID teamId;
    private UUID chapterId;
    /**
     * this attribute is the role of a user. There are three options:
     * employee, team_leader and manager
     */
    private String role = "employee";
}

