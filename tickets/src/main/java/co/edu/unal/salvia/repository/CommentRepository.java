package co.edu.unal.salvia.repository;

import co.edu.unal.salvia.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}