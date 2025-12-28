package pheninux.xdev.ticketcompare.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.service.LicenseService;

import java.util.Optional;

@Controller
@RequestMapping("/license")
@RequiredArgsConstructor
@Slf4j
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * Page d'information sur la licence utilisateur
     */
    @GetMapping
    public String showLicense(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<License> licenseOpt = licenseService.getUserLicense(user);

        if (licenseOpt.isEmpty()) {
            log.warn("Utilisateur {} sans licence", user.getEmail());
            return "redirect:/pricing";
        }

        License license = licenseOpt.get();

        model.addAttribute("user", user);
        model.addAttribute("license", license);
        model.addAttribute("daysRemaining", license.getDaysRemaining());
        model.addAttribute("isExpiringSoon", license.isExpiringSoon());
        model.addAttribute("isActive", license.isActive());
        model.addAttribute("isExpired", license.isExpired());

        return "license/details";
    }
}

