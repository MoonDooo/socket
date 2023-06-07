package socketchat.chat.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import socketchat.chat.controller.dto.Port;
import socketchat.chat.domain.Group;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;

public interface SocketService extends ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event);
    public void addServerSocket(int groupId);
    public Port getGroupSocketPort(int groupId) throws UnknownHostException;
}
