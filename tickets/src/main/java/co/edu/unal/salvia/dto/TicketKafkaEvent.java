package co.edu.unal.salvia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketKafkaEvent {
    
    // Aquí enviaremos "TicketCreated", "TicketUpdated" o "TicketClosed"
    private String eventType; 
    
    // Aquí va la información del ticket que el Python necesita leer
    private TicketPayload payload;

    // Clase estática para estructurar el objeto "payload" en el JSON
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketPayload {
        private UUID ticketId;
        private UUID patientId;
        private String title;
        // Agregamos el status por si tu compañero lo necesita en el futuro
        private String status; 
    }
}