package com.example.explore.web;

import com.example.explore.model.entity.Comment;
import com.example.explore.model.entity.Route;
import com.example.explore.model.entity.User;
import com.example.explore.repository.CommentsRepository;
import com.example.explore.repository.RouteRepository;
import com.example.explore.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
public class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private  RouteRepository routeRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;

    private User testUser;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser
                .setPassword("password")
                .setUsername("pesho")
                .setFullName("Petar Georgiev");

        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        routeRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    void testGetComments() throws Exception {
        long routeId = initRoute();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/" + routeId + "/comments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].message", is("comment 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].message", is("comment 2")));
    }

    private long initRoute() {
        Route testRoute = new Route();
        testRoute.setName("Testing route");

        testRoute = routeRepository.save(testRoute);

        Comment comment1 = new Comment();
        comment1.setCreated(LocalDateTime.now())
                .setAuthor(testUser)
                .setTextContent("comment 1")
                .setApproved(true)
                .setRoute(testRoute);

        Comment comment2 = new Comment();
        comment2.setCreated(LocalDateTime.now())
                .setAuthor(testUser)
                .setTextContent("comment 2")
                .setApproved(true)
                .setRoute(testRoute);

        testRoute.setComments(List.of(comment1, comment2));

        return routeRepository.save(testRoute).getId();
    }
}
