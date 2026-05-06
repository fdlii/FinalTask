package advertisement.DTOs.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdvertisementManageRequestDTO {
    private Long adNumber;

    @NotNull(message = "У объявления должен быть владелец.")
    private UserRequestDTO user;
}
