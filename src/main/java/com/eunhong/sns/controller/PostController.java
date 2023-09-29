package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.PostCommentRequest;
import com.eunhong.sns.controller.request.PostCreateRequest;
import com.eunhong.sns.controller.request.PostModifyRequest;
import com.eunhong.sns.controller.response.PostResponse;
import com.eunhong.sns.controller.response.Response;
import com.eunhong.sns.model.Post;
import com.eunhong.sns.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName()); // authentication의 Principal에서 name을 가져옴
        return Response.success();
    }

    @PutMapping("/{postId}") // 수정할 포스트 아이디를 @PathVariable로 받아옴
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId); // authentication의 Principal에서 name을 가져옴
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) { // Pageable은 페이징 기능을 가지는 인터페이스
        // 포스트 목록을 페이징 범위만큼 조회해서 가져온 Post들을 PostResponse로 매핑한 결과를 success에서 반환
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication) {
        // 자신이 작성한 포스트 리스트를 페이징하여 조회해옴
        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("{postId}/likes")
    public Response<Integer> likeCount(@PathVariable Integer postId) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        postService.comment(postId, authentication.getName(), request.getComment());
        return Response.success();
    }
}
