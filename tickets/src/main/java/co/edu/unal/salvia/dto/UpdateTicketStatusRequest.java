package co.edu.unal.salvia.dto;

import co.edu.unal.salvia.model.TicketStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTicketStatusRequest {
    private TicketStatus status;
    private UUID assignedTo;
}