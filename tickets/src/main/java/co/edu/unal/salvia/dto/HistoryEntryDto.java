package co.edu.unal.salvia.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class HistoryEntryDto {
    private UUID id;
    private UUID ticketId;
    private String action; // Aquí lo mandamos como String para el JSON
    private String field;
    private String oldValue;
    private String newValue;
    private String reason;
    private CommentResponse.UserRef performedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime performedAt;
}