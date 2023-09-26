package com.eunhong.sns.service;

import com.eunhong.sns.exception.ErrorCode;
import com.eunhong.sns.exception.SnsApplicationException;
import com.eunhong.sns.model.Post;
import com.eunhong.sns.model.entity.LikeEntity;
import com.eunhong.sns.model.entity.PostEntity;
import com.eunhong.sns.model.entity.UserEntity;
import com.eunhong.sns.repository.LikeEntityRepository;
import com.eunhong.sns.repository.PostEntityRepository;
import com.eunhong.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;

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

    public Page<Post> list(Pageable pageable) { // 피드 목록 조회, Post들로 이루어진 페이지 객체를 반환한다.
        // pageable을 적용하여 FindAll로 페이지 범위 피드 목록의 포스트 페이지들을 가져옴
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) { // 내가 작성한 피드 목록을 가져옴
        // 요청하는 유저의 유저엔티티를 받아옴, 유저가 존재하지 않으면 Error Throw
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("$s is not found", postId)));

        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        // check liked -> throw
        // like는 유저 당 한 번만 누를 수 있다. 해당 유저가 이미 like를 누른 적 있는 지 체크
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }

    @Transactional
    public int likeCount(Integer postId) { // 해당 게시물에 like가 몇 개 있는지 반환
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()
                -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("$s is not found", postId)));

        // List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
        return likeEntityRepository.countByPost(postEntity);
    }
}
