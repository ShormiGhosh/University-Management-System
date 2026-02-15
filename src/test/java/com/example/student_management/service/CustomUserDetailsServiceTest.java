package com.example.student_management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.student_management.entity.User;
import com.example.student_management.entity.User.Role;
import com.example.student_management.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.STUDENT);
    }

    @Test
    void loadUserByUsername() {
        // Test loading user by username when user exists
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword123", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT")));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findAll() {
        // Test finding all users
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("teacher1");
        user2.setRole(Role.TEACHER);
        List<User> users = Arrays.asList(testUser, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = customUserDetailsService.findAll();

        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("teacher1", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }
}