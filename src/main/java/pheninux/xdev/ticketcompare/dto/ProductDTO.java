package pheninux.xdev.ticketcompare.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal quantity;
    private String unit;
    private String description;
    private BigDecimal totalPrice;
}

