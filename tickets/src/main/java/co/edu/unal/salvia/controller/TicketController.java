package co.edu.unal.salvia.controller;

import co.edu.unal.salvia.dto.CreateTicketRequest;
import co.edu.unal.salvia.dto.HistoryEntryDto;
import co.edu.unal.salvia.dto.TicketResponse;
import co.edu.unal.salvia.dto.UpdateTicketRequest;
import co.edu.unal.salvia.model.HistoryEntry;
import co.edu.unal.salvia.model.TicketStatus;
import co.edu.unal.salvia.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.unal.salvia.dto.UpdateTicketStatusRequest;
import co.edu.unal.salvia.dto.CreateCommentRequest;
import co.edu.unal.salvia.dto.AssignTicketRequest;
import co.edu.unal.salvia.dto.CommentResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // NUEVO: Endpoint para listar tickets (Soporta query parameters como ?status=OPEN)
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(required = false) TicketStatus status) {
        
        List<TicketResponse> tickets = ticketService.getAllTickets(patientId, assignedTo, status);
        return ResponseEntity.ok(tickets);
    }

    // NUEVO: Endpoint para ver el detalle de un ticket específico
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable UUID id) {
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(response);
    }

    // NUEVO: Endpoint para actualizar estado y asignación (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable UUID id,
            @RequestBody co.edu.unal.salvia.dto.UpdateTicketStatusRequest request) {
        
        TicketResponse response = ticketService.updateTicketStatus(id, request);
        return ResponseEntity.ok(response);
    }

    // NUEVO: Endpoint para agregar comentarios
    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID ticketId,
            @RequestBody CreateCommentRequest request) {
        
        CommentResponse response = ticketService.addComment(ticketId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // NUEVO: Endpoint para listar comentarios
    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getTicketComments(@PathVariable UUID ticketId) {
        List<CommentResponse> comments = ticketService.getTicketComments(ticketId);
        return ResponseEntity.ok(comments);
    }


    @GetMapping("/{ticketId}/history")
    public ResponseEntity<List<HistoryEntryDto>> getTicketHistory(@PathVariable UUID ticketId) {
        List<HistoryEntryDto> history = ticketService.getTicketHistory(ticketId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable UUID ticketId,
            @RequestBody UpdateTicketRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, request));
    }

    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable UUID ticketId,
            @RequestBody AssignTicketRequest request) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, request));
    }


}