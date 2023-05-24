package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socketchat.chat.controller.dto.AddrDto;
import socketchat.chat.service.SocketService;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/socket")
public class SocketControllerImpl implements SocketController{
    private final SocketService socketService;

    @GetMapping("/port")
    public AddrDto getPortAndIp(@RequestParam("groupId")int groupId) {
        return socketService.getSocketPort(groupId);
    }


}
