package socketchat.chat.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "group_users")
public class GroupUser {
    @EmbeddedId
    private GroupUserId groupUserId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Group group;

    /**
     * 양방향 매핑
     */
    @OneToMany(mappedBy = "groupUser")
    private List<Chat> chats = new ArrayList<>();

    @Builder
    public GroupUser(User user, Group group) {
        this.user = user;
        this.group = group;
    }

    @Builder
    public GroupUser(User user) {
        this.user = user;
    }
}
