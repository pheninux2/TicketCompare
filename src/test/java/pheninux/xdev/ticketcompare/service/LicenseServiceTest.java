package pheninux.xdev.ticketcompare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;
import pheninux.xdev.ticketcompare.repository.LicenseRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;

    @InjectMocks
    private LicenseService licenseService;

    private User testUser;
    private License testLicense;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullName("Test User")
                .build();

        testLicense = License.builder()
                .id(1L)
                .user(testUser)
                .licenseType(LicenseType.TRIAL)
                .licenseKey("RECEIPTIQ-TEST-KEY-123")
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateTrialLicense_Success() {
        // Given
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.empty());
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // When
        License result = licenseService.createTrialLicense(testUser);

        // Then
        assertNotNull(result);
        assertEquals(LicenseType.TRIAL, result.getLicenseType());
        verify(licenseRepository).save(any(License.class));
    }

    @Test
    void testCreateTrialLicense_AlreadyExists() {
        // Given
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.of(testLicense));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            licenseService.createTrialLicense(testUser);
        });

        verify(licenseRepository, never()).save(any(License.class));
    }

    @Test
    void testUpgradeLicense_Success() {
        // Given
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        // When
        License result = licenseService.upgradeLicense(testUser, LicenseType.MONTHLY);

        // Then
        assertNotNull(result);
        assertEquals(LicenseType.MONTHLY, result.getLicenseType());
        verify(licenseRepository).save(any(License.class));
    }

    @Test
    void testRenewLicense_Success() {
        // Given
        testLicense.setExpiryDate(LocalDate.now().plusDays(5)); // Expire bientôt
        when(licenseRepository.findById(anyLong())).thenReturn(Optional.of(testLicense));
        when(licenseRepository.save(any(License.class))).thenReturn(testLicense);

        LocalDate oldExpiryDate = testLicense.getExpiryDate();

        // When
        License result = licenseService.renewLicense(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.getExpiryDate().isAfter(oldExpiryDate));
        verify(licenseRepository).save(any(License.class));
    }

    @Test
    void testCancelLicense_Success() {
        // Given
        when(licenseRepository.findById(anyLong())).thenReturn(Optional.of(testLicense));

        // When
        licenseService.cancelLicense(1L);

        // Then
        assertEquals(SubscriptionStatus.CANCELLED, testLicense.getStatus());
        assertFalse(testLicense.getAutoRenew());
        verify(licenseRepository).save(testLicense);
    }

    @Test
    void testHasActiveLicense_True() {
        // Given
        testUser.setLicense(testLicense);
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.of(testLicense));

        // When
        boolean result = licenseService.hasActiveLicense(testUser);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasActiveLicense_False_Expired() {
        // Given
        testLicense.setExpiryDate(LocalDate.now().minusDays(1)); // Expiré
        testUser.setLicense(testLicense);
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.of(testLicense));

        // When
        boolean result = licenseService.hasActiveLicense(testUser);

        // Then
        assertFalse(result);
    }

    @Test
    void testHasActiveLicense_False_NoLicense() {
        // Given
        when(licenseRepository.findByUser(any(User.class))).thenReturn(Optional.empty());

        // When
        boolean result = licenseService.hasActiveLicense(testUser);

        // Then
        assertFalse(result);
    }
}

