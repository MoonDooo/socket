package socketchat.chat.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.controller.dto.UserInfoDto;
import socketchat.chat.domain.User;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

import java.io.IOException;
import java.net.MalformedURLException;

public interface UserService {
    String signUp(UserDto userDto);
    String login(UserLoginDto userLoginDto);
    UrlDto saveProfileImgUrl(String userId, MultipartFile multipartFile) throws IOException;
    Boolean deleteProfileImgUrl(String userId);
    UserInfoDto getUserInfoByUserId(String userId);
    Resource downloadProfileImg(String url) throws MalformedURLException;
    boolean checkDuplication(String userId);
}
