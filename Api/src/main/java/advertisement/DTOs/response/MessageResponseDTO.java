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
public class MessageResponseDTO {
    private String senderLogin;
    private String recieverLogin;
    private LocalDateTime sentAt;
    private String content;
}
