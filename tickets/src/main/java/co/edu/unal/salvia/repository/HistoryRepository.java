package co.edu.unal.salvia.repository;

import co.edu.unal.salvia.model.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntry, UUID> {
    List<HistoryEntry> findByTicketIdOrderByPerformedAtDesc(UUID ticketId);
}
