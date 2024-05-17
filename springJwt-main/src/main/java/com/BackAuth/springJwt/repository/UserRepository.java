package com.BackAuth.springJwt.repository;

import com.BackAuth.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    @Override
    Optional<User> findById(Integer integer);
}
