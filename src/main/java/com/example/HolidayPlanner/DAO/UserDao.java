package com.example.HolidayPlanner.DAO;

import com.example.HolidayPlanner.DTO.UserDTO;
import com.example.HolidayPlanner.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserDao {
    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveToDatabase(User user) {
        this.userRepository.save(user);
    }
    public ArrayList<User> getAllUsers() {
        return (ArrayList<User>) this.userRepository.findAll();
    }

    public ArrayList<UserDTO> getAllUsersToApiRequest(){
        List<User> users = this.userRepository.findAll();
        ArrayList<UserDTO> usersWithoutSensitiveInformation = new ArrayList<>();
        for (User user : users) {
            usersWithoutSensitiveInformation.add(new UserDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getTeamId(),
                    user.getChapterId(),
                    user.getRole()
            ));
        }
        return usersWithoutSensitiveInformation;
    }

    public Optional<User> findByEmail(String email) {
        ArrayList<User> users = (ArrayList<User>) userRepository.findAll();

        for (User user : users) {
            if (user.getEmail().contains(email)) {

                return Optional.ofNullable(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User>findByUserID(UUID uuid){
        return this.userRepository.findById(uuid);
    };


}
