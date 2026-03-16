package co.edu.unal.salvia.service;

import co.edu.unal.salvia.dto.CreateTicketRequest;
import co.edu.unal.salvia.dto.TicketCreatedEvent;
import co.edu.unal.salvia.dto.TicketKafkaEvent;
import co.edu.unal.salvia.dto.TicketResponse;
import co.edu.unal.salvia.dto.UpdateTicketRequest;
import co.edu.unal.salvia.model.HistoryAction;
import co.edu.unal.salvia.model.HistoryEntry;
import co.edu.unal.salvia.model.Ticket;
import co.edu.unal.salvia.model.TicketStatus;
import co.edu.unal.salvia.producer.TicketEventProducer;
import co.edu.unal.salvia.repository.TicketRepository;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import co.edu.unal.salvia.dto.UpdateTicketStatusRequest;

import co.edu.unal.salvia.model.TicketComment;
import co.edu.unal.salvia.model.TicketHistory;
import co.edu.unal.salvia.repository.TicketCommentRepository;
import co.edu.unal.salvia.repository.TicketHistoryRepository;
import co.edu.unal.salvia.dto.CreateCommentRequest;
import co.edu.unal.salvia.dto.AssignTicketRequest;
import co.edu.unal.salvia.dto.CommentResponse;

import co.edu.unal.salvia.model.HistoryAction;
import co.edu.unal.salvia.dto.HistoryEntryDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketHistoryRepository historyRepository;
    private final TicketRepository ticketRepository;
    private final TicketEventProducer ticketEventProducer;
    private final TicketCommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository, 
                         TicketEventProducer ticketEventProducer,
                         TicketCommentRepository commentRepository,
                         TicketHistoryRepository historyRepository) { // <-- Agregado
        this.ticketRepository = ticketRepository;
        this.ticketEventProducer = ticketEventProducer;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository; // <-- Agregado
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

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketCreatedEvent event = new TicketCreatedEvent(
                savedTicket.getId(),
                savedTicket.getTitle(),
                savedTicket.getStatus().name(),
                savedTicket.getPatientId(),
                savedTicket.getCreatedAt()
        );
        dispatchTicketCreatedEvent(savedTicket);

        // Le pasamos savedTicket.getId() y el Enum HistoryAction.CREATED
        recordHistory(savedTicket.getId(), HistoryAction.CREATED, null, null, savedTicket.getStatus().name(), savedTicket.getCreatedBy());
        // ¡Usamos el nuevo método auxiliar!
        return mapToResponse(savedTicket);
    }

    // NUEVO: Obtener todos los tickets con filtros opcionales
    public List<TicketResponse> getAllTickets(UUID patientId, UUID assignedTo, TicketStatus status) {
        List<Ticket> tickets;
        
        // Lógica simple para usar los métodos que ya creamos en tu TicketRepository
        if (patientId != null) {
            tickets = ticketRepository.findByPatientId(patientId);
        } else if (assignedTo != null) {
            tickets = ticketRepository.findByAssignedTo(assignedTo);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else {
            tickets = ticketRepository.findAll();
        }

        // Convertimos la lista de entidades a lista de DTOs
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // NUEVO: Obtener un ticket por ID
    public TicketResponse getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));
        return mapToResponse(ticket);
    }

    // NUEVO: Método auxiliar para no repetir código
    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setCategory(ticket.getCategory().name());
        response.setPriority(ticket.getPriority().name());
        response.setStatus(ticket.getStatus());
        response.setCreatedBy(ticket.getCreatedBy());
        response.setCreatedAt(ticket.getCreatedAt());

        // AGREGAMOS ESTAS 4 LÍNEAS:
        response.setAssignedTo(ticket.getAssignedTo());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        response.setClosedAt(ticket.getClosedAt());
        return response;
    }

    // NUEVO: Método para actualizar el estado de un ticket
    public TicketResponse updateTicket(UUID ticketId, UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        List<String> updatedFields = new java.util.ArrayList<>();

        if (request.getTitle() != null && !request.getTitle().equals(ticket.getTitle())) {
            recordHistory(ticket.getId(), co.edu.unal.salvia.model.HistoryAction.UPDATED, "title", ticket.getTitle(), request.getTitle(), UUID.randomUUID());
            ticket.setTitle(request.getTitle());
            updatedFields.add("title");
        }
        if (request.getDescription() != null && !request.getDescription().equals(ticket.getDescription())) {
            recordHistory(ticket.getId(), co.edu.unal.salvia.model.HistoryAction.UPDATED, "description", ticket.getDescription(), request.getDescription(), UUID.randomUUID());
            ticket.setDescription(request.getDescription());
            updatedFields.add("description");
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Si se actualizó al menos un campo, disparamos el evento
        if (!updatedFields.isEmpty()) {
            dispatchTicketUpdatedEvent(updatedTicket, updatedFields);
        }

        return mapToResponse(updatedTicket);
    }

    // MÉTODO RESTAURADO: Actualizar el estado de un ticket
    public TicketResponse updateTicketStatus(UUID id, co.edu.unal.salvia.dto.UpdateTicketStatusRequest request) {
        // 1. Buscamos el ticket en la BD
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        // 2. Actualizamos el estado si viene en la petición
        if (request.getStatus() != null) {
            // Guardar el historial antes de cambiar el estado
            recordHistory(ticket.getId(), HistoryAction.STATUS_CHANGED, "status", ticket.getStatus().name(), request.getStatus().name(), UUID.randomUUID());
            ticket.setStatus(request.getStatus());
            
            // Lógica de negocio: Guardar la fecha exacta en la que se resuelve o cierra
            if (request.getStatus() == co.edu.unal.salvia.model.TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            } else if (request.getStatus() == co.edu.unal.salvia.model.TicketStatus.CLOSED) {
                ticket.setClosedAt(LocalDateTime.now());
            }
        }

        // 3. Asignamos el ticket a un trabajador si viene en la petición
        if (request.getAssignedTo() != null) {
            ticket.setAssignedTo(request.getAssignedTo());
        }

        // 4. Actualizamos la fecha de modificación general
        ticket.setUpdatedAt(LocalDateTime.now());

        // 5. Guardamos en BD
        Ticket updatedTicket = ticketRepository.save(ticket);

        // 6. ¡Lógica de Kafka actualizada con Cognito!
        if (request.getStatus() == co.edu.unal.salvia.model.TicketStatus.CLOSED) {
            dispatchTicketClosedEvent(updatedTicket, getCurrentUserEmail());
        } else {
            dispatchTicketUpdatedEvent(updatedTicket, List.of("status", "assignedTo"));
        }
        
        return mapToResponse(updatedTicket);
    }

    // NUEVO: Método para agregar un comentario
    public CommentResponse addComment(UUID ticketId, CreateCommentRequest request) {
        // 1. Verificamos que el ticket exista
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        // 2. Construimos la entidad del comentario
        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setBody(request.getBody());
        comment.setAuthorId(UUID.randomUUID()); // Simula el ID del usuario logueado
        comment.setCreatedAt(LocalDateTime.now());

        // 3. Guardamos en base de datos
        TicketComment savedComment = commentRepository.save(comment);

        // 4. Mapeamos la respuesta
        CommentResponse response = new CommentResponse();
        response.setId(savedComment.getId());
        response.setTicketId(ticket.getId());
        response.setBody(savedComment.getBody());
        response.setCreatedAt(savedComment.getCreatedAt());

        CommentResponse.UserRef authorRef = new CommentResponse.UserRef();
        authorRef.setId(savedComment.getAuthorId());
        response.setAuthor(authorRef);

        return response;
    }


    // NUEVO: Método para listar los comentarios de un ticket
    public List<CommentResponse> getTicketComments(UUID ticketId) {
        // 1. Verificamos que el ticket exista
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado");
        }

        // 2. Buscamos los comentarios usando el método que creamos en el repositorio
        List<TicketComment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);

        // 3. Convertimos la lista de entidades a lista de DTOs
        return comments.stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setTicketId(ticketId);
            response.setBody(comment.getBody());
            response.setCreatedAt(comment.getCreatedAt());

            CommentResponse.UserRef authorRef = new CommentResponse.UserRef();
            authorRef.setId(comment.getAuthorId());
            response.setAuthor(authorRef);
            
            return response;
        }).collect(Collectors.toList());
    }


    private void recordHistory(UUID ticketId, HistoryAction action, String field, String oldValue, String newValue, UUID performedBy) {
        // AQUÍ ESTABA EL DETALLE: Instanciar tu verdadero modelo HistoryEntry
        co.edu.unal.salvia.model.HistoryEntry history = new co.edu.unal.salvia.model.HistoryEntry();
        
        history.setTicketId(ticketId);
        history.setAction(action);
        history.setField(field);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setPerformedBy(performedBy);
        // Recuerda que tu performedAt se pone solo gracias a tu modelo

        historyRepository.save(history); // ¡Ahora sí encajan como piezas de Tetris!
    }


    // Método para obtener el historial devolviendo el nuevo DTO
    public List<HistoryEntryDto> getTicketHistory(UUID ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado");
        }

        return historyRepository.findByTicketIdOrderByPerformedAtDesc(ticketId).stream().map(h -> {
            HistoryEntryDto dto = new HistoryEntryDto();
            dto.setId(h.getId());
            dto.setTicketId(h.getTicketId());
            dto.setAction(h.getAction().name()); // Convertimos el Enum a String
            dto.setField(h.getField());
            dto.setOldValue(h.getOldValue());
            dto.setNewValue(h.getNewValue());
            dto.setReason(h.getReason());
            dto.setPerformedAt(h.getPerformedAt());

            CommentResponse.UserRef userRef = new CommentResponse.UserRef();
            userRef.setId(h.getPerformedBy());
            dto.setPerformedBy(userRef);

            return dto;
        }).collect(Collectors.toList());
    }

// --- MAGIA DE COGNITO ---
    // Este método saca el correo directamente del Token JWT que envió el usuario
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email"); // Extraemos el claim 'email' del JSON del token
            if (email != null && !email.isBlank()) return email;
        }
        return "usuario@pendiente.com"; // Fallback por si acaso
    }

    // Formateador de fecha exacto como lo pidió Python (Ej: 2026-03-15T12:00:00Z)
    private String getFormattedTimestamp(LocalDateTime date) {
        if (date == null) date = LocalDateTime.now();
        return date.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    // --- LOS 3 NUEVOS MÉTODOS DE ENVÍO ---

    private void dispatchTicketCreatedEvent(Ticket ticket) {
        String timestamp = getFormattedTimestamp(ticket.getCreatedAt());
        
        TicketKafkaEvent.CreatedPayload payload = new TicketKafkaEvent.CreatedPayload(
                ticket.getId().toString(),
                ticket.getPatientId(),
                getCurrentUserEmail(),
                ticket.getTitle(),
                ticket.getDescription(),
                timestamp
        );
        TicketKafkaEvent event = new TicketKafkaEvent("TicketCreated", timestamp, payload);
        ticketEventProducer.publishEvent(event);
    }

    private void dispatchTicketUpdatedEvent(Ticket ticket, List<String> updatedFields) {
        String timestamp = getFormattedTimestamp(ticket.getUpdatedAt());
        
        TicketKafkaEvent.UpdatedPayload payload = new TicketKafkaEvent.UpdatedPayload(
                ticket.getId().toString(),
                ticket.getPatientId(),
                getCurrentUserEmail(),
                ticket.getTitle(),
                updatedFields,
                timestamp
        );
        TicketKafkaEvent event = new TicketKafkaEvent("TicketUpdated", timestamp, payload);
        ticketEventProducer.publishEvent(event);
    }

    private void dispatchTicketClosedEvent(Ticket ticket, String closedBy) {
        String timestamp = getFormattedTimestamp(ticket.getClosedAt());
        
        TicketKafkaEvent.ClosedPayload payload = new TicketKafkaEvent.ClosedPayload(
                ticket.getId().toString(),
                ticket.getPatientId(),
                getCurrentUserEmail(),
                closedBy,
                timestamp
        );
        TicketKafkaEvent event = new TicketKafkaEvent("TicketClosed", timestamp, payload);
        ticketEventProducer.publishEvent(event);
    }


    // MÉTODO 2: Asignar el ticket a un médico o admin (PATCH /assign)
    public TicketResponse assignTicket(UUID ticketId, AssignTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        String oldAssignedTo = ticket.getAssignedTo() != null ? ticket.getAssignedTo().toString() : "UNASSIGNED";
        ticket.setAssignedTo(request.getAssignedTo());
        
        recordHistory(ticket.getId(), co.edu.unal.salvia.model.HistoryAction.ASSIGNED, "assignedTo", oldAssignedTo, request.getAssignedTo().toString(), UUID.randomUUID());

        // Regla de negocio del OpenAPI: Si está OPEN, pasa automáticamente a IN_PROGRESS
        if (ticket.getStatus() == co.edu.unal.salvia.model.TicketStatus.OPEN) {
            recordHistory(ticket.getId(), co.edu.unal.salvia.model.HistoryAction.STATUS_CHANGED, "status", ticket.getStatus().name(), co.edu.unal.salvia.model.TicketStatus.IN_PROGRESS.name(), UUID.randomUUID());
            ticket.setStatus(co.edu.unal.salvia.model.TicketStatus.IN_PROGRESS);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Le avisamos a Python que hubo un cambio importante
        dispatchTicketUpdatedEvent(updatedTicket, List.of("assignedTo", "status"));

        return mapToResponse(updatedTicket);
    }

    
}