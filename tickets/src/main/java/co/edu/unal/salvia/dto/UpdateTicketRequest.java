package co.edu.unal.salvia.dto;

import lombok.Data;

@Data
public class UpdateTicketRequest {
    private String title;
    private String description;
    private String category;
    private String priority;
}