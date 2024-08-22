package com.amanchoudhary.booking_reservation.controller;

import org.springframework.web.bind.annotation.RestController;
import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.repository.AppUserRepository;
import com.amanchoudhary.booking_reservation.service.CompanyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class RegistrationController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register", consumes = "application/json")
    public AppUser createUser(@RequestBody AppUser user) {
        Company company = companyService.findOrCreateCompany(user.getCompany().getCompanyName());
        user.setCompany(company);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.save(user);
    }
}
