package com.project.socialmediaapp.services;

import com.project.socialmediaapp.entities.Like;
import com.project.socialmediaapp.repos.LikeRepository;
import com.project.socialmediaapp.responses.LikeResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.socialmediaapp.entities.User;
import com.project.socialmediaapp.entities.Post;

import com.project.socialmediaapp.requests.like_requests.LikeCreateRequest;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;

    public LikeService(LikeRepository likeRepository, UserService userService, PostService postService) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public List<LikeResponse> getAllLikes(Optional<Long> userId, Optional<Long> postId) {
        List<Like> likes;
        if (userId.isPresent() && postId.isPresent()) {
            likes = likeRepository.findByUserIdAndPostId(userId.get(), postId.get());
        } else if (userId.isPresent()) {
            likes = likeRepository.findByUserId(userId.get());
        } else if (postId.isPresent()) {
            likes = likeRepository.findByPostId(postId.get());
        } else {
            likes = likeRepository.findAll();
        }

        return likes.stream().map(LikeResponse::new).collect(Collectors.toList());
    }

    public Like getLike(Long likeId) {
        return likeRepository.findById(likeId).orElse(null);
    }

    public Like createLike(LikeCreateRequest likeCreateRequest) {
        User user = userService.getUserbyId(likeCreateRequest.getUserId());
        Post post = postService.getPost(likeCreateRequest.getPostId());
        if (user == null || post == null) {
            return null;
        }

        Like newLike = new Like();
        newLike.setId(likeCreateRequest.getId());
        newLike.setPost(post);
        newLike.setUser(user);

        return likeRepository.save(newLike);
    }

    public void deleteLike(Long likeId) {
        likeRepository.deleteById(likeId);
    }
}
