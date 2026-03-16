package com.example.kitchensink.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MemberRequestDto {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String role;
}