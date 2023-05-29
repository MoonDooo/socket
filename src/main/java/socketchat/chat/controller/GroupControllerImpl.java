package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import socketchat.chat.controller.dto.*;
import socketchat.chat.service.GroupService;
import socketchat.chat.service.dto.UserDto;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupControllerImpl implements GroupController{
    private final GroupService groupService;

    @PostMapping("/add")
    public Port addGroup(@RequestBody GroupAddDto groupAddDto) throws IOException {
        return groupService.addGroup(groupAddDto);
    }

    @PostMapping("/join")
    public Port joinGroup(@RequestBody GroupJoinDto groupJoinDto) throws UnknownHostException {
        return groupService.joinGroup(groupJoinDto);
    }

    @GetMapping("/list")
    public Result<List<GroupDto>> getGroupList(){
        return new Result<>(groupService.getGroupList());
    }

    @GetMapping("/listByUserId")
    public Result<List<GroupDto>> geGroupListByUserId(@RequestParam("userId")String userId){
        return new Result<>(groupService.getGroupListByUserId(userId));
    }

    @DeleteMapping("/quit")
    public GroupReturnDto quitGroup(@RequestBody GroupJoinDto groupJoinDto){
        return groupService.quitGroup(groupJoinDto);
    }
    /**
     * to do list
     * 그룹 안에 모든 user 정보 가지고 오기 chat controller 만들기
     * 파일 보내는거 만들기
     */

}
