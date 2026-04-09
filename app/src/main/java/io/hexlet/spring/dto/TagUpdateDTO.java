package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagUpdateDTO {
    @NotBlank(message = "Tag name is required")
    @Size(min = 3)
    private String name;
}
