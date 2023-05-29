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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final GroupUserRepository groupUserRepository;
    private final ChatRepository chatRepository;
    private final SocketSession socketSession;

    public void saveChat(ChatMessageDto chatMessageDto){
        log.info("saveChat start : groupId = {} , userId = {}", chatMessageDto.getGroupId(), chatMessageDto.getUserId());
        GroupUser groupUser = groupUserRepository.findAllWithGroupAndUserByGroupIdAndUserId(chatMessageDto.getGroupId(), chatMessageDto.getUserId());
        log.info("findAllWithGroupAndUserByGroupIdAndUserId complete = {} , {}", groupUser.getUser(), groupUser.getGroup());
        Chat chat = Chat.builder()
                .message(chatMessageDto.getMessage())
                .groupUser(groupUser)
                .build();
        log.info("saveChat.class");
        chatRepository.save(chat);
        log.info("chat save complete");
    }

    public void ReadClientMessage(ServerSocket serverSocket, Socket clientSocket) throws IOException {
        log.info("clientSocket.addr = {}", clientSocket.getInetAddress());
        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String message;

        try{
            while((message = socketSession.readMessageByClientSocket(clientSocket))!=null){
                log.info("메세지를 받았습니다.");
                log.info("message = {}" , message);
                sendChat(clientSocket, serverSocket, message);
                ChatMessageDto chatMessageDto = new ObjectMapper().readValue(message, ChatMessageDto.class);
                log.info("groupId = {}, userId = {}, message = {}", chatMessageDto.getGroupId(), chatMessageDto.getUserId(), chatMessageDto.getMessage());
                saveChat(chatMessageDto);
        }}catch (IOException e){
            String userId = socketSession.getUserIdByClientSocket(serverSocket, clientSocket);
            socketSession.removeClientSocket(serverSocket, userId);
        }

    }

    private void sendChat(Socket clientSocket, ServerSocket serverSocket, String message) {
        log.info("client={}, message={}", clientSocket.getInetAddress().toString(), message);
        Map<String, Socket> clientSocketMap = socketSession.getClientSocketMap(serverSocket);

        clientSocketMap.entrySet().stream()
                .filter(entry->
                        entry.getValue() != clientSocket
                )
                .forEach(entry -> {
                    try {
                        socketSession.sendMessageToClientSocket(entry.getValue(), message);
                    } catch (IOException e) {
                        socketSession.removeClientSocket(serverSocket, entry.getKey());
                    }
                });
    }

}
