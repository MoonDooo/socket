package socketchat.chat.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "group_id", insertable = false, updatable = false),
            @JoinColumn(name = "user_id", insertable = false, updatable = false)
    })
    private GroupUser groupUser;

    @JoinColumn(columnDefinition = "TEXT", name = "message")
    private String message;

    @CreationTimestamp
    private LocalDate createdAt;

    @Builder
    public Chat(GroupUser groupUser, String message) {
        this.groupUser = groupUser;
        this.message = message;
    }
}
