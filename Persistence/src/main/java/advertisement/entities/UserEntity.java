package advertisement.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 64)
    private String login;

    @Column(length = 64)
    private String password;

    @Column(length = 128)
    private String username;

    @Column(length = 64)
    private String country;

    @Column(length = 64)
    private String region;

    @Column(length = 64)
    private String town;

    @Column(name = "avatar_link", length = 256)
    private String avatarLink;

    @Column(name = "seller_rating")
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
