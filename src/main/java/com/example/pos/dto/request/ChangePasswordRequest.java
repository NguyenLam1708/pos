package com.example.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Old password không được để trống")
    String oldPassword;

    @NotBlank(message = "New password không được để trống")
    @Size(min = 6, max = 100, message = "New password phải từ 6-100 ký tự")
    String newPassword;

    @NotBlank(message = "Confirm password không được để trống")
    String confirmPassword;
}
