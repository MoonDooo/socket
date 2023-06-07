package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStoreImpl implements FileStore{
    public void deleteFile(String absolutePath, String fileName) {
        File file = new File(absolutePath + fileName);
        if(file.delete()){
            log.info("{} 파일 삭제", absolutePath+fileName);
        }else{
            log.debug("파일 삭제 실패");
        }
    }

    public String saveFile(String absolutePath, MultipartFile file) throws IOException {
        String saveFilename = getUUID() + getExtension(requireNonNull(file.getOriginalFilename()));
        file.transferTo(new File(absolutePath + saveFilename));
        log.info("파일저장 = {}" , absolutePath + saveFilename);
        return saveFilename;
    }

    public Resource downloadFile(String absolutePath, String fileName) throws MalformedURLException {
        return new UrlResource("file:" + absolutePath + fileName);
    }

    private String getUUID(){
        return UUID.randomUUID().toString();
    }

    private String getExtension(String originalFilename){
        return originalFilename.substring(originalFilename.indexOf("."));
    }

}
