package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.PriceHistory;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByProductNameOrderByPriceDateDesc(String productName);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.productName = :productName AND ph.priceDate BETWEEN :startDate AND :endDate ORDER BY ph.priceDate ASC")
    List<PriceHistory> getPriceHistoryByRange(
        @Param("productName") String productName,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<PriceHistory> findByProductNameAndStore(String productName, String store);
}

