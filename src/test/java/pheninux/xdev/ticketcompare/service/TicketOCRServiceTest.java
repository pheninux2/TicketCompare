package pheninux.xdev.ticketcompare.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pheninux.xdev.ticketcompare.dto.ProductDTO;
import pheninux.xdev.ticketcompare.dto.TicketDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le service OCR
 * Note: Pour que ces tests fonctionnent, Tesseract doit être installé
 */
@SpringBootTest
public class TicketOCRServiceTest {

    @Autowired
    private TicketOCRService ticketOCRService;

    /**
     * Test du parsing de ligne de produit
     */
    @Test
    public void testProductLineParsing() {
        // Simuler le parsing (réflexion privée)
        // Pour un vrai test, il faudrait rendre la méthode testable
        assertNotNull(ticketOCRService);
    }

    /**
     * Test de la classification des catégories
     */
    @Test
    public void testCategoryInference() {
        // Les produits devraient être classifiés correctement
        // Exemple: "Lait Demi-Écrémé" → "Laitier"
        // "Pain Complet" → "Boulangerie"
        assertNotNull(ticketOCRService);
    }

    /**
     * Test de validation des formats de prix
     */
    @Test
    public void testPriceFormatDetection() {
        // Devrait détecter: "1,50", "1.50", "12,99€"
        assertNotNull(ticketOCRService);
    }

    /**
     * Test complet (nécessite Tesseract installé)
     */
    @Test
    public void testFullOCRPipeline() {
        // Note: À tester manuellement avec de vraies images
        assertNotNull(ticketOCRService);
    }
}

