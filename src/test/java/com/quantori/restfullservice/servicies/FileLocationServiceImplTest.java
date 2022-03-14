package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.Image;
import com.quantori.restfullservice.models.User;
import com.quantori.restfullservice.properties.DataProperties;
import com.quantori.restfullservice.repositories.FileSystemRepository;
import com.quantori.restfullservice.repositories.ImageDbRepository;
import com.quantori.restfullservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
@SpringBootTest
class FileLocationServiceImplTest {

    final static Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    private static FileSystemRepository fileSystemRepository;
    private static UserRepository userRepository;
    private static ImageDbRepository imageDbRepository;
    private static DataProperties dataProperties;

    private static User testUser;
    private static Image testDbImage;
    private static String testString;
    private static MultipartFile testImage;



    private FileLocationService fileLocationService = new FileLocationServiceImpl(userRepository, imageDbRepository, fileSystemRepository, dataProperties);

    @BeforeAll
    private static void setUp() {
        logger.info("Starting FileLocationServiceImplTest!!!");
        fileSystemRepository = Mockito.mock(FileSystemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        imageDbRepository = Mockito.mock(ImageDbRepository.class);
        dataProperties = Mockito.mock(DataProperties.class);

        testUser = new User(1L, "first", 20, "first20@mail.ru", null);

        testString = "testData";
        testImage = new MockMultipartFile("testFileName", testString.getBytes(StandardCharsets.UTF_8));

        testDbImage = new Image();
        testDbImage.setName(testImage.getOriginalFilename());
        testDbImage.setLocation("testLocation");
        testDbImage.setUser(testUser);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void saveTest() throws Exception {

        Exception badRequestException = assertThrows(BadRequestException.class, () -> {
            fileLocationService.save(null, testUser.getId());
        });
        assertTrue(badRequestException.getMessage().contains("There is no data in request"));

        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Exception resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            fileLocationService.save(testImage, 2L);
        });
        assertTrue(resourceNotFoundException.getMessage().contains("User is not founded"));

        Mockito.when(dataProperties.getSAVELOCAL()).thenReturn(Boolean.TRUE);
        Mockito.when(fileSystemRepository.save(testImage.getBytes(), testImage.getOriginalFilename())).thenReturn("testLocation");

        Image newDbImage = new Image();
        newDbImage.setId(1L);
        Mockito.when(imageDbRepository.save(testDbImage)).thenReturn(newDbImage);
        assertEquals(1L, fileLocationService.save(testImage, testUser.getId()), "Image ID has to be 1");

    }

    @Test
    void findTest() throws ResourceNotFoundException {
        Mockito.when(imageDbRepository.findByImageIdAndUserId(testDbImage.getId() ,testUser.getId())).thenReturn(testDbImage);
        Mockito.when(imageDbRepository.findByImageIdAndUserId(2L ,testUser.getId())).thenReturn(null);

        Exception resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
            fileLocationService.find(2L, testUser.getId());
        });
        assertTrue(resourceNotFoundException.getMessage().contains("Image with imageId: 2 and userId: 1 is not exists."));

        FileSystemResource testFileSystemResource = new FileSystemResource(testString);
        Mockito.when(fileSystemRepository.findInFileSystem("testLocation")).thenReturn(testFileSystemResource);
        assertEquals(testFileSystemResource, fileLocationService.find(testDbImage.getId() ,testUser.getId()), "Expected FileSystemResource didn't match");
    }
}