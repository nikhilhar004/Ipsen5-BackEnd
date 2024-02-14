package com.example.HolidayPlanner.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a Team entity. This model will be used when the manager creates, deletes or manage teams.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String name;
    private String description;
    private UUID teamleaderId;
}
