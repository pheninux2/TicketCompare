package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.StatisticSnapshot;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticSnapshotRepository extends JpaRepository<StatisticSnapshot, Long> {
    List<StatisticSnapshot> findByCategoryOrderBySnapshotDateDesc(String category);

    @Query("SELECT ss FROM StatisticSnapshot ss WHERE ss.snapshotDate = :date ORDER BY ss.category ASC")
    List<StatisticSnapshot> getSnapshotByDate(@Param("date") LocalDate date);

    StatisticSnapshot findByCategoryAndSnapshotDate(String category, LocalDate date);
}

