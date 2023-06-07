package socketchat.chat.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {
    String userId;
    String nickname;
    String profileImgUrl;
}
