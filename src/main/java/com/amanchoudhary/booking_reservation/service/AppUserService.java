package com.amanchoudhary.booking_reservation.service;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    public enum UserRole {
        USER,
        ADMIN
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username);

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }

    public void registerUser(String username, String password, String firstName, String lastName, String company,
            String role) {
        // Create a new AppUser instance with the provided details
        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(password); // You should hash the password before saving
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setCompanyId(company);
        newUser.setRole(role);

        // Save the new user to the repository
        userRepository.save(newUser);
    }
}
