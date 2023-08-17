package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.UserJoinRequest;
import com.eunhong.sns.controller.request.UserLoginRequest;
import com.eunhong.sns.controller.response.Response;
import com.eunhong.sns.controller.response.UserJoinResponse;
import com.eunhong.sns.controller.response.UserLoginResponse;
import com.eunhong.sns.model.User;
import com.eunhong.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // userService가 final로 선언되어있으므로 자동으로 생성자 만들어서 주입받을 수 있게 함
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // RequestBody로 받아온 값을 서비스에 넘김
        User user = userService.join(request.getUserName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
