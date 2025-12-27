package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.ConsumptionStatistic;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsumptionStatisticRepository extends JpaRepository<ConsumptionStatistic, Long> {
    List<ConsumptionStatistic> findByProductNameOrderByWeekStartDesc(String productName);

    @Query("SELECT cs FROM ConsumptionStatistic cs WHERE cs.weekStart BETWEEN :startDate AND :endDate ORDER BY cs.weekStart DESC")
    List<ConsumptionStatistic> getConsumptionByWeekRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT cs FROM ConsumptionStatistic cs WHERE cs.weekStart = :weekStart ORDER BY cs.totalQuantity DESC")
    List<ConsumptionStatistic> getTopProductsByWeek(@Param("weekStart") LocalDate weekStart);
}

