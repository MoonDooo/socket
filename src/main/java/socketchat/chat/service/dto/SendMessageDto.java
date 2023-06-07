package socketchat.chat.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import socketchat.chat.domain.Type;

@Data
@AllArgsConstructor
@Builder
public class SendMessageDto {
    int groupId;
    String userId;
    String message;
    Type type;
}
