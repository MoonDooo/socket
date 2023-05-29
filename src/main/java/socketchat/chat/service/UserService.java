package socketchat.chat.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.domain.User;
import socketchat.chat.repository.springdata.UserRepository;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 반환형 dto로 변환할 예정
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Value("${file.user.profileDir}")
    private String fileDir;

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

    public String saveProfileImgUrl(String userId, MultipartFile multipartFile) throws IOException {
        log.info("userId = {} 프로필 사진 업로드 요청", userId);
        String filePath = null;
        User user = userRepository.findByUserId(userId);

        if(multipartFile!=null){
            filePath = saveFile(multipartFile);
        }
        if(user.getProfileImgUrl()!=null){
            deleteProfileImg(user.getProfileImgUrl());
        }
        saveProfileImgByUserId(user, filePath);
        return filePath;
    }

    private void saveProfileImgByUserId(User user, String filePath) {
        user.updateProfileImgUrl(filePath);
        userRepository.save(user);
    }

    private void deleteProfileImg(String profileUrl) {
        File file = new File(profileUrl);
        if(file.delete()){
            log.info("{} 파일 삭제", profileUrl);
        }else{
            log.debug("파일 삭제 실패");
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileExtension = originalFilename.substring(originalFilename.indexOf("."));
        String filePath = fileDir + UUID.randomUUID().toString() + fileExtension;
        log.info("upload filePath = {}", filePath);
        file.transferTo(new File(filePath));
        return filePath;
    }

    public Boolean deleteProfileImgUrl(String userId) {
        User user = userRepository.findByUserId(userId);
        deleteProfileImg(user.getProfileImgUrl());
        saveProfileImgByUserId(user, null);
        return true;
    }
}
