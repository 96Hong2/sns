package com.eunhong.sns.service;

import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.Post;
import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import com.eunhong.sns.repository.PostEntityRepository;
import com.eunhong.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {

        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {

        // user find 포스트를 수정할 유저 받아옴, 실제로 존재하지 않으면 Exception Throw
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        // post exist 수정할 포스트 받아옴, 포스트가 실제로 존재하지 않으면 Exception Throw
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("$s is not found", postId)));

        // post permission 포스트를 작성한 사람이 수정하려하는지 확인
        if(postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        // post save 수정한 포스트 저장
        postEntity.setTitle(title);
        postEntity.setBody(body);

        // 수정한 포스트 반환
        Post post = Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
        return post;
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        // user find 포스트를 삭제할 유저 받아옴, 실제로 존재하지 않으면 Exception Throw
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        // post exist 삭제할 포스트 받아옴, 포스트가 실제로 존재하지 않으면 Exception Throw
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("$s is not found", postId)));

        // post permission 포스트를 작성한 사람이 삭제하려하는지 확인
        if(postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }
}
