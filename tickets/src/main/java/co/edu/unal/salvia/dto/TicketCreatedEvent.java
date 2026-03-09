package co.edu.unal.salvia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreatedEvent {
    private UUID ticketId;
    private String title;
    private String status;
    private UUID patientId;
    private LocalDateTime createdAt;
}