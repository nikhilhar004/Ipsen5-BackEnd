package com.example.HolidayPlanner;



import com.example.HolidayPlanner.services.InvalidMailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInvalidMailService {

    InvalidMailService invalidMailService;

    @BeforeEach
    public void TestInvalidMailService() {
        this.invalidMailService = new InvalidMailService();
    }

    @Test
    public void testWithRegex() {
        String emailAddress = "test@gmail.com";
        String emailAddress2 = "epi23123@live.tu";
        String emailAddress3 = "+++@323..tv";

        assertTrue(invalidMailService.patternMatches(emailAddress));
        assertTrue(invalidMailService.patternMatches(emailAddress2));
        assertFalse(invalidMailService.patternMatches(emailAddress3));
    }
}
