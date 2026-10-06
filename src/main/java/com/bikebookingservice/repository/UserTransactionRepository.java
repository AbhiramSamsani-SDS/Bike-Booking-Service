package com.bikebookingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bikebookingservice.entity.UserTransactions;

public interface UserTransactionRepository extends JpaRepository<UserTransactions, Integer> {
    Page<UserTransactions> findByUser_UserId(int userId, Pageable pageable);
}
