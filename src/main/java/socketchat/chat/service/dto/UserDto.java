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
    private String profileImgUrl;

    @Builder
    public UserDto(String userId, String name, String nickname, String password, String profileImgUrl) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
    }

    @Builder
    public UserDto(String userId, String name, String nickname, String password) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }
}
