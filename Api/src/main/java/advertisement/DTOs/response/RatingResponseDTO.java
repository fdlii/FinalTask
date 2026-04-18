package advertisement.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDTO {
    private UserResponseDTO seller;
    private UserResponseDTO reviewer;
    private int score;
    private String comment;
    private LocalDateTime writtenAt;
}
