package advertisement.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequestDTO {
    private String sellerLogin;
    private String reviewerLogin;
    private int score;
    private String comment;
}
