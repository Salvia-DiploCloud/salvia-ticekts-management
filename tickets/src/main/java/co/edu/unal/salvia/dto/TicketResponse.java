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
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    private UUID createdBy;
    private LocalDateTime createdAt;
}