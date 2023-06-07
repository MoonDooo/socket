package socketchat.chat.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public interface FileStore {
    void deleteFile(String absolutePath, String fileName);
    String saveFile(String absolutePath, MultipartFile file) throws IOException;
    Resource downloadFile(String absolutePath, String fileName) throws MalformedURLException;
}
