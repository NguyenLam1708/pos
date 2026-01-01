package com.example.pos.dto.request;

import com.example.pos.enums.user.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Pattern;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0\\d{8,9}", message = "Số điện thoại phải bắt đầu bằng 0 và gồm 9-10 chữ số")
    String phone;

    @NotBlank(message = "Họ không được để trống")
    String firstName;

    @NotBlank(message = "Tên không được để trống")
    String lastName;

    @NotBlank(message = "FullName không được để trống")
    String fullName;

    @NotBlank(message = "Username không được để trống")
    String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6-100 ký tự")
    String password;

    @NotBlank(message = "Confirm password không được để trống")
    String confirmPassword;

    @NotNull(message = "Status không được để trống")
    UserStatus status;

    @NotBlank(message = "Role code không được để trống")
    String roleCode;
}
