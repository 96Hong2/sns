package com.eunhong.sns.controller;

import com.eunhong.sns.controller.request.PostCreateRequest;
import com.eunhong.sns.controller.response.Response;
import com.eunhong.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private static PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request) {
        postService.create(request.getTitle(), request.getBody(), "");
        return Response.success(null);
    }
}
