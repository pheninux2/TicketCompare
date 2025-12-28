package pheninux.xdev.ticketcompare.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.PlanType;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PricingController {

    /**
     * Page de tarification
     */
    @GetMapping("/pricing")
    public String showPricing(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        model.addAttribute("plans", PlanType.values());
        model.addAttribute("currentUser", user);

        if (user != null) {
            model.addAttribute("hasActiveLicense", user.hasActiveLicense());
            model.addAttribute("isOnTrial", user.isOnTrial());
            model.addAttribute("daysRemaining", user.getDaysRemaining());
        }

        return "pricing";
    }

    /**
     * Sélection d'un plan
     */
    @PostMapping("/pricing/select")
    public String selectPlan(@RequestParam String planType,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("message",
                "Vous devez créer un compte pour continuer");
            return "redirect:/auth/register";
        }

        try {
            PlanType plan = PlanType.valueOf(planType);

            // Stocker le plan sélectionné en session
            session.setAttribute("selectedPlan", plan);

            log.info("Plan {} sélectionné par {}", plan, user.getEmail());

            // Rediriger vers la page de paiement
            return "redirect:/payment/checkout";

        } catch (Exception e) {
            log.error("Erreur lors de la sélection du plan: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                "Erreur lors de la sélection du plan");
            return "redirect:/pricing";
        }
    }

    /**
     * Page de paiement
     */
    @GetMapping("/payment/checkout")
    public String showCheckout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        PlanType selectedPlan = (PlanType) session.getAttribute("selectedPlan");

        if (selectedPlan == null) {
            return "redirect:/pricing";
        }

        model.addAttribute("plan", selectedPlan);
        model.addAttribute("user", user);

        return "payment/checkout";
    }

    /**
     * Confirmation de paiement
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        // Supprimer le plan sélectionné de la session
        session.removeAttribute("selectedPlan");

        redirectAttributes.addFlashAttribute("success",
            "Paiement effectué avec succès ! Votre licence a été activée.");

        return "redirect:/dashboard";
    }

    /**
     * Annulation de paiement
     */
    @GetMapping("/payment/cancel")
    public String paymentCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warning",
            "Le paiement a été annulé");

        return "redirect:/pricing";
    }
}

