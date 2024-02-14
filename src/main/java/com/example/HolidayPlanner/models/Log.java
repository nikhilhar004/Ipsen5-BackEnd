package com.example.HolidayPlanner.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String typeAction;
    private UUID userId;
    private Date loggingDate = new Date();

    public Log(String typeAction, UUID userId) {
        this.typeAction = typeAction;
        this.userId = userId;
    }
}
