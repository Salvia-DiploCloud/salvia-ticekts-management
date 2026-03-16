package co.edu.unal.salvia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketKafkaEvent {
    
    private String eventType; 
    private String timestamp; // El nuevo campo general que pidió tu compañero
    private Object payload;   // Lo dejamos como Object para que reciba cualquiera de las 3 plantillas

    // Plantilla 1: Para cuando se CREA el ticket
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatedPayload {
        private String ticketId;
        private UUID patientId;
        private String patientEmail;
        private String title;
        private String description;
        private String createdAt;
    }

    // Plantilla 2: Para cuando se ACTUALIZA el ticket
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatedPayload {
        private String ticketId;
        private UUID patientId;
        private String patientEmail;
        private String title;
        private List<String> updatedFields;
        private String updatedAt;
    }

    // Plantilla 3: Para cuando se CIERRA el ticket
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClosedPayload {
        private String ticketId;
        private UUID patientId;
        private String patientEmail;
        private String closedBy;
        private String closedAt;
    }
}