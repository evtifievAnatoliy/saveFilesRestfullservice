package com.quantori.restfullservice.repositories;

import com.quantori.restfullservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository <User,Long>{

    List<User> findByName(String name);

    List<User> findByEmail(String email);

}
