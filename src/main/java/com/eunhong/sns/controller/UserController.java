package com.eunhong.sns.controller;

import com.eunhong.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi/users")
@RequiredArgsConstructor // userService가 final로 선언되어있으므로 자동으로 생성자 만들어서 주입받을 수 있게 함
public class UserController {

    private final UserService userService;

    // TODO : implement
    @PostMapping
    public void join() {
        // join
        userService.join("","");
    }
}
