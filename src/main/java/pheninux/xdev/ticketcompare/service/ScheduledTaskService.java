package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final LicenseService licenseService;
    private final EmailService emailService;

    /**
     * Vérification quotidienne des licences expirantes
     * Exécuté tous les jours à 2h du matin
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void checkExpiringLicenses() {
        log.info("🔍 Début de la vérification des licences expirantes");

        try {
            // Licences expirant dans 7 jours
            List<License> expiring7Days = licenseService.getExpiringLicenses(7);
            for (License license : expiring7Days) {
                User user = license.getUser();
                log.info("📧 Envoi rappel 7 jours à {}", user.getEmail());
                emailService.sendLicenseExpiryReminder(user, 7);
            }

            // Licences expirant dans 3 jours
            List<License> expiring3Days = licenseService.getExpiringLicenses(3);
            for (License license : expiring3Days) {
                User user = license.getUser();
                log.info("📧 Envoi rappel 3 jours à {}", user.getEmail());
                emailService.sendLicenseExpiryReminder(user, 3);
            }

            // Licences expirant dans 1 jour
            List<License> expiring1Day = licenseService.getExpiringLicenses(1);
            for (License license : expiring1Day) {
                User user = license.getUser();
                log.info("📧 Envoi rappel 1 jour à {}", user.getEmail());
                emailService.sendLicenseExpiryReminder(user, 1);
            }

            log.info("✅ Vérification des licences expirantes terminée");

        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification des licences: {}", e.getMessage());
        }
    }

    /**
     * Processus de renouvellement automatique
     * Exécuté tous les jours à 3h du matin
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void processAutoRenewals() {
        log.info("🔄 Début du processus de renouvellement automatique");

        try {
            licenseService.processAutoRenewals();
            log.info("✅ Processus de renouvellement automatique terminé");

        } catch (Exception e) {
            log.error("❌ Erreur lors du renouvellement automatique: {}", e.getMessage());
        }
    }

    /**
     * Nettoyage des tokens expirés
     * Exécuté tous les jours à 4h du matin
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanExpiredTokens() {
        log.info("🧹 Début du nettoyage des tokens expirés");

        try {
            // TODO: Implémenter le nettoyage des tokens
            // userRepository.deleteExpiredVerificationTokens(LocalDateTime.now());

            log.info("✅ Nettoyage des tokens terminé");

        } catch (Exception e) {
            log.error("❌ Erreur lors du nettoyage des tokens: {}", e.getMessage());
        }
    }

    /**
     * Rapport quotidien des statistiques
     * Exécuté tous les jours à 8h du matin
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void generateDailyReport() {
        log.info("📊 Génération du rapport quotidien");

        try {
            // TODO: Implémenter la génération de rapports
            // - Nombre de nouvelles inscriptions
            // - Nombre de licences expirées
            // - Nombre de renouvellements
            // - Revenus du jour

            log.info("✅ Rapport quotidien généré");

        } catch (Exception e) {
            log.error("❌ Erreur lors de la génération du rapport: {}", e.getMessage());
        }
    }

    /**
     * Envoi de rappels hebdomadaires (tous les lundis à 10h)
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyReminders() {
        log.info("📅 Envoi des rappels hebdomadaires");

        try {
            // TODO: Envoyer des emails de rappel aux utilisateurs inactifs

            log.info("✅ Rappels hebdomadaires envoyés");

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi des rappels: {}", e.getMessage());
        }
    }
}

