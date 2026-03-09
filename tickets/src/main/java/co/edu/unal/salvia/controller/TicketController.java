package co.edu.unal.salvia.controller;

import co.edu.unal.salvia.dto.CreateTicketRequest;
import co.edu.unal.salvia.dto.TicketResponse;
import co.edu.unal.salvia.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tickets") // Alineado con el 'servers' de tu OpenAPI
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
}