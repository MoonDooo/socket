package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socketchat.chat.controller.dto.UserReturnDto;
import socketchat.chat.service.UserService;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserControllerImpl implements UserController{
    private final UserService userService;



    @PostMapping("/signup")
    public UserReturnDto signUp(@RequestBody UserDto userDto){
        return new UserReturnDto(userService.signUp(userDto));
    }

    @PostMapping("/login")
    public UserReturnDto login(@RequestBody UserLoginDto userLoginDto){
        return new UserReturnDto(userService.login(userLoginDto));
    }

    @PostMapping("/upload/profile")
    public UrlDto saveProfileImg(@RequestParam String userId, @RequestParam MultipartFile file) throws IOException {
        return new UrlDto(userService.saveProfileImgUrl(userId, file));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Boolean> deleteProfileImg(@RequestParam String userId){
        boolean status = userService.deleteProfileImgUrl(userId);
        return ResponseEntity.ok(status);
    }
}
