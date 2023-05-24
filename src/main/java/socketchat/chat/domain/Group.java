package socketchat.chat.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "`groups`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private int id;

    private String groupName;

    private String groupImgUrl;

    /**
     * 양방향 매핑
     */
    @OneToMany(mappedBy = "group")
    private List<GroupUser> groupUsers = new ArrayList<>();

    @Builder
    public Group(String groupName, String groupImgUrl, GroupUser groupUser) {
        this.groupName = groupName;
        this.groupImgUrl = groupImgUrl;
        this.groupUsers.add(groupUser);
    }

    @Builder
    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Group(String groupName, GroupUser groupUser) {
        this.groupName = groupName;
        this.groupUsers.add(groupUser);
    }
}
