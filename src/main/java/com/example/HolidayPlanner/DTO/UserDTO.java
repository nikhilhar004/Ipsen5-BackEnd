package com.example.HolidayPlanner.DTO;

import lombok.*;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) class representing a User.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private UUID id;
    private String fullName;

    private UUID teamId;

    private UUID chapterId;
    private String role;
}
