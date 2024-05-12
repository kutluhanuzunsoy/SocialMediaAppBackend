package com.project.socialmediaapp.services;

import com.project.socialmediaapp.entities.Comment;
import com.project.socialmediaapp.entities.Post;
import com.project.socialmediaapp.repos.CommentRepository;
import com.project.socialmediaapp.requests.comment_requests.CommentCreateRequest;
import com.project.socialmediaapp.requests.comment_requests.CommentUpdateRequest;
import com.project.socialmediaapp.responses.CommentResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.socialmediaapp.entities.User;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public List<CommentResponse> getAllComments(Optional<Long> userId, Optional<Long> postId) {
        List<Comment> comments;
        if (userId.isPresent() && postId.isPresent()) {
            comments = commentRepository.findByUserIdAndPostId(userId.get(), postId.get());
        } else if (userId.isPresent()) {
            comments = commentRepository.findByUserId(userId.get());
        } else if (postId.isPresent()) {
            comments = commentRepository.findByPostId(postId.get());
        } else {
            comments = commentRepository.findAll();
        }
        return comments.stream().map(CommentResponse::new).collect(Collectors.toList());
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public Comment createComment(CommentCreateRequest newCommentRequest) {
        User user = userService.getUserbyId(newCommentRequest.getUserId());
        Post post = postService.getPost(newCommentRequest.getPostId());
        if (user == null || post == null) {
            return null;
        }

        Comment newComment = new Comment();
        newComment.setId(newCommentRequest.getId());
        newComment.setPost(post);
        newComment.setUser(user);
        newComment.setText(newCommentRequest.getText());
        newComment.setCreateDate(new Date());

        return commentRepository.save(newComment);
    }

    public Comment updateComment(Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            Comment commentToUpdate = comment.get();
            commentToUpdate.setText(commentUpdateRequest.getText());

            return commentRepository.save(commentToUpdate);
        }
        return null;
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
