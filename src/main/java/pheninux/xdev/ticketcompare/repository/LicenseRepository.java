package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.License;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.LicenseType;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findByUser(User user);

    Optional<License> findByUserId(Long userId);

    Optional<License> findByLicenseKey(String licenseKey);

    List<License> findByStatus(SubscriptionStatus status);

    List<License> findByLicenseType(LicenseType licenseType);

    @Query("SELECT l FROM License l WHERE l.expiryDate <= :date AND l.status = 'ACTIVE' AND l.licenseType != 'LIFETIME'")
    List<License> findExpiringLicenses(LocalDate date);

    @Query("SELECT l FROM License l WHERE l.expiryDate BETWEEN :startDate AND :endDate AND l.status = 'ACTIVE' AND l.autoRenew = true")
    List<License> findLicensesNeedingRenewal(LocalDate startDate, LocalDate endDate);
}

