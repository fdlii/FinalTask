package advertisement.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Message {
    private User sender;
    private User reciever;
    private LocalDateTime sentAt;
    private String content;
}