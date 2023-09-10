package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.PostCreateRequest;
import com.eunhong.sns.controller.request.PostModifyRequest;
import com.eunhong.sns.controller.response.PostResponse;
import com.eunhong.sns.controller.response.Response;
import com.eunhong.sns.model.Post;
import com.eunhong.sns.service.PostService;
import lombok.AllArgsConstructor;
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
}
