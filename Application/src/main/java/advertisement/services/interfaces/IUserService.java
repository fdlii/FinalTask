package advertisement.services.interfaces;

import advertisement.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService {
    User registerUser(User user, MultipartFile multipartFile) throws IOException, IllegalAccessException;
    String verifyUser(String login, String password);
    void changePassword(String login, String password);
    User editProfile(User user, MultipartFile avatar) throws IOException;
}
