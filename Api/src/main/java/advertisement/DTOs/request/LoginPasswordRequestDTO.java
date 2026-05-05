package advertisement.DTOs.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginPasswordRequestDTO {
    @NotBlank(message = "Логин не может быть пустым.")
    @Email(message = "Некорректный формат логина.")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 8, message = "Пароль не может содержать меньше 8 символов.")
    private String password;
}
