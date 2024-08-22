package com.amanchoudhary.booking_reservation.service;

import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(Transaction transaction) {
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByBookingId(Long bookingId) {
        return transactionRepository.findByBookingId(bookingId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
