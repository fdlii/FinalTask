package advertisement;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
@Data
public class AdminConfig {
    @Value("${secret.admin.key}")
    private String apiKey;
}
