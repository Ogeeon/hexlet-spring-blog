package io.hexlet.spring.controller;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostPatchDTO;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final TagRepository tagRepository;

    public PostsController(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.tagRepository = tagRepository;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<PostDTO> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByPublishedTrue(pageable).map(postMapper::map);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO show(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(postMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " not found"));
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateDTO dto) {
        var user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var post = postMapper.map(dto);
        post.setUser(user);
        dto.getTags().ifPresent(l -> post.setTags(getTagsByIDs(l)));
        postRepository.save(post);

        var postDTO = postMapper.map(post);
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).body(postDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateDTO dto) {

        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postMapper.update(dto, post);
        dto.getTags().ifPresent(l -> post.setTags(getTagsByIDs(l)));
        postRepository.save(post);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDTO> patchPost(@PathVariable Long id,
                                             @RequestBody PostPatchDTO dto) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        dto.getTitle().ifPresent(post::setTitle);
        dto.getContent().ifPresent(post::setContent);
        dto.getPublished().ifPresent(post::setPublished);
        dto.getTags().ifPresent(l -> post.setTags(getTagsByIDs(l)));

        postRepository.save(post);
        return ResponseEntity.ok(postMapper.map(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with ID " + id + " no found"));
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private List<Tag> getTagsByIDs(List<Long> ids) {
        return tagRepository.findAllById(ids);
    }
}
