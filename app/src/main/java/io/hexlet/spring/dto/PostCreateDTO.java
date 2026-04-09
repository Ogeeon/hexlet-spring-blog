package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateDTO {
    @NotBlank(message = "Post title is required")
    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 10, message = "Post is too short")
    private String content;

    private boolean published;
    private Long userId;
    private List<Long> tags;
}
