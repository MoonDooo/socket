package socketchat.chat.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private String userId;
    private String name;
    private String nickname;
    private String password;

    @Builder
    public UserDto(String userId, String name, String nickname, String password) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }
}
