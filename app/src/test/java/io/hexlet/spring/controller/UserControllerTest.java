package io.hexlet.spring.controller;

import io.hexlet.spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_returns201_andBody() throws Exception {
        var body = """
            {
            "firstName": "John",
            "lastName": "Doe",
            "email": "john@example.com",
            "password": "password"
            }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createMultipleUsers_returns201_andCanRetrieve() throws Exception {
        // Создаем первого пользователя
        var body1 = """
        {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "password": "password"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated());

        // Создаем второго пользователя
        var body2 = """
        {
        "firstName": "Jane",
        "lastName": "Smith",
        "email": "jane@example.com",
        "password": "password"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isCreated());

        // Проверяем через GET, что оба пользователя созданы
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[*].email", hasItems("john@example.com", "jane@example.com")));
    }

    @Test
    void getUserById_returns200_andCorrectUser() throws Exception {
        // Сначала создаем пользователя
        var body = """
        {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "password": "password"
        }
        """;

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        // Извлекаем ID созданного пользователя
        String response = result.getResponse().getContentAsString();
        String userId = new ObjectMapper().readTree(response).get("id").asText();

        // Получаем пользователя по ID
        mockMvc.perform(get("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void updateUser_returns200_andCorrectsData() throws Exception {
        // Сначала создаем пользователя
        var body1 = """
        {
        "firstName": "John",
        "lastName": "Dowson",
        "email": "johnd@example.com",
        "password": "password"
        }
        """;

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isCreated())
                .andReturn();

        // Извлекаем ID созданного пользователя
        String response = result.getResponse().getContentAsString();
        String userId = new ObjectMapper().readTree(response).get("id").asText();

        var body2 = """
        {
        "firstName": "Johnathan",
        "lastName": "Dowson",
        "email": "johnd@example.com",
        "password": "password"
        }
        """;

        // Обновляем пользователя по ID
        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isOk())
                .andReturn();

        // Получаем пользователя по ID
        mockMvc.perform(get("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Johnathan"));
    }

    @Test
    void deleteUser_returns204_whenUserExists() throws Exception {
        // Сначала создаем пользователя
        var body = """
        {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "password": "password"
        }
        """;

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        // Извлекаем ID созданного пользователя
        String response = result.getResponse().getContentAsString();
        String userId = new ObjectMapper().readTree(response).get("id").asText();

        // Удаляем пользователя по ID
        mockMvc.perform(delete("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Проверяем, что пользователь действительно удален
        mockMvc.perform(get("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_returns404_whenUserNotExists() throws Exception {
        // Пытаемся удалить несуществующего пользователя
        mockMvc.perform(delete("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}