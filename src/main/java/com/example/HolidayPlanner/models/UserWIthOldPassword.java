package com.example.HolidayPlanner.models;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserWIthOldPassword {

    User user;
    String oldPassword;
}
