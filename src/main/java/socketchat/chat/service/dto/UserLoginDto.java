package socketchat.chat.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UserLoginDto {
    private String userId;
    private String password;
    private String name;
    private String nickname;
    private String profileImgUrl;
}
