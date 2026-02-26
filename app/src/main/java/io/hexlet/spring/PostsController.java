package io.hexlet.spring;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostRepository postRepository;

    public PostsController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity<List<Post>> index(@RequestParam(defaultValue = "10") Integer limit) {
        var posts = postRepository.findAll().stream().limit(limit).toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> show(@PathVariable Long id) {
        var post = postRepository.findById(id);
        return ResponseEntity.of(post);
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        if (post.getTitle() == null || post.getTitle().isEmpty() ||
                post.getContent() == null || post.getContent().isEmpty()) {
            ResponseEntity.badRequest().body("Post should have title and content");
            return null;
        }
        postRepository.save(post);
        return ResponseEntity.created(URI.create("/" + post.getId())).body(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post data) {
        if (data.getTitle() == null ||data.getTitle().isEmpty() ||
                data.getContent() == null || data.getContent().isEmpty()) {
            ResponseEntity.badRequest().body("Post should have title and content");
        }
        Optional<Post> maybePost = postRepository.findById(id);
        if (maybePost.isPresent()) {
            Post post = maybePost.get();
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
            postRepository.save(post);
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
