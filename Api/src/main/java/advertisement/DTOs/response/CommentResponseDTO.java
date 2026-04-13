package advertisement.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentResponseDTO {
    private UserResponseDTO user;
    private LocalDateTime sentAt;
    private String content;
}
