package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserRepository userRepository;

    public UsersController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var users = userRepository.findAll().stream().limit(limit).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User show(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody User user) {
        User saved = userRepository.save(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody User data) {
        if (data.getName() == null || data.getName().isEmpty() ||
                data.getEmail() == null || data.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User should have name and email");
        }
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        user.setName(data.getName());
        user.setEmail(data.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " no found"));
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
