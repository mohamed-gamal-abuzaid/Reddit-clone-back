package com.redditclone.reddit_backend.controller;


import com.redditclone.reddit_backend.dtos.Comment.CommentResponse;
import com.redditclone.reddit_backend.dtos.Comment.CreateCommentRequest;
import com.redditclone.reddit_backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "Comment endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment for the authenticated user.")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, createCommentRequest));
    }

    @Operation(
            summary = "Get comments by post ID",
            description = "Retrieves a list of comments for a specific post by its unique identifier."
    )
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }


    @Operation(
            summary = "Get a comment by ID",
            description = "Retrieves a comment by its unique identifier."
    )
    @GetMapping("/comments/{commentId}")
    public  ResponseEntity<CommentResponse> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }


    @Operation(
            summary = "Get replies to a comment",
            description = "Retrieves a list of replies for a specific comment by its unique identifier."
    )
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }



    @Operation(
            summary = "Get comments by user ID",
            description = "Retrieves a list of comments made by a specific user by their unique identifier."
    )
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentResponse>> getUserComments(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getUserComments(userId));
    }



    @Operation(
            summary = "Update a comment",
            description = "Updates an existing comment for the authenticated user."
    )
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        return ResponseEntity.ok(commentService
                .updateComment(commentId, createCommentRequest));
    }


    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment by its unique identifier."

    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
