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
public class Comment {
    private Advertisement advertisement;
    private User user;
    private LocalDateTime sentAt;
    private String content;
}
