package advertisement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "advertisement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private UserEntity user;

    @Column(nullable = false, columnDefinition = "VARCHAR(64)")
    private String title;

    private String description;

    @Column(name = "preview_link")
    private String previewLink;

    @Column(nullable = false)
    private Instant published;

    private double price;

    private String country;

    private String region;

    @Column(columnDefinition = "VARCHAR(128)")
    private String town;

    @Column(name = "is_paid", nullable = false)
    private boolean paid;

    @Column(name = "is_closed", nullable = false)
    private boolean closed;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advertisement")
    private List<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "advertisement_category",
            joinColumns = @JoinColumn(name = "advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories;

    public void addCategory(CategoryEntity categoryEntity) {
        categories.add(categoryEntity);
    }
}
