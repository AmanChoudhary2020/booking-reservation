package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.service.AppUserService;
import com.amanchoudhary.booking_reservation.service.BookingService;
import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.model.TransactionType;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            AppUser user = appUserService.findByUsername(username);

            if (user != null) {
                if ("ADMIN".equals(user.getRole())) {
                    return "adminHome";
                } else if ("USER".equals(user.getRole())) {
                    return "userHome";
                }
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/bookings")
    public String getUserBookings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        AppUser user = appUserService.findByUsername(username);
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getId());

        model.addAttribute("bookings", bookings);

        return "bookings";
    }

    @GetMapping("/createBooking")
    public String showCreateBookingForm() {
        return "createBooking";
    }

}
