package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.UrlDto;
import socketchat.chat.controller.dto.UserInfoDto;
import socketchat.chat.controller.dto.UserReturnDto;
import socketchat.chat.service.UserServiceImpl;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

import java.io.IOException;

@Slf4j
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserControllerImpl implements UserController{
    private final UserServiceImpl userServiceImpl;
    @PostMapping("/signup")
    public UserReturnDto signUp(@RequestBody UserDto userDto){
        return new UserReturnDto(userServiceImpl.signUp(userDto));
    }

    @GetMapping("/duplicateCheck")
    public ResponseEntity<Boolean> checkDuplication(@RequestParam String userId){
        boolean status = userServiceImpl.checkDuplication(userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/login")
    public UserReturnDto login(@RequestBody UserLoginDto userLoginDto){
        return new UserReturnDto(userServiceImpl.login(userLoginDto));
    }

    @GetMapping("/byUserId")
    public UserInfoDto getUserByUserId(@RequestParam String userId){
        return userServiceImpl.getUserInfoByUserId(userId);
    }


    @PostMapping("/profile")
    public UrlDto saveProfileImg(@RequestParam String userId, @RequestParam MultipartFile file) throws IOException {
        return userServiceImpl.saveProfileImgUrl(userId, file);
    }

    @GetMapping("/profile")
    public ResponseEntity<Resource> downloadProfileImg(@RequestParam String url) throws IOException {
        Resource resource = userServiceImpl.downloadProfileImg(url);
        log.info(resource.getFile().getAbsolutePath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Boolean> deleteProfileImg(@RequestParam String userId){
        boolean status = userServiceImpl.deleteProfileImgUrl(userId);
        return ResponseEntity.ok(status);
    }
}
