package co.edu.unal.salvia.producer;

import co.edu.unal.salvia.dto.TicketKafkaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TicketEventProducer {

    private static final String TOPIC = "ticket-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TicketEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Un único método maestro para enviar cualquier evento de tickets
    public void publishEvent(TicketKafkaEvent event) {
        String key = "default-key";
        Object payload = event.getPayload();

        // Verificamos cuál de las 3 plantillas es para extraer el ID correctamente
        if (payload instanceof TicketKafkaEvent.CreatedPayload p) {
            key = p.getTicketId();
        } else if (payload instanceof TicketKafkaEvent.UpdatedPayload p) {
            key = p.getTicketId();
        } else if (payload instanceof TicketKafkaEvent.ClosedPayload p) {
            key = p.getTicketId();
        }

        kafkaTemplate.send(TOPIC, key, event);
        
        System.out.println("Enviando a Python 🐍 -> [" + event.getEventType() + "] Ticket ID: " + key);
    }
}