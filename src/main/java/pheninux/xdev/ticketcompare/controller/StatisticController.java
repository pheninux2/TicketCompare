package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pheninux.xdev.ticketcompare.service.StatisticService;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    /**
     * Dashboard des statistiques (accessible via /statistics/dashboard)
     * Note: Le dashboard principal utilisateur est dans DashboardController (/dashboard)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("categories", statisticService.getAllCategoriesStatistics());
        return "statistics/dashboard";
    }

    @GetMapping("/category/{category}")
    public String categoryStatistics(@PathVariable String category, Model model) {
        var stats = statisticService.getCategoryStatistics(category);
        var products = statisticService.getCategoryProducts(category);
        model.addAttribute("statistic", stats);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "statistics/category";
    }

    @GetMapping("/price-comparison")
    public String priceComparison(Model model) {
        return "statistics/price-comparison";
    }

    @GetMapping("/api/price-comparison")
    @ResponseBody
    public java.util.Map<String, Object> getPriceComparison(@RequestParam String product) {
        return statisticService.getPriceComparison(product);
    }

    @GetMapping("/expensive")
    public String mostExpensive(@RequestParam(defaultValue = "10") int limit, Model model) {
        model.addAttribute("products", statisticService.getMostExpensiveProducts(limit));
        return "statistics/expensive";
    }

    @GetMapping("/cheap")
    public String cheapest(@RequestParam(defaultValue = "10") int limit, Model model) {
        model.addAttribute("products", statisticService.getCheapestProducts(limit));
        return "statistics/cheap";
    }

    @GetMapping("/trends")
    public String trends(Model model) {
        return "statistics/trends";
    }

    @GetMapping("/api/trends")
    @ResponseBody
    public java.util.Map<String, Object> getTrends(@RequestParam(defaultValue = "30") int days) {
        return statisticService.getPriceTrends(days);
    }
}

