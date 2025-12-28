package pheninux.xdev.ticketcompare.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.service.LicenseService;

import java.util.Optional;

@Controller
@RequestMapping("/license")
@RequiredArgsConstructor
@Slf4j
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * Page de gestion de licence
     */
    @GetMapping
    public String showLicense(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<License> licenseOpt = licenseService.getUserLicense(user);

        if (licenseOpt.isPresent()) {
            License license = licenseOpt.get();
            model.addAttribute("license", license);
            model.addAttribute("daysRemaining", license.getDaysRemaining());
            model.addAttribute("isExpiringSoon", license.isExpiringSoon());
            model.addAttribute("isActive", license.isActive());
        }

        return "license/manage";
    }

    /**
     * Upgrade de licence
     */
    @PostMapping("/upgrade")
    public String upgradeLicense(@RequestParam String licenseType,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            LicenseType newType = LicenseType.valueOf(licenseType);

            log.info("Upgrade de licence pour {} vers {}", user.getEmail(), newType);

            License license = licenseService.upgradeLicense(user, newType);

            // Mettre à jour l'utilisateur en session
            session.setAttribute("user", license.getUser());

            redirectAttributes.addFlashAttribute("success",
                "Votre licence a été mise à niveau avec succès vers " + newType.getDisplayName());

        } catch (Exception e) {
            log.error("Erreur lors de l'upgrade: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "Erreur lors de la mise à niveau de la licence");
        }

        return "redirect:/license";
    }

    /**
     * Renouvellement de licence
     */
    @PostMapping("/renew")
    public String renewLicense(HttpSession session,
                              RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            Optional<License> licenseOpt = licenseService.getUserLicense(user);

            if (licenseOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune licence trouvée");
                return "redirect:/license";
            }

            License license = licenseService.renewLicense(licenseOpt.get().getId());

            redirectAttributes.addFlashAttribute("success",
                "Votre licence a été renouvelée jusqu'au " + license.getExpiryDate());

        } catch (Exception e) {
            log.error("Erreur lors du renouvellement: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "Erreur lors du renouvellement de la licence");
        }

        return "redirect:/license";
    }

    /**
     * Annulation de licence
     */
    @PostMapping("/cancel")
    public String cancelLicense(HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            Optional<License> licenseOpt = licenseService.getUserLicense(user);

            if (licenseOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune licence trouvée");
                return "redirect:/license";
            }

            licenseService.cancelLicense(licenseOpt.get().getId());

            redirectAttributes.addFlashAttribute("warning",
                "Votre licence a été annulée. Vous pouvez continuer à utiliser " +
                "l'application jusqu'à la fin de votre période payée.");

        } catch (Exception e) {
            log.error("Erreur lors de l'annulation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "Erreur lors de l'annulation de la licence");
        }

        return "redirect:/license";
    }

    /**
     * Activation auto-renouvellement
     */
    @PostMapping("/auto-renew")
    public String toggleAutoRenew(@RequestParam boolean enable,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            Optional<License> licenseOpt = licenseService.getUserLicense(user);

            if (licenseOpt.isPresent()) {
                License license = licenseOpt.get();
                license.setAutoRenew(enable);

                String message = enable
                    ? "Le renouvellement automatique a été activé"
                    : "Le renouvellement automatique a été désactivé";

                redirectAttributes.addFlashAttribute("success", message);
            }

        } catch (Exception e) {
            log.error("Erreur lors du changement d'auto-renew: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "Erreur lors de la modification du renouvellement automatique");
        }

        return "redirect:/license";
    }
}

