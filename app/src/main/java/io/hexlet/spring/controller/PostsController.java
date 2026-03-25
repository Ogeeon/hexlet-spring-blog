package io.hexlet.spring.controller;

import io.hexlet.spring.dto.CreatePostDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostMapper;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostsController(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    @GetMapping("")
    public Page<PostDTO> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByPublishedTrue(pageable).map(postMapper::toDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO show(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody CreatePostDTO dto) {
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + dto.getUserId() + " not found"));
        var post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPublished(dto.isPublished());
        post.setUser(user);
        Post saved = postRepository.save(post);
        return ResponseEntity.created(URI.create("/api/posts/" + saved.getId()))
                .body(postMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable Long id, @RequestBody CreatePostDTO dto) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " no found"));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        postRepository.save(post);
        return ResponseEntity.ok(postMapper.toDTO(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " no found"));
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
