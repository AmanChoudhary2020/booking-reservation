package com.amanchoudhary.booking_reservation.service;

import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
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

    public List<Transaction> findTransactionsByCompanyId(Long companyId) {
        return transactionRepository.findByBookingCompanyId(companyId);
    }

    public List<Transaction> findTransactionsByUsername(String username) {
        return transactionRepository.findByBooking_User_Username(username);
    }

    public List<Transaction> findTransactionsByBookingOrigin(String bookingOrigin) {
        return transactionRepository.findByBookingOrigin(bookingOrigin);
    }

    public List<Transaction> findByBookingDestination(String destination) {
        return transactionRepository.findByBookingDestination(destination);
    }

    public List<Transaction> findTransactionsByStartDate(Date startDate) {
        return transactionRepository.findByBooking_StartDate(startDate);
    }

    public List<Transaction> findTransactionsByEndDate(Date endDate) {
        return transactionRepository.findByBooking_EndDate(endDate);
    }

    public List<Transaction> findTransactionsByStatus(String status) {
        return transactionRepository.findByBooking_Status(status);
    }

    public List<Transaction> findTransactionsByCurrency(String currency) {
        return transactionRepository.findByCurrency(currency);
    }

    public List<Transaction> findTransactionsByType(String transactionType) {
        return transactionRepository.findByType(transactionType);
    }
}
