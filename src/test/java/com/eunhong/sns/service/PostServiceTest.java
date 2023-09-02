package com.eunhong.sns.service;

import com.eunhong.sns.SnsApplication;
import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.fixture.PostEntityFixture;
import com.eunhong.sns.fixture.UserEntityFixture;
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

    @Test
    void 포스트수정이_성공한경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // PostEntity를 Fixture로 작성함으로써 포스트를 작성한(연관이 있는) 유저도 꺼내서 사용할 수 있다.
        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        // 포스트를 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        // 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // 아무 에러도 throw하지 않는지 검증
        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser();

        // 포스트를 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        // 해당 포스트 아이디의 포스트엔티티는 없어야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // POST_NOT_FOUND 에러를 throw해야 함
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId);
        UserEntity userEntity = postEntity.getUser(); // postEntity의 post를 작성한 실제 작성자 UserEntity
        UserEntity writer = UserEntityFixture.get("userName1", "aaa", 2);

        // 포스트를 작성하지 않은 유저를 찾음, 해당 유저는 존재하긴 해야 함 // 확인 필요
        when(userEntityRepository.findByUserName("userName1")).thenReturn(Optional.of(writer));
        // 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // INVALID_PERMISSION 에러를 throw해야 함
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }
}
