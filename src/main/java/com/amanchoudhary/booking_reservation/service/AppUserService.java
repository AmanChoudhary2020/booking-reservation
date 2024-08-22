package com.amanchoudhary.booking_reservation.service;

import com.amanchoudhary.booking_reservation.model.AppUser;
import com.amanchoudhary.booking_reservation.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

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
        Optional<AppUser> appUser = userRepository.findByUsername(username);

        if (appUser.isPresent()) {
            var userObj = appUser.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // public void registerUser(String username, String password, String firstName,
    // String lastName, String company,
    // String role) {
    // // Create a new AppUser instance with the provided details
    // AppUser newUser = new AppUser();
    // newUser.setUsername(username);
    // newUser.setPassword(password); // You should hash the password before saving
    // newUser.setFirstName(firstName);
    // newUser.setLastName(lastName);
    // newUser.setCompanyId(company);
    // newUser.setRole(role);

    // // Save the new user to the repository
    // userRepository.save(newUser);
    // }
}
