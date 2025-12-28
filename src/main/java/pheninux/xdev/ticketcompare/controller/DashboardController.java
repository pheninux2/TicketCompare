package pheninux.xdev.ticketcompare.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.service.LicenseService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final LicenseService licenseService;

    /**
     * Dashboard principal
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        // Vérifier si l'utilisateur a une licence active
        if (!user.hasActiveLicense()) {
            log.warn("Utilisateur {} sans licence active tente d'accéder au dashboard",
                    user.getEmail());
            return "redirect:/pricing";
        }

        Optional<License> licenseOpt = licenseService.getUserLicense(user);

        if (licenseOpt.isPresent()) {
            License license = licenseOpt.get();
            model.addAttribute("license", license);
            model.addAttribute("daysRemaining", license.getDaysRemaining());
            model.addAttribute("isExpiringSoon", license.isExpiringSoon());
        }

        model.addAttribute("user", user);
        model.addAttribute("isOnTrial", user.isOnTrial());

        return "dashboard";
    }

    /**
     * Page de profil utilisateur
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);

        Optional<License> licenseOpt = licenseService.getUserLicense(user);
        licenseOpt.ifPresent(license -> model.addAttribute("license", license));

        return "profile";
    }
}

