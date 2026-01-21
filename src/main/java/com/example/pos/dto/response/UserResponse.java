package com.example.pos.dto.response;

import com.example.pos.entity.user.User;
import com.example.pos.enums.user.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    UUID userId;
    String email;
    String phone;
    String firstName;
    String lastName;
    String fullName;
    String username;
    //String avatar;
    UserStatus status;

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())

                .build();
    }

}
