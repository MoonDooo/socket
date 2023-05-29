package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.controller.dto.*;
import socketchat.chat.domain.Group;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.GroupUserId;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.GroupRepository;
import socketchat.chat.repository.springdata.GroupUserRepository;
import socketchat.chat.repository.springdata.UserRepository;

import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final SocketService socketService;
    private final SocketSession socketSession;

    public Port addGroup(GroupAddDto groupAddDto) throws UnknownHostException {
        User user = userRepository.findByUserId(groupAddDto.getUserId());
        Group group = saveGroup(groupAddDto);

        log.info("userId = {}", user.getUserId());

        saveGroupUser(user, group);
        int groupId = addServerSocket(group);
        return socketService.getGroupSocketPort(groupId);
    }

    private Group saveGroup(GroupAddDto groupAddDto) {
        Group group = buildGroupFromDto(groupAddDto);
        group = groupRepository.save(group);
        return group;
    }

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
                .groupImgUrl(groupAddDto.getGroupImgUrl())
                .build();
    }

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
                .map(g -> new GroupDto(g.getId(), g.getGroupName(), g.getGroupImgUrl())).toList();
    }

    public List<GroupDto> getGroupListByUserId(String userId) {
        log.info("getGroupListBy{}", userId);
        List<GroupDto> groupDtos = groupUserRepository.findAllWithGroupAndUserByUserId(userId)
                .stream()
                .map(g -> new GroupDto(g.getGroup().getId(), g.getGroup().getGroupName(), g.getGroup().getGroupImgUrl()))
                .toList();

        log.info(groupDtos.toString());
        return groupDtos;
    }

    public GroupReturnDto quitGroup(GroupJoinDto groupJoinDto) {
        ServerSocket serverSocket = socketSession.getServerSocketByGroupId(groupJoinDto.getGroupId());
        socketSession.removeClientSocket(serverSocket, groupJoinDto.getUserId());
        groupUserRepository.removeByUserIdAndGroupId(groupJoinDto.getGroupId(), groupJoinDto.getUserId());

        if(groupUserRepository.existsUserByGroupId(groupJoinDto.getGroupId())){
            groupRepository.deleteById(groupJoinDto.getGroupId());
        }
        return new GroupReturnDto(groupJoinDto.getGroupId());
    }
}
