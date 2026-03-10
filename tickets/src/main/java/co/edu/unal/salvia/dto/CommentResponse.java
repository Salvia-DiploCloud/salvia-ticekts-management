package co.edu.unal.salvia.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CommentResponse {
    private UUID id;
    private UUID ticketId;
    private String body;
    private UserRef author;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Data
    public static class UserRef {
        private UUID id;
        // Más adelante puedes agregar firstName, lastName, role, etc.
    }
}