package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import socketchat.chat.controller.dto.AddrDto;
import socketchat.chat.domain.Group;
import socketchat.chat.repository.springdata.GroupRepository;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SocketService extends Thread implements ApplicationListener<ContextRefreshedEvent> {
    private final ChatService chatService;
    private final GroupRepository groupRepository;
    private ExecutorService threadPool;
    private Map<Integer, ServerSocket> groupSocket;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("쓰레드 생성");
        int groupCount = groupRepository.countByGroupId();
        List<Integer> groupIds= getGroupIdList();

        threadPool = Executors.newFixedThreadPool(2000);
        groupSocket = new HashMap<>();

        for(int i = 0; i < groupCount; i++){
            ServerSocket serverSocket = getServerSocket();
            groupSocket.put(groupIds.get(i), serverSocket);
            runSocketThread(serverSocket);
        }
    }

    private void startSocketListening(ServerSocket serverSocket) throws IOException {
        while(true){
            Socket clientSocket = serverSocket.accept();
            acceptSocket(clientSocket);
        }
    }

    private void acceptSocket(Socket clientSocket) throws IOException {
        log.info("소켓 접근 {}", clientSocket.getInetAddress());
        threadPool.submit(()->{
            try {
                log.info("체팅 접근");
                chatService.clientConnection(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getInet4Address() throws UnknownHostException {
        StringTokenizer st = new StringTokenizer( Inet4Address.getLocalHost().toString(), "/");
        st.nextToken();
        return st.nextToken();
    }

    private List<Integer> getGroupIdList() {
        return groupRepository.findAll().stream()
                .map(Group::getId).toList();
    }

    private void runSocketThread(ServerSocket serverSocket) {
        threadPool.submit(() -> {
            try {
                startSocketListening(serverSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static ServerSocket getServerSocket() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return serverSocket;
    }

    public void addServerSocket(int groupId) throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        groupSocket.put(groupId, serverSocket);
        threadPool.submit(()->{
           try{
               startSocketListening(serverSocket);
           }catch(IOException e){
               throw new RuntimeException(e);
           }
        });
    }

    public AddrDto getSocketPort(int groupId) {
        return new AddrDto(groupSocket.get(groupId).getInetAddress().getHostAddress().toString(), groupSocket.get(groupId).getLocalPort());
    }
}
