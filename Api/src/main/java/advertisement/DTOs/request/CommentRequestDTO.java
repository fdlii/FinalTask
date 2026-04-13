package advertisement.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentRequestDTO {
    private Long adNumber;
    private String senderLogin;
    private String content;
}
