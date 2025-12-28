package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LicenseService licenseService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Enregistre un nouvel utilisateur avec période d'essai
     */
    public User registerUser(String email, String fullName, String password) {
        log.info("Enregistrement d'un nouvel utilisateur: {}", email);

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Cet email est déjà utilisé");
        }

        // Créer l'utilisateur
        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .emailVerified(false)
                .verificationToken(generateVerificationToken())
                .verificationTokenExpiry(LocalDateTime.now().plusDays(7))
                .build();

        user = userRepository.save(user);

        // Créer automatiquement une licence d'essai gratuite de 1 mois
        licenseService.createTrialLicense(user);

        log.info("Utilisateur {} créé avec succès avec licence d'essai de 30 jours", email);

        // Envoyer l'email de bienvenue
        emailService.sendWelcomeEmail(user);

        return user;
    }

    /**
     * Authentifie un utilisateur
     */
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    /**
     * Enregistre la dernière connexion
     */
    public void recordLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Vérifie l'email d'un utilisateur
     */
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Vérifier si le token n'est pas expiré
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        log.info("Email vérifié pour l'utilisateur {}", user.getEmail());
        return true;
    }

    /**
     * Recherche un utilisateur par email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Recherche un utilisateur par ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Change le mot de passe d'un utilisateur
     */
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Mot de passe changé pour l'utilisateur {}", user.getEmail());
    }

    /**
     * Génère un token de vérification
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Compte le nombre total d'utilisateurs
     */
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }
}

