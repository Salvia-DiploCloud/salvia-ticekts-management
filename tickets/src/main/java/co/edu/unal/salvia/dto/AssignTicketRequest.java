package co.edu.unal.salvia.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AssignTicketRequest {
    private UUID assignedTo;
}