package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@ShopTracker.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    /**
     * Vérifie si l'email est configuré
     */
    private boolean isEmailConfigured() {
        return mailUsername != null && !mailUsername.isEmpty();
    }

    /**
     * Envoie un email de bienvenue
     */
    @Async
    public void sendWelcomeEmail(User user) {
        if (!isEmailConfigured()) {
            log.warn("Email non configuré - Email de bienvenue non envoyé à {}", user.getEmail());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Bienvenue sur ShopTracker ! 🎉");

            String body = String.format("""
                Bonjour %s,
                
                Bienvenue sur ShopTracker !
                
                Votre compte a été créé avec succès et vous bénéficiez de 30 jours d'essai gratuit.
                
                Pour commencer :
                1. Vérifiez votre email en cliquant sur le lien ci-dessous
                2. Connectez-vous à votre compte
                3. Scannez votre premier ticket de caisse
                
                Lien de vérification :
                %s/auth/verify-email?token=%s
                
                Ce lien expire dans 7 jours.
                
                Si vous avez des questions, n'hésitez pas à nous contacter.
                
                Cordialement,
                L'équipe ShopTracker
                """,
                user.getFullName(),
                baseUrl,
                user.getVerificationToken()
            );

            message.setText(body);

            mailSender.send(message);
            log.info("Email de bienvenue envoyé à {}", user.getEmail());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue à {}: {}",
                     user.getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie un rappel d'expiration de licence
     */
    @Async
    public void sendLicenseExpiryReminder(User user, int daysRemaining) {
        if (!isEmailConfigured()) {
            log.warn("Email non configuré - Rappel d'expiration non envoyé à {}", user.getEmail());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("⏰ Votre licence ShopTracker expire bientôt");

            String body = String.format("""
                Bonjour %s,
                
                Votre licence ShopTracker expire dans %d jour(s).
                
                Pour continuer à utiliser ShopTracker sans interruption, 
                veuillez renouveler votre licence dès maintenant.
                
                Renouveler maintenant :
                %s/license
                
                Découvrir nos offres :
                %s/pricing
                
                Cordialement,
                L'équipe ShopTracker
                """,
                user.getFullName(),
                daysRemaining,
                baseUrl,
                baseUrl
            );

            message.setText(body);

            mailSender.send(message);
            log.info("Rappel d'expiration envoyé à {} ({} jours restants)",
                    user.getEmail(), daysRemaining);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du rappel d'expiration à {}: {}",
                     user.getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie une confirmation de paiement
     */
    @Async
    public void sendPaymentConfirmation(User user, License license, String amount) {
        if (!isEmailConfigured()) {
            log.warn("Email non configuré - Confirmation de paiement non envoyée à {}", user.getEmail());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("✅ Confirmation de paiement - ShopTracker");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String expiryDate = license.getExpiryDate() != null
                ? license.getExpiryDate().format(formatter)
                : "Illimité";

            String body = String.format("""
                Bonjour %s,
                
                Votre paiement a été confirmé avec succès !
                
                Détails de votre licence :
                - Type : %s
                - Montant : %s
                - Date de début : %s
                - Date d'expiration : %s
                - Clé de licence : %s
                
                Vous pouvez maintenant profiter pleinement de toutes les fonctionnalités de ShopTracker.
                
                Merci de votre confiance !
                
                Cordialement,
                L'équipe ShopTracker
                """,
                user.getFullName(),
                license.getLicenseType().getDisplayName(),
                amount,
                license.getStartDate().format(formatter),
                expiryDate,
                license.getLicenseKey()
            );

            message.setText(body);

            mailSender.send(message);
            log.info("Confirmation de paiement envoyée à {}", user.getEmail());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la confirmation de paiement à {}: {}",
                     user.getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie une notification d'annulation
     */
    @Async
    public void sendCancellationConfirmation(User user) {
        if (!isEmailConfigured()) {
            log.warn("Email non configuré - Confirmation d'annulation non envoyée à {}", user.getEmail());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Confirmation d'annulation - ShopTracker");

            String body = String.format("""
                Bonjour %s,
                
                Votre licence ShopTracker a été annulée comme demandé.
                
                Vous pourrez continuer à utiliser l'application jusqu'à la fin de votre période payée.
                
                Nous sommes désolés de vous voir partir. Si vous changez d'avis, 
                vous pouvez réactiver votre licence à tout moment depuis votre compte.
                
                Pour nous aider à améliorer notre service, n'hésitez pas à nous faire part 
                de vos commentaires.
                
                Cordialement,
                L'équipe ShopTracker
                """,
                user.getFullName()
            );

            message.setText(body);

            mailSender.send(message);
            log.info("Confirmation d'annulation envoyée à {}", user.getEmail());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la confirmation d'annulation à {}: {}",
                     user.getEmail(), e.getMessage());
        }
    }

    /**
     * Envoie un reçu de paiement
     */
    @Async
    public void sendReceipt(User user, String transactionId, String amount, String date) {
        if (!isEmailConfigured()) {
            log.warn("Email non configuré - Reçu non envoyé à {}", user.getEmail());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Reçu de paiement - ShopTracker");

            String body = String.format("""
                Bonjour %s,
                
                Voici votre reçu de paiement pour ShopTracker.
                
                REÇU DE PAIEMENT
                ================
                
                Transaction ID : %s
                Date : %s
                Montant : %s
                Méthode de paiement : Carte bancaire
                
                Si vous avez des questions concernant cette transaction, 
                veuillez nous contacter en citant le numéro de transaction.
                
                Cordialement,
                L'équipe ShopTracker
                """,
                user.getFullName(),
                transactionId,
                date,
                amount
            );

            message.setText(body);

            mailSender.send(message);
            log.info("Reçu envoyé à {}", user.getEmail());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du reçu à {}: {}",
                     user.getEmail(), e.getMessage());
        }
    }
}

