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
public class MessageRequestDTO {
    @NotBlank(message = "Логин отправителя невалиден.")
    private String senderLogin;

    @NotBlank(message = "Логин получателя невалиден.")
    private String recieverLogin;

    @NotBlank(message = "Сообщение не может быть пустым.")
    @Size(max = 1000, message = "Сообщение слишком длинное.")
    private String content;
}
