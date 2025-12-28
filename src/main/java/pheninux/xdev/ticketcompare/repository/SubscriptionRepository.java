package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.Subscription;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(User user);

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findFirstByUserAndStatusOrderByCreatedAtDesc(User user, SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate <= :date AND s.status = 'ACTIVE' AND s.autoRenew = true")
    List<Subscription> findSubscriptionsNeedingBilling(LocalDate date);

    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    Optional<Subscription> findActiveSubscriptionByUser(User user);
}

