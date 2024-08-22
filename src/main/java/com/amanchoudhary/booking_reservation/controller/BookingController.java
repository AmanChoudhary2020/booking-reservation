package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.service.AppUserService;
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

                return bookingRepository.save(booking);
            } else {
                throw new RuntimeException("Company not found for user");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
