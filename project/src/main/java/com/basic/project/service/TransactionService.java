package com.basic.project.service;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.basic.project.entity.Transaction;
import com.basic.project.entity.User;
import com.basic.project.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    
    public void createIncomeTransaction(User user, double amount,Time time, Date date, String description, String type) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTime(time);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type); // กำหนดประเภทรายการรายรับ
        transactionRepository.save(transaction);
    }
    public void createExpenseTransaction(User user, double amount,Time time, Date date, String description, String type) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTime(time);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setType(type); // กำหนดประเภทรายการรายจ่าย
        transactionRepository.save(transaction);
    }

    public List<Transaction> getLatestTransactions(User user, int count) {
        // ดึงรายการ Transaction ล่าสุด count รายการ
        List<Transaction> latestTransactions = transactionRepository.findTop3ByUserOrderByDateDescTimeDesc(user);
        
        return latestTransactions;
    }

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

	public List<Transaction> getAllTransactions(User user) {
        List<Transaction> latestTransactions = transactionRepository.findByUserOrderByDateDescTimeDesc(user);
        return latestTransactions;
	}

	public Transaction getTransactionById(int id) {
	    // Use the repository to find the transaction by ID
	    return transactionRepository.findById((long) id).orElse(null);
	}


}