package com.example.HolidayPlanner.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiResponse<Type> {
    private HttpStatus code;
    private Type payload;
    private String message;

    public ApiResponse(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(HttpStatus code, Type payload) {
        this.code = code;
        this.payload = payload;
    }
}