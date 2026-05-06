package advertisement.DTOs.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequestDTO {
    @NotBlank(message = "Логин продацвца невалиден.")
    private String sellerLogin;

    @NotBlank(message = "Логин рецензента невалиден.")
    private String reviewerLogin;

    @Min(value = 1)
    @Max(value = 5)
    private int score;

    private String comment;
}
