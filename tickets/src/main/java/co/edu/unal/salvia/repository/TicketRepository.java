package co.edu.unal.salvia.repository;

import co.edu.unal.salvia.model.Ticket;
import co.edu.unal.salvia.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    // Spring Data JPA crea la consulta SQL automáticamente con solo leer el nombre del método
    List<Ticket> findByPatientId(UUID patientId);
    List<Ticket> findByAssignedTo(UUID assignedTo);
    List<Ticket> findByStatus(TicketStatus status);
}
