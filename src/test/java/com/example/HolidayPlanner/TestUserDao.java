package com.example.HolidayPlanner;

import com.example.HolidayPlanner.DAO.UserDao;
import com.example.HolidayPlanner.DAO.UserRepository;
import com.example.HolidayPlanner.DTO.UserDTO;
import com.example.HolidayPlanner.models.User;
import com.example.HolidayPlanner.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.when;

public class TestUserDao {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByEmail() {
        // ARRANGE
        User user1 = createUser("John Doe", "john@example.com");
        User user2 = createUser("Jane Smith", "jane@example.com");
        User user3 = createUser("Bob Johnson", "bob@example.com");

        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        when(userRepository.findAll()).thenReturn(users);

        // ACT - ASSERT
        // Test when email matches
        Optional<User> result1 = userDao.findByEmail("john@example.com");
        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals(user1, result1.get());

        // Test when email partially matches
        Optional<User> result2 = userDao.findByEmail("jane");
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals(user2, result2.get());

        // Test when email does not match
        Optional<User> result3 = userDao.findByEmail("foo@example.com");
        Assertions.assertTrue(result3.isEmpty());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    public void testGetAllUsersToApiRequest() {
        // ARRANGE
        User user1 = createUser(UUID.randomUUID(), "John Doe", UUID.randomUUID(), UUID.randomUUID(), "manager");
        User user2 = createUser(UUID.randomUUID(), "Jane Smith", UUID.randomUUID(), UUID.randomUUID(), "employee");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // ACT
        ArrayList<UserDTO> result = userDao.getAllUsersToApiRequest();

        // ASSERT
        Assertions.assertEquals(users.size(), result.size());

        UserDTO userDTO1 = result.get(0);
        Assertions.assertEquals(user1.getId(), userDTO1.getId());
        Assertions.assertEquals(user1.getFullName(), userDTO1.getFullName());
        Assertions.assertEquals(user1.getTeamId(), userDTO1.getTeamId());
        Assertions.assertEquals(user1.getChapterId(), userDTO1.getChapterId());
        Assertions.assertEquals(user1.getRole(), userDTO1.getRole());

        UserDTO userDTO2 = result.get(1);
        Assertions.assertEquals(user2.getId(), userDTO2.getId());
        Assertions.assertEquals(user2.getFullName(), userDTO2.getFullName());
        Assertions.assertEquals(user2.getTeamId(), userDTO2.getTeamId());
        Assertions.assertEquals(user2.getChapterId(), userDTO2.getChapterId());
        Assertions.assertEquals(user2.getRole(), userDTO2.getRole());
    }

    private User createUser(UUID id, String fullName, UUID teamId, UUID chapterId, String role) {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setTeamId(teamId);
        user.setChapterId(chapterId);
        user.setRole(role);
        return user;
    }

}
