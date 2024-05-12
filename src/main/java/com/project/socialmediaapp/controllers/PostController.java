package com.project.socialmediaapp.controllers;

import com.project.socialmediaapp.entities.Post;
import com.project.socialmediaapp.requests.post_requests.PostCreateRequest;
import com.project.socialmediaapp.requests.post_requests.PostUpdateRequest;

import com.project.socialmediaapp.responses.PostResponse;
import org.springframework.web.bind.annotation.*;

import com.project.socialmediaapp.services.PostService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> getAllPosts(@RequestParam Optional<Long> userId) {
        return postService.getAllPosts(userId);
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable Long postId) {
        return postService.getPostWithLikes(postId);
    }

    @PostMapping
    public Post createPost(@RequestBody PostCreateRequest newPostRequest) {
        return postService.createPost(newPostRequest);
    }

    @PutMapping("/{postId}")
    public Post updatePost(@PathVariable Long postId, @RequestBody PostUpdateRequest postUpdateRequest) {
        return postService.updatePost(postId, postUpdateRequest);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}
