package pheninux.xdev.ticketcompare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean pour l'encodeur de mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de la sécurité HTTP
     * Pour l'instant, on autorise tous les accès pour ne pas bloquer l'application
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permet tous les accès pour l'instant
            )
            .csrf(csrf -> csrf.disable()) // Désactiver CSRF temporairement
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()) // Pour H2 Console
            );

        return http.build();
    }
}

