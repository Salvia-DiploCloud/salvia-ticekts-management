package co.edu.unal.salvia.repository;

import co.edu.unal.salvia.model.HistoryEntry; // <-- Cambiamos la importación
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketHistoryRepository extends JpaRepository<HistoryEntry, UUID> { // <-- Cambiamos el tipo
    // Cambiamos el tipo de retorno
    List<HistoryEntry> findByTicketIdOrderByPerformedAtDesc(UUID ticketId);
}