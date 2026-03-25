package io.hexlet.spring.controller;

import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserMapper;
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
    private final UserMapper userMapper;

    public UsersController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var users = userRepository.findAll().stream().limit(limit).map(userMapper::toDTO).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        User saved = userRepository.save(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId()))
                .body(userMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        user.setFirstName(dto.getFirstName());
        user.setEmail(dto.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " no found"));
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
