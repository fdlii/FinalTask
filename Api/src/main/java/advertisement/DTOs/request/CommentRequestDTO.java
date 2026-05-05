package advertisement.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Логин комментатора невалиден.")
    private String senderLogin;

    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(max = 500, message = "Комментарий слишком длинный.")
    private String content;
}