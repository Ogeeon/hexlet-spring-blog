package io.hexlet.spring;

import io.hexlet.spring.model.Post;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    private List<Post> posts = new ArrayList<>();
    private Long lastId = 0L;

    @GetMapping("/posts")
    public List<Post> index(@RequestParam(defaultValue = "10") Integer limit) {
        return new ArrayList<>(posts);
    }

    @GetMapping("/posts/{id}")
    public Optional<Post> show(@PathVariable Long id) {
        return posts.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @PostMapping("/posts")
    public Post create(@RequestBody Post post) {
        if (post.getTitle() == null || post.getTitle().isEmpty() ||
                post.getContent() == null || post.getContent().isEmpty()) {
            throw new IllegalArgumentException("Post should have title and content");
        }
        post.setId(lastId++);
        post.setCreatedAt(LocalDateTime.now());
        posts.add(post);
        return post;
    }

    @PostMapping("/posts/{id}")
    public Post update(@PathVariable Long id, @RequestBody Post data) {
        if (data.getTitle() == null ||data.getTitle().isEmpty() ||
                data.getContent() == null || data.getContent().isEmpty()) {
            throw new IllegalArgumentException("Post should have title and content");
        }
        Optional<Post> maybePost = posts.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (maybePost.isPresent()) {
            Post post = maybePost.get();
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
        }
        return data;
    }

    @DeleteMapping("/posts/{id}")
    public void destroy(@PathVariable Long id) {
        posts.removeIf(p -> p.getId().equals(id));
    }
}
