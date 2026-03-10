package co.edu.unal.salvia.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relación de base de datos: Muchos comentarios pertenecen a un Ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Según tu OpenAPI: maxLength es 2000
    @Column(nullable = false, length = 2000)
    private String body;

    // Guardamos el ID del autor (Paciente, Doctor o Admin)
    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}