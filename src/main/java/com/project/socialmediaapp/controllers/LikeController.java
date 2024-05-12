package com.project.socialmediaapp.controllers;

import com.project.socialmediaapp.entities.Like;
import com.project.socialmediaapp.requests.like_requests.LikeCreateRequest;
import com.project.socialmediaapp.responses.LikeResponse;
import org.springframework.web.bind.annotation.*;
import com.project.socialmediaapp.services.LikeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping
    public List<LikeResponse> getAllLikes(@RequestParam Optional<Long> userId, @RequestParam Optional<Long> postId){
        return likeService.getAllLikes(userId, postId);
    }

    @GetMapping("/like")
    public Like createLike(@RequestBody LikeCreateRequest likeCreateRequest){
        return likeService.createLike(likeCreateRequest);
    }

    @GetMapping("/{likeId}")
    public Like getLike(@PathVariable Long likeId) {
        return likeService.getLike(likeId);
    }

    @DeleteMapping("/{likeId}")
    public void deleteLike(@PathVariable Long likeId) {
        likeService.deleteLike(likeId);
    }
}
