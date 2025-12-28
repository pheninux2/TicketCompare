package pheninux.xdev.ticketcompare.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pheninux.xdev.ticketcompare.entity.User;
import pheninux.xdev.ticketcompare.enums.PlanType;

import java.math.BigDecimal;

@Service
@Slf4j
public class StripeService {

    @Value("${stripe.api.key:sk_test_YOUR_SECRET_KEY}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe initialisé avec succès");
    }

    /**
     * Crée un client Stripe
     */
    public Customer createCustomer(User user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getFullName())
                .build();

        Customer customer = Customer.create(params);
        log.info("Client Stripe créé : {}", customer.getId());
        return customer;
    }

    /**
     * Traite un paiement
     */
    public Charge charge(String token, BigDecimal amount, String email, PlanType plan)
            throws StripeException {

        log.info("Traitement paiement de {}€ pour {}", amount, email);

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(amount.multiply(new BigDecimal("100")).longValue()) // Convertir en centimes
                .setCurrency("eur")
                .setDescription("ShopTracker - " + plan.getDisplayName())
                .setSource(token)
                .setReceiptEmail(email)
                .build();

        Charge charge = Charge.create(params);

        log.info("Paiement réussi - Charge ID: {}", charge.getId());
        return charge;
    }

    /**
     * Crée un abonnement récurrent
     */
    public String createSubscription(User user, PlanType plan) throws StripeException {
        // TODO: Implémenter avec Stripe Subscriptions API
        log.info("Création abonnement {} pour {}", plan, user.getEmail());
        return "sub_" + System.currentTimeMillis();
    }

    /**
     * Annule un abonnement
     */
    public void cancelSubscription(String subscriptionId) throws StripeException {
        // TODO: Implémenter avec Stripe Subscriptions API
        log.info("Annulation abonnement {}", subscriptionId);
    }
}

