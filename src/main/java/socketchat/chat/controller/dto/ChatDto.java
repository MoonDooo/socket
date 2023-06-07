package socketchat.chat.controller.dto;

import lombok.Data;
import socketchat.chat.domain.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ChatDto {
    String userId;
    String message;
    String createdAt;
    Type type;
    public ChatDto(String userId, String message, LocalDateTime createdAt, Type type) {
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.type = type;
    }
}
