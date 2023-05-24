package socketchat.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.controller.dto.GroupAddDto;
import socketchat.chat.controller.dto.GroupDto;
import socketchat.chat.controller.dto.GroupJoinDto;
import socketchat.chat.domain.Group;
import socketchat.chat.domain.GroupUser;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.GroupRepository;
import socketchat.chat.repository.springdata.GroupUserRepository;
import socketchat.chat.repository.springdata.UserRepository;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupUserRepository groupUserRepository;
    private final SocketService socketService;

    public int addGroup(GroupAddDto groupAddDto) throws IOException {
        User user = userRepository.findByUserId(groupAddDto.getUserId());
        GroupUser groupUser = GroupUser.builder()
                .user(user).build();
        Group group = getGroupFromDto(groupAddDto, groupUser);
        int groupId = addServerSocket(group);

        return groupId;
    }

    private int addServerSocket(Group group) throws IOException {
        int groupId = groupRepository.save(group).getId();
        socketService.addServerSocket(groupId);
        return groupId;
    }

    private static Group getGroupFromDto(GroupAddDto groupAddDto, GroupUser groupUser) {
        return Group.builder()
                .groupName(groupAddDto.getGroupName())
                .groupImgUrl(groupAddDto.getGroupImgUrl())
                .groupUser(groupUser).build();
    }

    public int joinGroup(GroupJoinDto groupJoinDto) {
        User user = userRepository.findByUserId(groupJoinDto.getUserId());
        Group group = groupRepository.findById(groupJoinDto.getGroupId());
        GroupUser groupUser = GroupUser.builder().group(group).user(user).build();

        return groupUserRepository.save(groupUser).getGroupUserId().getGroupId();
    }

    public List<GroupDto> getGroupList() {
        return groupRepository.findAll().stream()
                .map(g -> new GroupDto(g.getId(), g.getGroupName(), g.getGroupImgUrl())).toList();
    }
}
