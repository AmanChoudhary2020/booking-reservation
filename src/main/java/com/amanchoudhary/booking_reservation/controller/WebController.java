package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.Booking;
import com.amanchoudhary.booking_reservation.service.AppUserService;
import com.amanchoudhary.booking_reservation.service.AppUserService.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private AppUserService userService;

    private List<Booking> bookings = new ArrayList<>(); // Temporary storage for bookings

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
        System.out.println("HI");
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username, @RequestParam String password,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String company) {
        System.out.println("handle register post called!");
        userService.registerUser(username, password, firstName, lastName, company,
                UserRole.USER.toString());
        return "login"; // Redirect to login page after successful registration
    }

    @GetMapping("/home")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "adminHome";
        } else {
            return "userHome";
        }
    }

    @GetMapping("/bookings")
    public String viewBookings(Authentication authentication, Model model) {
        String username = authentication.getName();
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("bookings", bookings);
        } else {
            List<Booking> userBookings = bookings.stream()
                    .filter(booking -> booking.getUsername().equals(username))
                    .collect(Collectors.toList());
            model.addAttribute("bookings", userBookings);
        }
        return "bookings";
    }

    @GetMapping("/createBooking")
    public String showCreateBookingForm() {
        return "createBooking";
    }

    @PostMapping("/createBooking")
    public String createBooking(@RequestParam String inventoryType,
            @RequestParam(required = false) String originCity,
            @RequestParam(required = false) String destinationCity,
            @RequestParam(required = false) String hotelName,
            @RequestParam(required = false) String airlineCode,
            @RequestParam(required = false) String roomType,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String cardLast4,
            @RequestParam double baseAmount,
            @RequestParam double taxAmount,
            @RequestParam double totalAmount,
            @RequestParam String currency,
            Authentication authentication) {
        String username = authentication.getName();
        Booking booking = new Booking(username, inventoryType, originCity, destinationCity, hotelName,
                airlineCode, roomType, startDate, endDate, cardLast4,
                baseAmount, taxAmount, totalAmount, currency);
        bookings.add(booking);
        return "redirect:/bookings";
    }
}
