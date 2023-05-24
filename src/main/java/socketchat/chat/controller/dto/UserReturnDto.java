package socketchat.chat.controller.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReturnDto {
    String userId;
}
