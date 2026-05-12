package advertisement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "advertisement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertisementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private UserEntity user;

    @Column(length = 128)
    private String title;

    private String description;

    @Column(name = "preview_link", length = 256)
    private String previewLink;

    private Instant published;

    private double price;

    @Column(length = 64)
    private String country;

    @Column(length = 64)
    private String region;

    @Column(length = 64)
    private String town;

    @Column(name = "is_paid")
    private boolean paid;

    @Column(name = "is_closed")
    private boolean closed;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "advertisement")
    private List<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.LAZY)
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
