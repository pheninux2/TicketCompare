package pheninux.xdev.ticketcompare.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pheninux.xdev.ticketcompare.dto.LoginDTO;
import pheninux.xdev.ticketcompare.dto.RegisterDTO;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Page d'inscription
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    /**
     * Traitement de l'inscription
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        log.info("Tentative d'inscription pour: {}", registerDTO.getEmail());

        // Vérifier les erreurs de validation
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Vérifier que les mots de passe correspondent
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDTO",
                             "Les mots de passe ne correspondent pas");
            return "auth/register";
        }

        try {
            // Enregistrer l'utilisateur (licence d'essai créée automatiquement)
            User user = userService.registerUser(
                registerDTO.getEmail(),
                registerDTO.getFullName(),
                registerDTO.getPassword()
            );

            log.info("Utilisateur {} enregistré avec succès", user.getEmail());

            redirectAttributes.addFlashAttribute("success",
                "Inscription réussie ! Vous avez 30 jours d'essai gratuit. " +
                "Vérifiez votre email pour activer votre compte.");

            return "redirect:/auth/login";

        } catch (IllegalStateException e) {
            result.rejectValue("email", "error.registerDTO", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            model.addAttribute("error", "Une erreur est survenue. Veuillez réessayer.");
            return "auth/register";
        }
    }

    /**
     * Page de connexion
     */
    @GetMapping("/login")
    public String showLoginForm(Model model,
                               @RequestParam(required = false) String error,
                               @RequestParam(required = false) String logout) {

        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }

        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }

        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    /**
     * Traitement de la connexion
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO,
                       BindingResult result,
                       HttpSession session,
                       RedirectAttributes redirectAttributes,
                       @RequestParam(defaultValue = "/") String redirect) {

        log.info("Tentative de connexion pour: {}", loginDTO.getEmail());

        if (result.hasErrors()) {
            return "auth/login";
        }

        Optional<User> userOpt = userService.authenticate(
            loginDTO.getEmail(),
            loginDTO.getPassword()
        );

        if (userOpt.isEmpty()) {
            result.rejectValue("email", "error.loginDTO",
                             "Email ou mot de passe incorrect");
            return "auth/login";
        }

        User user = userOpt.get();

        // Vérifier si le compte est activé
        if (!user.getEnabled()) {
            result.rejectValue("email", "error.loginDTO",
                             "Votre compte est désactivé. Contactez le support.");
            return "auth/login";
        }

        // Vérifier si l'utilisateur a une licence active
        if (!user.hasActiveLicense()) {
            log.warn("Utilisateur {} n'a pas de licence active", user.getEmail());
            session.setAttribute("user", user);
            return "redirect:/pricing";
        }

        // Enregistrer la connexion
        userService.recordLogin(user);

        // Stocker l'utilisateur en session
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());

        log.info("Connexion réussie pour: {}", user.getEmail());

        redirectAttributes.addFlashAttribute("success",
            "Bienvenue " + user.getFullName() + " !");

        return "redirect:" + redirect;
    }


    /**
     * Vérification d'email
     */
    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token,
                             RedirectAttributes redirectAttributes) {

        log.info("Vérification de l'email avec token: {}", token);

        boolean verified = userService.verifyEmail(token);

        if (verified) {
            redirectAttributes.addFlashAttribute("success",
                "Votre email a été vérifié avec succès ! Vous pouvez maintenant vous connecter.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                "Le lien de vérification est invalide ou expiré.");
        }

        return "redirect:/auth/login";
    }

    /**
     * Déconnexion de l'utilisateur
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {

        // Récupérer l'utilisateur avant de détruire la session
        User user = (User) session.getAttribute("user");

        if (user != null) {
            log.info("Déconnexion de l'utilisateur: {}", user.getEmail());
        }

        // Invalider la session
        session.invalidate();

        // Message de confirmation
        redirectAttributes.addFlashAttribute("success",
            "Vous avez été déconnecté avec succès.");

        return "redirect:/auth/login";
    }

    /**
     * Page "Mot de passe oublié"
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    /**
     * Traitement mot de passe oublié
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                RedirectAttributes redirectAttributes) {

        log.info("Demande de réinitialisation de mot de passe pour: {}", email);

        // TODO: Implémenter l'envoi d'email de réinitialisation

        redirectAttributes.addFlashAttribute("message",
            "Si cet email existe, vous recevrez un lien de réinitialisation.");

        return "redirect:/auth/login";
    }
}

