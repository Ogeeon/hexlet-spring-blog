package io.hexlet.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        var user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("Definitely-Valid");
        testUserId = userRepository.save(user).getId();
    }

    @Test
    void listPublished_returns200_andPage() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void createPost_returns201_andBody() throws Exception {
        var body = String.format("""
        {
        "title": "Test Post",
        "content": "This is a test post content with sufficient length",
        "published": true,
        "userId": %d
        }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("This is a test post content with sufficient length"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void createMultiplePosts_returns201_andCanRetrieve() throws Exception {
        // Create first post
        var body1 = String.format("""
    {
    "title": "First Post",
    "content": "This is the first test post content with sufficient length",
    "published": true,
    "userId": %d
    }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated());

        // Create second post
        var body2 = String.format("""
    {
    "title": "Second Post",
    "content": "This is the second test post content with sufficient length",
    "published": true,
    "userId": %d
    }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isCreated());

        // Check both posts are in the list
        mockMvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.content[*].title", hasItems("First Post", "Second Post")));
    }

    @Test
    void getPostById_returns200_andCorrectPost() throws Exception {
        // First create a post
        var body = String.format("""
    {
    "title": "Test Post",
    "content": "This is a test post content with sufficient length",
    "published": true,
    "userId": %d
    }
    """, testUserId);

        var result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract ID of created post
        String response = result.getResponse().getContentAsString();
        String postId = new ObjectMapper().readTree(response).get("id").asText();

        // Get post by ID
        mockMvc.perform(get("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.content").value("This is a test post content with sufficient length"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void updatePost_returns200_andCorrectsData() throws Exception {
        // First create a post
        var body1 = String.format("""
    {
    "title": "Original Title",
    "content": "This is the original test post content with sufficient length",
    "published": false,
    "userId": %d
    }
    """, testUserId);

        var result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract ID of created post
        String response = result.getResponse().getContentAsString();
        String postId = new ObjectMapper().readTree(response).get("id").asText();

        var body2 = """
    {
    "title": "Updated Title",
    "content": "This is the updated test post content with sufficient length"
    }
    """;

        // Update post by ID
        mockMvc.perform(put("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isOk())
                .andReturn();

        // Get post by ID to verify update
        mockMvc.perform(get("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("This is the updated test post content with sufficient length"));
    }

    @Test
    void deletePost_returns204_whenPostExists() throws Exception {
        // First create a post
        var body = String.format("""
    {
    "title": "Post to Delete",
    "content": "This is a test post that will be deleted with sufficient length",
    "published": true,
    "userId": %d
    }
    """, testUserId);

        var result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract ID of created post
        String response = result.getResponse().getContentAsString();
        String postId = new ObjectMapper().readTree(response).get("id").asText();

        // Delete post by ID
        mockMvc.perform(delete("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify post is actually deleted
        mockMvc.perform(get("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_returns404_whenPostNotExists() throws Exception {
        // Try to delete non-existent post
        mockMvc.perform(delete("/api/posts/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostById_returns404_whenPostNotExists() throws Exception {
        // Try to get non-existent post
        mockMvc.perform(get("/api/posts/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost_returns400_whenTitleIsEmpty() throws Exception {
        var body = String.format("""
        {
        "title": "",
        "content": "This is a test post content with sufficient length",
        "published": true,
        "userId": %d
        }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createPost_returns400_whenContentIsTooShort() throws Exception {
        var body = String.format("""
        {
        "title": "Test Post",
        "content": "Short",
        "published": true,
        "userId": %d
        }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listPublished_returnsOnlyPublishedPosts() throws Exception {
        // Create a published post
        var publishedBody = String.format("""
    {
    "title": "Published Post",
    "content": "This is a published test post content with sufficient length",
    "published": true,
    "userId": %d
    }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publishedBody))
                .andExpect(status().isCreated());

        // Create an unpublished post
        var unpublishedBody = String.format("""
    {
    "title": "Unpublished Post",
    "content": "This is an unpublished test post content with sufficient length",
    "published": false,
    "userId": %d
    }
    """, testUserId);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unpublishedBody))
                .andExpect(status().isCreated());

        // Check that only published posts are returned
        mockMvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].title", hasItems("Published Post")))
                .andExpect(jsonPath("$.content.length()").value(equalTo(1)));
    }

}
