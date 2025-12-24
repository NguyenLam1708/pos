package com.example.pos.entities.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRole {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "user_id")
  String userId;

  @Column(name = "role_id")
  String roleId;

}
