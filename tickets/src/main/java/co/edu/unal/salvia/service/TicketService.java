package co.edu.unal.salvia.service;

import co.edu.unal.salvia.dto.CreateTicketRequest;
import co.edu.unal.salvia.dto.TicketCreatedEvent;
import co.edu.unal.salvia.dto.TicketResponse;
import co.edu.unal.salvia.model.Ticket;
import co.edu.unal.salvia.model.TicketStatus;
import co.edu.unal.salvia.producer.TicketEventProducer;
import co.edu.unal.salvia.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketEventProducer ticketEventProducer; // <-- Agregamos el productor

    // Actualizamos el constructor
    public TicketService(TicketRepository ticketRepository, TicketEventProducer ticketEventProducer) {
        this.ticketRepository = ticketRepository;
        this.ticketEventProducer = ticketEventProducer;
    }

    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN); 
        
        ticket.setCreatedBy(UUID.randomUUID()); 
        
        if (request.getPatientId() != null) {
            ticket.setPatientId(request.getPatientId());
        } else {
            ticket.setPatientId(ticket.getCreatedBy()); 
        }

        ticket.setCreatedAt(LocalDateTime.now());

        // 1. Guardar en PostgreSQL
        Ticket savedTicket = ticketRepository.save(ticket);

        // 2. Construir el evento para Kafka
        TicketCreatedEvent event = new TicketCreatedEvent(
                savedTicket.getId(),
                savedTicket.getTitle(),
                savedTicket.getStatus().name(),
                savedTicket.getPatientId(),
                savedTicket.getCreatedAt()
        );

        // 3. ¡Publicar el evento!
        ticketEventProducer.publishTicketCreated(event);

        // 4. Mapear la respuesta para el usuario
        TicketResponse response = new TicketResponse();
        response.setId(savedTicket.getId());
        response.setTitle(savedTicket.getTitle());
        response.setDescription(savedTicket.getDescription());
        response.setCategory(savedTicket.getCategory());
        response.setPriority(savedTicket.getPriority());
        response.setStatus(savedTicket.getStatus());
        response.setCreatedBy(savedTicket.getCreatedBy());
        response.setCreatedAt(savedTicket.getCreatedAt());

        return response;
    }
}