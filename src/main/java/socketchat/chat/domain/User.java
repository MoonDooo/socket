package socketchat.chat.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * publicKey : 유저의 공개키
 * privateKey : 서버에서 해당 유저에 대한 의사난수 개인키
 */
@Entity @Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(length = 20, unique = true, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String nickname;

    private String profileImgUrl;

    /**
     * 양방향 매핑
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<GroupUser> groupUsers = new ArrayList<>();
    public void updateProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }
    @Builder
    public User(String userId, String password, String name, String nickname, String profileImgUrl) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }

    @Builder
    public User(String userId, String profileImgUrl){
        this.userId = userId;
        this.profileImgUrl = profileImgUrl;
    }
}
