package pheninux.xdev.ticketcompare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pheninux.xdev.ticketcompare.service.TicketService;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final TicketService ticketService;

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Add dashboard statistics
        model.addAttribute("recentTickets", ticketService.getAllTickets(0, 5));
        return "dashboard";
    }
}

