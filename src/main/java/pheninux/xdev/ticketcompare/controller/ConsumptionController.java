package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pheninux.xdev.ticketcompare.service.ConsumptionService;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Controller
@RequestMapping("/consumption")
@RequiredArgsConstructor
@Slf4j
public class ConsumptionController {
    private final ConsumptionService consumptionService;

    @GetMapping("/weekly")
    public String weeklyConsumption(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            Model model) {

        log.info("===== FILTRAGE CONSOMMATION =====");
        log.info("Paramètres reçus - period: {}, startDate: {}, endDate: {}, search: {}", period, startDate, endDate, search);
        log.info("Date actuelle (LocalDate.now()): {}", LocalDate.now());

        // Déterminer la période
        if (startDate == null || endDate == null) {
            if ("month".equals(period)) {
                // Mois actuel
                startDate = LocalDate.now().withDayOfMonth(1);
                endDate = LocalDate.now().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
                log.info("Période MOIS calculée - startDate: {}, endDate: {}", startDate, endDate);
            } else {
                // Semaine actuelle par défaut
                startDate = LocalDate.now().with(WeekFields.ISO.dayOfWeek(), 1);
                endDate = startDate.plusDays(6);
                log.info("Période SEMAINE calculée - startDate: {}, endDate: {}", startDate, endDate);
            }
        } else {
            log.info("Période PERSONNALISÉE - startDate: {}, endDate: {}", startDate, endDate);
        }

        // Valeurs par défaut pour le tri
        if (sortBy == null) sortBy = "totalCost";
        if (sortOrder == null) sortOrder = "desc";

        var consumption = consumptionService.getProductConsumption(startDate, endDate, search, sortBy, sortOrder);

        log.info("Nombre de produits trouvés: {}", consumption.size());
        log.info("=================================");

        // Grouper par catégorie pour l'affichage
        var consumptionByCategory = consumption.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                item -> item.getCategory() != null ? item.getCategory() : "Non catégorisé",
                java.util.LinkedHashMap::new,
                java.util.stream.Collectors.toList()
            ));

        model.addAttribute("consumption", consumption);
        model.addAttribute("consumptionByCategory", consumptionByCategory);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("period", period != null ? period : "week");
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "consumption/weekly";
    }

    @GetMapping("/top-products")
    public String topProducts(@RequestParam(required = false) LocalDate weekStart,
                             @RequestParam(defaultValue = "10") int limit,
                             Model model) {
        if (weekStart == null) {
            weekStart = LocalDate.now().with(WeekFields.ISO.dayOfWeek(), 1);
        }
        var topProducts = consumptionService.getTopProductsByWeek(weekStart, limit);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("weekStart", weekStart);
        return "consumption/top-products";
    }

    @GetMapping("/trend")
    public String consumptionTrend(@RequestParam String product,
                                  @RequestParam(defaultValue = "12") int weeks,
                                  Model model) {
        var trend = consumptionService.getConsumptionTrend(product, weeks);
        model.addAttribute("trend", trend);
        model.addAttribute("product", product);
        return "consumption/trend";
    }

    @PostMapping("/calculate")
    public String calculateConsumption(@RequestParam LocalDate startDate,
                                      @RequestParam LocalDate endDate) {
        consumptionService.calculateWeeklyConsumption(startDate, endDate);
        return "redirect:/consumption/weekly";
    }
}

