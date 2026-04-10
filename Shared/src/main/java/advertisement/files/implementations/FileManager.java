package advertisement.files.implementations;

import advertisement.files.interfaces.IFileManager;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Repository
public class FileManager implements IFileManager {
    private final Path avatarsDir = Paths.get("static","images", "avatars").toAbsolutePath().normalize();

    @Override
    public String saveAvatar(MultipartFile multipartFile) throws IOException {
        if (!Files.exists(avatarsDir)) {
            Files.createDirectories(avatarsDir);
        }

        String filename = UUID.randomUUID().toString();

        Path targetPath = avatarsDir.resolve(filename);

        multipartFile.transferTo(targetPath);
        return "static/images/avatars" + filename;
    }
}
