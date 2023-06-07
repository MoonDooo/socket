package socketchat.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.repository.springdata.GroupUserRepository;
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
public class SocketSessionImpl implements SocketSession{
    private final GroupUserRepository groupUserRepository;

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
        String userId = getUserIdProtocol(serverSocket, clientSocket);
        clientSocketMap.get(serverSocket).put(userId, clientSocket);
        return userId;
    }

    private String getUserIdProtocol(ServerSocket serverSocket, Socket clientSocket) throws IOException {
        String message = "{ \"action\" : \"request_id\" }";
        sendMessageToClientSocket(clientSocket, message);
        UserIdDto userIdDto = new ObjectMapper().readValue(readMessageByClientSocket(clientSocket), UserIdDto.class);
        log.info("userId 접속 시도 : {}", userIdDto.getUserId());
        if(isValidUserId(serverSocket, userIdDto.getUserId())){
            message = "{ \"status\" : \"200\" }";
            sendMessageToClientSocket(clientSocket, message);
        }else{
            message = "{ \"status\" : \"405\" }";
            sendMessageToClientSocket(clientSocket, message);
            throw new NullPointerException("누구세요?");
        }

        return userIdDto.getUserId();
    }

    private boolean isValidUserId(ServerSocket serverSocket, String userId) {
        int groupId = getGroupByServerSocket(serverSocket);
        return groupUserRepository.existsUserAndGroup(userId, groupId);
    }

    public Integer getGroupByServerSocket(ServerSocket serverSocket) {
        return groupSocketMap.entrySet().stream()
                .filter(g->g.getValue()==serverSocket)
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
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
        Optional<Socket> socket;
        log.info("사용자 소켓 삭제 userId : {}", userId);
        if (clientSocketMap.get(serverSocket).containsKey(userId)){
            socket = Optional.ofNullable(clientSocketMap.get(serverSocket).get(userId));
            log.info("socket = {}", socket.get().getInetAddress());
        }else return;

        if(socket.isPresent()){
            log.info("removeClientSocket {}", socket.get());
            clientSocketMap.get(serverSocket).remove(userId);
        }
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

    public void removeServerSocket(int groupId, ServerSocket serverSocket) {
        groupSocketMap.remove(groupId);
        clientSocketMap.remove(serverSocket);
    }

    public Socket getClientSocketByServerSocketAndUserId(ServerSocket serverSocket, String userId) {
        if (clientSocketMap.get(serverSocket).containsKey(userId)){
            return clientSocketMap.get(serverSocket).get(userId);
        }else{
            throw new NullPointerException("접속도 안하신 분이 어떻게 오셨데");
        }
    }
}
