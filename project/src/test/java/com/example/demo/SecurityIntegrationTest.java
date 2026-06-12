package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void protectedEndpoint_withoutAuth_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_canListBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_cannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"X\",\"author\":\"Y\",\"isbn\":\"111\",\"quantity\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canAddBook() throws Exception {
        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Czysty kod\",\"author\":\"Robert C. Martin\",\"isbn\":\"978-0132350884\",\"quantity\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Czysty kod"));
    }

    @Test
    void register_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
