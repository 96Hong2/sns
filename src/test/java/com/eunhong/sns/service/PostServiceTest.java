package com.eunhong.sns.service;

import com.eunhong.sns.SnsApplication;
import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import com.eunhong.sns.repository.PostEntityRepository;
import com.eunhong.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트작성이_성공한경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        // 포스트 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        // 포스트 저장 시 PostEntity mock 객체가 나와야 함
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // 아무 에러도 throw하지 않는지 검증
        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

   @Test
    void 포스트작성시_요청한유저가_존재하지않는경우() { // 포스트 작성 시 유저가 존재하지 않는 경우
       String title = "title";
       String body = "body";
       String userName = "userName";

       // mocking
       // 포스트 작성한 유저가 존재하지 않아야 함
       when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
       // 포스트 저장 시 PostEntity mock 객체가 나와야 함
       when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

       // 적절한 에러를 throw해야 함
       SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
       Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
   }
}
