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
    void 포스트작성시_요청한유저가_존재하지않는경우() {
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
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // 포스트를 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        // 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        // 수정한 포스트 저장하는 Mocking
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        // 아무 에러도 throw하지 않는지 검증
        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
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

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "test", 2);

        // mocking : userName으로 유저를 찾았을 때 포스트를 작성하지 않은 유저인 writer가 반환되도록 모킹
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        // mocking : 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // INVALID_PERMISSION 에러를 throw해야 함
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트삭제가_성공한경우() {
        String userName = "userName";
        Integer postId = 1;

        // PostEntity를 Fixture로 작성함으로써 포스트를 작성한(연관이 있는) 유저도 꺼내서 사용할 수 있다.
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // mocking : 포스트를 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        // mocking : 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // 아무 에러도 throw하지 않는지 검증
        Assertions.assertDoesNotThrow(() -> postService.delete(userName, postId));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지않는_경우() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // mocking : 포스트를 작성한 유저가 존재해야 함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        // mocking : 해당 포스트 아이디의 포스트엔티티는 없어야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // POST_NOT_FOUND 에러를 throw해야 함
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_없는_경우() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "test", 2);

        // mocking : userName으로 유저를 찾았을 때 포스트를 작성하지 않은 유저인 writer가 반환되도록 모킹
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        // mocking : 해당 포스트 아이디의 포스트엔티티가 존재해야 함
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // INVALID_PERMISSION 에러를 throw해야 함
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }
}
