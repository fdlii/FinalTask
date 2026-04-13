package advertisement.files.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileManager {
    String saveAvatar(MultipartFile multipartFile) throws IOException;
    void deleteOldAvatar(String oldAvatarPath) throws IOException;
    String savePreview(MultipartFile multipartFile) throws IOException;
    void deleteOldPreview(String oldPreviewPath) throws IOException;
}
