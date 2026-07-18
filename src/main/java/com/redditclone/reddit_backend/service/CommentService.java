package com.redditclone.reddit_backend.service;

import com.redditclone.reddit_backend.dtos.Comment.CommentResponse;
import com.redditclone.reddit_backend.dtos.Comment.CreateCommentRequest;
import com.redditclone.reddit_backend.entity.Comment;
import com.redditclone.reddit_backend.entity.Post;
import com.redditclone.reddit_backend.entity.User;
import com.redditclone.reddit_backend.exception.ForbiddenException;
import com.redditclone.reddit_backend.exception.ResourceNotFoundException;
import com.redditclone.reddit_backend.repository.CommentRepository;
import com.redditclone.reddit_backend.repository.PostRepository;
import com.redditclone.reddit_backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest commentRequest) {


        User currentUser = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment parent = null;

        if (commentRequest.getParentId() != null) {
            parent = commentRepository.findById(commentRequest.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .body(commentRequest.getBody())
                .post(post)
                .author(currentUser)
                .parent(parent)
                .build();

        commentRepository.save(comment);

        return mapToCommentResponse(comment);
    }

    public CommentResponse getCommentById(Long commentId) {
        Comment comment= getCommentOrThrow(commentId ,"Comment not found");

        return mapToCommentResponse(comment);
    }



    public List<CommentResponse> getCommentsByPostId(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }


    public List<CommentResponse> getReplies(Long parentId) {
        Comment parent = getCommentOrThrow(parentId,"Parent comment not found");

        List<Comment> replies = commentRepository.findByParent(parent);

        return replies.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }


    public List<CommentResponse> getUserComments(Long userId) {

        User currentUser = SecurityUtils.getCurrentUser();
        List<Comment> allComments = commentRepository.findByAuthor(currentUser);
        return allComments.stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CreateCommentRequest commentRequest) {


        Comment comment = getCommentOrThrow(commentId ,"Comment not found");

        comment.setBody(commentRequest.getBody());

        commentRepository.save(comment);

        return mapToCommentResponse(comment);

    }



    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId ,"Comment not found");

        User currentUser = SecurityUtils.getCurrentUser();

        if (!comment.getAuthor().equals(currentUser)) {
            throw new ForbiddenException("You are not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    private Comment getCommentOrThrow(Long id ,String message) {

        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message));

    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .author(comment.getAuthor().getDisplayName())
                .postId(comment.getPost().getId())
                .parentCommentId(
                        comment.getParent() != null
                                ? comment.getParent().getId()
                                : null
                )
                .voteScore(comment.getVoteScore())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
