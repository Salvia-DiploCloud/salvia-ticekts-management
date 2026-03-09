package co.edu.unal.salvia.producer;

import co.edu.unal.salvia.dto.TicketCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TicketEventProducer {

    // El topic es como el "canal" o "buzón" donde dejaremos el mensaje
    private static final String TOPIC = "ticket-events";

    // KafkaTemplate es la herramienta que nos da Spring para enviar mensajes
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TicketEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTicketCreated(TicketCreatedEvent event) {
        // Enviamos el evento al topic. Usamos el ID del ticket como "Key" (llave)
        kafkaTemplate.send(TOPIC, event.getTicketId().toString(), event);
        System.out.println("Evento enviado a Kafka: Ticket creado con ID " + event.getTicketId());
    }
}