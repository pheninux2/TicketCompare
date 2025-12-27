package pheninux.xdev.ticketcompare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pheninux.xdev.ticketcompare.dto.ProductDTO;
import pheninux.xdev.ticketcompare.dto.TicketDTO;
import pheninux.xdev.ticketcompare.entity.PriceHistory;
import pheninux.xdev.ticketcompare.entity.Product;
import pheninux.xdev.ticketcompare.entity.Ticket;
import pheninux.xdev.ticketcompare.repository.PriceHistoryRepository;
import pheninux.xdev.ticketcompare.repository.ProductRepository;
import pheninux.xdev.ticketcompare.repository.TicketRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        ticket.setDate(ticketDTO.getDate());

        // S'assurer que le store n'est jamais null
        String store = ticketDTO.getStore();
        if (store == null || store.trim().isEmpty()) {
            store = "À définir";
        }
        ticket.setStore(store);

        ticket.setTotalAmount(ticketDTO.getTotalAmount());
        ticket.setNotes(ticketDTO.getNotes());

        if (ticketDTO.getProducts() != null) {
            for (ProductDTO productDTO : ticketDTO.getProducts()) {
                Product product = new Product();
                product.setName(productDTO.getName());
                product.setCategory(productDTO.getCategory());
                product.setPrice(productDTO.getPrice());
                product.setQuantity(productDTO.getQuantity());
                product.setUnit(productDTO.getUnit());
                product.setDescription(productDTO.getDescription());
                ticket.addProduct(product);

                // Save price history (only if price is not null)
                if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                    PriceHistory history = PriceHistory.builder()
                        .productName(product.getName())
                        .price(product.getPrice())
                        .priceDate(ticket.getDate())
                        .store(ticket.getStore())
                        .unit(product.getUnit())
                        .build();
                    priceHistoryRepository.save(history);
                }
            }
        }

        Ticket saved = ticketRepository.save(ticket);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicketById(Long id) {
        return ticketRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> getAllTickets(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findAll(pageable)
            .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> getTicketsByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findByDateBetween(startDate, endDate, pageable)
            .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> getTicketsByStore(String store, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ticketRepository.findByStore(store, pageable)
            .map(this::mapToDTO);
    }

    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Validation : ne pas accepter une date NULL
        if (ticketDTO.getDate() == null) {
            throw new IllegalArgumentException("La date du ticket ne peut pas être vide");
        }

        ticket.setDate(ticketDTO.getDate());
        ticket.setStore(ticketDTO.getStore());
        ticket.setTotalAmount(ticketDTO.getTotalAmount());
        ticket.setNotes(ticketDTO.getNotes());

        // Mettre à jour les produits existants
        if (ticketDTO.getProducts() != null && !ticketDTO.getProducts().isEmpty()) {
            List<Product> existingProducts = ticket.getProducts();

            for (int i = 0; i < ticketDTO.getProducts().size() && i < existingProducts.size(); i++) {
                ProductDTO productDTO = ticketDTO.getProducts().get(i);
                Product existingProduct = existingProducts.get(i);

                // Mettre à jour les champs modifiables
                existingProduct.setPrice(productDTO.getPrice());
                existingProduct.setQuantity(productDTO.getQuantity());
                existingProduct.setCategory(productDTO.getCategory());

                // Recalculer le prix total
                if (productDTO.getPrice() != null && productDTO.getQuantity() != null) {
                    BigDecimal totalPrice = productDTO.getPrice()
                        .multiply(productDTO.getQuantity());
                    existingProduct.setTotalPrice(totalPrice);

                    // Mettre à jour l'historique des prix
                    PriceHistory history = PriceHistory.builder()
                        .productName(existingProduct.getName())
                        .price(existingProduct.getPrice())
                        .priceDate(ticket.getDate())
                        .store(ticket.getStore())
                        .unit(existingProduct.getUnit())
                        .build();
                    priceHistoryRepository.save(history);
                }
            }
        }

        Ticket updated = ticketRepository.save(ticket);
        return mapToDTO(updated);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    /**
     * Obtient les catégories disponibles dans la base de données
     */
    @Transactional(readOnly = true)
    public List<String> getAvailableCategories() {
        return productRepository.findDistinctCategories();
    }

    /**
     * Met à jour la catégorie d'un produit
     */
    @Transactional
    public void updateProductCategory(Long productId, String category) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        product.setCategory(category);
        productRepository.save(product);
    }

    /**
     * Recatégorise tous les produits existants en utilisant la logique d'inférence automatique
     */
    @Transactional
    public int recategorizeAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        int updatedCount = 0;

        for (Product product : allProducts) {
            String newCategory = inferCategory(product.getName());
            if (!newCategory.equals(product.getCategory())) {
                product.setCategory(newCategory);
                productRepository.save(product);
                updatedCount++;
            }
        }

        return updatedCount;
    }

    /**
     * Déduit la catégorie basée sur le nom du produit
     * (Copie de la méthode du TicketOCRService pour cohérence)
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

    private TicketDTO mapToDTO(Ticket ticket) {
        return TicketDTO.builder()
            .id(ticket.getId())
            .date(ticket.getDate())
            .store(ticket.getStore())
            .totalAmount(ticket.getTotalAmount())
            .notes(ticket.getNotes())
            .createdAt(ticket.getCreatedAt())
            .products(ticket.getProducts().stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList()))
            .build();
    }

    private ProductDTO mapProductToDTO(Product product) {
        return ProductDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .category(product.getCategory())
            .price(product.getPrice())
            .quantity(product.getQuantity())
            .unit(product.getUnit())
            .description(product.getDescription())
            .totalPrice(product.getTotalPrice())
            .build();
    }
}

