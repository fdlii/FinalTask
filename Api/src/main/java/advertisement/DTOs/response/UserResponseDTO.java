package advertisement.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponseDTO {
    private String login;
    private String password;
    private String username;
    private String country;
    private String region;
    private String town;
    private double sellerRating;
    private Set<String> roles;
    private String avatarUrl;
}
