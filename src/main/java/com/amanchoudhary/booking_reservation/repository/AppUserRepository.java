package com.amanchoudhary.booking_reservation.repository;

import com.amanchoudhary.booking_reservation.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
