package advertisement.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdvertisementResponseDTO {
    private Long adNumber;
    private UserResponseDTO user;
    private String title;
    private String description;
    private String previewLink;
    private LocalDateTime published;
    private double price;
    private String country;
    private String region;
    private String town;
    private boolean closed;
    private List<CommentResponseDTO> comments;
    private List<CategoryResponseDTO> categories;
}