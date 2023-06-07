package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.*;
import socketchat.chat.service.GroupService;

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

    @GetMapping("/users")
    public Result<List<UserInfoDto>> getUserListByGroupId(@RequestParam int groupId){
        return new Result<>(groupService.getUserListByGroupId(groupId));
    }

    @PostMapping("/img")
    public UrlDto saveGroupImg(@RequestParam int groupId, @RequestParam MultipartFile file) throws IOException {
        return new UrlDto(groupService.saveGroupImg(groupId, file));
    }

    @DeleteMapping("/img")
    public ResponseEntity<Boolean> deleteGroupImg(@RequestParam int groupId){
        boolean status = groupService.deleteProfileImgUrl(groupId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/img")
    public ResponseEntity<Resource> downloadGroupImg(@RequestParam String url) throws IOException {
        Resource resource = groupService.downloadGroupImg(url);
        log.info(resource.getFile().getAbsolutePath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/search")
    public Result<List<GroupDto>> searchGroup(@RequestParam String groupName) {
        return groupService.searchGroup(groupName);
    }

}
