package com.project.socialmediaapp.services;

import com.project.socialmediaapp.entities.Comment;
import com.project.socialmediaapp.entities.Like;
import com.project.socialmediaapp.entities.User;
import com.project.socialmediaapp.repos.CommentRepository;
import com.project.socialmediaapp.repos.LikeRepository;
import com.project.socialmediaapp.repos.PostRepository;
import com.project.socialmediaapp.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository, LikeRepository likeRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public User getUserbyId(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    public User updateUser(Long userId, User updatedUser) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setUserName(updatedUser.getUserName());
            foundUser.setPassword(updatedUser.getPassword());
            foundUser.setAvatarId(updatedUser.getAvatarId());

            return userRepository.save(foundUser);
        } else {
            return null;
        }
    }

    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Object> getUserActivity(Long userId) {
        List<Long> postIds = postRepository.findTopByUserId(userId);
        if (postIds.isEmpty()) {
            return null;
        }
        List<Object> comments = commentRepository.findUserCommentsByPostId(postIds);
        List<Object> likes = likeRepository.findUserLikesByPostId(postIds);

        List<Object> result = new ArrayList<>();
        result.addAll(comments);
        result.addAll(likes);

        return result;
    }
}
