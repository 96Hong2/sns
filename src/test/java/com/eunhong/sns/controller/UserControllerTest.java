package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.UserJoinRequest;
import com.eunhong.sns.controller.request.UserLoginRequest;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.User;
import com.eunhong.sns.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @Test
    public void 회원가입() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join()).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
            ).andDo(print()) // 결과 프린트
                .andExpect(status().isOk()); // status가 ok로 떨어지는지 확인
    }

    @Test
    public void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        // TODO : develop
        when(userService.join()).thenThrow(new SnsApplicationException());

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print()) // 결과 프린트
                .andExpect(status().isConflict()); // status가 conflict로 떨어지는지 확인
    }

    @Test
    public void 로그인() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login()).thenReturn("test_token");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print()) // 결과 프린트
                .andExpect(status().isOk()); // status가 ok로 떨어지는지 확인
    }

    @Test
    public void 로그인시_회원가입이_안된_userName을_입력할경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login()).thenThrow(new SnsApplicationException());

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print()) // 결과 프린트
                .andExpect(status().isNotFound()); // user를 찾지 못했으므로 not found status 반환
    }

    @Test
    public void 로그인시_틀린_password를_입력할경우_에러반환() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login()).thenThrow(new SnsApplicationException());

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print()) // 결과 프린트
                .andExpect(status().isUnauthorized()); // invalid한 password니까 인증실패 status 반환
    }
}
