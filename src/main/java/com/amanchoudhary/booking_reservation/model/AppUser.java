package com.amanchoudhary.booking_reservation.model;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
@Data
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String companyId;
    private String role;
}
