package io.hexlet.spring;

import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<User> show(@PathVariable Long id) {
        var userOptional = userRepository.findById(id);
        return ResponseEntity.of(userOptional);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User should have name and email");
        }
        userRepository.save(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody User data) {
        if (data.getName() == null || data.getName().isEmpty() ||
                data.getEmail() == null || data.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User should have name and email");
        }
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            user.setName(data.getName());
            user.setEmail(data.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
