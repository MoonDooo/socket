package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.ChatDto;
import socketchat.chat.controller.dto.Result;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.service.ChatServiceImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/chat")
public class ChatControllerImpl implements ChatController{
    private final ChatServiceImpl chatServiceImpl;

    @GetMapping("/group")
    public Result<List<ChatDto>> getChatListByGroupId(@RequestParam("groupId")int groupId){
        return new Result<>(chatServiceImpl.getChatListByGroupId(groupId));
    }

    @PostMapping("/file")
    public UrlDto sendFile(@RequestParam int groupId, @RequestParam String userId, @RequestParam MultipartFile file) throws IOException {
        return chatServiceImpl.sendFile(groupId, userId, file);
    }

    @GetMapping("file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String url) throws MalformedURLException {
        Resource resource = chatServiceImpl.downloadFile(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
