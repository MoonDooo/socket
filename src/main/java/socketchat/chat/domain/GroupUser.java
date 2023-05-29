package socketchat.chat.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "group_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUser {
    @EmbeddedId
    private GroupUserId groupUserId;
    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @ManyToOne(fetch = LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    /**
     * 양방향 매핑
     */
    @OneToMany(mappedBy = "groupUser", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public GroupUser(GroupUserId groupUserId, User user, Group group) {
        this.groupUserId = groupUserId;
        this.user = user;
        this.group = group;
    }

    @Builder
    public GroupUser(User user) {
        this.user = user;
    }

    public void addGroupMapping(Group group){
        this.group = group;
    }
}
