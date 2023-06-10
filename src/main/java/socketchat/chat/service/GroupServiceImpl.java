package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.*;
import socketchat.chat.domain.Group;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.ChatRepository;
import socketchat.chat.repository.springdata.GroupRepository;
import socketchat.chat.repository.springdata.GroupUserRepository;
import socketchat.chat.repository.springdata.UserRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final ChatRepository chatRepository;
    private final SocketService socketService;
    private final SocketSession socketSession;
    private final FileStore fileStore;

    @Value("${file.group.profileDir}")
    private String fileDir;

    public Port addGroup(GroupAddDto groupAddDto) throws UnknownHostException {
        User user = userRepository.findByUserId(groupAddDto.getUserId());
        Group group = saveGroup(groupAddDto);

        log.info("userId = {}", user.getUserId());

        saveGroupUser(user, group);
        int groupId = addServerSocket(group);
        return socketService.getGroupSocketPort(groupId);
    }
    @Transactional
    private Group saveGroup(GroupAddDto groupAddDto) {
        Group group = buildGroupFromDto(groupAddDto);
        group = groupRepository.save(group);
        return group;
    }

    @Transactional
    private void saveGroupUser(User user, Group group) {
        GroupUser groupUser = buildGroupUser(user, group);
        groupUserRepository.save(groupUser);
    }

    private static GroupUser buildGroupUser(User user, Group group) {
        return GroupUser.builder()
                .groupUserId(new GroupUserId(group.getId(), user.getUserId()))
                .user(user)
                .group(group)
                .build();
    }

    private int addServerSocket(Group group) {
        socketService.addServerSocket(group.getId());
        return group.getId();
    }

    private static Group buildGroupFromDto(GroupAddDto groupAddDto) {
        return Group.builder()
                .groupName(groupAddDto.getGroupName())
                .build();
    }
    @Transactional
    public Port joinGroup(GroupJoinDto groupJoinDto) throws UnknownHostException {
        User user = userRepository.findByUserId(groupJoinDto.getUserId());
        Group group = groupRepository.findById(groupJoinDto.getGroupId());
        GroupUserId groupUserId = new GroupUserId(group.getId(), user.getUserId());
        GroupUser groupUser = GroupUser.builder().groupUserId(groupUserId).group(group).user(user).build();
        groupUser = groupUserRepository.save(groupUser);


        return socketService.getGroupSocketPort(group.getId());
    }

    public List<GroupDto> getGroupList() {
        return groupRepository.findAll().stream()
                .map(g -> new GroupDto(g.getId(), g.getGroupName(), g.getGroupImgUrl(), g.getPeople())).toList();
    }

    public List<GroupDto> getGroupListByUserId(String userId) {
        log.info("getGroupListBy{}", userId);
        List<GroupDto> groupDtos = groupUserRepository.findAllWithGroupAndUserByUserId(userId)
                .stream()
                .map(g -> new GroupDto(g.getGroup().getId(), g.getGroup().getGroupName(), g.getGroup().getGroupImgUrl(), g.getGroup().getPeople()))
                .toList();

        log.info(groupDtos.toString());
        return groupDtos;
    }

    public GroupReturnDto quitGroup(GroupJoinDto groupJoinDto) {
        ServerSocket serverSocket = socketSession.getServerSocketByGroupId(groupJoinDto.getGroupId());
        socketSession.removeClientSocket(serverSocket, groupJoinDto.getUserId());

        chatRepository.deleteAllByGroupUser(groupJoinDto.getGroupId(), groupJoinDto.getUserId());
        groupUserRepository.removeByUserIdAndGroupId(groupJoinDto.getGroupId(), groupJoinDto.getUserId());

        if(!groupUserRepository.existsUserByGroupId(groupJoinDto.getGroupId())){
            groupRepository.deleteById(groupJoinDto.getGroupId());
            socketSession.removeServerSocket(groupJoinDto.getGroupId(), serverSocket);
        }
        return new GroupReturnDto(groupJoinDto.getGroupId());
    }

    public List<UserInfoDto> getUserListByGroupId(int groupId) {
        return groupUserRepository.findAllWithGroupAndUserByGroupId(groupId).stream()
                .map(gu->new UserInfoDto(gu.getUser().getUserId(), gu.getUser().getNickname(), gu.getUser().getProfileImgUrl()))
                .collect(Collectors.toList());
    }


    public String saveGroupImg(int groupId, MultipartFile multipartFile) throws IOException {
        Group group = groupRepository.findById(groupId);
        String filename = null;
        if(multipartFile != null){
            filename = fileStore.saveFile(fileDir, multipartFile);
        }else{
            return null;
        }

        if(group.getGroupImgUrl()!=null){
            fileStore.deleteFile(fileDir, group.getGroupImgUrl());
        }

        saveGroupImgUrlByGroup(group, filename);
        return filename;
    }

    @Transactional
    private void saveGroupImgUrlByGroup(Group group, String filename) {
        group.updateGroupImgUrl(filename);
        groupRepository.save(group);
    }

    @Transactional
    public Boolean deleteProfileImgUrl(int groupId) {
        Group group = groupRepository.findById(groupId);
        fileStore.deleteFile(fileDir, group.getGroupImgUrl());
        saveGroupImgUrlByGroup(group, null);
        return true;
    }

    public Resource downloadGroupImg(String url) throws MalformedURLException {
        return fileStore.downloadFile(fileDir, url);
    }

    public Result<List<GroupDto>> searchGroup(String groupName){
        return new Result<>(groupRepository.findAllLikeGroupName(groupName).stream()
                .map(g->new GroupDto(g.getId(), g.getGroupName(), g.getGroupImgUrl(), g.getPeople()))
                .toList());
    }
}
