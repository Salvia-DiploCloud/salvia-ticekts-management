package co.edu.unal.salvia.dto;

import co.edu.unal.salvia.model.TicketCategory;
import co.edu.unal.salvia.model.TicketPriority;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateTicketRequest {
    private String title;
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private UUID patientId; // Opcional, según el OpenAPI para cuando el Admin crea el ticket
}