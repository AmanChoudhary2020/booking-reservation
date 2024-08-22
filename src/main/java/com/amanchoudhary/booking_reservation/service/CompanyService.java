package com.amanchoudhary.booking_reservation.service;

import com.amanchoudhary.booking_reservation.model.Company;
import com.amanchoudhary.booking_reservation.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company findOrCreateCompany(String companyName) {
        Optional<Company> existingCompany = companyRepository.findByCompanyName(companyName);

        if (existingCompany.isPresent()) {
            return existingCompany.get();
        }

        Company newCompany = new Company();
        newCompany.setCompanyName(companyName);
        return companyRepository.save(newCompany);
    }
}
