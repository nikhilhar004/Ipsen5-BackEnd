package com.example.HolidayPlanner.services;

import com.example.HolidayPlanner.DAO.UserDao;
import com.example.HolidayPlanner.DTO.UserDTO;
import com.example.HolidayPlanner.models.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getCurrentUserEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public ArrayList<UserDTO> getAllUsersToApiRequest() {
        return userDao.getAllUsersToApiRequest();
    }

    public User findUserByEmail(String email) {
        return userDao.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ArrayList<User> getAllUsers(){
        return this.userDao.getAllUsers();
    }

    public void saveUserToDatabase(User user) {
        this.userDao.saveToDatabase(user);
    }

}
