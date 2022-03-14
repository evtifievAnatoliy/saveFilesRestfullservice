package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.models.Image;
import com.quantori.restfullservice.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnit4.class)
@SpringBootTest
class ImageDbRepositoryTest {

    final static Logger logger = LoggerFactory.getLogger(ImageDbRepositoryTest.class);

    @Autowired
    private ImageDbRepository imageDbRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User anotherTestUser;
    private Image testImage;

    @BeforeAll
    private static void setUp() {
        logger.info("Starting ImageDbRepositoryTest!!!");
    }

    @AfterEach
    private void tearDown() {
        logger.info("Clearing test data!!!");
        imageDbRepository.deleteAll();
    }

    private void insertTestImages() {
        testUser = userRepository.save(new User(null, "first", 21, "first21@mail.ru", null));
        testImage = imageDbRepository.save(new Image(null, "testName", "trstLocation", testUser));

    }

    @Test
    void findByImageIdAndUserIdTest() {
        logger.info("Starting findByImageIdAndUserId");
        logger.info("Inserting DATA");
        insertTestImages();
        assertEquals(testImage.getId(), imageDbRepository.findByImageIdAndUserId(testImage.getId(), testUser.getId()).getId(), "Image id has to be 1");
    }
}