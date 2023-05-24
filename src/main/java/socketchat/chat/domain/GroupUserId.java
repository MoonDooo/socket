package socketchat.chat.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@Embeddable
public class GroupUserId implements Serializable{
    @Column(name = "group_id")
    int groupId;
    @Column(name = "user_id")
    Long userId;
}
