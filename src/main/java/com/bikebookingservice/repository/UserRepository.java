package com.bikebookingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bikebookingservice.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
	
	Users findByUserIdAndPasswordAndActiveTrue(int userId, String password);

	boolean existsByEmail(String email);
}
