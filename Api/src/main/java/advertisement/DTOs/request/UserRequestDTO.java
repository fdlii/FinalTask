package advertisement.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserRequestDTO {
    private String login;
    private String password;
    private String username;
    private String country;
    private String region;
    private String town;
    private Set<String> roles;
    private String secretAdminKey;
}
