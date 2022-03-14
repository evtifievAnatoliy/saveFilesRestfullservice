package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.ResourceConflictException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.User;
import com.quantori.restfullservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnit4.class)
@SpringBootTest
class UserServiceImplTest {

    final static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    private static UserRepository userRepository;

    private UserService userService = new UserServiceImpl(userRepository);

    private static User testUser;

    @BeforeAll
    private static void setUp() {
        logger.info("Starting UserServiceImplTest!!!");
        userRepository = Mockito.mock(UserRepository.class);

        testUser = new User(1L, "first", 20, "first20@mail.ru", null);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserByIdTestTest() throws ResourceNotFoundException {
        logger.info("Starting ThrowResourceNotFoundException test");
        User testUser = new User(1L, "first", 20, "first20@mail.ru", null);
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(2L);
        });
        assertTrue(exception.getMessage().contains("User has not found."));

        logger.info("Starting assert true tests");
        User user = userService.getUserById(1L);
        assertTrue(user.getName().equals(testUser.getName()));
        assertTrue(user.getAge().equals(testUser.getAge()));
        assertTrue(user.getEmail().equals(testUser.getEmail()));
    }

    @Test
    void addUserTestTest() {
        User newUser = new User(1L, "first", 20, "emtyItemEmail@mail.ru", null);
        User existUser = new User(1L, "first", 20, "oneItemEmail@mail.ru", null);
        List<User> listWithOneItem = Arrays.asList(existUser);
        List<User> listWithoutItem = new ArrayList<>();

        Mockito.when(userRepository.findByEmail("oneItemEmail@mail.ru")).thenReturn(listWithOneItem);
        Mockito.when(userRepository.findByEmail("emtyItemEmail@mail.ru")).thenReturn(listWithoutItem);
        Mockito.when(userRepository.save(newUser)).thenReturn(newUser);

        logger.info("Starting ResourceConflictException test");
        Exception exception = assertThrows(ResourceConflictException.class, () -> {
            userService.addUser(existUser);
        });
        assertTrue(exception.getMessage().contains("User hasn't added. User email is already in use"));

        logger.info("Starting assert true tests");
        assertTrue(newUser.getId().equals(1L));

    }

    @Test
    void deleteUserTest() throws ResourceNotFoundException {
        logger.info("Starting ResourceNotFoundException test");
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Mockito.doNothing().when(userRepository).delete(testUser);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(2L);
        });
        assertTrue(exception.getMessage().contains("User with ID: 2 is not exists and has not deleted."));

        logger.info("Starting assert true tests");
        assertTrue(userService.deleteUser(testUser.getId()));
    }

    @Test
    void updateUserTest() throws BadRequestException, ResourceNotFoundException {
        User userNotFound = new User(2L, "first", 20, "emtyItemEmail@mail.ru", null);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Exception resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userNotFound);
        });
        assertTrue(resourceNotFoundException.getMessage().contains(String.format("Warning. User is not exists and has not updated: %s", userNotFound.toString())));

        User userBadRequest = new User(3L, null, null, null, null);
        Mockito.when(userRepository.findById(userBadRequest.getId())).thenReturn(Optional.of(userBadRequest));
        Exception badRequestException = assertThrows(BadRequestException.class, () -> {
            userService.updateUser(userBadRequest);
        });
        assertTrue(badRequestException.getMessage().contains(String.format("Warning. Bad request. user name, age and email are null.")));

        User dbUser = new User(1L, null, null, null, null);
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(dbUser));
        assertEquals(testUser, userService.updateUser(testUser), "Expected User didn't match");

    }

    @Test
    void getUsersByNameTest() {
        List<User> emptyList = new ArrayList<>();
        Mockito.when(userRepository.findByName("emptyName")).thenReturn(emptyList);
        assertEquals(0L, userService.getUsersByName("emptyName").size(), "ArrayList size have to be 0");

        List<User> notEmptyList = new ArrayList<>();
        notEmptyList.add(testUser);
        Mockito.when(userRepository.findByName(testUser.getName())).thenReturn(notEmptyList);
        assertEquals(1L, userService.getUsersByName(testUser.getName()).size(), "ArrayList size have to be 1");

    }

    @Test
    void getUsersByEmailTest() {
        List<User> emptyList = new ArrayList<>();
        Mockito.when(userRepository.findByEmail("empty@mail.ru")).thenReturn(emptyList);
        assertEquals(0L, userService.getUsersByEmail("empty@mail.ru").size(), "ArrayList size have to be 0");

        List<User> notEmptyList = new ArrayList<>();
        notEmptyList.add(testUser);
        Mockito.when(userRepository.findByEmail(testUser.getEmail())).thenReturn(notEmptyList);
        assertEquals(1L, userService.getUsersByEmail(testUser.getEmail()).size(), "ArrayList size have to be 1");

    }

}