package pheninux.xdev.ticketcompare.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pheninux.xdev.ticketcompare.entity.User;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class LicenseCheckInterceptor implements HandlerInterceptor {

    // URLs qui ne nécessitent pas de licence active
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/auth/",
        "/pricing",
        "/payment/",
        "/css/",
        "/js/",
        "/images/",
        "/favicon.ico",
        "/error",
        "/actuator/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        // Ignorer les chemins exclus
        if (isExcludedPath(requestURI)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.debug("Pas de session - redirection vers login");
            response.sendRedirect("/auth/login?redirect=" + requestURI);
            return false;
        }

        User user = (User) session.getAttribute("user");

        if (user == null) {
            log.debug("Utilisateur non connecté - redirection vers login");
            response.sendRedirect("/auth/login?redirect=" + requestURI);
            return false;
        }

        // Vérifier si l'utilisateur a une licence active
        if (!user.hasActiveLicense()) {
            log.warn("Utilisateur {} sans licence active tente d'accéder à {}",
                    user.getEmail(), requestURI);
            response.sendRedirect("/pricing");
            return false;
        }

        // Vérifier si la licence expire bientôt
        if (user.getLicense() != null && user.getLicense().isExpiringSoon()) {
            long daysRemaining = user.getDaysRemaining();
            log.info("Licence de {} expire dans {} jours", user.getEmail(), daysRemaining);

            // Ajouter un attribut pour afficher une bannière d'avertissement
            request.setAttribute("licenseExpiringSoon", true);
            request.setAttribute("daysRemaining", daysRemaining);
        }

        return true;
    }

    /**
     * Vérifie si le chemin est dans la liste des exclusions
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}

