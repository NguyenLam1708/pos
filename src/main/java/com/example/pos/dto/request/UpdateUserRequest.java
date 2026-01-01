package com.example.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {

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
}
