package com.basic.project.repository;

import com.basic.project.entity.Transaction;
import com.basic.project.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByUser(User user);

	List<Transaction> findTop3ByUserOrderByDateDescTimeDesc(User user);

	List<Transaction> findTop3ByUserOrderByDateDesc(User user);

    List<Transaction> findByUserOrderByDateDescTimeDesc(User user);
}
