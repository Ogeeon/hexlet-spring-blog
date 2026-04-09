package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PostUpdateDTO {

    @Getter
    @Setter
    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Getter
    @Setter
    @NotBlank
    @Size(min = 10)
    private String content;

    @Getter
    @Setter
    private List<Long> tags;
}
