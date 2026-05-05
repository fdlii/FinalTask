package advertisement.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryRequestDTO {
    @NotBlank(message = "Категория не может быть пустой.")
    private String name;
}
