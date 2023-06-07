package socketchat.chat.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUserId implements Serializable{
    @Column(name = "group_id")
    int groupId;
    @Column(name = "user_id")
    String userId;

    public GroupUserId(int groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}
