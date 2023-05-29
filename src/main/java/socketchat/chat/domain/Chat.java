package socketchat.chat.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "group_id"),
            @JoinColumn(name = "user_id")
    })
    private GroupUser groupUser;

    @JoinColumn(columnDefinition = "TEXT", name = "message")
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Chat(GroupUser groupUser, String message) {
        this.groupUser = groupUser;
        this.message = message;
    }
}
