package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import socketchat.chat.controller.dto.Port;
import socketchat.chat.domain.Group;
import socketchat.chat.repository.springdata.GroupRepository;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class SocketServiceImpl extends Thread implements SocketService {
    private final GroupRepository groupRepository;
    private final SocketSession socketSession;
    private final ChatService chatService;
    private ExecutorService threadPool;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("서버 시작 기존 그룹 ServerSocket 생성 및 listen 시작");
        int groupCount = groupRepository.countGroup();
        List<Integer> groupIds= getGroupIdList();
        threadPool = Executors.newFixedThreadPool(2000);

        for(int i = 0; i < groupCount; i++){
            createServerSocket(groupIds.get(i));
        }
    }
    public void addServerSocket(int groupId) {
        createServerSocket(groupId);
    }

    private void createServerSocket(int groupId) {
        ServerSocket serverSocket = newServerSocket();
        socketSession.createServerSocketAndInitClientSocket(serverSocket, groupId);
        runSocketThread(serverSocket);
    }

    private static ServerSocket newServerSocket() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverSocket;
    }

    private void startSocketListening(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            acceptSocket(serverSocket, clientSocket);
        }
    }

    private void acceptSocket(ServerSocket serverSocket, Socket clientSocket) throws IOException {
        log.info("소켓 접근 {}", clientSocket.getInetAddress());
        threadPool.submit(()->{log.info("체팅 접근");try {
                socketSession.clientConnection(serverSocket, clientSocket);
                chatService.ReadClientMessage(serverSocket, clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Integer> getGroupIdList() {
        log.info("groupRepository");
        return groupRepository.findAll().stream()
                .map(Group::getId).toList();
    }

    private void runSocketThread(ServerSocket serverSocket) {
        threadPool.submit(() -> {try { startSocketListening(serverSocket);}
        catch (IOException e) {throw new RuntimeException(e);}
        });
    }

    /**
     * getter
     */
    public Port getGroupSocketPort(int groupId) throws UnknownHostException {
        return new Port(socketSession.getServerSocketByGroupId(groupId).getLocalPort());
    }


}
