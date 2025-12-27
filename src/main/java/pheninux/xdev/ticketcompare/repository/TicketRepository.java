package pheninux.xdev.ticketcompare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pheninux.xdev.ticketcompare.entity.Ticket;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Ticket> findByStore(String store, Pageable pageable);

    List<Ticket> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Ticket t WHERE t.date >= :startDate AND t.date <= :endDate ORDER BY t.date DESC")
    List<Ticket> getTicketsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

