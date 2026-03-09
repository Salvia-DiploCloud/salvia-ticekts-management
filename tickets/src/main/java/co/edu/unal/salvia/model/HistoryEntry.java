package co.edu.unal.salvia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "history_entries")
@Data
public class HistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryAction action;

    private String field;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    private String reason;

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt = LocalDateTime.now();
}