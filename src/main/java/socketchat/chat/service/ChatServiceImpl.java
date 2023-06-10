package socketchat.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.ChatDto;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.domain.GroupUserId;
import socketchat.chat.domain.Type;
import socketchat.chat.service.dto.RecvMessageDto;
import socketchat.chat.service.dto.SendMessageDto;
import socketchat.chat.domain.Chat;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.repository.springdata.ChatRepository;
import socketchat.chat.repository.springdata.GroupUserRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{
    @Value("${file.chat.profileDir}")
    private String fileDir;
    private final GroupUserRepository groupUserRepository;
    private final ChatRepository chatRepository;
    private final SocketSession socketSession;
    private final FileStore fileStore;

    @Transactional
    public void saveChat(SendMessageDto chatMessageDto){
        log.info("saveChat start : groupId = {} , userId = {}", chatMessageDto.getGroupId(), chatMessageDto.getUserId());
        GroupUser groupUser = getGroupUser(chatMessageDto.getGroupId(), chatMessageDto.getUserId());
        Chat chat = Chat.builder()
                .message(chatMessageDto.getMessage())
                .groupUser(groupUser)
                .type(chatMessageDto.getType())
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
                int groupId = socketSession.getGroupByServerSocket(serverSocket);
                SendMessageDto sendMessageDto = getSendMessageDto(serverSocket, clientSocket, groupId, new ObjectMapper().readValue(message, RecvMessageDto.class), Type.TEXT);
                sendChat(clientSocket, serverSocket, sendMessageDto);

                log.info("groupId = {}, userId = {}, message = {}", sendMessageDto.getGroupId(), sendMessageDto.getUserId(), sendMessageDto.getMessage());
                saveChat(sendMessageDto);
        }}catch (IOException e){
            String userId = socketSession.getUserIdByClientSocket(serverSocket, clientSocket);
            socketSession.removeClientSocket(serverSocket, userId);
        }

    }

    private SendMessageDto getSendMessageDto(ServerSocket serverSocket, Socket clientSocket, int groupId, RecvMessageDto recvMessageDto, Type type) {
        String userId = socketSession.getUserIdByClientSocket(serverSocket, clientSocket);
        SendMessageDto sendMessageDto = new SendMessageDto(groupId, userId, recvMessageDto.getMessage(), type);
        return sendMessageDto;
    }

    private SendMessageDto getSendMessageDto(int groupId, String userId, String message, Type type){
        SendMessageDto sendMessageDto = new SendMessageDto(groupId, userId, message, type);
        return sendMessageDto;
    }

    private void sendChat(SendMessageDto sendMessageDto) throws JsonProcessingException {
        ServerSocket serverSocket = socketSession.getServerSocketByGroupId(sendMessageDto.getGroupId());
        Socket clientSocket = socketSession.getClientSocketByServerSocketAndUserId(serverSocket, sendMessageDto.getUserId());
        sendChat(clientSocket, serverSocket, sendMessageDto);
    }


    private void sendChat(Socket clientSocket, ServerSocket serverSocket, SendMessageDto sendMessageDto) throws JsonProcessingException {
        log.info("client={}, message={}", clientSocket.getInetAddress().toString(), sendMessageDto.getMessage());

        String message = new ObjectMapper().writeValueAsString(sendMessageDto);
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

    public List<ChatDto> getChatListByGroupId(int groupId) {
        return chatRepository.findAllWithGroupUserByGroupId(groupId).stream()
                .map(c -> new ChatDto(c.getGroupUser().getUser().getUserId(), c.getMessage(), c.getCreatedAt(), c.getType()))
                .collect(Collectors.toList());
    }

    public UrlDto sendFile(int groupId, String userId, MultipartFile file) throws IOException {
        //파일을 저장한다.
        String url=null;
        if(file!=null){
            url = fileStore.saveFile(fileDir, file);
        }else return new UrlDto(null);
        if(groupUserRepository.existsUserAndGroup(userId, groupId)){
            SendMessageDto sendMessageDto = getSendMessageDto(groupId, userId, url, Type.FILE);
            log.info("groupId={}, userId={}, MultipartFile={}, fileDir={}", groupId, userId, file.getOriginalFilename(), fileDir);
            saveChat(sendMessageDto);
            sendChat(sendMessageDto);
        }else throw new RuntimeException("누구세요?");


        return new UrlDto(url);
    }


    private GroupUser getGroupUser(int groupId, String userId) {
        GroupUserId groupUserId = new GroupUserId(groupId, userId);
        GroupUser groupUser = groupUserRepository.findById(groupUserId).orElseThrow();
        return groupUser;
    }

    public Resource downloadFile(String url) throws MalformedURLException {
        return fileStore.downloadFile(fileDir, url);
    }
}
