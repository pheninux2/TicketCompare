package pheninux.xdev.ticketcompare.entity;

import jakarta.persistence.*;
import lombok.*;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private String password; // Sera hashé avec BCrypt

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(unique = true)
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;

    // Relation avec la licence
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private License license;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Vérifie si l'utilisateur a une licence active
     */
    public boolean hasActiveLicense() {
        return license != null && license.isActive();
    }

    /**
     * Vérifie si l'utilisateur est en période d'essai
     */
    public boolean isOnTrial() {
        return license != null && license.getLicenseType() == LicenseType.TRIAL && license.isActive();
    }

    /**
     * Obtient le nombre de jours restants
     */
    public long getDaysRemaining() {
        if (license == null || !license.isActive()) {
            return 0;
        }
        return license.getDaysRemaining();
    }
}

