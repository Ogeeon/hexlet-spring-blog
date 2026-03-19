package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostRepository postRepository;

    public PostsController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    public Page<Post> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByPublishedTrue(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post show(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        Post saved = postRepository.save(post);
        return ResponseEntity.created(URI.create("/" + post.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post data) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " no found"));
        post.setTitle(data.getTitle());
        post.setContent(data.getContent());
        postRepository.save(post);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " no found"));
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
