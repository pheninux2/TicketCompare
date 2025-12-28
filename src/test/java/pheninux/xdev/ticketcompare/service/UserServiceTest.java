package pheninux.xdev.ticketcompare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LicenseService licenseService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .enabled(true)
                .emailVerified(false)
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser("test@example.com", "Test User", "password123");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(licenseService).createTrialLicense(any(User.class));
        verify(emailService).sendWelcomeEmail(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            userService.registerUser("test@example.com", "Test User", "password123");
        });

        verify(userRepository, never()).save(any(User.class));
        verify(licenseService, never()).createTrialLicense(any(User.class));
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        Optional<User> result = userService.authenticate("test@example.com", "password123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testAuthenticate_WrongPassword() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        Optional<User> result = userService.authenticate("test@example.com", "wrongpassword");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.authenticate("nonexistent@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testVerifyEmail_Success() {
        // Given
        when(userRepository.findByVerificationToken(anyString())).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.verifyEmail("valid-token");

        // Then
        assertTrue(result);
        assertTrue(testUser.getEmailVerified());
        verify(userRepository).save(testUser);
    }

    @Test
    void testVerifyEmail_InvalidToken() {
        // Given
        when(userRepository.findByVerificationToken(anyString())).thenReturn(Optional.empty());

        // When
        boolean result = userService.verifyEmail("invalid-token");

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}

