package socketchat.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.ChatDto;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.domain.Chat;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;
import socketchat.chat.domain.Type;
import socketchat.chat.service.dto.RecvMessageDto;
import socketchat.chat.service.dto.SendMessageDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ChatService {
    void saveChat(SendMessageDto chatMessageDto);
    void ReadClientMessage(ServerSocket serverSocket, Socket clientSocket) throws IOException;
    List<ChatDto> getChatListByGroupId(int groupId);
    UrlDto sendFile(int groupId, String userId, MultipartFile file) throws IOException;
    Resource downloadFile(String url) throws MalformedURLException;
}
