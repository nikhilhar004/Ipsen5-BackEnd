package com.example.HolidayPlanner.models;

import lombok.*;

/**
 * Represents the login credentials from a user. If the user logs in, then these credentials will be checked.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginCredentials {

    private String email;
    private String password;

}
