package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pheninux.xdev.ticketcompare.dto.TicketDTO;
import pheninux.xdev.ticketcompare.service.TicketOCRService;
import pheninux.xdev.ticketcompare.service.TicketService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tickets/ocr")
@RequiredArgsConstructor
@Slf4j
public class TicketOCRRestController {
    private final TicketOCRService ticketOCRService;
    private final TicketService ticketService;

    /**
     * Analyse une image de ticket uploadée
     * @param file Fichier image du ticket
     * @param store Magasin (optionnel)
     * @param notes Notes (optionnel)
     * @param autoSave Sauvegarder automatiquement (par défaut: false)
     */
    @PostMapping("/analyze/upload")
    public ResponseEntity<?> analyzeUploadedImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean autoSave) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Fichier vide"));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(errorResponse("Le fichier doit être une image"));
            }

            log.info("OCR API: Analyse de l'image {}", file.getOriginalFilename());

            // Analyser l'image
            TicketDTO analyzedTicket = ticketOCRService.analyzeTicketImage(file);

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

            // Sauvegarder automatiquement si demandé
            if (autoSave) {
                TicketDTO savedTicket = ticketService.createTicket(analyzedTicket);
                return ResponseEntity.ok(successResponse("Ticket analysé et créé", savedTicket));
            } else {
                return ResponseEntity.ok(successResponse("Ticket analysé", analyzedTicket));
            }

        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse("Erreur lors de la lecture du fichier: " + e.getMessage()));
        } catch (TesseractException e) {
            log.error("Erreur OCR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Erreur lors de l'OCR: " + e.getMessage()));
        }
    }

    /**
     * Analyse une image depuis un chemin local
     * @param imagePath Chemin du fichier image
     * @param store Magasin (optionnel)
     * @param notes Notes (optionnel)
     * @param autoSave Sauvegarder automatiquement (par défaut: false)
     */
    @PostMapping("/analyze/path")
    public ResponseEntity<?> analyzeLocalImage(
            @RequestParam String imagePath,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "false") boolean autoSave) {

        try {
            log.info("OCR API: Analyse du fichier {}", imagePath);

            TicketDTO analyzedTicket = ticketOCRService.analyzeTicketImageFromPath(imagePath);

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

            if (autoSave) {
                TicketDTO savedTicket = ticketService.createTicket(analyzedTicket);
                return ResponseEntity.ok(successResponse("Ticket analysé et créé", savedTicket));
            } else {
                return ResponseEntity.ok(successResponse("Ticket analysé", analyzedTicket));
            }

        } catch (IOException e) {
            log.error("Fichier non trouvé", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse("Fichier non trouvé: " + e.getMessage()));
        } catch (TesseractException e) {
            log.error("Erreur OCR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Erreur lors de l'OCR: " + e.getMessage()));
        }
    }

    /**
     * Analyse et crée un ticket en une seule requête
     */
    @PostMapping("/analyze-and-create")
    public ResponseEntity<?> analyzeAndCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String notes) {

        try {
            log.info("OCR API: Analyse et création du ticket {}", file.getOriginalFilename());

            TicketDTO analyzedTicket = ticketOCRService.analyzeTicketImage(file);

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

            TicketDTO savedTicket = ticketService.createTicket(analyzedTicket);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Ticket créé avec succès");
            response.put("data", savedTicket);
            response.put("productsCount", savedTicket.getProducts().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors du traitement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Erreur: " + e.getMessage()));
        }
    }

    /**
     * Crée une réponse d'erreur standard
     */
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    /**
     * Crée une réponse de succès standard
     */
    private Map<String, Object> successResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * Health check pour l'API OCR
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "OCR API");
        response.put("version", "1.0");
        response.put("available", true);
        return ResponseEntity.ok(response);
    }
}

