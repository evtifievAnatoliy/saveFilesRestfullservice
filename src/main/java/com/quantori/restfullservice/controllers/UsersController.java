package com.quantori.restfullservice.controllers;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.FileSystemRepositoryException;
import com.quantori.restfullservice.exeption.ResourceConflictException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.User;
import com.quantori.restfullservice.servicies.FileLocationServiceImpl;
import com.quantori.restfullservice.servicies.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequestMapping("api/users")
public class UsersController {

    final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private UserServiceImpl userService;
    private FileLocationServiceImpl fileLocationService;

    @Autowired
    public UsersController(UserServiceImpl userService, FileLocationServiceImpl fileLocationService) {
        this.userService = userService;
        this.fileLocationService = fileLocationService;
    }

    @GetMapping(value ="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUser(@PathVariable long userId) throws ResourceNotFoundException {

        logger.info("Try to get user with ID: {}", userId);
        final User user = userService.getUserById(userId);
        logger.info("User : {}", user.toString());
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PostMapping(value ="/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user) throws ResourceConflictException {

        logger.info("Try to add: {}", user);
        userService.addUser(user);
        return new ResponseEntity<>(String.format("User has added. ID: %d", user.getId()), HttpStatus.CREATED);

    }

    @PatchMapping(value ="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@RequestBody User user) throws ResourceNotFoundException, BadRequestException {

        logger.info("Try to update: {}", user);

        return new ResponseEntity<>(userService.updateUser(user),HttpStatus.OK);
    }

    @DeleteMapping(value ="/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) throws ResourceNotFoundException {

        logger.info("Try to delete user with ID: {}", userId);
        final boolean isDeleted = userService.deleteUser(userId);

        return new ResponseEntity<>("User has deleted", HttpStatus.OK);

    }

    @PostMapping(value ="/list/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findUsersByName(@RequestParam("userName") String userName) {

        logger.info("Try to find Users with name: {}", userName);

        return new ResponseEntity<>(userService.getUsersByName(userName), HttpStatus.OK);
    }

    @PostMapping(value ="/{userId}/images/upload", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> uploadUserImage(@PathVariable long userId, @RequestPart("file") MultipartFile image) throws FileSystemRepositoryException, BadRequestException, IOException, ResourceNotFoundException {

        logger.info("Try to add image to user: {}", userId);
        Long id = fileLocationService.save(image, userId);
        return new ResponseEntity<>(String.format("Image has added. ID: %d", id),
            HttpStatus.CREATED);

    }

    @GetMapping(value ="/{userId}/images/{imageId}", produces = MediaType.ALL_VALUE)
    FileSystemResource downloadImage(@PathVariable long userId, @PathVariable long imageId) throws Exception {
        return fileLocationService.find(imageId, userId);
    }

}
