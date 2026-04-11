package advertisement.DTOs.request;

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
    private UserRequestDTO user;
    private String title;
    private String description;
    private double price;
    private String country;
    private String region;
    private String town;
    private List<CategoryRequestDTO> categories;
}
