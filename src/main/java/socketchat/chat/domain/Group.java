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

    @Column(nullable = false)
    private String groupName;

    private String groupImgUrl;

    /**
     * 양방향 매핑
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupUser> groupUsers = new ArrayList<>();

    @Builder
    public Group(String groupName, String groupImgUrl) {
        this.groupName = groupName;
        this.groupImgUrl = groupImgUrl;
    }

    @Builder
    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Group(String groupName, GroupUser groupUser) {
        this.groupName = groupName;
        this.groupUsers.add(groupUser);
        groupUser.addGroupMapping(this);
    }
}
