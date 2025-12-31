package com.example.pos.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    public boolean succeed;
    public T data;
    public String message;

    // Success: chỉ có succeed + data, message null → JSON bỏ message
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // Fail: chỉ có succeed + message, data null → JSON bỏ data
    public static ApiResponse<Object> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
