package advertisement.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdvertisementRequestDTO {
    private Long adNumber;

    @NotNull(message = "У объявления должен быть владелец.")
    private UserRequestDTO user;

    @NotBlank(message = "У объявления должно быть название.")
    private String title;
    private String description;

    @Min(value = 0, message = "Цена товара/услуги не может быть отрицательной.")
    private double price;

    private String country;
    private String region;
    private String town;

    @NotNull(message = "Категории не могут быть null.")
    private List<CategoryRequestDTO> categories;
}
