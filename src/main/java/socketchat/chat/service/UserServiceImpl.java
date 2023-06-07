package socketchat.chat.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.controller.dto.UserInfoDto;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.UserRepository;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * 반환형 dto로 변환할 예정
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final FileStore fileStore;

    @Value("${file.user.profileDir}")
    private String fileDir;

    public String signUp(UserDto userDto){
        log.info("userId={}, name={}, nickname={}", userDto.getUserId(), userDto.getName(), userDto.getNickname());
        if (checkDuplication(userDto.getUserId())){
            throw new RuntimeException("누구세요?");
        }
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
                .build();
    }

    public UrlDto saveProfileImgUrl(String userId, MultipartFile multipartFile) throws IOException {
        log.info("userId = {} 프로필 사진 업로드 요청", userId);
        String fileName = null;
        User user = userRepository.findByUserId(userId);

        if(multipartFile!=null){
            fileName = fileStore.saveFile(fileDir, multipartFile);
        }
        else{
            return null;
        }
        if(user.getProfileImgUrl()!=null){
            fileStore.deleteFile(fileDir, user.getProfileImgUrl());
        }
        saveProfileImgByUser(user, fileName);
        return new UrlDto(fileName);
    }

    private void saveProfileImgByUser(User user, String filePath) {
        user.updateProfileImgUrl(filePath);
        userRepository.save(user);
    }

    public Boolean deleteProfileImgUrl(String userId) {
        User user = userRepository.findByUserId(userId);
        fileStore.deleteFile(fileDir, user.getProfileImgUrl());
        saveProfileImgByUser(user, null);
        return true;
    }

    public UserInfoDto getUserInfoByUserId(String userId) {
        log.info("userId = {}", userId);
        User user = userRepository.findByUserId(userId);
        return new UserInfoDto(user.getUserId(), user.getNickname(), user.getProfileImgUrl());
    }

    public Resource downloadProfileImg(String url) throws MalformedURLException {
        return fileStore.downloadFile(fileDir, url);
    }

    public boolean checkDuplication(String userId) {
        return userRepository.existsById(userId);
    }
}
