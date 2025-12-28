package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;
import pheninux.xdev.ticketcompare.repository.LicenseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LicenseService {

    private final LicenseRepository licenseRepository;

    /**
     * Crée une nouvelle licence pour un utilisateur
     */
    public License createLicense(User user, LicenseType licenseType) {
        log.info("Création d'une licence {} pour l'utilisateur {}", licenseType, user.getEmail());

        License license = License.builder()
                .user(user)
                .licenseType(licenseType)
                .licenseKey(generateLicenseKey())
                .startDate(LocalDate.now())
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(licenseType != LicenseType.LIFETIME)
                .build();

        // Calculer la date d'expiration
        if (licenseType != LicenseType.LIFETIME) {
            license.setExpiryDate(LocalDate.now().plusDays(licenseType.getValidityDays()));
        }

        return licenseRepository.save(license);
    }

    /**
     * Crée une licence d'essai gratuite
     */
    public License createTrialLicense(User user) {
        log.info("Création d'une licence d'essai pour {}", user.getEmail());

        // Vérifier si l'utilisateur n'a pas déjà eu une période d'essai
        Optional<License> existingLicense = licenseRepository.findByUser(user);
        if (existingLicense.isPresent()) {
            throw new IllegalStateException("L'utilisateur a déjà une licence");
        }

        return createLicense(user, LicenseType.TRIAL);
    }

    /**
     * Met à niveau une licence
     */
    public License upgradeLicense(User user, LicenseType newLicenseType) {
        log.info("Mise à niveau de la licence pour {} vers {}", user.getEmail(), newLicenseType);

        License license = licenseRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Aucune licence trouvée"));

        // Si upgrade vers LIFETIME, pas de date d'expiration
        if (newLicenseType == LicenseType.LIFETIME) {
            license.setExpiryDate(null);
            license.setAutoRenew(false);
        } else {
            // Calculer nouvelle date d'expiration
            LocalDate newExpiryDate = LocalDate.now().plusDays(newLicenseType.getValidityDays());
            license.setExpiryDate(newExpiryDate);
        }

        license.setLicenseType(newLicenseType);
        license.setStatus(SubscriptionStatus.ACTIVE);

        return licenseRepository.save(license);
    }

    /**
     * Renouvelle une licence
     */
    public License renewLicense(Long licenseId) {
        log.info("Renouvellement de la licence {}", licenseId);

        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalStateException("Licence introuvable"));

        license.renew();
        return licenseRepository.save(license);
    }

    /**
     * Annule une licence
     */
    public void cancelLicense(Long licenseId) {
        log.info("Annulation de la licence {}", licenseId);

        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalStateException("Licence introuvable"));

        license.cancel();
        licenseRepository.save(license);
    }

    /**
     * Vérifie si un utilisateur a une licence active
     */
    @Transactional(readOnly = true)
    public boolean hasActiveLicense(User user) {
        Optional<License> license = licenseRepository.findByUser(user);
        return license.isPresent() && license.get().isActive();
    }

    /**
     * Récupère la licence d'un utilisateur
     */
    @Transactional(readOnly = true)
    public Optional<License> getUserLicense(User user) {
        return licenseRepository.findByUser(user);
    }

    /**
     * Récupère les licences expirant bientôt
     */
    @Transactional(readOnly = true)
    public List<License> getExpiringLicenses(int daysBeforeExpiry) {
        LocalDate targetDate = LocalDate.now().plusDays(daysBeforeExpiry);
        return licenseRepository.findExpiringLicenses(targetDate);
    }

    /**
     * Processus automatique de renouvellement
     */
    public void processAutoRenewals() {
        log.info("Traitement des renouvellements automatiques");

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        List<License> licensesToRenew = licenseRepository.findLicensesNeedingRenewal(today, nextWeek);

        for (License license : licensesToRenew) {
            try {
                log.info("Renouvellement automatique de la licence {} pour {}",
                         license.getId(), license.getUser().getEmail());

                // Ici, intégrer la logique de paiement si nécessaire
                license.renew();
                licenseRepository.save(license);

            } catch (Exception e) {
                log.error("Erreur lors du renouvellement de la licence {}: {}",
                         license.getId(), e.getMessage());
            }
        }
    }

    /**
     * Génère une clé de licence unique
     */
    private String generateLicenseKey() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return String.format("ShopTracker-%s-%s-%s",
                           uuid.substring(0, 8),
                           uuid.substring(8, 16),
                           uuid.substring(16, 24));
    }

    /**
     * Valide une clé de licence
     */
    @Transactional(readOnly = true)
    public Optional<License> validateLicenseKey(String licenseKey) {
        return licenseRepository.findByLicenseKey(licenseKey);
    }
}

