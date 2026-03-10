package co.edu.unal.salvia.repository;

import co.edu.unal.salvia.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, UUID> {
    // Este método nos servirá más adelante para el GET /comments
    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}