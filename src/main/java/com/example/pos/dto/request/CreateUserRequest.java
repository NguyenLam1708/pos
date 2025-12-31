package com.example.pos.dto.request;

import com.example.pos.enums.user.UserStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String email;
    String phone;
    String firstName;
    String lastName;
    String fullName;
    String username;
    String password;
    String confirmPassword;
    //String avatar;
    UserStatus status;
    String roleCode;
}
