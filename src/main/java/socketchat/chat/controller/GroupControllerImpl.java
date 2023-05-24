package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import socketchat.chat.controller.dto.GroupAddDto;
import socketchat.chat.controller.dto.GroupDto;
import socketchat.chat.controller.dto.GroupJoinDto;
import socketchat.chat.controller.dto.GroupReturnDto;
import socketchat.chat.service.GroupService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupControllerImpl implements GroupController{
    private final GroupService groupService;

    @PostMapping("/add")
    public GroupReturnDto addGroup(@RequestBody GroupAddDto groupAddDto) throws IOException {
        return new GroupReturnDto(groupService.addGroup(groupAddDto));
    }

    @PostMapping("/join")
    public GroupReturnDto joinGroup(@RequestBody GroupJoinDto groupJoinDto){
        return new GroupReturnDto(groupService.joinGroup(groupJoinDto));
    }

    @GetMapping("/list")
    public List<GroupDto> getGroupList(){
        return groupService.getGroupList();
    }
}
