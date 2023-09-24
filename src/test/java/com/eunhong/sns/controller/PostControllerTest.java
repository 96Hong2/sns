package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.PostCreateRequest;
import com.eunhong.sns.controller.request.PostModifyRequest;
import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.fixture.PostEntityFixture;
import com.eunhong.sns.model.Post;
import com.eunhong.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser // 포스트 작성 시 인증된 유저가 작성한 것을 표현
    void 포스트작성() throws Exception { // 포스트 작성이 성공한 경우

        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 익명의 유저로 이 요청을 날렸을 경우를 표현
    void 포스트작성시_로그인하지않은경우() throws Exception {

        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser // 포스트 수정 시 인증된 유저가 작성한 것을 표현
    void 포스트수정() throws Exception { // 포스트 수정이 성공한 경우

        String title = "title";
        String body = "body";

        // mocking을 해줘서 modify시 포스트를 반환하도록 mocking
        when(postService.modify(eq(title), eq(body), any(), any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        mockMvc.perform(put("/api/v1/posts/1") // posts 다음에 수정할 포스트의 아이디를 넣었음
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 포스트 수정 시 인증되지 않은 유저가 작성한 것을 표현
    void 포스트수정시_로그인하지않은경우() throws Exception {

        String title = "title";
        String body = "body";

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트수정시_본인이_작성한_글이_아니라면_에러발생() throws Exception {

        String title = "title";
        String body = "body";

        // any()를 썼기 때문에 eq로 감쌈
        // modify를 했을 때 INVALID_PERMISSION 에러를 발생시키도록 mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트수정시_수정하려는_글이_없는경우_에러발생() throws Exception {

        String title = "title";
        String body = "body";

        // modify를 했을 때 POST_NOT_FOUND 에러를 발생시키도록 하는 상황을 가정하여 mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser // 포스트 삭제 시 인증된 유저가 작성한 것을 표현
    void 포스트삭제() throws Exception { // 포스트 삭제가 성공한 경우
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트삭제시_로그인하지_않은경우() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트삭제시_작성자와_삭제요청자가_다를_경우() throws Exception {
        // mocking
        // delete메서드의 반환타입이 void이기 때문에 doThrow를 하고 나서 when을 해준다.
        // delete 작업을 수행했을 때 INVALID_PERMISSION 에러가 떨어지도록 mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트삭제시_삭제하려는_포스트가_존재하지_않을_경우() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 피드목록() throws Exception {
        // mocking : postService.list를 호출하여 빈 페이지를 리턴하도록 한다.
        when(postService.list(any())).thenReturn(Page.empty()); // Page.empty는 빈 페이지를 생성하는 메서드

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 피드목록요청시_로그인하지_않은경우() throws Exception {
        //mocking
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 내피드목록() throws Exception { // 자신이 작성한 글의 피드 목록 조회
        // mocking : 내 피드목록을 조회하는 메서드 postService.my 호출하여 빈 페이지 리턴
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 내피드목록요청시_로그인하지_않은경우() throws Exception {
        // mocking
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요기능() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes") // 좋아요를 눌러서 변경시키므로 post
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 좋아요버튼클릭시_로그인하지_않은경우() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요버튼클릭시_게시물이_없는경우() throws Exception {
        // mocking : postService의 like를 실행했을 때 POST_NOT_FOUND 에러를 던지도록 모킹
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

}
