package socketchat.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.service.dto.UserIdDto;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * clientSocketMap : ServerSocket에 대한 유저 Id와 해당 클라이언트 소켓을 매핑하여 저장
 * groupSocketMap : groupId에 해당하는 serverSocket 유지
 */
@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class SocketSession {
    private final Map<ServerSocket, Map<String, Socket>> clientSocketMap;
    private final Map<Integer, ServerSocket> groupSocketMap;

    public ServerSocket getServerSocketByGroupId(int groupId){
        return groupSocketMap.get(groupId);
    }

    public void createServerSocketAndInitClientSocket(ServerSocket serverSocket, int groupId) {
        groupSocketMap.put(groupId, serverSocket);
        initClientSocketFromServerSocket(serverSocket);
    }

    public void initClientSocketFromServerSocket(ServerSocket serverSocket){
        clientSocketMap.put(serverSocket, new HashMap<>());
    }

    public String clientConnection(ServerSocket serverSocket, Socket clientSocket) throws IOException {
        String userId = getUserIdProtocol(clientSocket);
        clientSocketMap.get(serverSocket).put(userId, clientSocket);
        return userId;
    }

    private String getUserIdProtocol(Socket clientSocket) throws IOException {
        String message = "{ \"action\" : \"request_id\" }";
        sendMessageToClientSocket(clientSocket, message);
        UserIdDto userIdDto = new ObjectMapper().readValue(readMessageByClientSocket(clientSocket), UserIdDto.class);
        log.info("userId 접속 시도 : {}", userIdDto.getUserId());
        message = "{ \"status\" : \"200\" }";
        sendMessageToClientSocket(clientSocket, message);
        return userIdDto.getUserId();
    }

    public String readMessageByClientSocket(Socket clientSocket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String s = br.readLine();
        return s;
    }

    public void sendMessageToClientSocket(Socket clientSocket, String message) throws IOException {
        log.info("client={} send", clientSocket.getInetAddress().toString());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
        outputStreamWriter.write(message);
        outputStreamWriter.flush();
    }


    public void removeClientSocket(ServerSocket serverSocket, String userId) {
        log.info("removeClientSocket {}", clientSocketMap.get(serverSocket).get(userId).getInetAddress());
        clientSocketMap.get(serverSocket).remove(userId);
    }

    public Map<String, Socket> getClientSocketMap(ServerSocket serverSocket){
        return clientSocketMap.get(serverSocket);
    }

    public String getUserIdByClientSocket(ServerSocket serverSocket, Socket clientSocket) {
        return clientSocketMap.get(serverSocket).entrySet().stream()
                .filter(cm -> cm.getValue() == clientSocket)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
