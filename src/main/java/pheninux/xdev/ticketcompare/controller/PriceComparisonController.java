package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pheninux.xdev.ticketcompare.service.PriceComparisonService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/compare")
@RequiredArgsConstructor
public class PriceComparisonController {
    private final PriceComparisonService comparisonService;

    @GetMapping
    public String comparisonPage(Model model) {
        return "compare/index";
    }

    @GetMapping("/global")
    public String globalComparisonPage(Model model) {
        Map<String, Object> comparison = comparisonService.getGlobalPriceComparison();
        model.addAttribute("comparison", comparison);
        return "compare/global";
    }

    @GetMapping("/product/{productName}")
    public String productComparisonPage(@PathVariable String productName, Model model) {
        Map<String, Object> comparison = comparisonService.compareProductPricesByStore(productName);
        model.addAttribute("comparison", comparison);
        return "compare/product";
    }

    // API Endpoints
    @GetMapping("/api/product")
    @ResponseBody
    public Map<String, Object> compareProduct(@RequestParam String product) {
        return comparisonService.compareProductPricesByStore(product);
    }

    @GetMapping("/api/products")
    @ResponseBody
    public List<Map<String, Object>> getAllProducts() {
        return comparisonService.getAllProductsForComparison();
    }

    @GetMapping("/api/evolution")
    @ResponseBody
    public Map<String, Object> getPriceEvolution(@RequestParam String product,
                                                  @RequestParam String store) {
        return comparisonService.getPriceEvolution(product, store);
    }

    @GetMapping("/api/global")
    @ResponseBody
    public Map<String, Object> getGlobalComparison() {
        return comparisonService.getGlobalPriceComparison();
    }

    @GetMapping("/api/price-drops")
    @ResponseBody
    public List<Map<String, Object>> getPriceDrops() {
        return comparisonService.detectPriceDrops();
    }
}

