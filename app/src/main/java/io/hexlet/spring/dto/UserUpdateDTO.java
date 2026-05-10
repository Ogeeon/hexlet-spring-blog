package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class UserUpdateDTO {
    @Getter
    @Setter
    @NotBlank(message = "User email is required")
    private String email;

    @Getter
    @Setter
    @NotBlank(message = "User first name is required")
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "User last name is required")
    private String lastName;

    @Getter
    @Setter
    @NotBlank(message = "Password is required")
    private String password;
}
