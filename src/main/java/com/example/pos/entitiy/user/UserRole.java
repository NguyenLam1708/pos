package com.example.pos.entitiy.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_roles",
        uniqueConstraints = {
          @UniqueConstraint(columnNames = {"user_id", "role_id"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(name = "user_id")
  UUID userId;

  @Column(name = "role_id")
  UUID roleId;

}
