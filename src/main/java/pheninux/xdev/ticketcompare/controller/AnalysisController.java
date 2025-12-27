package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pheninux.xdev.ticketcompare.service.PriceAnalysisService;
import pheninux.xdev.ticketcompare.service.ShoppingListPredictionService;

@Controller
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final PriceAnalysisService analysisService;
    private final ShoppingListPredictionService shoppingListService;

    @GetMapping("/forecast")
    public String forecastPage(Model model) {
        return "analysis/forecast";
    }

    @GetMapping("/api/forecast")
    @ResponseBody
    public Object forecastPrice(@RequestParam String product,
                               @RequestParam(defaultValue = "30") int days) {
        return analysisService.forecastPrice(product, days);
    }

    @GetMapping("/trend")
    public String trendPage(Model model) {
        return "analysis/trend";
    }

    @GetMapping("/api/trend")
    @ResponseBody
    public java.util.Map<String, Object> getPriceTrend(@RequestParam String product,
                                                        @RequestParam(defaultValue = "6") int months) {
        return analysisService.getPriceTrend(product, months);
    }

    @GetMapping("/consumption-forecast")
    public String consumptionForecastPage(Model model) {
        return "analysis/consumption-forecast";
    }

    @GetMapping("/api/consumption-forecast")
    @ResponseBody
    public java.util.Map<String, Object> forecastConsumption(@RequestParam String product,
                                                              @RequestParam(defaultValue = "30") int days) {
        return analysisService.forecastConsumption(product, days);
    }

    @GetMapping("/smart-shopping-list")
    public String smartShoppingListPage(Model model) {
        return "analysis/smart-shopping-list";
    }

    @GetMapping("/api/smart-shopping-list")
    @ResponseBody
    public java.util.Map<String, Object> generateSmartShoppingList() {
        return shoppingListService.generateSmartShoppingList();
    }

    @GetMapping("/api/consumption-analysis")
    @ResponseBody
    public java.util.Map<String, Object> analyzeConsumption() {
        return shoppingListService.analyzeConsumptionHabits();
    }
}

