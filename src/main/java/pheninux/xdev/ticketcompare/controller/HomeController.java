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
        // Add recent tickets to homepage
        model.addAttribute("recentTickets", ticketService.getAllTickets(0, 5));
        return "index";
    }
}

