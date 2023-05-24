package socketchat.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import socketchat.chat.controller.dto.UserReturnDto;
import socketchat.chat.service.UserService;
import socketchat.chat.service.dto.UserDto;
import socketchat.chat.service.dto.UserLoginDto;

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
}
