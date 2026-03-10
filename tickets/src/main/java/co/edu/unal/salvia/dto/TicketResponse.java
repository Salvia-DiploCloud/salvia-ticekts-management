package co.edu.unal.salvia.dto;

import co.edu.unal.salvia.model.TicketCategory;
import co.edu.unal.salvia.model.TicketPriority;
import co.edu.unal.salvia.model.TicketStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponse {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private TicketStatus status;
    private UUID patientId;
    private UUID createdBy;
    private UUID assignedTo;         // NUEVO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // NUEVO
    private LocalDateTime resolvedAt;// NUEVO
    private LocalDateTime closedAt;  // NUEVO
}