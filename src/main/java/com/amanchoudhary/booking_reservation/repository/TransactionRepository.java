package com.amanchoudhary.booking_reservation.repository;

import com.amanchoudhary.booking_reservation.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBookingId(Long bookingId);

    List<Transaction> findByBookingCompanyId(Long companyId);

    List<Transaction> findByBooking_User_Username(String username);

    List<Transaction> findByBookingOrigin(String bookingOrigin);

    List<Transaction> findByBookingDestination(String destination);

    List<Transaction> findByBooking_StartDate(Date startDate);

    List<Transaction> findByBooking_EndDate(Date endDate);

    List<Transaction> findByBooking_Status(String status);

    List<Transaction> findByCurrency(String currency);

    List<Transaction> findByType(String transactionType);

}
