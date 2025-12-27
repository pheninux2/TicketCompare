package pheninux.xdev.ticketcompare.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String store;
    private BigDecimal totalAmount;
    private List<ProductDTO> products;
    private String notes;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}


