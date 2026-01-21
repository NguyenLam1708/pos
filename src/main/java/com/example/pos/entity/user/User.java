package com.example.pos.entity.user;

import com.example.pos.enums.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "phone")
    String phone;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    UserStatus status;
    
    // TODO implent address
}
