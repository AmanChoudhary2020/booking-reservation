package com.amanchoudhary.booking_reservation.repository;

import com.amanchoudhary.booking_reservation.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBookingId(Long bookingId);
}
