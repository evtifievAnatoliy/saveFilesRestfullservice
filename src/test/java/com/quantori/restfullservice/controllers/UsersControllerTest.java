package com.quantori.restfullservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@RunWith(JUnit4.class)
@SpringBootTest
class UsersControllerTest {

    final static Logger logger = LoggerFactory.getLogger(UsersControllerTest.class);

    private static User testUser;

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileSystemRepository fileSystemRepository;
    @Autowired
    private ImageDbRepository imageDbRepository;
    @Autowired
    private DataProperties dataProperties;


    @BeforeAll
    private static void setUp() {
        logger.info("Starting UsersControllerTest!!!");
        testUser = new User(null, "first", 20, "first20@mail.ru", null);
    }

    @AfterEach
    void tearDown() {
        imageDbRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUserTest() throws Exception {
        User testDbUser = userRepository.save(testUser);

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/{userId}";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                                    .get(uri, testDbUser.getId())
                                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                                    .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(testDbUser.toString(), mapFromJson(mvcResult.getResponse().getContentAsString(), User.class).toString());

        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .get(uri, testDbUser.getId()+1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        assertEquals("User has not found.", mvcResult.getResponse().getContentAsString());

    }

    @Test
    void addUserTest() throws Exception {

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .post(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(testUser)))
                .andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("User has added. ID: "));

        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .post(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(testUser)))
                .andReturn();
        assertEquals(409, mvcResult.getResponse().getStatus());
        assertEquals("User hasn't added. User email is already in use", mvcResult.getResponse().getContentAsString());

    }

    @Test
    void updateUserTest() throws Exception {

        User testDbUser = userRepository.save(testUser);
        new User(null, "first", 20, "first20@mail.ru", null);
        testDbUser.setName("second");
        testDbUser.setAge(21);
        testDbUser.setEmail("second20@mail.ru");

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .patch(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(testDbUser)))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(testDbUser.toString(), mapFromJson(mvcResult.getResponse().getContentAsString(), User.class).toString());


        User badRequestUser = new User(testDbUser.getId(), null, null, null, null);
        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .patch(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(badRequestUser)))
                .andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());
        assertEquals("Warning. Bad request. user name, age and email are null.", mvcResult.getResponse().getContentAsString());

        testDbUser.setId(testDbUser.getId()+1);
        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .patch(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(testDbUser)))
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        assertEquals(String.format("Warning. User is not exists and has not updated: %s", testDbUser.toString()), mvcResult.getResponse().getContentAsString());

    }

    @Test
    void deleteUserTest() throws Exception {

        User testDbUser = userRepository.save(testUser);

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/{userId}";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .delete(uri, testDbUser.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals("User has deleted", mvcResult.getResponse().getContentAsString());

        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .delete(uri, testDbUser.getId()+1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        assertEquals(String.format(String.format("User with ID: %d is not exists and has not deleted.", testDbUser.getId()+1), testDbUser.getId()+1), mvcResult.getResponse().getContentAsString());

    }

    @Test
    void findUsersByNameTest() throws Exception {
        User testDbUser = userRepository.save(testUser);
        User anotherTestDbUser = userRepository.save(new User(null, testDbUser.getName(), 20, "second@mail.ru", null));

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/list/";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .post(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("userName", testDbUser.getName()))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        List<User> testUsersList = mapListFromJson(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(2, testUsersList.size());
        assertEquals(testDbUser.toString(), testUsersList.get(0).toString());
        assertEquals(anotherTestDbUser.toString(), testUsersList.get(1).toString());

        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .post(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("userName", "userNotFoundedName"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertEquals(0, mapListFromJson(mvcResult.getResponse().getContentAsString(), User.class).size());
    }

    @Test
    void uploadUserImageTest() throws Exception {
        User testUser = userRepository.save(new User(null, "first", 21, "first21@mail.ru", null));
        String testString = "testData";
        byte [] testContent = testString.getBytes(StandardCharsets.UTF_8);
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testName.txt", MediaType.TEXT_PLAIN_VALUE, testContent);

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/{userId}/images/upload";

        MvcResult mvcResult =  mvc.perform(multipart(uri, testUser.getId())
                        .file(multipartFile)
                        .contentType(MediaType.ALL_VALUE))
                .andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
        File dir = new File(dataProperties.getPATH());
        File[] files = dir.listFiles((dir1, name) -> name.endsWith("testName.txt"));
        logger.info("test");
        assertEquals(1, files.length);
        Files.delete(files[0].toPath());


        mvcResult =  mvc.perform(multipart(uri, testUser.getId())
                        .contentType(MediaType.ALL_VALUE))
                .andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

        mvcResult =  mvc.perform(multipart(uri, testUser.getId()+1)
                        .file(multipartFile)
                        .contentType(MediaType.ALL_VALUE))
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        assertEquals("User is not founded", mvcResult.getResponse().getContentAsString());

    }

    @Test
    void downloadImageTest() throws Exception {
        String testString = "testData";
        byte [] testContent = testString.getBytes(StandardCharsets.UTF_8);
        Path testFile = Paths.get(dataProperties.getPATH() + new Date().getTime() + "-" + "testImage");
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, testContent);
        try {
            Files.createDirectories(testFile.getParent());
            Files.write(testFile, testContent);
        } catch (IOException e) {
            throw new FileSystemRepositoryException("Saving files to local drive error");
        }
        User testUser = userRepository.save(new User(null, "first", 21, "first21@mail.ru", null));
        Image testImage = imageDbRepository.save(new Image(null, testFile.getFileName().toString(), testFile.toAbsolutePath().toString(), testUser));


        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        String uri = "/api/users/{userId}/images/{imageId}";
        MvcResult mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .get(uri, testUser.getId(), testImage.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        mvcResult =  mvc.perform(MockMvcRequestBuilders
                        .get(uri, testUser.getId()+1, testImage.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
        assertEquals(String.format("Image with imageId: %d and userId: %d is not exists.",
                testImage.getId(), testUser.getId()+1), mvcResult.getResponse().getContentAsString());

        Files.delete(testFile);
    }

    protected <T>T mapFromJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    protected <T> List<T> mapListFromJson (String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        return objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, clazz));
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }


}