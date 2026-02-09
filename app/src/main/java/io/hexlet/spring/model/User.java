package io.hexlet.spring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class User {
    private Long id;
    private String name;
    private String email;
}
