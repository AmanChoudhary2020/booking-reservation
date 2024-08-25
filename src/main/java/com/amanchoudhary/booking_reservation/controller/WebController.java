package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.service.AppUserService;
import com.amanchoudhary.booking_reservation.service.BookingService;
import com.amanchoudhary.booking_reservation.service.TransactionService;
import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.model.TransactionType;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private TransactionService transactionService;

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

    @GetMapping("/bookings/{bookingId}")
    public String getBookingById(@PathVariable Long bookingId, Model model) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            model.addAttribute("booking", booking);
            return "createBooking";
        } else {
            return "redirect:/bookings";
        }
    }

    @GetMapping("/createBooking")
    public String showCreateBookingForm() {
        return "createBooking";
    }

    @GetMapping("/reports")
    public String getTransactions(
            @RequestParam(required = false, name = "searchType") String searchType,
            @RequestParam(required = false, name = "searchTerm") String searchTerm,
            @RequestParam(required = false, name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "currency") String currency,
            @RequestParam(required = false, name = "transactionType") String transactionType,
            Model model) {

        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authUsername = authentication.getName();
        AppUser user = appUserService.findByUsername(authUsername);

        // Get the company ID for the user
        Long companyId = user.getCompany().getId();

        System.out.println("reports hi");
        System.out.println(searchType);
        System.out.println(endDate);

        // Fetch transactions for the company
        List<Transaction> transactions = transactionRepository.findByBookingCompanyId(companyId);

        // Apply filters based on search type and term
        if (searchType != null && !searchType.trim().isEmpty()) {
            if ("username".equals(searchType) && searchTerm != null && !searchTerm.trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getBooking().getUser().getUsername().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList());
            } else if ("bookingOrigin".equals(searchType) && searchTerm != null && !searchTerm.trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getBooking().getOrigin().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList());
            } else if ("bookingDestination".equals(searchType) && searchTerm != null && !searchTerm.trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getBooking().getDestination().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList());
            } else if ("startDate".equals(searchType) && startDate != null) {
                transactions = transactions.stream()
                        .filter(t -> !t.getBooking().getStartDate().isBefore(startDate))
                        .collect(Collectors.toList());
            } else if ("endDate".equals(searchType) && endDate != null) {
                transactions = transactions.stream()
                        .filter(t -> !t.getBooking().getEndDate().isAfter(endDate))
                        .collect(Collectors.toList());
            } else if ("status".equals(searchType) && status != null && !status.trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getBooking().getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());
            } else if ("currency".equals(searchType) && searchTerm != null && !searchTerm.trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getCurrency().equalsIgnoreCase(searchTerm))
                        .collect(Collectors.toList());
            } else if ("transactionType".equals(searchType) && transactionType != null
                    && !transactionType.trim().isEmpty()) {
                try {
                    TransactionType type = TransactionType.valueOf(transactionType.toUpperCase());
                    transactions = transactions.stream()
                            .filter(t -> t.getType() == type)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    // Handle invalid TransactionType
                    transactions = List.of();
                }
            }
        }

        // Add attributes to the model
        model.addAttribute("transactions", transactions);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("status", status);
        model.addAttribute("currency", currency);
        model.addAttribute("transactionType", transactionType);

        return "reports";
    }

}
