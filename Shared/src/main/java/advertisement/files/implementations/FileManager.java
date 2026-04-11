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

        String filename = UUID.randomUUID() + getExtensionFromContentType(multipartFile.getContentType());

        Path targetPath = avatarsDir.resolve(filename);

        multipartFile.transferTo(targetPath);
        return "static/images/avatars/" + filename;
    }

    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }

        return switch (contentType.toLowerCase()) {
            case "image/png"               -> ".png";
            case "image/webp"              -> ".webp";
            case "image/gif"               -> ".gif";
            case "image/svg+xml"           -> ".svg";
            default                        -> ".jpg";
        };
    }

    @Override
    public void deleteOldAvatar(String oldAvatarPath) throws IOException {
        if (oldAvatarPath == null || oldAvatarPath.isBlank()) {
            return;
        }

        String filename = oldAvatarPath.substring(oldAvatarPath.lastIndexOf('/') + 1);
        Path oldFilePath = avatarsDir.resolve(filename);

        if (Files.exists(oldFilePath)) {
            Files.delete(oldFilePath);
        }
    }
}
