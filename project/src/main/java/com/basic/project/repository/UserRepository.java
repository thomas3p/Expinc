package com.basic.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.basic.project.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	User findBySessionToken(String sessionToken);
	
	

}

