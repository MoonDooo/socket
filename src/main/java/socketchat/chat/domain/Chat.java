package socketchat.chat.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
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

    @Column(columnDefinition = "TEXT", name = "message")
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Builder
    public Chat(GroupUser groupUser, String message, Type type) {
        this.groupUser = groupUser;
        this.message = message;
        this.type = type;
    }
}
