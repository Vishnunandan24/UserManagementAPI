package com.example.UserManagementAPI;

import com.example.UserManagementAPI.Model.User;
import com.example.UserManagementAPI.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private List<User> manyUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        User initialUser1 = new User("John Doe", "john.doe@example.com");
        User initialUser2 = new User("Jane Smith", "jane.smith@example.com");

        List<User> savedUsers = userRepository.saveAll(List.of(initialUser1, initialUser2));
        this.user1 = savedUsers.get(0);
        this.user2 = savedUsers.get(1);

        manyUsers = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> new User("User" + i, "user" + i + "@example.com"))
                .collect(Collectors.toList());
        userRepository.saveAll(manyUsers);
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        mockMvc.perform(get("/api/users/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name",is(user1.getName())))
                .andExpect(jsonPath("$.email",is(user1.getEmail())));
    }

    @Test
    void testCreateUsers() throws Exception{
        User newUser1 = new User("Alice Brown","alice.brown@example.com");
        User newUser2 = new User("John Doe","john.doe@example.com");
        List<User> newUsers = Arrays.asList(newUser1, newUser2);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id",notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Alice Brown")))
                .andExpect(jsonPath("$[0].email",is("alice.brown@example.com")))
                .andExpect(jsonPath("$[1].id",notNullValue()))
                .andExpect(jsonPath("$[1].name",is("John Doe")))
                .andExpect(jsonPath("$[1].email",is("john.doe@example.com")));
    }
    @Test
    void testDeleteUserFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



    @Test
    void testDeleteUserNotFound() throws Exception{
        Long nonExistenceId = 9L;
        mockMvc.perform(delete("/api/users/{id}",nonExistenceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}