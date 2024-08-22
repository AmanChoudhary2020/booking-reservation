package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.model.TransactionType;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;
import com.amanchoudhary.booking_reservation.service.AppUserService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
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
    private TransactionRepository transactionRepository; // Inject the TransactionRepository

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
}
