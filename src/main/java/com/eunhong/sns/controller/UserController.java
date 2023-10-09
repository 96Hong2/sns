package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.UserJoinRequest;
import com.eunhong.sns.controller.request.UserLoginRequest;
import com.eunhong.sns.controller.response.AlarmResponse;
import com.eunhong.sns.controller.response.Response;
import com.eunhong.sns.controller.response.UserJoinResponse;
import com.eunhong.sns.controller.response.UserLoginResponse;
import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.User;
import com.eunhong.sns.service.UserService;
import com.eunhong.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // userService가 final로 선언되어있으므로 자동으로 생성자 만들어서 주입받을 수 있게 함
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // RequestBody로 받아온 값을 서비스에 넘김
        User user = userService.join(request.getName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        // 유저의 중복 인증 체크를 피하기 위해 authentication에서 유저를 가져온다.
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class) // safe casting
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User Class failed."));
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
