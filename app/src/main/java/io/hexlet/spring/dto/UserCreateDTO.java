package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCreateDTO {
    @NotBlank(message = "User email is required")
    private String email;
    @NotBlank(message = "User first name is required")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Password is required")
    private String password;
}
