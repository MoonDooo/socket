package socketchat.chat.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.UserRepository;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

/**
 * 반환형 dto로 변환할 예정
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public String signUp(UserDto userDto){
        log.info("userId={}, name={}, nickname={}", userDto.getUserId(), userDto.getName(), userDto.getNickname());
        User user = UserBuild(userDto);
        userRepository.save(user);
        return user.getUserId();
    }

    public String login(UserLoginDto userLoginDto){
        if(userRepository.existsByUserIdAndPassword(userLoginDto.getUserId(), userLoginDto.getPassword())){
            return userLoginDto.getUserId();
        }
        return null;
    }

    private static User UserBuild(UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .nickname(userDto.getNickname())
                .password(userDto.getPassword())
                .profileImgUrl(userDto.getProfileImgUrl())
                .build();
    }

}