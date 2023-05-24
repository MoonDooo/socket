package socketchat.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.controller.dto.ChatMessageDto;
import socketchat.chat.domain.Chat;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.repository.springdata.ChatRepository;
import socketchat.chat.repository.springdata.GroupUserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final GroupUserRepository groupUserRepository;
    private final ChatRepository chatRepository;

    public void saveChat(ChatMessageDto chatMessageDto) {
        GroupUser groupUser = groupUserRepository.findAllWithGroupAndUserByGroupIdAndUserId(chatMessageDto.getGroupId(), chatMessageDto.getUserId());
        Chat chat = Chat.builder()
                .message(chatMessageDto.getMessage())
                .groupUser(groupUser)
                .build();
        chatRepository.save(chat);
    }

    public void clientConnection(Socket clientSocket) throws IOException {
        log.info("clientSocket.addr = {}", clientSocket.getInetAddress());

        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;
        while((message = br.readLine())!= null){
            log.info("메세지를 받았습니다.");
            log.info("message = {}" , message);
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessageDto chatMessageDto = objectMapper.readValue(message, ChatMessageDto.class);

            log.info("groupId = {}, userId = {}, message = {}", chatMessageDto.getGroupId(), chatMessageDto.getUserId(), chatMessageDto.getMessage());

            saveChat(chatMessageDto);
            sendChat(chatMessageDto);
        }

    }

    private void sendChat(ChatMessageDto chatMessageDto) {

    }
}
