package socketchat.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import socketchat.chat.service.dto.UserIdDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface SocketSession {
    ServerSocket getServerSocketByGroupId(int groupId);
    void createServerSocketAndInitClientSocket(ServerSocket serverSocket, int groupId);
    void initClientSocketFromServerSocket(ServerSocket serverSocket);
    String clientConnection(ServerSocket serverSocket, Socket clientSocket) throws IOException;
    String readMessageByClientSocket(Socket clientSocket) throws IOException;
    void sendMessageToClientSocket(Socket clientSocket, String message) throws IOException;
    void removeClientSocket(ServerSocket serverSocket, String userId);
    Map<String, Socket> getClientSocketMap(ServerSocket serverSocket);
    String getUserIdByClientSocket(ServerSocket serverSocket, Socket clientSocket);
    void removeServerSocket(int groupId, ServerSocket serverSocket);
    Socket getClientSocketByServerSocketAndUserId(ServerSocket serverSocket, String userId);
    public Integer getGroupByServerSocket(ServerSocket serverSocket);
}
