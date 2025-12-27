package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pheninux.xdev.ticketcompare.dto.TicketDTO;
import pheninux.xdev.ticketcompare.service.TicketService;
import pheninux.xdev.ticketcompare.service.TicketOCRService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {
    private final TicketService ticketService;
    private final TicketOCRService ticketOCRService;

    @GetMapping
    public String listTickets(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        Page<TicketDTO> tickets = ticketService.getAllTickets(page, size);
        model.addAttribute("tickets", tickets);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tickets.getTotalPages());
        return "tickets/list";
    }

    @GetMapping("/filter")
    public String filterByDate(@RequestParam LocalDate startDate,
                              @RequestParam LocalDate endDate,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<TicketDTO> tickets = ticketService.getTicketsByDateRange(startDate, endDate, page, size);
        model.addAttribute("tickets", tickets);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        return "tickets/list";
    }

    // IMPORTANT: /new et /create doivent être AVANT /{id}
    @GetMapping("/new")
    public String showNewTicketForm(Model model) {
        model.addAttribute("ticket", new TicketDTO());
        return "tickets/create";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new TicketDTO());
        return "tickets/scan"; // Affiche la page avec les 3 options
    }

    @GetMapping("/create/manual")
    public String showManualCreateForm(Model model) {
        model.addAttribute("ticket", new TicketDTO());
        return "tickets/create-manual"; // Formulaire manuel
    }

    @PostMapping
    public String createTicket(@ModelAttribute TicketDTO ticketDTO) {
        ticketService.createTicket(ticketDTO);
        return "redirect:/tickets";
    }

    // /{id} doit être APRÈS les routes spécifiques
    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        TicketDTO ticket = ticketService.getTicketById(id);
        List<String> categories = ticketService.getAvailableCategories();
        model.addAttribute("ticket", ticket);
        model.addAttribute("categories", categories);
        return "tickets/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        TicketDTO ticket = ticketService.getTicketById(id);
        model.addAttribute("ticket", ticket);
        return "tickets/edit";
    }

    @PostMapping("/{id}")
    public String updateTicket(@PathVariable Long id, @ModelAttribute TicketDTO ticketDTO) {
        ticketService.updateTicket(id, ticketDTO);
        return "redirect:/tickets/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return "redirect:/tickets";
    }

    // ==================== OCR Scanning Methods ====================

    /**
     * Affiche le formulaire de scan de ticket
     */
    @GetMapping("/scan")
    public String showScanForm(Model model) {
        model.addAttribute("ticket", new TicketDTO());
        return "tickets/scan";
    }

    /**
     * Upload et analyse une image de ticket
     */
    @PostMapping("/scan/upload")
    public String uploadTicketImage(@RequestParam("file") MultipartFile file,
                                   @RequestParam(required = false) String store,
                                   @RequestParam(required = false) String notes,
                                   Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Veuillez sélectionner une image");
                return "tickets/scan";
            }

            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                model.addAttribute("error", "Le fichier doit être une image");
                return "tickets/scan";
            }

            log.info("Analyse de l'image: {}", file.getOriginalFilename());

            // Analyser l'image avec OCR
            TicketDTO analyzedTicket = ticketOCRService.analyzeTicketImage(file);

            // Ajouter les informations optionnelles
            if (store != null && !store.isEmpty()) {
                analyzedTicket.setStore(store);
            }
            if (notes != null && !notes.isEmpty()) {
                analyzedTicket.setNotes(notes);
            }

            // S'assurer que le store n'est pas null
            if (analyzedTicket.getStore() == null || analyzedTicket.getStore().isEmpty()) {
                analyzedTicket.setStore("À définir");
            }

            // Sauvegarder le ticket
            TicketDTO savedTicket = ticketService.createTicket(analyzedTicket);

            model.addAttribute("success", "Ticket analysé et créé avec succès!");
            model.addAttribute("ticket", savedTicket);
            model.addAttribute("productsCount", savedTicket.getProducts().size());

            return "tickets/scan-result";

        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier", e);
            model.addAttribute("error", "Erreur lors de la lecture du fichier: " + e.getMessage());
            return "tickets/scan";
        } catch (TesseractException e) {
            log.error("Erreur lors de l'OCR", e);
            model.addAttribute("error", "Erreur lors de l'analyse du ticket: " + e.getMessage());
            return "tickets/scan";
        }
    }

    /**
     * Analyse une image de ticket depuis un chemin local
     */
    @PostMapping("/scan/path")
    public String analyzeTicketFromPath(@RequestParam String imagePath,
                                       @RequestParam(required = false) String store,
                                       @RequestParam(required = false) String notes,
                                       Model model) {
        try {
            log.info("Analyse du ticket depuis le chemin: {}", imagePath);

            // Analyser l'image
            TicketDTO analyzedTicket = ticketOCRService.analyzeTicketImageFromPath(imagePath);

            if (store != null && !store.isEmpty()) {
                analyzedTicket.setStore(store);
            }
            if (notes != null && !notes.isEmpty()) {
                analyzedTicket.setNotes(notes);
            }

            // Sauvegarder le ticket
            TicketDTO savedTicket = ticketService.createTicket(analyzedTicket);

            model.addAttribute("success", "Ticket analysé et créé avec succès!");
            model.addAttribute("ticket", savedTicket);
            model.addAttribute("productsCount", savedTicket.getProducts().size());

            return "tickets/scan-result";

        } catch (IOException e) {
            log.error("Fichier non trouvé", e);
            model.addAttribute("error", "Fichier non trouvé: " + e.getMessage());
            return "tickets/scan";
        } catch (TesseractException e) {
            log.error("Erreur lors de l'OCR", e);
            model.addAttribute("error", "Erreur lors de l'analyse: " + e.getMessage());
            return "tickets/scan";
        }
    }

    /**
     * API REST pour mettre à jour la catégorie d'un produit
     */
    @PostMapping("/api/product/{productId}/category")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProductCategory(@PathVariable Long productId,
                                                                     @RequestParam String category) {
        try {
            ticketService.updateProductCategory(productId, category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Catégorie mise à jour avec succès");
            response.put("productId", productId);
            response.put("category", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API REST pour obtenir les catégories disponibles
     */
    @GetMapping("/api/categories")
    @ResponseBody
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = ticketService.getAvailableCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * API REST pour recatégoriser automatiquement tous les produits
     */
    @PostMapping("/api/recategorize-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recategorizeAllProducts() {
        try {
            int updatedCount = ticketService.recategorizeAllProducts();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Recatégorisation terminée avec succès");
            response.put("updatedCount", updatedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la recatégorisation", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la recatégorisation: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
