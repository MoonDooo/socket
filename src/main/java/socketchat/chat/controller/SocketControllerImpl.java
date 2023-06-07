package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socketchat.chat.controller.dto.Port;
import socketchat.chat.service.SocketServiceImpl;

import java.net.UnknownHostException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/socket")
public class SocketControllerImpl implements SocketController{
    private final SocketServiceImpl socketServiceImpl;

    @GetMapping("/port")
    public Port getPortAndIp(@RequestParam("groupId")int groupId) throws UnknownHostException {
        return socketServiceImpl.getGroupSocketPort(groupId);
    }


}
