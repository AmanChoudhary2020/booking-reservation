package com.amanchoudhary.booking_reservation.controller;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Booking;
import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.model.Transaction;
import com.amanchoudhary.booking_reservation.model.TransactionType;
import com.amanchoudhary.booking_reservation.repository.BookingRepository;
import com.amanchoudhary.booking_reservation.repository.TransactionRepository;
import com.amanchoudhary.booking_reservation.service.AppUserService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                transaction.setBaseAmount(booking.getBaseAmount());
                transaction.setTaxAmount(booking.getTaxAmount());
                transaction.setTotalAmount(booking.getTotalAmount());
                transaction.setCurrency(booking.getCurrency());
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
            transaction.setBaseAmount(booking.getBaseAmount());
            transaction.setTaxAmount(booking.getTaxAmount());
            transaction.setTotalAmount(booking.getTotalAmount());
            transaction.setCurrency(booking.getCurrency());
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);

            response.put("success", true);
            response.put("message", "Booking has been cancelled successfully.");
        } else {
            response.put("success", false);
            response.put("message", "Cannot cancel booking that has already started.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}/invoice")
    public ResponseEntity<InputStreamResource> getInvoice(@PathVariable Long bookingId) throws IOException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        AppUser user = booking.getUser();
        Company company = booking.getCompany();
        List<Transaction> transactions = transactionRepository.findByBookingId(bookingId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Company Info:"));
            document.add(new Paragraph("Company Name: " + company.getCompanyName()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("User Info:"));
            document.add(new Paragraph("First Name: " + user.getFirstName()));
            document.add(new Paragraph("Last Name: " + user.getLastName()));
            document.add(new Paragraph("Role: " + user.getRole()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Booking Info:"));
            document.add(new Paragraph("Inventory Type: " + booking.getInventoryType()));

            if ("hotel".equalsIgnoreCase(booking.getInventoryType())) {
                document.add(new Paragraph("Hotel Name: " + booking.getHotelName()));
                document.add(new Paragraph("Room Type: " + booking.getRoomType()));
                document.add(new Paragraph("Start Date: " + booking.getStartDate()));
                document.add(new Paragraph("End Date: " + booking.getEndDate()));
            } else if ("flight".equalsIgnoreCase(booking.getInventoryType())) {
                document.add(new Paragraph("Origin City: " + booking.getOrigin()));
                document.add(new Paragraph("Destination City: " + booking.getDestination()));
                document.add(new Paragraph("Airline Code: " + booking.getAirlineCode()));
                document.add(new Paragraph("Start Date: " + booking.getStartDate()));
                document.add(new Paragraph("End Date: " + booking.getEndDate()));
            }

            document.add(new Paragraph("Status: " + booking.getStatus()));
            document.add(new Paragraph("Card Last 4 Digits: " + booking.getLast4DigitsCard()));
            document.add(new Paragraph("Base Amount: " + booking.getBaseAmount()));
            document.add(new Paragraph("Tax Amount: " + booking.getTaxAmount()));
            document.add(new Paragraph("Total Amount: " + booking.getTotalAmount()));
            document.add(new Paragraph("Currency: " + booking.getCurrency()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Transactions Info:"));
            for (Transaction transaction : transactions) {
                document.add(new Paragraph("Type: " + transaction.getType()));
                document.add(new Paragraph("Base Amount: " + transaction.getBaseAmount()));
                document.add(new Paragraph("Tax Amount: " + transaction.getTaxAmount()));
                document.add(new Paragraph("Total Amount: " + transaction.getTotalAmount()));
                document.add(new Paragraph("Currency: " + transaction.getCurrency()));
                document.add(new Paragraph("Date: " + transaction.getCreatedAt().format(formatter)));
                document.add(new Paragraph("\n"));
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error while generating PDF", e);
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice-" + bookingId + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @PutMapping("/{bookingId}/edit")
    public Booking updateBooking(@PathVariable Long bookingId, @RequestBody Booking updatedBooking) {
        // Validate if the ID in the URL matches the ID in the request body
        if (!bookingId.equals(updatedBooking.getId())) {
            throw new RuntimeException("Booking ID in the path and body do not match");
        }

        // Find the existing booking by ID
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String existingInventoryType = existingBooking.getInventoryType();
        String existingHotelName = existingBooking.getHotelName();
        String existingAirlineCode = existingBooking.getAirlineCode();
        String existingRoomType = existingBooking.getRoomType();

        // Check if the base or tax amount has changed
        boolean hasAmountChanged = !existingBooking.getBaseAmount().equals(updatedBooking.getBaseAmount()) ||
                !existingBooking.getTaxAmount().equals(updatedBooking.getTaxAmount()) ||
                !existingBooking.getCurrency().equals(updatedBooking.getCurrency());

        // Update allowed booking details
        existingBooking.setStartDate(updatedBooking.getStartDate());
        existingBooking.setEndDate(updatedBooking.getEndDate());
        existingBooking.setLast4DigitsCard(updatedBooking.getLast4DigitsCard());
        existingBooking.setCurrency(updatedBooking.getCurrency());
        existingBooking.setBaseAmount(updatedBooking.getBaseAmount());
        existingBooking.setTaxAmount(updatedBooking.getTaxAmount());
        existingBooking.setTotalAmount(updatedBooking.getTotalAmount());

        // Conditionally update fields
        if (existingInventoryType.equals("Flight")) {
            existingBooking.setAirlineCode(
                    updatedBooking.getAirlineCode() != null ? updatedBooking.getAirlineCode() : existingAirlineCode);
            existingBooking.setRoomType(null); // Room type should be null for flights
        } else if (existingInventoryType.equals("Hotel")) {
            existingBooking.setHotelName(
                    updatedBooking.getHotelName() != null ? updatedBooking.getHotelName() : existingHotelName);
            existingBooking.setRoomType(
                    updatedBooking.getRoomType() != null ? updatedBooking.getRoomType() : existingRoomType);
            existingBooking.setAirlineCode(null); // Airline code should be null for hotels
        }

        Booking savedBooking = bookingRepository.save(existingBooking);

        // Conditionally update the transaction table
        if (hasAmountChanged) {
            Transaction transaction = new Transaction();
            transaction.setBooking(savedBooking);
            transaction.setType(TransactionType.UPDATE);
            transaction.setBaseAmount(updatedBooking.getBaseAmount());
            transaction.setTaxAmount(updatedBooking.getTaxAmount());
            transaction.setTotalAmount(updatedBooking.getTotalAmount());
            transaction.setCurrency(updatedBooking.getCurrency());
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction);
        }

        return savedBooking;
    }
}
