package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.models.User;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnit4.class)
@SpringBootTest
class UserRepositoryTest {

    final static Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    private static void setUp() {
        logger.info("Starting UserRepositoryTest!!!");
    }

    @AfterEach
    private void tearDown() {
        logger.info("Clearing test data!!!");
        userRepository.deleteAll();
    }

    @Test
    void findByNameTest() {
        logger.info("Starting findByNameTest");
        logger.info("Inserting DATA");
        insertTestUsers();
        assertEquals(2, userRepository.findByName("first").size(), "Filter first ArraySize have to be 2");
        assertEquals(1, userRepository.findByName("second").size(), "Filter second ArraySize have to be 1");
        assertEquals(0, userRepository.findByName("some").size(), "Filter some ArraySize have to be 1");
    }

    @Test
    void findByEmailTest() {
        logger.info("Starting findByNameTest");
        logger.info("Inserting DATA");
        insertTestUsers();
        assertEquals(1, userRepository.findByEmail("first20@mail.ru").size(), "Filter first20@mail.ru ArraySize have to be 1");
        assertEquals(1, userRepository.findByEmail("first21@mail.ru").size(), "Filter first21@mail.ru ArraySize have to be 1");
        assertEquals(1, userRepository.findByEmail("second21@mail.ru").size(), "Filter second21@mail.ru ArraySize have to be 1");
        assertEquals(0, userRepository.findByEmail("some@mail.ru").size(), "Filter some@mail.ru ArraySize have to be 0");
    }

    private void insertTestUsers() {
        userRepository.save(new User(null, "first", 20, "first20@mail.ru", null));
        userRepository.save(new User(null, "first", 21, "first21@mail.ru", null));
        userRepository.save(new User(null, "second", 21, "second21@mail.ru", null));
    }

}