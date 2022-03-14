package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.ResourceConflictException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.User;

import java.util.List;

public interface UserService {
    User getUserById (Long id) throws ResourceNotFoundException;

    Long addUser(User user) throws ResourceNotFoundException, BadRequestException, ResourceConflictException;

    boolean deleteUser(long userId) throws ResourceNotFoundException;

    User updateUser(User user) throws ResourceNotFoundException, BadRequestException;

    List<User> getUsersByName(String name);

    List<User> getUsersByEmail(String email);
}
