package com.example.kitchensink.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("kitchensink")
@Data
@NoArgsConstructor
public class Member {

    @Id
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z]+([ '-][A-Za-z]+)*$",
            message = "Name must contain only letters, spaces, hyphens, or apostrophes"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(
            regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
    )
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#])[A-Za-z\\d@$!%*?&_\\-#]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&_-#)"
    )
    private String password;

    private String role;
}