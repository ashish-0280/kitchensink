package com.example.kitchensink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
