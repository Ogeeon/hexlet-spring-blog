package io.hexlet.spring;

import io.hexlet.spring.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final List<User> users = new ArrayList<>();
    private Long lastId = 0L;

    @GetMapping
    public ResponseEntity<List<User>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var result = users.stream().limit(limit).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> show(@PathVariable Long id) {
        var postOptional = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        return ResponseEntity.of(postOptional);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User should have name and email");
        }
        user.setId(lastId++);
        users.add(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody User data) {
        if (data.getName() == null || data.getName().isEmpty() ||
                data.getEmail() == null || data.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("User should have name and email");
        }
        Optional<User> maybeUser = users.stream().filter(u -> u.getId().equals(id)).findFirst();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            user.setName(data.getName());
            user.setEmail(data.getEmail());
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        var removed = users.removeIf(u -> u.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
