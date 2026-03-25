package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostDTO {
    @NotBlank(message = "Post title is required")
    private String title;
    @Size(min = 10, message = "Post is too short")
    private String content;
    private boolean published;
    private Long userId;
}
