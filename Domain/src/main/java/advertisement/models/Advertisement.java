package advertisement.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Advertisement {
    private Long adNumber;
    private User user;
    private String title;
    private String description;
    private String previewLink;
    private LocalDateTime published;
    private double price;
    private String country;
    private String region;
    private String town;
    private boolean paid;
    private boolean closed;
    private List<Comment> comments;
    private List<Category> categories;
}
