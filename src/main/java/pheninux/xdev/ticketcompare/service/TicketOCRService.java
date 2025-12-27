package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pheninux.xdev.ticketcompare.dto.ProductDTO;
import pheninux.xdev.ticketcompare.dto.TicketDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketOCRService {
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d+[.,]\\d{2})");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("([0-9]+(?:[.,][0-9]+)?(?:\\s*(?:kg|l|ml|pcs|piece|pièce|u))?)", Pattern.CASE_INSENSITIVE);

    /**
     * Analyse une image de ticket et extrait les produits
     * @param imageFile Fichier image du ticket
     * @return TicketDTO avec les produits extraits
     */
    public TicketDTO analyzeTicketImage(MultipartFile imageFile) throws IOException, TesseractException {
        log.info("Analyse du ticket: {}", imageFile.getOriginalFilename());

        // Convertir le fichier uploadé en image
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
        File tempFile = File.createTempFile("ticket_", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);

        try {
            // Extraire le texte avec Tesseract
            String extractedText = extractTextFromImage(tempFile);

            // Vérifier si le texte a pu être extrait
            if (extractedText == null || extractedText.trim().isEmpty()) {
                log.warn("⚠️ Tesseract n'a pas pu extraire le texte. Assurez-vous que:");
                log.warn("1. Tesseract-OCR est installé");
                log.warn("2. Le chemin est dans PATH");
                log.warn("3. Les données linguistiques (tessdata) sont présentes");
                log.warn("4. Voir: GUIDE_DEMARRAGE_FINAL.md pour l'installation");

                // Créer un ticket vide avec message
                TicketDTO ticketDTO = new TicketDTO();
                ticketDTO.setDate(LocalDate.now());
                ticketDTO.setProducts(new ArrayList<>());
                ticketDTO.setCreatedAt(LocalDate.now());
                ticketDTO.setNotes("⚠️ OCR non disponible - Tesseract n'a pas pu extraire le texte. Ajoutez les produits manuellement.");
                ticketDTO.setTotalAmount(BigDecimal.ZERO);
                ticketDTO.setStore("À définir");  // Valeur par défaut pour éviter NULL

                return ticketDTO;
            }

            log.debug("Texte extrait:\n{}", extractedText);

            // Parser le texte pour extraire les produits
            List<ProductDTO> products = parseTicketText(extractedText);

            // Créer le TicketDTO
            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setDate(LocalDate.now());
            ticketDTO.setProducts(products);
            ticketDTO.setCreatedAt(LocalDate.now());

            // Extraire le nom du magasin du texte OCR
            String storeName = extractStoreName(extractedText);
            ticketDTO.setStore(storeName != null ? storeName : "À définir");

            // Calculer le montant total
            BigDecimal totalAmount = products.stream()
                .map(ProductDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            ticketDTO.setTotalAmount(totalAmount);

            log.info("Ticket analysé: {} produits détectés, magasin: {}", products.size(), ticketDTO.getStore());
            return ticketDTO;

        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Extrait le texte d'une image using Tesseract OCR
     */
    private String extractTextFromImage(File imageFile) throws TesseractException {
        try {
            Tesseract instance = new Tesseract();

            // Configuration de Tesseract
            instance.setLanguage("fra");  // Français
            instance.setOcrEngineMode(1); // Tesseract only
            instance.setPageSegMode(3);   // Automatic page segmentation with OSD

            // Chemins possibles pour tessdata
            String[] possiblePaths = {
                "C:\\Program Files\\Tesseract-OCR\\tessdata",
                "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata",
                "/usr/local/share/tessdata",
                "/usr/share/tesseract-ocr/4.00/tessdata",
                "/usr/share/tesseract-ocr/tessdata",
                "/opt/homebrew/share/tessdata"
            };

            // Essayer de définir le chemin des données
            for (String path : possiblePaths) {
                java.io.File dataDir = new java.io.File(path);
                if (dataDir.exists()) {
                    instance.setDatapath(path);
                    log.info("Tesseract datapath set to: {}", path);
                    break;
                }
            }

            String result = instance.doOCR(imageFile);
            return result;

        } catch (TesseractException | Error e) {
            log.error("Erreur critique lors de l'OCR: {}", e.getMessage(), e);
            // Fallback: retourner texte vide avec log
            log.warn("OCR failed, returning empty string. Please ensure Tesseract is properly installed.");
            return "";
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'OCR", e);
            return "";
        }
    }

    /**
     * Parse le texte du ticket et extrait les lignes de produits
     */
    private List<ProductDTO> parseTicketText(String ticketText) {
        List<ProductDTO> products = new ArrayList<>();
        String[] lines = ticketText.split("\n");

        // Trouver la section des produits
        int startLine = findProductSectionStart(lines);
        int endLine = findProductSectionEnd(lines, startLine);

        // Fusionner les lignes multi-lignes avant de parser
        List<String> mergedLines = mergeMultiLineProducts(lines, startLine, endLine);

        // Parser uniquement la section des produits
        for (String line : mergedLines) {
            line = line.trim();

            // Ignorer les lignes vides ou les lignes d'en-tête
            if (line.isEmpty() || isHeaderLine(line)) {
                continue;
            }

            ProductDTO product = parseProductLine(line);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    /**
     * Fusionne les lignes de produits qui s'étalent sur plusieurs lignes
     * Ex: Aubergine vrac T4 AT
     *     0.668 kg x 2,19 EUR/Kg
     */
    private List<String> mergeMultiLineProducts(String[] lines, int startLine, int endLine) {
        List<String> merged = new ArrayList<>();

        for (int i = startLine; i < endLine && i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty()) {
                continue;
            }

            // Vérifier si la ligne suivante est une continuation (commence par poids/kg, ou nombre avec unité)
            if (i + 1 < endLine && i + 1 < lines.length) {
                String nextLine = lines[i + 1].trim();

                // Déterminer si nextLine est une continuation de ligne
                if (isLineContinuation(nextLine)) {
                    // Fusionner les deux lignes
                    String mergedLine = line + " " + nextLine;
                    merged.add(mergedLine);
                    i++; // Sauter la ligne suivante car elle a été fusionnée
                    continue;
                }
            }

            merged.add(line);
        }

        return merged;
    }

    /**
     * Vérifie si une ligne est une continuation de la ligne précédente (poids, prix au kilo, etc.)
     * Exemples: "0.668 kg x 2,19 EUR/Kg", "12 pièces", "500ml", etc.
     */
    private boolean isLineContinuation(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        String lowerLine = line.toLowerCase();

        // Patterns pour les continuations de lignes (poids, quantité, prix au kilo, etc.)
        return line.matches("^[0-9.,\\s]+\\s*(kg|g|l|ml|pcs|piece|pièce|u|x).*") ||
               lowerLine.contains("kg x") ||
               lowerLine.contains("x ") && line.matches("^\\d.*") ||
               lowerLine.matches(".*\\d+\\.\\d+\\s*(kg|g|l|ml).*") ||
               (lowerLine.contains("eur/kg") || lowerLine.contains("€/kg")) ||
               line.matches("^\\d+[.,]\\d+\\s*(kg|g|l|ml|pcs|piece|pièce|u).*");
    }

    /**
     * Trouve le début de la section des produits
     */
    private int findProductSectionStart(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase();
            // Chercher des marqueurs de début de liste de produits
            if (line.contains("article") || line.contains("description") ||
                line.contains("p.u") || line.contains("prix") ||
                (line.contains("qté") && line.contains("eur"))) {
                return i + 1; // Commencer après la ligne d'en-tête
            }
        }
        return 0;
    }

    /**
     * Trouve la fin de la section des produits
     */
    private int findProductSectionEnd(String[] lines, int startLine) {
        for (int i = startLine; i < lines.length; i++) {
            String line = lines[i].toLowerCase().trim();
            // Chercher des marqueurs de fin de liste de produits
            if (line.contains("nombre de lignes") ||
                (line.contains("total") && (line.contains("éligible") || line.contains("tva") || line.contains("à payer"))) ||
                line.contains("sous-total") ||
                line.contains("titre") || line.contains("restaurant") ||
                (line.startsWith("33") && line.length() > 15 && line.matches(".*\\d+.*") && !line.contains("produit")) ||
                line.contains("code-barres") ||
                (line.contains("à payer") && line.contains("€") && !line.matches(".*\\d+\\.\\d{2}\\s+\\d+.*"))) {
                return i;
            }
        }
        return lines.length;
    }

    /**
     * Vérifie si la ligne est une ligne d'en-tête (total, sous-total, etc.) ou du bruit OCR
     */
    private boolean isHeaderLine(String line) {
        String lowerLine = line.toLowerCase();

        // Ignorer les lignes contenant des mots clés d'en-tête ou de fin
        if (lowerLine.contains("total") ||
            lowerLine.contains("sous-total") ||
            lowerLine.contains("montant") ||
            lowerLine.contains("caisse") ||
            lowerLine.contains("date") ||
            lowerLine.contains("heure") ||
            lowerLine.contains("magasin") ||
            lowerLine.contains("carrefour") ||
            lowerLine.contains("leclerc") ||
            lowerLine.contains("monoprix") ||
            lowerLine.contains("lidl") ||
            lowerLine.contains("article") ||
            lowerLine.contains("description") ||
            lowerLine.contains("p.u") ||
            lowerLine.contains("qté") ||
            lowerLine.contains("ticket") ||
            lowerLine.contains("siret") ||
            lowerLine.contains("code-barres") ||
            lowerLine.contains("barres") ||
            lowerLine.contains("garantie") ||
            lowerLine.contains("facture") ||
            lowerLine.contains("tva") ||
            lowerLine.contains("restaurant") ||
            lowerLine.contains("contact") ||
            lowerLine.contains("sans") ||
            lowerLine.contains("réduction") ||
            lowerLine.contains("reduction") ||
            lowerLine.contains("route") ||
            lowerLine.contains("siret") ||
            lowerLine.contains("satisfaction") ||
            lowerLine.contains("conditions") ||
            lowerLine.contains("retourner") ||
            lowerLine.contains("merci") ||
            lowerLine.contains("visite")) {
            return true;
        }

        // Ignorer les lignes qui sont principalement des chiffres/codes (codes-barres, numéros de ticket)
        String cleanedLine = line.replaceAll("[\\s.-]", "");
        if (cleanedLine.matches("\\d{12,}") || cleanedLine.matches("[A-Z0-9]{10,}")) {
            return true;
        }

        // Ignorer les lignes trop courtes (probablement du bruit)
        if (line.length() < 3) {
            return true;
        }

        return false;
    }

    /**
     * Parse une ligne pour extraire le produit, quantité et prix
     */
    private ProductDTO parseProductLine(String line) {
        try {
            // Ignorer les lignes trop courtes
            if (line.length() < 5) {
                return null;
            }

            // Nettoyer les suffixes OCR erronés (AT, OAI, T4, etc.)
            String cleanLine = line.replaceAll("\\s+(AT|OAI|T4|T5|T6)\\s*$", "").trim();

            // Nettoyer les symboles aberrants comme "0%" qui sont en fait du bruit
            cleanLine = cleanLine.replaceAll("\\s+0%\\s*", " ").trim();

            log.debug("Parsing ligne: {}", cleanLine);

            // Cas spécial: produit avec poids (ex: "Aubergine vrac 0.668 kg x 2,19 EUR/Kg 1.46")
            if (cleanLine.toLowerCase().contains("kg") && cleanLine.contains("x")) {
                ProductDTO product = parseWeightedProduct(cleanLine);
                if (product != null) {
                    return product;
                }
            }

            // Extraire les prix (format: XX.XX ou XX,XX ou XXX.XX ou XXX,XX)
            Matcher priceMatcher = PRICE_PATTERN.matcher(cleanLine);
            List<String> prices = new ArrayList<>();
            List<Integer> pricePositions = new ArrayList<>();

            while (priceMatcher.find()) {
                String price = priceMatcher.group(1).replace(",", ".");
                try {
                    BigDecimal priceValue = new BigDecimal(price);
                    // Filtrer les prix déraisonnables et les prix trop petits (< 0.01)
                    if (priceValue.compareTo(new BigDecimal("0.01")) > 0 &&
                        priceValue.compareTo(new BigDecimal("10000")) < 0) {
                        prices.add(price);
                        pricePositions.add(priceMatcher.start());
                    }
                } catch (Exception e) {
                    // Ignorer les prix invalides
                }
            }

            if (prices.isEmpty()) {
                log.debug("Aucun prix trouvé dans: {}", line);
                return null;
            }

            // Extraire le nom du produit (avant le premier prix)
            String productName = cleanLine.substring(0, pricePositions.get(0)).trim();
            productName = productName.replaceAll("\\s+", " ");

            // Nettoyer les codes de produits mal reconnus au début (codes EAN mal lus)
            productName = productName.replaceAll("^\\d{4,}\\s+", "").trim();

            // Valider le nom
            if (productName.isEmpty() || productName.length() < 2 ||
                productName.matches(".*\\d{10,}.*") ||
                productName.matches("[A-Z0-9]{4,}") ||
                productName.matches("[\\d.,\\s]+") ||
                productName.length() > 100) {
                log.debug("Nom de produit invalide: {}", productName);
                return null;
            }

            ProductDTO product = new ProductDTO();
            product.setName(productName);

            // Parsing des prix selon la stratégie
            BigDecimal unitPrice;
            BigDecimal totalPrice;
            BigDecimal quantity = BigDecimal.ONE;

            if (prices.size() == 1) {
                // Un seul prix = prix unitaire et total identique
                unitPrice = new BigDecimal(prices.get(0));
                totalPrice = unitPrice;
                quantity = BigDecimal.ONE;

            } else if (prices.size() == 2) {
                // Deux prix: généralement PU et Total
                BigDecimal price1 = new BigDecimal(prices.get(0));
                BigDecimal price2 = new BigDecimal(prices.get(1));

                // Le prix unitaire est toujours inférieur au prix total
                if (price1.compareTo(price2) <= 0) {
                    unitPrice = price1;
                    totalPrice = price2;
                } else {
                    unitPrice = price2;
                    totalPrice = price1;
                }

                // Calculer la quantité
                if (unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                    quantity = totalPrice.divide(unitPrice, 3, java.math.RoundingMode.HALF_UP);
                } else {
                    quantity = BigDecimal.ONE;
                }

            } else {
                // 3+ prix: première = PU, dernière = Total, les autres sont ignorées
                unitPrice = new BigDecimal(prices.get(0));
                totalPrice = new BigDecimal(prices.get(prices.size() - 1));

                if (unitPrice.compareTo(BigDecimal.ZERO) > 0) {
                    quantity = totalPrice.divide(unitPrice, 3, java.math.RoundingMode.HALF_UP);
                } else {
                    quantity = BigDecimal.ONE;
                }
            }

            // Validation finale
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0 || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }

            product.setPrice(unitPrice);
            product.setQuantity(quantity);
            product.setTotalPrice(totalPrice);
            product.setCategory(inferCategory(productName));
            product.setUnit("u");

            log.debug("✓ Produit parsé: {} - PU: {}, Qté: {}, Total: {}",
                productName, unitPrice, quantity, totalPrice);

            return product;

        } catch (Exception e) {
            log.warn("Erreur parsing: {} - {}", line, e.getMessage());
            return null;
        }
    }

    /**
     * Parse un produit vendu au poids (kg, grammes, etc.)
     * Exemple: "Aubergine vrac 0.668 kg x 2,19 EUR/Kg 1.46"
     * Où: 0.668 kg = quantité/poids, 2.19 = prix au kg, 1.46 = prix total
     */
    private ProductDTO parseWeightedProduct(String line) {
        try {
            log.debug("Parsing produit au poids: {}", line);

            // Pattern pour extraire: productName, poids (avec unité), prix/unité, prix total
            // Ex: "Aubergine vrac 0.668 kg x 2,19 EUR/Kg 1.46"
            Pattern weightPattern = Pattern.compile(
                "^(.+?)\\s+(\\d+[.,]\\d+)\\s*(kg|g|l|ml)\\s+x\\s+(\\d+[.,]\\d+).*(?:eur|€|/kg|/l|/ml).*\\s+(\\d+[.,]\\d+)\\s*$",
                Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = weightPattern.matcher(line);

            if (!matcher.find()) {
                return null;
            }

            String productName = matcher.group(1).trim();
            String weightStr = matcher.group(2).replace(",", ".");
            String unit = matcher.group(3).toLowerCase();
            String pricePerUnitStr = matcher.group(4).replace(",", ".");
            String totalPriceStr = matcher.group(5).replace(",", ".");

            // Nettoyer le nom du produit
            productName = productName.replaceAll("\\s+(AT|OAI|T4|T5|T6)\\s*$", "").trim();

            if (productName.isEmpty() || productName.length() < 2) {
                return null;
            }

            ProductDTO product = new ProductDTO();
            product.setName(productName);

            BigDecimal weight = new BigDecimal(weightStr);
            BigDecimal pricePerUnit = new BigDecimal(pricePerUnitStr);
            BigDecimal totalPrice = new BigDecimal(totalPriceStr);

            // Pour les produits au poids, la quantité est le poids
            product.setQuantity(weight);
            // Le prix unitaire est le prix par kg/litre/etc.
            product.setPrice(pricePerUnit);
            product.setTotalPrice(totalPrice);
            product.setCategory(inferCategory(productName));
            product.setUnit(unit); // kg, g, l, ml, etc.

            log.debug("✓ Produit au poids parsé: {} - Qté: {} {}, Prix/unité: {}, Total: {}",
                productName, weight, unit, pricePerUnit, totalPrice);

            return product;

        } catch (Exception e) {
            log.warn("Erreur parsing produit au poids: {} - {}", line, e.getMessage());
            return null;
        }
    }

    /**
     * Déduit la catégorie basée sur le nom du produit
     */
    private String inferCategory(String productName) {
        String name = productName.toLowerCase();

        if (name.matches(".*(lait|yaourt|fromage|beurre|crème|crémeux).*")) {
            return "Laitier";
        } else if (name.matches(".*(pain|baguette|croissant|viennoiserie).*")) {
            return "Boulangerie";
        } else if (name.matches(".*(tomate|carotte|oignon|pomme|banane|orange|citron|fruit|légume|legume|raisin|aubergine|courgette|poivron|salade|laitue|concombre|navet|radis|poireau|chou|brocoli|épinard|epinard|haricot|pois|fraise|framboise|cerise|poire|pêche|peche|abricot|prune|kiwi|mangue|ananas|melon|pastèque|pasteque).*")) {
            return "Fruits & Légumes";
        } else if (name.matches(".*(viande|poulet|boeuf|porc|jambon|saucisse|steak|côte|cote|rôti|roti).*")) {
            return "Viande";
        } else if (name.matches(".*(poisson|saumon|truite|morue|thon|cabillaud|merlu|sole).*")) {
            return "Poisson";
        } else if (name.matches(".*(chocolat|bonbon|sucrerie|confiserie).*")) {
            return "Confiserie";
        } else if (name.matches(".*(biscuit|cookie|gâteau|gateau|pâtisserie|patisserie).*")) {
            return "Biscuiterie";
        } else if (name.matches(".*(riz|pâte|pate|féculent|feculent|semoule|blé|ble|quinoa).*")) {
            return "Féculents";
        } else if (name.matches(".*(huile|sauce|vinaigrette|vinaigre|moutarde|ketchup|mayonnaise).*")) {
            return "Condiments";
        } else if (name.matches(".*(café|cafe|thé|the|boisson|jus|eau|soda|limonade).*")) {
            return "Boissons";
        }

        return "Autre";
    }

    /**
     * Extrait le nom du magasin du texte du ticket
     */
    private String extractStoreName(String ticketText) {
        if (ticketText == null || ticketText.isEmpty()) {
            return null;
        }

        String[] lines = ticketText.split("\n");

        // Chercher les noms de magasins courants
        String[] storeNames = {"LIDL", "CARREFOUR", "LECLERC", "MONOPRIX", "AUCHAN", "E.LECLERC",
                              "INTERMACHE", "CASINO", "SUPER_U", "SUPER U", "FRANPRIX", "SIMPLY MARKET",
                              "CORA", "LEADER_PRICE", "LEADER PRICE", "ATAC", "PRIX_MINI", "PRIX MINI"};

        for (String line : lines) {
            String upperLine = line.toUpperCase().trim();
            for (String store : storeNames) {
                if (upperLine.contains(store)) {
                    return store;
                }
            }
        }

        return null;
    }

    /**
     * Analyse un fichier image local du disque
     */
    public TicketDTO analyzeTicketImageFromPath(String imagePath) throws IOException, TesseractException {
        log.info("Analyse du ticket depuis le chemin: {}", imagePath);

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException("Fichier non trouvé: " + imagePath);
        }

        BufferedImage bufferedImage = ImageIO.read(imageFile);
        File tempFile = File.createTempFile("ticket_", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);

        try {
            String extractedText = extractTextFromImage(tempFile);
            List<ProductDTO> products = parseTicketText(extractedText);

            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setDate(LocalDate.now());
            ticketDTO.setProducts(products);
            ticketDTO.setCreatedAt(LocalDate.now());

            // Extraire le nom du magasin du texte OCR
            String storeName = extractStoreName(extractedText);
            ticketDTO.setStore(storeName != null ? storeName : "À définir");

            BigDecimal totalAmount = products.stream()
                .map(ProductDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            ticketDTO.setTotalAmount(totalAmount);

            return ticketDTO;

        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}

