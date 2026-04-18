package advertisement.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    private User seller;
    private User reviewer;
    private int score;
    private String comment;
    private LocalDateTime writtenAt;
}
