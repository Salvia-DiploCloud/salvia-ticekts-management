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
        // Usamos el ticketId que viene dentro del payload como llave (key) para Kafka
        String key = event.getPayload().getTicketId().toString();
        
        kafkaTemplate.send(TOPIC, key, event);
        
        System.out.println("Enviando a Python 🐍 -> [" + event.getEventType() + "] Ticket ID: " + key);
    }
}