package advertisement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    private String country;

    private String region;

    private String town;

    @Column(name = "avatar_link")
    private String avatarLink;

    @Column(name = "seller_rating", nullable = false)
    private double sellerRating;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "seller")
    private List<RatingEntity> ratings;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    private List<MessageEntity> messages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles;
}
