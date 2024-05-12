package com.project.socialmediaapp.services;

import com.project.socialmediaapp.entities.Post;
import com.project.socialmediaapp.entities.User;
import com.project.socialmediaapp.repos.PostRepository;
import com.project.socialmediaapp.requests.post_requests.PostCreateRequest;
import com.project.socialmediaapp.requests.post_requests.PostUpdateRequest;
import com.project.socialmediaapp.responses.LikeResponse;
import com.project.socialmediaapp.responses.PostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private LikeService likeService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Autowired
    public void setLikeService(LikeService likeService) {
        this.likeService = likeService;
    }

    public List<PostResponse> getAllPosts(Optional<Long> userId) {
        List<Post> posts;
        if (userId.isPresent()) {
            posts = postRepository.findByUserId(userId.get());
        } else {
            posts = postRepository.findAll();
        }

        return posts.stream().map(post -> {
            List<LikeResponse> postLikes = likeService.getAllLikes(Optional.empty(), Optional.of(post.getId()));
            return new PostResponse(post, postLikes);
        }).collect(Collectors.toList());
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public PostResponse getPostWithLikes(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        List<LikeResponse> postLikes = likeService.getAllLikes(Optional.empty(), Optional.of(postId));

        return new PostResponse(post, postLikes);
    }

    public Post createPost(PostCreateRequest postCreateRequest) {
        User user = userService.getUserbyId(postCreateRequest.getUserId());
        if (user == null) {
            return null;
        }
        Post newPost = new Post();
        newPost.setId(postCreateRequest.getId());
        newPost.setText(postCreateRequest.getText());
        newPost.setTitle(postCreateRequest.getTitle());
        newPost.setUser(user);
        newPost.setCreateDate(new Date());

        return postRepository.save(newPost);
    }

    public Post updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            Post postToUpdate = post.get();
            postToUpdate.setTitle(postUpdateRequest.getTitle());
            postToUpdate.setText(postUpdateRequest.getText());

            return postRepository.save(postToUpdate);
        }
        return null;
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
