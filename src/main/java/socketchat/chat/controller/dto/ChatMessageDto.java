package socketchat.chat.controller.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto {
    private String userId;
    private int groupId;
    private String message;
}
