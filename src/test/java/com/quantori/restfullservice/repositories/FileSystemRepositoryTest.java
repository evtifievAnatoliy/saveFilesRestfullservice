package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
import com.quantori.restfullservice.properties.DataProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnit4.class)
@SpringBootTest
class FileSystemRepositoryTest {

    final static Logger logger = LoggerFactory.getLogger(FileSystemRepositoryTest.class);

    @Autowired
    private FileSystemRepository fileSystemRepository;

    @Autowired
    private DataProperties dataProperties;

    String testString = "testData";
    byte [] testContent = testString.getBytes(StandardCharsets.UTF_8);
    Path testFile;

    @BeforeAll
    private static void setUp() {
        logger.info("Starting FileSystemRepositoryTest!!!");
    }

    @AfterEach
    private void tearDown() throws IOException {
        logger.info("Clearing test data!!!");
        Files.delete(testFile);
    }

    private void insertTestFiles() throws FileSystemRepositoryException {
        testFile = Paths.get(dataProperties.getPATH() + new Date().getTime() + "-" + "testImage");
        try {
            Files.createDirectories(testFile.getParent());
            Files.write(testFile, testContent);
        } catch (IOException e) {
            throw new FileSystemRepositoryException("Saving files to local drive error");
        }
    }

    @Test
    void saveTest() throws FileSystemRepositoryException, IOException {
        testFile = Paths.get(dataProperties.getPATH() + new Date().getTime() + "-" + "testImage");
        String pathStr = fileSystemRepository.save(testContent, testString);
        testFile = Paths.get(pathStr);
        String data = Files.readString(testFile);
        assertEquals("testData", data, "Data has to be testData");

    }

    @Test
    void findInFileSystemTest() throws FileSystemRepositoryException {
        insertTestFiles();
        FileSystemResource fileSystemResource = fileSystemRepository.findInFileSystem(testString);
        assertEquals(testString, fileSystemResource.getPath(), "Data has to be testData");
    }
}