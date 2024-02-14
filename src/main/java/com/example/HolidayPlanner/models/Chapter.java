package com.example.HolidayPlanner.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a Chapter entity. This model will be used when the manager creates, deletes or manage chapters.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Chapter")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String name;
    private String description;
}
