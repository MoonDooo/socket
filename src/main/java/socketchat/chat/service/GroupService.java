package socketchat.chat.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.*;
import socketchat.chat.domain.Group;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;
import socketchat.chat.domain.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

public interface GroupService {
    Port addGroup(GroupAddDto groupAddDto) throws UnknownHostException;
    Port joinGroup(GroupJoinDto groupJoinDto) throws UnknownHostException;
    List<GroupDto> getGroupList();
    List<GroupDto> getGroupListByUserId(String userId);
    GroupReturnDto quitGroup(GroupJoinDto groupJoinDto);
    List<UserInfoDto> getUserListByGroupId(int groupId);
    String saveGroupImg(int groupId, MultipartFile multipartFile) throws IOException;
    Boolean deleteProfileImgUrl(int groupId);
    Resource downloadGroupImg(String url) throws MalformedURLException;
    Result<List<GroupDto>> searchGroup(String groupName);
}
