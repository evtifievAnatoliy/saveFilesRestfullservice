package com.quantori.restfullservice.servicies;

import com.quantori.restfullservice.exeption.BadRequestException;
import com.quantori.restfullservice.exeption.ResourceConflictException;
import com.quantori.restfullservice.exeption.ResourceNotFoundException;
import com.quantori.restfullservice.models.User;
import com.quantori.restfullservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long id) throws ResourceNotFoundException {

        Optional<User> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("User has not found.");
        }

        return optional.get();
    }

    @Override
    public Long addUser(User user) throws ResourceConflictException {

        if (userRepository.findByEmail(user.getEmail()).size() > 0) {
            throw new ResourceConflictException("User hasn't added. User email is already in use");
        }

        userRepository.save(user);
        logger.info("User has added: {}", user);
        return user.getId();
    }

    @Override
    public boolean deleteUser(long userId) throws ResourceNotFoundException {

        Optional<User> optional = userRepository.findById(userId);
        if (!optional.isPresent()){
            throw new ResourceNotFoundException(String.format("User with ID: %d is not exists and has not deleted.", userId));
        }

        userRepository.delete(optional.get());
        logger.info("User with ID: {} exists and has deleted.", userId);
        return true;
    }

    @Override
    public User updateUser(User user) throws ResourceNotFoundException, BadRequestException {

        Optional<User> optional = userRepository.findById(user.getId());

        if (!optional.isPresent()) {
            throw new ResourceNotFoundException(String.format("Warning. User is not exists and has not updated: %s", user.toString()));
        }
        if (user.getName() == null && user.getAge() == null && user.getEmail() == null) {
            throw new BadRequestException("Warning. Bad request. user name, age and email are null.");
        }
        if (user.getName() !=null) {
            optional.get().setName(user.getName());
        }
        if (user.getAge() !=null) {
            optional.get().setAge(user.getAge());
        }
        if (user.getEmail() !=null) {
            optional.get().setEmail(user.getEmail());
        }
        userRepository.save(optional.get());
        logger.info("User exists and has updated: {}", user);
        return optional.get();
    }

    @Override
    public List<User> getUsersByName(String name) {

        List<User> list = new ArrayList<>();
        list = userRepository.findByName(name);

        logger.info("Users list: {}", list.stream().map(Object::toString).collect(Collectors.joining(",")));
        return list;
    }

    @Override
    public List<User> getUsersByEmail(String email) {

        List<User> list = new ArrayList<>();
        list = userRepository.findByEmail(email);

        return list;
    }

}
