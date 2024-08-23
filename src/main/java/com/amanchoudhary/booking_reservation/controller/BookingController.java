package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.model.TransactionType;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;
import com.amanchoudhary.booking_reservation.service.AppUserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        AppUser user = appUserService.findByUsername(username);

        if (user != null) {
            Company company = user.getCompany();
            if (company != null) {
                booking.setUser(user);
                booking.setCompany(company);
                booking.setStatus("CONFIRMED");

                Booking savedBooking = bookingRepository.save(booking);

                Transaction transaction = new Transaction();
                transaction.setBooking(savedBooking);
                transaction.setType(TransactionType.CHARGE);
                transaction.setAmount(savedBooking.getTotalAmount());
                transaction.setCurrency(savedBooking.getCurrency());
                transaction.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);

                return savedBooking;
            } else {
                throw new RuntimeException("Company not found for user");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @PostMapping("/{bookingId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Long bookingId) {
        Map<String, Object> response = new HashMap<>();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStartDate().isAfter(LocalDate.now())) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            // Record the cancellation in the transaction table
            Transaction transaction = new Transaction();
            transaction.setBooking(booking);
            transaction.setType(TransactionType.REFUND);
            transaction.setAmount(booking.getTotalAmount());
            transaction.setCurrency(booking.getCurrency());
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

            // Return success response
            response.put("success", true);
            response.put("message", "Booking has been cancelled successfully.");
        } else {
            // Return failure response
            response.put("success", false);
            response.put("message", "Cannot cancel booking that has already started.");
        }

        return ResponseEntity.ok(response);
    }

}
