package io.hexlet.spring;

import io.hexlet.spring.model.Post;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    private final List<Post> posts = new ArrayList<>();
    private Long lastId = 0L;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var result = posts.stream().limit(limit).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> show(@PathVariable Long id) {
        var postOptional = posts.stream().filter(p -> p.getId().equals(id)).findFirst();
        return ResponseEntity.of(postOptional);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> create(@RequestBody Post post) {
        if (post.getTitle() == null || post.getTitle().isEmpty() ||
                post.getContent() == null || post.getContent().isEmpty()) {
            ResponseEntity.badRequest().body("Post should have title and content");
        }
        post.setId(lastId++);
        post.setCreatedAt(LocalDateTime.now());
        posts.add(post);
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).body(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post data) {
        if (data.getTitle() == null ||data.getTitle().isEmpty() ||
                data.getContent() == null || data.getContent().isEmpty()) {
            ResponseEntity.badRequest().body("Post should have title and content");
        }
        Optional<Post> maybePost = posts.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (maybePost.isPresent()) {
            Post post = maybePost.get();
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        var removed = posts.removeIf(p -> p.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
