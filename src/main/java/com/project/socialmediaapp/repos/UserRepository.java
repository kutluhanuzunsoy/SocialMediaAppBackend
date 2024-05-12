package com.project.socialmediaapp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.socialmediaapp.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);
}
