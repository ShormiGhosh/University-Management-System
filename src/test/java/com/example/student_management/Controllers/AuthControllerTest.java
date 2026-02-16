package com.example.student_management.Controllers;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.student_management.entity.User;
import com.example.student_management.entity.User.Role;
import com.example.student_management.service.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice()
                .build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void getCurrentUser() throws Exception {
        // Test successful retrieval of current user
        String credentials = "testuser:password";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeader = "Basic " + base64Credentials;

        when(userDetailsService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.id").value(1));

        verify(userDetailsService, times(1)).findByUsername("testuser");
    }
}